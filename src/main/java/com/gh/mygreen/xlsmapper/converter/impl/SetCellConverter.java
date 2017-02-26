package com.gh.mygreen.xlsmapper.converter.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.ItemConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link Set}型を変換するためのConverter。
 *
 * @version 1.5
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class SetCellConverter extends AbstractCellConverter<Set> {
    
    @SuppressWarnings("unchecked")
    @Override
    public Set toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        final XlsArrayConverter anno = converter.getLoadingAnnotation(adaptor);
        
        Class<?> fieldClass = adaptor.getTargetClass();
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getLoadingGenericClassType();
        }
        
        final List<?> list = converter.toObject(cell, adaptor, config);
        Set<?> set = (Set) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
        return set;
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Set targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        final XlsFormula formulaAnno = adaptor.getSavingAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno == null ? false : formulaAnno.primary();
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsArrayConverter anno = converter.getSavingAnnotation(adaptor);
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getSavingGenericClassType();
        }
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            cell.getCellStyle().setWrapText(converterAnno.wrapText());
            cell.getCellStyle().setShrinkToFit(converterAnno.shrinkToFit());
        }
        
        Set value = targetValue;
        
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && Utils.hasDefaultValue(converterAnno)) {
            final List<?> list = converter.convertList(Utils.getDefaultValue(converterAnno), itemClass, converterAnno, anno, config);
            value = new LinkedHashSet(list);
        }
        
        if(Utils.isNotEmpty(value) && !primaryFormula) {
            final boolean trim = (converterAnno == null ? false : converterAnno.trim()); 
            final ItemConverter itemConverter = converter.getItemConverter(anno.itemConverterClass(), config);
            final String cellValue = Utils.join(value, anno.separator(), anno.ignoreEmptyItem(), trim, itemConverter);
            cell.setCellValue(cellValue);
            
        } else if(formulaAnno != null) {
            Utils.setupCellFormula(adaptor, formulaAnno, config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }

}
