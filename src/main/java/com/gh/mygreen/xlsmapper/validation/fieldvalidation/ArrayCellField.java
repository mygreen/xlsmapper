package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * 配列や{@link Collection}型に対する入力値チェックをするためのクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayCellField<E> extends CellField<E> {

    /**
     * 要素が必須かどうか
     */
    private boolean requiredElement;

    /**
     * 配列のフィールドに対するValidator
     */
    private List<ArrayFieldValidator<E>> arrayValidators;

    /**
     * 指定されたフィールドの名称に対応するオブジェクトを構築します。
     *
     * @param fieldName フィールド名
     * @param errors エラー情報
     */
    public ArrayCellField(String fieldName, SheetBindingErrors<?> errors) {
        super(fieldName, errors);

        this.requiredElement = false;
        this.arrayValidators = new ArrayList<>();
    }

    /**
     * 要素の値が必須かの設定を行う。
     * @param requiredElement 必須チェックを行いたい場合、「true」を設定する。
     * @return 自身のインスタンス。メソッドチェーンで記述する。
     */
    public ArrayCellField<E> setRequiredElement(final boolean requiredElement) {
        this.requiredElement = requiredElement;
        return this;
    }

    /**
     * 要素の値が必須かチェックを行うかどうか。
     * @return true: 必須入力チェックを行う。
     *               初期値は、非必須（オプション）です。
     */
    public boolean isRequiredElement() {
        return requiredElement;
    }

    /**
     * フィールドの値をリストに変換して取得する。
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<E> getValueAsList() {

        final Object fieldValue = getValue();
        final Class<?> fieldType = getType();

        if(Collection.class.isAssignableFrom(fieldType)) {
            final Collection<Object> value = (fieldValue == null ? new ArrayList<Object>() : (Collection<Object>) fieldValue);
            final List<Object> list = Utils.convertCollectionToList(value);
            return (List)list;

        } else if(fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            final List<Object> list = Utils.asList(fieldValue, componentType);
            return (List)list;

        } else {
            throw new IllegalStateException(MessageBuilder.create("validation.notSupportType")
                    .var("property", getFieldPath())
                    .varWithClass("type", fieldType)
                    .format());
        }

    }

    /**
     * フィールドの値が空かどうか。
     * <p>値がnullまたは、文字列の場合空文字のとき、空と判定する。
     *  <br>配列や{@link Collection}型のときは、要素の数が0のとき空と判定する。
     * </p>
     * @return trueの場合、値は空。
     */
    @SuppressWarnings("rawtypes")
    public boolean isInputEmpty() {

        final Object fieldValue = getValue();
        final Class<?> fieldType = getType();

        if(fieldValue == null) {
            return true;

        } else if(Collection.class.isAssignableFrom(fieldType) && ((Collection) fieldValue).isEmpty()) {
            return true;

        } else if(fieldType.isArray()) {

            Class<?> componentType = fieldType.getComponentType();
            int length = Utils.getArraySize(fieldValue, componentType);
            if(length > 0) {
                return false;
            }

            return true;

        }

        return super.isInputEmpty();
    }

    @Override
    public ArrayCellField<E> validate(final Class<?>... groups) {

        // 既に型変換エラーなどがある場合、値が設定されていないため処理を終了します。
        if(hasErrors()) {
            return this;
        }

        // 必須チェック
        if(!validateForRequired()) {
            return this;
        }

        final List<Class<?>> hints = Arrays.asList(groups);

        // 配列用のValidatorの実行
        if(getArrayValidators() != null && !getArrayValidators().isEmpty()) {
            for(ArrayFieldValidator<E> validator : getArrayValidators()) {
                if(!validator.validate(this, hints)) {
                    return this;
                }
            }
        }

        // 各要素に対するValidatorの実行
        int size = getValueAsList().size();
        if(size > 0) {
            for(int i=0; i < size; i++) {
                final String elementName = String.format("%s[%d]", getFieldName(), i);

                CellField<E> elementField = new CellField<>(elementName, getBindingErrors());
                elementField.setRequired(isRequiredElement())
                    .add(getValidators())
                    .validate(groups);
            }

        }

        return this;
    }

    /**
     * {@link ArrayFieldValidator} を追加する。
     * @param validator validatorのインスタンス。
     * @return 自身のインスタンス。
     * @throws NullPointerException validator is null.
     */
    public ArrayCellField<E> add(final ArrayFieldValidator<E> validator) {
        ArgUtils.notNull(validator, "validator");
        this.arrayValidators.add(validator);

        return this;
    }

    /**
     * 現在の{@link ArrayFieldValidator}を取得する。
     * @return 現在設定されている{@link ArrayFieldValidator}。
     */
    public List<ArrayFieldValidator<E>> getArrayValidators() {
        return arrayValidators;
    }

}
