package com.gh.mygreen.xlsmapper.cellconvert.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


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
        
        String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        cellValue = Utils.trim(cellValue, converterAnno);
        
        Character resultValue = null;
        if(Utils.isEmpty(cellValue)) {
            if(Utils.hasDefaultValue(converterAnno)) {
                resultValue = converterAnno.defaultValue().charAt(0);
            }
            
        } else {
            resultValue = cellValue.charAt(0);
        }
        
        if(resultValue == null && adaptor.getTargetClass().isPrimitive()) {
            resultValue = '\u0000';
        }
        
        return resultValue;
    }

    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Character targetValue, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Character value = targetValue;
        
        String cellValue = Utils.trim(value, converterAnno);
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, converterAnno);
        if(cellValue.length() >= 1) {
            cellValue = cellValue.substring(0, 1);
        }
        
        if(Utils.isNotEmpty(cellValue)) {
            cell.setCellValue(cellValue);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
