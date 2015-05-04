package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * int/Integer型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class IntegerCellConverter extends AbstractNumberCellConverter<Integer> {
    
    @Override
    protected Integer convertNumber(double value) {
        // 少数以下を四捨五入
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(0, RoundingMode.HALF_UP);
        return decimal.intValueExact();
    }
    
    @Override
    protected Integer convertNumber(final Number value) {
        return value.intValue();
    }
    
    @Override
    protected Integer convertNumber(final BigDecimal value) {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.intValueExact();
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
