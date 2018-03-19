package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;

/**
 * {@link String}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class StringCellConverterFactory extends CellConverterFactorySupport<String>
        implements CellConverterFactory<String> {
    
    @Override
    public StringCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final StringCellConverter cellConverter = new StringCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<String> cellConverter, final FieldAccessor field, final Configuration config) {
        // 何もしない
    }
    
    @Override
    protected TextFormatter<String> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        return new TextFormatter<String>() {
            
            @Override
            public String parse(String text) throws TextParseException {
                
                if(text.isEmpty() && !field.hasAnnotation(XlsTrim.class)) {
                    // トリムを行わない場合は、空文字をnullに補完する。
                    return null;
                }
                
                return text;
            }
            
            @Override
            public String format(String value) {
                return value;
            }
        };
    }
    
    public class StringCellConverter extends BaseCellConverter<String> {
        
        private StringCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected String parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            
            return textFormatter.parse(formattedValue);
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<String> cellValue) throws TypeBindException {
            
            if(cellValue.isPresent() && !cellValue.get().isEmpty()) {
                cell.setCellValue(cellValue.get());
                
            } else {
                cell.setCellType(CellType.BLANK);
            }
        }
        
    }
    
}
