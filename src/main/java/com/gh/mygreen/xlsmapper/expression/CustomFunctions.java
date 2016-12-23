package com.gh.mygreen.xlsmapper.expression;

import org.apache.poi.ss.util.CellReference;

/**
 * EL式中で利用可能なEL関数。
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class CustomFunctions {
    
    /**
     * 列番号を英字名に変換します。
     * @param column 列番号(1から始まる)
     * @return 列の英字名
     */
    public static String colToAlpha(final int column) {
        return CellReference.convertNumToColString(column-1);
    }
    
}
