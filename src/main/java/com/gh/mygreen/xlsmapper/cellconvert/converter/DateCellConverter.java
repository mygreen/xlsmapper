package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.util.Date;


/**
 * {@link Date}型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class DateCellConverter extends AbstractDateCellConverter<Date> {

    @Override
    protected Date convertDate(Date value) {
        return value;
    }
}
