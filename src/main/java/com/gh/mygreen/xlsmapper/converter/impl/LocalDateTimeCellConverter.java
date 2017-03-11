package com.gh.mygreen.xlsmapper.converter.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * {@link LocalDateTime}型に対するConverter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimeCellConverter extends AbstractTemporalCellConverter<LocalDateTime> {

    @Override
    protected LocalDateTime parseTemporal(final String str, final DateTimeFormatter formatter) throws DateTimeParseException {
        return LocalDateTime.parse(str, formatter);
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
    
    @Override
    protected String getDefaultJavaPattern() {
        return "uuuu-MM-dd HH:mm:ss";
    }

    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd hh:mm:ss";
    }
    
}
