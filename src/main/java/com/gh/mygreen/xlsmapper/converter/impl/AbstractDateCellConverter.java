package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 日時型のConverterの抽象クラス。
 * <p>{@link Date}を継承している<code>javax.sql.Time/Date/Timestamp</code>はこのクラスを継承して作成します。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractDateCellConverter<T extends Date> extends AbstractCellConverter<T> {
    
    @Override
    public T toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsDateConverter anno = adapter.getAnnotation(XlsDateConverter.class)
                .orElseGet(() -> getDefaultDateConverterAnnotation());
        
        T resultValue = null;
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(!defaultValueAnno.isPresent()) {
                return null;
                
            } else if(Utils.isNotEmpty(anno.javaPattern())) {
                final String defaultValue = defaultValueAnno.get().value();
                try {
                    resultValue = parseDate(defaultValue, createDateFormat(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(e, cell, adapter, defaultValue)
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
                return toObject(evalCell, adapter, config);
                
            } catch(Exception e) {
                throw newTypeBindException(e, cell, adapter, cell)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        } else {
            String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
            cellValue = Utils.trim(cellValue, trimAnno);
            if(Utils.isNotEmpty(cellValue)) {
                try {
                    resultValue = parseDate(cellValue,  createDateFormat(anno));
                } catch(ParseException e) {
                    throw newTypeBindException(e, cell, adapter, cellValue)
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
        
        final String pattern = anno.javaPattern().isEmpty() ? getDefaultJavaPattern() : anno.javaPattern();
        final DateFormat format = new SimpleDateFormat(pattern, locale);
        format.setLenient(anno.lenient());
        
        return format;
        
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    Map<String, Object> createTypeErrorMessageVars(final XlsDateConverter anno) {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("javaPattern", anno.javaPattern());
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
     * その型における標準のJavaの書式を返す。
     * @since 0.5
     * @return {@link SimpleDateFormat}で処理可能な形式。
     */
    abstract protected String getDefaultJavaPattern();
    
    /**
     * その型における標準のExcelの書式を返す。
     * @since 1.1
     * @return Excelの書式
     */
    abstract protected String getDefaultExcelPattern();
    
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
    
    XlsDateConverter getDefaultDateConverterAnnotation() {
        return new XlsDateConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsDateConverter.class;
            }
            
            @Override
            public String javaPattern() {
                // 各タイプごとの標準の書式を取得する。
                return getDefaultJavaPattern();
            }
            
            @Override
            public String locale() {
                return "";
            }
            
            @Override
            public boolean lenient() {
                return false;
            }
            
            @Override
            public String excelPattern() {
                return "";
            }
        };
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final Date targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row, 
            final XlsMapperConfig config) throws XlsMapperException {
         
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsDateConverter anno = adapter.getAnnotation(XlsDateConverter.class)
                .orElseGet(() -> getDefaultDateConverterAnnotation());
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        Date value = targetValue;
        
        // デフォルト値から値を設定する
        if(value == null && defaultValueAnno.isPresent()) {
            final String defaultValue = defaultValueAnno.get().value();
            
            final DateFormat formatter;
            if(Utils.isNotEmpty(anno.javaPattern())) {
                formatter = createDateFormat(anno);
            } else {
                formatter = createDateFormat(getDefaultDateConverterAnnotation());
            }
            
            try {
                value = parseDate(defaultValue, formatter);
            } catch (ParseException e) {
                throw newTypeBindException(e, cell, adapter, defaultValue)
                    .addAllMessageVars(createTypeErrorMessageVars(anno));
            }
            
        }
        
        // セルの書式の設定
        if(Utils.isNotEmpty(anno.excelPattern()) && !POIUtils.getCellFormatPattern(cell).equalsIgnoreCase(anno.excelPattern())) {
            
            // 既にCell中に書式が設定され、それが異なる場合
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setDataFormat(POIUtils.getDataFormatIndex(sheet, anno.excelPattern()));
            cell.setCellStyle(style);
            
        } else if(Utils.isEmpty(anno.excelPattern()) && POIUtils.getCellFormatPattern(cell).isEmpty()) {
            
            // アノテーションの書式が指定されておらず、セルの書式が空の場合
            // 標準の書式を設定する。
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setDataFormat(POIUtils.getDataFormatIndex(sheet, getDefaultExcelPattern()));
            cell.setCellStyle(style);
            
        }
        
        if(value != null && !primaryFormula) {
            cell.setCellValue(value);
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
}
