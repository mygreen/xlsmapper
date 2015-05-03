package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * レコードの値が空からどうか判定するメソッドに付与します。
 * <p>引数なしで、戻り値がbooleanである必要があります。
 * <p>{@link com.gh.mygreen.xlsmapper.IsEmptyBuilder}を利用して簡単に判定することもできます。
 * 
 * @since 0.2
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsIsEmpty {
}
