package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * short/Short型を処理するためのConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ShortCellConverter extends AbstractNumberCellConverter<Short> {
    
    @Override
    protected Short convertTypeValue(final BigDecimal value) throws ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.shortValueExact();
    }
    
}
