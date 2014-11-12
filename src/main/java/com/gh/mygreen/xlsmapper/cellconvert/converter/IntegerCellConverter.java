package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 * int/Integer型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class IntegerCellConverter extends AbstractNumberCellConverter<Integer> {
    
    @Override
    protected Integer convertNumber(double value) {
        return new BigDecimal(value).intValue();
    }
    
    @Override
    protected Integer convertNumber(final Number value) {
        return value.intValue();
    }
    
    @Override
    protected Integer convertNumber(final BigDecimal value) {
        return value.intValue();
    }
    
    @Override
    protected Integer getZeroValue() {
        return 0;
    }
    
    @Override
    protected double getMaxValue() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return Integer.MIN_VALUE;
    }
    
}
