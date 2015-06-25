package com.gh.mygreen.xlsmapper.validation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gh.mygreen.xlsmapper.Utils;


/**
 * シートのエラー情報を処理するためのクラス。
 * 
 * @version 0.5
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
    
    /**
     * オブジェクト名を指定しするコンストラクタ。
     * <p>エラーメッセージを組み立てる際に、パスのルートとなる。
     * @param objectName オブジェクト名
     */
    public SheetBindingErrors(final String objectName) {
        this.objectName = objectName;
    }
    
    /**
     * クラス名をオブジェクト名とするコンストラクタ。
     * @param clazz クラス名
     */
    public SheetBindingErrors(final Class<?> clazz) {
        this(clazz.getCanonicalName());
    }
    
    /**
     * 指定したパスで現在のパスを初期化します。
     * <p>nullまたは空文字を与えると、トップに移動します。
     * @param nestedPath
     */
    public void setNestedPath(final String nestedPath) {
        final String canonicalPath = getCanonicalPath(nestedPath);
        this.nestedPathStack.clear();
        if(canonicalPath.isEmpty()) {
            this.currentPath = buildPath();
        } else {
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
    private String getCanonicalPath(final String subPath) {
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
     * 全てのエラーをリセットする。
     * @since 0.5
     */
    public void clearAllErrors() {
        this.errors.clear();
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
    
    /**
     * エラーがあるか確かめる。
     * @return true:エラーがある。
     */
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
    public ObjectError getFirstGlobalError() {
        for(ObjectError item : this.errors) {
            if(!(item instanceof FieldError)) {
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * グローバルエラーがあるか確かめる。
     * @return
     */
    public boolean hasGlobalErrors() {
        return !getGlobalErrors().isEmpty();
    }
    
    /**
     * グローバルエラーの件数を取得する
     * @return
     */
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
    public SheetObjectError getFirstSheetGlobalError() {
        for(SheetObjectError item : getSheetGlobalErrors()) {
            return (SheetObjectError)item;
        }
        
        return null;
    }
    
    /**
     * シートに関するグローバルエラーがあるか確かめる。
     * @return true:シートに関するグローバルエラー。
     */
    public boolean hasSheetGlobalErrors() {
        return !getSheetGlobalErrors().isEmpty();
    }
    
    /**
     * シートに関するグローバルエラーの件数を取得する。
     * @return
     */
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
    
    /**
     * フィールドエラーが存在するか確かめる。
     * @return true:フィールドエラーを持つ。
     */
    public boolean hasFieldErrors() {
        return !getFieldErrors().isEmpty();
    }
    
    /**
     * フィールドエラーの件数を取得する。
     * @return
     */
    public int getFieldErrorCount() {
        return getFieldErrors().size();
    }
    
    /**
     * パスを指定してフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     */
    public List<FieldError> getFieldErrors(final String path) {
        final String fullPath = buildFieldPath(path);
        final List<FieldError> list = new ArrayList<FieldError>();
        for(ObjectError item : this.errors) {
            if(item instanceof FieldError && isMatchingFieldError(fullPath, (FieldError) item)) {
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
        final String fullPath = buildFieldPath(path);
        for(ObjectError item : this.errors) {
            if(item instanceof FieldError && isMatchingFieldError(fullPath, (FieldError) item)) {
                return (FieldError) item;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したパスのフィィールドエラーが存在するか確かめる。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return true:エラーがある場合。
     */
    public boolean hasFieldErrors(final String path) {
        return !getFieldErrors(path).isEmpty();
    }
    
    /**
     * 指定したパスのフィィールドエラーの件数を取得する。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     */
    public int getFieldErrorCount(final String path) {
        return getFieldErrors(path).size();
    }
    
    /**
     * セルのフィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<CellFieldError> getCellFieldErrors() {
        final List<CellFieldError> list = new ArrayList<CellFieldError>();
        for(ObjectError item : this.errors) {
            if(item instanceof CellFieldError) {
                list.add((CellFieldError) item);
            }
        }
        
        return list;
    }
    
    /**
     * 先頭のセルフィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public CellFieldError getCellFirstFieldError() {
        for(ObjectError item : this.errors) {
            if(item instanceof CellFieldError) {
                return (CellFieldError) item;
            }
        }
        
        return null;
    }
    
    /**
     * セルフィールドエラーが存在するか確かめる。
     * @return true:フィールドエラーを持つ。
     */
    public boolean hasCellFieldErrors() {
        return !getCellFieldErrors().isEmpty();
    }
    
    /**
     * セルフィールドエラーの件数を取得する。
     * @return
     */
    public int getCellFieldErrorCount() {
        return getCellFieldErrors().size();
    }
    
    /**
     * パスを指定してセルフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     */
    public List<CellFieldError> getCellFieldErrors(final String path) {
        final String fullPath = buildFieldPath(path);
        final List<CellFieldError> list = new ArrayList<CellFieldError>();
        for(ObjectError item : this.errors) {
            if(item instanceof CellFieldError && isMatchingFieldError(fullPath, (CellFieldError) item)) {
                list.add((CellFieldError) item);
            }
        }
        
        return list;
    }
    
    /**
     * パスを指定して先頭のセルフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return エラーがない場合はnullを返す。
     */
    public CellFieldError getFirstCellFieldError(final String path) {
        final String fullPath = buildFieldPath(path);
        for(ObjectError item : this.errors) {
            if(item instanceof CellFieldError && isMatchingFieldError(fullPath, (CellFieldError) item)) {
                return (CellFieldError) item;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したパスのセルフィィールドエラーが存在するか確かめる。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return true:エラーがある場合。
     */
    public boolean hasCellFieldErrors(final String path) {
        return !getCellFieldErrors(path).isEmpty();
    }
    
    /**
     * 指定したパスのセルフィィールドエラーの件数を取得する。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     */
    public int getCellFieldErrorCount(final String path) {
        return getCellFieldErrors(path).size();
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
    
    /**
     * 現在のシート名を取得する。
     * @return
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * 現在のシート名を設定します。
     * @param sheetName
     * @return
     */
    public SheetBindingErrors setSheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    
    /**
     * エラーコードを指定してグローバルエラーを登録します。
     * @param errorCode エラーコード
     */
    public void reject(final String errorCode) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{}));
    }
    
    /**
     * エラーコードとデフォルトメッセージを指定してグローバルエラーを登録します。
     * @param errorCode エラーコード
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void reject(final String errorCode, final String defaultMessage) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{})
            .setDefaultMessage(defaultMessage));
    }
    
    /**
     * エラーコードとメッセージ引数の値を指定してグローバルエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param errorCode エラーコード
     * @param errorArgs メッセージ引数。
     */
    public void reject(final String errorCode, final Object[] errorArgs) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs));
    }
    
    /**
     * エラーコードとメッセージ引数の値を指定してグローバルエラーを登録します。
     * @param errorCode エラーコード
     * @param errorArgs メッセージ引数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void reject(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(new ObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs)
                .setDefaultMessage(defaultMessage));
    }
    
    /**
     * エラーコードとメッセージ変数の値を指定してグローバルエラーを登録します。
     * @param errorCode エラーコード
     * @param errorVars メッセージ変数。
     */
    public void reject(final String errorCode, final Map<String, Object> errorVars) {
        addError(new ObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars));
    }
    
    /**
     * エラーコードとメッセージ変数の値を指定してグローバルエラーを登録します。
     * @param errorCode エラーコード
     * @param errorVars メッセージ変数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void reject(final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(new ObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars)
            .setDefaultMessage(defaultMessage));
    }
    
    /**
     * エラーコードを指定してシートのグローバルエラーを登録します。
     * @param errorCode エラーコード。
     */
    public void rejectSheet(final String errorCode) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{},
           getSheetName()));
    }
    
    /**
     * エラーコードとデフォルトメッセージを指定してシートのグローバルエラーを登録します。
     * @param errorCode エラーコード。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheet(final String errorCode, final String defaultMessage) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), new Object[]{},
                getSheetName())
            .setDefaultMessage(defaultMessage));
    }
    
    /**
     * エラーコードとメッセージ引数を指定してシートのグローバルエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param errorCode エラーコード
     * @param errorArgs メッセージ引数
     */
    public void rejectSheet(final String errorCode, final Object[] errorArgs) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs,
            getSheetName()));
    }
    
    /**
     * エラーコードとメッセージ引数を指定してシートのグローバルエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param errorCode エラーコード
     * @param errorArgs メッセージ引数
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheet(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(new SheetObjectError(
                getObjectName(),
                getMessageCodeGenerator().generateCodes(errorCode, getObjectName(), null, null), errorArgs,
                getSheetName())
            .setDefaultMessage(defaultMessage));
    }
    
    /**
     * エラーコードとメッセージ変数を指定してシートのグローバルエラーを登録します。
     * @param errorCode エラーコード
     * @param errorVars メッセージ変数
     */
    public void rejectSheet(final String errorCode, final Map<String, Object> errorVars) {
        addError(new SheetObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars,
                getSheetName()));
    }
    
    /**
     * エラーコードとメッセージ変数を指定してシートのグローバルエラーを登録します。
     * @param errorCode エラーコード
     * @param errorVars メッセージ変数
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheet(final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(new SheetObjectError(
                getObjectName(),
                generateMessageCodes(errorCode), errorVars,
                getSheetName())
            .setDefaultMessage(defaultMessage));
    }
    
    /**
     * エラーコードを指定してフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     */
    public void rejectValue(final String field, final String errorCode) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .build());
    }
    
    /**
     * エラーコードを指定してフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectValue(final String field, final String errorCode, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してフィールドエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     * @param errorArgs メッセージ引数。
     */
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してフィールドエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     * @param errorArgs メッセージ引数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     * @param errorVars メッセージ変数。
     */
    public void rejectValue(final String field, final String errorCode, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorVars)
                .build());
    }
    
    /**
     * エラーコードを指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     */
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param errorCode エラーコード。
     * @param errorVars メッセージ変数。
     */
    public void rejectValue(final String field, final Object fieldValue, final Class<?> fieldType,
            final String errorCode, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(generateMessageCodes(errorCode, field, fieldType), errorVars)
                .build());
    }
    
    /**
     * エラーコードを指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     */
    public void rejectSheetValue(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String errorCode) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(generateMessageCodes(errorCode, field, fieldType))
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    /**
     * エラーコードとデフォルトメッセージを指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field))
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してシートのフィールドエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param field フィールドパス。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorArgs メッセージ変数。
     */
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Object[] errorArgs) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してシートのフィールドエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param field フィールドパス。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorArgs メッセージ変数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorArgs)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * エラーコードとデメッセージ変数を指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorVars メッセージ変数。
     */
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    /**
     * エラーコードとメッセージ変数を指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorVars メッセージ変数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheetValue(final String field, final Point cellAddress, final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .codes(generateMessageCodes(errorCode, field), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してシートのフィールドエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorArgs メッセージ変数。
     */
    public void rejectSheetValue(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String errorCode, final Object[] errorArgs) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(generateMessageCodes(errorCode, field, fieldType), errorArgs)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    /**
     * エラーコードとメッセージ引数を指定してシートのフィールドエラーを登録します。
     * <p>メッセージ中の変数はインデックス形式で指定する必要がります。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorArgs メッセージ変数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheetValue(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(generateMessageCodes(errorCode, field, fieldType), errorArgs)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * エラーコードとデメッセージ変数を指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorVars メッセージ変数。
     */
    public void rejectSheetValue(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String errorCode, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(generateMessageCodes(errorCode, field, fieldType), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .build());
    }
    
    /**
     * エラーコードとメッセージ変数を指定してシートのフィールドエラーを登録します。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param errorCode メッセージコード。
     * @param errorVars メッセージ変数。
     * @param defaultMessage デフォルトメッセージ。指定したエラーコードに対するメッセージが見つからないときに使用する値です。
     */
    public void rejectSheetValue(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String errorCode, final Map<String, Object> errorVars, final String defaultMessage) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .codes(generateMessageCodes(errorCode, field, fieldType), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .defaultMessage(defaultMessage)
                .build());
    }
    
    /**
     * メッセージ変数付きのフィールド型変換エラーを指定する。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param errorVars メッセージ変数
     */
    public void rejectTypeBind(final String field, final Object fieldValue, final Class<?> fieldType, final Map<String, Object> errorVars) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .typeBindFailure(true)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType), errorVars)
                .build());
    }
    
    /**
     * メッセージ変数付きのシートのフィールド型変換エラーを指定する。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param errorVars メッセージ変数
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param label フィールドのラベル。
     */
    public void rejectSheetTypeBind(final String field, final Object fieldValue, final Class<?> fieldType, final Map<String, Object> errorVars,
            final Point cellAddress, final String label) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .typeBindFailure(true)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType), errorVars)
                .sheetName(getSheetName()).cellAddress(cellAddress)
                .label(label)
                .build());
    }
    
    /**
     * フィールド型変換エラーを指定する。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     */
    public void rejectTypeBind(final String field, final Object fieldValue, final Class<?> fieldType) {
        addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .typeBindFailure(true)
                .codes(getMessageCodeGenerator().generateTypeMismatchCodes(getObjectName(), field, fieldType))
                .build());
    }
    
    /**
     * シートのフィールド型変換エラーを指定する。
     * @param field フィールドパス。
     * @param fieldValue フィールドの値。
     * @param fieldType フィールドのクラスタイプ。
     * @param cellAddress セルのアドレス。x座標が列番号です。y座標が行番号です。列番号、行番号は0から始まります。
     * @param label フィールドのラベル。
     */
    public void rejectSheetTypeBind(final String field, final Object fieldValue, final Class<?> fieldType,
            final Point cellAddress, final String label) {
            addError(FieldErrorBuilder.create()
                .objectName(getObjectName()).fieldPath(buildFieldPath(field))
                .fieldValue(fieldValue).fieldType(fieldType)
                .typeBindFailure(true)
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
