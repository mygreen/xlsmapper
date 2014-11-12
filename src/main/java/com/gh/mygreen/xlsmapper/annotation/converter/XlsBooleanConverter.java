package com.gh.mygreen.xlsmapper.annotation.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * boolean/Booleanに対するConverter.
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsBooleanConverter {
    
    /**
     * 読み込み時に'true'と判断する候補を指定します。
     * <p>candidate string with pare string as true.
     * @return 
     */
    String[] loadForTrue() default {"true", "1", "yes", "on", "y", "t"};
    
    /**
     * 読み込み時に'false'と判断する候補を指定します。
     * <p>candidate string with pare string as true.
     * @return
     */
    String[] loadForFalse() default {"false", "0", "no", "off", "f", "n"};
    
    /**
     * 書き込み時の'true'の値を指定します。
     * @return
     */
    String saveAsTrue() default "true";
    
    /**
     * 書き込み時の'false'の値を指定します。
     * @return
     */
    String saveAsFalse() default "false";
    
    /**
     * セルの読み込み時に、{@link #loadForTrue()}、{@link #loadForFalse()}で指定した候補と比較する際に文字の大小を無視します。
     * <p>ignore lower / upper case.
     * @return
     */
    boolean ignoreCase() default true;
    
    /**
     * セルの読み込み時に、{@link #loadForTrue()}、{@link #loadForFalse()}で指定した候補と一致しない場合、値をfalseとして読み込みます。
     * <p>if fail parsing, convert to false.
     * @return
     */
    boolean failToFalse() default false;
    
}
