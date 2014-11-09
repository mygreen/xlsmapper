package org.mygreen.xlsmapper.cellconvert.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.POIUtils;
import org.mygreen.xlsmapper.Utils;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.annotation.converter.XlsConverter;
import org.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * char/Charcter型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class CharacterCellConverter extends AbstractCellConverter<Character> {

    @Override
    public Character toObject(Cell cell, FieldAdaptor adaptor, XlsMapperConfig config) {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        final String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        Character resultValue = null;
        if(Utils.isNotEmpty(cellValue)) {
            resultValue = cellValue.charAt(0);
            
        } else if(Utils.hasDefaultValue(converterAnno)) {
            resultValue = converterAnno.defaultValue().charAt(0);
            
        }
        
        if(resultValue == null && adaptor.getTargetClass().isPrimitive()) {
            resultValue = 0;
        }
        
        return resultValue;
    }

    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, null);
    }
    
    @Override
    public Cell toCellWithMap(final FieldAdaptor adaptor, final String key, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, key);
    }
    
    private Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config, final String mapKey) {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Character value;
        if(mapKey == null) {
            value = (Character)adaptor.getValue(targetObj);
        } else {
            value = (Character)adaptor.getValueOfMap(mapKey, targetObj);
        }
        String cellValue = Utils.trim(value, converterAnno);
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, converterAnno);
        if(Utils.isNotEmpty(cellValue)) {
            cell.setCellValue(cellValue);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
