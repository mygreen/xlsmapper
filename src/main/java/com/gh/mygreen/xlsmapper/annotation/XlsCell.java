package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 *    <img src="doc-files/Cell.png">
 *    <p>概要</p>
 * </div>
 * 
 * 
 * 
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsCell {
    
    /**
     * セルの行番号を指定します。0から始まります。
     * {@link #column()}属性とセットで指定します。
     */
    int row() default -1;
    
    /**
     * セルの列番号を指定します。0から始まります。
     * {@link #row()}属性とセットで指定します。
     */
    int column() default -1;
    
    /**
     * セルのアドレスを指定します。'A1'などのようにシートのアドレスで指定します。
     * <p>{@link #row()}、{@link #column()}属性のどちらか一方を指定します。
     * @return
     */
    String address() default "";
}
