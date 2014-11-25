package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * メソッド（setter/getter）とフィールドのアクセスを吸収するクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class FieldAdaptor {
    
    /** フィールドの名称 */
    private String name;
    
    /** フィールドの情報 */
    private Field targetField;
    
    /** Getterメソッドの情報 */
    private Method targetGetter;
    
    /** Setterメソッドの情報 */
    private Method targetSetter;
    
    /** フィールド優先かどうか */
    private final boolean withField;
    
    /** フィールドのアノテーション情報 */
    private Map<Class<? extends Annotation>, Annotation> fieldAnnotationMap;
    
    /** getterメソッドのアノテーション情報 */
    private Map<Class<? extends Annotation>, Annotation> getterAnnotationMap;
    
    /** setterメソッドのアノテーション情報 */
    private Map<Class<? extends Annotation>, Annotation> setterAnnotationMap;
    
    /** クラスタイプ */
    private Class<?> targetClass;
    
    /** 親のクラスタイプ */
    private Class<?> parentClass;
    
    /**
     * フィールド情報を指定して初期化する。
     * @param clazz
     * @param field
     * @param reader 別定義のアノテーション情報（利用しない場合はnullを設定）
     */
    public FieldAdaptor(final Class<?> clazz, final Field field, final AnnotationReader reader) {
        
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(field, "field");
        
        this.parentClass = clazz;
        this.withField = true;
        
        initWithField(clazz, field, reader);

    }
    
    /**
     * フィールド情報を指定して初期化する。
     * @param clazz
     * @param field
     */
    public FieldAdaptor(final Class<?> clazz, final Field field) {
        this(clazz, field, null);
    }
    
    /**
     * Getter/Setterメソッド情報を指定して比較する。
     * @param clazz
     * @param method
     * @param reader 別定義のアノテーション情報（利用しない場合はnullを設定）
     * @throws IllegalStateException setter/getter以外のメソッドを指定した場合。
     */
    public FieldAdaptor(final Class<?> clazz, final Method method, final AnnotationReader reader) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(method, "method");
        
        this.parentClass = clazz;
        this.withField = false;
        
        if(method.getName().startsWith("get")) {
            initWithGetter(clazz, method, reader);
        } else if(Utils.isBooleanGetterMethod(method)) {
            initWithBooleanGetter(clazz, method, reader);
        } else if(method.getName().startsWith("set")) {
            initWithSetter(clazz, method, reader);
        } else {
            throw new IllegalStateException(
                    String.format("method name '%s' should start with 'get' or 'set'", method.getName()));
        }
    }
    
    
    /**
     * Getter/Setterメソッド情報を指定して比較する。
     * @param clazz
     * @param method
     */
    public FieldAdaptor(final Class<?> clazz, final Method method) {
        this(clazz, method, null);
    }
    
    private void initWithField(final Class<?> clazz, final Field field, final AnnotationReader reader) {
        field.setAccessible(true);
        this.targetField = field;
        this.targetClass = field.getType();
        
        this.name = targetField.getName();
        
        final String fieldName = field.getName();
        this.targetGetter = Utils.getGetter(clazz, fieldName);
        if(getTargetGetter() == null && Utils.isBooleanField(field)) {
            this.targetGetter = Utils.getBooleanGetter(clazz, fieldName);
        }
        this.targetSetter = Utils.getSetter(clazz, fieldName, field.getType());
        
        initAnnotation(reader);
        
    }
    
    private void initWithGetter(final Class<?> clazz, final Method method, final AnnotationReader reader) {
        
        this.targetGetter = method;
        this.targetClass = method.getReturnType();
        
        final String fieldName = Utils.uncapitalize(method.getName().substring(3));
        this.name = fieldName;
        this.targetField = Utils.getField(clazz, fieldName);
        this.targetSetter = Utils.getSetter(clazz, fieldName, method.getReturnType());
        
        initAnnotation(reader);
    }
    
    private void initWithBooleanGetter(final Class<?> clazz, final Method method, final AnnotationReader reader) {
        
        this.targetGetter = method;
        this.targetClass = method.getReturnType();
        
        final String fieldName = Utils.uncapitalize(method.getName().substring(3));
        this.name = fieldName;
        this.targetField = Utils.getField(clazz, fieldName);
        this.targetSetter = Utils.getSetter(clazz, fieldName, method.getReturnType());
        
        initAnnotation(reader);
    }
    
    private void initWithSetter(final Class<?> clazz, final Method method, final AnnotationReader reader) {
        
        this.targetSetter = method;
        this.targetClass = method.getParameterTypes()[0];
        
        final String fieldName = Utils.uncapitalize(method.getName().substring(3));
        this.name = fieldName;
        this.targetField = Utils.getField(clazz, fieldName);
        this.targetGetter = Utils.getGetter(clazz, fieldName);
        if(getTargetGetter() == null && Utils.isPrimitiveBoolean(getTargetClass())) {
            this.targetGetter = Utils.getBooleanGetter(clazz, fieldName);
        }
        
        initAnnotation(reader);
    }
    
    private void initAnnotation(final AnnotationReader reader) {
        if(reader != null) {
            try {
                if(targetField != null) {
                    this.fieldAnnotationMap = createAnnotationMap(reader.getAnnotations(targetField.getDeclaringClass(), targetField));
                } else {
                    this.fieldAnnotationMap = new LinkedHashMap<>();
                }
                
                if(targetGetter != null) {
                    this.getterAnnotationMap = createAnnotationMap(reader.getAnnotations(targetGetter.getDeclaringClass(), targetGetter));
                } else {
                    this.getterAnnotationMap = new LinkedHashMap<>();
                }
                
                if(targetSetter != null) {
                    this.setterAnnotationMap = createAnnotationMap(reader.getAnnotations(targetSetter.getDeclaringClass(), targetSetter));
                } else {
                    this.setterAnnotationMap = new LinkedHashMap<>();
                }
                
            } catch(Exception e) {
                throw new IllegalStateException("fail load annotations", e);
            }
        } else {
            if(targetField != null) {
                this.fieldAnnotationMap = createAnnotationMap(targetField.getAnnotations());
            } else {
                this.fieldAnnotationMap = new LinkedHashMap<>();
            }
            
            if(targetGetter != null) {
                this.getterAnnotationMap = createAnnotationMap(targetGetter.getAnnotations());
            } else {
                this.getterAnnotationMap = new LinkedHashMap<>();
            }
            
            if(targetSetter != null) {
                this.setterAnnotationMap = createAnnotationMap(targetSetter.getAnnotations());
            } else {
                this.setterAnnotationMap = new LinkedHashMap<>();
            }
        }
    }
    
    private Map<Class<? extends Annotation>, Annotation> createAnnotationMap(Annotation[] annos) {
        final Map<Class<? extends Annotation>, Annotation> map = new LinkedHashMap<>();
        
        for(Annotation anno : annos) {
            map.put(anno.annotationType(), anno);
        }
        
        return map;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if(obj == null) {
            return false;
        }
        
        if(obj == this) {
            return true;
        }
        
        if(obj.getClass() != getClass()) {
            return false;
        }
        
        final FieldAdaptor rhs = (FieldAdaptor) obj;
        
        if(!this.getTargetClass().equals(rhs.getTargetClass())) {
            return false;
            
        } else if(!this.getName().equals(rhs.getName())) {
            return false;
            
        } else if(Utils.notEquals(this.targetField, rhs.targetField)) {
            return false;
            
        } else if(Utils.notEquals(this.targetSetter, rhs.targetSetter)) {
            return false;
            
        } else if(Utils.notEquals(this.targetGetter, rhs.targetGetter)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * オブジェクトのフィールド値を取得する。
     * @param targetObj
     * @return
     */
    public Object getValue(final Object targetObj) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        try {
            if(withField) {
                return targetField.get(targetObj);
            } else {
                return targetGetter.invoke(targetObj);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("fail get field value.");
        }
        
    }
    
    /**
     * マップ形式の場合に、キーを指定して値を取得する。
     * @param key
     * @param targetObj
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object getValueOfMap(final String key, final Object targetObj) {
        
        if(!Map.class.isAssignableFrom(targetClass)) {
            throw new IllegalStateException("this method cannot call Map. This target class is " + targetClass.getName());
        }
        
        final Map<String, Object> map = (Map<String, Object>) getValue(targetObj);
        if(map == null) {
            return null;
        }
        
        return map.get(key);
        
    }
    
    /**
     * オブジェクトのフィールドに値を設定する。
     * @param targetObj
     * @param value
     */
    public void setValue(final Object targetObj, final Object value) {
        ArgUtils.notNull(targetObj, "targetObj");
        
        try {
            if(withField) {
                targetField.set(targetObj, value);
            } else {
                targetSetter.invoke(targetObj, value);
            }
            
        }catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("fail get field value.");
        }
    }
    
    /**
     * 読み込み時用のアノテーションを取得する。
     * <p>setterなどから取得する
     * @return
     */
    public Collection<? extends Annotation> getLoadingAnnotations() {
        if(withField) {
            return fieldAnnotationMap.values();
        } else {
            return setterAnnotationMap.values();
        }
    }
    
    /**
     * アノテーションを指定して、読み込み時用のアノテーションを取得する。
     * <p>setterなどから取得する
     * @param annoClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getLoadingAnnotation(final Class<T> annoClass) {
        if(withField) {
            return (T) fieldAnnotationMap.get(annoClass);
        } else {
            return (T) setterAnnotationMap.get(annoClass);
        }
    }
    
    /**
     * 指定したアノテーションを保持するか判定する。
     * <p>setterなどから取得する。
     * @param annoClass
     * @return
     */
    public <T extends Annotation> boolean hasLoadingAnnotation(final Class<T> annoClass) {
        return getLoadingAnnotation(annoClass) != null;
    }
    
    /**
     * 書き込み時用のアノテーションを取得する。
     * <p>getterなどから取得する
     * @return
     */
    public Collection<? extends Annotation> getSavingAnnotations() {
        
        if(withField) {
            return fieldAnnotationMap.values();
        } else {
            return getterAnnotationMap.values();
        }
    }
    
    /**
     * アノテーションを指定して、下記っこみ時用のアノテーションを取得する。
     * <p>getterなどから取得する
     * @param annoClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getSavingAnnotation(final Class<T> annoClass) {
        if(withField) {
            return (T) fieldAnnotationMap.get(annoClass);
        } else {
            return (T) getterAnnotationMap.get(annoClass);
        }
    }
    
    /**
     * 指定したアノテーションを保持するか判定する。
     * <p>getterなどから取得する。
     * @param annoClass
     * @return
     */
    public <T extends Annotation> boolean hasSavingAnnotation(final Class<T> annoClass) {
        return getSavingAnnotation(annoClass) != null;
    }
    
    public Field getTargetField() {
        return targetField;
    }
    
    public Method getTargetGetter() {
        return targetGetter;
    }
    
    public Method getTargetSetter() {
        return targetSetter;
    }
    
    public Class<?> getTargetClass() {
        return targetClass;
    }
    
    /**
     * 読み込み時のGenericsのクラスタイプを取得する
     * <p>Genericsでない場合やサポート対象外のクラスの場合は、nullを返す。
     * <p>マップの場合は、valueの型を返す。
     * @return
     */
    public Class<?> getLoadingGenericClassType() {
        
        if(List.class.isAssignableFrom(targetClass)) {
            if(withField) {
                final ParameterizedType type = (ParameterizedType) targetField.getGenericType();
                return (Class<?>) type.getActualTypeArguments()[0];
                
            } else {
                final ParameterizedType type = (ParameterizedType) targetSetter.getGenericParameterTypes()[0];
                return (Class<?>) type.getActualTypeArguments()[0];
            }
            
        } else if(Set.class.isAssignableFrom(targetClass)) {
            if(withField) {
                final ParameterizedType type = (ParameterizedType) targetField.getGenericType();
                return (Class<?>) type.getActualTypeArguments()[0];
                
            } else {
                final ParameterizedType type = (ParameterizedType) targetSetter.getGenericParameterTypes()[0];
                return (Class<?>) type.getActualTypeArguments()[0];
            }
            
        } else if(targetClass.isArray()) {
            return targetClass.getComponentType();
            
        } else if(Map.class.isAssignableFrom(targetClass)) {
            
            if(withField) {
                final ParameterizedType type = (ParameterizedType) targetField.getGenericType();
                return (Class<?>) type.getActualTypeArguments()[1];
            } else {
                final ParameterizedType type = (ParameterizedType) targetSetter.getGenericParameterTypes()[0];
                return (Class<?>) type.getActualTypeArguments()[1];
            }
        }
        
        return null;
        
    }
    
    /**
     * 書き込み時のGenericsのクラスタイプを取得する
     * <p>Genericsでない場合やサポート対象外のクラスの場合は、nullを返す。
     * @return
     */
    public Class<?> getSavingGenericClassType() {
        
        if(List.class.isAssignableFrom(targetClass)) {
            if(withField) {
                ParameterizedType type = (ParameterizedType) targetField.getGenericType();
                return (Class<?>) type.getActualTypeArguments()[0];
                        
            } else {
                ParameterizedType type = (ParameterizedType) targetGetter.getGenericReturnType();
                return (Class<?>) type.getActualTypeArguments()[0];
            }
            
        } else if(List.class.isAssignableFrom(targetClass)) {
            if(withField) {
                ParameterizedType type = (ParameterizedType) targetField.getGenericType();
                return (Class<?>) type.getActualTypeArguments()[0];
                        
            } else {
                ParameterizedType type = (ParameterizedType) targetGetter.getGenericReturnType();
                return (Class<?>) type.getActualTypeArguments()[0];
            }
            
        } else if(targetClass.isArray()) {
            return targetClass.getComponentType();
            
        } else if(Map.class.isAssignableFrom(targetClass)) {
            if(withField) {
                final ParameterizedType type = (ParameterizedType) targetField.getGenericType();
                return (Class<?>) type.getActualTypeArguments()[1];
            } else {
                final ParameterizedType type = (ParameterizedType) targetGetter.getGenericParameterTypes()[0];
                return (Class<?>) type.getActualTypeArguments()[1];
            }
        }
        
        return null;
        
    }
    
    /**
     * フィールドの名称を取得する。
     * <p>getter / setterのメソッドの場合は、set / getなどは排除した名前を取得する。
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * フィールドが定義されているクラス情報を取得する。
     * @return
     */
    public Class<?> getDeclaringClass() {
        
        if(targetField != null) {
            return targetField.getDeclaringClass();
            
        } else if(targetSetter != null) {
            return targetSetter.getDeclaringClass();
        } else {
            return targetGetter.getDeclaringClass();
        }
        
    }
    
    /**
     * フィールドが定義されている親のクラスを取得する。
     * @return
     */
    public Class<?> getParentClass() {
        return parentClass;
    }
}
