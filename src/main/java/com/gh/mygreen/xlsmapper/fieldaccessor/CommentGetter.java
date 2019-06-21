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
public interface CommentGetter {
    
    /**
     * フィールドのコメント情報を取得します。
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @return コメント情報がない場合は、空を返します。
     */
    Optional<String> get(Object beanObj);
    
}
