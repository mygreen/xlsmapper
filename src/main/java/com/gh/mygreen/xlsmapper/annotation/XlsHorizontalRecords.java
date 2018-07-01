package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 水平方向に連続する行をCollection(List, Set)または配列にマッピングする際に指定します。
 *
 * <h3 class="description">基本的な使い方</h3>
 * <p>表名を、属性{@link #tableLabel()}で指定します。</p>
 * <p>レコード用クラスは、列の定義をアノテーション{@link XlsColumn}で指定します。</p>
 *
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧")}
 *     private {@literal List<UserRecord>} records;
 * }
 *
 * // レコード用クラス
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/HorizontalRecord.png" alt="">
 *    <p>基本的な使い方</p>
 * </div>
 *
 *
 * <h3 class="description">書き込み時にレコードが不足、余分である場合の操作の指定</h3>
 *
 * <p>アノテーション{@link XlsRecordOption} 指定することで、書き込み時のレコードの制御を指定することができます。</p>
 * <p>属性{@link XlsRecordOption#overOperation()} で、書き込み時にJavaオブジェクトのレコード数に対して、シートのレコード数が足りないときの操作を指定します。</p>
 * <p>属性{@link XlsRecordOption#remainedOperation()} で、書き込み時にJavaオブジェクトのレコード数に対して、シートのレコード数が余っているときの操作を指定します。</p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧")}
 *     {@literal @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Clear)}
 *     private {@literal List<UserRecord>} records;
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/HorizontalRecord_RecordOption.png" alt="">
 *    <p>書き込み時の制御を行う場合</p>
 * </div>
 *
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
 * {@literal @XlsSheet(name="Users")}
 * // マッピングの定義
 * public class SampleSheet {
 *
 *     {@literal @XlsOrder(1)}
 *     {@literal @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border, terminateLabel="/クラス.+/")}
 *     {@literal @XlsRecordFinder(value=ClassNameRecordFinder.class, args="クラスA")}
 *     private {@literal List<UserRecord>} classA;
 *
 *     {@literal @XlsOrder(2)}
 *     {@literal @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border, terminateLabel="/クラス.+/")}
 *     {@literal @XlsRecordFinder(value=ClassNameRecordFinder.class, args="クラスB")}
 *     private {@literal List<UserRecord>} classB;
 * }
 *
 * // クラス用の見出しのレコードを探すクラス
 * public class ClassNameRecordFinder implements RecordFinder {
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
 *    <img src="doc-files/HorizontalRecord_RecordFinder.png" alt="">
 *    <p>任意の位置のレコードをマッピングする場合</p>
 * </div>
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
 *     {@literal @XlsHorizontalRecords(tableLabel="/ユーザ一覧.+/")}
 *     private {@literal List<UserRecord>} records;
 *
 * }
 * </code></pre>
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
public @interface XlsHorizontalRecords {

    /**
     * レコードが見つからない場合に、エラーとしないで、無視して処理を続行するかどうかを指定します。
     * @return trueの場合、無視しして処理を続行します。
     */
    boolean optional() default false;

    /**
     * 表の見出し（タイトル）ラベルを指定します。
     * 値を指定した場合、ラベルと一致するセルを起点に走査を行います。
     * <p>属性{@link #headerRow()},{@link #headerColumn()}{@link #headerAddress()}のどちらか一方を指定可能です。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return
     */
    String tableLabel() default "";

    /**
     * 表の終端を示すセルの文字列を指定します。
     * <p>表が他の表と連続しており属性{@link #terminal()}で{@link RecordTerminal#Border}、{@link RecordTerminal#Empty}のいずれを指定しても終端を検出できない場合があります。
     *  <br>このような場合は属性{@link #terminateLabel()}で終端を示すセルの文字列を指定します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // レコードの読み飛ばしを有効にします。
     *     {@literal @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border,
     *             terminateLabel="平均")}
     *     private {@literal List<UserRecord>} records;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_terminateLabel.png" alt="">
     *    <p>表の終端セルの指定</p>
     * </div>
     *
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return
     */
    String terminateLabel() default "";

    /**
     * 表の開始位置（見出し行）セルの行番号を指定します。
     * {@link #headerColumn()}属性とセットで指定します。
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // インデックス形式で表の開始位置を指定する(値は0から始まります)
     *     {@literal @XlsHorizontalRecords(headerColumn=0, headerRow=1)}
     *     private {@literal List<UserRecord>} records1;
     *
     * }
     * </code></pre>
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int headerRow() default -1;

    /**
     * 表の開始位置（見出し列）セルの行番号を指定します。
     * {@link #headerRow()}属性とセットで指定します。
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // インデックス形式で表の開始位置を指定する(値は0から始まります)
     *     {@literal @XlsHorizontalRecords(headerColumn=0, headerRow=1)}
     *     private {@literal List<UserRecord>} records1;
     *
     * }
     * </code></pre>
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int headerColumn() default -1;

    /**
     * 表の開始位置のセルのアドレスを指定します。
     * 値を指定した場合、指定したアドレスを起点に走査を行います
     *
     * <p>属性{@link #headerRow()},{@link #headerColumn()}のどちらか一方を指定可能です</p>
     * <p>表の名称がない場合、表の開始位置をインデックスやアドレスで指定します。</p>
     * <ul>
     *  <li>{@link #headerAddress()}で、{@code B3}のようにシートのアドレス形式で指定します。</li>
     * </ul>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // アドレス形式で表の開始位置を指定する場合
     *     {@literal @XlsHorizontalRecords(headerAddress="A2")}
     *     private {@literal List<UserRecord>} records2;
     * }
     * </code></pre>
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
     * <p>デフォルトでは行に1つもデータが存在しない場合、その表の終端となります。
     *   行の一番左側の列の罫線によって表の終端を検出する方法もあります。
     *   この場合は 属性{@link #terminal()}に {@link RecordTerminal#Border} を指定してください。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧"), terminal=RecordTerminal.Border)}
     *     private {@literal List<UserRecord>} records;
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_terminal.png" alt="">
     *    <p>表の終端の指定</p>
     * </div>
     *
     * @return {@link RecordTerminal#Empty}の場合、空のレコードがあると処理を終了します。
     */
    RecordTerminal terminal() default RecordTerminal.Empty;

    /**
     * 見出し用セルを走査するときの許容する空白セルの個数を指定します。
     * <p>見出しセルを走査する際には、右方向に向かって検索をしますが、通常は空白セルが見つかった時点で走査を終了します。
     *  <br>空白セルの次にも見出し用セルがあるような場合、属性{@link #range()}を指定することで、
     *    指定した値分の空白セルを許容し、さらに先のセルの検索を試みます。
     * </p>
     * <p>また、属性{@link #headerAddress()}や{@link #tableLabel()}で指定した位置から表が開始しないような場合も、
     *  属性{@link #range()}を指定することで、さらに先のセルの検索を試みます。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // レコードの読み飛ばしを有効にします。
     *     {@literal @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border,
     *             range=3)}
     *     private {@literal List<UserRecord>} records;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_range.png" alt="">
     *    <p>表の見出しに空白がある場合</p>
     * </div>
     *
     * @return 値は1から始まります。
     */
    int range() default 1;

    /**
     * {@link #tableLabel()}で指定した表のタイトルから、実際の表の開始位置がどれだけ離れているか指定します。
     * <p>表の名称が定義してあるセルの直後に表がなく離れている場合、属性{@link #bottom()}で表の開始位置がどれだけ離れているか指定します。
     *  <br>下方向の行数を指定します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧", bottom=3)}
     *     private {@literal List<UserRecord>} records;
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_bottom.png" alt="">
     *    <p>表の名称から離れている際の開始位置の指定</p>
     * </div>
     *
     * @return 値は1から始まります。
     */
    int bottom() default 1;

    /**
     * 表の見出しとなるセルのカラムが指定数見つかったタイミングで Excelシートの走査を終了したい場合に指定します。
     * <p>主に無駄な走査を抑制したい場合にします。</p>
     * <p>テーブルが隣接しており終端を検出できない場合などに、
     *   見出し用セルのカラム数を明示的に指定してテーブルの区切りを指定する場合に使用できます。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // レコードの読み飛ばしを有効にします。
     *     {@literal @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border,
     *             headerLimit=3)}
     *     private {@literal List<UserRecord>} records;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_headerLimit.png" alt="">
     *    <p>表の見出しの走査の終了条件の指定</p>
     * </div>
     *
     * @return 値は0から始まります。
     */
    int headerLimit() default 0;

    /**
     * 見出し用セルから、データ行の開始位置がどれだけ離れているかを指定します。
     * <p>表の見出しセルが縦に結合され、データレコードの開始位置が離れている場合、属性{@link #headerBottom()}でデータレコードの開始位置がどれだけ離れているか指定します。
     *   <br>下方向の行数を指定します。
     * </p>
     * <p>下記の例の場合、見出しの「テスト結果」は横に結合されているため {@link XlsColumn#headerMerged()}と組み合わせて利用します。</p>
     *
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // 見出しが縦に結合され、データのレコードの開始位置が離れている場合
     *     {@literal @XlsHorizontalRecords(tableLabel="クラス情報", headerBottom=2)}
     *         private {@literal List<SampleRecord>} records;
     *
     *     }
     * }
     *
     * // レコード用クラス
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="No.")}
     *     private int no;
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     private String name;
     *
     *     // セル「算数」のマッピング
     *     {@literal @XlsColumn(columnName="テスト結果")}
     *     private int sansu;
     *
     *     // セル「国語」のマッピング
     *     // 結合されている見出しから離れている数を指定する
     *     {@literal @XlsColumn(columnName="テスト結果", headerMerged=1)}
     *     private int kokugo;
     *
     *     / セル「合計」のマッピング
     *     // 結合されている見出しから離れている数を指定する
     *     {@literal @XlsColumn(columnName="テスト結果", headerMerged=2)}
     *     private int sum;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_headerBottom.png" alt="">
     *    <p>表の見出しからデータレコードが離れているときの指定</p>
     * </div>
     *
     * @since 1.1
     * @return 値は1から始まります。
     */
    int headerBottom() default 1;

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};

}
