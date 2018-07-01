package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 同一の構造の表がシート内で繰り返し出現する場合に使用し、Collection(List、Set)または配列にマッピングします。
 *
 * 次のアノテーションを組み合わせて使用します。
 * <ul>
 *   <li>横方向の表をマッピングするアノテーション {@link XlsHorizontalRecords}。</li>
 *   <li>縦方向の表をマッピングするアノテーション {@link XlsVerticalRecords}。
 *     <br>ただし、アノテーション {@link XlsHorizontalRecords} と同時に使用することはできません。
 *   </li>
 *   <li>見出し付きの1つのセルをマッピングするアノテーション {@link XlsLabelledCell}。</li>
 *   <li>見出し付きの連続し隣接する複数のセルをマッピングするアノテーション {@link XlsLabelledArrayCells}。</li>
 * </ul>
 *
 * <h3 class="description">基本的な使い方</h3>
 *
 * <p>使い方を基本的な例を元に説明していきます。</p>
 *
 * <div class="picture">
 *    <img src="doc-files/IterateTables.png" alt="">
 *    <p>基本的な例</p>
 * </div>
 *
 * <p>シート用クラスの場合、属性{@link XlsIterateTables#tableLabel()}で、繰り返し部分の表の名称を指定します。
 *    <br>また、属性{@link XlsIterateTables#bottom()}で、アノテーション{@link XlsHorizontalRecords}を
 *        マッピングする表の開始位置が表の名称からどれだけ離れているかを指定します。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="シート名")}
 * public class SampleSheet {
 *
 *     {@literal @XlsIterateTables(tableLabel="部門情報", bottom=2)}
 *     private {@literal List<SampleTable>} tables;
 * }
 * </code></pre>
 *
 *
 * <p>繰り返し部分に対応するクラスは、以下のように、
 * アノテーション{@link XlsLabelledCell}、{@link XlsHorizontalRecords}を使用することができます。
 * </p>
 *
 * <p>アノテーション{@link XlsHorizontalRecords} を使用する場合、属性{@link XlsHorizontalRecords#tableLabel()} は設定する必要はありません。
 *  <br>アノテーション {@link XlsIterateTables}の属性{@link #tableLabel()}と{@link #bottom()}の値を引き継ぐため、指定しなくても問題ないためです。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * // テーブル用クラス
 * public class SampleTable {
 *
 *     {@literal @XlsLabelledCell(label="部門名", type=LabelledCellType.Right)}
 *     private String deptName;
 *
 *     {@literal @XlsHorizontalRecords(terminal=RecordTerminal.Border)}
 *     private {@literal List<SampleRecord>} records;
 * }
 * </code></pre>
 *
 * <p>繰り返し部分に対応するJavaBeanでアノテーション {@link XlsHorizontalRecords} を使用した場合、
 * 通常の場合と同じくアノテーション{@link XlsColumn}や{@link XlsMapColumns}で列とのマッピングを行います。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * // レコード用クラス
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private String id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 * }
 * </code></pre>
 *
 * <h3 class="description">縦方向の表を組み合わせてマッピングする場合</h3>
 * 縦方向の表をマッピングするアノテーション {@link XlsVerticalRecords} も使用することができます。
 *
 * <ul>
 *   <li>ただし、横方向の表をマッピングするアノテーション {@link XlsHorizontalRecords} と同時に使用することはできません。</li>
 *   <li>属性 {@link XlsVerticalRecords#tableLabelAbove()}の値がtrueとが自動的に有効になり、表の見出しが上部にあることを前提に処理されます。</li>
 * </ul>
 *
 * <div class="picture">
 *    <img src="doc-files/IterateTables_VerticalRecords.png" alt="">
 *    <p>縦方向の表のマッピング</p>
 * </div>
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="観測データ")}
 * public class SampleSheet {
 *
 *     {@literal @XlsIterateTables(tableLabel="/観測情報.+/", bottom=2)}
 *     private {@literal List<DataTable>} tables;
 * }
 *
 * // テーブル用クラス
 * public class DataTable {
 *
 *     {@literal @XlsLabelledCell(label="日付", type=LabelledCellType.Right)}
 *     private LocalDate date;
 *
 *     {@literal XlsVerticalRecords(terminal=RecordTerminal.Border)}
 *     private {@literal List<WeatherRecord>} records;
 * }
 *
 * // レコード用クラス
 * public class WeatherRecord {
 *
 *     {@literal @XlsColumn(columnName="時間")}
 *     private String time;
 *
 *     {@literal @XlsColumn(columnName="降水")}
 *     private double precipitation;
 * }
 * </code></pre>
 *
 *
 * <h3 class="description">表の名称を正規表現、正規化して指定する場合</h3>
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
 * <p>これらの指定が可能な属性は、{@link #tableLabel()}です。</p>
 *
 * <pre class="highlight"><code class="java">
 *
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
 *     {@literal @XlsIterateTables(tableLabel="/部門情報.+/", bottom=2)}
 *     private {@literal List<SampleTable>} tables;
 *
 * }
 * </code></pre>
 *
 * @version 2.0
 * @author Mitsuyoshi Hasegawa
 * @author T.TSUCHIE
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Documented
@XlsFieldProcessor(value={})
public @interface XlsIterateTables {

    /**
     * 繰り返し部分の見出しラベルを指定します。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return
     */
    String tableLabel();

    /**
     * 繰り返し部分の情報を格納するJavaBeanのクラス。
     * <p>指定しない場合は、Genericsの定義タイプを使用します。
     * @return
     */
    Class<?> tableClass() default Object.class;

    /**
     * {@link XlsIterateTables}内で{@link XlsHorizontalRecords}を使用する場合に、
     * テーブルの開始位置が{@link XlsIterateTables}の見出しセルからどれだけ離れているかを指定します。
     * @return
     */
    int bottom() default -1;

    /**
     * 表が見つからなかった場合、無視するか指定します。
     * @return
     */
    boolean optional() default false;

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
