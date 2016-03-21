package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;

/**
 * 垂直方向に連続する列をCollection(List, Set)または配列にマッピングする際に指定します。
 * <p>アノテーション{@link XlsHorizontalRecords}を垂直方向にしたものであり、使い方はほとんど同じです。
 * <p>ここでは、アノテーション{@link XlsHorizontalRecords}と異なる部分を説明します。
 *    <br>詳細は、アノテーション{@link XlsHorizontalRecords}の説明を参照してください。
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
 *    <img src="doc-files/VerticalRecord.png">
 *    <p>基本的な使い方</p>
 * </div>
 *
 *
 * <h3 class="description">表の名称の位置の指定</h3>
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
 *    <img src="doc-files/VerticalRecord_tableLabelAbove.png">
 *    <p>表の名称が上方にある場合</p>
 * </div>
 * 
 * 
 * <h3 class="description">表の名称から開始位置が離れた場所にある場合</h3>
 * <p>表の名称が定義してあるセルの直後に表がなく離れている場合、属性{@link #right()}で表の開始位置がどれだけ離れているか指定します。</p>
 * <p>{@link XlsHorizontalRecords#bottom()} と同じような意味になります。</p>
 * <p>さらに、属性 {@link #tableLabelAbove()} と組み合わせると、下方向にどれだけ離れているかの意味になります。
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
 *    <img src="doc-files/VerticalRecord_right.png">
 *    <p>表の名称から離れている際の開始位置の指定</p>
 * </div>
 * 
 * 
 * <h3 class="description">表の名称から開始位置が離れた場所にある場合</h3>
 * <p>表の名称が定義してあるセルの直後に表がなく離れている場合、属性{@link #bottom()}で表の開始位置がどれだけ離れているか指定します。</p>
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
 * 
 * <h3 class="description">表の見出しが横に結合されデータレコードの開始位置が離れた場所にある場合</h3>
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
 *    <img src="doc-files/VerticalRecord_headerRight.png">
 *    <p>表の見出しからデータレコードが離れているときの指定</p>
 * </div>
 * 
 *  
 * 
 * <h3 class="description">書き込み時にレコードが不足、余分である場合の操作の指定</h3>
 * <p>属性{@link #overRecord()}、属性{@link #remainedRecord()}で、書き込み時のレコードの操作を指定することができますが、
 *    {@link XlsHorizontalRecords}の場合は一部の設定が使用できません。
 * </p>
 * 
 * <p>{@link XlsVerticalRecords#overRecord()} の場合、列の挿入を行う{@link OverRecordOperate#Insert}は使用できません。
 *   <br>また、{@link XlsVerticalRecords#remainedRecord()} の場合、列の削除を行う{@link RemainedRecordOperate#Delete}は使用できません。
 *   <br>これは、Apache POIが一括で列の挿入や削除の処理を行うことをサポートしていないためです。
 *</p>
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
 *     {@literal @VerticalRecords(tableLabel="/ユーザ一覧.+/")}
 *     private {@literal List<UserRecord>} records;
 *     
 * }
 * </code></pre>
 * 
 * @version 1.1
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
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
     * @return 値は0から始まり、指定しない場合は-1を指定します。
     */
    int headerColumn() default -1;
    
    /**
     * 表の開始位置（見出し行）セルの行番号を指定する。
     * @return 値は0から始まり、指定しない場合は-1を指定します。
     */
    int headerRow() default -1;
    
    /**
     * 表の開始位置のセルのアドレスを'A1'などのように指定します。値を指定した場合、指定したアドレスを起点に走査を行います
     * <p>属性{@link #headerRow()},{@link #headerColumn()}のどちらか一方を指定可能です。
     * @return 
     */
    String headerAddress() default "";
    
    /** 
     * レコードのマッピング先のクラスを指定します。
     * <p>指定しない場合は、Genericsの定義タイプを使用します。
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
     * @return 値は1から始まります。初期値は1です。
     */
    int range() default 1;
    
    /**
     * {@link #tableLabel()}で指定した表のタイトルから、実際の表の開始位置がどれだけ離れているか指定する。
     * <p>右方向の列数を指定する。
     * <p>{@link #tableLabelAbove()}の値がtrueの場合は、下方向に行数になる。
     * @since 1.0
     * @return 値は1から始まり、指定しない場合は1を指定します。
     */
    int right() default 1;
    
    /**
     * テーブルのカラムが指定数見つかったタイミングで Excelシートの走査を終了したい場合に指定します。
     * <p>主に無駄な走査を抑制したい場合にしますが、 {@link XlsIterateTables}使用時に、
     * テーブルが隣接しており終端を検出できない場合などに カラム数を明示的に指定してテーブルの区切りを指定する場合にも使用できます。 
     * 
     * @return 値は0から始まり、指定しない場合は0を指定します。
     */
    int headerLimit() default 0;
    
    /**
     * 見出し用セルから、データ行の開始位置がどれだけ離れているかを指定します。
     * <p>右方向の列数を指定します。</p>
     * <p>見出しが横に結合されているような場合に指定します。</p>
     * @since 1.1
     * @return 値は1から始まり、指定しない場合は1を指定します。
     */
    int headerRight() default 1;
    
    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
     * {@link XlsVerticalRecords}の場合、{@link OverRecordOperate#Insert}は対応していません。
     * @return {@link OverRecordOperate#Break}の場合、足りないレコードがあるとそこで処理を終了します。
     */
    OverRecordOperate overRecord() default OverRecordOperate.Break;
    
    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
     * {@link XlsVerticalRecords}の場合、{@link RemainedRecordOperate#Delete}は対応していません。
     * @return {@link RemainedRecordOperate#None}の場合、余っているレコードがあっても何もしません。
     */
    RemainedRecordOperate remainedRecord() default RemainedRecordOperate.None; 
    
    /**
     * 空のレコードの場合、処理を無視するかどうか。
     * <p>レコードの判定用のメソッドに、アノテーション{@link XlsIsEmpty}を付与する必要があります。
     * @since 0.2
     * @return trueの場合、空のレコードを無視します。
     */
    boolean ignoreEmptyRecord() default false;
}
