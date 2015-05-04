package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 * double/Double型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class DoubleCellConverter extends AbstractNumberCellConverter<Double> {
    
    @Override
    protected Double convertNumber(double value) {
        return new BigDecimal(value).doubleValue();
    }
    
    @Override
    protected Double convertNumber(final Number value) {
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
