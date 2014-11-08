package org.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 * byte/Byte型を処理するConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class ByteCellConverter extends AbstractNumberCellConverter<Byte> {
    
    @Override
    protected Byte convertNumber(double value) {
        return new BigDecimal(value).byteValue();
    }
    
    @Override
    protected Byte convertNumber(final Number value) {
        return value.byteValue();
    }
    
    @Override
    protected Byte convertNumber(final BigDecimal value) {
        return value.byteValue();
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
