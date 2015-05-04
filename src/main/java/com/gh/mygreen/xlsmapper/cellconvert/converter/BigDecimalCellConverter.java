package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 * 数値の{@link BigDecimal}のConverter。
 *
 * @author T.TSUCHIE
 *
 */
public class BigDecimalCellConverter extends AbstractNumberCellConverter<BigDecimal> {
    
    private static final BigDecimal ZERO = new BigDecimal(0);
    
    @Override
    protected BigDecimal convertNumber(double value) {
        return new BigDecimal(value);
    }
    
    @Override
    protected BigDecimal convertNumber(final Number value) {
        return new BigDecimal(value.doubleValue());
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
