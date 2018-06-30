package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数値型に対する変換規則を指定するアノテーションです。
 * <p>対応するJavaのクラスタイプは次の通り。</p>
 * <ul>
 *   <li>byte/short/int/long/float/doubleのプリミティブ型とそのラッパークラス。</li>
 *   <li>{@link java.math.BigDecimal}/{@link java.math.BigInteger}</li>
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
public @interface XlsNumberConverter {

    /**
     * 数値の書式のパターン値を指定します。
     * <p>{@link java.text.DecimalFormat}で指定可能な書式を指定します。</p>
     *  <p>読み込み時にセルの種類が数値（通貨、会計、パーセンテージ、分数、指数）ではない場合、
     *    文字列として値を取得し、その値を属性{@link #javaPattern()}で指定した書式に従いパースし、Javaの数値型に変換します。
     * </p>
     *
     * <p>属性{@link #javaPattern()}を指定するとき、他に、属性として、{@link #locale()}、{@link #currency()}}が指定可能です。 </p>
     * <p>書式に合わない値をパースした場合、例外{@link com.gh.mygreen.xlsmapper.cellconverter.TypeBindException}が発生します。</p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="給料")}
     *     {@literal @XlsNumberConverter(pattern="\u00A4\u00A4 #,##0.0000", locale="ja_JP", currency="USD")}
     *     private double salary;
     *
     * }
     * </code></pre>
     *
     * @return
     */
    String javaPattern() default "";

    /**
     * 通貨を指定します。
     * <p>{@link java.util.Currency}で処理可能なコード(ISO 4217のコード)で指定します。</p>
     * <p>属性{@link #javaPattern()}を設定した場合に有効になります。
     * @return
     */
    String currency() default "";

    /**
     * ロケールの指定を行います。
     * <p>指定しない場合、デフォルトのロケールで処理されます。</p>
     * <p>国コード、バリアントを指定したい場合は、アンダーバー '_' で区切ります。</p>
     * <p>例. 'ja', 'ja_JP', 'ja_JP_JP'</p>
     * <p>属性{@link #javaPattern()}を設定した場合に有効になります。</p>
     */
    String locale() default "";

    /**
     * 有効桁数を指定します。
     * <p>Excel内部とJavaの数値は、表現可能な有効桁数が異なります。
     *    <br>そのため、特に読み込み時などExcelの仕様に合わせてJavaのクラスに指定することが可能です。
     * </p>
     * <p>Excelの場合、有効桁数は15桁であるため、デフォルト値は15です。</p>
     * <p>0以下の値を設定すると、桁数の指定を省略したことになり、無制限になります。</p>
     * <p>Excelでは有効桁数が15桁であるため、Javaのlong型など15桁を超える表現が可能な数値を書き込んだ場合、
     *    数値が丸められるため注意してください。
     * </p>
     * <ul>
     *   <li>例えば、long型の19桁の数値 {@code 1234567890123456789} を書き込んだ場合、
     *       16桁以降の値が丸められ {@code 1234567890123450000} として書き込まれます。
     *   <li>Excelの仕様については、
     *       <a href="https://support.office.com/ja-jp/article/Excel-%E3%81%AE%E4%BB%95%E6%A7%98%E3%81%A8%E5%88%B6%E9%99%90-1672b34d-7043-467e-8e27-269d656771c3?ui=ja-JP&rs=ja-JP&ad=JP" target="_blank">Excel の仕様と制限</a>
     *       を参照してください。
     * </ul>
     *
     * @since 0.5
     * @return
     */
    int precision() default 15;

    /**
     * Excelの書式のパターン。
     * <p>書き込み時に、セルの書式を直接設定したい場合に指定します。</p>
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
     *     {@literal @XlsColumn(columnName="給与")}
     *     {@literal @XlsNumberConverter(javaPattern="[$-411]\"￥\"#,##0.0000")}
     *     private double salary;
     *
     * }
     * </code></pre>
     *
     * @since 1.1
     */
    String excelPattern() default "";

}
