package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.sql.Time;
import java.util.Date;


/**
 * {@link java.sql.Time}型を処理するためのConverter.
 * <p>標準の書式として{@code HH:mm:ss}で処理する。
 *
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class SqlTimeCellConverter extends AbstractDateCellConverter<Time> {

    @Override
    protected Time convertTypeValue(Date value) {
        
        return new Time(value.getTime());
    }
    
    @Override
    protected String getDefaultJavaPattern() {
        return "HH:mm:ss";
    }
    
    @Override
    protected String getDefaultExcelPattern() {
        return "HH:mm:ss";
    }
    
}
