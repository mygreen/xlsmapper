package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;


/**
 * float/Float型を処理するConverter.
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class FloatCellConverter extends AbstractNumberCellConverter<Float> {
    
    @Override
    protected Float convertTypeValue(final BigDecimal value) throws ArithmeticException {
        return value.floatValue();
    }
    
}
