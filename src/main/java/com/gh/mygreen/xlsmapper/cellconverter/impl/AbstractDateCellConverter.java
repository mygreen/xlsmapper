package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
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
    protected T parseDefaultValue(final String defaultValue, final FieldAccessor accessor, final XlsMapperConfig config) 
            throws TypeBindException {
        
        final Optional<XlsDateConverter> convertAnno = accessor.getAnnotation(XlsDateConverter.class);
        final DateFormat formatter = createFormatter(convertAnno);
        
        try {
            Date value = formatter.parse(defaultValue);
            return convertTypeValue(value);
            
        } catch(ParseException e) {
            throw newTypeBindExceptionWithDefaultValue(e, accessor, defaultValue)
                .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
        }
        
    }
    
    @Override
    protected T parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor, final XlsMapperConfig config) 
            throws TypeBindException {
        
        if(evaluatedCell.getCellTypeEnum() == CellType.NUMERIC) {
            return convertTypeValue(evaluatedCell.getDateCellValue());
            
        } else if(!formattedValue.isEmpty()) {
            
            final Optional<XlsDateConverter> convertAnno = accessor.getAnnotation(XlsDateConverter.class);
            final DateFormat formatter = createFormatter(convertAnno);
            try {
                Date value = formatter.parse(formattedValue);
                return convertTypeValue(value);
                
            } catch (ParseException e) {
                
                throw newTypeBindExceptionWithParse(e, evaluatedCell, accessor, formattedValue)
                    .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
                
            }
            
        }
        
        return null;
    }
    
    @Override
    protected void setupCell(final Cell cell, final Optional<T> cellValue, final FieldAccessor accessor, final XlsMapperConfig config) 
            throws TypeBindException {
        
        Optional<XlsDateConverter> converterAnno = accessor.getAnnotation(XlsDateConverter.class);
        
        String excelPattern = converterAnno.map(a -> a.excelPattern()).orElseGet(() -> getDefaultExcelPattern());
        if(excelPattern.isEmpty()) {
            excelPattern = getDefaultExcelPattern();
        }
        
        // 現在設定されている書式が異なる場合、変更する。
        if(!POIUtils.getCellFormatPattern(cell).equalsIgnoreCase(excelPattern)) {
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            style.cloneStyleFrom(cell.getCellStyle());
            style.setDataFormat(POIUtils.getDataFormatIndex(cell.getSheet(), excelPattern));
            cell.setCellStyle(style);
        }
        
        if(cellValue.isPresent()) {
            cell.setCellValue(cellValue.get());
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }
    
    /**
     * アノテーションを元にフォーマッタを作成する。
     * @param converterAnno 変換用のアノテーション。
     * @return フォーマッタ
     */
    protected DateFormat createFormatter(final Optional<XlsDateConverter> converterAnno) {
        
        final boolean lenient = converterAnno.map(a -> a.lenient()).orElse(false);
        if(!converterAnno.isPresent()) {
            SimpleDateFormat formatter = new SimpleDateFormat(getDefaultJavaPattern());
            formatter.setLenient(lenient);
            return formatter;
        }
        
        String pattern = converterAnno.get().javaPattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultJavaPattern();
        }
        
        final Locale locale = Utils.getLocale(converterAnno.get().locale());
        final TimeZone timeZone = converterAnno.get().timezone().isEmpty() ? TimeZone.getDefault()
                : TimeZone.getTimeZone(converterAnno.get().timezone());
        
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        formatter.setLenient(lenient);
        formatter.setTimeZone(timeZone);
        
        return formatter;
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    private Map<String, Object> createTypeErrorMessageVars(final Optional<XlsDateConverter> converterAnno) {
        
        String pattern = converterAnno.map(a -> a.javaPattern()).orElse("");
        if(pattern.isEmpty()) {
            pattern = getDefaultJavaPattern();
        }
        
        final boolean lenient = converterAnno.map(a -> a.lenient()).orElse(false);
        final String locale = converterAnno.map(a -> a.locale()).orElse("");
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("javaPattern", pattern);
        vars.put("lenient", lenient);
        vars.put("locale", locale);
        return vars;
    }
    
    /**
     * その型における型に変換する
     * @param value 変換対象の値
     * @return 変換後の値
     */
    protected abstract T convertTypeValue(final Date value);
    
    /**
     * その型における標準のJavaの書式を返す。
     * @since 0.5
     * @return {@link SimpleDateFormat}で処理可能な形式。
     */
    protected abstract String getDefaultJavaPattern();
    
    /**
     * その型における標準のExcelの書式を返す。
     * @since 1.1
     * @return Excelの書式
     */
    protected abstract String getDefaultExcelPattern();
    
    
}
