package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultElementConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ElementConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.ListFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;

/**
 * {@link List}型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ListCellConverterFactory extends CellConverterFactorySupport<List>
        implements CellConverterFactory<List>{

    @Override
    public ListCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final ListCellConverter cellConverter = new ListCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<List> cellConverter, final FieldAccessor field,
            final Configuration config) {
        // 何もしない
    }
    
    @Override
    protected TextFormatter<List> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        Optional<XlsArrayConverter> converterAnno = field.getAnnotation(XlsArrayConverter.class);
        Optional<XlsTrim> trimAnno = field.getAnnotation(XlsTrim.class);
        
        final ListFormatter formatter = new ListFormatter(field.getComponentType());
        converterAnno.ifPresent(anno -> {
            formatter.setSeparator(anno.separator());
            formatter.setIgnoreEmptyElement(anno.ignoreEmptyElement());
            
            final ElementConverter elementConverter;
            if(anno.elementConverter().equals(DefaultElementConverter.class)) {
                elementConverter = new DefaultElementConverter();
            } else {
                elementConverter = (ElementConverter)config.getBeanFactory().create(anno.elementConverter());
            }
            formatter.setElementConverter(elementConverter);
        });
        trimAnno.ifPresent(a -> formatter.setTrimmed(true));
        
        return formatter;
    }
    
    public class ListCellConverter extends BaseCellConverter<List> {
        
        private ListCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected List parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            if(formattedValue.isEmpty()) {
                return Collections.emptyList();
            }
            
            try {
                return this.textFormatter.parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<List> cellValue) throws TypeBindException {
            
            final ListFormatter formatter = (ListFormatter) textFormatter;
            if(cellValue.isPresent()) {
                cell.setCellValue(formatter.format(cellValue.get()));
            } else {
                cell.setCellType(CellType.BLANK);
            }
            
        }
        
    }
    
}
