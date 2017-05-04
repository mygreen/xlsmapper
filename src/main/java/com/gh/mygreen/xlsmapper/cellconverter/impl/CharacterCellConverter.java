package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;


/**
 * char/Charcter型を処理するConverter.
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CharacterCellConverter extends AbstractCellConverter<Character> {
    
    @Override
    protected Character parseDefaultValue(final String defaultValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        return defaultValue.charAt(0);
    }
    
    @Override
    protected Character parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        if(!formattedValue.isEmpty()) {
            return formattedValue.charAt(0);
        
        }
        
        if(accessor.getType().isPrimitive()) {
            return '\u0000';
            
        } else if(accessor.isComponentType() && accessor.getComponentType().isPrimitive()) {
            return '\u0000';
        }
        
        return null;
    }

    @Override
    protected void setupCell(final Cell cell, Optional<Character> cellValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        // \u0000 は、初期値として空と判定する。
        
        if(cellValue.isPresent() && cellValue.get() != '\u0000') {
            final String value = String.valueOf(cellValue.get());
            cell.setCellValue(value);
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }

}
