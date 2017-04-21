package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;


/**
 * {@link Calendar}型の変換用クラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CalendarCellConverter extends AbstractCellConverter<Calendar> {
    
    private DateCellConverter dateConverter = new DateCellConverter();
    
    @Override
    protected Calendar parseDefaultValue(final String defaultValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        Date date = dateConverter.parseDefaultValue(defaultValue, accessor, config);
        Calendar cal= Calendar.getInstance();
        cal.setTime(date);
        
        return cal;
    }
    
    @Override
    protected Calendar parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        Date date = dateConverter.parseCell(evaluatedCell, formattedValue, accessor, config);
        if(date != null) {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }
        
        return null;
        
    }

    @Override
    protected void setupCell(final Cell cell, Optional<Calendar> cellValue, final FieldAccessor accessor, final Configuration config)
            throws TypeBindException {
        
        Optional<Date> date = cellValue.map(c -> c.getTime());
        
        dateConverter.setupCell(cell, date, accessor, config);
        
    }
    
}
