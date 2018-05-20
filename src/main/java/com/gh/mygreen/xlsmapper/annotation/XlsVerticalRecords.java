package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.RemainedOperation;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 垂直方向に連続する列をCollection(List, Set)または配列にマッピングする際に指定します。
 * <p>アノテーション{@link XlsHorizontalRecords}を垂直方向にしたものであり、使い方はほとんど同じです。
 * <p>ここでは、アノテーション{@link XlsHorizontalRecords}と異なる部分を説明します。
 *    <br>共通の使い方は、アノテーション{@link XlsHorizontalRecords}の説明を参照してください。
 * </p>
 *
 * <h3 class="description">基本的な使い方</h3>
 * <p>表名を、属性{@link #tableLabel()}で指定します。</p>
 * <p>レコード用クラスは、列の定義をアノテーション{@link XlsColumn}で指定します。</p>
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="Weather")}
 * public class SampleSheet {
 *
 *     {@literal @XlsVerticalRecords(tableLabel="天気情報")}
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
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/VerticalRecord.png" alt="">
 *    <p>基本的な使い方</p>
 * </div>
 *
 * <h3 class="description">書き込み時にレコードが不足、余分である場合の操作の指定</h3>
 *
 * <p>アノテーション{@link XlsRecordOption} 指定することで、書き込み時のレコードの制御を指定することができます。</p>
 * <p>属性{@link XlsRecordOption#overOperation()} で、書き込み時にJavaオブジェクトのレコード数に対して、シートのレコード数が足りないときの操作を指定します。
 *   <br>ただし、{@link XlsVerticalRecords}の場合は、列の挿入を行う {@link OverOperation#Insert} は使用できません。
 * </p>
 * <p>属性{@link XlsRecordOption#remainedOperation()} で、書き込み時にJavaオブジェクトのレコード数に対して、シートのレコード数が余っているときの操作を指定します。
 *   <br>ただし、{@link XlsVerticalRecords}の場合は、列の削除を行う {@link RemainedOperation#Delete} は使用できません。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsVerticalRecords(tableLabel="天気情報")}
 *     {@literal @XlsRecordOption(overOperation=OverOperation.Copy, remainedOperation=RemainedOperation.Clear)}
 *     private {@literal List<UserRecord>} records;
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/VerticalRecord_RecordOption.png" alt="">
 *    <p>書き込み時の制御を行う場合</p>
 * </div>
 *
 * <h3 class="description">任意の位置からレコードが開始するかを指定する場合</h3>
 *
 * <p>データレコードの途中で中見出しがあり、分割されているような表の場合、アノテーション{@link XlsRecordFinder} で、
 *   レコードの開始位置を決める処理を指定することができます。
 * </p>
 * <p>属性{@link XlsRecordFinder#value()} で、レコードの開始位置を検索する実装クラスを指定します。</p>
 * <p>属性{@link XlsRecordFinder#args()} で、レコードの開始位置を検索する実装クラスに渡す引数を指定します。</p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Weather")}
 * // マッピングの定義
 * public class SampleSheet {
 *
 *     {@literal @XlsOrder(1)}
 *     {@literal @XlsVerticalRecords(tableLabel="天気情報", tableLabelAbove=true, terminal=RecordTerminal.Border, terminateLabel="/{0-9}月{0-9}[1-2]日/")}
 *     {@literal @XlsRecordFinder(value=DateRecordFinder.class, args="2月1日")}
 *     private {@literal List<WeatherRecord>} date1;
 *
 *     {@literal @XlsOrder(2)}
 *     {@literal @XlsVerticalRecords(tableLabel="天気情報", tableLabelAbove=true, terminal=RecordTerminal.Border, terminateLabel="/{0-9}月{0-9}[1-2]日/")}
 *     {@literal @XlsRecordFinder(value=DateRecordFinder.class, args="2月1日")}
 *     private {@literal List<WeatherRecord>} date2;
 * }
 *
 * // 日にち用の見出しのレコードを探すクラス
 * public class DateRecordFinder implements RecordFinder {
 *
 *     {@literal @Override}
 *     public CellPosition find(ProcessCase processCase, String[] args, Sheet sheet,
 *             CellPosition initAddress, Object beanObj, Configuration config) {
 *
 *         // 実装は省略
 *     }
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/VerticalRecord_RecordFinder.png" alt="">
 *    <p>任意の位置のレコードをマッピングする場合</p>
 * </div>
 *
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
 * <p>これらの指定が可能な属性は、{@link #tableLabel()}、{@link #terminateLabel()}です。</p>
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
 *     {@literal @VerticalRecords(tableLabel="/ユーザ一覧.+/")}
 *     private {@literal List<UserRecord>} records;
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
public @interface XlsVerticalRecords {

    /**
     * レコードが見つからない場合に、エラーとしないで、無視して処理を続行するかどうかを指定します。
     * @return trueの場合、無視して処理を続行します。
     */
    boolean optional() default false;

    /**
     * 表の見出し（タイトル）ラベルを指定します。
     * 値を指定した場合、ラベルと一致するセルを起点に走査を行う。
     * <p>属性{@link #headerRow()},{@link #headerColumn()}{@link #headerAddress()}のどちらか一方を指定可能です。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     */
    String tableLabel() default "";

    /**
     * 表の名称（タイトル）ラベルの位置が上方に位置するかどうか指定します。
     * <p>実際に表を作る場合、垂直方向ですが表の名称は上方に設定することが一般的です。</p>
     * <p>そのような場合、属性 {@link #tableLabelAbove()} の値を'true' に設定すると表のタイトルが上方に位置するとして処理します。</p>
     *
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Weather")}
     * public class SampleSheet {
     *
     *     {@literal @XlsVerticalRecords(tableLabel="天気情報", tableLabelAbove=true)}
     *     private {@literal List<WeatherRecord>} records;
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/VerticalRecord_tableLabelAbove.png" alt="">
     *    <p>表の名称が上方にある場合</p>
     * </div>
     *
     *
     * @since 1.0
     * @return trueの場合、表の名称（タイトル）が上方にあるとして処理をします。
     */
    boolean tableLabelAbove() default false;

    /**
     * 表の終端を示すセルの文字列を指定します。
     * <p>テーブルが他のテーブルと連続しており、属性{@link #terminal()}でBorder、Emptyのいずれを指定しても終端を検出できない場合に指定します</p>
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return
     */
    String terminateLabel() default "";

    /**
     * 表の開始位置（見出し列）セルの行番号を指定する。
     * {@link #headerRow()}属性とセットで指定します。
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int headerColumn() default -1;

    /**
     * 表の開始位置（見出し行）セルの行番号を指定する。
     * {@link #headerColumn()}属性とセットで指定します。
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int headerRow() default -1;

    /**
     * 表の開始位置のセルのアドレスを指定します。
     * 値を指定した場合、指定したアドレスを起点に走査を行います
     * <p>属性{@link #headerRow()},{@link #headerColumn()}のどちらか一方を指定可能です。</p>
     *
     * @return 'A1'の形式で指定します。空文字は無視されます。
     */
    String headerAddress() default "";

    /**
     * レコードのマッピング先のクラスを指定します。
     * <p>省略した場合、定義されたGenericsタイプから取得します。</p>
     */
    Class<?> recordClass() default Object.class;

    /**
     * 表の終端の種類を指定します。
     * return {@link RecordTerminal#Empty}の場合、空のレコードがあると処理を終了します。
     */
    RecordTerminal terminal() default RecordTerminal.Empty;

    /**
     * 見出し用セルを走査するときの許容する空白セルの個数を指定します。
     * <p>見出しセルを走査する際には、下方向に向かって検索をしますが、通常は空白セルが見つかった時点で走査を終了します。
     *  <br>空白セルの次にも見出し用セルがあるような場合、属性{@link #range()}を指定することで、
     *    指定した値分の空白セルを許容し、さらに先のセルの検索を試みます。
     * </p>
     * <p>また、属性{@link #headerAddress()}や{@link #tableLabel()}で指定した位置から表が開始しないような場合も、
     *  属性{@link #range()}を指定することで、さらに先のセルの検索を試みます。
     * </p>
     * @return 値は1から始まります。
     */
    int range() default 1;

    /**
     * {@link #tableLabel()}で指定した表のタイトルから、実際の表の開始位置がどれだけ離れているか指定します。
     * <p>表の名称が定義してあるセルの直後に表がなく離れている場合、属性{@link #right()}で表の開始位置がどれだけ離れているか指定します。
     *  <br>右方向の列数を指定します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsHorizontalRecords(tableLabel="天気情報", right=3)}
     *     private {@literal List<WeatherRecord>} records;
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/VerticalRecord_right.png" alt="">
     *    <p>表の名称から右方向に離れている際の開始位置の指定</p>
     * </div>
     *
     *
     * @since 1.0
     * @return 値は1から始まります。
     */
    int right() default 1;

    /**
     * 属性{@link #tableLabelAbove()}がtrueのとき、表のタイトルが上部にあるときのみ有効になります。
     * <p>表の名称が定義してあるセルの直後に表がなく離れている場合、属性{@link #bottom()}で表の開始位置がどれだけ離れているか指定します。
     *  <br>下方向の行数を指定します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsHorizontalRecords(tableLabel="天気情報", tableLabelAbove=true, bottom=3)}
     *     private {@literal List<WeatherRecord>} records;
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/VerticalRecord_bottom.png" alt="">
     *    <p>表の名称から下方向に離れている際の開始位置の指定</p>
     * </div>
     *
     * @since 2.0
     * @return 値は1から始まります。
     */
    int bottom() default 1;

    /**
     * テーブルのカラムが指定数見つかったタイミングで Excelシートの走査を終了したい場合に指定します。
     * <p>主に無駄な走査を抑制したい場合にしますが、 {@link XlsIterateTables}使用時に、
     * テーブルが隣接しており終端を検出できない場合などに カラム数を明示的に指定してテーブルの区切りを指定する場合にも使用できます。
     *
     * @return 値は0から始まります。
     */
    int headerLimit() default 0;

    /**
     * 見出し用セルから、データ行の開始位置がどれだけ離れているかを指定します。
     * <p>表の見出しセルが横に結合され、データレコードの開始位置が離れている場合、属性{@link #headerRight()}でデータレコードの開始位置がどれだけ離れているか指定します。 `[ver1.1]`</p>
     * <p>下記の例の場合、見出しの「測定結果」は縦に結合されているため {@link XlsColumn#headerMerged()}と組み合わせて利用します。
     *
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Weather")}
     * public class SampleSheet {
     *
     *     // 見出しが横に結合され、データのレコードの開始位置が離れている場合
     *     {@literal XlsVerticalRecords(tableLabel="クラス情報", headerRight=2)}
     *         private {@literal List<SampleRecord>} records;
     *
     *     }
     * }
     *
     * // レコード用クラス
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="時間")}
     *     private String name;
     *
     *     // セル「降水」のマッピング
     *     {@literal @XlsColumn(columnName="測定結果")}
     *     private double precipitation;
     *
     *     // セル「気温」のマッピング
     *     // 結合されている見出しから離れている数を指定する
     *     {@literal @XlsColumn(columnName="測定結果", headerMerged=1)}
     *     private int temperature;
     *
     *     / セル「天気」のマッピング
     *     // 結合されている見出しから離れている数を指定する
     *     {@literal @XlsColumn(columnName="測定結果", headerMerged=2)}
     *     private String wather;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/VerticalRecord_headerRight.png" alt="">
     *    <p>表の見出しからデータレコードが離れているときの指定</p>
     * </div>
     *
     * @since 1.1
     * @return 値は1から始まります。
     */
    int headerRight() default 1;

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};

}
