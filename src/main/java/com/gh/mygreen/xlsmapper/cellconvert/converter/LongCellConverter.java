package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


/**
 * long/Long型を処理するためのConverter.
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class LongCellConverter extends AbstractNumberCellConverter<Long> {
    
    @Override
    protected Long convertNumber(final double value, final MathContext context) {
        // 少数以下を四捨五入
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(0, RoundingMode.HALF_UP);
        return decimal.longValueExact();
    }
    
    @Override
    protected Long convertNumber(final Number value, final MathContext context) {
        return value.longValue();
    }
    
    @Override
    protected Long convertNumber(final BigDecimal value) {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.longValueExact();
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
