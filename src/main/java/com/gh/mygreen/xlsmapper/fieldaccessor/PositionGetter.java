package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * フィールドの位置情報を取得するためのインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface PositionGetter {
    
    /**
     * フィールドの位置情報を取得します。
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @return 位置情報がない場合は、空を返します。
     */
    Optional<CellPosition> get(Object beanObj);
    
}
