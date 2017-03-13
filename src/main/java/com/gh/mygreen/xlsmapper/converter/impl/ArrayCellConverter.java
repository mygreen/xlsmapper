package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;


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
    protected Object[] parseDefaultValue(final String defaultValue,final  FieldAdapter adapter, final  XlsMapperConfig config) 
            throws TypeBindException {
        
        List<?> list = listConverter.parseDefaultValue(defaultValue, adapter, config);
        
        final XlsArrayConverter anno = adapter.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> listConverter.getDefaultArrayConverterAnnotation());
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adapter.getComponentType();
        }
        
        return list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
    }

    @Override
    protected Object[] parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        
        List<?> list = listConverter.parseCell(evaluatedCell, formattedValue, adapter, config);
        
        final XlsArrayConverter anno = adapter.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> listConverter.getDefaultArrayConverterAnnotation());
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adapter.getComponentType();
        }
        
        return list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
        
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected void setupCell(final Cell cell, final Optional<Object[]> cellValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        Optional<List> list = cellValue.map(c -> Arrays.asList(c));
        
        listConverter.setupCell(cell, list, adapter, config);
        
    }
    
}
