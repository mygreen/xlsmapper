package com.gh.mygreen.xlsmapper.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link FieldAccessor}に対するユーティリティクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAccessorUtils {
    
    /**
     * 指定したアノテーションを持つBeanのプロパティ（フィールド、アクセッサメソッド）の一覧を取得します。
     * @param targetClass 取得元のクラス情報
     * @param annoReader {@link AnnotationReader}のインスタンス。
     * @param annoClass アノテーションのクラス
     * @return プロパティの一覧。存在しない場合は、空のリストを返す。
     * @throws IllegalArgumentException {@literal targetClass == null or annoReader == null or annoClass == null.}
     */
    public static List<FieldAccessor> getPropertiesWithAnnotation(final Class<?> targetClass, final AnnotationReader annoReader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(targetClass, "targetClass");
        ArgUtils.notNull(annoReader, "annoReader");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final FieldAccessorFactory accessorFactory = new FieldAccessorFactory(annoReader);
        final List<FieldAccessor> list = new ArrayList<>();
        
        for(Method method : targetClass.getMethods()) {
            if(!ClassUtils.isAccessorMethod(method)) {
                continue;
            }
            
            method.setAccessible(true);
            
            final FieldAccessor accessor = accessorFactory.create(method);
            if(accessor.hasAnnotation(annoClass) && !list.contains(accessor)) {
                list.add(accessor);
            }
        }
        
        for(Field field : targetClass.getDeclaredFields()) {
            field.setAccessible(true);
            
            final FieldAccessor accessor = accessorFactory.create(field);
            if(accessor.hasAnnotation(annoClass) && !list.contains(accessor)) {
                list.add(accessor);
            }
            
        }
        
        return list;
        
    }
    
    /**
     * アノテーション{@link XlsColumn}が付与されているBeanのプロパティ（フィールド、アクセッサメソッド）の一覧を取得します。
     * ただし、属性{@link XlsColumn#columnName()}の値が、指定した値と等しいか判定します。
     * 
     * @param targetClass 取得元のクラス情報
     * @param annoReader {@link AnnotationReader}のインスタンス。
     * @param config システム設定情報
     * @param columnName カラムの値
     * @return プロパティの一覧。存在しない場合は、空のリストを返す。
     * @throws IllegalArgumentException {@literal config == null || columnName == null.}
     */
    public static List<FieldAccessor> getColumnPropertiesByName(final Class<?> targetClass, final AnnotationReader annoReader,
            final Configuration config, final String columnName) {
        
        return getPropertiesWithAnnotation(targetClass, annoReader, XlsColumn.class)
            .stream()
            .filter(accessor -> Utils.matches(columnName, accessor.getAnnotation(XlsColumn.class).get().columnName(), config))
            .collect(Collectors.toList());
        
    }
    
    /**
     * アノテーション{@link XlsArrayColumns}が付与されているBeanのプロパティ（フィールド、アクセッサメソッド）の一覧を取得します。
     * ただし、属性{@link XlsArrayColumns#columnName()}の値が、指定した値と等しいか判定します。
     * 
     * @param targetClass 取得元のクラス情報
     * @param annoReader {@link AnnotationReader}のインスタンス。
     * @param config システム設定情報
     * @param columnName カラムの値
     * @return プロパティの一覧。存在しない場合は、空のリストを返す。
     * @throws IllegalArgumentException {@literal config == null || columnName == null.}
     */
    public static List<FieldAccessor> getArrayColumnsPropertiesByName(final Class<?> targetClass, final AnnotationReader annoReader,
            final Configuration config, final String columnName) {
        
        return getPropertiesWithAnnotation(targetClass, annoReader, XlsArrayColumns.class)
            .stream()
            .filter(accessor -> Utils.matches(columnName, accessor.getAnnotation(XlsArrayColumns.class).get().columnName(), config))
            .collect(Collectors.toList());
        
    }
}
