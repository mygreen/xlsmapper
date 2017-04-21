package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.Set;


/**
 * 何もしないフィールドバリデータ。.
 * <p>{@link FieldValidator}を組み立てる際に、ロジック上何か設定すべきときに利用します。</p>
 * 
 * @since 0.2.2
 * @author T.TSUCHIE
 *
 */
public class DefaultFieldValidator<T> implements FieldValidator<T> {
    
    @Override
    public boolean validate(final CellField<T> cellField, final Set<Object> hints) {
        return true;
    }
    
}
