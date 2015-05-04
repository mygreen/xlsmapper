package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
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
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.ConversionException;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 数値型のConverterの抽象クラス。
 * <p>数値型のConverterは、基本的にこのクラスを継承して作成する。
 * 
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
                
            } else if(Utils.isNotEmpty(anno.pattern())) {
                final String defaultValue = converterAnno.defaultValue();
                try {
                    resultValue = parseNumber(defaultValue, createNumberFormat(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(e, cell, adaptor, defaultValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // セルのタイプが数値型の場合はそのまま取得する。
            resultValue = convertNumber(cell.getNumericCellValue());
            
        } else {
            String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
            cellValue = Utils.trim(cellValue, converterAnno);
            if(Utils.isNotEmpty(cellValue)) {
                try {
                    resultValue = parseNumber(cellValue, createNumberFormat(anno));
                } catch(ParseException e) {
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
        
        if(anno.pattern().isEmpty()) {
            return null;
        }
        
        final Locale locale;
        if(anno.locale().isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = Utils.getLocale(anno.locale());
        }
        
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        
        final DecimalFormat format = new DecimalFormat(anno.pattern(), symbols);
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
        return vars;
    }
    
    /**
     * その型における数値型を返す。
     * @param value
     * @return
     */
    protected abstract T convertNumber(double value);
    
    /**
     * その型における数値型を返す。
     * @param value
     * @return
     */
    protected abstract T convertNumber(Number value);
    
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
     * @return
     * @throws ParseException 
     */
    protected T parseNumber(final String value, final NumberFormat format) throws ParseException {
        
        if(format == null) {
            final BigDecimal bg;
            try {
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
        
        return convertNumber(result);
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
    public Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row, 
            final XlsMapperConfig config) throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, null);
        
    }
    
    @Override
    public Cell toCellWithMap(final FieldAdaptor adaptor, final String key, final Object targetObj, final Sheet sheet, final int column, final int row, 
            final XlsMapperConfig config) throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, key);
        
    }
    
    private Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row, 
            final XlsMapperConfig config, final String mapKey) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsNumberConverter anno = getSavingAnnotation(adaptor);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Number value;
        if(mapKey == null) {
            value = (Number) adaptor.getValue(targetObj);
        } else {
            value = (Number) adaptor.getValueOfMap(mapKey, targetObj);
        }
        
        // デフォルト値から値を設定する
        if(value == null && Utils.hasDefaultValue(converterAnno)) {
            final String defaultValue = converterAnno.defaultValue();
            if(Utils.isNotEmpty(anno.pattern())) {
                try {
                    value = parseNumber(defaultValue, createNumberFormat(anno));
                } catch (ParseException e) {
                    throw new ConversionException(String.format("Cannot convert string to Object [%s].", adaptor.getTargetClass()), adaptor.getTargetClass());
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
