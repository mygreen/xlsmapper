package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.ItemConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 配列型を変換するためのConverter。
 *
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
public class ArrayCellConverter extends AbstractCellConverter<Object[]> {
    
    @Override
    public Object[] toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        final XlsArrayConverter anno = converter.getLoadingAnnotation(adaptor);
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getLoadingGenericClassType();
        }
        
        final List<?> list = converter.toObject(cell, adaptor, config);
        return list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Object[] targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsArrayConverter anno = converter.getSavingAnnotation(adaptor);
        final XlsFormula formulaAnno = adaptor.getSavingAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno == null ? false : formulaAnno.primary();
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getSavingGenericClassType();
        }
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.wrapText());
            POIUtils.shrinkToFit(cell, converterAnno.shrinkToFit());
        }
        
        Object[] value = targetValue;
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && Utils.hasDefaultValue(converterAnno)) {
            final List<?> list = converter.convertList(Utils.getDefaultValue(converterAnno), itemClass, converterAnno, anno, config);
            value = list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
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
