package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


/**
 * byte/Byte型を処理するConverter.
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class ByteCellConverter extends AbstractNumberCellConverter<Byte> {
    
    @Override
    protected Byte convertNumber(double value, final MathContext context) {
        // 少数以下を四捨五入
        BigDecimal decimal = new BigDecimal(value, context);
        decimal = decimal.setScale(0, RoundingMode.HALF_UP);
        return decimal.byteValueExact();
    }
    
    @Override
    protected Byte convertNumber(final Number value, final MathContext context) {
        return value.byteValue();
    }
    
    @Override
    protected Byte convertNumber(final BigDecimal value) {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.byteValueExact();
    }
    
    @Override
    protected Byte getZeroValue() {
        return 0;
    }
    
    @Override
    protected double getMaxValue() {
        return Byte.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return Byte.MIN_VALUE;
    }
    
    
}
