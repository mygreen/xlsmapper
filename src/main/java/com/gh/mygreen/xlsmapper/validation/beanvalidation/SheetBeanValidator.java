package com.gh.mygreen.xlsmapper.validation.beanvalidation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.fieldaccessor.LabelGetterFactory;
import com.gh.mygreen.xlsmapper.fieldaccessor.PositionGetterFactory;
import com.gh.mygreen.xlsmapper.localization.MessageInterpolator;
import com.gh.mygreen.xlsmapper.localization.ResourceBundleMessageResolver;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.ObjectValidator;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatter;


/**
 * Bean Validaion JSR-303(ver.1.0)/JSR-349(ver.1.1)/JSR-380(ver.2.0)を利用したValidator.
 * 
 * @version 2.3
 * @author T.TSUCHIE
 *
 */
public class SheetBeanValidator implements ObjectValidator<Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(SheetBeanValidator.class);
    
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
     * Bean Validaion のデフォルトのインスタンスを作成する。
     * @return Validatorのインスタンス。
     */
    protected Validator createDefaultValidator() {
        
        final ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new MessageInterpolatorAdapter(new ResourceBundleMessageResolver(), new MessageInterpolator()))
                .buildValidatorFactory();
        final Validator validator = validatorFactory.usingContext()
                .getValidator();
        
        return validator;
    }
    
    /**
     * Bean ValidationのValidatorを取得する。
     * @return Validatorのインスタンス。
     */
    public Validator getTargetValidator() {
        return targetValidator;
    }
    
    /**
     * グループを指定して検証を実行する。
     * @param targetObj 検証対象のオブジェクト。
     * @param errors エラーオブジェクト
     * @param groups BeanValiationのグループのクラス
     */
    @Override
    public void validate(final Object targetObj, final SheetBindingErrors<?> errors, final Class<?>... groups) {
        
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
            final SheetBindingErrors<?> errors) {
        
        for(ConstraintViolation<Object> violation : violations) {
            
            final String fieldName = violation.getPropertyPath().toString();
            final Optional<FieldError> fieldError = errors.getFirstFieldError(fieldName);
            
            if(fieldError.isPresent() && fieldError.get().isConversionFailure()) {
                // 型変換エラーが既存のエラーにある場合は、処理をスキップする。
                continue;
            }
            
            final ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
            
            final String[] errorCodes = determineErrorCode(cd);
            
            final Map<String, Object> errorVars = createVariableForConstraint(cd);
            
            final String nestedPath = errors.buildFieldPath(fieldName);
            if(Utils.isEmpty(nestedPath)) {
                // オブジェクトエラーの場合
                errors.createGlobalError(errorCodes)
                    .variables(errorVars)
                    .defaultMessage(violation.getMessageTemplate())
                    .buildAndAddError();
                
            } else {
                // フィールドエラーの場合
                
                // 親のオブジェクトから、セルの座標を取得する
                final Object parentObj = violation.getLeafBean();
                final Path path = violation.getPropertyPath();
                Optional<CellPosition> cellAddress = Optional.empty();
                Optional<String> label = Optional.empty();
                if(Path.class.isAssignableFrom(PathImpl.class)) {
                    final String pathNodeName = getPathNodeName(path);
                    cellAddress = new PositionGetterFactory().create(parentObj.getClass(), pathNodeName)
                            .map(getter -> getter.get(parentObj)).orElse(Optional.empty());
                    
                    label = new LabelGetterFactory().create(parentObj.getClass(), pathNodeName)
                            .map(getter -> getter.get(parentObj)).orElse(Optional.empty());
                    
                }
                
                // フィールドフォーマッタ
                Class<?> fieldType = errors.getFieldType(nestedPath);
                if(fieldType != null) {
                    FieldFormatter<?> fieldFormatter = errors.findFieldFormatter(nestedPath, fieldType);
                    if(fieldFormatter != null) {
                        errorVars.putIfAbsent("fieldFormatter", fieldFormatter);
                    }
                }
                
                // 実際の値を取得する
                errorVars.putIfAbsent("validatedValue", violation.getInvalidValue());
                
                errors.createFieldError(fieldName, errorCodes)
                    .variables(errorVars)
                    .address(cellAddress)
                    .label(label)
                    .defaultMessage(violation.getMessageTemplate())
                    .buildAndAddError();
                
            }
            
        }
        
    }
    
    /**
     * BeanValidationのPathの名称を取得する。
     * <p>Hibernateのバージョンにより、パッケージが異なるのでリフレクションで取得する。
     * 
     * @param path パス
     * @return 名称
     */
    private String getPathNodeName(final Path path) {
        
        try {
            Method getLeafNodeMethod = PathImpl.class.getMethod("getLeafNode");
            Object leafNodeObj = getLeafNodeMethod.invoke(path);
            if(leafNodeObj == null) {
                return null;
            }
            
            Method getNodeNameMethod = NodeImpl.class.getMethod("getName");
            Object nodeName = getNodeNameMethod.invoke(leafNodeObj);
            return nodeName != null ? nodeName.toString() : null;
            
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("fail PathImple.getLeafNode().getName()", e);
        }
        
    }
    
    /**
     * エラーコードを決定する。
     * <p>※ユーザ指定メッセージの場合はエラーコードは空。</p>
     * 
     * @since 2.3
     * @param descriptor フィールド情報
     * @return エラーコード
     */
    protected String[] determineErrorCode(final ConstraintDescriptor<?> descriptor) {
        
     // バリデーション用アノテーションから属性「message」のでデフォルト値を取得し、変更されているかどう比較する。
        String defaultMessage = null;
        try {
            Method messageMethod = descriptor.getAnnotation().annotationType().getMethod("message");
            messageMethod.setAccessible(true);
            defaultMessage = Objects.toString(messageMethod.getDefaultValue(), null);
        } catch (NoSuchMethodException | SecurityException e) {
            logger.warn("Fail getting annotation's attribute 'message' for " + descriptor.getAnnotation().annotationType().getSimpleName() , e);
        }
        
        if(!descriptor.getMessageTemplate().equals(defaultMessage)) {
            /*
             * アノテーション属性「message」の値がデフォルト値から変更されている場合は、
             * ユーザー指定メッセージとして判断し、エラーコードは空にしてユーザー指定メッセージを優先させる。
             */
            return new String[]{};
            
        } else {
            // アノテーションのクラス名をもとに生成する。
            return new String[]{
                    descriptor.getAnnotation().annotationType().getSimpleName(),
                    descriptor.getAnnotation().annotationType().getCanonicalName(),
                    descriptor.getAnnotation().annotationType().getCanonicalName() + ".message"
            };
        }
    }
    
    /**
     * BeanValidationのアノテーションの値を元に、メッセージ変数を作成する。
     * @param descriptor
     * @return メッセージ変数
     */
    protected Map<String, Object> createVariableForConstraint(final ConstraintDescriptor<?> descriptor) {
        
        final Map<String, Object> vars = new HashMap<String, Object>();
        
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
