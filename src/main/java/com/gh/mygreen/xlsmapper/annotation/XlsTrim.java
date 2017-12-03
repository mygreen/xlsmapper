package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 読み込み時、または書き込み時に値をトリミング行うかどうか指定するためのアノテーション。
 * 文字列型の時にのみ適用されます。
 *
 * <ul>
 *   <li>シート上のセルのタイプ（分類）が数値などの文字列以外の場合は、トリム処理は行われません。
 *       <br>ただし、シートのセルタイプが文字列型で、Javaの型がString型以外の数値型やDate型などの場合は、
 *          変換する前にトリム処理を行います。
 *   </li>
 *   <li>値が空のセルをString型に読み込む場合、アノテーション{@link XlsTrim}を付与していないとはnullが設定されますが、
 *       付与していると空文字が設定されます。
 *   </li>
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     {@literal @XlsTrim}
 *     {@literal @XlsDefaultValue("  123   ")} // 初期値もトリム対象となる。
 *     private Integer id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     {@literal @XlsTrim}   // 空のセルを読み込むと空文字が設定される。
 *     private String name;
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
public @interface XlsTrim {

}
