package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.Set;

/**
 * フィールドに対する値の検証を行うためのインタフェース。
 * <p>{@link CellField}に対して実装を追加して利用します。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface FieldValidator<T> {
    
    /**
     * 値の検証を行います。
     * @param cellField 検証対象のフィールド情報
     * @param hints 検証する際のヒント
     * @return trueの場合、正常の値と判定します。
     */
    boolean validate(CellField<T> cellField, Set<Object> hints);
    
}
