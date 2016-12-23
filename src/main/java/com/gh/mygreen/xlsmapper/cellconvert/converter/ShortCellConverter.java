package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


/**
 * short/Short型を処理するためのConveter.
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class ShortCellConverter extends AbstractNumberCellConverter<Short> {
    
    @Override
    protected Short convertNumber(final double value, final MathContext context) {
        // 少数以下を四捨五入
        BigDecimal decimal = new BigDecimal(value, context);
        decimal = decimal.setScale(0, RoundingMode.HALF_UP);
        return decimal.shortValueExact();
    }
    
    @Override
    protected Short convertNumber(final Number value, final MathContext context) {
        return value.shortValue();
    }
    
    @Override
    protected Short convertNumber(final BigDecimal value) {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.shortValueExact();
    }
    
    @Override
    protected Short getZeroValue() {
        return 0;
    }
    
    @Override
    protected double getMaxValue() {
        return Short.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return Short.MIN_VALUE;
    }
    
}
