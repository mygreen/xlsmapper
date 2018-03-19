package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsEnumConverter;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.EnumFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;

/**
 * 列挙型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class EnumCellConverterFactory extends CellConverterFactorySupport<Enum>
        implements CellConverterFactory<Enum> {

    @Override
    public EnumCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final EnumCellConverter cellConverter = new EnumCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<Enum> cellConverter, final FieldAccessor field, final Configuration config) {
        // 何もしない
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected TextFormatter<Enum> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        Optional<XlsEnumConverter> converterAnno = field.getAnnotation(XlsEnumConverter.class);
        
        return converterAnno.map(anno -> anno.aliasMethod().isEmpty() ? 
                new EnumFormatter(field.getType(), anno.ignoreCase())
                : new EnumFormatter(field.getType(), anno.ignoreCase(), anno.aliasMethod()))
                .orElseGet(() -> new EnumFormatter(field.getType()));
        
    }
    
    public class EnumCellConverter extends BaseCellConverter<Enum> {
        
        private EnumCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected Enum parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            if(formattedValue.isEmpty()) {
                return null;
            }
            
            try {
                return this.textFormatter.parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue)
                    .addAllMessageVars(e.getErrorVariables());
            }
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<Enum> cellValue) throws TypeBindException {
            
            if(cellValue.isPresent()) {
                cell.setCellValue(textFormatter.format(cellValue.get()));
                
            } else {
                cell.setCellType(CellType.BLANK);
            }
            
        }
        
    }
    
}
