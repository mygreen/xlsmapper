package com.gh.mygreen.xlsmapper.annotation.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;


/**
 * 日時に対する変換規則を指定するアノテーション。
 * <p>対応するJavaの型は次の通り。</p>
 * <ul>
 *   <li>{@link java.util.Date}</li>
 *   <li>{@link java.sql.Date}/{@link java.sql.Time}/{@link java.sql.Timestamp}</li>
 *   <li>{@link java.util.Calendar}</li>
 * </ul>
 * 
 * <h3 class="description">読み込み時の書式の指定</h3>
 * <p>読み込み時にセルの種類が日付、時刻ではない場合、
 *    文字列として値を取得し、その値を属性{@link #pattern()}で指定した書式に従いパースし、Javaの日時型に変換します。
 * </p>
 * 
 * <ul>
 *   <li>属性{@link #pattern()}で書式を指定します。
 *       <br>Javaのクラス{@link SimpleDateFormat}で解釈可能な書式を指定します。
 *   </li>
 *   <li>属性{@link #locale()}で、ロケールを指定します。
 *       <br>言語コードのみを指定する場合、'ja'の2桁で指定します。
 *       <br>言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
 *   </li>
 *   <li>属性{@link #lenient()}で、日付/時刻の解析を厳密に行わないか指定します。
 *       <br>デフォルト値はtrueで、厳密に解析を行いません。falseの場合厳密に解析を行います。
 *   </li>
 *   <li>書式に合わない値をパースした場合、例外{@link com.gh.mygreen.xlsmapper.cellconvert.TypeBindException}が発生します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 * 
 *     {@literal @XlsColumn(columnName="有効期限")}
 *     {@literal @XlsDateConverter(pattern="yyyy年MM月dd日 HH時mm分ss秒", locale="ja_JP", lenient=true)}
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
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsDateConverter {
    
    /**
     * 日時の書式パターン。{@link java.text.SimpleDateFormat}の書式を指定します。
     * @return
     */
    String pattern();
    
    /**
     * 日付／時刻の解析を厳密に行うか指定します。
     * @return
     */
    boolean lenient() default false;
    
    /**
     * ロケールの指定を行います。指定しない場合、デフォルトのロケールで処理されます。
     * <p>例. 'ja_JP', 'ja'
     * @return
     */
    String locale() default "";
}
