package com.gh.mygreen.xlsmapper.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.processor.FieldAdapterBuilder;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link FieldAdapter}に対するユーティリティクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAdapterUtils {
    
    /**
     * 指定したアノテーションを持つBeanのプロパティ（フィールド、アクセッサメソッド）の一覧を取得します。
     * @param targetClass 取得元のクラス情報
     * @param annoReader {@link AnnotationReader}のインスタンス。
     * @param annoClass アノテーションのクラス
     * @return プロパティの一覧。存在しない場合は、空のリストを返す。
     * @throws NullPointerException {@literal targetClass == null or annoReader == null or annoClass == null.}
     */
    public static List<FieldAdapter> getPropertiesWithAnnotation(final Class<?> targetClass, final AnnotationReader annoReader,
            final Class<? extends Annotation> annoClass) {
        
        ArgUtils.notNull(targetClass, "targetClass");
        ArgUtils.notNull(annoReader, "annoReader");
        ArgUtils.notNull(annoClass, "annoClass");
        
        final FieldAdapterBuilder builder = new FieldAdapterBuilder(annoReader);
        final List<FieldAdapter> list = new ArrayList<>();
        
        for(Method method : targetClass.getMethods()) {
            if(!ClassUtils.isAccessorMethod(method)) {
                continue;
            }
            
            final FieldAdapter adapter = builder.of(method);
            if(adapter.hasAnnotation(annoClass) && !list.contains(adapter)) {
                list.add(adapter);
            }
        }
        
        for(Field field : targetClass.getDeclaredFields()) {
            field.setAccessible(true);
            
            final FieldAdapter adapter = builder.of(field);
            if(adapter.hasAnnotation(annoClass) && !list.contains(adapter)) {
                list.add(adapter);
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
     * @throws NullPointerException {@literal config == null || columnName == null.}
     */
    public static List<FieldAdapter> getColumnPropertiesByName(final Class<?> targetClass, final AnnotationReader annoReader,
            final XlsMapperConfig config, final String columnName) {
        
        return getPropertiesWithAnnotation(targetClass, annoReader, XlsColumn.class)
            .stream()
            .filter(adapter -> Utils.matches(columnName, adapter.getAnnotation(XlsColumn.class).get().columnName(), config))
            .collect(Collectors.toList());
        
    }
}
