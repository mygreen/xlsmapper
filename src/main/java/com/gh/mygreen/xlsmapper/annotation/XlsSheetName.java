package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * @author Naoki Takezoe
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsSheetName {
}
