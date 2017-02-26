package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;
import java.math.MathContext;


/**
 * float/Float型を処理するConverter.
 *
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class FloatCellConverter extends AbstractNumberCellConverter<Float> {
    
    @Override
    protected Float convertNumber(final double value, final MathContext context) {
        return new BigDecimal(value, context).floatValue();
    }
    
    @Override
    protected Float convertNumber(final Number value, final MathContext context) {
        return value.floatValue();
    }
    
    @Override
    protected Float convertNumber(final BigDecimal value) {
        return value.floatValue();
    }
    
    @Override
    protected Float getZeroValue() {
        return 0.0f;
    }
    
    @Override
    protected double getMaxValue() {
        return Float.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return -Float.MAX_VALUE;
    }
    
}
