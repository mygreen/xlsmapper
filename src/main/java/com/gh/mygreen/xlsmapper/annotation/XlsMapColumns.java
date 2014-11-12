package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}でカラム数が可変の場合に、 それらのカラムをMapとして設定します。
 * BeanにはMapを引数に取るセッターメソッドを用意し、このアノテーションを記述します。
 * 
 * @author Naoki Takezoe
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsMapColumns {
    
    /**
     * この属性で指定した次のカラム以降、カラム名をキーとしたMapが生成され、Beanにセットされます。
     * @return
     */
    String previousColumnName();
    
    /** 
     * マップの値のクラスを指定します。
     * <p>省略した場合、定義されたたGenericsの情報から取得します。
     */
    Class<?> itemClass() default Object.class;
}
