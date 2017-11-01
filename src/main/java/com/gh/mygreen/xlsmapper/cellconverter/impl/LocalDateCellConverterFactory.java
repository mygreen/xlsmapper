package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link LocalDate}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LocalDateCellConverterFactory extends AbstractTemporalCellConverterFactory<LocalDate>{

    @Override
    public LocalDateCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final LocalDateCellConverter cellConverter = new LocalDateCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected LocalDate parseTemporal(final String str, final DateTimeFormatter formatter) throws DateTimeParseException {
        return LocalDate.parse(str, formatter);
    }
    
    @Override
    protected String getDefaultJavaPattern() {
        return "uuuu-MM-dd";
    }

    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd";
    }
    
    public class LocalDateCellConverter extends AbstractTemporalCellConverter<LocalDate> {
        
        private LocalDateCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected LocalDate convertFromDate(final Date date) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            return dateTime.toLocalDate();
        }
        
        @Override
        protected Date convertToDate(final LocalDate value, final boolean dateStart1904) {
            LocalDateTime dateTime = value.atStartOfDay();
            ZonedDateTime zoneDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
            return Date.from(zoneDateTime.toInstant());
        }
        
    }
    
}
