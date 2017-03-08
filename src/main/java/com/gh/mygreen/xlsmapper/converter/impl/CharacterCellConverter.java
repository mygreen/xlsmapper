package com.gh.mygreen.xlsmapper.converter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;


/**
 * char/Charcter型を処理するConverter.
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CharacterCellConverter extends AbstractCellConverter<Character> {
    
    @Override
    protected Character parseDefaultValue(final String defaultValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        return defaultValue.charAt(0);
    }
    
    @Override
    protected Character parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        if(!formattedValue.isEmpty()) {
            return formattedValue.charAt(0);
        
        }
        
        if(adapter.getType().isPrimitive()) {
            return '\u0000';
        }
        
        return null;
    }

    @Override
    protected void setupCell(final Cell cell, Optional<Character> cellValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        // \u0000 は、初期値として空と判定する。
        
        if(cellValue.isPresent() && cellValue.get() != '\u0000') {
            final String value = String.valueOf(cellValue.get());
            cell.setCellValue(value);
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }

}
