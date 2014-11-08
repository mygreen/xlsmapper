package org.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.POIUtils;
import org.mygreen.xlsmapper.Utils;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;
import org.mygreen.xlsmapper.annotation.converter.XlsArrayConverter;
import org.mygreen.xlsmapper.annotation.converter.XlsConverter;
import org.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import org.mygreen.xlsmapper.cellconvert.ConversionException;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


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
        
        String cellValue = POIUtils.getCellContents(cell);
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, converterAnno);
        
        Class<?> itemClass = anno.itemClass();
        if(itemClass == Object.class) {
            itemClass = adaptor.getLoadingGenericClassType();
        }
        
        final List list = convertList(cellValue, itemClass, converterAnno, anno);
        return list;
    }
    
    @SuppressWarnings("unchecked")
    protected List<?> convertList(final String value, Class<?> itemClass, final XlsConverter converterAnno, final XlsArrayConverter anno) throws ConversionException {
        
        final String[] split = value.split(anno.separator());
        final List list = new ArrayList<>();
        if(split.length == 0 || value.isEmpty()) {
            return list;
        }
        
        for(String item : split) {
            
            if(anno.ignoreEmptyItem() && Utils.isEmpty(item)) {
                continue;
            }
            
            String strVal = Utils.trim(item, converterAnno);
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
            
        };
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
    public Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, null);
    }
    
    @Override
    public Cell toCellWithMap(final FieldAdaptor adaptor, final String key, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, key);
    }
    
    private Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config, final String mapKey) throws XlsMapperException {
        
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
        
        List value;
        if(mapKey == null) {
            value = (List) adaptor.getValue(targetObj);
        } else {
            value = (List) adaptor.getValueOfMap(mapKey, targetObj);
        }
        // デフォルト値から値を設定する
        if(Utils.isEmpty(value) && Utils.hasDefaultValue(converterAnno)) {
            value = convertList(Utils.getDefaultValue(converterAnno), itemClass, converterAnno, anno);
        }
        
        if(Utils.isNotEmpty(value)) {
            final String cellValue = Utils.join(value, anno.separator(), anno.ignoreEmptyItem());
            cell.setCellValue(cellValue);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
}
