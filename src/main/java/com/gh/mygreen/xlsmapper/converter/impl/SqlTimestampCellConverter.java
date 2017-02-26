package com.gh.mygreen.xlsmapper.converter.impl;

import java.sql.Timestamp;
import java.util.Date;


/**
 * {@link java.sql.Timestamp}型を処理するためのConverter.
 * <p>標準の書式として{@code yyyy-MM-dd HH:mm:ss.SSS}で処理する。
 * 
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class SqlTimestampCellConverter extends AbstractDateCellConverter<Timestamp> {
    
    @Override
    protected Timestamp convertDate(Date value) {
        
        return new Timestamp(value.getTime());
    }
    
    @Override
    protected String getDefaultJavaPattern() {
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }
    
    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd hh:mm:ss.SSS";
    }
    
}
