package org.mygreen.xlsmapper.cellconvert.converter;

import java.math.BigDecimal;


/**
 * short/Short型を処理するためのConveter.
 *
 * @author T.TSUCHIE
 *
 */
public class ShortCellConverter extends AbstractNumberCellConverter<Short> {
    
    @Override
    protected Short convertNumber(double value) {
        return new BigDecimal(value).shortValue();
    }
    
    @Override
    protected Short convertNumber(final Number value) {
        return value.shortValue();
    }
    
    @Override
    protected Short convertNumber(final BigDecimal value) {
        return value.shortValue();
    }
    
    @Override
    protected Short getZeroValue() {
        return 0;
    }
    
    @Override
    protected double getMaxValue() {
        return Short.MAX_VALUE;
    }
    
    @Override
    protected double getMinValue() {
        return Short.MIN_VALUE;
    }
    
}
