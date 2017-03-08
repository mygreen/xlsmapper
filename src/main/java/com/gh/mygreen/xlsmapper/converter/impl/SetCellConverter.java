package com.gh.mygreen.xlsmapper.converter.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.ItemConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link Set}型を変換するためのConverter。
 *
 * @version 2.0
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class SetCellConverter extends AbstractCellConverter<Set> {
    
    private ListCellConverter listConverter = new ListCellConverter();
    
    @Override
    protected Set parseDefaultValue(final String defaultValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        List list = listConverter.parseDefaultValue(defaultValue, adapter, config);
        
        Class<?> fieldClass = adapter.getType();
        
        Set<?> set = (Set) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
        return set;
    }

    @Override
    protected Set parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        List list = listConverter.parseCell(evaluatedCell, formattedValue, adapter, config);
        
        Class<?> fieldClass = adapter.getType();
        
        Set<?> set = (Set) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
        return set;
        
    }

    @Override
    protected void setupCell(final Cell cell, final Optional<Set> cellValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        Optional<List> set = cellValue.map(v -> new ArrayList<>(v));
        
        listConverter.setupCell(cell, set, adapter, config);
        
    }

}
