package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Byte}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ByteCellConverterFactory extends AbstractNumberCellConverterFactory<Byte> {
    
    @Override
    public ByteCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final ByteCellConverter cellConverter = new ByteCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected Byte convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.byteValueExact();
    }
    
    public class ByteCellConverter extends AbstractNumberCellConverter<Byte> {
        
        private final ByteCellConverterFactory convererFactory;
        
        private ByteCellConverter(final FieldAccessor field, final Configuration config,
                final ByteCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Byte convertTypeValue(final BigDecimal value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
