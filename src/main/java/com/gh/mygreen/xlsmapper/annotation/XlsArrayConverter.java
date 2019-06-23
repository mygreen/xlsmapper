package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultElementConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ElementConverter;


/**
 * {@link java.util.Collection}({@link java.util.List}, {@link java.util.Set})または配列に対する変換規則を指定するアノテーション。
 *
 * <h3 class="description">基本的な使い方</h3>
 * <p>{@link java.util.Collection}型のインタフェースを指定している場合、読み込み時のインスタンスは次のクラスが指定されます。</p>
 * <ul>
 *   <li>{@link java.util.List}の場合、{@link java.util.ArrayList}がインスタンスのクラスとなります。
 *   <li>{@link java.util.Set} の場合、{@link java.util.LinkedHashSet}がインスタンスのクラスとなります。
 * </ul>
 *
 * <p>配列、またはCollection型の要素で指定可能なクラスタイプは、次の通りです。</p>
 * <ul>
 *   <li>{@link String}型</li>
 *   <li>プリミティブ型「boolean/char/byte/short/int/long/float/double」と、そのラッパークラス。</li>
 *   <li>{@link java.math.BigDecimal}/{@link java.math.BigInteger}</li>
 * </ul>
 *
 * <p>文字列のセルに対して、任意の区切り文字を指定し、配列やListに対してマッピングします。</p>
 * <ul>
 *   <li>属性{@link #separator()}で区切り文字を指定します。
 *     <br>区切り文字の初期値は、半角カンマ(,)です。
 *   </li>
 *   <li>{@link XlsTrim}を設定すると、区切った項目にもトリム処理が適用されます。</li>
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     {@literal @XlsColumn(columnName="リスト")}
 *     {@literal @XlsArrayConverter(separator="\n")}  // 区切り文字を改行コード({@literal \\}n)として指定します。
 *     private {@literal List<String>} list;
 *
 *     {@literal @XlsColumn(columnName="配列")}
 *     {@literal @XlsTrim}    // 区切った配列の要素にもトリムが適用されます。
 *     {@literal @XlsArrayConverter(separator=",")}
 *     private int[] array;
 *
 * }
 * </code></pre>
 *
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsArrayConverter {

    /**
     * 区切り文字を指定します。
     * @return
     */
    String separator() default ",";

    /**
     * 区切った項目の値が空文字または{@literal null}の場合、無視するか指定します。
     *
     * <p>例えば、区切り文字が「,」のとき、セルの値が「{@code a,,b}」の場合、
     *    trueを設定すると、空の項目は覗き、{@code ["a", "b"]}として読み込みます。
     *   <br>書き込み時も同様に、値が空またはnullの項目を無視します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     {@literal @XlsColumn(columnName="集合")}
     *     {@literal @XlsArrayConverter(ignoreEmptyElement=true)}
     *     private {@literal Set<String>} set;
     *
     * }
     * </code></pre>
     *
     * @return trueのとき、区切った項目の値が空文字または{@literal null}の場合、無視します。
     */
    boolean ignoreEmptyElement() default false;

    /**
     * 配列やリストの要素のクラス型を指定します。
     * <p>プリミティブ型とそのラッパークラスのみ指定できます。</p>
     * <p>省略した場合、Genericsから自動的に判断してます。</p>
     */
    Class<?> elementClass() default Object.class;

    /**
     * 配列やリストの要素に対する変換用のクラスを指定します。
     * <p>変換するクラスは、インタフェース{@link ElementConverter}を実装している必要があります。
     *   <br>標準では、{@link DefaultElementConverter} が使用され、基本的な型のみサポートしています。
     * </p>
     * <p>インスタンスは、システム設定{@link Configuration#getBeanFactory()}経由で作成されるため、
     *   SpringFrameworkのコンテナからインスタンスを取得することもできます。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * // 変換用クラス
     * public class CustomElementConverter implements {@literal ElementConverter<User>} {
     *
     *     {@literal @Override}
     *     public User convertToObject(final String str, final {@literal Class<User>} targetClass) throws ConversionException {
     *         // 文字列 {@literal =>} オブジェクトに変換する処理
     *     }
     *
     *     {@literal @Override}
     *     public String convertToString(final User value) {
     *         // オブジェクト {@literal =>} 文字列に変換する処理
     *     }
     *
     * }
     *
     * // レコード用クラス
     * public class SampleRecord {
     *
     *     // 任意のクラス型の要素の値を変換するConverterを指定します。
     *     {@literal @XlsColumn(columnName="リスト")}
     *     {@literal @XlsArrayConverter(elementConverter=CustomElementConverter.class)}
     *     private {@literal List<User>} list;
     *
     * }
     * </code></pre>
     *
     *
     * @since 1.1
     * @return {@link ElementConverter}の実装クラス。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ElementConverter> elementConverter() default DefaultElementConverter.class;

}
