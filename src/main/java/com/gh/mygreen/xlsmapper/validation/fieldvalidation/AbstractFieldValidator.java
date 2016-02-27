package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * フィールドValidatorの抽象クラス。
 * @author T.TSUCHIE
 * @param <T> チェック対象のフィールドのタイプ
 *
 */
public abstract class AbstractFieldValidator<T> implements FieldValidator<T>, CellFieldValidator<T> {
    
    /** カスタムメッセージのキー */
    protected String customMessageKey;
    
    public AbstractFieldValidator() {
    
    }
    
    /**
     * 値がnullかどうか判定を行う。
     * <p>値が空文字も同様。
     * @param value
     * @return
     */
    public boolean isNullValue(final T value) {
        return (value == null || value.toString().isEmpty());
    }
    
    /**
     * 値がnull出ないかどうか判定を行う。
     * @return
     */
    public boolean isNotNullValue(T value) {
        return !isNullValue(value);
    }
    
    /**
     * 標準の入力値エラー時のメッセージキーを取得する。
     * @return
     */
    public String getMessageKey() {
        if(Utils.isEmpty(getCustomMessageKey())) {
            return getDefaultMessageKey();
        }
        return getCustomMessageKey();
    }
    
    /**
     * 通常のメッセージキーの取得
     */
    public abstract String getDefaultMessageKey();
    
    /**
     * 任意のメッセージキーの取得
     * @return
     */
    public String getCustomMessageKey() {
        return customMessageKey;
    }
    
    /**
     * 任意のメッセージキーの設定
     * @param customMessageKey
     * @return
     */
    public AbstractFieldValidator<T> setCustomMessageKey(String customMessageKey) {
        this.customMessageKey = customMessageKey;
        return this;
    }
    
    @Override
    public boolean validate(final String fieldName, final T value, final SheetBindingErrors errors) {
        
        if(validate(value)) {
            return true;
        }
        
        errors.rejectValue(fieldName, value, value.getClass(), getMessageKey(), getMessageVars(value));
        
        return false;
    }
    
    @Override
    public boolean validate(final String fieldName, final T value, final Point cellAddress, final SheetBindingErrors errors) {
        
        if(validate(value)) {
            return true;
        }
        
        final Map<String, Object> messageVars = getMessageVars(value);
        errors.rejectSheetValue(fieldName, value, value.getClass(), cellAddress, getMessageKey(), messageVars);
        
        return false;
    }
    
    /**
     * 入力値の検証を行う。
     * @param value 検証対象の値
     * @return
     */
    protected abstract boolean validate(T value);
    
    /**
     * メッセージ中の変数を取得する。
     * @param value 検証対象の値
     * @return メッセージ中の変数のマップ。
     */
    protected abstract LinkedHashMap<String, Object> getMessageVars(T value);
    
}
