package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * {@link FieldFormatter}を管理するためのクラス。
 * <p>フィールドの値を検証する際に、フィールドの値をフォーマットしてエラーメッセージに埋め込むために使用します。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldFormatterRegistry {
    
    /**
     * クラスタイプで関連づけられたフォーマッタ
     */
    private Map<Class<?>, FieldFormatter<?>> typeMap = new HashMap<>();
    
    /**
     * フィールドパスで関連づけられたフォーマッタ
     */
    private Map<String, FormatterHolder> pathMap = new HashMap<>();
    
    /**
     * 登録されているフォーマッタを初期化する。
     */
    public void init() {
        this.typeMap.clear();
        this.pathMap.clear();
        
        
    }
    
    /**
     * クラスタイプを指定してフォーマッタを登録する。
     * @param <T> 処理対象のタイプ
     * @param requiredType クラスタイプ
     * @param formatter 登録対象のフォーマッタ
     * @throws IllegalArgumentException {@literal requiredType == null or formatter == null}
     */
    public <T> void registerFormatter(final Class<T> requiredType, FieldFormatter<T> formatter) {
        ArgUtils.notNull(requiredType, "requiredType");
        ArgUtils.notNull(formatter, "formatter");
        
        this.typeMap.put(requiredType, formatter);
        
    }
    
    /**
     * フィールドパスとクラスタイプを指定してフォーマッタを登録する。
     * @param <T> 処理対象のタイプ
     * @param fieldPath
     * @param requiredType クラスタイプ
     * @param formatter 登録対象のフォーマッタ
     */
    public <T> void registerFormatter(final String fieldPath, final Class<T> requiredType, FieldFormatter<T> formatter) {
        ArgUtils.notEmpty(fieldPath, "fieldPath");
        ArgUtils.notNull(requiredType, "requiredType");
        ArgUtils.notNull(formatter, "formatter");
        
        this.pathMap.put(fieldPath, new FormatterHolder(requiredType, formatter));
        
    }
    
    /**
     * 指定したクラスタイプに対する{@link FieldFormatter}を取得する。
     * @param <T> クラスタイプ
     * @param requiredType 取得したいクラスタイプ
     * @return 見つからない場合は、nullを返す。
     */
    @SuppressWarnings("unchecked")
    public <T> FieldFormatter<T> getFormatter(final Class<T> requiredType) {
        return (FieldFormatter<T>)typeMap.get(requiredType);
    }
    
    /**
     * 指定したパスとクラスタイプに対する{@link FieldFormatter}を取得する。
     * <p>引数「fieldPath」に完全に一致するフォーマッタを取得します。</p>
     * @param fieldPath フィールドパス。
     * @param requiredType 取得したいクラスタイプ
     * @return 見つからない場合は、nullを返す。
     */
    public <T> FieldFormatter<T> getFormatter(final String fieldPath, final Class<T> requiredType) {
        FormatterHolder holder = pathMap.get(fieldPath);
        return holder != null ? holder.getFormatter(requiredType) : null;
    }
    
    /**
     * 指定したパスとクラスタイプに対する{@link FieldFormatter}を取得する。
     * <p>ただし、リストのインデックスやキーが含まれている場合、引数「fieldPath」からそれらを取り除いた値で比較する。
     *  <br>一致するフィールドに対するフォーマッタが見つからない場合は、タイプのみで比較する。
     * </p>
     * @param fieldPath フィールドパス。
     * @param requiredType 取得したいクラスタイプ
     * @return 見つからない場合は、nullを返す。
     */
    public <T> FieldFormatter<T> findFormatter(final String fieldPath, final Class<T> requiredType) {
        
        // 完全なパスで比較
        FieldFormatter<T> formatter = getFormatter(fieldPath, requiredType);
        if(formatter != null) {
            return formatter;
        }
        
        // インデックスを除去した形式で比較
        final List<String> strippedPaths = new ArrayList<>();
        addStrippedPropertyPaths(strippedPaths, "", fieldPath);
        
        for(String strippedPath : strippedPaths) {
            formatter = getFormatter(strippedPath, requiredType);
            if(formatter != null) {
                return formatter;
            }
            
        }
        
        // 見つからない場合は、タイプのみで比較した物を取得する
        return getFormatter(requiredType);
        
        
    }
    
    /**
     * パスからリストのインデックス([1])やマップのキー([key])を除去したものを構成する。
     * <p>SpringFrameworkの「PropertyEditorRegistrySupport#addStrippedPropertyPaths(...)」の処理</p>
     * @param strippedPaths 除去したパス
     * @param nestedPath 現在のネストしたパス
     * @param propertyPath 処理対象のパス
     */
    public void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {
        
        final int startIndex = propertyPath.indexOf('[');
        if (startIndex != -1) {
            final int endIndex = propertyPath.indexOf(']');
            if (endIndex != -1) {
                final String prefix = propertyPath.substring(0, startIndex);
                final String key = propertyPath.substring(startIndex, endIndex + 1);
                final String suffix = propertyPath.substring(endIndex + 1, propertyPath.length());
                
                // Strip the first key.
                strippedPaths.add(nestedPath + prefix + suffix);
                
                // Search for further keys to strip, with the first key stripped.
                addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
                
                // Search for further keys to strip, with the first key not stripped.
                addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
            }
        }
    }
    
    private static class FormatterHolder {
        
        private final Class<?> registerdType;
        
        private final FieldFormatter<?> formatter;
        
        private FormatterHolder(Class<?> registerdType, FieldFormatter<?> formatter) {
            this.registerdType = registerdType;
            this.formatter = formatter;
        }
        
        @SuppressWarnings("unchecked")
        private <T> FieldFormatter<T> getFormatter(final Class<T> requiredType) {
            if(registerdType.isAssignableFrom(requiredType)) {
                return (FieldFormatter<T>)formatter;
            } else {
                return null;
            }
        }
    }
    
}
