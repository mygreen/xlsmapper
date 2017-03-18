package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
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
 * JSR-310 'Date and Time API' の{@link TemporalAccessor}のテンプレートクラス。
 * <p>基本的に、{@link TemporalAccessor}のサブクラスのビルダは、このクラスを継承して作成する。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalCellConverter<T extends TemporalAccessor & Comparable<? super T>> extends AbstractCellConverter<T> {
    
    @Override
    protected T parseDefaultValue(final String defaultValue, final FieldAccessor accessor, final XlsMapperConfig config) 
            throws TypeBindException {
        
        final Optional<XlsDateConverter> convertAnno = accessor.getAnnotation(XlsDateConverter.class);
        final DateTimeFormatter formatter = createFormatter(convertAnno);
        
        try {
            
            return parseTemporal(defaultValue, formatter);
            
        } catch(DateTimeParseException e) {
            throw newTypeBindExceptionWithDefaultValue(e, accessor, defaultValue)
                .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
        }
        
    }
    
    @Override
    protected T parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor, final XlsMapperConfig config) 
            throws TypeBindException {
        
        if(evaluatedCell.getCellTypeEnum() == CellType.NUMERIC) {
            return convertFromDate(evaluatedCell.getDateCellValue());
            
        } else if(!formattedValue.isEmpty()) {
            
            final Optional<XlsDateConverter> convertAnno = accessor.getAnnotation(XlsDateConverter.class);
            final DateTimeFormatter formatter = createFormatter(convertAnno);
            try {
                return parseTemporal(formattedValue, formatter);
                
            } catch (DateTimeParseException e) {
                
                throw newTypeBindExceptionWithParse(e, evaluatedCell, accessor, formattedValue)
                    .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
                
            }
            
        }
        
        return null;
    }
    
    /**
     * 文字列を解析してJavaオブジェクトに変換します。
     * @param str 解析対象の文字列
     * @param formatter フォーマッタ
     * @return パースした結果
     * @throws DateTimeParseException パースに失敗した場合
     */
    protected abstract T parseTemporal(String str, DateTimeFormatter formatter) throws DateTimeParseException;
    
    /**
     * 日時型から各タイプに変換する。
     * @param date 日時型
     * @return 変換した値
     */
    protected abstract T convertFromDate(Date date);
    
    /**
     * 日時型に変換する。
     * @param value 変換対象の値
     * @param dateStart1904 1904年始まりのシートかどうか
     * @return 変換した値
     */
    protected abstract Date convertToDate(T value, boolean dateStart1904);
    
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
            boolean isStartDate1904 = POIUtils.isDateStart1904(cell.getSheet().getWorkbook());
            Date date = convertToDate(cellValue.get(), isStartDate1904);
            cell.setCellValue(date);
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
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
     * その型における標準のJavaの書式を返す。
     * @return {@link DateTimeFormatter}で処理可能な形式。
     */
    protected abstract String getDefaultJavaPattern();
    
    /**
     * その型における標準のExcelの書式を返す。
     * @return Excelの書式
     */
    protected abstract String getDefaultExcelPattern();
    
    /**
     * アノテーションを元にフォーマッタを作成する。
     * @param converterAnno 変換用のアノテーション。
     * @return フォーマッタ
     */
    protected DateTimeFormatter createFormatter(final Optional<XlsDateConverter> converterAnno) {
        
        final boolean lenient = converterAnno.map(a -> a.lenient()).orElse(false);
        final ResolverStyle style = lenient ? ResolverStyle.LENIENT : ResolverStyle.STRICT;
        if(!converterAnno.isPresent()) {
            return DateTimeFormatter.ofPattern(getDefaultJavaPattern())
                    .withResolverStyle(style);
        }
        
        String pattern = converterAnno.get().javaPattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultJavaPattern();
        }
        
        final Locale locale = Utils.getLocale(converterAnno.get().locale());
        final ZoneId zone = converterAnno.get().timezone().isEmpty() ? ZoneId.systemDefault()
                : TimeZone.getTimeZone(converterAnno.get().timezone()).toZoneId();
        
        return DateTimeFormatter.ofPattern(pattern, locale)
                .withResolverStyle(style)
                .withZone(zone);
        
    }
    
    
}
