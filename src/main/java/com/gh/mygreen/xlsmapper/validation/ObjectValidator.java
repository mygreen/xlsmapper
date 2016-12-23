package com.gh.mygreen.xlsmapper.validation;



/**
 * Validatorのインタフェース
 * @version 1.0
 * @since 1.0
 * @param <T> チェック対象のBeanのクラスタイプ
 * @author T.TSUCHIE
 *
 */
public interface ObjectValidator<T> {
    
    void validate(T targetObj, SheetBindingErrors errors);
}
