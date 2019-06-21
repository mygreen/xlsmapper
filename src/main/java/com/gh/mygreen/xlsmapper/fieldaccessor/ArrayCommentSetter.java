package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;

/**
 * {@link XlsArrayColumns}などのフィールドが配列またはリストに対するコメント情報のsetter
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ArrayCommentSetter {
    
    /**
     * フィールドのラベル情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param comment コメント情報
     * @param index 配列またはリストのインデックス。0から始まります。
     */
    void set(Object beanObj, String comment, int index);
    
    
}
