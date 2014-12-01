package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * 何もしないフィールドValidator.
 * <p>CellFiledValidatorを組み立てる際に、ロジック上何か設定すべきときに利用する。
 * 
 * @since 0.2.2
 * @author T.TSUCHIE
 *
 */
public class DefaultFieldValidator<T> implements FieldValidator<T> {
    
    @Override
    public boolean validate(final String fieldName, final T value, final SheetBindingErrors errors) {
        return true;
    }
    
}
