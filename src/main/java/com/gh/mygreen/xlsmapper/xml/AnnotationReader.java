package com.gh.mygreen.xlsmapper.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;
import com.gh.mygreen.xlsmapper.xml.bind.ClassInfo;
import com.gh.mygreen.xlsmapper.xml.bind.FieldInfo;
import com.gh.mygreen.xlsmapper.xml.bind.MethodInfo;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;


/**
 * フィールド、メソッドのアノテーションへアクセスするためのクラス。
 * <p>Javaソースに直接アノテーションを付与する場合と、XMLで定義する方法の両方をサポートする。
 * 
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class AnnotationReader {
    
    /**
     * XMLで定義した情報。
     * nullでもよい。
     */
    private final AnnotationMappingInfo xmlInfo;
    
    /**
     * アノテーションを動的に組み立てるクラス。
     */
    private DynamicAnnotationBuilder annotationBuilder = DynamicAnnotationBuilder.getInstance();
    
    /**
     * XMLで定義した情報を指定するコンストラクタ。
     * @param xmlInfo XMLで定義したアノテーションの情報。{@link XmlIO}で読み込んで取得した値。指定しない場合はnull。
     */
    public AnnotationReader(final AnnotationMappingInfo xmlInfo) {
        this.xmlInfo = xmlInfo;
    }
    
    /**
     * Returns all class annotations.
     *
     * @param clazz the target class
     * @return all annotations present on target class
     * @throws AnnotationReadException 
     */
    public Annotation[] getAnnotations(final Class<?> clazz) throws AnnotationReadException {
        
        if(xmlInfo != null && xmlInfo.containsClassInfo(clazz.getName())) {
            final ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            final Map<String, Annotation> map = new HashMap<>();
            
            if(classInfo.isOverride()) {
                for(Annotation ann : clazz.getAnnotations()) {
                    map.put(ann.annotationType().getName(), ann);
                }
            }
            
            for(AnnotationInfo annInfo: classInfo.getAnnotationInfos()) {
                try {
                    map.put(annInfo.getClassName(),
                            annotationBuilder.buildAnnotation(Class.forName(annInfo.getClassName()), annInfo));
                } catch (ClassNotFoundException e) {
                    throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getClassName()), e);
                }
            }
            
            return map.values().toArray(new Annotation[map.size()]);
        }
        
        return clazz.getAnnotations();
    }
    
    /**
     * Returns a class annotation for the specified type if such an annotation is present, else null.
     *
     * @param <A> the type of the annotation
     * @param clazz the target class
     * @param annClass the Class object corresponding to the annotation type
     * @return the target class's annotation for the specified annotation type if present on this element, else null
     * @throws AnnotationReadException 
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(final Class<?> clazz, final Class<A> annClass) throws AnnotationReadException {
        if(xmlInfo != null && xmlInfo.containsClassInfo(clazz.getName())) {
            final ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            
            if(classInfo.containsAnnotationInfo(annClass.getName())) {
                AnnotationInfo annInfo = classInfo.getAnnotationInfo(annClass.getName());
                try {
                    return (A)annotationBuilder.buildAnnotation(Class.forName(annInfo.getClassName()), annInfo);
                } catch (ClassNotFoundException e) {
                    throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getClassName()), e);
                }
            }
        }
        
        return clazz.getAnnotation(annClass);
    }
    
    /**
     * メソッドに付与された指定したアノテーションを取得する。
     * 
     * @param method 取得対象のメソッド。
     * @param annClas 取得対象のアノテーションのタイプ。
     * @return
     * @throws AnnotationReadException
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(final Method method, final Class<A> annClas) throws AnnotationReadException {
        
        final Class<?> clazz = method.getDeclaringClass();
        
        if(xmlInfo != null && xmlInfo.containsClassInfo(clazz.getName())) {
            final ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            
            if(classInfo.containsMethodInfo(method.getName())) {
                MethodInfo methodInfo = classInfo.getMethodInfo(method.getName());
                if(methodInfo != null && methodInfo.containsAnnotationInfo(annClas.getName())) {
                    AnnotationInfo annInfo = methodInfo.getAnnotationInfo(annClas.getName());
                    try {
                        return (A)annotationBuilder.buildAnnotation(Class.forName(annInfo.getClassName()), annInfo);
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getClassName()), e);
                    }
                }
            }
        }
        return method.getAnnotation(annClas);
    }
    
    /**
     * メソッドに付与されたアノテーションを持つか判定します。
     * @since 2.0
     * @param method 判定対象のメソッド
     * @param annClass アノテーションのタイプ
     * @return trueの場合、アノテーションを持ちます。
     */
    public <A extends Annotation> boolean hasAnnotation(final Method method, final Class<A> annClass) {
        return getAnnotation(method, annClass) != null;
    }
    
    /**
     * メソッドに付与されたアノテーションを全て取得する。
     * 
     * @param method 取得対象のメソッド。
     * @return 取得対象のアノテーションのタイプ。
     * @throws AnnotationReadException
     */
    public Annotation[] getAnnotations(final Method method) throws AnnotationReadException {
        
        final Class<?> clazz = method.getDeclaringClass();
        
        if(xmlInfo != null && xmlInfo.containsClassInfo(clazz.getName())) {
            final ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            
            if(classInfo.containsMethodInfo(method.getName())) {
                final MethodInfo methodInfo = classInfo.getMethodInfo(method.getName());
                final Map<String, Annotation> map = new HashMap<>();
                
                if(methodInfo.isOverride()) {
                    for(Annotation ann : method.getAnnotations()) {
                        map.put(ann.annotationType().getName(), ann);
                    }
                }
                
                for(AnnotationInfo annInfo: methodInfo.getAnnotationInfos()){
                    try {
                        map.put(annInfo.getClassName(),
                                annotationBuilder.buildAnnotation(Class.forName(annInfo.getClassName()), annInfo));
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getClassName()), e);
                    }
                }
                
                return map.values().toArray(new Annotation[map.size()]);
            }
        }
        return method.getAnnotations();
    }
    
    /**
     * フィールドに付与されたアノテーションを指定して取得する。
     * @param field 取得対象のフィールド
     * @param annClass アノテーションのタイプ
     * @return
     * @throws AnnotationReadException
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(final Field field, final Class<A> annClass) throws AnnotationReadException {
        
        final Class<?> clazz = field.getDeclaringClass();
        
        if(xmlInfo != null && xmlInfo.containsClassInfo(clazz.getName())) {
            final ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            
            if(classInfo.containsFieldInfo(field.getName())) {
                FieldInfo fieldInfo = classInfo.getFieldInfo(field.getName());
                if(fieldInfo != null && fieldInfo.containsAnnotationInfo(annClass.getName())){
                    AnnotationInfo annInfo = fieldInfo.getAnnotationInfo(annClass.getName());
                    try {
                        return (A)annotationBuilder.buildAnnotation(Class.forName(annInfo.getClassName()), annInfo);
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getClassName()), e);
                    }
                }
            }
        }
        return field.getAnnotation(annClass);
    }
    
    /**
     * フィールドに付与されたアノテーションを持つか判定します。
     * @since 2.0
     * @param field 判定対象のフィールド
     * @param annClass アノテーションのタイプ
     * @return trueの場合、アノテーションを持ちます。
     */
    public <A extends Annotation> boolean hasAnnotation(final Field field, final Class<A> annClass) {
        return getAnnotation(field, annClass) != null;
    }
    
    /**
     * フィールドに付与されたアノテーションを全て取得する。
     * @param field 取得対象のフィールド
     * @return
     * @throws AnnotationReadException
     */
    public Annotation[] getAnnotations(final Field field) throws AnnotationReadException {
        
        final Class<?> clazz = field.getDeclaringClass();
        
        if(xmlInfo != null && xmlInfo.containsClassInfo(clazz.getName())) {
            final ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            
            if(classInfo.containsFieldInfo(field.getName())) {
                final FieldInfo fieldInfo = classInfo.getFieldInfo(field.getName());
                final Map<String, Annotation> map = new HashMap<>();
                
                if(fieldInfo.isOverride()) {
                    for(Annotation ann : field.getAnnotations()) {
                        map.put(ann.annotationType().getName(), ann);
                    }
                }
                
                for(AnnotationInfo annInfo: fieldInfo.getAnnotationInfos()){
                    try {
                        map.put(annInfo.getClassName(), 
                                annotationBuilder.buildAnnotation(Class.forName(annInfo.getClassName()), annInfo));
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getClassName()), e);
                    }
                }
                return map.values().toArray(new Annotation[map.size()]);
            }
        }
        return field.getAnnotations();
    }
    
    public DynamicAnnotationBuilder getAnnotationBuilder() {
        return annotationBuilder;
    }
    
    public void setAnnotationBuilder(DynamicAnnotationBuilder annotationBuilder) {
        this.annotationBuilder = annotationBuilder;
    }
    
}
