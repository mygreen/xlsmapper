package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * セルの列と行を指定して、セルのコメントをマッピングします。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>インデックス形式の{@link #column()}と{@link #row()}か、アドレス形式の{@link #address()}のどちらか一方の形式を指定します。
 *    両方を指定した場合、{@link #address()}の設定値が優先されます。
 * </p>
 * <p>このアノテーションは、String型にのみマッピング可能です。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // インデックス形式で指定する場合。
 *     // インデックスは0から始まります。
 *     {@literal @XlsComment(column=0, row=0)}
 *     private String titleComment;
 *
 *     // アドレス形式で指定する場合
 *     {@literal @XlsComment(address="B3")}
 *     private String nameComment;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/Comment.png" alt="">
 *    <p>基本的な使い方</p>
 * </div>
 *
 * <h3 class="description">書き込み時のコメント書式を制御したい場合</h3>
 * <p>アノテーション {@link XlsCommentOption} を使用することで、書き込み時のコメントの制御を指定することができます。
 *   <br>既にコメントが設定されている場合は、基本的にその設定値を引き継ぎます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // コメントの表示指定する場合
 *     {@literal @XlsComment(column=0, row=0)}
 *     {@literal @XlsCommentOption(visible=true)}
 *     private String titleComment;
 *
 *     // コメント枠のサイズを指定する場合
 *     {@literal @XlsComment(address="B3")}
 *     {@literal @XlsCommentOption(verticalSize=5, horizontalSize=3)}
 *     private String nameComment;
 *
 * }
 * </code></pre>
 *
 * @since 2.1
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@XlsFieldProcessor(value={})
public @interface XlsComment {

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
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
