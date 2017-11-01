package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link BigInteger}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BigIntegerCellConverterFactory extends AbstractNumberCellConverterFactory<BigInteger> {
    
    @Override
    public BigIntegerCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final BigIntegerCellConverter cellConverter = new BigIntegerCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected BigInteger convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.toBigIntegerExact();
    }
    
    public class BigIntegerCellConverter extends AbstractNumberCellConverter<BigInteger> {
        
        private final BigIntegerCellConverterFactory convererFactory;
        
        private BigIntegerCellConverter(final FieldAccessor field, final Configuration config,
                final BigIntegerCellConverterFactory convererFactory) {
            
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected BigInteger convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
