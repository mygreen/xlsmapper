package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * セルの列と行を指定して、セルの値をマッピングします。
 * <p>インデックス形式の{@link #column()}と{@link #row()}か、アドレス形式の{@link #address()}のどちらか一方の形式を指定します。
 *    両方を指定した場合、{@link #address()}の設定値が優先されます。
 * </p>
 *
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // インデックス形式で指定する場合。
 *     // インデックスは0から始まります。
 *     {@literal @XlsCell(column=0, row=0)}
 *     private String title;
 *
 *     // アドレス形式で指定する場合
 *     {@literal @XlsCell(address="B3")}
 *     private String name;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/Cell.png" alt="">
 *    <p>概要</p>
 * </div>
 *
 *
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@XlsFieldProcessor(value={})
public @interface XlsCell {

    /**
     * セルの行番号を指定します。
     * {@link #column()}属性とセットで指定します。
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int row() default -1;

    /**
     * セルの列番号を指定します。
     * {@link #row()}属性とセットで指定します。
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int column() default -1;

    /**
     * セルのアドレスを指定します。
     * <p>{@link #row()}、{@link #column()}属性のどちらか一方を指定します。</p>
     *
     * @return 'A1'の形式で指定します。空文字は無視されます。
     */
    String address() default "";

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
