package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ConversionException;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultItemConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ItemConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link List}型のConverter。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ListCellConverter extends AbstractCellConverter<List> {
    
    @Override
    protected List parseDefaultValue(final String defaultValue, final FieldAccessor accessor, final Configuration config) 
            throws TypeBindException {
        
        final XlsArrayConverter convertAnno = accessor.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> getDefaultArrayConverterAnnotation());
        
        final Optional<XlsTrim> trimAnno = accessor.getAnnotation(XlsTrim.class);
        
        Class<?> fieldClass = accessor.getType();
        Class<?> itemClass = convertAnno.itemClass();
        if(itemClass == Object.class) {
            itemClass = accessor.getComponentType();
        }
        
        try {
            List list = convertList(defaultValue, itemClass, trimAnno, convertAnno, config);
            if(List.class.isAssignableFrom(fieldClass)) {
                list = (List) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
            }
            return list;
        } catch(NumberFormatException e) {
            throw newTypeBindExceptionWithDefaultValue(e, accessor, defaultValue)
                .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
        }
    }

    @Override
    protected List parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        if(!formattedValue.isEmpty()) {
            
            final XlsArrayConverter convertAnno = accessor.getAnnotation(XlsArrayConverter.class)
                    .orElseGet(() -> getDefaultArrayConverterAnnotation());
            
            final Optional<XlsTrim> trimAnno = accessor.getAnnotation(XlsTrim.class);
            
            Class<?> fieldClass = accessor.getType();
            Class<?> itemClass = convertAnno.itemClass();
            if(itemClass == Object.class) {
                itemClass = accessor.getComponentType();
            }
            
            try {
                List list = convertList(formattedValue, itemClass, trimAnno, convertAnno, config);
                if(List.class.isAssignableFrom(fieldClass)) {
                    list = (List) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
                }
                return list;
            } catch(NumberFormatException e) {
                throw newTypeBindExceptionWithParse(e, evaluatedCell, accessor, formattedValue)
                    .addAllMessageVars(createTypeErrorMessageVars(convertAnno));
            }
            
        }
        
        return Collections.emptyList();
    }

    @Override
    protected void setupCell(final Cell cell, Optional<List> cellValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        final XlsArrayConverter convertAnno = accessor.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> getDefaultArrayConverterAnnotation());
        final Optional<XlsTrim> trimAnno = accessor.getAnnotation(XlsTrim.class);
        
        if(cellValue.isPresent() && !cellValue.get().isEmpty()) {
            
            final ItemConverter itemConverter = getItemConverter(convertAnno.itemConverterClass(), config);
            final String strValue = Utils.join(cellValue.get(), convertAnno.separator(), convertAnno.ignoreEmptyItem(), trimAnno.isPresent(), itemConverter);
            cell.setCellValue(strValue);
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    private List<?> convertList(final String value, Class<?> itemClass, final Optional<XlsTrim> trimAnno,
            final XlsArrayConverter anno, final Configuration config) throws ConversionException {
        
        final String[] split = value.split(anno.separator());
        final List list = new ArrayList<>();
        if(split.length == 0 || value.isEmpty()) {
            return list;
        }
        
        final ItemConverter itemConverter = getItemConverter(anno.itemConverterClass(), config);
        
        for(String item : split) {
            
            String strVal = Utils.trim(item, trimAnno);
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
    private ItemConverter getItemConverter(Class<? extends ItemConverter> converterClass, final Configuration config) {
        
        if(converterClass.isAssignableFrom(DefaultItemConverter.class)) {
            return config.getItemConverter();
        } else {
            return (ItemConverter) config.getBeanFactory().create(converterClass);
        }
        
    }
    
    XlsArrayConverter getDefaultArrayConverterAnnotation() {
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
    
}
