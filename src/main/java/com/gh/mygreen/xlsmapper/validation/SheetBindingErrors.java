package com.gh.mygreen.xlsmapper.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import com.gh.mygreen.xlsmapper.util.PropertyTypeNavigator;
import com.gh.mygreen.xlsmapper.util.PropertyValueNavigator;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatter;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatterRegistry;
import com.github.mygreen.cellformatter.lang.ArgUtils;


/**
 * 1シート分のエラー情報を管理するクラス。
 * 
 * @param <P> シートにマッピングするクラスタイプ
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class SheetBindingErrors<P> {
    
    /** パスの区切り文字 */
    public static final String PATH_SEPARATOR = ".";
    
    /**
     * 検証対象のオブジェクト。
     * ・ルートオブジェクト
     */
    private final P target;
    
    /**
     * オブジェクト名
     */
    private final String objectName;
    
    /**
     * シート名
     */
    private String sheetName;
    
    /**
     * シートのインデックス
     */
    private int sheetIndex = -1;
    
    /**
     * 現在のパス。
     * キャッシュ用。
     */
    private String currentPath;
    
    /** 
     * 検証対象のオブジェクトの現在のパス
     */
    private Stack<String> nestedPathStack = new Stack<>();
    
    /**
     * エラーオブジェクト
     */
    private final List<ObjectError> errors = new ArrayList<>();
    
    /**
     * フィールドの値のフォーマッタの管理クラス
     */
    private FieldFormatterRegistry fieldFormatterRegistry = new FieldFormatterRegistry();
    
    /** エラーコードの候補を生成するクラス */
    private MessageCodeGenerator messageCodeGenerator = new MessageCodeGenerator();
    
    /**
     * プロパティ式から、値を取得する。
     * ・private/protectedなどのフィールドにもアクセス可能にする。
     */
    private final PropertyValueNavigator propertyValueNavigator = new PropertyValueNavigator();
    {
        propertyValueNavigator.setAllowPrivate(true);
        propertyValueNavigator.setIgnoreNull(true);
        propertyValueNavigator.setIgnoreNotFoundKey(true);
        propertyValueNavigator.setCacheWithPath(true);
    }
    
    /**
     * プロパティ式から、クラスタイプを取得する。
     * ・private/protectedなどのフィールドにもアクセス可能にする。
     */
    private final PropertyTypeNavigator propertyTypeNavigator = new PropertyTypeNavigator();
    {
        propertyTypeNavigator.setAllowPrivate(true);
        propertyTypeNavigator.setIgnoreNotResolveType(true);
        propertyTypeNavigator.setCacheWithPath(true);
    }
    
    /**
     * オブジェクト名を指定しするコンストラクタ。
     * <p>エラーメッセージを組み立てる際に、パスのルートとなる。
     * @param target 検証対象のオブジェクト
     * @param objectName オブジェクト名
     */
    public SheetBindingErrors(final P target, final String objectName) {
        
        this.target = target;
        this.objectName = objectName;
        
        this.fieldFormatterRegistry.init();
    }
    
    /**
     * クラス名をオブジェクト名とするコンストラクタ。
     * <p>オブジェクト名として、{@link Class#getCanonicalName()}を設定します。</p>
     * @param target 検証対象のオブジェクト
     * @throws IllegalArgumentException {@link target == null.}
     */
    public SheetBindingErrors(final P target) {
        this(target, target.getClass().getCanonicalName());
    }
    
    /**
     * 検証対象のオブジェクトを取得する。
     * @return
     */
    public P getTarget() {
        return target;
    }
    
    /**
     * 現在のオブジェクト名称を取得する
     * @return コンストラクタで設定したオブジェクト名称。
     */
    public String getObjectName() {
        return objectName;
    }
    
    /**
     * 現在のシート名を取得する。
     * @return シート名称
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * 現在のシート名を設定します。
     * @param sheetName シートの名称
     */
    public void setSheetName(final String sheetName) {
        this.sheetName = sheetName;
    }
    
    /**
     * シート番号を取得する
     * @return 0から始まる。ただし、シートが設定されていない状態の時は、-1を返す。
     */
    public int getSheetIndex() {
        return sheetIndex;
    }
    
    /**
     * シート番号を設定する
     * @param sheetIndex シート番号(0から始まる)
     */
    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    
    /**
     * 指定したパスのフィールドのクラスタイプを取得する。
     * @since 2.0
     * @param field フィールド名
     * @return クラスタイプ。ただし、リストなどGenericsのタイプが指定されていない場合、クラスタイプもnullとなる。
     */
    public Class<?> getFieldType(final String field) {
        
        final String fieldPath = buildFieldPath(field);
        Class<?> type = propertyTypeNavigator.getPropertyType(target.getClass(), fieldPath);
        if(type != null) {
            return type;
        }
        
        return getActualFieldType(fieldPath);
    }
    
    /**
     * 指定したパスのフィールドのクラスタイプを取得する。
     * <p>インスタンスを元に取得するため、サブクラスの可能性がある。</p>
     * @since 2.0
     * @param field フィールド名
     * @return クラスタイプ。ただし、オブジェクトの値がnullの場合は、クラスタイプもnullとなる。
     */
    public Class<?> getActualFieldType(final String field) {
        
        final Object fieldValue = getFieldValue(field);
        return fieldValue == null ? null : fieldValue.getClass();
        
    }
    
    /**
     * 指定したパスのフィールドの値を取得する。
     * <p>フィールドエラーにエラーが存在するときは、エラーオブジェクトから値を取得し、存在しない場合は、実際の値を取得する。</p>
     * @param field フィールド名
     * @return フィールドの値。
     */
    public Object getFieldValue(final String field) {
        
        final FieldError error = getFirstFieldError(field).orElse(null);
        if(error != null && !error.isConversionFailure()) {
            return error.getRejectedValue();
        } else {
            return getFieldActualValue(field);
        }
        
    }
    
    /**
     * 指定したパスのフィールドの値を取得する。
     * @since 2.0
     * @param field フィールド名
     * @return フィールドの値。
     */
    public Object getFieldActualValue(final String field) {
        final String fieldPath = buildFieldPath(field);
        return propertyValueNavigator.getProperty(target, fieldPath);
    }
    
    /**
     * 現在のパス上のプロパティの値を取得します。
     * <p>{@link #getTarget()}で取得できるルートオブジェクトに対して、{@link #getCurrentPath()}のパスで示された値を取得します。</p>
     * @return 現在のパス上の値。
     */
    public Object getValue() {
        final String currentPath = getCurrentPath();
        if(Utils.isEmpty(currentPath)) {
            return target;
        } else {
            return propertyValueNavigator.getProperty(target, currentPath);
        }
    }
    
    /**
     * 指定したパスで現在のパスを初期化します。
     * <p>nullまたは空文字を与えると、トップに移動します。
     * @param nestedPath ネストするパス
     */
    public void setNestedPath(final String nestedPath) {
        final String canonicalPath = normalizePath(nestedPath);
        this.nestedPathStack.clear();
        if(canonicalPath.isEmpty()) {
            this.currentPath = buildPath();
        } else {
            pushNestedPath(canonicalPath);
        }
    }
    
    /**
     * 現在のパスをルートに移動します。
     */
    public void setRootPath() {
        setNestedPath(null);
    }
    
    /**
     * パスを正規化する。
     * <ol>
     *  <li>トリムする。</li>
     *  <li>値がnullの場合は、空文字を返す。</li>
     *  <li>最後に'.'がついている場合、除去する。</li>
     * </ol>
     * @param subPath
     * @return
     */
    private String normalizePath(final String subPath) {
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
     * @param subPath ネストするパス
     * @throws IllegalArgumentException subPath is empty.
     */
    public void pushNestedPath(final String subPath) {
        final String canonicalPath = normalizePath(subPath);
        ArgUtils.notEmpty(canonicalPath, "canonicalPath");
        
        this.nestedPathStack.push(canonicalPath);
        this.currentPath = buildPath();
    }
    
    /**
     * 配列やリストなどのインデックス付きのパスを１つ下位に移動します。
     * @param subPath ネストするパス
     * @param index インデックス番号(0から始まります。)
     * @throws IllegalArgumentException {@literal subPath is empty or index < 0}
     */
    public void pushNestedPath(final String subPath, final int index) {
        final String canonicalPath = normalizePath(subPath);
        ArgUtils.notEmpty(subPath, "subPath");
        ArgUtils.notMin(index, -1, "index");
        
        pushNestedPath(String.format("%s[%d]", canonicalPath, index));
    }
    
    /**
     * マップなどのキー付きのパスを１つ下位に移動します。
     * @param subPath ネストするパス
     * @param key マップのキー
     * @throws IllegalArgumentException {@literal subPath is empty or key is empty}
     */
    public void pushNestedPath(final String subPath, final String key) {
        final String canonicalPath = normalizePath(subPath);
        ArgUtils.notEmpty(subPath, "subPath");
        ArgUtils.notEmpty(key, "key");
        
        pushNestedPath(String.format("%s[%s]", canonicalPath, key));
    }
    
    /**
     * パスを１つ上位に移動します。
     * @return 現在のパスを返しまます。
     * @throws IllegalStateException {@literal パスがこれ以上移動できない場合}
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
     * 現在パスのスタックに積まれているパスを結合し、１つに組み立てる。
     * <p>ルートの時は空文字を返します。</p>
     * @return 結合したパス
     */
    private String buildPath() {
        return Utils.join(nestedPathStack, PATH_SEPARATOR);
    }
    
    /**
     * 現在のパスを取得します。
     * <p>ルートの時は空文字を返します。</p>
     * @return 現在のパス
     */
    public String getCurrentPath() {
        return currentPath;
    }
    
    /**
     * 現在のパスに引数で指定したフィールド名を追加した値を返す。
     * <p>現在のパスが空の場合は、フィールド名を返す。</p>
     * @param fieldName フィールド名
     * @return フィールド名を追加したパス
     */
    public String buildFieldPath(final String fieldName) {
        if(Utils.isEmpty(getCurrentPath())) {
            return fieldName;
        } else {
            return Utils.join(new String[]{getCurrentPath(), fieldName}, PATH_SEPARATOR);
        }
    }
    
    /**
     * 全てのエラーをクリアする。
     * @since 0.5
     */
    public void clearAllErrors() {
        this.errors.clear();
    }
    
    /**
     * エラー情報を追加する
     * @param error エラー情報
     * @throws IllegalArgumentException {@literal error == null.}
     */
    public void addError(final ObjectError error) {
        ArgUtils.notNull(error, "error");
        this.errors.add(error);
    }
    
    /**
     * エラー情報を全て追加する。
     * @param errors エラー情報
     * @throws IllegalArgumentException {@literal errors == null.}
     */
    public void addAllErrors(final Collection<ObjectError> errors) {
        ArgUtils.notNull(errors, "errors");
        this.errors.addAll(errors);
    }
    
    /**
     * 全てのエラー情報を取得する
     * @return 全てのエラー情報
     */
    public List<ObjectError> getAllErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * エラーがあるか確かめる。
     * @return true:エラーがある。
     */
    public boolean hasErrors() {
        return errors.size() > 0;
    }
    
    /**
     * グローバルエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<ObjectError> getGlobalErrors() {
        return errors.stream()
                .filter(e -> !(e instanceof FieldError))
                .collect(Collectors.toList());
    }
    
    /**
     * 先頭のグローバルエラーを取得する。
     * @return 存在しない場合は、空を返す。
     */
    public Optional<ObjectError> getFirstGlobalError() {
        return errors.stream()
                .filter(e -> !(e instanceof FieldError))
                .findFirst();
        
    }
    
    /**
     * グローバルエラーがあるか確かめる。
     * @return true:グローバルエラーがある。
     */
    public boolean hasGlobalErrors() {
        return getFirstGlobalError().isPresent();
    }
    
    /**
     * グローバルエラーの件数を取得する
     * @return エラーの件数
     */
    public int getGlobalErrorCount() {
        return getGlobalErrors().size();
    }
    
    /**
     * フィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<FieldError> getFieldErrors() {
        return errors.stream()
                .filter(e -> e instanceof FieldError)
                .map(e -> (FieldError)e)
                .collect(Collectors.toList());
        
    }
    
    /**
     * 先頭のフィールドエラーを取得する
     * @return エラーがない場合は空を返す
     */
    public Optional<FieldError> getFirstFieldError() {
        return errors.stream()
                .filter(e -> e instanceof FieldError)
                .map(e -> (FieldError)e)
                .findFirst();
        
    }
    
    /**
     * フィールドエラーが存在するか確かめる。
     * @return true:フィールドエラーを持つ。
     */
    public boolean hasFieldErrors() {
        return getFirstFieldError().isPresent();
    }
    
    /**
     * フィールドエラーの件数を取得する。
     * @return フィールドエラーの件数
     */
    public int getFieldErrorCount() {
        return getFieldErrors().size();
    }
    
    /**
     * パスを指定してフィールドエラーを取得する。
     * <p>検索する際には、引数「path」に現在のパス({@link #getCurrentPath()})を付与して処理します。</p>
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return エラーがない場合は空のリストを返す
     */
    public List<FieldError> getFieldErrors(final String path) {
        final String fullPath = buildFieldPath(path);
        
        return getFieldErrors().stream()
                .filter(e -> isMatchingFieldError(fullPath, e))
                .collect(Collectors.toList());
        
    }
    
    /**
     * パスを指定して先頭のフィールドエラーを取得する。
     * <p>検索する際には、引数「path」に現在のパス({@link #getCurrentPath()})を付与して処理します。</p>
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return エラーがない場合は空を返す
     */
    public Optional<FieldError> getFirstFieldError(final String path) {
        final String fullPath = buildFieldPath(path);
        return getFieldErrors().stream()
                .filter(e -> isMatchingFieldError(fullPath, e))
                .findFirst();
        
    }
    
    /**
     * 指定したパスのフィィールドエラーが存在するか確かめる。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return true:エラーがある場合。
     */
    public boolean hasFieldErrors(final String path) {
        return getFirstFieldError(path).isPresent();
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
     * 指定したパスがフィールドエラーのパスと一致するかチェックするかどうか。
     * @param path パス
     * @param fieldError フィールドエラー
     * @return true: 一致する場合。
     */
    private boolean isMatchingFieldError(final String path, final FieldError fieldError) {
        
        if (fieldError.getField().equals(path)) {
            return true;
        }
        
        if(path.endsWith("*")) {
            String subPath = path.substring(0, path.length()-1);
            return fieldError.getField().startsWith(subPath);
        }
        
        return false;
    }
    
    /**
     * グローバルエラーのビルダーを作成します。
     * @param errorCode エラーコード
     * @return {@link ObjectError}のインスタンスを組み立てるビルダクラス。
     */
    public InternalObjectErrorBuilder createGlobalError(final String errorCode) {
        return createGlobalError(new String[]{errorCode});
    }
    
    /**
     * グローバルエラーのビルダーを作成します。
     * @param errorCodes エラーコード。先頭の要素が優先されます。
     * @return {@link ObjectError}のインスタンスを組み立てるビルダクラス。
     */
    public InternalObjectErrorBuilder createGlobalError(final String[] errorCodes) {
        
        String[] codes = new String[0];
        for(String errorCode : errorCodes) {
            codes = Utils.concat(codes, generateMessageCodes(errorCode));
        }
        
        return new InternalObjectErrorBuilder(this, getObjectName(), codes)
                .sheetName(getSheetName());
    }
    
    /**
     * フィールドエラーのビルダーを作成します。
     * @param field フィールドパス。
     * @param errorCode エラーコード
     * @return {@link FieldError}のインスタンスを組み立てるビルダクラス。
     */
    public InternalFieldErrorBuilder createFieldError(final String field, final String errorCode) {
        
        return createFieldError(field, new String[]{errorCode});
        
    }
    
    /**
     * フィールドエラーのビルダーを作成します。
     * @param field フィールドパス。
     * @param errorCodes エラーコード。先頭の要素が優先されます。
     * @return {@link FieldError}のインスタンスを組み立てるビルダクラス。
     */
    public InternalFieldErrorBuilder createFieldError(final String field, final String[] errorCodes) {
        
        final String fieldPath = buildFieldPath(field);
        final Class<?> fieldType = getFieldType(field);
        final Object fieldValue = getFieldValue(field);
        
        String[] codes = new String[0];
        for(String errorCode : errorCodes) {
            codes = Utils.concat(codes, generateMessageCodes(errorCode, fieldPath, fieldType));
        }
        
        return new InternalFieldErrorBuilder(this, getObjectName(), fieldPath, codes)
                .sheetName(getSheetName())
                .rejectedValue(fieldValue);
                
    }
    
    /**
     * 型変換失敗時のフィールエラー用のビルダを作成します。
     * @param field フィールドパス。
     * @param fieldType フィールドのクラスタイプ
     * @param rejectedValue 型変換に失敗した値
     * @return {@link FieldError}のインスタンスを組み立てるビルダクラス。
     */
    public InternalFieldErrorBuilder createFieldConversionError(final String field, final Class<?> fieldType, final Object rejectedValue) {
        
        final String fieldPath = buildFieldPath(field);
        final String[] codes = messageCodeGenerator.generateTypeMismatchCodes(getObjectName(), fieldPath, fieldType);
        
        return new InternalFieldErrorBuilder(this, getObjectName(), fieldPath, codes)
                .sheetName(getSheetName())
                .rejectedValue(rejectedValue)
                .conversionFailure(true);
        
        
    }
    
    /**
     * フィールドに対するフォーマッタを登録する。
     * @since 2.0
     * @param field フィールド名
     * @param fieldType フィールドのクラスタイプ
     * @param formatter フォーマッタ
     */
    public void registerFieldFormatter(final String field, final Class<?> fieldType, final FieldFormatter<?> formatter) {
        
        registerFieldFormatter(field, fieldType, formatter, false);
        
    }
    
    /**
     * フィールドに対するフォーマッタを登録する。
     * @since 2.0
     * @param field フィールド名
     * @param fieldType フィールドのクラスタイプ
     * @param formatter フォーマッタ
     * @param strippedIndex 登録するときにフィールドパスから、インデックス情報を除去するかどうか。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerFieldFormatter(final String field, final Class<?> fieldType, final FieldFormatter<?> formatter,
            final boolean strippedIndex) {
        
        String fieldPath = buildFieldPath(field);
        
        if(strippedIndex) {
            // パスからインデックスやキーを削除する
            List<String> strippedPaths = new ArrayList<>();
            fieldFormatterRegistry.addStrippedPropertyPaths(strippedPaths, "", fieldPath);
            
            if(strippedPaths.size() > 0) {
                // 辞書順位並び変えて先頭に来るのが、インデックスを全て削除されたパス
                Collections.sort(strippedPaths);
                fieldPath = strippedPaths.get(0);
            }
        }
        
        fieldFormatterRegistry.registerFormatter(fieldPath, (Class)fieldType, (FieldFormatter)formatter);
        
    }
    
    /**
     * フィールドとクラスタイプを指定してフォーマッタを取得する。
     * @since 2.0
     * @param field フィールド名
     * @param fieldType フィールドのクラスタイプ
     * @return 見つからない場合は、nullを返す。
     */
    public <T> FieldFormatter<T> findFieldFormatter(final String field, final Class<T> fieldType) {
        String fieldPath = buildFieldPath(field);
        return fieldFormatterRegistry.findFormatter(fieldPath, fieldType);
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
    
    public MessageCodeGenerator getMessageCodeGenerator() {
        return messageCodeGenerator;
    }
    
    public void setMessageCodeGenerator(MessageCodeGenerator messageCodeGenerator) {
        this.messageCodeGenerator = messageCodeGenerator;
    }
    
    /**
     * フィールドのフォーマッタの管理クラスを取得する。
     * @return
     */
    public FieldFormatterRegistry getFieldFormatterRegistry() {
        return fieldFormatterRegistry;
    }
    
    /**
     * フィールドのフォーマッタクラスを設定する。
     * @param fieldFormatterRegistry フィールドのフォーマッタの管理クラス
     */
    public void setFieldFormatterRegistry(FieldFormatterRegistry fieldFormatterRegistry) {
        this.fieldFormatterRegistry = fieldFormatterRegistry;
    }
    
}
