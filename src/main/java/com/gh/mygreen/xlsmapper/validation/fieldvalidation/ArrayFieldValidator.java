package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.List;

/**
 * フィールドに対する値の検証を行うためのインタフェース。
 * <p>{@link ArrayCellField}に対して実装を追加して利用します。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ArrayFieldValidator<E> {

    /**
     * 値の検証を行います。
     * @param cellField 検証対象のフィールド情報。
     * @param groups 検証する際のヒントとなるグループ。
     * @return trueの場合、正常の値と判定します。
     */
    boolean validate(ArrayCellField<E> cellField, List<Class<?>> groups);

}
