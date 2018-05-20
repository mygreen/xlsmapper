package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link LocalTime}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LocalTimeCellConverterFactory extends AbstractTemporalCellConverterFactory<LocalTime>{

    @Override
    public LocalTimeCellConverter create(final FieldAccessor field, final Configuration config) {

        final LocalTimeCellConverter cellConverter = new LocalTimeCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);

        return cellConverter;

    }

    @Override
    protected LocalTime parseTemporal(final String str, final DateTimeFormatter formatter) throws DateTimeParseException {
        return LocalTime.parse(str, formatter);
    }

    @Override
    protected String getDefaultJavaPattern() {
        return "HH:mm:ss";
    }

    @Override
    protected String getDefaultExcelPattern() {
        return "hh:mm:ss";
    }

    public class LocalTimeCellConverter extends AbstractTemporalCellConverter<LocalTime> {

        private LocalTimeCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
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
                dateTime = value.atDate(LocalDate.of(1899, 12, 31));
            }

            ZonedDateTime zoneDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());

            return Date.from(zoneDateTime.toInstant());
        }

    }

}
