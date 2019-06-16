package com.gh.mygreen.xlsmapper.fieldaccessor;

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
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link MapCommentSetter}のインスタンスを作成する
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MapCommentSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(MapCommentSetterFactory.class);
    
    /**
     * フィールドの位置情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return コメント情報のsetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<MapCommentSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map commentsの場合
        Optional<MapCommentSetter> mapCommentSetter = createMapField(beanClass, fieldName);
        if(mapCommentSetter.isPresent()) {
            return mapCommentSetter;
        }
        
        // setter メソッドの場合
        mapCommentSetter = createMethod(beanClass, fieldName);
        if(mapCommentSetter.isPresent()) {
            return mapCommentSetter;
        }
        
        // フィールド + commentの場合
        mapCommentSetter = createField(beanClass, fieldName);
        if(mapCommentSetter.isPresent()) {
            return mapCommentSetter;
        }
        
        
        return Optional.empty();
    }
    
    private String createMapKey(final String fieldName, final String key) {
        return String.format("%s[%s]", fieldName, key);
    }
    
    /**
     * {@link Map}フィールドにコメント情報が格納されている場合。
     * <p>キーはフィールド名。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return コメント情報の設定用クラス
     */
    private Optional<MapCommentSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
        final Field commentsField;
        try {
            commentsField = beanClass.getDeclaredField("comments");
            commentsField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は、何もしない。
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(commentsField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) commentsField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(String.class)) {
            return Optional.of(new MapCommentSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String comment, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                    try {
                        Map<String, String> commentsMapObj = (Map<String, String>) commentsField.get(beanObj);
                        if(commentsMapObj == null) {
                            commentsMapObj = new LinkedHashMap<>();
                            commentsField.set(beanObj, commentsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, key);
                        
                        commentsMapObj.put(mapKey, comment);
                        
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access comments field.", e);
                    }
                }
            });
            
        } else {
            // タイプが一致しない場合
            log.warn("not match generics type of comments. key type:{}, value type:{}.", keyType.getName(), valueType.getName());
            return Optional.empty();
        }
        
    }
    
    /**
     * setterメソッドによるコメント情報を格納する場合。
     * <p>{@code set + <フィールド名> + Comments}のメソッド名</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return コメント情報の設定用クラス
     */
    private Optional<MapCommentSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String commentMethodName = "set" + Utils.capitalize(fieldName) + "Comment";
        
        try {
            final Method method = beanClass.getDeclaredMethod(commentMethodName, String.class, String.class);
            method.setAccessible(true);
            
            return Optional.of(new MapCommentSetter() {
                
                
                @Override
                public void set(final Object beanObj, final String comment, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                    try {
                        method.invoke(beanObj, key, comment);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access comment field.", e);
                    }
                    
                }
            });
            
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        return Optional.empty();
        
    }
    
    /**
     * フィールドによるコメント情報を格納する場合。
     * <p>{@code <フィールド名> + Comment}のメソッド名</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return コメント情報の設定用クラス
     */
    private Optional<MapCommentSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String commentFieldName = fieldName + "Comment";
        
        final Field commentField;
        try {
            commentField = beanClass.getDeclaredField(commentFieldName);
            commentField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(commentField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) commentField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(String.class)) {
            
            return Optional.of(new MapCommentSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String comment, final String key) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                   try {
                       Map<String, String> commentMapObj = (Map<String, String>) commentField.get(beanObj);
                       if(commentMapObj == null) {
                           commentMapObj = new LinkedHashMap<>();
                           commentField.set(beanObj, commentMapObj);
                       }
                       
                       
                       commentMapObj.put(key, comment);
                       
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access comment field.", e);
                    }
                }
            });
            
        }
        
        return Optional.empty();
    }
}
