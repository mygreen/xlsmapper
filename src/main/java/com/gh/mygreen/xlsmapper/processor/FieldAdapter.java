package com.gh.mygreen.xlsmapper.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * メソッド（setter/getter）とフィールドのアクセスを吸収するクラス。
 * <p>インスタンスは、{@link FieldAdapterBuilder}から作成します。</p>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAdapter {
    
    /**
     * フィールド（プロパティ）の名称
     */
    String name;
    
    /**
     * フィールドやメソッドが定義されているクラス
     */
    Class<?> declaringClass;
    
    /**
     * フィールドのクラスタイプ
     */
    Class<?> targetType;
    
    /**
     * フィールド情報
     */
    Optional<Field> targetField = Optional.empty();
    
    /**
     * Getterメソッド
     */
    Optional<Method> targetGetter = Optional.empty();
    
    /**
     * Setterメソッド
     */
    Optional<Method> targetSetter = Optional.empty();
    
    /**
     * フィールドのタイプがListや配列の時の要素のクラスタイプ
     */
    Optional<Class<?>> componentType = Optional.empty();
    
    /** 
     * アノテーションの情報
     */
    Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
    
    FieldAdapter() {
        
    }
    
    /**
     * フィールドとメソッドに同じアノテーションが付与されているときに重複を除外するための判定に使用する。
     * そのため、{@link FieldAdapter#getNameWithClass()}が等しいかで判定します。
     * 
     */
    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof FieldAdapter)) {
            return false;
        }
        
        final FieldAdapter target = (FieldAdapter)obj;
        
        // クラス名#フィールドの比較
        if(!getNameWithClass().equals(target.getNameWithClass())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 読み込み可能かどうか判定します。
     * @return フィールドまたはgetterメソッドが存在する場合にtrueを返します。
     */
    public boolean isReadable() {
        
        return targetField.isPresent() || targetGetter.isPresent();
        
    }
    
    /**
     * 書き込み可能かどうか判定します。
     * @return フィールドまたはsetterメソッドが存在する場合にtrueを返します。
     */
    public boolean isWritable() {
        
        return targetField.isPresent() || targetSetter.isPresent();
    }
    
    /**
     * オブジェクトのフィールドの値を取得します。
     * <p>getterが存在する場合は、getterメソッド経由で取得します。</p>
     * @param targetObj オブジェクト（インスタンス）
     * @return フィールドの値。
     * @throws NullPointerException {@literal targetObj == null.}
     * @throws FieldAdapterException {@literal 値の取得に失敗した場合。}
     */
    public Object getValue(final Object targetObj) {
        
        ArgUtils.notNull(targetObj, "targetObj");
        
        if(targetGetter.isPresent()) {
            try {
                return targetGetter.get().invoke(targetObj);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new FieldAdapterException(this, "fail getter value", e);
            }
            
        } else if(targetField.isPresent()) {
            try {
                return targetField.get().get(targetObj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new FieldAdapterException(this, "fail get field value", e);
            }
            
        } else {
            throw new FieldAdapterException(this, "not found getter method or field.");
        }
        
    }
    
    /**
     * オブジェクトのフィールドに値を設定する。
     * <p>setterが存在する場合は、setterメソッド経由で値を設定します。</p>
     * @param targetObj オブジェクト（インスタンス）
     * @param value フィールドの値。
     * @throws NullPointerException {@literal targetObj == null.}
     * @throws FieldAdapterException {@literal 値の設定に失敗した場合。}
     * 
     */
    public void setValue(final Object targetObj, final Object value) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        if(targetSetter.isPresent()) {
            try {
                targetSetter.get().invoke(targetObj, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new FieldAdapterException(this, "fail setter value", e);
            }
            
        } else if(targetField.isPresent()) {
            try {
                targetField.get().set(targetObj, value);
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new FieldAdapterException(this, "fail setter field value", e);
            }
            
        } else {
            throw new FieldAdapterException(this, "not found setter method or field.");
        }
    }
    
    /**
     * フィールドがマップ形式の場合に、キーを指定して値を取得する。
     * 
     * @param key マップキーの値
     * @param targetObj オブジェクト（インスタンス）
     * @return マップの値
     * @throws NullPointerException {@literal targetObj == null.}
     * @throws IllegalStateException {@link フィールドのタイプがMap出ない場合}
     */
    @SuppressWarnings("unchecked")
    public Object getValueOfMap(final Object key, final Object targetObj) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        if(!Map.class.isAssignableFrom(targetType)) {
            throw new IllegalStateException("this method cannot call Map. This target type is " + targetType.getName());
        }
        
        final Map<Object, Object> map = (Map<Object, Object>) getValue(targetObj);
        if(map == null) {
            return null;
        }
        
        return map.get(key);
        
    }
    
    /**
     * アノテーションの一覧を取得します。
     * @return 付与されているアノテーションの一覧
     */
    public Collection<? extends Annotation> getAnnotations() {
        return annotationMap.values();
    }
    
    /**
     * 指定したアノテーションを持つか判定します。
     * @param annoClass アノテーションのクラスタイプ。
     * @return trueの場合、アノテーションを持ちます。
     */
    public <A extends Annotation> boolean hasAnnotation(final Class<A> annoClass) {
        return annotationMap.containsKey(annoClass);
    }
    
    /**
     * タイプを指定して、アノテーションを取得する。
     * @param annoClass アノテーションのクラスタイプ。
     * @return 存在しない場合、空を返します。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annoClass) {
        return Optional.ofNullable((A)annotationMap.get(annoClass));
    }
    
    /**
     * フィールドの名称を取得します。
     * @return フィールドの名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * フィールドのタイプを取得します。
     * @return フィールドのクラスタイプ
     */
    public Class<?> getType() {
        
        return targetType;
    }
    
    /**
     * フィールドのタイプがListや配列の時の要素のGenericsのクラスタイプを取得します。
     * @return クラスタイプ。
     * @throws NoSuchElementException {@literal Genericsの指定がないとき、またはサポートしていないクラスタイプの場合}
     */
    public Class<?> getComponentType() {
        return componentType.get();
    }
    
    /**
     * フィールド情報を取得します。
     * @return フィールドを持たない場合は、空を返します。
     */
    public Optional<Field> getField() {
        return targetField;
    }
    
    /**
     * Getterメソッドを取得します。
     * @return getterメソッドを持たない場合は、空を返します。
     */
    public Optional<Method> getGetter() {
        return targetGetter;
    }
    
    /**
     * Setterメソッドを取得します。
     * @return setterメソッドを持たない場合は、空を返します。
     */
    public Optional<Method> getSetter() {
        return targetSetter;
    }
    
    /**
     * フィールドが定義されているクラス情報を取得します。
     * @return フィールドが定義されているクラス情報
     */
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }
    
    /**
     * クラス名付きのフィールド名称を取得する。
     * @since 1.4
     * @return {@literal <クラス名#フィールド名>}
     */
    public String getNameWithClass() {
        return getDeclaringClass().getName() + "#" + getName();
    }
    
}
