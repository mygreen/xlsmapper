package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * int/Integer型を処理するConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class IntegerCellConverter extends AbstractNumberCellConverter<Integer> {
    
    @Override
    protected Integer convertTypeValue(final BigDecimal value) throws ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.intValueExact();
    }
    
}
