package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * boolean/Booleanに対する変換規則を指定するアノテーションです。
 * <p>Excelのセルの種類が「ブール型」以外の場合に、Javaの「boolean/Boolean」にマッピング規則を定義できます。</p>
 *
 * <h3 class="description">読み込み時の値の指定</h3>
 *
 * <p>属性{@link #loadForTrue()}、{@link #loadForFalse()}で読み込み時の値の候補を指定します。</p>
 * <ul>
 *   <li>属性{@link #loadForTrue()}で、読み込み時のtrueとして判断する際の値を指定します。</li>
 *   <li>属性{@link #loadForFalse()}で、読み込み時のfalseとして判断する際の値を指定します。</li>
 *   <li>属性{@link #loadForTrue()}と{@link #loadForFalse()}の値に重複がある場合、{@link #loadForTrue()}の定義が優先されまます。</li>
 * </ul>
 *
 * <p>読み込み時に、大文字・小文字の区別を行わない場合、属性{@link #ignoreCase()}の値をtrueに設定します。</p>
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ステータス")}
 *     {@literal @XlsBooleanConverter(
 *             loadForTrue={"○", "有効", "レ"},
 *             loadForFalse={"×", "無効", "-", ""})}
 *     private boolean availabled;
 *
 *     {@literal @XlsColumn(columnName="チェック")}
 *     {@literal @XlsBooleanConverter(
 *             loadForTrue={"OK"},
 *             loadForFalse={"NO"},
 *             ignoreCase=true)}
 *     private Boolean checked;
 *
 * }
 * </code></pre>
 *
 * <h3 class="description">書き込み時の値の指定</h3>
 *
 * <p>属性{@link #saveAsTrue()}、{@link #saveAsFalse()}で書き込み時の値を指定します。</p>
 * <ul>
 *   <li>属性{@link #saveAsTrue()}で、書き込み時のtrueに該当する文字を指定します。</li>
 *   <li>属性{@link #saveAsFalse()}で、書き込み時のfalseに該当する文字を指定します。</li>
 *   <li>属性{@link #saveAsTrue()}と{@link #saveAsFalse()}を指定しない場合、セルの種類がブール型として書き込まれます。
 * </ul>
 *
 * <p>読み込みと書き込みの両方を行う場合、属性{@link #loadForTrue()}と{@link #loadForFalse()}の値に、
 *    {@link #saveAsTrue()}、{@link #saveAsFalse()}の値を含める必要があります。
 * </p>
 *
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ステータス")}
 *     {@literal @XlsBooleanConverter(
 *             loadForTrue={"○", "有効", "レ"}, //  読み書きの両方を行う場合、書き込む値を含める必要がある。
 *             loadForFalse={"×", "無効", "-", ""},
 *             saveAsTrue="○",
 *             saveAsFalse="-")}
 *     private boolean availabled;
 *
 * }
 * </code></pre>
 *
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsBooleanConverter {

    /**
     * 読み込み時に'true'と判断する候補を指定します。
     * @return
     */
    String[] loadForTrue() default {"true", "1", "yes", "on", "y", "t"};

    /**
     * 読み込み時に'false'と判断する候補を指定します。
     * @return
     */
    String[] loadForFalse() default {"false", "0", "no", "off", "f", "n"};

    /**
     * 書き込み時の'true'の値を指定します。
     * @return
     */
    String saveAsTrue() default "true";

    /**
     * 書き込み時の'false'の値を指定します。
     * @return
     */
    String saveAsFalse() default "false";

    /**
     * セルの読み込み時に、{@link #loadForTrue()}、{@link #loadForFalse()}で指定した候補と比較する際に文字の大小を無視するか指定します。
     * @return trueのとき、文字の大小を無視します。
     */
    boolean ignoreCase() default true;

    /**
     * セルの読み込み時に、{@link #loadForTrue()}、{@link #loadForFalse()}で指定した候補と一致しない場合、値をfalseとして読み込みます。
     *
     * <p>読み込み時にセルの値が、属性{@link #loadForTrue()}と{@link #loadForFalse()}で指定した中に、
     *    該当するものがない場合、例外{@link com.gh.mygreen.xlsmapper.cellconverter.TypeBindException}が発生します。</p>
     * <p>属性{@link #failToFalse()}をtrueに設定することで、変換できない場合に強制的に値をfalseとして読み込み、
     *    例外を発生しなくできます。</p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="ステータス")}
     *     {@literal @XlsBooleanConverter(
     *             loadForTrue={"○", "有効", "レ"},
     *             loadForFalse={"×", "無効", "-", ""},
     *             failToFalse=true)}
     *     private boolean availabled;
     *
     * }
     * </code></pre>
     *
     * @return trueのとき、読み込み時のマッピングに失敗しても、値をfalseとして処理します。
     */
    boolean failToFalse() default false;

}
