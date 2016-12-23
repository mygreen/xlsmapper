package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


/**
 * 数値の{@link BigInteger}のConverter。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class BigIntegerCellConverter extends AbstractNumberCellConverter<BigInteger> {
    
    private static final BigInteger ZERO = new BigDecimal(0).toBigInteger();
    
    @Override
    protected BigInteger convertNumber(final double value, final MathContext context) {
        return new BigDecimal(value, context).toBigInteger();
    }
    
    @Override
    protected BigInteger convertNumber(final Number value, final MathContext context) {
        return new BigDecimal(value.toString(), context).toBigInteger();
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
        return -Double.MAX_VALUE;
    }
    
}
