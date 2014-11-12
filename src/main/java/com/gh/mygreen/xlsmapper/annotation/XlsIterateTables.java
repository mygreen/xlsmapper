package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 同一の構造の表がシート内で繰り返し出現する場合に使用します。 
 * @author Mitsuyoshi Hasegawa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface XlsIterateTables {
    
    /**
     * 繰り返し部分の見出しラベルを指定します。
     * @return
     */
    String tableLabel();
    
    /**
     * 繰り返し部分の情報を格納するJavaBeanのクラス。
     * <p>指定しない場合は、Genericsの定義タイプを使用します。
     * @return
     */
    Class<?> tableClass() default Object.class;
    
    /**
     * {@link XlsIterateTables}内で{@link XlsHorizontalRecords}を使用する場合に、 
     * テーブルの開始位置が{@link XlsIterateTables}の見出しセルからどれだけ離れているかを指定します。
     * @return
     */
    int bottom() default -1;
    
    /**
     * 表が見つからなかった場合、無視するか指定します。
     * @return
     */
    boolean optional() default false;
}
