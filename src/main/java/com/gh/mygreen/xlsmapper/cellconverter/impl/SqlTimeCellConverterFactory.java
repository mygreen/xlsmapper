package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Time} を処理する {@link CellConverter} を作成するためのファクトリクラス。
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SqlTimeCellConverterFactory extends AbstractDateCellConverterFactory<Time> {

    @Override
    public SqlTimeCellConverter create(final FieldAccessor field, final Configuration config) {

        final SqlTimeCellConverter cellConverter = new SqlTimeCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);

        return cellConverter;
    }

    /**
     * {@inheritDoc}
     * 基準となる日付をExcelの1900年1月0日=1899年12月31日として、パースする。
     */
    @Override
    protected Time parseString(final DateFormat formatter, final String text) throws ParseException {
        Date date = formatter.parse(text);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(1899, 12-1, 31);

        return convertTypeValue(cal.getTime());
    }

    @Override
    protected Time convertTypeValue(Date date) {
        return new Time(date.getTime());
    }

    @Override
    protected String getDefaultJavaPattern() {
        return "HH:mm:ss";
    }

    @Override
    protected String getDefaultExcelPattern() {
        return "HH:mm:ss";
    }

    public class SqlTimeCellConverter extends AbstractDateCellConverter<Time> {

        private final SqlTimeCellConverterFactory convererFactory;

        private SqlTimeCellConverter(final FieldAccessor field, final Configuration config,
                final SqlTimeCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }

        @Override
        protected Time convertTypeValue(final Date value) {
            return convererFactory.convertTypeValue(value);
        }

    }

}
