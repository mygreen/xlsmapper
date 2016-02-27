package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * メッセージのフォーマット可能なフィールドValidatorのインタフェース。
 * 
 * @author T.TSUCHIE
 *
 */
public interface FieldValidator<T> {
    
    /**
     * フィールドの入力値チェックを行う。
     * 
     * @param fieldName フィールド名
     * @param value フィールドの値
     * @param errors 入力値チェックした結果
     * @return true: チェックを実行した結果、エラーがない場合。
     */
    public boolean validate(String fieldName, T value, SheetBindingErrors errors);
    
}
