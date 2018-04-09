package com.gh.mygreen.xlsmapper.validation;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * オブジェクトのValidatorの抽象クラス。
 *
 * @author T.TSUCHIE
 * @version 2.0
 * @param <T> 検証対象のオブジェクトのクラスタイプ
 */
public abstract class ObjectValidatorSupport<T> implements ObjectValidator<T> {

    /**
     * ネストしたプロパティの値の検証を実行する。
     * @param <S> ネストしたプロパティのクラスタイプ
     * @param validator ネストしたプロパティに対するValidator
     * @param targetObject ネストしたプロパティのインスタンス
     * @param errors エラー情報
     * @param subPath ネストするパス
     * @param groups バリデーション時のヒントとなるグループ。
     * @throws IllegalArgumentException {@literal validator == null or targetObject == null or errors == null}
     */
    protected <S> void invokeNestedValidator(final ObjectValidator<S> validator, final S targetObject,
            final SheetBindingErrors<?> errors, final String subPath, final Class<?>... groups) {

        ArgUtils.notNull(validator, "validator");
        ArgUtils.notNull(targetObject, "targetObject");
        ArgUtils.notNull(errors, "errors");

        errors.pushNestedPath(subPath);
        try {
            validator.validate(targetObject, errors, groups);
        } finally {
            errors.popNestedPath();
        }
    }

    /**
     * リストや配列の形式のネストしたプロパティの値の検証を実行する。
     * @param <S> ネストしたプロパティのクラスタイプ
     * @param validator ネストしたプロパティに対するValidator
     * @param targetObject ネストしたプロパティのインスタンス
     * @param errors エラー情報
     * @param subPath ネストするパス
     * @param index リストや配列のインデックス。0から始まる。
     * @param groups バリデーション時のヒントとなるグループ。
     * @throws IllegalArgumentException {@literal validator == null or targetObject == null or errors == null}
     * @throws IllegalArgumentException {@literal index < 0}
     */
    protected <S> void invokeNestedValidator(final ObjectValidator<S> validator, final S targetObject,
            final SheetBindingErrors<?> errors, final String subPath, final int index, final Class<?>... groups) {

        ArgUtils.notNull(validator, "validator");
        ArgUtils.notNull(targetObject, "targetObject");
        ArgUtils.notNull(errors, "errors");
        ArgUtils.notMin(index, 0, "index");

        errors.pushNestedPath(subPath, index);
        try {
            validator.validate(targetObject, errors, groups);
        } finally {
            errors.popNestedPath();
        }
    }

    /**
     * マップ形式のネストしたプロパティの値の検証を実行する。
     * @param <S> ネストしたプロパティのクラスタイプ
     * @param validator ネストしたプロパティに対するValidator
     * @param targetObject ネストしたプロパティのインスタンス
     * @param errors エラー情報
     * @param subPath ネストするパス
     * @param key マップのキー。
     * @param groups バリデーション時のヒントとなるグループ。
     * @throws IllegalArgumentException {@literal validator == null or targetObject == null or errors == null or key == null}
     * @throws IllegalArgumentException {@literal key.length() == 0}
     */
    protected <S> void invokeNestedValidator(final ObjectValidator<S> validator, final S targetObject,
            final SheetBindingErrors<?> errors, final String subPath, final String key, final Class<?>... groups) {

        ArgUtils.notNull(validator, "validator");
        ArgUtils.notNull(targetObject, "targetObject");
        ArgUtils.notNull(errors, "errors");
        ArgUtils.notEmpty(key, "key");

        errors.pushNestedPath(subPath, key);
        try {
            validator.validate(targetObject, errors, groups);
        } finally {
            errors.popNestedPath();
        }
    }

}
