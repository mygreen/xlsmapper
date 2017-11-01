package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Integer}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class IntegerCellConverterFactory extends AbstractNumberCellConverterFactory<Integer> {
    
    @Override
    public IntegerCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final IntegerCellConverter cellConverter = new IntegerCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected Integer convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.intValueExact();
    }
    
    public class IntegerCellConverter extends AbstractNumberCellConverter<Integer> {
        
        private final IntegerCellConverterFactory convererFactory;
        
        private IntegerCellConverter(final FieldAccessor field, final Configuration config,
                final IntegerCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Integer convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
