package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 指定したラベルセルのコメント情報をマッピングします。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #label()} で、見出しとなるセルの値を指定します。</p>
 * <p>セルが見つからない場合はエラーとなりますが、属性{@link #optional()}を'true'とすることで無視して処理を続行します。</p>
 * <p>このアノテーションは、String型にのみマッピング可能です。</p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsLabelledComment(label="ラベル1")}
 *     private String titleComment;
 *
 *     // ラベルセルが見つからなくても処理を続行する
 *     {@literal @XlsLabelledComment(label="ラベル2", optional=true)}
 *     private String summaryComment;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/LabelledComment.png" alt="">
 *    <p>基本的な使い方</p>
 * </div>
 *
 * <h3 class="description">ラベルセルを正規表現、正規化して指定する場合</h3>
 *
 * <p>シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
 *   <br>また、空白や改行を除去してラベルセルを比較するように設定することも可能です。</p>
 *
 * <p>正規表現で指定する場合、アノテーションの属性の値を {@code /正規表現/} のように、スラッシュで囲みます。</p>
 * <ul>
 *   <li>スラッシュで囲まない場合、通常の文字列として処理されます。</li>
 *   <li>正規表現の指定機能を有効にするには、システム設定のプロパティ {@link Configuration#setRegexLabelText(boolean)} の値を trueに設定します。</li>
 * </ul>
 *
 * <p>ラベセルの値に改行が空白が入っている場合、それらを除去し正規化してアノテーションの属性値と比較することが可能です。</p>
 * <ul>
 *   <li>正規化とは、空白、改行、タブを除去することを指します。</li>
 *   <li>ラベルを正規化する機能を有効にするには、、システム設定のプロパティ {@link Configuration#setNormalizeLabelText(boolean)} の値を trueに設定します。</li>
 * </ul>
 *
 * <p>これらの指定が可能な属性は、{@link #label()}、{@link #headerLabel()}です。</p>
 *
 * <pre class="highlight"><code class="java">
 * // システム設定
 * XlsMapper xlsMapper = new XlsMapper();
 * xlsMapper.getConfiguration()
 *         .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
 *         .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
 *
 * // シート用クラス
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // 正規表現による指定
 *     {@literal @XlsLabelledComment(label="/名前.+/")}
 *     private String classNameComment;
 *
 *     {@literal @XlsLabelledComment(label="コメント（オプション）")}
 *     private String commentComment;
 *
 * }
 * </code></pre>
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
 *     {@literal @XlsLabelledComment(label="タイトル")}
 *     {@literal @XlsCommentOption(visible=true)}
 *     private String titleComment;
 *
 *     // コメント枠のサイズを指定する場合
 *     {@literal @XlsLabelledComment(label="名前")}
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
public @interface XlsLabelledComment {

    /**
     * セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。
     * <p>falseを指定し、セルが見つからない場合は、例外{@link CellNotFoundException}がスローされます。</p>
     */
    boolean optional() default false;

    /**
     * 見出しとなるセルの値を指定します。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     */
    String label() default "";

    /**
     * 見出しとなるセルの行番号を指定します。
     * <p>{@link #labelColumn()}属性とセットで指定します。</p>
     * <p>この属性は、{@link XlsIterateTables}の中で指定したときに、処理内部で使用されるため、通常は設定しません。</p>
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int labelRow() default -1;

    /**
     * 見出しとなるセルの列番号を指定します。
     * <p>{@link #labelRow()}属性とセットで指定します。</p>
     * <p>この属性は、{@link XlsIterateTables}の中で指定したときに、処理内部で使用されるため、通常は設定しません。</p>
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int labelColumn() default -1;

    /**
     * 同じラベルのセルが複数ある場合に区別するための見出しを指定します。
     * <p>属性{@link #headerLabel()}で指定されたセルから、属性{@link #label()}で指定されたセルを下方向に検索し、
     *   最初に見つかったセルをラベルセルとして使用します。
     * </p>
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsLabelledComment(label="クラス名", headerLabel="アクション")}
     *     private String actionClassNameComment;
     *
     *     {@literal @XlsLabelledComment(label="クラス名", headerLabel="アクションフォーム")}
     *     private String formClassNameComment;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledComment_headerLabel.png" alt="">
     *    <p>属性headerLabelの概要</p>
     * </div>
     *
     * @return 見出しとなるセルを指定します。指定しない場合は空文字を指定します。
     */
    String headerLabel() default "";

    /**
     * 適用するケースを指定します。
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
