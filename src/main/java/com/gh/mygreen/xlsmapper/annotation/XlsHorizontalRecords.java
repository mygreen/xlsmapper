package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;

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
 *    <img src="doc-files/HorizontalRecord.png">
 *    <p>基本的な使い方</p>
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
 *   <li>正規表現の指定機能を有効にするには、システム設定のプロパティ {@link XlsMapperConfig#setRegexLabelText(boolean)} の値を trueに設定します。</li>
 * </ul>
 * 
 * <p>ラベセルの値に改行が空白が入っている場合、それらを除去し正規化してアノテーションの属性値と比較することが可能です。</p>
 * <ul>
 *   <li>正規化とは、空白、改行、タブを除去することを指します。</li>
 *   <li>ラベルを正規化する機能を有効にするには、、システム設定のプロパティ {@link XlsMapperConfig#setNormalizeLabelText(boolean)} の値を trueに設定します。</li>
 * </ul>
 * 
 * <p>これらの指定が可能な属性は、{@link #tableLabel()}、{@link #terminateLabel()}です。</p>
 * 
 * <pre class="highlight"><code class="java">
 * 
 * // システム設定
 * XlsMapper xlsMapper = new XlsMapper();
 * xlsMapper.getConfig()
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
 * @version 1.1
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
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
     *    <img src="doc-files/HorizontalRecord_terminateLabel.png">
     *    <p>表の終端セルの指定</p>
     * </div>
     * 
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return
     */
    String terminateLabel() default "";
    
    /**
     * 表の開始位置（見出し行）セルの行番号を指定します。{@link #headerColumn()}属性とセットで指定します。
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
     * @return 値は0から始まり、指定しない場合は-1を指定します。
     */
    int headerRow() default -1;
    
    /**
     * 表の開始位置（見出し列）セルの行番号を指定します。{@link #headerRow()}属性とセットで指定します。
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
     * @return 値は0から始まり、指定しない場合は-1を指定します。</p>
     */
    int headerColumn() default -1;
    
    /**
     * 表の開始位置のセルのアドレスを'A1'などのように指定します。値を指定した場合、指定したアドレスを起点に走査を行います
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
     * @return 
     */
    String headerAddress() default "";
    
    
    /** 
     * レコードのマッピング先のクラスを指定します。
     * <p>指定しない場合は、Genericsの定義タイプが自動的に採用されます。</p>
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
     *    <img src="doc-files/HorizontalRecord_terminal.png">
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
     *    <img src="doc-files/HorizontalRecord_range.png">
     *    <p>表の見出しに空白がある場合</p>
     * </div>
     * 
     * @return 値は1から始まります。初期値は1です。
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
     *    <img src="doc-files/HorizontalRecord_bottom.png">
     *    <p>表の名称から離れている際の開始位置の指定</p>
     * </div>
     * 
     * @return 値は1から始まり、指定しない場合は1を指定します。
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
     *    <img src="doc-files/HorizontalRecord_headerLimit.png">
     *    <p>表の見出しの走査の終了条件の指定</p>
     * </div>
     * 
     * @return 値は0から始まり、指定しない場合は0を指定します。
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
     *    <img src="doc-files/HorizontalRecord_headerBottom.png">
     *    <p>表の見出しからデータレコードが離れているときの指定</p>
     * </div>
     * 
     * @since 1.1
     * @return 値は1から始まり、指定しない場合は1を指定します。
     */
    int headerBottom() default 1;
    
    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
     * <p>値は、列挙型{@link OverRecordOperate}で指定し、行の挿入や、上部のセルをコピーするなど指定ができます。
     *    <br>デフォルトでは何もしません。
     * </p>
     * 
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *     
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧",
     *             overRecord=OverRecordOperate.Insert)}
     *     private {@literal List<UserRecord>} records;
     *     
     * }
     * </code></pre>
     * 
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_overRecord.png">
     *    <p>表の書き込み時の不足するレコードの操作の指定</p>
     * </div>
     * 
     * @return {@link OverRecordOperate#Break}の場合、足りないレコードがあるとそこで処理を終了します。
     */
    OverRecordOperate overRecord() default OverRecordOperate.Break;
    
    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
     * <p>値は、列挙型{@link RemainedRecordOperate}で指定し、行の値のクリアや、行の削除を指定することができます。
     *   <br>デフォルトでは何もしません。
     * </p>
     * 
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *     
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧",
     *             remainedRecord=RemainedRecordOperate.Clear)}
     *     private {@literal List<UserRecord>} records;
     *     
     * }
     * </code></pre>
     * 
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_remainedRecord.png">
     *    <p>表の書き込み時の余分なレコードの操作の指定</p>
     * </div>
     * 
     * @return {@link RemainedRecordOperate#None}の場合、余っているレコードがあっても何もしません。
     */
    RemainedRecordOperate remainedRecord() default RemainedRecordOperate.None;
    
    /**
     * 空のレコードの場合、処理を無視するかどうか。
     * 
     * <p>空のレコードが存在すると無駄なレコードをとなり、読み込んだ後に除外する処理をわざわざ行う必要があります。
     *   <br>そのような場合、属性{@link #ignoreEmptyRecord()}を'true'に設定することで、予め空のレコードを読み飛ばしておく方法もあります。
     * </p>
     * <p>レコード用クラスには、空を判定するメソッドを用意し、アノテーション {@link XlsIsEmpty}を付与します。
     *    <br>publicかつ引数なしの戻り値がboolean形式の書式にする必要があります。
     * </p>
     * <p>ただし、書き込み時にはこの設定は無効で、空のレコードも出力されます。</p>
     * 
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *     
     *     // レコードの読み飛ばしを有効にします。
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧"), terminal=RecordTerminal.Border,
     *             ignoreEmptyRecord=true)}
     *     private {@literal List<UserRecord>} records;
     *     
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
     *     {@literal @XlsColumn(columnName="住所")}
     *     private String address;
     *     
     *     // レコードが空と判定するためのメソッド
     *     // 列「ID」は空の判定には含まない。
     *     {@literal @XlsIsEmpty}
     *     public boolean isEmpty() {
     *         if(name != null || !name.isEmpty()) {
     *             return false;
     *         }
     *         
     *         if(address != null || !address.isEmpty()) {
     *             return false;
     *         } 
     *     }
     * }
     * </code></pre>
     * 
     * 
     * アノテーション{@link XlsIsEmpty}を付与したメソッドの実装において、{@link com.gh.mygreen.xlsmapper.IsEmptyBuilder}を使用すると、
     * より簡潔に記述することができます。
     * 
     * <pre class="highlight"><code class="java">
     * // レコード用クラス
     * public class UserRecord {
     * 
     *     {@literal @XlsColumn(columnName="ID")}
     *     private int id;
     *     
     *     {@literal @XlsColumn(columnName="名前")}
     *     private String name;
     *     
     *     {@literal @XlsColumn(columnName="住所")}
     *     private String address;
     *     
     *     // レコードが空と判定するためのメソッド
     *     {@literal @XlsIsEmpty}
     *     public boolean isEmpty() {
     *         // 列「ID」は空の判定には含まないよう除外する。
     *         return IsEmptyBuilder.reflectionIsEmpty(this, "id");
     *     }
     * }
     * </code></pre>
     * 
     * @since 0.2
     * @return trueの場合、空のレコードを無視します。
     */
    boolean ignoreEmptyRecord() default false;
}
