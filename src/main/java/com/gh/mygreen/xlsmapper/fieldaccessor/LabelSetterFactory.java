package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link LabelSetter}のインスタンスを作成する
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LabelSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(LabelSetterFactory.class);
    
    /**
     * フィールドのラベル情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return 位置情報のsetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<LabelSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map labelsの場合
        Optional<LabelSetter> LabelSetter = createMapField(beanClass, fieldName);
        if(LabelSetter.isPresent()) {
            return LabelSetter;
        }
        
        // setter メソッドの場合
        LabelSetter = createMethod(beanClass, fieldName);
        if(LabelSetter.isPresent()) {
            return LabelSetter;
        }
        
        // フィールド + labelの場合
        LabelSetter = createField(beanClass, fieldName);
        if(LabelSetter.isPresent()) {
            return LabelSetter;
        }
        
        
        return Optional.empty();
    }
    
    /**
     * {@link Map}フィールドにラベル情報が格納されている場合。
     * <p>キーはフィールド名。</p>
     * <p>マップの値は、Stringをサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<LabelSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
        final Field labelField;
        try {
            labelField = beanClass.getDeclaredField("labels");
            labelField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は、何もしない。
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(labelField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) labelField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(String.class)) {
            return Optional.of(new LabelSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String label) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(label, "label");
                    
                    try {
                        Map<String, String> labelsMapObj = (Map<String, String>) labelField.get(beanObj);
                        if(labelsMapObj == null) {
                            labelsMapObj = new LinkedHashMap<>();
                            labelField.set(beanObj, labelsMapObj);
                        }
                        
                        labelsMapObj.put(fieldName, label);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access labels field.", e);
                    }
                }
            });
            
        } else {
            // タイプが一致しない場合
            log.warn("not match generics type of labels. key type:{}, value type:{}.", keyType.getName(), valueType.getName());
            return Optional.empty();
        }
        
    }
    
    /**
     * setterメソッドによるラベル情報を格納する場合。
     * <p>{@code set + <フィールド名> + Label}のメソッド名</p>
     * <p>引数として、をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<LabelSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String labelMethodName = "set" + Utils.capitalize(fieldName) + "Label";
        
        try {
            final Method method = beanClass.getDeclaredMethod(labelMethodName, String.class);
            method.setAccessible(true);
            
            return Optional.of(new LabelSetter() {
                
                
                @Override
                public void set(final Object beanObj, final String label) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(label, "label");
                    
                    try {
                        method.invoke(beanObj, label);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access labels field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        return Optional.empty();
        
    }
    
    /**
     * フィールドによるラベル情報を格納する場合。
     * <p>{@code <フィールド名> + Label}のメソッド名</p>
     * <p>引数として、{@link CellPosition}、{@link Point}、 {@link org.apache.poi.ss.util.CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<LabelSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String labelFieldName = fieldName + "Label";
        
        final Field labelField;
        try {
            labelField = beanClass.getDeclaredField(labelFieldName);
            labelField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(labelField.getType().equals(String.class)) {
            
            return Optional.of(new LabelSetter() {
                
                @Override
                public void set(final Object beanObj, final String label) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(label, "label");
                    
                    try {
                        labelField.set(beanObj, label);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access label field.", e);
                    }
                }
            });
            
        }
        
        return Optional.empty();
    }
}
