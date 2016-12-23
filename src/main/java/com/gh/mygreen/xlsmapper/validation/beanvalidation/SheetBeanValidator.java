package com.gh.mygreen.xlsmapper.validation.beanvalidation;

import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.path.PathImpl;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.FieldErrorBuilder;
import com.gh.mygreen.xlsmapper.validation.ObjectValidator;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * BeanValidaion JSR-303(ver.1.0)/JSR-349(ver.1.1)を利用したValidator.
 * @author T.TSUCHIE
 *
 */
public class SheetBeanValidator implements ObjectValidator<Object> {
    
    /**
     * BeanValidationのアノテーションの属性で、メッセージ中の変数から除外するもの。
     * <p>メッセージの再構築を行う際に必要
     */
    private static final Set<String> EXCLUDE_MESSAGE_ANNOTATION_ATTRIBUTES;
    static {
        Set<String> set = new HashSet<String>(3);
        set.add("message");
        set.add("groups");
        set.add("payload");
        
        EXCLUDE_MESSAGE_ANNOTATION_ATTRIBUTES = Collections.unmodifiableSet(set);
    }
    
    private final Validator targetValidator;
    
    public SheetBeanValidator(final Validator targetValidator) {
        ArgUtils.notNull(targetValidator, "targetValidator");
        this.targetValidator = targetValidator;
    }
    
    public SheetBeanValidator() {
        this.targetValidator = createDefaultValidator();
    }
    
    /**
     * Bean Validatorのデフォルトのインスタンスを取得する。
     * @return
     */
    protected Validator createDefaultValidator() {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();
        return validator;
    }
    
    /**
     * BeanValidationのValidatorを取得する。
     * @return
     */
    public Validator getTargetValidator() {
        return targetValidator;
    }
    
    @Override
    public void validate(final Object targetObj, final SheetBindingErrors errors) {
        validate(targetObj, errors, new Class<?>[0]);
        
    }
    
    /**
     * グループを指定して検証を実行する。
     * @param targetObj 検証対象のオブジェクト。
     * @param errors エラーオブジェクト
     * @param groups BeanValiationのグループのクラス
     */
    public void validate(final Object targetObj, final SheetBindingErrors errors, final Class<?>... groups) {
        ArgUtils.notNull(targetObj, "targetObj");
        ArgUtils.notNull(errors, "errors");
        
        processConstraintViolation(getTargetValidator().validate(targetObj, groups), errors);
    }
    
    /**
     * BeanValidationの検証結果をSheet用のエラーに変換する
     * @param violations BeanValidationの検証結果
     * @param errors シートのエラー
     */
    protected void processConstraintViolation(final Set<ConstraintViolation<Object>> violations,
            final SheetBindingErrors errors) {
        
        for(ConstraintViolation<Object> violation : violations) {
            
            final String fieldName = violation.getPropertyPath().toString();
            final FieldError fieldError = errors.getFirstFieldError(fieldName);
            
            if(fieldError != null && fieldError.isTypeBindFailure()) {
                // 型変換エラーが既存のエラーにある場合は、処理をスキップする。
                continue;
            }
            
            final ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
            final String errorCode = cd.getAnnotation().annotationType().getSimpleName();
            final Map<String, Object> errorVars = createVariableForConstraint(cd);
            
            final String nestedPath = errors.buildFieldPath(fieldName);
            if(Utils.isEmpty(nestedPath)) {
                // オブジェクトエラーの場合
                errors.rejectSheet(errorCode, errorVars, violation.getMessage());
                
            } else {
                // フィールドエラーの場合
                
                // 親のオブジェクトから、セルの座標を取得する
                final Object parentObj = violation.getLeafBean();
                final Path path = violation.getPropertyPath();
                Point cellAddress = null;
                String label = null;
                if(path instanceof PathImpl) {
                    final PathImpl pathImpl = (PathImpl) path;
                    cellAddress = Utils.getPosition(parentObj, pathImpl.getLeafNode().getName());
                    label = Utils.getLabel(parentObj, pathImpl.getLeafNode().getName());
                }
                
                // 実際の値を取得する
                final Object fieldValue = violation.getInvalidValue();
                if(!errorVars.containsKey("validatedValue")) {
                    errorVars.put("validatedValue", fieldValue);
                }
                
                Class<?> fieldType = fieldValue != null ? fieldValue.getClass() : null;
                
                errors.addError(FieldErrorBuilder.create()
                        .objectName(errors.getObjectName()).fieldPath(errors.buildFieldPath(fieldName))
                        .codes(errors.generateMessageCodes(errorCode, fieldName, fieldType), errorVars)
                        .sheetName(errors.getSheetName()).cellAddress(cellAddress)
                        .label(label)
                        .defaultMessage(violation.getMessage())
                        .fieldValue(fieldValue)
                        .build());
                
            }
            
        }
        
    }
    
    /**
     * BeanValidationのアノテーションの値を元に、メッセージ変数を作成する。
     * @param descriptor
     * @return メッセージ変数
     */
    protected Map<String, Object> createVariableForConstraint(final ConstraintDescriptor<?> descriptor) {
        
        final Map<String, Object> vars = new LinkedHashMap<String, Object>();
        
        for(Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attrName = entry.getKey();
            final Object attrValue = entry.getValue();
            
            // メッセージ変数で必要ないものを除外する
            if(EXCLUDE_MESSAGE_ANNOTATION_ATTRIBUTES.contains(attrName)) {
                continue;
            }
            
            vars.put(attrName, attrValue);
        }
        
        return vars;
        
    }
    
}
