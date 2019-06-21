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
 * {@link ArrayCommentSetter}のインスタンスを作成する
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class ArrayCommentSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(ArrayCommentSetterFactory.class);
    
    /**
     * フィールドの位置情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return ラベル情報のsetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<ArrayCommentSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map commentsの場合
        Optional<ArrayCommentSetter> arrayCommentSetter = createMapField(beanClass, fieldName);
        if(arrayCommentSetter.isPresent()) {
            return arrayCommentSetter;
        }
        
        // setter メソッドの場合
        arrayCommentSetter = createMethod(beanClass, fieldName);
        if(arrayCommentSetter.isPresent()) {
            return arrayCommentSetter;
        }
        
        // フィールド + commentの場合
        arrayCommentSetter = createField(beanClass, fieldName);
        if(arrayCommentSetter.isPresent()) {
            return arrayCommentSetter;
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
    private Optional<ArrayCommentSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
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
            return Optional.of(new ArrayCommentSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String comment, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                    try {
                        Map<String, String> commentsMapObj = (Map<String, String>) commentsField.get(beanObj);
                        if(commentsMapObj == null) {
                            commentsMapObj = new LinkedHashMap<>();
                            commentsField.set(beanObj, commentsMapObj);
                        }
                        
                        final String mapKey = createMapKey(fieldName, index);
                        
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
     * setterメソッドによるラベル情報を格納する場合。
     * <p>{@code set + <フィールド名> + Comments}のメソッド名</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<ArrayCommentSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String commentMethodName = "set" + Utils.capitalize(fieldName) + "Comment";
        
        try {
            final Method method = beanClass.getDeclaredMethod(commentMethodName, Integer.TYPE, String.class);
            method.setAccessible(true);
            
            return Optional.of(new ArrayCommentSetter() {
                
                
                @Override
                public void set(final Object beanObj, final String comment, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                    try {
                        method.invoke(beanObj, index, comment);
                        
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
     * フィールドによるラベル情報を格納する場合。
     * <p>{@code <フィールド名> + Comment}のメソッド名</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return ラベル情報の設定用クラス
     */
    private Optional<ArrayCommentSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String commentFieldName = fieldName + "Comment";
        
        final Field commentField;
        try {
            commentField = beanClass.getDeclaredField(commentFieldName);
            commentField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(!List.class.isAssignableFrom(commentField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) commentField.getGenericType();
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[0];
        
        if(valueType.equals(String.class)) {
            
            return Optional.of(new ArrayCommentSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String comment, final int index) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                   try {
                       List<String> commentListObj = (List<String>) commentField.get(beanObj);
                       if(commentListObj == null) {
                           commentListObj = new ArrayList<>();
                           commentField.set(beanObj, commentListObj);
                       }
                       
                       Utils.addListWithIndex(commentListObj, comment, index);
                       
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access comment field.", e);
                    }
                }
            });
            
        }
        
        return Optional.empty();
    }
}
