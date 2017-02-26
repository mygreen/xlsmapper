package com.gh.mygreen.xlsmapper.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link FieldAdapter}を組み立てるクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAdapterBuilder {
    
    private static Logger log = LoggerFactory.getLogger(FieldAdapterBuilder.class);
    
    private AnnotationReader annoReader;
    
    /**
     * コンストラクタ
     * @param annoReader XMLで定義したアノテーション情報を提供するクラス。
     * @throws NullPointerException {@literal annoReader == null.}
     */
    public FieldAdapterBuilder(final AnnotationReader annoReader) {
        ArgUtils.notNull(annoReader, "annoReader");
        
        this.annoReader = annoReader;
    }
    
    /**
     * フィールド情報を元にインスタンスを作成する。
     * @param field フィールド
     * @return フィールド情報を元に組み立てられたインスタンス。
     * @throws NullPointerException {@literal field == null
     */
    public FieldAdapter of(final Field field) {
        
        ArgUtils.notNull(field, "field");
        
        final FieldAdapter adapter = new FieldAdapter();
        
        // 共通情報の設定
        adapter.name = field.getName();
        adapter.targetType = field.getType();
        adapter.declaringClass = field.getDeclaringClass();
        
        // フィールド情報の設定
        setupWithFiled(adapter, field);
        
        // getter情報の設定
        if(ClassUtils.isPrimitiveBoolean(adapter.getType())) {
            ClassUtils.extractBooleanGetter(adapter.getDeclaringClass(), adapter.getName())
                    .ifPresent(getter -> setupWithGetter(adapter, getter));
            
        } else {
            ClassUtils.extractGetter(adapter.getDeclaringClass(), adapter.getName(), adapter.getType())
                    .ifPresent(getter -> setupWithGetter(adapter, getter));
        }
        
        // setter情報の設定
        ClassUtils.extractSetter(adapter.getDeclaringClass(), adapter.getName(), adapter.getType())
                .ifPresent(setter -> setupWithSetter(adapter, setter));
        
        // コンポーネントタイプの設定
        if(Collection.class.isAssignableFrom(adapter.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            adapter.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[0]);
            
        } else if(adapter.getType().isArray()) {
            adapter.componentType = Optional.of(adapter.getType().getComponentType());
            
        } else if(Map.class.isAssignableFrom(adapter.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            adapter.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[1]);
        }
        
        return adapter;
    }
    
    /**
     * メソッド情報を元にインスタンスを作成する。
     * @param method メソッド情報
     * @return メソッド情報を元に組み立てられたインスタンス。
     * @throws NullPointerException {@literal method == null
     * @throws IllegalArgumentException {@literal methodの名称がsetterまたはgetterの書式でない場合。}
     */
    public FieldAdapter of(final Method method) {
        
        ArgUtils.notNull(method, "method");
        
        final FieldAdapter adapter = new FieldAdapter();
        
        final String methodName = method.getName();
        if(ClassUtils.isGetterMethod(method) || ClassUtils.isBooleanGetterMethod(method)) {
            final String propertyName;
            if(methodName.startsWith("get")) { 
                propertyName = Utils.uncapitalize(methodName.substring(3));
            } else {
                propertyName = Utils.uncapitalize(methodName.substring(2));
            }
            
            // 共通情報の設定
            adapter.name = propertyName;
            adapter.targetType = method.getReturnType();
            adapter.declaringClass = method.getDeclaringClass();
            
            // getter情報の設定
            setupWithGetter(adapter, method);
            
            // フィールド情報の設定
            ClassUtils.extractField(adapter.getDeclaringClass(), adapter.getName(), adapter.getType())
                    .ifPresent(field -> setupWithFiled(adapter, field));
            
            // setter情報の設定
            ClassUtils.extractSetter(adapter.getDeclaringClass(), adapter.getName(), adapter.getType())
                    .ifPresent(setter -> setupWithSetter(adapter, setter));
            
            // コンポーネントタイプの設定
            if(Collection.class.isAssignableFrom(adapter.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                adapter.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[0]);
                
            } else if(adapter.getType().isArray()) {
                adapter.componentType = Optional.of(adapter.getType().getComponentType());
                
            } else if(Map.class.isAssignableFrom(adapter.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                adapter.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[1]);
            }
            
        } else if(ClassUtils.isSetterMethod(method)) {
            final String propertyName = Utils.uncapitalize(methodName.substring(3));
            
            // 共通情報の設定
            adapter.name = propertyName;
            adapter.targetType = method.getParameterTypes()[0];
            adapter.declaringClass = method.getDeclaringClass();
            
            // setter情報の設定
            setupWithSetter(adapter, method);
            
            // フィールド情報の設定
            ClassUtils.extractField(adapter.getDeclaringClass(), adapter.getName(), adapter.getType())
                    .ifPresent(field -> setupWithFiled(adapter, field));
            
            // getter情報の設定
            if(ClassUtils.isPrimitiveBoolean(adapter.getType())) {
                ClassUtils.extractBooleanGetter(adapter.getDeclaringClass(), adapter.getName())
                        .ifPresent(getter -> setupWithGetter(adapter, getter));
                
            } else {
                ClassUtils.extractGetter(adapter.getDeclaringClass(), adapter.getName(), adapter.getType())
                        .ifPresent(getter -> setupWithGetter(adapter, getter));
            }
            
            // コンポーネントタイプの設定
            if(Collection.class.isAssignableFrom(adapter.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[0];
                adapter.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[0]);
                
            } else if(adapter.getType().isArray()) {
                adapter.componentType = Optional.of(adapter.getType().getComponentType());
                
            } else if(Map.class.isAssignableFrom(adapter.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[0];
                adapter.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[1]);
            }
            
        } else {
            throw new IllegalArgumentException(MessageBuilder.create("method.noAccessor")
                    .varWithClass("className", method.getDeclaringClass())
                    .var("methodName", methodName)
                    .format());
        }
        
        return adapter;
    }
    
    private void setupWithFiled(final FieldAdapter adapter, final Field field) {
        
        adapter.targetField = Optional.of(field);
        
        final Annotation[] annos = annoReader.getAnnotations(field);
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }
            
            final Class<? extends Annotation> annoClass = anno.annotationType();
            
            if(adapter.annotationMap.containsKey(annoClass)) {
                final String message = MessageBuilder.create("anno.duplicated")
                        .varWithClass("classType", adapter.getDeclaringClass())
                        .var("property", field.getName())
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
            }
            
            adapter.annotationMap.put(annoClass, anno);
        }
        
        
    }
    
    private void setupWithGetter(final FieldAdapter adapter, final Method method) {
        
        adapter.targetGetter = Optional.of(method);
        
        final Annotation[] annos = annoReader.getAnnotations(method);
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }
            
            final Class<? extends Annotation> annoClass = anno.annotationType();
            
            if(adapter.annotationMap.containsKey(annoClass)) {
                final String message = MessageBuilder.create("anno.duplicated")
                        .varWithClass("classType", adapter.getDeclaringClass())
                        .var("property", method.getName() + "()")
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
            }
            
            adapter.annotationMap.put(annoClass, anno);
        }
    }
    
    private void setupWithSetter(final FieldAdapter adapter, final Method method) {
        
        adapter.targetSetter = Optional.of(method);
        
        final Annotation[] annos = annoReader.getAnnotations(method);
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }
            
            final Class<? extends Annotation> annoClass = anno.annotationType();
            
            if(adapter.annotationMap.containsKey(annoClass)) {
                final String message = MessageBuilder.create("anno.duplicated")
                        .varWithClass("classType", adapter.getDeclaringClass())
                        .var("property", method.getName() + "(...)")
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
            }
            
            adapter.annotationMap.put(annoClass, anno);
        }
    }
    
    /**
     * サポートするアノテーションか判定する。
     * <p>確実に重複するJava標準のアノテーションは除外するようにします。</p>
     * 
     * @param anno 判定対象のアノテーション
     * @return tureの場合、サポートします。
     */
    private boolean isSupportedAnnotation(final Annotation anno) {
        
        final String name = anno.annotationType().getName();
        if(name.startsWith("java.lang.annotation.")) {
            return false;
        }
        
        return true;
    }
    
}
