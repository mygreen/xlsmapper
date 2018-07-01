package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 処理対象のシート名をマッピングするメソッド、またはフィールドに付与します。
 * <p>付与するフィートのクラスタイプは、{@link String}型にする必要があります。
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(number=1)}
 * public class SampleSheet {
 *
 *    // シート名をマッピングするフィールド
 *    {@literal @XlsSheetName}
 *    private String sheetName;
 *    ・・・
 *
 * }
 * </code></pre>
 *
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@XlsFieldProcessor(value={})
public @interface XlsSheetName {

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
