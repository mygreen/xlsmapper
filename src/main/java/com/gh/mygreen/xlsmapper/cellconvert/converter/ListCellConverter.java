package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsItemConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.ConversionException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * {@link List}型のConverter。
 * 
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
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getLoadingGenericClassType();
        }
        
        try {
            final List list = convertList(cellValue, itemClass, converterAnno, anno);
            return list;
        } catch(NumberFormatException e) {
            throw newTypeBindException(e, cell, adaptor, cellValue)
                .addAllMessageVars(createTypeErrorMessageVars(anno));
        }
    }
    
    @SuppressWarnings("unchecked")
    protected List<?> convertList(final String value, Class<?> itemClass, final XlsConverter converterAnno, final XlsArrayConverter anno) throws ConversionException {
        
        final String[] split = value.split(anno.separator());
        final List list = new ArrayList<>();
        if(split.length == 0 || value.isEmpty()) {
            return list;
        }
        
        for(String item : split) {
            
            String strVal = Utils.trim(item, converterAnno);
            if(anno.ignoreEmptyItem() && Utils.isEmpty(strVal)) {
                continue;
            }
            
            list.add(Utils.convertToObject(strVal, itemClass));
        }
        return list;
        
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
            public XlsItemConverter[] itemConverter() {
                return new XlsItemConverter[]{};
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
    public Cell toCell(final FieldAdaptor adaptor, final List targetValue, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsArrayConverter anno = getSavingAnnotation(adaptor);
        
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
        
        List value = targetValue;
        
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && Utils.hasDefaultValue(converterAnno)) {
            value = convertList(Utils.getDefaultValue(converterAnno), itemClass, converterAnno, anno);
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
