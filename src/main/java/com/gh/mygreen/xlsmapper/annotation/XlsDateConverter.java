package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * 日時に対する変換規則を指定するアノテーションです。
 * <p>対応するJavaのクラスタイプは次の通りです。</p>
 * <ul>
 *   <li>{@link java.util.Date}</li>
 *   <li>{@link java.sql.Date}/{@link java.sql.Time}/{@link java.sql.Timestamp}</li>
 *   <li>{@link java.util.Calendar}</li>
 * </ul>
 * 
 * 
 * <h3 class="description">書き込み時の注意事項</h3>
 * 
 * <p>テンプレートファイルのセルの書式を「標準」に設定している場合に書き込むと、
 *     書式が「標準」設定の全てのセルの書式が書き換わってしまいます。
 *   <br>そのため、日付や数値などの書式が必要な場合は、テンプレートファイルで予め書式を設定しておくか、
 *     アノテーションの属性{@link #excelPattern()}で書式を指定しておいてください。
 * </p>
 * 
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsDateConverter {
    
    /**
     * 日時の書式パターン。{@link java.text.SimpleDateFormat}の書式を指定します。
     * <p>読み込み時にセルの種類が日付、時刻ではない場合、
     *    文字列として値を取得し、その値を属性{@link #javaPattern()}で指定した書式に従いパースし、Javaの日時型に変換します。
     * </p>
     * 
     * <ul>
     *   <li>属性{@link #javaPattern()}で書式を指定します。
     *       <br>Javaのクラス{@link SimpleDateFormat}で解釈可能な書式を指定します。
     *   </li>
     *   <li>属性{@link #locale()}で、ロケールを指定します。
     *       <br>言語コードのみを指定する場合、'ja'の2桁で指定します。
     *       <br>言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
     *   </li>
     *   <li>属性{@link #lenient()}で、日付/時刻の解析を厳密に行わないか指定します。
     *       <br>デフォルト値はtrueで、厳密に解析を行いません。falseの場合厳密に解析を行います。
     *   </li>
     *   <li>書式に合わない値をパースした場合、例外{@link com.gh.mygreen.xlsmapper.cellconverter.TypeBindException}が発生します。</li>
     * </ul>
     * 
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     * 
     *     {@literal @XlsColumn(columnName="有効期限")}
     *     {@literal @XlsDateConverter(javaPattern="yyyy年MM月dd日 HH時mm分ss秒", locale="ja_JP", lenient=true)}
     *     private Date expired;
     * 
     * }
     * </code></pre>
     * 
     * <p>アノテーション{@link XlsDateConverter}を付与しない場合、Javaの型ごとに次の書式が標準で適用されます。</p>
     * <ul>
     *   <li>{@link java.util.Date}の場合、{@literal "yyyy-MM-dd HH:mm:ss"}</li>
     *   <li>{@link java.sql.Date}の場合、{@literal "yyyy-MM-dd HH:mm:ss"}</li>
     *   <li>{@link java.sql.Time}の場合、{@literal "HH:mm:ss"}</li>
     *   <li>{@link java.sql.Timestamp}の場合、{@literal "yyyy-MM-dd HH:mm:ss.SSS"}</li>
     *   <li>{@link java.util.Calendar}の場合、{@literal "yyyy-MM-dd HH:mm:ss"}</li>
     * </ul>
     * 
     * @since 1.1
     * @return
     */
    String javaPattern() default "";
    
    /**
     * 日付／時刻の解析を厳密に行うか指定します。
     * <p>属性{@link #javaPattern()}を設定した場合に有効になります。</p>
     * @return
     */
    boolean lenient() default false;
    
    /**
     * ロケールの指定を行います。指定しない場合、デフォルトのロケールで処理されます。
     * <p>国コード、バリアントを指定したい場合は、アンダーバー '_' で区切ります。</p>
     * <p>例. 'ja', 'ja_JP', 'ja_JP_JP'</p>
     * <p>属性{@link #javaPattern()}を設定した場合に有効になります。</p>
     * @return
     */
    String locale() default "";
    
    /**
     * タイムゾーンを指定します。
     * <p>{@link TimeZone#getTimeZone(String)}で解釈可能な値を指定する必要があります。</p>
     * <p>{@literal Asia/Tokyo, GMT, GMT+09:00}などの値を指定します。</p>
     * <p>ただし、オフセットを持たないクラスタイプ{@literal LocalDateTime, LocalDate, LocalTime}の時は、指定しても意味がありません。</p>
     * @since 2.0
     * @return 省略した場合、システム標準の値を使用します。
     */
    String timezone() default "";
    
    /**
     * 書き込み時にセルの書式を直接設定したい場合に指定します。
     * <p>値を指定しない場合、テンプレートファイルに設定してある書式が適用されます。</p>
     * 
     * <ul>
     *   <li>属性{@link #excelPattern()}で書式を指定します。
     *       <br>Excelの書式を指定する場合は、
     *           <a href="http://mygreen.github.io/excel-cellformatter/sphinx/format_basic.html" target="_blank">ユーザ定義</a>
     *           の形式で指定します。
     *   </li>
     * </ul>
     * 
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     * 
     *     {@literal @XlsColumn(columnName="有効期限")}
     *     {@literal @XlsDateConverter(excelPattern="[$-411]yyyy\"年\"mm\"月\"dd\"日\" hh\"時\"mm\"分\"ss\"秒\"")}
     *     private Date expired;
     * 
     * }
     * </code></pre>
     * 
     * @since 1.1
     */
    String excelPattern() default "";
    
}
