package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * フィールドの位置情報を設定するためのインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface PositionSetter {
    
    /**
     * フィールドの位置情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param position 位置情報
     */
    void set(Object beanObj, CellPosition position);
    
}
