package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.RecordFinder;

/**
 * {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}の読み込み時において、
 * データレコードの開始位置が既存のアノテーションの属性だと表現できない場合に
 * 任意の実装方法を指定するようにします。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsRecordFinder {
    
    /**
     * {@link RecordFinder}の実装クラスを指定します。
     * @return {@link RecordFinder}の実装クラス。
     */
    Class<? extends RecordFinder> value();
    
    /**
     * {@link RecordFinder}に渡す引数を指定します。
     * @return {@link RecordFinder}に渡す引数。
     */
    String[] args() default {};
    
}
