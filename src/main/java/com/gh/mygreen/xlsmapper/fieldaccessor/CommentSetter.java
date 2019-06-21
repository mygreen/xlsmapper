package com.gh.mygreen.xlsmapper.fieldaccessor;

/**
 * フィールドのコメント情報を設定するためのインタフェース。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface CommentSetter {
    
    /**
     * フィールドのコメント情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param comment コメント情報
     */
    void set(Object beanObj, String comment);
    
}
