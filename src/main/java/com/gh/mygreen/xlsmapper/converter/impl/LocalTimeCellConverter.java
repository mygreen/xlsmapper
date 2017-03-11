package com.gh.mygreen.xlsmapper.converter.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * {@link LocalTime}型に対するConverter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LocalTimeCellConverter extends AbstractTemporalCellConverter<LocalTime> {

    @Override
    protected LocalTime parseTemporal(final String str, final DateTimeFormatter formatter) throws DateTimeParseException {
        return LocalTime.parse(str, formatter);
    }
    
    @Override
    protected LocalTime convertFromDate(final Date date) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return dateTime.toLocalTime();
    }
    
    @Override
    protected Date convertToDate(final LocalTime value, final boolean dateStart1904) {
        
        final LocalDateTime dateTime;
        if(dateStart1904) {
            dateTime = value.atDate(LocalDate.of(1904, 1, 1));
        } else {
            dateTime = value.atDate(LocalDate.of(1900, 1, 1));
        }
        
        ZonedDateTime zoneDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        return Date.from(zoneDateTime.toInstant());
        
    }
    
    @Override
    protected String getDefaultJavaPattern() {
        return "HH:mm:ss";
    }
    
    @Override
    protected String getDefaultExcelPattern() {
        return "hh:mm:ss";
    }
    
}
