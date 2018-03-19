package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;

/**
 *  char/Character を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CharacterCellConverterFactory extends CellConverterFactorySupport<Character>
        implements CellConverterFactory<Character> {
    
    private static final char DEFAULT_VALUE = '\u0000';;
    
    @Override
    public CharacterCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final CharacterCellConverter cellConverter = new CharacterCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<Character> cellConverter, final FieldAccessor field, final Configuration config) {
        // 何もしない
        
    }
    
    @Override
    protected TextFormatter<Character> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        return new TextFormatter<Character>() {
            
            @Override
            public Character parse(final String text) throws TextParseException {
                
                if(text.length() >= 1) {
                    return text.charAt(0);
                }
                
                // 値が空のとき
                if(field.getType().isPrimitive()) {
                    return DEFAULT_VALUE;
                    
                } else if(field.isComponentType() && field.getComponentType().isPrimitive()) {
                    return DEFAULT_VALUE;
                }
                
                return null;
            }
            
            @Override
            public String format(final Character value) {
                return value.toString();
            }
        };
    }
    
    public class CharacterCellConverter extends BaseCellConverter<Character> {
        
        private CharacterCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected Character parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            
            return this.textFormatter.parse(formattedValue);
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<Character> cellValue) throws TypeBindException {
            
            // \u0000 は、初期値として空と判定する。
            if(cellValue.isPresent() && cellValue.get() != DEFAULT_VALUE) {
                cell.setCellValue(cellValue.get().toString());
                
            } else {
                cell.setCellType(CellType.BLANK);
            }
        }
        
    }
    
}
