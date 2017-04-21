package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.mygreen.cellformatter.lang.ArgUtils;

/**
 * フィールドの値を検証するための抽象クラス。
 * <p>基本的に、{@link FieldValidator}を実装する際には、このクラスを継承して作成します。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 * @param <T> チェック対象のフィールドのタイプ
 *
 */
public abstract class AbstractFieldValidator<T> implements FieldValidator<T> {
    
    @Override
    public boolean validate(final CellField<T> cellField, final Set<Object> hints) {
        
        // 必須チェックは、CellFieldで行っているため、値が空の場合は無視する。
        if(cellField.isInputEmpty() && !validateOnEmptyValue()) {
            return true;
        }
        
        onValidate(cellField);
        
        return !cellField.hasError();
        
        
    }
    
    /**
     * 値が空のときでも検証を行うかどうか。
     * @return trueの場合、検証を行う。
     */
    protected boolean validateOnEmptyValue() {
        return false;
    }
    
    /**
     * エラー用のメッセージキーを取得します。
     * @return メッセージキー。独自に指定するような場合は、nuullを返します。
     */
    protected String getMessageKey() {
        return null;
    }
    
    /**
     * エラーメッセージ中の変数を取得します。
     * @return エラーメッセージ中の変数。
     */
    protected Map<String, Object> getMessageVariables(final CellField<T> cellField) {
        
        final Map<String, Object> variables = new HashMap<>();
        variables.put("formatter", cellField.getFormatter());
        variables.put("rejectedValue", cellField.getValue());
        return variables;
        
    }
    
    /**
     * 値の検証を行います。
     * @param cellField フィールド情報
     */
    protected abstract void onValidate(CellField<T> cellField);
    
    /**
     * エラー情報を追加します。
     * <p>エラーメッセージのキーは、{@link #getMessageKey()}の値を使用するため、必ず空以外の値を返す必要があります。</p>
     * <p>エラーメッセージ中の変数は、{@link #getMessageVariables(CellField)()}の値を使用します。</p>
     * @param cellField フィールド情報
     */
    public void error(final CellField<T> cellField) {
        ArgUtils.notNull(cellField, "cellField");
        error(cellField, getMessageKey(), getMessageVariables(cellField));
    }
    
    /**
     * エラー情報を追加します。
     * <p>エラーメッセージは、{@link #getMessageKey()}の値を使用するため、必ず空以外の値を返す必要があります。</p>
     * @param cellField フィールド情報
     * @param messageVariables メッセージ中の変数
     */
    public void error(final CellField<T> cellField, final Map<String, Object> messageVariables) {
        error(cellField, getMessageKey(), messageVariables);
    }
    
    /**
     * メッセージキーを指定して、エラー情報を追加します。
     * <p>エラーメッセージ中の変数は、{@link #getMessageVariables(CellField)()}の値を使用します。</p>
     * @param cellField フィールド情報
     * @param messageKey メッセージキー
     * @throws NullPointerException {@literal cellField == null or messageKey == null}
     * @throws IllegalArgumentException {@literal messageKey.length() == 0}
     */
    public void error(final CellField<T> cellField, final String messageKey) {
        error(cellField, messageKey, getMessageVariables(cellField));
    }
    
    /**
     * メッセージキーを指定して、エラー情報を追加します。
     * @param cellField フィールド情報
     * @param messageKey メッセージキー
     * @param messageVariables メッセージ中の変数
     * @throws NullPointerException {@literal cellField == null or messageKey == null or messageVariables == null}
     * @throws IllegalArgumentException {@literal messageKey.length() == 0}
     */
    public void error(final CellField<T> cellField, final String messageKey, final Map<String, Object> messageVariables) {
        ArgUtils.notEmpty(messageKey, "messageKey");
        ArgUtils.notNull(cellField, "cellField");
        ArgUtils.notNull(messageVariables, "messageVariables");
        
        cellField.rejectValue(messageKey, messageVariables);
        
    }
    
}
