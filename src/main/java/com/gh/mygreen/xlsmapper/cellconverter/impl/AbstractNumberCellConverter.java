package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 数値型のConverterの抽象クラス。
 * <p>数値型のConverterは、基本的にこのクラスを継承して作成する。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellConverter<T extends Number> extends AbstractCellConverter<T> {
    
    @Override
    protected T parseDefaultValue(final String defaultValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        final Optional<XlsNumberConverter> convertAnno = accessor.getAnnotation(XlsNumberConverter.class);
        
        final Optional<NumberFormat> formatter = createFormatter(convertAnno);
        final MathContext mathContext = createMathContext(convertAnno);
        
        try {
            return parseNumber(defaultValue, formatter, mathContext);
            
        } catch(ParseException | NumberFormatException | ArithmeticException e) {
            throw newTypeBindExceptionWithDefaultValue(e, accessor, defaultValue)
                .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
        }
        
    }
    
    /**
     * 文字列を数値に変換する。
     * @param strValue
     * @param formatter
     * @param mathContext
     * @return
     * @throws ParseException 書式が不正な場合
     * @throws NumberFormatException 書式が不正な場合
     * @throws ArithmeticException オーバフローした場合など
     */
    private T parseNumber(final String strValue, final Optional<NumberFormat> formatter, final MathContext mathContext) 
            throws ParseException, NumberFormatException, ArithmeticException {
        
        if(formatter.isPresent()) {
            // 書式が指定されている場合
            final ParsePosition position = new ParsePosition(0);
            BigDecimal value = (BigDecimal)formatter.get().parse(strValue, position);
            
            if(position.getIndex() != strValue.length()) {
                throw new ParseException(strValue, position.getErrorIndex());
            }
            
            value = value.setScale(mathContext.getPrecision(), mathContext.getRoundingMode());
            return convertTypeValue(value);
            
        } else {
            /*
             * 有効桁数15桁を超えている場合、Excelの場合は、切り捨てによる丸めが入るが、
             * Javaではそのまま、HALF_UPとなり、オーバーフローが起こる場合がある。
             */
            BigDecimal value = new BigDecimal(strValue, mathContext);
            return convertTypeValue(value);
        }   
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected T parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        final Optional<XlsNumberConverter> convertAnno = accessor.getAnnotation(XlsNumberConverter.class);
        final MathContext mathContext = createMathContext(convertAnno);
        
        if(evaluatedCell.getCellTypeEnum() == CellType.NUMERIC) {
            try {
                return convertTypeValue(new BigDecimal(evaluatedCell.getNumericCellValue(), mathContext));
                
            } catch(ArithmeticException e) {
                
                throw newTypeBindExceptionWithParse(e, evaluatedCell, accessor, evaluatedCell.getNumericCellValue())
                    .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
            }
            
        } else if(!formattedValue.isEmpty()) {
            final Optional<NumberFormat> formatter = createFormatter(convertAnno);
            
            try {
                return parseNumber(formattedValue, formatter, mathContext);
                
            } catch(ParseException | NumberFormatException | ArithmeticException e) {
                throw newTypeBindExceptionWithParse(e, evaluatedCell, accessor, formattedValue)
                    .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
            }
        }
        
        // プリミティブ型の場合、値がnullの時は初期値を設定する
        if(accessor.getType().isPrimitive()) {
            return (T)Utils.getPrimitiveDefaultValue(accessor.getType());
            
        } else if(accessor.isComponentType() && accessor.getComponentType().isPrimitive()) {
            return (T)Utils.getPrimitiveDefaultValue(accessor.getComponentType());
        }
        
        return null;
        
    }
    
    /**
     * アノテーションから数値のフォーマッタを取得する。
     * @param convertAnno 引数がnull(アノテーションが設定されていない場合)は、nullを返す。
     * @return アノテーションに書式が設定されていない場合は空を返す。
     */
    private Optional<NumberFormat> createFormatter(final Optional<XlsNumberConverter> convertAnno) {
        
        if(!convertAnno.isPresent()) {
            return Optional.empty();
        }
        
        final String javaPattern = convertAnno.get().javaPattern();
        final Locale locale = Utils.getLocale(convertAnno.get().locale());
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        final Optional<Currency> currency = convertAnno.get().currency().isEmpty() ? Optional.empty()
                : Optional.of(Currency.getInstance(convertAnno.get().currency()));
        
        if(javaPattern.isEmpty()) {
            if(!convertAnno.get().currency().isEmpty()) {
                // 通貨の場合
                DecimalFormat formatter = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
                formatter.setParseBigDecimal(true);
                formatter.setDecimalFormatSymbols(symbols);
                currency.ifPresent(c -> formatter.setCurrency(c));
                
                return Optional.of(formatter);
                
            } else {
                return Optional.empty();
            }
        }
        
        final DecimalFormat formatter = new DecimalFormat(javaPattern, symbols);
        
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setParseBigDecimal(true);
        currency.ifPresent(c -> formatter.setCurrency(c));
        
        return Optional.of(formatter);
        
    }
    
    
    /**
     * アノテーションを元に、{@link MathContext}のインスタンスを取得する。
     * <p>有効桁数、丸め方法を設定したものを返す。
     * <p>有効桁数は、デフォルトでは無期限にする。
     * <p>丸め方法は、Excelに合わせて、{@link RoundingMode#HALF_UP}で固定。
     * @param convertAnno
     * @return
     */
    private MathContext createMathContext(final Optional<XlsNumberConverter> convertAnno) {
        
        if(convertAnno.isPresent() && convertAnno.get().precision() > 0) {
            return new MathContext(convertAnno.get().precision(), RoundingMode.HALF_UP);
            
        } else {
            //アノテーションがない場合は、制限なし。
            return MathContext.UNLIMITED;
        }
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     * @param convertAnno 変換用のアノテーション
     * @return メッセージの変数
     */
    private Map<String, Object> createTypeErrorMessageVars(final Optional<XlsNumberConverter> convertAnno) {
        
        final Map<String, Object> vars = new HashMap<>();
        
        convertAnno.ifPresent(anno -> {
            vars.put("javaPattern", anno.javaPattern());
            vars.put("currency", anno.currency());
            vars.put("locale", anno.locale());
            vars.put("precision", anno.precision());
            
        });
        return vars;
    }
    
    /**
     * その型における型に変換する
     * BigDecimalから変換する際には、exactXXX()メソッドを呼ぶ。
     * 
     * @param bg 変換対象のBigDecimal
     * @return 変換した値
     * @throws ArithmeticException 変換する数値型に合わない場合
     */
    protected abstract T convertTypeValue(final BigDecimal value) throws ArithmeticException;
    
    @Override
    protected void setupCell(final Cell cell, final Optional<T> cellValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        Optional<XlsNumberConverter> converterAnno = accessor.getAnnotation(XlsNumberConverter.class);
        
        final String excelPattern = converterAnno.map(a -> a.excelPattern()).orElse("");
        
        // 現在設定されている書式が異なる場合、変更する。
        if(!excelPattern.isEmpty() && !POIUtils.getCellFormatPattern(cell).equalsIgnoreCase(excelPattern)) {
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setDataFormat(POIUtils.getDataFormatIndex(cell.getSheet(), excelPattern));
            cell.setCellStyle(style);
        }
        
        if(cellValue.isPresent()) {
            cell.setCellValue(cellValue.get().doubleValue());
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }
    
}
