package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;

/**
 * アノテーション{@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で指定されたレコード用のクラスにおいて、
 * 隣接する連続したカラムを、配列や{@link java.util.List}にマッピングします。
 *
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #columnName()} で、見出しとなるセルのラベルを指定します。</p>
 * <p>属性{@link #size()} で連続するセルの個数を指定します。
 *  <br>見出しとなるカラムは、結合している必要があります。
 * </p>
 * <p>セルが見つからない場合はエラーとなりますが、属性{@link #optional()}を'true'とすることで無視して処理を続行します。</p>
 * <p>配列または、{@link java.util.Collection}({@link java.util.List}/{@link java.util.Set})にマッピングします。</p>
 * <p>{@link java.util.Collection}型のインタフェースを指定している場合、読み込み時のインスタンスは次のクラスが指定されます。</p>
 * <ul>
 *   <li>{@link java.util.List}の場合、{@link java.util.ArrayList}がインスタンスのクラスとなります。
 *   <li>{@link java.util.Set} の場合、{@link java.util.LinkedHashSet}がインスタンスのクラスとなります。
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *
 *     {@literal @XlsArrayColumns(columnName="ふりがな", size=10))}
 *     private {@literal List<String>} nameRuby;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/ArrayColumns.png" alt="">
 *    <p>基本的な使い方</p>
 * </div>
 *
 *
 * <h3 class="description">書き込み時に配列・リストのサイズが不足、または余分である場合</h3>
 * アノテーション {@link XlsArrayOption} を指定することで、書き込み時のセルの制御を指定することができます。
 *
 * <p>属性 {@link XlsArrayOption#overOpration()} で、書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性 {@link #size()} の値が小さく、足りない場合の操作を指定します。
 * <p>属性 {@link XlsArrayOption#remainedOperation()} で、書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性 {@link #size()} の値が大きく、余っている場合の操作を指定します。
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *
 *     {@literal @XlsArrayColumns(columnName="ふりがな", size=6)}
 *     {@literal @XlsArrayOption(overOperation=OverOperation.Error, remainedOperation=RemainedOperation.Clear)}
 *     private {@literal List<String>} nameRuby;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/ArrayColumns_ArrayOption.png" alt="">
 *    <p>書き込み時の制御を行う場合</p>
 * </div>
 *
 *
 * <h3 class="description">見出しを正規表現、正規化して指定する場合</h3>
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
 * <p>これらの指定が可能な属性は、{@link #columnName()}です。</p>
 *
 * <pre class="highlight"><code class="java">
 * // システム設定
 * XlsMapper xlsMapper = new XlsMapper();
 * xlsMapper.getConfiguration()
 *         .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
 *         .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
 *
 * // レコード用クラス
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     // 正規表現による指定
 *     {@literal @XlsColumn(columnName="/名前.+/")}
 *     private String name;
 *
 *     // 正規表現による指定
 *     {@literal @XlsArrayColumns(columnName="/ふりがな.+/", size=10)}
 *     private {@literal List<String>} nameRuby;
 *
 * }
 * </code></pre>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsArrayColumns {

    /**
     * 見出しとなるカラム名を指定します。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return カラム名
     */
    String columnName();

    /**
     * 連続するセルの個数を指定します。
     * @return 1以上の値を指定します。
     */
    int size();

    /**
     * 配列またはリスト要素の値のクラスを指定します。
     * <p>省略した場合、定義されたたGenericsの情報から取得します。
     */
    Class<?> elementClass() default Object.class;

    /**
     * 値のセルが結合している場合、それを考慮するかどうか指定します。
     * この値により、属性{@link #size()}の指定方法が変わります。
     * <p>セル結合されている場合は、結合後の個数を指定します。</p>
     * <ul>
     *   <li>trueの場合は、結合されているセルを1つのセルとしてマッピングします。</li>
     *   <li>falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
     *     <br>ただし、書き込む際には、結合が解除されます。
     *   </li>
     * </ul>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsColumn(columnName="ID")}
     *     private int id;
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     private String name;
     *
     *     // elementMerged=trueは初期値なので、省略可
     *     {@literal @XlsArrayColumns(columnName="連絡先", size=3)}
     *     private {@literal List<String>} contactInfos;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/ArrayColumns_elementMerged.png" alt="">
     *    <p>結合したセルをマッピングする場合</p>
     * </div>
     *
     * @return trueの場合、値のセルが結合されていることを考慮します。
     */
    boolean elementMerged() default true;

    /**
     * 属性{@link #columnName()}で指定したカラム（セル）が見つからない場合、trueと設定すると無視して処理を続行します。
     * <p>falseを指定し、セルが見つからない場合は、例外{@link CellNotFoundException}がスローされます。</p>
     * @return trueの場合、該当するカラム（セル）が見つからないときは無視して処理を続行します。
     */
    boolean optional() default false;

}
