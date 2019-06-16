package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;

/**
 * {@link XlsMapColumns}のフィールドに対するコメント情報のsetter
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface MapCommentSetter {
    
    /**
     * フィールドのコメント情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param comment コメント情報
     * @param key マップのキー
     */
    void set(Object beanObj, String comment, String key);
    
    
}
