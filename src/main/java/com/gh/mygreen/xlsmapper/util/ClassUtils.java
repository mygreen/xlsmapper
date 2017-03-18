package com.gh.mygreen.xlsmapper.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * クラスやメソッドに関するユーティリティクラス。
 * また、リフレクションについても処理する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ClassUtils {
    
    /**
     * メソッドがアクセッサメソッド（getter/setter）か判定します。
     * 
     * @param method メソッド情報
     * @return trueの場合、アクセッサメソッド。
     */
    public static boolean isAccessorMethod(final Method method) {
        
        return isGetterMethod(method)
                || isBooleanGetterMethod(method)
                || isSetterMethod(method);
        
    }
    
    /**
     * メソッドがgetterの書式かどうか判定する。
     * ただし、boolean型にたいするisは{@link #isBooleanGetterMethod(Method)}で判定すること。
     * <ol>
     *  <li>メソッド名が'get'か始まっていること。</li>
     *  <li>メソッド名が4文字以上であること。</li>
     *  <li>引数がないこと。</li>
     *  <li>戻り値が存在すること。</li>
     * </ol>
     * @param method メソッド情報
     * @return trueの場合はgetterメソッドである。
     */
    public static boolean isGetterMethod(final Method method) {
        
        final String methodName = method.getName();
        if(!methodName.startsWith("get")) {
            return false;
            
        } else if(methodName.length() <= 3) {
            return false;
        }
        
        if(method.getParameterCount() > 0) {
            return false;
        }
        
        if(method.getReturnType().equals(Void.class)) {
            return false;
        }
        
        return true;
        
    }
    
    /**
     * メソッドがsetterの書式かどうか判定する。
     * <ol>
     *  <li>メソッド名が'set'か始まっていること。</li>
     *  <li>メソッド名が4文字以上であること。</li>
     *  <li>引数が1つのみ存在すること</li>
     *  <li>戻り値は、検証しません。</li>
     * </ol>
     * @param method メソッド情報
     * @return trueの場合はsetterメソッドである。
     */
    public static boolean isSetterMethod(final Method method) {
        
        final String methodName = method.getName();
        if(!methodName.startsWith("set")) {
            return false;
            
        } else if(methodName.length() <= 3) {
            return false;
        }
        
        if(method.getParameterCount() != 1) {
            return false;
        }
        
        return true;
        
    }
    
    /**
     * メソッドがプリミティブ型のbooleanに対するgetterの書式かどうか判定する。
     * <ol>
     *  <li>メソッド名が'is'か始まっていること。</li>
     *  <li>メソッド名が3文字以上であること。</li>
     *  <li>引数がないこと。</li>
     *  <li>戻り値がプリミティブのboolean型であること。</li>
     * </ol>
     * 
     * @param method メソッド情報
     * @return trueの場合はboolean型のgetterメソッドである。
     */
    public static boolean isBooleanGetterMethod(final Method method) {
        
        final String methodName = method.getName();
        if(!methodName.startsWith("is")) {
            return false;
            
        } else if(methodName.length() <= 2) {
            return false;
        }
        
        if(method.getParameterCount() > 0) {
            return false;
        }
        
        if(!isPrimitiveBoolean(method.getReturnType())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * タイプがプリミティブのboolean型かどうか判定する。
     * @param type 判定対象のクラスタイプ。
     * @return trueの場合、プリミティブのboolean型。
     * @throws NullPointerException {@literal type == null.}
     */
    public static boolean isPrimitiveBoolean(final Class<?> type) {
        if(type.isPrimitive() && boolean.class.isAssignableFrom(type)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * クラスから、指定したフィールド情報を取得します。
     * @param targetClass フィールドが定義されているクラス
     * @param propertyName プロパティ名
     * @param propertyType プロパティタイプ
     * @return 存在しない場合、空を返します。
     */
    public static Optional<Field> extractField(final Class<?> targetClass, final String propertyName, final Class<?> propertyType) {
        
        final Field field;
        try {
            field = targetClass.getDeclaredField(propertyName);
        } catch (NoSuchFieldException | SecurityException e) {
            return Optional.empty();
        }
        
        field.setAccessible(true);
        
        if(!field.getType().equals(propertyType)) {
            return Optional.empty();
            
        }
        
        return Optional.of(field);
        
    }
    
    /**
     * クラスから、指定したgetterメソッド情報を取得します。
     * @param targetClass し抽出先のクラス
     * @param propertyName プロパティ名
     * @param propertyType プロパティタイプ
     * @return 存在しない場合、空を返します。
     */
    public static Optional<Method> extractGetter(final Class<?> targetClass, final String propertyName, final Class<?> propertyType) {
        
        final String methodName = "get" + Utils.capitalize(propertyName);
        
        Method method;
        try {
            method = targetClass.getMethod(methodName);
            
        } catch (NoSuchMethodException | SecurityException e) {
            return Optional.empty();
        }
        
        method.setAccessible(true);
        
        if(method.getParameterCount() > 0) {
            return Optional.empty();
        }
        
        if(!method.getReturnType().equals(propertyType)) {
            return Optional.empty();
        }
        
        return Optional.of(method);
    }
    
    /**
     * クラスから、指定したboolean型のgetterメソッド情報を取得します。
     * @param targetClass 抽出先のクラス
     * @param propertyName プロパティ名
     * @param propertyType プロパティタイプ
     * @return 存在しない場合、空を返します。
     */
    public static Optional<Method> extractBooleanGetter(final Class<?> targetClass, final String propertyName) {
        
        final String methodName = "is" + Utils.capitalize(propertyName);
        
        Method method;
        try {
            method = targetClass.getMethod(methodName);
            
        } catch (NoSuchMethodException | SecurityException e) {
            return Optional.empty();
        }
        
        method.setAccessible(true);
        
        if(!method.getReturnType().equals(boolean.class)) {
            return Optional.empty();
        }
        
        return Optional.of(method);
    }
    
    /**
     * クラスから、指定したsetterメソッド情報を取得します。
     * @param targetClass 抽出先のクラス
     * @param propertyName プロパティ名
     * @param propertyType プロパティタイプ
     * @return 存在しない場合、空を返します。
     */
    public static Optional<Method> extractSetter(final Class<?> targetClass, final String propertyName, final Class<?> propertyType) {
        
        final String methodName = "set" + Utils.capitalize(propertyName);
        
        Method method;
        try {
            method = targetClass.getMethod(methodName, propertyType);
        } catch (NoSuchMethodException | SecurityException e) {
            return Optional.empty();
        }
        
        method.setAccessible(true);
        
        return Optional.of(method);
    }
    
}
