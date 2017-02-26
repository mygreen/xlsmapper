package com.gh.mygreen.xlsmapper.converter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
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
    public Character toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config) {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        cellValue = Utils.trim(cellValue, trimAnno);
        
        Character resultValue = null;
        if(Utils.isEmpty(cellValue)) {
            if(defaultValueAnno.isPresent()) {
                resultValue = defaultValueAnno.get().value().charAt(0);
            }
            
        } else {
            resultValue = cellValue.charAt(0);
        }
        
        if(resultValue == null && adapter.getType().isPrimitive()) {
            resultValue = '\u0000';
        }
        
        return resultValue;
    }

    @Override
    public Cell toCell(final FieldAdapter adapter, final Character targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row, final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        Character value = targetValue;
        
        String cellValue = Utils.trim(value, trimAnno);
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, defaultValueAnno);
        if(cellValue.length() >= 1) {
            cellValue = cellValue.substring(0, 1);
        }
        
        if(Utils.isNotEmpty(cellValue) && !primaryFormula) {
            cell.setCellValue(cellValue);
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
