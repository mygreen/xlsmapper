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
 * {@link CommentSetter}のインスタンスを作成する
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CommentSetterFactory {
    
    private static final Logger log = LoggerFactory.getLogger(CommentSetterFactory.class);
    
    /**
     * フィールドのコメント情報を設定するためのアクセッサを作成します。
     * @param beanClass フィールドが定義されているクラス情報
     * @param fieldName フィールドの名称
     * @return 位置情報のsetterが存在しない場合は空を返す。
     * @throws IllegalArgumentException {@literal beanClass == null or fieldName == null}
     * @throws IllegalArgumentException {@literal fieldName.isEmpty() = true}
     */
    public Optional<CommentSetter> create(final Class<?> beanClass, final String fieldName) {
        
        ArgUtils.notNull(beanClass, "beanClass");
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        // フィールド Map commentsの場合
        Optional<CommentSetter> CommentSetter = createMapField(beanClass, fieldName);
        if(CommentSetter.isPresent()) {
            return CommentSetter;
        }
        
        // setter メソッドの場合
        CommentSetter = createMethod(beanClass, fieldName);
        if(CommentSetter.isPresent()) {
            return CommentSetter;
        }
        
        // フィールド + commentの場合
        CommentSetter = createField(beanClass, fieldName);
        if(CommentSetter.isPresent()) {
            return CommentSetter;
        }
        
        
        return Optional.empty();
    }
    
    /**
     * {@link Map}フィールドにコメント情報が格納されている場合。
     * <p>キーはフィールド名。</p>
     * <p>マップの値は、Stringをサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return コメント情報の設定用クラス
     */
    private Optional<CommentSetter> createMapField(final Class<?> beanClass, final String fieldName) {
        
        final Field commentField;
        try {
            commentField = beanClass.getDeclaredField("comments");
            commentField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            // フィールドが見つからない場合は、何もしない。
            return Optional.empty();
        }
        
        if(!Map.class.isAssignableFrom(commentField.getType())) {
            return Optional.empty();
        }
        
        final ParameterizedType type = (ParameterizedType) commentField.getGenericType();
        final Class<?> keyType = (Class<?>) type.getActualTypeArguments()[0];
        final Class<?> valueType = (Class<?>) type.getActualTypeArguments()[1];
        
        if(keyType.equals(String.class) && valueType.equals(String.class)) {
            return Optional.of(new CommentSetter() {
                
                @SuppressWarnings("unchecked")
                @Override
                public void set(final Object beanObj, final String comment) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                    try {
                        Map<String, String> commentsMapObj = (Map<String, String>) commentField.get(beanObj);
                        if(commentsMapObj == null) {
                            commentsMapObj = new LinkedHashMap<>();
                            commentField.set(beanObj, commentsMapObj);
                        }
                        
                        commentsMapObj.put(fieldName, comment);
                        
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
     * <p>{@code set + <フィールド名> + Comment}のメソッド名</p>
     * <p>引数として、をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return コメント情報の設定用クラス
     */
    private Optional<CommentSetter> createMethod(final Class<?> beanClass, final String fieldName) {
        
        final String commentMethodName = "set" + Utils.capitalize(fieldName) + "Comment";
        
        try {
            final Method method = beanClass.getDeclaredMethod(commentMethodName, String.class);
            method.setAccessible(true);
            
            return Optional.of(new CommentSetter() {
                
                
                @Override
                public void set(final Object beanObj, final String comment) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notEmpty(comment, "comment");
                    
                    try {
                        method.invoke(beanObj, comment);
                        
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException("fail access comments field.", e);
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
     * <p>引数として、{@link CellPosition}、{@link Point}、 {@link org.apache.poi.ss.util.CellAddress}をサポートする。</p>
     * 
     * @param beanClass フィールドが定義してあるクラスのインスタンス
     * @param fieldName フィールド名
     * @return コメント情報の設定用クラス
     */
    private Optional<CommentSetter> createField(final Class<?> beanClass, final String fieldName) {
        
        final String commentFieldName = fieldName + "Comment";
        
        final Field commentField;
        try {
            commentField = beanClass.getDeclaredField(commentFieldName);
            commentField.setAccessible(true);
            
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        if(commentField.getType().equals(String.class)) {
            
            return Optional.of(new CommentSetter() {
                
                @Override
                public void set(final Object beanObj, final String comment) {
                    ArgUtils.notNull(beanObj, "beanObj");
                    ArgUtils.notNull(comment, "comment");
                    
                    try {
                        commentField.set(beanObj, comment);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("fail access comment field.", e);
                    }
                }
            });
            
        }
        
        return Optional.empty();
    }
}
