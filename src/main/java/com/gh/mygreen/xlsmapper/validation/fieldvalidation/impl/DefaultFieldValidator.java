package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.List;

import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldValidator;


/**
 * 何もしないフィールドバリデータ。.
 * <p>{@link FieldValidator}を組み立てる際に、ロジック上何か設定したいときに利用します。</p>
 * 
 * @since 0.2.2
 * @author T.TSUCHIE
 *
 */
public class DefaultFieldValidator<T> implements FieldValidator<T> {
    
    @Override
    public boolean validate(final CellField<T> cellField, final List<Class<?>> groups) {
        return true;
    }
    
}
