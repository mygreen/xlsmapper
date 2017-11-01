package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Short}型を処理する{@link CellConverter}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ShortCellConverterFactory extends AbstractNumberCellConverterFactory<Short> {
    
    @Override
    public ShortCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final ShortCellConverter cellConverter = new ShortCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected Short convertTypeValue(final BigDecimal value) throws NumberFormatException, ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.shortValueExact();
    }
    
    public class ShortCellConverter extends AbstractNumberCellConverter<Short> {
            
            private final ShortCellConverterFactory convererFactory;
            
            private ShortCellConverter(final FieldAccessor field, final Configuration config,
                    final ShortCellConverterFactory convererFactory) {
                super(field, config);
                this.convererFactory = convererFactory;
            }
            
            @Override
            protected Short convertTypeValue(final BigDecimal value) {
                return convererFactory.convertTypeValue(value);
            }
            
        }
    
}
