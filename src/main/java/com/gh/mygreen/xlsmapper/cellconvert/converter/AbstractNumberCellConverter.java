package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 数値型のConverterの抽象クラス。
 * <p>数値型のConverterは、基本的にこのクラスを継承して作成する。
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellConverter<T extends Number> extends AbstractCellConverter<T> {
    
    @Override
    public T toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config) throws TypeBindException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsNumberConverter anno = getLoadingAnnotation(adaptor);
        
        T resultValue = null;
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(Utils.hasNotDefaultValue(converterAnno)) {
                // デフォルト値を持たない場合
                if(adaptor.getTargetClass().isPrimitive()) {
                    resultValue = getZeroValue();
                }
                
            } else {
                String defaultValue = converterAnno.defaultValue();
                try {
                    resultValue = parseNumber(defaultValue, createNumberFormat(anno), createMathContext(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(e, cell, adaptor, defaultValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // セルのタイプが数値型の場合はそのまま取得する。
            try {
                resultValue = convertNumber(cell.getNumericCellValue(), createMathContext(anno));
            } catch(ArithmeticException e) {
                throw newTypeBindException(e, cell, adaptor, cell)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            // 式を評価して再帰的に処理する。
            final Workbook workbook = cell.getSheet().getWorkbook();
            final CreationHelper helper = workbook.getCreationHelper();
            final FormulaEvaluator evaluator = helper.createFormulaEvaluator();
            try {
                // 再帰的に処理する
                final Cell evalCell = evaluator.evaluateInCell(cell);
                return toObject(evalCell, adaptor, config);
                
            } catch(Exception e) {
                throw newTypeBindException(e, cell, adaptor, cell)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        } else {
            String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
            cellValue = Utils.trim(cellValue, converterAnno);
            if(Utils.isNotEmpty(cellValue)) {
                try {
                    resultValue = parseNumber(cellValue, createNumberFormat(anno), createMathContext(anno));
                } catch(ParseException | ArithmeticException e) {
                    throw newTypeBindException(e, cell, adaptor, cellValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            }
        }
        
        if(resultValue != null) {
            return resultValue;
            
        } else if(adaptor.getTargetClass().isPrimitive()) {
            return getZeroValue();
        }
        
        return null;
    }
    
    /**
     * アノテーションから数値のフォーマッタを取得する。
     * @param anno 引数がnull(アノテーションが設定されていない場合)は、nullを返す。
     * @return アノテーションに書式が設定されていない場合はnullを返す。
     */
    protected NumberFormat createNumberFormat(final XlsNumberConverter anno) {
        
        final Locale locale;
        if(anno.locale().isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = Utils.getLocale(anno.locale());
        }
        
        if(anno.pattern().isEmpty()) {
            if(anno.currency().isEmpty()) {
                return null;
            } else {
                // 通貨の場合
                return NumberFormat.getCurrencyInstance(locale);
            }
        }
        
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        final DecimalFormat format = new DecimalFormat(anno.pattern(), symbols);
        
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setParseBigDecimal(true);
        
        if(Utils.isNotEmpty(anno.currency())) {
            format.setCurrency(Currency.getInstance(anno.currency()));
        }
        
        return format;
        
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    private Map<String, Object> createTypeErrorMessageVars(final XlsNumberConverter anno) {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("pattern", anno.pattern());
        vars.put("currency", anno.currency());
        vars.put("locale", anno.locale());
        vars.put("precision", anno.precision());
        return vars;
    }
    
    /**
     * アノテーションを元に、{@link MathContext}のインスタンスを取得する。
     * <p>有効桁数、丸め方法を設定したものを返す。
     * <p>有効桁数は、デフォルトではExcelに仕様に合わせて15桁。
     * <p>丸め方法は、Excelに合わせて、{@link RoundingMode#HALF_UP}で固定。
     * @param XlsNumberConcerter
     * @return
     */
    protected MathContext createMathContext(final XlsNumberConverter anno) {
        
        if(anno.precision() > 0) {
            return new MathContext(anno.precision(), RoundingMode.HALF_UP);
        } else {
            return new MathContext(15, RoundingMode.HALF_UP);
        }
        
    }
    
    /**
     * その型における数値型を返す。
     * @param value
     * @param context
     * @return
     */
    protected abstract T convertNumber(double value, MathContext context);
    
    /**
     * その型における数値型を返す。
     * @param value
     * @param context
     * @return
     */
    protected abstract T convertNumber(Number value, MathContext context);
    
    /**
     * その型における数値型を返す。
     * @param value
     * @return
     */
    protected abstract T convertNumber(BigDecimal value);
    
    /**
     * 文字列をその型における数値型を返す。
     * <p>アノテーション{@link XlsNumberConverter}でフォーマットが与えられている場合は、パースして返す。
     * @param value
     * @param format フォーマットが指定されていない場合はnullが渡される
     * @param context 数値変換する際の設定
     * @return
     * @throws ParseException 
     */
    protected T parseNumber(final String value, final NumberFormat format, final MathContext context) throws ParseException {
        
        if(format == null) {
            final BigDecimal bg;
            try {
                // 文字列の時は、精度指定しない。
                bg = new BigDecimal(value);
            } catch(NumberFormatException e) {
                throw new ParseException(String.format("Cannot parse '%s'", value), 0);
            }
            
            if(bg.doubleValue() < getMinValue()) {
                throw new ParseException(String.format("'%s' cannot less than %f", value, getMinValue()), 0);
            }
            
            if(bg.doubleValue() > getMaxValue()) {
                throw new ParseException(String.format("'%s' cannot greater than %f", value, getMaxValue()), 0);
            }
            
            return convertNumber(bg);
        }
        
        final ParsePosition position = new ParsePosition(0);
        final Number result = (Number) format.parseObject(value, position);
        
        if(position.getIndex() != value.length()) {
            throw new ParseException(
                    String.format("Cannot parse '%s' using fromat %s", value, format.toString()), position.getIndex());
        }
        
        if(result.doubleValue() < getMinValue()) {
            throw new ParseException(String.format("'%s' cannot less than %f", value, getMinValue()), 0);
        }
        
        if(result.doubleValue() > getMaxValue()) {
            throw new ParseException(String.format("'%s' cannot greater than %f", value, getMaxValue()), 0);
        }
        
        if(result instanceof BigDecimal) {
            // NumberFormatのインスタンスを作成する際に、DecimalFormat#setParseBigDecimal(true)としているため、戻り値がBigDecimalになる。
            return convertNumber((BigDecimal) result);
        } else {
            return convertNumber(result, context);
        }
    }
    
    /**
     * その型におけるゼロ値を返す。
     * @return
     */
    protected abstract T getZeroValue();
    
    /**
     * その型における最大値を返す。
     * @return
     */
    protected abstract double getMaxValue();
    
    /**
     * その型における最小値を返す。
     * @return
     */
    protected abstract double getMinValue();
    
    private XlsNumberConverter getDefaultNumberConverterAnnotation() {
        return new XlsNumberConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsNumberConverter.class;
            }
            
            @Override
            public String pattern() {
                return "";
            }
            
            @Override
            public String locale() {
                return "";
            }
            
            @Override
            public String currency() {
                return "";
            }
            
            @Override
            public int precision() {
                return 15;
            }
            
        };
    }
    
    private XlsNumberConverter getLoadingAnnotation(final FieldAdaptor adaptor) {
        XlsNumberConverter anno = adaptor.getLoadingAnnotation(XlsNumberConverter.class);
        if(anno == null) {
            anno = getDefaultNumberConverterAnnotation();
        }
        
        return anno;
    }
    
    private XlsNumberConverter getSavingAnnotation(final FieldAdaptor adaptor) {
        XlsNumberConverter anno = adaptor.getSavingAnnotation(XlsNumberConverter.class);
        if(anno == null) {
            anno = getDefaultNumberConverterAnnotation();
        }
        
        return anno;
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Number targetValue, final Sheet sheet, final int column, final int row, 
            final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsNumberConverter anno = getSavingAnnotation(adaptor);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Number value = targetValue;
        
        // デフォルト値から値を設定する
        if(value == null && Utils.hasDefaultValue(converterAnno)) {
            final String defaultValue = converterAnno.defaultValue();
            if(Utils.isNotEmpty(anno.pattern())) {
                try {
                    value = parseNumber(defaultValue, createNumberFormat(anno), createMathContext(anno));
                } catch (ParseException e) {
                    throw newTypeBindException(e, cell, adaptor, defaultValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            } else {
                value = (Number) Utils.convertToObject(defaultValue, adaptor.getTargetClass());
            }
            
        }
        
        // セルの書式の設定
        if(Utils.isNotEmpty(anno.pattern())) {
            cell.getCellStyle().setDataFormat(POIUtils.getDataFormatIndex(sheet, anno.pattern()));
        }
        
        if(value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
        
    }
    
}
