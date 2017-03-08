package com.gh.mygreen.xlsmapper.converter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * byte/Byte型を処理するConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ByteCellConverter extends AbstractNumberCellConverter<Byte> {
    
    @Override
    protected Byte convertTypeValue(BigDecimal value) throws ArithmeticException {
        // 少数以下を四捨五入
        BigDecimal decimal = value.setScale(0, RoundingMode.HALF_UP);
        return decimal.byteValueExact();
    }
    
    
}
