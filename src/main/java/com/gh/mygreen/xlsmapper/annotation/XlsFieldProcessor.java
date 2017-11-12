package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessorRegistry;

/**
 * マッピング用のアノテーションを表現するためのメタアノテーション。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsFieldProcessor {
    
    /**
     * アノテーションに対応した{@link FieldProcessor}の実装クラスを指定します。
     * <p>省略した場合、{@link FieldProcessorRegistry}に登録されているクラスを使用します。
     * @return {@link FieldProcessor}の実装クラス。
     */
    Class<? extends FieldProcessor<?>>[] value();
    
}
