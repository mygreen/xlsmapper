package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * 数値の{@link BigInteger}のConverter。
 *
 * @author T.TSUCHIE
 *
 */
public class BigIntegerCellConverter extends AbstractNumberCellConverter<BigInteger> {
    
    private static final BigInteger ZERO = new BigDecimal(0).toBigInteger();
    
    @Override
    protected BigInteger convertNumber(double value) {
        return new BigDecimal(value).toBigInteger();
    }
    
    @Override
    protected BigInteger convertNumber(final Number value) {
        return new BigInteger(value.toString());
    }
    
    @Override
    protected BigInteger convertNumber(final BigDecimal value) {
        return value.toBigInteger();
    }
    
    @Override
    protected BigInteger getZeroValue() {
        return ZERO;
    }
    
    @Override
    protected double getMaxValue() {
        return Double.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return Double.MIN_VALUE;
    }
    
}
