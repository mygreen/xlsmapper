package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link ArrayLabelSetter}のインスタンスを作成する
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ArrayLabelSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(ArrayLabelSetterFactory.class);
    
    /**
     * フィールドの位置情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return ラベル情報のsetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<ArrayLabelSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map labelsの場合
        Optional<ArrayLabelSetter> arrayLabelSetter = createMapField(beanClass, fieldName);
        if(arrayLabelSetter.isPresent()) {
            return arrayLabelSetter;
        }
        
        // setter メソッドの場合
        arrayLabelSetter = createMethod(beanClass, fieldName);
        if(arrayLabelSetter.isPresent()) {
            return arrayLabelSetter;
        }
        
        // フィールド + labelの場合
        arrayLabelSetter = createField(beanClass, fieldName);
        if(arrayLabelSetter.isPresent()) {
            return arrayLabelSetter;
        }
        
        
        return Optional.empty();
    }
    
    private String createMapKey(final String fieldName, final int index) {
        return String.format("%s[%d]", fieldName, index);
    }
    
    /**
     * {@link Map}フィールドにラベル情報が格納されている場合。
     * <p>キーはフィールド名。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<ArrayLabelSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
        final Field labelsField;
        try {
            labelsField = beanClass.getDeclaredField("labels");
            labelsField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は、何もしない。
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(labelsField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) labelsField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(String.class)) {
            return Optional.of(new ArrayLabelSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String label, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(label, "label");
                    
                    try {
                        Map<String, String> labelsMapObj = (Map<String, String>) labelsField.get(beanObj);
                        if(labelsMapObj == null) {
                            labelsMapObj = new LinkedHashMap<>();
                            labelsField.set(beanObj, labelsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, index);
                        
                        labelsMapObj.put(mapKey, label);
                        
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
     * <p>{@code set + <フィールド名> + Labels}のメソッド名</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<ArrayLabelSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String labelMethodName = "set" + Utils.capitalize(fieldName) + "Label";
        
        try {
            final Method method = beanClass.getDeclaredMethod(labelMethodName, Integer.TYPE, String.class);
            method.setAccessible(true);
            
            return Optional.of(new ArrayLabelSetter() {
                
                
                @Override
                public void set(final Object beanObj, final String label, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(label, "label");
                    
                    try {
                        method.invoke(beanObj, index, label);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access label field.", e);
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
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<ArrayLabelSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String labelFieldName = fieldName + "Label";
        
        final Field labelField;
        try {
            labelField = beanClass.getDeclaredField(labelFieldName);
            labelField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(!List.class.isAssignableFrom(labelField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) labelField.getGenericType();
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[0];
        
        if(valueType.equals(String.class)) {
            
            return Optional.of(new ArrayLabelSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String label, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(label, "label");
                    
                   try {
                       List<String> labelListObj = (List<String>) labelField.get(beanObj);
                       if(labelListObj == null) {
                           labelListObj = new ArrayList<>();
                           labelField.set(beanObj, labelListObj);
                       }
                       
                       Utils.addListWithIndex(labelListObj, label, index);
                       
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access label field.", e);
                    }
                }
            });
            
        }
        
        return Optional.empty();
    }
}
