package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.ClassUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link FieldAccessor}のインスタンスを作成するクラス。
 *
 * @version 2.1
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAccessorFactory {

    private static Logger log = LoggerFactory.getLogger(FieldAccessorFactory.class);

    private final AnnotationReader annoReader;

    private PositionSetterFactory positionSetterFactory = new PositionSetterFactory();
    private PositionGetterFactory positionGetterFactory = new PositionGetterFactory();
    private MapPositionSetterFactory mapPositionSetterFactory = new MapPositionSetterFactory();
    private ArrayPositionSetterFactory arrayPositionSetterFactory = new ArrayPositionSetterFactory();

    private LabelSetterFactory labelSetterFactory = new LabelSetterFactory();
    private LabelGetterFactory labelGetterFactory = new LabelGetterFactory();
    private MapLabelSetterFactory mapLabelSetterFactory = new MapLabelSetterFactory();
    private ArrayLabelSetterFactory arrayLabelSetterFactory = new ArrayLabelSetterFactory();
    
    private CommentSetterFactory commentSetterFactory = new CommentSetterFactory();
    private CommentGetterFactory commentGetterFactory = new CommentGetterFactory();
    
    private MapCommentSetterFactory mapCommentSetterFactory = new MapCommentSetterFactory();
    private MapCommentGetterFactory mapCommentGetterFactory = new MapCommentGetterFactory();
    
    private ArrayCommentSetterFactory arrayCommentSetterFactory = new ArrayCommentSetterFactory();
    private ArrayCommentGetterFactory arrayCommentGetterFactory = new ArrayCommentGetterFactory();

    
    /**
     * コンストラクタ
     * @param annoReader XMLで定義したアノテーション情報を提供するクラス。
     * @throws IllegalArgumentException {@literal annoReader == null.}
     */
    public FieldAccessorFactory(final AnnotationReader annoReader) {
        ArgUtils.notNull(annoReader, "annoReader");

        this.annoReader = annoReader;
    }

    /**
     * フィールド情報を元にインスタンスを作成する。
     * @param field フィールド
     * @return フィールド情報を元に組み立てられたインスタンス。
     * @throws IllegalArgumentException {@literal field == null.}
     */
    public FieldAccessor create(final Field field) {

        ArgUtils.notNull(field, "field");

        final FieldAccessor accessor = new FieldAccessor();

        // 共通情報の設定
        accessor.name = field.getName();
        accessor.targetType = field.getType();
        accessor.declaringClass = field.getDeclaringClass();

        // フィールド情報の設定
        setupWithFiled(accessor, field);

        // getter情報の設定
        if(ClassUtils.isPrimitiveBoolean(accessor.getType())) {
            ClassUtils.extractBooleanGetter(accessor.getDeclaringClass(), accessor.getName())
                    .ifPresent(getter -> setupWithGetter(accessor, getter));

        } else {
            ClassUtils.extractGetter(accessor.getDeclaringClass(), accessor.getName(), accessor.getType())
                    .ifPresent(getter -> setupWithGetter(accessor, getter));
        }

        // setter情報の設定
        ClassUtils.extractSetter(accessor.getDeclaringClass(), accessor.getName(), accessor.getType())
                .ifPresent(setter -> setupWithSetter(accessor, setter));

        // コンポーネントタイプの設定
        if(Collection.class.isAssignableFrom(accessor.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            accessor.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[0]);

        } else if(accessor.getType().isArray()) {
            accessor.componentType = Optional.of(accessor.getType().getComponentType());

        } else if(Map.class.isAssignableFrom(accessor.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            accessor.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[1]);
        }

        // 位置・ラベル情報のアクセッサの設定
        if(accessor.hasAnnotation(XlsMapColumns.class)) {
            // マップ形式の場合
            accessor.mapPositionSetter = mapPositionSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.mapLabelSetter = mapLabelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            
            accessor.mapCommentSetter = mapCommentSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.mapCommentGetter = mapCommentGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            

        } else if(accessor.hasAnnotation(XlsArrayColumns.class)
                || accessor.hasAnnotation(XlsArrayCells.class)
                || accessor.hasAnnotation(XlsLabelledArrayCells.class)
                || accessor.hasAnnotation(XlsIterateTables.class)){
            // リストや配列形式の場合
            accessor.arrayPositionSetter = arrayPositionSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.arrayLabelSetter = arrayLabelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            
            accessor.arrayCommentSetter = arrayCommentSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.arrayCommentGetter = arrayCommentGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());

            if(accessor.hasAnnotation(XlsArrayColumns.class)
                    || accessor.hasAnnotation(XlsLabelledArrayCells.class)) {
                // インデックスが付いていないラベルの設定
                accessor.labelSetter = labelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            }

        } else {
            // リストやMapではない通常のプロパティの場合
            accessor.positionSetter = positionSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.positionGetter = positionGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());

            accessor.labelSetter = labelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.labelGetter = labelGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            
            accessor.commentSetter = commentSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.commentGetter = commentGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            

        }

        return accessor;
    }

    /**
     * メソッド情報を元にインスタンスを作成する。
     * @param method メソッド情報
     * @return メソッド情報を元に組み立てられたインスタンス。
     * @throws IllegalArgumentException {@literal method == null.}
     * @throws IllegalArgumentException {@literal methodの名称がsetterまたはgetterの書式でない場合。}
     */
    public FieldAccessor create(final Method method) {

        ArgUtils.notNull(method, "method");

        final FieldAccessor accessor = new FieldAccessor();

        final String methodName = method.getName();
        if(ClassUtils.isGetterMethod(method) || ClassUtils.isBooleanGetterMethod(method)) {
            final String propertyName;
            if(methodName.startsWith("get")) {
                propertyName = Utils.uncapitalize(methodName.substring(3));
            } else {
                propertyName = Utils.uncapitalize(methodName.substring(2));
            }

            // 共通情報の設定
            accessor.name = propertyName;
            accessor.targetType = method.getReturnType();
            accessor.declaringClass = method.getDeclaringClass();

            // getter情報の設定
            setupWithGetter(accessor, method);

            // フィールド情報の設定
            ClassUtils.extractField(accessor.getDeclaringClass(), accessor.getName(), accessor.getType())
                    .ifPresent(field -> setupWithFiled(accessor, field));

            // setter情報の設定
            ClassUtils.extractSetter(accessor.getDeclaringClass(), accessor.getName(), accessor.getType())
                    .ifPresent(setter -> setupWithSetter(accessor, setter));

            // コンポーネントタイプの設定
            if(Collection.class.isAssignableFrom(accessor.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                accessor.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[0]);

            } else if(accessor.getType().isArray()) {
                accessor.componentType = Optional.of(accessor.getType().getComponentType());

            } else if(Map.class.isAssignableFrom(accessor.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                accessor.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[1]);
            }

        } else if(ClassUtils.isSetterMethod(method)) {
            final String propertyName = Utils.uncapitalize(methodName.substring(3));

            // 共通情報の設定
            accessor.name = propertyName;
            accessor.targetType = method.getParameterTypes()[0];
            accessor.declaringClass = method.getDeclaringClass();

            // setter情報の設定
            setupWithSetter(accessor, method);

            // フィールド情報の設定
            ClassUtils.extractField(accessor.getDeclaringClass(), accessor.getName(), accessor.getType())
                    .ifPresent(field -> setupWithFiled(accessor, field));

            // getter情報の設定
            if(ClassUtils.isPrimitiveBoolean(accessor.getType())) {
                ClassUtils.extractBooleanGetter(accessor.getDeclaringClass(), accessor.getName())
                        .ifPresent(getter -> setupWithGetter(accessor, getter));

            } else {
                ClassUtils.extractGetter(accessor.getDeclaringClass(), accessor.getName(), accessor.getType())
                        .ifPresent(getter -> setupWithGetter(accessor, getter));
            }

            // コンポーネントタイプの設定
            if(Collection.class.isAssignableFrom(accessor.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[0];
                accessor.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[0]);

            } else if(accessor.getType().isArray()) {
                accessor.componentType = Optional.of(accessor.getType().getComponentType());

            } else if(Map.class.isAssignableFrom(accessor.getType())) {
                ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[0];
                accessor.componentType = Optional.of((Class<?>) type.getActualTypeArguments()[1]);
            }

        } else {
            throw new IllegalArgumentException(MessageBuilder.create("method.noAccessor")
                    .varWithClass("className", method.getDeclaringClass())
                    .var("methodName", methodName)
                    .format());
        }

        // 位置・ラベル情報のアクセッサの設定
        if(accessor.hasAnnotation(XlsMapColumns.class)) {
            accessor.mapPositionSetter = mapPositionSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.mapLabelSetter = mapLabelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());

        } else if(accessor.hasAnnotation(XlsArrayColumns.class)
                || accessor.hasAnnotation(XlsArrayCells.class)
                || accessor.hasAnnotation(XlsLabelledArrayCells.class)
                || accessor.hasAnnotation(XlsIterateTables.class)){
            // リストや配列形式の場合
            accessor.arrayPositionSetter = arrayPositionSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.arrayLabelSetter = arrayLabelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());

            if(accessor.hasAnnotation(XlsArrayColumns.class)
                    || accessor.hasAnnotation(XlsLabelledArrayCells.class)) {
                // インデックスが付いていないラベルの設定
                accessor.labelSetter = labelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            }

        } else {
            // XlsMapColumnsを持たない通常のプロパティの場合
            accessor.positionSetter = positionSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.positionGetter = positionGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());

            accessor.labelSetter = labelSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.labelGetter = labelGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            
            accessor.commentSetter = commentSetterFactory.create(accessor.getDeclaringClass(), accessor.getName());
            accessor.commentGetter = commentGetterFactory.create(accessor.getDeclaringClass(), accessor.getName());

        }


        return accessor;
    }

    private void setupWithFiled(final FieldAccessor accessor, final Field field) {

        accessor.targetField = Optional.of(field);

        final Annotation[] annos = annoReader.getAnnotations(field);
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();

            if(accessor.annotationMap.containsKey(annoClass)) {
                final String message = MessageBuilder.create("anno.duplicated")
                        .varWithClass("classType", accessor.getDeclaringClass())
                        .var("property", field.getName())
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
            }

            accessor.annotationMap.put(annoClass, anno);
        }


    }

    private void setupWithGetter(final FieldAccessor accessor, final Method method) {

        accessor.targetGetter = Optional.of(method);

        final Annotation[] annos = annoReader.getAnnotations(method);
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();

            if(accessor.annotationMap.containsKey(annoClass)) {
                final String message = MessageBuilder.create("anno.duplicated")
                        .varWithClass("classType", accessor.getDeclaringClass())
                        .var("property", method.getName() + "()")
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
            }

            accessor.annotationMap.put(annoClass, anno);
        }
    }

    private void setupWithSetter(final FieldAccessor accessor, final Method method) {

        accessor.targetSetter = Optional.of(method);

        final Annotation[] annos = annoReader.getAnnotations(method);
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();

            if(accessor.annotationMap.containsKey(annoClass)) {
                final String message = MessageBuilder.create("anno.duplicated")
                        .varWithClass("classType", accessor.getDeclaringClass())
                        .var("property", method.getName() + "(...)")
                        .varWithAnno("anno", annoClass)
                        .format();
                log.warn(message);
            }

            accessor.annotationMap.put(annoClass, anno);
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
