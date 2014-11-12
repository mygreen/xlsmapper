package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.sql.Time;
import java.util.Date;


/**
 * {@link java.sql.Time}型を処理するためのConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimeCellConverter extends AbstractDateCellConverter<Time> {

    @Override
    protected Time convertDate(Date value) {
        
        return new Time(value.getTime());
    }
    
    
    
}
