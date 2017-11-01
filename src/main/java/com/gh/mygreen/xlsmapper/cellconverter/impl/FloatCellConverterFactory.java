package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Float}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FloatCellConverterFactory extends AbstractNumberCellConverterFactory<Float> {
    
    @Override
    public FloatCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final FloatCellConverter cellConverter = new FloatCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected Float convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        return value.floatValue();
    }
    
    public class FloatCellConverter extends AbstractNumberCellConverter<Float> {
        
        private final FloatCellConverterFactory convererFactory;
        
        private FloatCellConverter(final FieldAccessor field, final Configuration config,
                final FloatCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Float convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
