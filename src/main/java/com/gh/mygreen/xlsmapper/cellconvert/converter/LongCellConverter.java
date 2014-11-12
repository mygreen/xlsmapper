package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class LongCellConverter extends AbstractNumberCellConverter<Long> {
    
    @Override
    protected Long convertNumber(double value) {
        return new BigDecimal(value).longValue();
    }
    
    @Override
    protected Long convertNumber(final Number value) {
        return value.longValue();
    }
    
    @Override
    protected Long convertNumber(final BigDecimal value) {
        return value.longValue();
    }
    
    @Override
    protected Long getZeroValue() {
        return 0L;
    }
    
    @Override
    protected double getMaxValue() {
        return Long.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return Long.MIN_VALUE;
    }
    
}
