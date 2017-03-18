package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.util.Optional;

/**
 * フィールドのラベル情報を取得するためのインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface LabelGetter {
    
    /**
     * フィールドのラベル情報を取得します。
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @return ラベル情報がない場合は、空を返します。
     */
    Optional<String> get(Object beanObj);
    
}
