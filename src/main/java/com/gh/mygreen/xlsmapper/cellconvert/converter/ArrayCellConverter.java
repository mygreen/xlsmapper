package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 配列型を変換するためのConverter。
 *
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
    public Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, null);
    }
    
    @Override
    public Cell toCellWithMap(FieldAdaptor adaptor, String key, Object targetObj, Sheet sheet,
            int column, int row, XlsMapperConfig config)
            throws XlsMapperException {
        return toCell(adaptor, targetObj, sheet, column, row, config, key);
    }
    
    private Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config, final String mapKey) throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsArrayConverter anno = converter.getSavingAnnotation(adaptor);
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getSavingGenericClassType();
        }
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Object[] value;
        if(mapKey == null) {
            value = (Object[]) adaptor.getValue(targetObj);
        } else {
            value = (Object[]) adaptor.getValueOfMap(mapKey, targetObj);
        }
        
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && Utils.hasDefaultValue(converterAnno)) {
            final List<?> list = converter.convertList(Utils.getDefaultValue(converterAnno), itemClass, converterAnno, anno);
            value = list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
        }
        
        if(Utils.isNotEmpty(value)) {
            final boolean trim = (converterAnno == null ? false : converterAnno.trim()); 
            final String cellValue = Utils.join(value, anno.separator(), anno.ignoreEmptyItem(), trim);
            cell.setCellValue(cellValue);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }

}
