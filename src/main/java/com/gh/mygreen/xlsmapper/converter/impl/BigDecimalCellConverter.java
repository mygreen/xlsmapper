package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;


/**
 * 数値の{@link BigDecimal}のConverter。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class BigDecimalCellConverter extends AbstractNumberCellConverter<BigDecimal> {
    
    @Override
    protected BigDecimal convertTypeValue(final BigDecimal value) throws ArithmeticException {
        return value;
    }
    
}
