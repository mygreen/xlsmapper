package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;

/**
 * {@link XlsArrayColumns}などのフィールドが配列またはリストに対するラベル情報のsetter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ArrayLabelSetter {
    
    /**
     * フィールドのラベル情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param label ラベル情報
     * @param index 配列またはリストのインデックス。0から始まります。
     */
    void set(Object beanObj, String label, int index);
    
    
}
