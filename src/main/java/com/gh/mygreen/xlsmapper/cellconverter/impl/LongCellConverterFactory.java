package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Long}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LongCellConverterFactory extends AbstractNumberCellConverterFactory<Long> {
    
    @Override
    public LongCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final LongCellConverter cellConverter = new LongCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected Long convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.longValueExact();
    }
    
    public class LongCellConverter extends AbstractNumberCellConverter<Long> {
        
        private final LongCellConverterFactory convererFactory;
        
        private LongCellConverter(final FieldAccessor field, final Configuration config,
                final LongCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Long convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
