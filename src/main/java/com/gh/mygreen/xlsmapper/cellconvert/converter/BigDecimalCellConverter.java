package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.MathContext;


/**
 * 数値の{@link BigDecimal}のConverter。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class BigDecimalCellConverter extends AbstractNumberCellConverter<BigDecimal> {
    
    private static final BigDecimal ZERO = new BigDecimal(0);
    
    @Override
    protected BigDecimal convertNumber(final double value, final MathContext context) {
        return new BigDecimal(value, context);
    }
    
    @Override
    protected BigDecimal convertNumber(final Number value, final MathContext context) {
        return new BigDecimal(value.doubleValue(), context);
    }
    
    @Override
    protected BigDecimal convertNumber(final BigDecimal value) {
        return value;
    }
    
    @Override
    protected BigDecimal getZeroValue() {
        return ZERO;
    }
    
    @Override
    protected double getMaxValue() {
        return Double.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return -Double.MAX_VALUE;
    }
    
}
