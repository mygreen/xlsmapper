package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.util.Optional;

/**
 * フィールドのコメント情報を取得するためのインタフェース。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface MapCommentGetter {
    
    /**
     * フィールドのコメント情報を取得します。
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param key マップのキー
     * @return コメント情報がない場合は、空を返します。
     */
    Optional<String> get(Object beanObj, String key);
    
}
