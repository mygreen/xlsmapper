package org.mygreen.xlsmapper.validation;

import org.mygreen.xlsmapper.ArgUtils;

/**
 * オブジェクトのValidatorの抽象クラス。
 * 
 * @author T.TSUCHIE
 *
 * @param <T> チェック対象のオブジェクトのクラス
 */
public abstract class AbstractObjectValidator<T> implements ObjectValidator<T> {
    
    /**
     * 引数taretObjectで指定したValidatorを実行する。
     * @param validator
     * @param targetObject
     * @param errors
     * @param subPath ネストするパス
     */
    protected <S> void invokeNestedValidator(final ObjectValidator<S> validator, final S targetObj,
            final SheetBindingErrors errors, final String subPath) {
        ArgUtils.notNull(validator, "validator");
        ArgUtils.notNull(targetObj, "targetObject");
        ArgUtils.notNull(errors, "errors");
        
        errors.pushNestedPath(subPath);
        try {
            validator.validate(targetObj, errors);
        } finally {
            errors.popNestedPath();
        }
    }
    
    /**
     * 引数taretObjectで指定したValidatorを実行する。
     * @param validator
     * @param targetObject
     * @param errors
     * @param subPath ネストするパス
     * @param index インデックス
     */
    protected <S> void invokeNestedValidator(final ObjectValidator<S> validator, final S targetObj,
            final SheetBindingErrors errors, final String subPath, final int index) {
        ArgUtils.notNull(validator, "validator");
        ArgUtils.notNull(targetObj, "targetObject");
        ArgUtils.notNull(errors, "errors");
        
        errors.pushNestedPath(subPath, index);
        try {
            validator.validate(targetObj, errors);
        } finally {
            errors.popNestedPath();
        }
    }
    
    /**
     * 引数taretObjectで指定したValidatorを実行する。
     * @param validator
     * @param targetObject
     * @param errors
     * @param subPath ネストするパス
     * @param key インデックス
     */
    protected <S> void invokeNestedValidator(final ObjectValidator<S> validator, final S targetObj,
            final SheetBindingErrors errors, final String subPath, final String key) {
        ArgUtils.notNull(validator, "validator");
        ArgUtils.notNull(targetObj, "targetObject");
        ArgUtils.notNull(errors, "errors");
        
        errors.pushNestedPath(subPath, key);
        try {
            validator.validate(targetObj, errors);
        } finally {
            errors.popNestedPath();
        }
    }
    
}
