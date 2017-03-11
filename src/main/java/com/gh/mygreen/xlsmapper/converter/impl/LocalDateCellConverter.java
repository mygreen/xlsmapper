package com.gh.mygreen.xlsmapper.converter.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * {@link LocalDate}型に対するConverter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LocalDateCellConverter extends AbstractTemporalCellConverter<LocalDate> {

    @Override
    protected LocalDate parseTemporal(final String str, final DateTimeFormatter formatter) throws DateTimeParseException {
        return LocalDate.parse(str, formatter);
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
    
    @Override
    protected String getDefaultJavaPattern() {
        return "uuuu-MM-dd";
    }

    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd";
    }
    
}
