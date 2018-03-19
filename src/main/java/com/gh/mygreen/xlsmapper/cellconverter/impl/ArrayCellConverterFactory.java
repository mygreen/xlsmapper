package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
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
 * 配列型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellConverterFactory extends CellConverterFactorySupport<Object[]>
        implements CellConverterFactory<Object[]>{
    
    private ListCellConverterFactory listCellConverterFactory = new ListCellConverterFactory();
    
    @Override
    public ArrayCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final ArrayCellConverter cellConverter = new ArrayCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<Object[]> cellConverter, final FieldAccessor field,
            final Configuration config) {
        // 何もしない
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected TextFormatter<Object[]> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        TextFormatter<List> listFormatter = listCellConverterFactory.createTextFormatter(field, config);
        
        return new TextFormatter<Object[]>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public Object[] parse(String text) throws TextParseException {
                List list = listFormatter.parse(text);
                return list.toArray((Object[])Array.newInstance(field.getComponentType(), list.size()));
            }
            
            @Override
            public String format(Object[] value) {
                return listFormatter.format(Arrays.asList(value));
            }
        };
    }
    
    public class ArrayCellConverter extends BaseCellConverter<Object[]> {
        
        private ArrayCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected Object[] parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            try {
                return this.textFormatter.parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<Object[]> cellValue) throws TypeBindException {
            
            if(cellValue.isPresent()) {
                cell.setCellValue(textFormatter.format(cellValue.get()));
            } else {
                cell.setCellType(CellType.BLANK);
            }
            
        }
        
    }
    
}
