package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Double}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DoubleCellConverterFactory extends AbstractNumberCellConverterFactory<Double> {
    
    @Override
    public DoubleCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final DoubleCellConverter cellConverter = new DoubleCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected Double convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        return value.doubleValue();
    }
    
    public class DoubleCellConverter extends AbstractNumberCellConverter<Double> {
        
        private final DoubleCellConverterFactory convererFactory;
        
        private DoubleCellConverter(final FieldAccessor field, final Configuration config,
                final DoubleCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Double convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
}
