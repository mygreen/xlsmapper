package com.gh.mygreen.xlsmapper.converter.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * char/Charcter型を処理するConverter.
 * 
 * @since 1.5
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
    public Cell toCell(final FieldAdaptor adaptor, final Character targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row, final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsFormula formulaAnno = adaptor.getSavingAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno == null ? false : formulaAnno.primary();
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            cell.getCellStyle().setWrapText(converterAnno.wrapText());
            cell.getCellStyle().setShrinkToFit(converterAnno.shrinkToFit());
        }
        
        Character value = targetValue;
        
        String cellValue = Utils.trim(value, converterAnno);
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, converterAnno);
        if(cellValue.length() >= 1) {
            cellValue = cellValue.substring(0, 1);
        }
        
        if(Utils.isNotEmpty(cellValue) && !primaryFormula) {
            cell.setCellValue(cellValue);
            
        } else if(formulaAnno != null) {
            Utils.setupCellFormula(adaptor, formulaAnno, config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
