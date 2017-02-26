package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.gh.mygreen.xlsmapper.converter.ConversionException;
import com.gh.mygreen.xlsmapper.converter.DefaultItemConverter;
import com.gh.mygreen.xlsmapper.converter.ItemConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
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
    public List toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsArrayConverter anno = adapter.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> getDefaultArrayConverterAnnotation());
        
        String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, defaultValueAnno);
        
        Class<?> fieldClass = adapter.getType();
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adapter.getComponentType();
        }
        
        try {
            List list = convertList(cellValue, itemClass, trimAnno, anno, config);
            if(List.class.isAssignableFrom(fieldClass)) {
                list = (List) Utils.convertListToCollection(list, (Class<Collection>)fieldClass, config.getBeanFactory());
            }
            return list;
        } catch(NumberFormatException e) {
            throw newTypeBindException(e, cell, adapter, cellValue)
                .addAllMessageVars(createTypeErrorMessageVars(anno));
        }
    }
    
    @SuppressWarnings("unchecked")
    protected List<?> convertList(final String value, Class<?> itemClass, final Optional<XlsTrim> trimAnno,
            final XlsArrayConverter anno, final XlsMapperConfig config) throws ConversionException {
        
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
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final List targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final XlsArrayConverter anno = adapter.getAnnotation(XlsArrayConverter.class)
                .orElseGet(() -> getDefaultArrayConverterAnnotation());
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adapter.getComponentType();
        }
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        List value = targetValue;
        
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && defaultValueAnno.isPresent()) {
            value = convertList(defaultValueAnno.get().value(), itemClass, trimAnno, anno, config);
        }
        
        if(Utils.isNotEmpty(value) && !primaryFormula) {
            final ItemConverter itemConverter = getItemConverter(anno.itemConverterClass(), config);
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
