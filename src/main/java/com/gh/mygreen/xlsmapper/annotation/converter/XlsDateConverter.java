package com.gh.mygreen.xlsmapper.annotation.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 日付、時刻型に対するConverter.
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsDateConverter {
    
    /**
     * 日時の書式パターン。{@link java.text.SimpleDateFormat}の書式を指定します。
     * @return
     */
    String pattern();
    
    /**
     * 日付／時刻の解析を厳密に行うか指定します。
     * @return
     */
    boolean lenient() default false;
    
    /**
     * ロケールの指定を行います。指定しない場合、デフォルトのロケールで処理されます。
     * <p>例. 'ja_JP', 'ja'
     * @return
     */
    String locale() default "";
}
