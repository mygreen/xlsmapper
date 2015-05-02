package com.gh.mygreen.xlsmapper.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;
import com.gh.mygreen.xlsmapper.xml.bind.ClassInfo;
import com.gh.mygreen.xlsmapper.xml.bind.FieldInfo;
import com.gh.mygreen.xlsmapper.xml.bind.MethodInfo;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;


/**
 * フィールド、メソッドのアノテーションへアクセスするためのクラス。
 * <p>Javaソースに直接アノテーションを付与する場合と、XMLで定義する方法の両方をサポートする。
 * 
 * @version 0.5
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class AnnotationReader {
    
    /**
     * XMLで定義した情報。
     * nullでもよい。
     */
    private XmlInfo xmlInfo;
    
    /**
     * アノテーションを動的に組み立てるクラス。
     */
    private DynamicAnnotationBuilder annotationBuilder = DynamicAnnotationBuilder.getInstance();
    
    /**
     * XMLで定義した情報を指定するコンストラクタ。
     * @param xmlInfo XMLで定義したアノテーションの情報。{@link XmlLoader}で読み込んで取得した値。指定しない場合はnull。
     */
    public AnnotationReader(final XmlInfo xmlInfo) {
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
        
        if(xmlInfo!=null && xmlInfo.getClassInfo(clazz.getName()) != null){
            ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            List<Annotation> list = new ArrayList<Annotation>();
            for(AnnotationInfo annInfo: classInfo.getAnnotationInfos()){
                try {
                    list.add(annotationBuilder.buildAnnotation(Class.forName(annInfo.getAnnotationClass()), annInfo));
                } catch (ClassNotFoundException e) {
                    throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getAnnotationClass()), e);
                }
            }
            return list.toArray(new Annotation[list.size()]);
        }
        return clazz.getAnnotations();
    }

    /**
     * Returns a class annotation for the specified type if such an annotation is present, else null.
     *
     * @param <A> the type of the annotation
     * @param clazz the target class
     * @param ann the Class object corresponding to the annotation type
     * @return the target class's annotation for the specified annotation type if present on this element, else null
     * @throws AnnotationReadException 
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A  getAnnotation(final Class<?> clazz, final Class<A> ann) throws AnnotationReadException {
        if(xmlInfo != null && xmlInfo.getClassInfo(clazz.getName()) != null){
            ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            if(classInfo.getAnnotationInfo(ann.getName()) != null){
                AnnotationInfo annInfo = classInfo.getAnnotationInfo(ann.getName());
                try {
                    return (A)annotationBuilder.buildAnnotation(Class.forName(annInfo.getAnnotationClass()), annInfo);
                } catch (ClassNotFoundException e) {
                    throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getAnnotationClass()), e);
                }
            }
        }
        return clazz.getAnnotation(ann);
    }
    
    /**
     * メソッドに付与された指定したアノテーションを取得する。
     * 
     * @param clazz 取得対象のクラス。
     * @param method 取得対象のメソッド。
     * @param ann 取得対象のアノテーションのタイプ。
     * @return
     * @throws AnnotationReadException
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(final Class<?> clazz, final Method method, final Class<A> ann) throws AnnotationReadException {
        if(xmlInfo != null && xmlInfo.getClassInfo(clazz.getName()) != null){
            ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            if(classInfo.getMethodInfo(method.getName())!=null){
                MethodInfo methodInfo = classInfo.getMethodInfo(method.getName());
                if(methodInfo!=null && methodInfo.getAnnotationInfo(ann.getName())!=null){
                    AnnotationInfo annInfo = methodInfo.getAnnotationInfo(ann.getName());
                    try {
                        return (A)annotationBuilder.buildAnnotation(Class.forName(annInfo.getAnnotationClass()), annInfo);
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getAnnotationClass()), e);
                    }
                }
            }
        }
        return method.getAnnotation(ann);
    }
    
    /**
     * メソッドに付与されたアノテーションを全て取得する。
     * 
     * @param clazz 取得対象のクラス。
     * @param method 取得対象のメソッド。
     * @return
     * @throws AnnotationReadException
     */
    public Annotation[] getAnnotations(final Class<?> clazz, final Method method) throws AnnotationReadException {
        if(xmlInfo != null && xmlInfo.getClassInfo(clazz.getName()) != null){
            ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            if(classInfo.getMethodInfo(method.getName()) != null){
                MethodInfo methodInfo = classInfo.getMethodInfo(method.getName());
                List<Annotation> list = new ArrayList<Annotation>();
                for(AnnotationInfo annInfo: methodInfo.getAnnotationInfos()){
                    try {
                        list.add(annotationBuilder.buildAnnotation( Class.forName(annInfo.getAnnotationClass()), annInfo));
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getAnnotationClass()), e);
                    }
                }
                return list.toArray(new Annotation[list.size()]);
            }
        }
        return method.getAnnotations();
    }
    
    
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(final Class<?> clazz, final Field field, final Class<A> ann) throws AnnotationReadException {
        if(xmlInfo != null && xmlInfo.getClassInfo(clazz.getName()) != null){
            ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            if(classInfo.getFieldInfo(field.getName()) != null){
                FieldInfo fieldInfo = classInfo.getFieldInfo(field.getName());
                if(fieldInfo != null && fieldInfo.getAnnotationInfo(ann.getName()) != null){
                    AnnotationInfo annInfo = fieldInfo.getAnnotationInfo(ann.getName());
                    try {
                        return (A)annotationBuilder.buildAnnotation(Class.forName(annInfo.getAnnotationClass()), annInfo);
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getAnnotationClass()), e);
                    }
                }
            }
        }
        return field.getAnnotation(ann);
    }
    
    /**
     * 
     * @param clazz
     * @param field
     * @return
     * @throws AnnotationReadException
     */
    public Annotation[] getAnnotations(Class<?> clazz, Field field) throws AnnotationReadException {
        if(xmlInfo != null && xmlInfo.getClassInfo(clazz.getName()) != null){
            ClassInfo classInfo = xmlInfo.getClassInfo(clazz.getName());
            if(classInfo.getFieldInfo(field.getName()) != null){
                FieldInfo fieldInfo = classInfo.getFieldInfo(field.getName());
                List<Annotation> list = new ArrayList<Annotation>();
                for(AnnotationInfo annInfo: fieldInfo.getAnnotationInfos()){
                    try {
                        list.add(annotationBuilder.buildAnnotation(Class.forName(annInfo.getAnnotationClass()), annInfo));
                    } catch (ClassNotFoundException e) {
                        throw new AnnotationReadException(String.format("not found class '%s'", annInfo.getAnnotationClass()), e);
                    }
                }
                return list.toArray(new Annotation[list.size()]);
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
