package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.MathContext;


/**
 * double/Double型を処理するConverter.
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class DoubleCellConverter extends AbstractNumberCellConverter<Double> {
    
    @Override
    protected Double convertNumber(final double value, final MathContext context) {
        return new BigDecimal(value, context).doubleValue();
    }
    
    @Override
    protected Double convertNumber(final Number value, final MathContext context) {
        return value.doubleValue();
    }
    
    @Override
    protected Double convertNumber(final BigDecimal value) {
        return value.doubleValue();
    }
    
    @Override
    protected Double getZeroValue() {
        return 0.0;
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
