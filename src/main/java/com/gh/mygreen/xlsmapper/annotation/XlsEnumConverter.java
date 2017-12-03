package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 列挙型に対する変換規則を指定するアノテーションです。
 *
 * <h3 class="description">基本的な使い方</h3>
 *
 * <p>セルの値と列挙型の要素の値をマッピングさせます。
 *    <br>要素の値とは、 {@link Enum#name()} の値です。
 * </p>
 *
 * <ul>
 *   <li>属性{@link #ignoreCase()}の値をtrueにすると、読み込み時に大文字/小文字の区別なく変換します。
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="権限")}
 *     {@literal @XlsEnumConverter(ignoreCase=true)}
 *     private RoleType role;
 *
 * }
 *
 * // 列挙型の定義
 * public enum RoleType {
 *     Normal, Admin;
 * }
 * </code></pre>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsEnumConverter {

    /**
     * 読み込み時に大文字／小文字を無視して比較するか指定します。
     * @return trueの場合、大文字・小文字の区別は行いません。
     */
    boolean ignoreCase() default false;

    /**
     * 列挙型のname()メソッド以外から取得し、別名でマッピングするときに指定します。
     * <p>例). RoleType.localeName()のlocaleName()メソッドを指定するときには、'localeName'と指定します。</p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="権限")}
     *     {@literal @XlsEnumConverter(aliasMethod="localeName")}
     *     private RoleType role;
     *
     * }
     *
     * // 列挙型の定義
     * public enum RoleType {
     *     Normal("一般権限"), Admin("管理者権限");
     *
     *     // 別名の設定
     *     private String localeName;
     *
     *     private RoleType(String localeName) {
     *         this.localeName = localeName;
     *     }
     *
     *     // 別名の取得用メソッド
     *     public String localeName() {
     *         return this.localeName;
     *     }
     * }
     * </code></pre>
     *
     * @return 指定しない場合は、name()メソッドが使用されます。
     */
    String aliasMethod() default "";
}
