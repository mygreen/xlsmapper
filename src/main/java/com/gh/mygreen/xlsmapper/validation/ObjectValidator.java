package com.gh.mygreen.xlsmapper.validation;



/**
 * Validatorのインタフェース
 *
 * @version 2.0
 * @since 1.0
 * @param <T> チェック対象のBeanのクラスタイプ
 * @author T.TSUCHIE
 *
 */
public interface ObjectValidator<T> {

    /**
     * オブジェクトの値を検証する。
     *
     * @param targetObj チェック対象のオブジェクト
     * @param errors エラー情報
     * @param groups バリデーション時のヒントとなるグループ。
     */
    void validate(T targetObj, SheetBindingErrors<?> errors, Class<?>... groups);
}
