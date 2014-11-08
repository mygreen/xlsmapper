package org.mygreen.xlsmapper.validation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.mygreen.xlsmapper.Utils;


/**
 * シートのエラー情報を処理するためのクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class SheetBindingErrors {
    
    /** パスの区切り文字 */
    public static final String PATH_SEPARATOR = ".";
    
    /** シート名 */
    private String sheetName;
    
    /** オブジェクト名 */
    private final String objectName;
    
    /** 現在のパス。キャッシュ用。 */
    private String currentPath;
    
    /** 検証対象のオブジェクトの現在のパス */
    private Stack<String> nestedPathStack = new Stack<String>();
    
    /** エラーオブジェクト */
    private final List<ObjectError> errors = new ArrayList<ObjectError>();
    
    /** エラーコードの候補を生成するクラス */
    private MessageCodeGenerator messageCodeGenerator = new MessageCodeGenerator();
    
    public String getObjectName() {
        return objectName;
    }
    
    public SheetBindingErrors(final String objectName) {
        this.objectName = objectName;
    }
    
    /**
     * クラス名をオブジェクト名として設定します。
     * @param clazz
     * @return
     */
    public SheetBindingErrors(final Class<?> clazz) {
        this.objectName = clazz.getCanonicalName();
    }
    
    /**
     * 指定したパスで現在のパスを初期化します。
     * <p>nullまたは空文字を与えると、トップに移動します。
     * @param nestedPath
     */
    public void setNestedPath(final String nestedPath) {
        final String canonicalPath = getCanonicalPath(nestedPath);
        this.nestedPathStack.clear();
        if(!canonicalPath.isEmpty()) {
            pushNestedPath(canonicalPath);
        }
    }
    
    /**
     * パスのルートに移動します。
     */
    public void setRootPath() {
        setNestedPath(null);
    }
    
    /**
     * パスを標準化する。
     * <ol>
     *  <li>トリムする。
     *  <li>値がnullの場合は、空文字を返す。
     *  <li>最後に'.'がついている場合、除去する。
     * @param subPath
     * @return
     */
    private String getCanonicalPath(String subPath) {
        if(subPath == null) {
            return "";
        }
        
        String value = subPath.trim();
        if(value.isEmpty()) {
            return value;
        }
        
        if(value.startsWith(PATH_SEPARATOR)) {
            value = value.substring(1);
        }
        
        if(value.endsWith(PATH_SEPARATOR)) {
            value = value.substring(0, value.length()-1);
        }
        
        return value;
        
    }
    
    /**
     * パスを１つ下位に移動します。
     * @param subPath
     * @return 
     * @throws IllegalArgumentException subPath is empty.
     */
    public void pushNestedPath(final String subPath) {
        final String canonicalPath = getCanonicalPath(subPath);
        if(canonicalPath.isEmpty()) {
            throw new IllegalArgumentException(String.format("subPath is invalid path : '%s'", subPath));
        }
        this.nestedPathStack.push(canonicalPath);
        this.currentPath = buildPath();
    }
    
    /**
     * インデックス付きのパスを１つ下位に移動します。
     * @param subPath
     * @param index
     * @return 
     * @throws IllegalArgumentException subPath is empty.
     */
    public void pushNestedPath(final String subPath, final int index) {
        pushNestedPath(String.format("%s[%d]", subPath, index));
    }
    
    /**
     * キー付きのパスを１つ下位に移動します。
     * @param subPath
     * @param key
     * @return 
     * @throws IllegalArgumentException subPath is empty.
     */
    public void pushNestedPath(final String subPath, final String key) {
        pushNestedPath(String.format("%s[%s]", subPath, key));
    }
    
    /**
     * パスを１つ上位に移動します。
     * @return 
     * @throws IllegalStateException path stask is empty.
     */
    public String popNestedPath() {
        
        if(nestedPathStack.isEmpty()) {
            throw new IllegalStateException("Cannot pop nested path: no nested path on stack");
        }
        
        final String subPath = nestedPathStack.pop();
        this.currentPath = buildPath();
        return subPath;
    }
    
    /**
     * パスを組み立てる。
     * <p>ルートの時は空文字を返します。
     * @return
     */
    private String buildPath() {
        return Utils.join(nestedPathStack, PATH_SEPARATOR);
    }
    
    /**
     * 現在のパスを取得します。
     * <p>ルートの時は空文字を返します。
     * @return
     */
    public String getCurrentPath() {
        return currentPath;
    }
    
    /**
     * 現在のパスに引数で指定したフィールド名を追加した値を返す。
     * @param fieldName
     * @return
     */
    public String buildFieldPath(final String fieldName) {
        if(Utils.isEmpty(getCurrentPath())) {
            return fieldName;
        } else {
            return Utils.join(new String[]{getCurrentPath(), fieldName}, PATH_SEPARATOR);
        }
    }
    
    /**
     * エラーを追加する
     * @param error
     */
    public void addError(final ObjectError error) {
        this.errors.add(error);
    }
    
    /**
     * エラーを全て追加する。
     * @param errors
     */
    public void addAllErrors(final Collection<ObjectError> errors) {
        this.errors.addAll(errors);
    }
    
    /**
     * 全てのエラーを取得する
     * @return
     */
    public List<ObjectError> getAllErrors() {
        return new ArrayList<ObjectError>(errors);
    }
    
    public boolean hasErrors() {
        return !getAllErrors().isEmpty();
    }
    
    /**
     * グローバルエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<ObjectError> getGlobalErrors() {
        final List<ObjectError> list = new ArrayList<ObjectError>();
        for(ObjectError item : this.errors) {
            if(!(item instanceof FieldError)) {
                list.add(item);
            }
        }
        
        return list;
    }
    
    /**
     * 先頭のグローバルエラーを取得する。
     * @return 存在しない場合は、nullを返す。
     */
    public ObjectError getFistGlobalError() {
        for(ObjectError item : this.errors) {
            if(!(item instanceof FieldError)) {
                return item;
            }
        }
        
        return null;
    }
    
    public boolean hasGlobalErrors() {
        return !getGlobalErrors().isEmpty();
    }
    
    public int getGlobalErrorCount() {
        return getGlobalErrors().size();
    }
    
    /**
     * グローバルエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<SheetObjectError> getSheetGlobalErrors() {
        final List<SheetObjectError> list = new ArrayList<SheetObjectError>();
        for(ObjectError item : this.errors) {
            if(item instanceof SheetObjectError) {
                list.add((SheetObjectError) item);
            }
        }
        
        return list;
    }
    
    /**
     * 先頭のグローバルエラーを取得する。
     * @param sheetName シート名
     * @return 存在しない場合は、nullを返す。
     */
    public SheetObjectError getFistSheetGlobalError() {
        for(SheetObjectError item : getSheetGlobalErrors()) {
            return (SheetObjectError)item;
        }
        
        return null;
    }
    
    public boolean hasSheetGlobalErrors() {
        return !getSheetGlobalErrors().isEmpty();
    }
    
    public int getSheetGlobalErrorCount() {
        return getSheetGlobalErrors().size();
    }
    
    /**
     * フィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<FieldError> getFieldErrors() {
        final List<FieldError> list = new ArrayList<FieldError>();
        for(ObjectError item : this.errors) {
            if(item instanceof FieldError) {
                list.add((FieldError) item);
            }
        }
        
        return list;
    }
    
    /**
     * 先頭のフィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public FieldError getFirstFieldError() {
        for(ObjectError item : this.errors) {
            if(item instanceof FieldError) {
                return (FieldError) item;
            }
        }
        
        return null;
    }
    
    public boolean hasFieldErrors() {
        return !getFieldErrors().isEmpty();
    }
    
    public int getFieldErrorCount() {
        return getFieldErrors().size();
    }
    
    /**
     * パスを指定してフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     */
    public List<FieldError> getFieldErrors(final String path) {
        final List<FieldError> list = new ArrayList<FieldError>();
        for(ObjectError item : this.errors) {
            if(item instanceof FieldError && isMatchingFieldError(path, (FieldError) item)) {
                list.add((FieldError) item);
            }
        }
        
        return list;
    }
    
    /**
     * パスを指定して先頭のフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return エラーがない場合は空のリストを返す
     */
    public FieldError getFirstFieldError(final String path) {
        for(ObjectError item : this.errors) {
            if(item instanceof FieldError && isMatchingFieldError(path, (FieldError) item)) {
                return (FieldError) item;
            }
        }
        
        return null;
    }
    
    public boolean hasFieldErrors(final String path) {
        return !getFieldErrors(path).isEmpty();
    }
    
    public int getFieldErrorCount(final String path) {
        return getFieldErrors(path).size();
    }
    
    /**
     * 指定したパスがフィールドエラーのパスと一致するかチェックするかどうか。
     * @param path 
     * @param fieldError
     * @return true: 一致する場合。
     */
    protected boolean isMatchingFieldError(final String path, final FieldError fieldError) {
        
        if (fieldError.getFieldPath().equals(path)) {
            return true;
        }
        
        if(path.endsWith("*")) {
            String subPath = path.substring(0, path.length()-1);
            return fieldError.getFieldPath().startsWith(subPath);
        }
        
        return false;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public SheetBindingErrors setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    
    public void reject(final String errorCode) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{}));
    }
    
    public void reject(final String errorCode, final String defaultMessage) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{})
            .setDefaultMessage(defaultMessage));
    }
    
    public void reject(final String errorCode, final Object[] errorArgs) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs));
    }
    
    public void reject(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs)
                .setDefaultMessage(defaultMessage));
    }
    
    public void reject(final String errorCode, final Map<String, Object> errorVars) {
        addError(new ObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars));
    }
    
    public void reject(final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(new ObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars)
            .setDefaultMessage(defaultMessage));
    }
    
    public void rejectSheet(final String errorCode) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{},
           getSheetName()));
    }
    
    public void rejectSheet(final String errorCode, final String defaultMessage) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{},
                getSheetName())
            .setDefaultMessage(defaultMessage));
    }
    
    public void rejectSheet(final String errorCode, final Object[] errorArgs) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs,
            getSheetName()));
    }
    
    public void rejectSheet(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs,
                getSheetName())
            .setDefaultMessage(defaultMessage));
    }
    
    public void rejectSheet(final String errorCode, final Map<String, Object> errorVars) {
        addError(new SheetObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars,
                getSheetName()));
    }
    
    public void rejectSheet(final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(new SheetObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars,
                getSheetName())
            .setDefaultMessage(defaultMessage));
    }
    
    public void rejectValue(final String field, final String errorCode) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .build());
    }
    
    public void rejectValue(final String field, final String errorCode, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .defaultMessage(defaultMessage)
                .build());
    }
    
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .build());
    }
    
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    public void rejectValue(final String field, final String errorCode, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorVars)
                .build());
    }
    
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Object[] errorArgs) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    public void rejectTypeBind(final String field, final Object fieldValue, final Class<?> fieldType, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType), errorVars)
                .build());
    }
    
    public void rejectSheetTypeBind(final String field, final Object fieldValue, final Class<?> fieldType, final Map<String, Object> errorVars,
            final Point cellAddress, final String label) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .label(label)
                .build());
    }
    
    public void rejectTypeBind(final String field, final Object fieldValue, final Class<?> fieldType) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType))
                .build());
    }
    
    public void rejectSheetTypeBind(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String label) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType))
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .label(label)
                .build());
    }
    
    public MessageCodeGenerator getMessageCodeGenerator() {
        return messageCodeGenerator;
    }
    
    public void setMessageCodeGenerator(MessageCodeGenerator messageCodeGenerator) {
        this.messageCodeGenerator = messageCodeGenerator;
    }
    
    public String[] generateMessageCodes(final String code) {
        return getMessageCodeGenerator().generateCodes(code, getObjectName());
    }
    
    public String[] generateMessageCodes(final String code, final String field) {
        return getMessageCodeGenerator().generateCodes(code, getObjectName(), field, null);
    }
    
    public String[] generateMessageCodes(final String code, final String field, final Class<?> fieldType) {
        return getMessageCodeGenerator().generateCodes(code, getObjectName(), field, fieldType);
    }
    
}
