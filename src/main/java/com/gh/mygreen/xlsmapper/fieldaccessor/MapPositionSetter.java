package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link XlsMapColumns}のフィールドに対する位置情報のsetter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface MapPositionSetter {
    
    /**
     * フィールドの位置情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param position 位置情報
     * @param key マップのキー
     */
    void set(Object beanObj, CellPosition position, String key);
    
    
}
