package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link BigDecimal}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BigDecimalCellConverterFactory extends AbstractNumberCellConverterFactory<BigDecimal> {
    
    @Override
    public BigDecimalCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final BigDecimalCellConverter cellConverter = new BigDecimalCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected BigDecimal convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        return value;
    }
    
    public class BigDecimalCellConverter extends AbstractNumberCellConverter<BigDecimal> {
        
        private final BigDecimalCellConverterFactory convererFactory;
        
        private BigDecimalCellConverter(final FieldAccessor field, final Configuration config,
                final BigDecimalCellConverterFactory convererFactory) {
            
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected BigDecimal convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    } 
    
}
