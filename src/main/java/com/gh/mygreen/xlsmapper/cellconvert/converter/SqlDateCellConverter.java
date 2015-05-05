package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.util.Date;


/**
 * {@link java.sql.Date}型を処理するためのConverter.
 * <p>標準の書式として{@code yyyy-MM-dd}で処理する。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class SqlDateCellConverter extends AbstractDateCellConverter<java.sql.Date> {
    
    @Override
    protected java.sql.Date convertDate(Date value) {
        return new java.sql.Date(value.getTime());
    }
    
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd";
    }
}
