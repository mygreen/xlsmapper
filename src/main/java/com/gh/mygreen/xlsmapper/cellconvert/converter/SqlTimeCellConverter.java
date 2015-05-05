package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.sql.Time;
import java.util.Date;


/**
 * {@link java.sql.Time}型を処理するためのConverter.
 * <p>標準の書式として{@code HH:mm:ss}で処理する。
 *
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class SqlTimeCellConverter extends AbstractDateCellConverter<Time> {

    @Override
    protected Time convertDate(Date value) {
        
        return new Time(value.getTime());
    }
    
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
}
