package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link XlsArrayColumns}などのフィールドが配列またはリストに対する位置情報のsetter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ArrayPositionSetter {
    
    /**
     * フィールドの位置情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param position 位置情報
     * @param index 配列またはリストのインデックス。0から始まります。
     */
    void set(Object beanObj, CellPosition position, int index);
    
    
}
