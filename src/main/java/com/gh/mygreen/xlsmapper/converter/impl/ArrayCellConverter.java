package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

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
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 配列型を変換するためのConverter。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellConverter extends AbstractCellConverter<Object[]> {
    
    @Override
    public Object[] toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        final XlsArrayConverter anno = adapter.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> converter.getDefaultArrayConverterAnnotation());
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adapter.getComponentType();
        }
        
        final List<?> list = converter.toObject(cell, adapter, config);
        return list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final Object[] targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final ListCellConverter converter = new ListCellConverter();
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsArrayConverter anno = adapter.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> converter.getDefaultArrayConverterAnnotation());
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adapter.getComponentType();
        }
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        Object[] value = targetValue;
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && defaultValueAnno.isPresent()) {
            final List<?> list = converter.convertList(defaultValueAnno.get().value(), itemClass, trimAnno, anno, config);
            value = list.toArray(((Object[])Array.newInstance(itemClass, list.size())));
        }
        
        if(Utils.isNotEmpty(value) && !primaryFormula) {
            final ItemConverter itemConverter = converter.getItemConverter(anno.itemConverterClass(), config);
            final String cellValue = Utils.join(value, anno.separator(), anno.ignoreEmptyItem(), trimAnno.isPresent(), itemConverter);
            cell.setCellValue(cellValue);
        
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }

}
