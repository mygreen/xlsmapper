package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.sql.Timestamp;
import java.util.Date;


/**
 * {@link java.sql.Timestamp}型を処理するためのConverter.
 * <p>標準の書式として{@code HH:mm:ss.SSS}で処理する。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class SqlTimestampCellConverter extends AbstractDateCellConverter<Timestamp> {
    
    @Override
    protected Timestamp convertDate(Date value) {
        
        return new Timestamp(value.getTime());
    }
    
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }
    
}
