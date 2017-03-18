package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;


/**
 * 配列型を変換するためのConverter。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellConverter extends AbstractCellConverter<Object[]> {
    
    private ListCellConverter listConverter = new ListCellConverter();
    
    @Override
    protected Object[] parseDefaultValue(final String defaultValue,final  FieldAccessor accessor, final  XlsMapperConfig config) 
            throws TypeBindException {
        
        List<?> list = listConverter.parseDefaultValue(defaultValue, accessor, config);
        
        final XlsArrayConverter anno = accessor.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> listConverter.getDefaultArrayConverterAnnotation());
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = accessor.getComponentType();
        }
        
        return list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
    }

    @Override
    protected Object[] parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor,
            final XlsMapperConfig config) throws TypeBindException {
        
        
        List<?> list = listConverter.parseCell(evaluatedCell, formattedValue, accessor, config);
        
        final XlsArrayConverter anno = accessor.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> listConverter.getDefaultArrayConverterAnnotation());
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = accessor.getComponentType();
        }
        
        return list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
        
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected void setupCell(final Cell cell, final Optional<Object[]> cellValue, final FieldAccessor accessor,
            final XlsMapperConfig config) throws TypeBindException {
        
        Optional<List> list = cellValue.map(c -> Arrays.asList(c));
        
        listConverter.setupCell(cell, list, accessor, config);
        
    }
    
}
