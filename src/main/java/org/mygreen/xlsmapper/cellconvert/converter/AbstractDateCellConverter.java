package org.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.AnnotationInvalidException;
import org.mygreen.xlsmapper.POIUtils;
import org.mygreen.xlsmapper.Utils;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;
import org.mygreen.xlsmapper.annotation.converter.XlsConverter;
import org.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import org.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import org.mygreen.xlsmapper.cellconvert.ConversionException;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 日時型のConverterの抽象クラス。
 * <p>{@link Date}を継承している<code>javax.sql.Time/Date/Timestamp</code>はこのクラスを継承して作成します。
 *
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractDateCellConverter<T extends Date> extends AbstractCellConverter<T> {
    
    public static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    @Override
    public T toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsDateConverter anno = getLoadingAnnotation(adaptor);
        
        T resultValue = null;
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(Utils.hasNotDefaultValue(converterAnno)) {
                return null;
                
            } else if(Utils.isNotEmpty(anno.pattern())) {
                final String defaultValue = converterAnno.defaultValue();
                try {
                    resultValue = parseDate(defaultValue, createDateFormat(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(cell, adaptor, defaultValue);
                }
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            // セルのタイプが日付型の場合はそのまま取得する
            resultValue = convertDate(cell.getDateCellValue());
            
        } else {
            String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
            cellValue = Utils.trim(cellValue, converterAnno);
            if(Utils.isNotEmpty(cellValue)) {
                try {
                    resultValue = parseDate(cellValue,  createDateFormat(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(cell, adaptor, cellValue);
                }
            }
        }
        
        if(resultValue != null) {
            return resultValue;
            
        } 
        
        return null;
    }
    
    /**
     * アノテーションを元に日付のフォーマッターのインスタンスを作成します。
     * @param anno
     * @return
     * @throws AnnotationInvalidException フォーマットが指定されていない場合
     */
    protected DateFormat createDateFormat(final XlsDateConverter anno) throws AnnotationInvalidException {
        
        if(anno.pattern().isEmpty()) {
            throw new AnnotationInvalidException(
                    String.format("Anotation '@%s' attribute 'pattern' should be not empty.", XlsDateConverter.class.getSigners()),
                    anno);
        }
        
        final Locale locale;
        if(anno.locale().isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = Utils.getLocale(anno.locale());
        }
        
        final DateFormat format = new SimpleDateFormat(anno.pattern(), locale);
        format.setLenient(anno.lenient());
        
        return format;
        
    }
    
    /**
     * その型における日付型を返す。
     * @param value
     * @return
     */
    abstract protected T convertDate(final Date value);
    
    /**
     * 文字列をその型における日付型を返す。
     * <p>アノテーション{@link XlsDateConverter}でフォーマットが与えられている場合は、パースして返す。
     * @param value
     * @param format フォーマットが指定されていない場合はnullが渡される
     * @return
     * @throws ParseException 
     */
    protected T parseDate(String value, DateFormat format) throws ParseException {
        return convertDate(format.parse(value));
    }
    
    private XlsDateConverter getDefaultDateConverterAnnotation() {
        return new XlsDateConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsDateConverter.class;
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
            public boolean lenient() {
                return false;
            }
        };
    }
    
    private XlsDateConverter getLoadingAnnotation(final FieldAdaptor adaptor) {
        XlsDateConverter anno = adaptor.getLoadingAnnotation(XlsDateConverter.class);
        if(anno == null) {
            anno = getDefaultDateConverterAnnotation();
        }
        
        return anno;
    }
    
    private XlsDateConverter getSavingAnnotation(final FieldAdaptor adaptor) {
        XlsDateConverter anno = adaptor.getSavingAnnotation(XlsDateConverter.class);
        if(anno == null) {
            anno = getDefaultDateConverterAnnotation();
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
        final XlsDateConverter anno = getSavingAnnotation(adaptor);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Date value;
        if(mapKey == null) {
            value = (Date) adaptor.getValue(targetObj);
        } else {
            value = (Date) adaptor.getValueOfMap(mapKey, targetObj);
        }
        
        // デフォルト値から値を設定する
        if(value == null && Utils.hasDefaultValue(converterAnno)) {
            final String defaultValue = converterAnno.defaultValue();
            if(Utils.isNotEmpty(anno.pattern())) {
                try {
                    value = parseDate(defaultValue, createDateFormat(anno));
                } catch (ParseException e) {
                    throw new ConversionException(String.format("Cannot convert string to Object [%s].", adaptor.getTargetClass()), adaptor.getTargetClass());
                }
            } else {
                value = (Date) Utils.convertToObject(defaultValue, adaptor.getTargetClass());
            }
            
        }
        
        // セルの書式の設定
        if(Utils.isNotEmpty(anno.pattern())) {
            cell.getCellStyle().setDataFormat(POIUtils.getDataFormatIndex(sheet, anno.pattern()));
        }
        
        if(value != null) {
            cell.setCellValue(value);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
}
