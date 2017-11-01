package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link LabelGetter}のインスタンスを作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LabelGetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(LabelGetterFactory.class);
    
    /**
     * フィールドのラベル情報を取得するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return ラベル情報のgetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<LabelGetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map labelsの場合
        Optional<LabelGetter> LabelGetter = createMapField(beanClass, fieldName);
        if(LabelGetter.isPresent()) {
            return LabelGetter;
        }
        
        // setter メソッドの場合
        LabelGetter = createMethod(beanClass, fieldName);
        if(LabelGetter.isPresent()) {
            return LabelGetter;
        }
        
        // フィールド + labelの場合
        LabelGetter = createField(beanClass, fieldName);
        if(LabelGetter.isPresent()) {
            return LabelGetter;
        }
        
        
        return Optional.empty();
        
        
    }
    
    private Optional<LabelGetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
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
            return Optional.of(new LabelGetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public Optional<String> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        Map<String, String> labelsMapObj = (Map<String, String>) labelsField.get(beanObj);
                        if(labelsMapObj == null) {
                            return Optional.empty();
                        }
                        
                        return Optional.ofNullable(labelsMapObj.get(fieldName));
                        
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
    
    private Optional<LabelGetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String labelMethodName = "get" + Utils.capitalize(fieldName) + "Label";
        
        final Method method;
        try {
            method = beanClass.getDeclaredMethod(labelMethodName);
            method.setAccessible(true);
            
        } catch (NoSuchMethodException | SecurityException e) {
            return Optional.empty();
        }
        
        if(method.getReturnType().equals(String.class)) {
            return Optional.of(new LabelGetter() {
                
                @Override
                public Optional<String> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final String address = (String)method.invoke(beanObj);
                        return Optional.ofNullable(address);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access labels getter method.", e);
                    }
                    
                }
            });
            
        }
        
        return Optional.empty();
        
    }
    
    private Optional<LabelGetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String labelFieldName = fieldName + "Label";
        
        final Field labelField;
        try {
            labelField = beanClass.getDeclaredField(labelFieldName);
            labelField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(labelField.getType().equals(String.class)) {
            
            return Optional.of(new LabelGetter() {
                
                @Override
                public Optional<String> get(final Object beanObj) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    
                    try {
                        final String address = (String) labelField.get(beanObj);
                        return Optional.ofNullable(address);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access label field.", e);
                    }
                }
            });
            
        }
        
        return Optional.empty();
    }
    
}
