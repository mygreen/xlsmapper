package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.PropertyNavigator;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.validation.FieldError;
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
    
    /**
     * プロパティにアクセスするための式言語。
     * ・OGNLを利用し、private/protectedなどのフィールドにもアクセス可能にする。
     */
    private static final PropertyNavigator propertyNavigator = new PropertyNavigator();
    static {
        propertyNavigator.setAllowPrivate(true);
        propertyNavigator.setIgnoreNull(true);
        propertyNavigator.setIgnoreNotFoundKey(true);
        propertyNavigator.setCacheWithPath(true);
    }
    
    /** フィールド名（チェック対象のプロパティ名） */
    final private String name;
    
    /** チェック対象の値 */
    private T value;
    
    /** セルのアドレス */
    private Point cellAddress;
    
    /** 必須かどうか */
    private boolean required;
    
    /** ラベル */
    private String label;
    
    /** フィールドValidator */
    private List<FieldValidator<T>> validators;
    
    public CellField(final String name) {
        this(name, (T)null);
    }
    
    /**
     * フィールド名とその値を指定するコンストラクタ
     * @param fieldName フィールドの名称
     * @param fieldValue フィールドの値
     * @throws IllegalArgumentException fieledName is empty.
     */
    public CellField(final String fieldName, final T fieldValue) {
        ArgUtils.notEmpty(fieldName, "fieldName");
        this.name = fieldName;
        setValue(fieldValue);
        this.validators = new ArrayList<FieldValidator<T>>();
    }
    
    /**
     * Commandから指定したフィールド名（プロパティ名）の値をBeanWrapperにて自動的に取得するコンストラクタ。
     * @param targetObj Commandのインスタンス。
     * @param fieldName Commandにおけるプロパティ名。
     * @throws IllegalArgumentException commandObj is null.
     * @throws IllegalArgumentException fieldName is empty.
     */
    public CellField(final Object targetObj, final String fieldName) {
        ArgUtils.notNull(targetObj, "commandObj");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        this.name = fieldName;
        @SuppressWarnings("unchecked")
        final T fieldValue = (T) propertyNavigator.getProperty(targetObj, fieldName);
        setValue(fieldValue);
        
        setCellAddress(Utils.getPosition(targetObj, fieldName));
        setLabel(Utils.getLabel(targetObj, fieldName));
        
        this.validators = new ArrayList<FieldValidator<T>>();
    }
    
    /**
     * {@link FieldValidator} を追加する。
     * @param validator validatorのインスタンス。
     * @return 自身のインスタンス。
     * @throws IllegalArgumentException validator is null.
     */
    public CellField<T> add(final FieldValidator<T> validator) {
        ArgUtils.notNull(validator, "validator");
        validators.add(validator);
        
        return this;
    }
    
    /**
     * 現在のValidatorを取得する。
     * @return
     */
    public List<FieldValidator<T>> getValidators() {
        return validators;
    }
    
    /**
     * 入力値チェックを行う。
     * <p>既に、引数のerrorsの中に自身に関するエラーがある場合は無視する。
     * <p>チェック順は、(1)必須チェック、(2)追加したFieldValidatorの順。
     * 
     * @param errors エラーオブジェクト。
     * @throws IllegalArgumentException errors is null.
     */
    public CellField<T> validate(final SheetBindingErrors errors) {
        ArgUtils.notNull(errors, "errors");
        
        if(hasErrors(errors)) {
            // 既にフィールドに対するエラーがある場合
            return this;
        }
        
        if(!validateAsRequired(errors)) {
            // 必須エラーの場合
            appendSheetInfo(errors);
            return this;
        }
        
        if(getValidators() != null && !getValidators().isEmpty()) {
            // 各種入力値チェックを行う。
            for(FieldValidator<T> validator : getValidators()) {
                
                if(!invokeValidate(validator, errors)) {
                    // エラーがある場合
                    break;
                }
            }
        }
        
        appendSheetInfo(errors);
        return this;
    }
    
    /**
     * 現在のエラーにシート情報を補完する
     * @param errors
     */
    protected void appendSheetInfo(final SheetBindingErrors errors) {
        
        for(FieldError error : errors.getFieldErrors(getName())) {
            error.setLabel(getLabel());
        }
    }
    
    /**
     * 指定したFieldValidatorを実行し、値を検証する。
     * @param validator 
     * @param errors
     * @return
     */
    protected boolean invokeValidate(final FieldValidator<T> validator, final SheetBindingErrors errors) {
        
        if(validator instanceof CellFieldValidator && getCellAddress() != null) {
            CellFieldValidator<T> sheetValidator = (CellFieldValidator<T>) validator;
            return sheetValidator.validate(getName(), getValue(), getCellAddress(), errors);
        } else {
            return validator.validate(getName(), getValue(), errors);
        }
        
    }
    
    /**
     * 必須チェックを行う。
     * 
     * @param errors
     * @return true:エラーがない場合。既にエラーがある場合。
     */
    protected boolean validateAsRequired(final SheetBindingErrors errors) {
        
        if(isInputEmpty()) {
            // 必須エラーチェックを行う場合
            if(isRequired()) {
                Map<String, Object> vars = new LinkedHashMap<>();
                vars.put("validatedValue", getValue());
                errors.rejectSheetValue(getName(), getCellAddress(), getMessageKeyRequired(), vars);
                return false;
            }
            
            return true;
        }
        
        // エラーがない場合
        return true;
        
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
     * フィールドの値が空 or nullかどうか。
     * @return
     */
    public boolean isInputEmpty() {
        return (getValue() == null || getValue().toString().isEmpty());
    }
    
    /**
     * フィールドの値が空 or null出ない場合。
     * @return
     */
    public boolean isNotInputEmpty() {
        return !isInputEmpty();
    }
    
    /**
     * 検証対象の値を設定する。
     * @param value 検証対象の値。
     * @return
     */
    protected CellField<T> setValue(final T value) {
        this.value = value;
        return this;
    }
    
    /**
     * 検証対象の値を取得する。
     * @return
     */
    public T getValue() {
        return value;
    }
    
    /**
     * フィールドの名前を取得する。
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * 値が必須かの設定を行う。
     * @param required 必須チェックを行いたい場合、「true」を設定する。
     * @return
     */
    public CellField<T> setRequired(final boolean required) {
        this.required = required;
        return this;
    }
    
    /**
     * 値が必須かチェックを行うかどうか。
     * @return true: 必須入力チェックを行う。
     */
    public boolean isRequired() {
        return required;
    }
    
    /**
     * 自身のフィールドに対してフィールドエラーを持つかどうか。
     * <p>{@link Errors#hasFieldErrors(String)}を呼び出す。
     * @param errors Springのエラーオブジェクト。
     * @return true:エラーを持つ場合。
     * @throws IllegalArgumentException errors == null.
     */
    public boolean hasErrors(final SheetBindingErrors errors) {
        ArgUtils.notNull(errors, "errors");
        return errors.hasFieldErrors(getName());
    }
    
    /**
     * 自身のフィールドに対してエラーを持たないかどうか
     * @param errors
     * @return
     */
    public boolean hasNotErrors(SheetBindingErrors errors) {
        return !hasErrors(errors);
    }
    
    public Point getCellAddress() {
        return cellAddress;
    }
    
    public CellField<T> setCellAddress(Point cellAddress) {
        this.cellAddress = cellAddress;
        return this;
    }
    
    public String getLabel() {
        return label;
    }
    
    public CellField<T> setLabel(String label) {
        this.label = label;
        return this;
    }
    
}
