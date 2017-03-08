package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * 数値の{@link BigInteger}のConverter。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class BigIntegerCellConverter extends AbstractNumberCellConverter<BigInteger> {
    
    @Override
    protected BigInteger convertTypeValue(final BigDecimal value) throws ArithmeticException {
        return value.toBigInteger();
    }
    
}
