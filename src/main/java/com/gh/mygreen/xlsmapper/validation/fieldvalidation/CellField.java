package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.fieldaccessor.LabelGetterFactory;
import com.gh.mygreen.xlsmapper.fieldaccessor.PositionGetterFactory;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.FieldErrorBuilder;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * 1つの項目（フィールド）に対する入力値チェックをするためのクラス。
 * <p>型変換などにおけるバインドエラーなどはシートの読み込み時に行われます。
 * <p>必須エラーのメッセージキーは、「fieldError.required」。
 *
 * @version 0.5
 * @author T.TSUCHIE
 * @param <T> チェック対象の値のタイプ
 *
 */
public class CellField<T> {

    private static final PositionGetterFactory positionGetterFactory = new PositionGetterFactory();
    private static final LabelGetterFactory labelGetterFactory = new LabelGetterFactory();

    private final SheetBindingErrors<?> errors;

    /**
     * フィールドの名称
     */
    private final String fieldName;

    /**
     * フィールドが定義されているBeanクラスのインスタンス
     */
    private Object beanObj;

    /**
     * フィールドの値
     */
    private T fieldValue;

    /**
     * フィールドのJavaBean上のパス
     */
    private String fieldPath;

    /**
     * セルの位置情報。
     * Beanに定義されたプロパティ情報から取得する。
     * 定義されていない場合は、nullが設定される。
     */
    private CellPosition position;

    /**
     * セルのラベル情報
     * Beanに定義されたプロパティ情報から取得する。
     * 定義されていない場合は、nullが設定される。
     */
    private String label;

    /**
     * 必須かどうか
     */
    private boolean required;

    private List<FieldValidator<T>> validators;

    private FieldFormatter<T> formatter;

    private Class<T> fieldType;

    /**
     * 指定されたフィールドの名称に対応するオブジェクトを構築します。
     * @param fieldName フィールドの名称。現在のBeanに対するフィールドの相対パスを指定します。
     * @param errors エラー情報
     */
    public CellField(final String fieldName, final SheetBindingErrors<?> errors) {

        ArgUtils.notEmpty(fieldName, "fieldName");
        ArgUtils.notNull(errors, "errors");

        this.fieldName = fieldName;
        this.errors = errors;

        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {

        this.beanObj = errors.getValue();
        this.fieldValue = (T)errors.getFieldValue(fieldName);
        this.fieldType = (Class<T>)errors.getFieldType(fieldName);
        this.fieldPath = errors.buildFieldPath(fieldName);

        Optional<CellPosition> position = positionGetterFactory.create(beanObj.getClass(), fieldName)
                .map(getter -> getter.get(beanObj)).orElse(Optional.empty());
        position.ifPresent(p -> setPosition(p));

        Optional<String> label = labelGetterFactory.create(beanObj.getClass(), fieldName)
                .map(getter -> getter.get(beanObj)).orElse(Optional.empty());
        label.ifPresent(l -> setLabel(l));

        this.required = false;
        this.validators = new ArrayList<>();

        this.formatter = errors.findFieldFormatter(fieldName, fieldType);
    }

    /**
     * 値が必須かの設定を行う。
     * @param required 必須チェックを行いたい場合、「true」を設定する。
     * @return 自身のインスタンス。メソッドチェーンで記述する。
     */
    public CellField<T> setRequired(final boolean required) {
        this.required = required;
        return this;
    }

    /**
     * 値が必須かチェックを行うかどうか。
     * @return true: 必須入力チェックを行う。
     *               初期値は、非必須（オプション）です。
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * {@link FieldValidator} を追加する。
     * @param validator validatorのインスタンス。
     * @return 自身のインスタンス。
     * @throws NullPointerException validator is null.
     */
    public CellField<T> add(final FieldValidator<T> validator) {
        ArgUtils.notNull(validator, "validator");
        this.validators.add(validator);

        return this;
    }

    /**
     * 複数の{@link FieldValidator} を追加する。
     * @param validators 複数のvalidatorのインスタンス。
     * @return 自身のインスタンス。
     * @throws NullPointerException validator is null.
     */
    public CellField<T> add(final List<FieldValidator<T>> validators) {

        if(validators.isEmpty()) {
            return this;
        }

        for(FieldValidator<T> validator : validators) {
            add(validator);
        }
        return this;
    }

    /**
     * 現在の{@link FieldValidator}を取得する。
     * @return 現在設定されている{@link FieldValidator}。
     */
    public List<FieldValidator<T>> getValidators() {
        return validators;
    }

    /**
     * グループなどのヒントを指定して、入力値の検証を行う。
     * <p>判定結果は、{@link #hasErrors()}で確認します。</p>
     * <p>型変換エラーなどが既に存在するときには、処理は終了します。</p>
     *
     * @param groups 検証するときのヒントとなるグループ。
     * @return 自身のインスタンス。
     */
    public CellField<T> validate(final Class<?>... groups) {

        // 既に型変換エラーなどがある場合、値が設定されていないため処理を終了します。
        if(hasErrors()) {
            return this;
        }

        // 必須チェック
        if(!validateForRequired()) {
            return this;
        }

        final List<Class<?>> hints = Arrays.asList(groups);

        if(getValidators() != null && !getValidators().isEmpty()) {
            for(FieldValidator<T> validator : getValidators()) {
                if(!validator.validate(this, hints)) {
                    return this;
                }
            }
        }

        return this;

    }

    /**
     * 必須エラーのメッセージキーを取得する。
     * <p>キー名は、「fieldError.required」。
     * @return
     */
    protected String getMessageKeyRequired() {
        return "cellFieldError.required";
    }

    /**
     * 必須チェックを行う。
     * @return trueの場合、必須エラーでない。
     */
    protected boolean validateForRequired() {

        if(isRequired() && isInputEmpty()) {
            errors.createFieldError(fieldName, getMessageKeyRequired())
                .address(getPosition())
                .label(getLabel())
                .variables("validatedValue", getValue())
                .buildAndAddError();
            return false;
        }

        return true;

    }

    /**
     * エラーを追加する。
     * @param errorCode エラーコード
     */
    public void rejectValue(final String errorCode) {
        rejectValue(errorCode, Collections.emptyMap());
    }

    /**
     * エラーを追加する
     * @param errorCode エラコード
     * @param variables エラーメッセージ中の変数
     */
    public void rejectValue(final String errorCode, final Map<String, Object> variables) {

        final String codes[] = errors.generateMessageCodes(errorCode, fieldPath, fieldType);

        final FieldError error = new FieldErrorBuilder(errors.getObjectName(), fieldPath, codes)
            .sheetName(errors.getSheetName())
            .rejectedValue(fieldValue)
            .variables(variables)
            .address(position)
            .label(label)
            .build();

        errors.addError(error);
    }

    /**
     * フィールドの値が空かどうか。
     * <p>値がnullまたは、文字列の場合空文字のとき、空と判定する。
     * @return trueの場合、値は空。
     */
    public boolean isInputEmpty() {
        if(fieldValue == null || fieldValue.toString().isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * フィールドに対してエラーが存在するかどうか。
     * @return trueの場合、エラーが存在する。
     */
    public boolean hasErrors() {
        return errors.hasFieldErrors(fieldName);
    }

    /**
     * フィールドに対してエラーが存在しなかどうか。
     * @return trueの場合、エラーが存在しない。
     */
    public boolean hasNotErrors() {
        return !hasErrors();
    }

    /**
     * エラー情報を取得する。
     * @return エラー情報
     */
    public SheetBindingErrors<?> getBindingErrors() {
        return errors;
    }

    /**
     * フィールドの名称を取得する
     * @return フィールドの名称
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * フィールドの値を取得する。
     * @return フィールドの値。
     */
    public T getValue() {
        return fieldValue;
    }

    /**
     * フィールドのクラスタイプを取得する。
     * @return クラスタイプ
     */
    public Class<T> getType() {
        return fieldType;
    }

    /**
     * フィールドのJavaBean上のパスを取得する。
     * @return フィールドのJavaBean上のパス
     */
    public String getFieldPath() {
        return fieldPath;
    }

    /**
     * セルの位置情報を取得します。
     * <p>位置情報の取得用のフィールドやメソッドがbeanに定義されている場合は、コンストラクタの呼び出し時に設定されています。</p>
     * @return 自身のインスタンス。
     */
    public CellPosition getPosition() {
        return position;
    }

    /**
     * セルの位置情報を設定します。
     * @param position セルの位置情報
     */
    public void setPosition(CellPosition position) {
        this.position = position;
    }

    /**
     * セルのラベル情報を取得します。
     * <p>ラベル情報の取得用のフィールドやメソッドがbeanに定義されている場合は、コンストラクタの呼び出し時に設定されています。</p>
     * @return 自身のインスタンス。
     */
    public String getLabel() {
        return label;
    }

    /**
     * セルのラベル情報を設定します。
     * @param label セルのラベル情報
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * フォーマッタを取得する。
     * @return フォーマッタ。
     *         デフォルトでは、フィールドのクラスタイプ、付与されたアノテーションを元にしたもの。
     *
     */
    public FieldFormatter<T> getFormatter() {
        return formatter;
    }

    /**
     * フォーマッタを設定する。
     * @param formatter
     */
    public void setFormatter(FieldFormatter<T> formatter) {
        this.formatter = formatter;
    }
}
