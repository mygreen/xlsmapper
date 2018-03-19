package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link Set}型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class SetCellConverterFactory extends CellConverterFactorySupport<Set>
        implements CellConverterFactory<Set>{
    
    private ListCellConverterFactory listCellConverterFactory = new ListCellConverterFactory();
    
    @Override
    public SetCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final SetCellConverter cellConverter = new SetCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<Set> cellConverter, final FieldAccessor field,
            final Configuration config) {
        // 何もしない
    }
    
    @Override
    protected TextFormatter<Set> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        TextFormatter<List> listFormatter = listCellConverterFactory.createTextFormatter(field, config);
        
        return new TextFormatter<Set>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public Set parse(String text) throws TextParseException {
                List list = listFormatter.parse(text);
                Set set = (Set)Utils.convertListToCollection(list, ((Class<Collection>)field.getType()), config.getBeanFactory());
                return set;
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public String format(Set value) {
                return listFormatter.format(new ArrayList<>(value));
            }
        };
    }
    
    public class SetCellConverter extends BaseCellConverter<Set> {
        
        private SetCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected Set parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            if(formattedValue.isEmpty()) {
                return Collections.emptySet();
            }
            
            try {
                return this.textFormatter.parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<Set> cellValue) throws TypeBindException {
            
            if(cellValue.isPresent()) {
                cell.setCellValue(textFormatter.format(cellValue.get()));
            } else {
                cell.setCellType(CellType.BLANK);
            }
            
        }
        
    }
    
}
