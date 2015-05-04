package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * byte/Byte型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class ByteCellConverter extends AbstractNumberCellConverter<Byte> {
    
    @Override
    protected Byte convertNumber(double value) {
        // 少数以下を四捨五入
        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(0, RoundingMode.HALF_UP);
        return decimal.byteValueExact();
    }
    
    @Override
    protected Byte convertNumber(final Number value) {
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
