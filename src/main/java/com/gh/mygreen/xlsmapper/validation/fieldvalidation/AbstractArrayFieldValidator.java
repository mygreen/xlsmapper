package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * フィールドの値を検証するための抽象クラス。
 * <p>基本的に、{@link ArrayFieldValidator}を実装する際には、このクラスを継承して作成します。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 * @param <E> チェック対象のフィールドの配列や{@link Collection}の要素のクラスタイプ
 */
public abstract class AbstractArrayFieldValidator<E> extends GroupValidatorSupport implements ArrayFieldValidator<E> {

    @Override
    public boolean validate(final ArrayCellField<E> cellField, final List<Class<?>> validationGroups) {

        // 必須チェックは、ArrayCellFieldで行っているため、値が空の場合は無視する。
        if(cellField.isInputEmpty() && !validateOnEmptyValue()) {
            return true;
        }

        if(!containsValidationGroups(validationGroups)) {
            // バリデーション時のヒントが該当しない場合は、スキップする。
            return true;
        }

        onValidate(cellField);

        return !cellField.hasErrors();


    }

    /**
     * バリデーション時のヒントを追加する。
     * @param groups バリデーション時のヒント。
     * @return 自身のインスタンス。
     */
    public AbstractArrayFieldValidator<E> addGroup(final Class<?>... groups) {

        super.addGroup(groups);
        return this;
    }

    /**
     * 値の検証を行います。
     * @param cellField フィールド情報
     */
    protected abstract void onValidate(ArrayCellField<E> cellField);

    /**
     * エラーメッセージ中の変数を取得します。
     * @return エラーメッセージ中の変数。
     */
    protected Map<String, Object> getMessageVariables(final ArrayCellField<E> cellField) {

        final Map<String, Object> variables = new HashMap<>();
        variables.put("validatedValue", cellField.getValue());
        Optional.ofNullable(cellField.getFormatter())
            .ifPresent(f -> variables.put("fieldFormatter", f));
        return variables;

    }

    /**
     * エラー情報を追加します。
     * <p>エラーメッセージのキーは、{@link #getMessageKey()}の値を使用するため、必ず空以外の値を返す必要があります。</p>
     * <p>エラーメッセージ中の変数は、{@link #getMessageVariables(ArrayCellField)}の値を使用します。</p>
     * @param cellField フィールド情報
     */
    public void error(final ArrayCellField<E> cellField) {
        ArgUtils.notNull(cellField, "cellField");
        error(cellField, getMessageKey(), getMessageVariables(cellField));
    }

    /**
     * エラー情報を追加します。
     * <p>エラーメッセージは、{@link #getMessageKey()}の値を使用するため、必ず空以外の値を返す必要があります。</p>
     * @param cellField フィールド情報
     * @param messageVariables メッセージ中の変数
     */
    public void error(final ArrayCellField<E> cellField, final Map<String, Object> messageVariables) {
        error(cellField, getMessageKey(), messageVariables);
    }

    /**
     * メッセージキーを指定して、エラー情報を追加します。
     * <p>エラーメッセージ中の変数は、{@link #getMessageVariables(ArrayCellField)}の値を使用します。</p>
     * @param cellField フィールド情報
     * @param messageKey メッセージキー
     * @throws IllegalArgumentException {@literal cellField == null or messageKey == null}
     * @throws IllegalArgumentException {@literal messageKey.length() == 0}
     */
    public void error(final ArrayCellField<E> cellField, final String messageKey) {
        error(cellField, messageKey, getMessageVariables(cellField));
    }

    /**
     * メッセージキーを指定して、エラー情報を追加します。
     * @param cellField フィールド情報
     * @param messageKey メッセージキー
     * @param messageVariables メッセージ中の変数
     * @throws IllegalArgumentException {@literal cellField == null or messageKey == null or messageVariables == null}
     * @throws IllegalArgumentException {@literal messageKey.length() == 0}
     */
    public void error(final ArrayCellField<E> cellField, final String messageKey, final Map<String, Object> messageVariables) {
        ArgUtils.notEmpty(messageKey, "messageKey");
        ArgUtils.notNull(cellField, "cellField");
        ArgUtils.notNull(messageVariables, "messageVariables");

        cellField.rejectValue(messageKey, messageVariables);

    }
}
