package com.gh.mygreen.xlsmapper.converter.impl;

import java.util.Date;


/**
 * {@link Date}型を処理するConverter.
 * <p>標準の書式として{@code yyyy-MM-dd HH:mm:ss}で処理する。
 * 
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class DateCellConverter extends AbstractDateCellConverter<Date> {

    @Override
    protected Date convertDate(Date value) {
        return value;
    }
    
    @Override
    protected String getDefaultJavaPattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }
    
    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd hh:mm:ss";
    }
}
