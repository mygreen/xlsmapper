package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 書き込み時のセルの折り返し設定などのオプションを指定するためのアノテーションです。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsCellOption {
    
    /**
     * 'true'のとき書き込み時にセルの「折り返し設定」を有効にします。
     * 'false'の場合は、既存の折り返し設定は変更せずに、テンプレートファイルの設定を引き継ぎます。
     * <p>属性{@link #wrapText()}と{@link #shrinkToFit()}の両方の値をtrueに指定する場合、
     *    {@link #shrinkToFit()}の設定が優先され、「縮小して全体を表示する」が有効になります。
     * </p>
     * 
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     * 
     *     {@literal @XlsColumn(columnName="ID")}
     *     {@literal @XlsCellOption(wrapText=true)} // 「縮小して全体を表示する」が有効になる。
     *     private int id;
     *     
     * }
     * </code></pre>
     * 
     * @return trueの場合、「折り返し設定」が有効になります。
     */
    boolean wrapText() default false;
    
    /**
     * 'true'のとき書き込み時にセルの「縮小して表示」を有効にします。
     * 'false'の場合は、既存の縮小して表示は変更せずに、テンプレートファイルの設定を引き継ぎます。
     * <p>属性{@link #wrapText()}と{@link #shrinkToFit()}の両方の値をtrueに指定する場合、
     *    {@link #shrinkToFit()}の設定が優先され、「縮小して全体を表示する」が有効になります。
     * </p>
     * 
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     * 
     *     {@literal @XlsColumn(columnName="名前")}
     *     {@literal @XlsCellOption(shrinkToFit=true)} //「折り返して全体を表示する」が有効になる。
     *     private String name;
     *     
     *     {@literal @XlsColumn(columnName="備考")}
     *     {@literal @XlsCellOption(shrinkToFit=false)} // 設定しない場合は、テンプレート設定が有効になる。
     *     private String comment;
     *     
     * }
     * </code></pre>
     * 
     * @return trueの場合、「縮小して表示」が有効になります。
     */
    boolean shrinkToFit() default false;
    
}
