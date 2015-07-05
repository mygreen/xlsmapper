package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 日時型のConverterの抽象クラス。
 * <p>{@link Date}を継承している<code>javax.sql.Time/Date/Timestamp</code>はこのクラスを継承して作成します。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractDateCellConverter<T extends Date> extends AbstractCellConverter<T> {
    
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
                    throw newTypeBindException(e, cell, adaptor, defaultValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
                }
            }
            
        } else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            // セルのタイプが数値型の場合は、強制的に取得する
            resultValue = convertDate(cell.getDateCellValue());
            
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
                    resultValue = parseDate(cellValue,  createDateFormat(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(e, cell, adaptor, cellValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
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
        
        final Locale locale;
        if(anno.locale().isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = Utils.getLocale(anno.locale());
        }
        
        final String pattern = anno.pattern().isEmpty() ? getDefaultPattern() : anno.pattern();
        final DateFormat format = new SimpleDateFormat(pattern, locale);
        format.setLenient(anno.lenient());
        
        return format;
        
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    Map<String, Object> createTypeErrorMessageVars(final XlsDateConverter anno) {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("pattern", anno.pattern());
        vars.put("lenient", anno.lenient());
        vars.put("locale", anno.locale());
        return vars;
    }
    
    /**
     * その型における日付型を返す。
     * @param value
     * @return
     */
    abstract protected T convertDate(final Date value);
    
    /**
     * その型における標準の書式を返す。
     * @since 0.5
     * @return {@link SimpleDateFormat}で処理可能な形式。
     */
    abstract protected String getDefaultPattern();
    
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
                // 各タイプごとの標準の書式を取得する。
                return getDefaultPattern();
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
    
    XlsDateConverter getLoadingAnnotation(final FieldAdaptor adaptor) {
        XlsDateConverter anno = adaptor.getLoadingAnnotation(XlsDateConverter.class);
        if(anno == null) {
            anno = getDefaultDateConverterAnnotation();
        }
        
        return anno;
    }
    
    XlsDateConverter getSavingAnnotation(final FieldAdaptor adaptor) {
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
                    throw newTypeBindException(e, cell, adaptor, defaultValue)
                        .addAllMessageVars(createTypeErrorMessageVars(anno));
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
