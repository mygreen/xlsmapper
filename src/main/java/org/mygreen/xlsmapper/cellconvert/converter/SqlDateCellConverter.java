package org.mygreen.xlsmapper.cellconvert.converter;

import java.util.Date;


/**
 * {@link java.sql.Date}型を処理するためのConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class SqlDateCellConverter extends AbstractDateCellConverter<java.sql.Date> {

    @Override
    protected java.sql.Date convertDate(Date value) {
        return new java.sql.Date(value.getTime());
    }
}
