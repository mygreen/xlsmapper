package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;

import com.gh.mygreen.xlsmapper.Utils;


/**
 * フィールドのエラー情報を保持するクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class FieldError extends ObjectError {
    
    private final String fieldPath;
    
    private Object fieldValue;
    
    private Class<?> fieldType;
    
    /** 型のConversionエラーの場合、値をtrueに設定する。 */
    private boolean typeBindFailure;
    
    public FieldError(final String objectName, final String fieldPath) {
        super(objectName);
        this.fieldPath = fieldPath;
    }
    
    public FieldError(final String objectName, final String fieldPath,
            final String[] codes, final Object[] args) {
        super(objectName, codes, args);
        this.fieldPath = fieldPath;
    }
    
    public FieldError(final String objectName, final String fieldPath,
            final String[] codes, final Map<String, ?> vars) {
        super(objectName, codes, vars);
        this.fieldPath = fieldPath;
    }
    
    public FieldError(final String objectName, final String fieldPath,
            final Class<?> fieldType, final Object fieldValue,
            final boolean typeBindFailure,
            final String[] codes, final Object[] args) {
        this(objectName, fieldPath, codes, args);
        this.fieldType = fieldType;
        this.fieldValue = fieldValue;
        this.typeBindFailure = typeBindFailure;
    }
    
    public FieldError(final String objectName, final String fieldPath,
            final Class<?> fieldType, final Object fieldValue,
            final boolean typeBindFailure,
            final String[] codes, final Map<String, ?> vars) {
        this(objectName, fieldPath, codes, vars);
        this.fieldType = fieldType;
        this.fieldValue = fieldValue;
        this.typeBindFailure = typeBindFailure;
    }
    
    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        
        msg.append(String.format("Fail conversion field '%s'.", getFieldPath()));
        
        if(isTypeBindFailure()) {
            msg.append(String.format("field value '%s' => type '%s'.",
                    Utils.convertToString(getFieldValue()), fieldType.getName()));
        }
        
        return msg.toString();
    }
    
    public String getFieldPath() {
        return fieldPath;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
    
    public Class<?> getFieldType() {
        return fieldType;
    }
    
    public boolean isTypeBindFailure() {
        return typeBindFailure;
    }
    
    @Override
    public FieldError setDefaultMessage(String defaultMessage) {
        super.setDefaultMessage(defaultMessage);
        return this;
    }
}
