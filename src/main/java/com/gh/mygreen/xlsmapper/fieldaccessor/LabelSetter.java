package com.gh.mygreen.xlsmapper.fieldaccessor;

/**
 * フィールドのラベル情報を設定するためのインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface LabelSetter {
    
    /**
     * フィールドのラベル情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param label ラベル情報
     */
    void set(Object beanObj, String label);
    
}
