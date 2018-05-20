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
 * ラベルセルを指定し、その左右もしくは下側のセルの値をマッピングします。
 *
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #label()} で、見出しとなるセルの値を指定します。</p>
 * <p>属性{@link #type()}で見出しとなるセルから見て、設定されている位置を指定します。</p>
 * <p>セルが見つからない場合はエラーとなりますが、属性{@link #optional()}を'true'とすることで無視して処理を続行します。</p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right)}
 *     private String title;
 *
 *     // ラベルセルが見つからなくても処理を続行する
 *     {@literal @XlsLabelledCell(label="ラベル2", type=LabelledCellType.Bottom, optional=true)}
 *     private String summary;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/LabelledCell.png" alt="">
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
 *     {@literal @XlsLabelledCell(label="/名前.+/", type=LabelledCellType.Right)}
 *     private String className;
 *
 *     {@literal @XlsLabelledCell(label="コメント（オプション）", type=LabelledCellType.Right)}
 *     private String comment;
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
public @interface XlsLabelledCell {

    /**
     * セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。
     * <p>falseを指定し、セルが見つからない場合は、例外{@link CellNotFoundException}がスローされます。</p>
     */
    boolean optional() default false;

    /**
     * 属性{@link #type()}の位置に向かって指定したセル数分を検索し、最初に発見した空白以外のセルの値を取得します。
     * <p>属性{@link #range()}と{@link #skip()}を同時に指定した場合、まず、skip分セルを読み飛ばし、そこからrangeの範囲で空白以外のセルを検索します。</p>
     * <p>この属性 は、 <strong>読み込み時のみ有効</strong> です。書き込み時に指定しても無視されます。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsLabelledCell(label="ラベル", type=LabelledCellType.Right, range=4)}
     *     private String title;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledCell_range.png" alt="">
     *    <p>属性rangeの概要</p>
     * </div>
     *
     * @return 値は1から始まり、指定しない場合は1を指定します。
     */
    int range() default 1;

    /**
     * 見出しセルから見て値が設定されているセルの位置を指定します。
     */
    LabelledCellType type();

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
     *     {@literal @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, headerLabel="アクション")}
     *     private String actionClassName;
     *
     *     {@literal @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, headerLabel="アクションフォーム")}
     *     private String formClassName;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledCell_headerLabel.png" alt="">
     *    <p>属性headerLabelの概要</p>
     * </div>
     *
     * @return 見出しとなるセルを指定します。指定しない場合は空文字を指定します。
     */
    String headerLabel() default "";

    /**
     * ラベルセルから指定したセル数分離れたセルを開始位置としてマッピングする際に指定します。
     * <p>属性{@link #type()}の方向に向かってラベルセルから指定したセル数分離れたセルの値をマッピングすることができます。</p>
     * <p>属性{@link #range()}と{@link #skip()}を同時に指定した場合、まず、skip分セルを読み飛ばし、そこからrangeの範囲で空白以外のセルを検索します。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right, skip=2)}
     *     private String title1;
     *
     *      {@literal @XlsLabelledCell(label="ラベル2", type=LabelledCellType.Bottom, skip=3)}
     *     private String title2;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledCell_skip.png" alt="">
     *    <p>属性skipの概要</p>
     * </div>
     *
     *
     * @return 値は0から始まります。0の場合は無視されます。
     */
    int skip() default 0;

    /**
     * ラベルセルが結合している場合を考慮するかどうか指定します。
     * <ul>
     *  <li>値がtrueのとき、結合されているセルを1つのラベルセルとしてマッピングします。</li>
     *  <li>値がfalseの場合は、結合されていても解除した状態と同じマッピング結果となります。</li>
     * </ul>
     * <p>初期値はtrueであるため、結合されているかどうかは特に意識はする必要はありません。</p>
     * <p>{@link #labelMerged()}の値が falseのとき、ラベルセルが結合されていると、
     *    値が設定されているデータセルまでの距離が変わるため、属性{@link #skip()}を併用します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // labelMerged=trueは初期値なので、省略可
     *     {@literal @@XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right)}
     *     private String title1;
     *
     *     // labelMerged=falseで、ラベルが結合しているときは、skip属性を併用します。
     *     {@literal  @XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right, labelMerged=false, skip=2)}
     *     private String title2;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledCell_labelMerged.png" alt="">
     *    <p>属性labelMergedの概要</p>
     * </div>
     *
     * @since 2.0
     * @return trueの場合、ラベルがセルが結合されていることを考慮します。
     */
    boolean labelMerged() default true;

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
