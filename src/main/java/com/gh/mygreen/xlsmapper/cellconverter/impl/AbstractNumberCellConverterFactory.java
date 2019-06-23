package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * 数値型のCellConverterを作成するためのベースクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellConverterFactory<T extends Number> extends CellConverterFactorySupport<T> 
            implements CellConverterFactory<T> {
    
    @Override
    protected void setupCustom(final BaseCellConverter<T> cellConverter, final FieldAccessor field, final Configuration config) {
        
        ArgUtils.instanceOf(cellConverter, AbstractNumberCellConverter.class, "cellConverter");
        
        if(cellConverter instanceof AbstractNumberCellConverter) {
            
            final AbstractNumberCellConverter<T> numberCellConverter = (AbstractNumberCellConverter<T>)cellConverter;
            
            // 書き込み時のセルの書式を設定する
            Optional<XlsNumberConverter> convertAnno = field.getAnnotation(XlsNumberConverter.class);
            Optional<String> excelPattern = getExcelPattern(convertAnno);
            excelPattern.ifPresent(pattern -> numberCellConverter.setExcelPattern(pattern));
            
            numberCellConverter.setMathContext(createMathContext(convertAnno));
            
        }
        
    }
    
    @Override
    protected TextFormatter<T> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<XlsNumberConverter> convertAnno = field.getAnnotation(XlsNumberConverter.class);
        final Optional<NumberFormat> numberFormat = createFormatter(convertAnno);
        final MathContext mathContext = createMathContext(convertAnno);
        
        if(numberFormat.isPresent()) {
            final NumberFormat fromatter = numberFormat.get();
            
            // 書式が指定されている場合
            return new TextFormatter<T>() {
                
                @Override
                public T parse(final String text) {
                    ParsePosition position = new ParsePosition(0);
                    BigDecimal number = (BigDecimal) fromatter.parse(text, position);
                    
                    if(position.getIndex() != text.length()) {
                        throw new TextParseException(text, field.getType());
                    }
                    
                    try {
                        number = number.setScale(mathContext.getPrecision(), mathContext.getRoundingMode());
                        return convertTypeValue(number);
                    } catch(NumberFormatException | ArithmeticException e) {
                        final Map<String, Object> vars = new HashMap<>();
                        vars.put("javaPattern", getJavaPattern(convertAnno).orElse(null));
                        vars.put("excelPattern", getExcelPattern(convertAnno).orElse(null));
                        
                        throw new TextParseException(text, field.getType(), e, vars);
                    }
                    
                }
                
                @Override
                public String format(final T value) {
                    return fromatter.format(value);
                }
                
            };
            
        } else {
            return new TextFormatter<T>() {
                
                @Override
                public T parse(final String text) {
                    /*
                     * 有効桁数15桁を超えている場合、Excelの場合は、切り捨てによる丸めが入るが、
                     * Javaではそのまま、HALF_UPとなり、オーバーフローが起こる場合がある。
                     */
                    
                    try {
                        BigDecimal value = new BigDecimal(text, mathContext);
                        return convertTypeValue(value);
                    } catch(NumberFormatException | ArithmeticException e) {
                        throw new TextParseException(text, field.getType(), e);
                    }
                }
                
                @Override
                public String format(final T value) {
                    return value.toString();
                }
                
            };
        }
        
    }
    
    /**
     * その型における型に変換する
     * @param value 変換対象の値
     * @return 変換後の値
     * @throws NumberFormatException
     * @throws ArithmeticException
     */
    protected abstract T convertTypeValue(BigDecimal value) throws NumberFormatException, ArithmeticException;
    
    /**
     * アノテーションから数値のフォーマッタを取得する。
     * @param convertAnno 引数がnull(アノテーションが設定されていない場合)は、nullを返す。
     * @return アノテーションに書式が設定されていない場合は空を返す。
     */
    private Optional<NumberFormat> createFormatter(final Optional<XlsNumberConverter> convertAnno) {
        
        if(!convertAnno.isPresent()) {
            return Optional.empty();
        }
        
        final Optional<String> javaPattern = getJavaPattern(convertAnno);
        final Locale locale = Utils.getLocale(convertAnno.get().locale());
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        final Optional<Currency> currency = convertAnno.get().currency().isEmpty() ? Optional.empty()
                : Optional.of(Currency.getInstance(convertAnno.get().currency()));
        
        if(!javaPattern.isPresent()) {
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
        
        final DecimalFormat formatter = new DecimalFormat(javaPattern.get(), symbols);
        
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setParseBigDecimal(true);
        currency.ifPresent(c -> formatter.setCurrency(c));
        
        return Optional.of(formatter);
        
    }
    
    private Optional<String> getJavaPattern(final Optional<XlsNumberConverter> converterAnno) {
        if(!converterAnno.isPresent()) {
            return Optional.empty();
        }
        
        String pattern = converterAnno.get().javaPattern();
        if(pattern.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(pattern);
    }
    
    private Optional<String> getExcelPattern(final Optional<XlsNumberConverter> converterAnno) {
        if(!converterAnno.isPresent()) {
            return Optional.empty();
        }
        
        String pattern = converterAnno.get().excelPattern();
        if(pattern.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(pattern);
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
    
}
