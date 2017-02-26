package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.ConversionException;
import com.gh.mygreen.xlsmapper.converter.DefaultItemConverter;
import com.gh.mygreen.xlsmapper.converter.ItemConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link List}型のConverter。
 * 
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ListCellConverter extends AbstractCellConverter<List> {
    
    @Override
    public List toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsArrayConverter anno = getLoadingAnnotation(adaptor);
        
        String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, converterAnno);
        
        Class<?> fieldClass = adaptor.getTargetClass();
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getLoadingGenericClassType();
        }
        
        try {
            List list = convertList(cellValue, itemClass, converterAnno, anno, config);
            if(List.class.isAssignableFrom(fieldClass)) {
                list = (List) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
            }
            return list;
        } catch(NumberFormatException e) {
            throw newTypeBindException(e, cell, adaptor, cellValue)
                .addAllMessageVars(createTypeErrorMessageVars(anno));
        }
    }
    
    @SuppressWarnings("unchecked")
    protected List<?> convertList(final String value, Class<?> itemClass, final XlsConverter converterAnno,
            final XlsArrayConverter anno, final XlsMapperConfig config) throws ConversionException {
        
        final String[] split = value.split(anno.separator());
        final List list = new ArrayList<>();
        if(split.length == 0 || value.isEmpty()) {
            return list;
        }
        
        final ItemConverter itemConverter = getItemConverter(anno.itemConverterClass(), config);
        
        for(String item : split) {
            
            String strVal = Utils.trim(item, converterAnno);
            if(anno.ignoreEmptyItem() && Utils.isEmpty(strVal)) {
                continue;
            }
            
            list.add(itemConverter.convertToObject(strVal, itemClass));
        }
        return list;
        
    }
    
    /**
     * リストの要素の値を変換するクラスを取得する。
     * @param converterClass
     * @param config
     * @return
     */
    ItemConverter getItemConverter(Class<? extends ItemConverter> converterClass, final XlsMapperConfig config) {
        
        if(converterClass.isAssignableFrom(DefaultItemConverter.class)) {
            return config.getItemConverter();
        } else {
            return (ItemConverter) config.getBeanFactory().create(converterClass);
        }
        
    }
    
    protected XlsArrayConverter getDefaultArrayConverterAnnotation() {
        return new XlsArrayConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsArrayConverter.class;
            }
            
            @Override
            public String separator() {
                return ",";
            }
            
            @Override
            public Class<?> itemClass() {
                return Object.class;
            }
            
            @Override
            public boolean ignoreEmptyItem() {
                return false;
            }
            
            @Override
            public Class<? extends ItemConverter> itemConverterClass() {
                return DefaultItemConverter.class;
            }
            
        };
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     */
    private Map<String, Object> createTypeErrorMessageVars(final XlsArrayConverter anno) {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("separator", anno.separator());
        vars.put("ignoreEmptyItem", anno.ignoreEmptyItem());
        vars.put("itemConverter", Utils.convertToString(anno.itemConverterClass()));
        return vars;
    }
    
    protected XlsArrayConverter getLoadingAnnotation(final FieldAdaptor adaptor) {
        XlsArrayConverter anno = adaptor.getLoadingAnnotation(XlsArrayConverter.class);
        if(anno == null) {
            anno = getDefaultArrayConverterAnnotation();
        }
        return anno;
    }
    
    protected XlsArrayConverter getSavingAnnotation(final FieldAdaptor adaptor) {
        XlsArrayConverter anno = adaptor.getSavingAnnotation(XlsArrayConverter.class);
        if(anno == null) {
            anno = getDefaultArrayConverterAnnotation();
        }
        return anno;
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final List targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsArrayConverter anno = getSavingAnnotation(adaptor);
        final XlsFormula formulaAnno = adaptor.getSavingAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno == null ? false : formulaAnno.primary();
        
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
        
        List value = targetValue;
        
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && Utils.hasDefaultValue(converterAnno)) {
            value = convertList(Utils.getDefaultValue(converterAnno), itemClass, converterAnno, anno, config);
        }
        
        if(Utils.isNotEmpty(value) && !primaryFormula) {
            final boolean trim = (converterAnno == null ? false : converterAnno.trim()); 
            final ItemConverter itemConverter = getItemConverter(anno.itemConverterClass(), config);
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
