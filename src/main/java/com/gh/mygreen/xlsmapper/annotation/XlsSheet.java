package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * シートのマッピング対象のルートクラス（JavaBeans）に付与するアノテーション。
 * <p>マッピング対象のシート名を属性を使って指定します。
 * 
 * @author Naoki Takezoe
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsSheet {
    
    /**
     * シート名を指定します。
     * Returns the mapped sheet name.
     * 
     * @return the sheet name
     */
    String name() default "";
    
    /**
     * シート名を正規表現で指定します。
     * <p>書き込み時は、{@link XlsSheetName}を付与したフィールドでシート名を指定します。
     * Returns the regular expression to map sheet name.
     * 
     * @return the regular expression
     */
    String regex() default "";
    
    /**
     * マッピング対象のシートを番号で指定します。'0'から始まります。
     * Returns the mapped sheet number.
     * 
     * @return the sheet number
     */
    int number() default -1;
    
}
