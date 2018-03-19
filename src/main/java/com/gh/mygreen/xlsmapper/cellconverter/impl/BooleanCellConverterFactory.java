package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.BooleanFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;

/**
 * {@link boolean}/{@link Boolean}型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BooleanCellConverterFactory extends CellConverterFactorySupport<Boolean>
        implements CellConverterFactory<Boolean> {
    
    @Override
    public BooleanCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final BooleanCellConverter cellConverter = new BooleanCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<Boolean> cellConverter, final FieldAccessor field,
            final Configuration config) {
        // 何もしない
        
    }
    
    @Override
    protected TextFormatter<Boolean> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        Optional<XlsBooleanConverter> converterAnno = field.getAnnotation(XlsBooleanConverter.class);
        
        return converterAnno.map(anno -> new BooleanFormatter(anno.loadForTrue(), anno.loadForFalse(), 
                anno.saveAsTrue(), anno.saveAsFalse(), anno.ignoreCase(), anno.failToFalse()))
                .orElseGet(() -> new BooleanFormatter());
        
    }
    
    public class BooleanCellConverter extends BaseCellConverter<Boolean> {
        
        private BooleanCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected Boolean parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            
            if(evaluatedCell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
                return evaluatedCell.getBooleanCellValue();
                
            } else if(!formattedValue.isEmpty()) {
                try {
                    return this.textFormatter.parse(formattedValue);
                } catch(TextParseException e) {
                    throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
                }
                
            }
            
            if(field.getType().isPrimitive()) {
                return false;
                
            } else if(field.isComponentType() && field.getComponentType().isPrimitive()) {
                return false;
            }
            
            return null;
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<Boolean> cellValue) throws TypeBindException {
            
            final BooleanFormatter formatter = (BooleanFormatter) textFormatter;
            
            if(cellValue.isPresent()) {
                if(formatter.getSaveTrueValue().equalsIgnoreCase("true") 
                        && formatter.getSaveFalseValue().equalsIgnoreCase("false")
                        && cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
                    // テンプレートのセルの書式がbooleanの場合はそのまま設定する
                    cell.setCellValue(cellValue.get());
                    
                } else {
                    cell.setCellValue(formatter.format(cellValue.get()));
                }
                
            } else {
                cell.setCellType(CellType.BLANK);
            }
        }
        
    }
    
}
