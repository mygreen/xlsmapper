package org.mygreen.xlsmapper.cellconvert.converter;

import java.sql.Timestamp;
import java.util.Date;


/**
 * {@link java.sql.Timestamp}型を処理するためのConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class SqlTimestampCellConverter extends AbstractDateCellConverter<Timestamp> {

    @Override
    protected Timestamp convertDate(Date value) {
        
        return new Timestamp(value.getTime());
    }
    
    
    
}
