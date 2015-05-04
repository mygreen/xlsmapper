package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 * float/Float型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class FloatCellConverter extends AbstractNumberCellConverter<Float> {
    
    @Override
    protected Float convertNumber(double value) {
        return new BigDecimal(value).floatValue();
    }
    
    @Override
    protected Float convertNumber(final Number value) {
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
