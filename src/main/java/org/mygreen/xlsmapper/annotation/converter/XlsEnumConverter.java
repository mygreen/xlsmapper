package org.mygreen.xlsmapper.annotation.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 列挙型に対するConverter。
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsEnumConverter {
    
    /**
     * 読み込み時に大文字／小文字を無視して比較するか指定します。
     * @return
     */
    boolean ignoreCase() default false;
    
    /**
     * 列挙型のをname()メソッド以外から取得するときに指定します。
     * <p>例). Color.label()のlabel()メソッドを指定するときには、'label'と指定します。
     */
    String valueMethodName() default "";
}
