package com.gh.mygreen.xlsmapper.cellconverter.impl;

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
 * {@link LocalDateTime}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimeCellConverterFactory extends AbstractTemporalCellConverterFactory<LocalDateTime>{

    @Override
    public LocalDateTimeCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final LocalDateTimeCellConverter cellConverter = new LocalDateTimeCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected LocalDateTime parseTemporal(final String str, final DateTimeFormatter formatter) throws DateTimeParseException {
        return LocalDateTime.parse(str, formatter);
    }
    
    @Override
    protected String getDefaultJavaPattern() {
        return "uuuu-MM-dd HH:mm:ss";
    }

    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd hh:mm:ss";
    }
    
    public class LocalDateTimeCellConverter extends AbstractTemporalCellConverter<LocalDateTime> {
        
        private LocalDateTimeCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected LocalDateTime convertFromDate(final Date date) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            return dateTime;
        }
        
        @Override
        protected Date convertToDate(final LocalDateTime value, final boolean dateStart1904) {
            ZonedDateTime zoneDateTime = ZonedDateTime.of(value, ZoneId.systemDefault());
            return Date.from(zoneDateTime.toInstant());
        }
        
    }
    
}
