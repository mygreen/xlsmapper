package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;


/**
 * double/Double型を処理するConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class DoubleCellConverter extends AbstractNumberCellConverter<Double> {
    
    @Override
    protected Double convertTypeValue(final BigDecimal value) throws ArithmeticException {
        return value.doubleValue();
    }
    
}
