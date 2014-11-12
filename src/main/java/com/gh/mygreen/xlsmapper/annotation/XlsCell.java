package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * セルの列と行を指定して、セルの値を読み込み／書き込みします。
 * 
 * @author Naoki Takezoe
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsCell {
    
    /**
     * セルの行を指定します。0から始まります。
     * {@link #column()}属性とセットで指定します。
     */
    int row() default -1;
    
    /**
     * セルの列を指定します。0から始まります。
     * {@link #row()}属性とセットで指定します。
     */
    int column() default -1;
    
    /**
     * セルのアドレスを指定します。'A1'などのようにシートのアドレスで指定します。
     * <p>{@link #row()}、{@link #column()}属性のどちらか一方を指定します。
     * @return
     */
    String address() default "";
}
