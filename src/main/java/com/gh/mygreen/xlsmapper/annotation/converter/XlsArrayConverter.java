package com.gh.mygreen.xlsmapper.annotation.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * {@link java.util.Collection}({@link java.util.List}, {@link java.util.Set})または配列に対する変換規則を指定するアノテーション。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>{@link java.util.Collection}型のインタフェースを指定している場合、読み込み時のインスタンスは次のクラスが指定されます。
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
 *   <li>{@link XlsConverter#trim()}の値をtrueにすると、区切った項目にもトリム処理が適用されます。</li>
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
 *     {@literal @XlsConverter(trim=true)}    // 区切った配列の要素にもトリムが適用されます。
 *     {@literal @XlsArrayConverter(separator=",")}
 *     private int[] array;
 * 
 * }
 * </code></pre>
 * 
 * <h3 class="description">空の要素を無視する場合</h3>
 * 
 *  <p>属性{@link #ignoreEmptyItem()}で、区切った項目の値が空文字の場合、無視するか指定します。</p>
 *  
 *  <p>例えば、区切り文字が「,」のとき、セルの値が「{@code a,,b}」の場合、
 *     trueを設定すると、空の項目は覗き、{@code ["a", "b"]}として読み込みます。
 *    <br>書き込み時も同様に、値が空またはnullの項目を無視します。
 *  </p>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 * 
 *     {@literal @XlsColumn(columnName="集合")}
 *     {@literal @XlsArrayConverter(ignoreEmptyItem=true)}
 *     private {@literal Set<String>} set;
 *     
 * }
 * </code></pre>
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsArrayConverter {
    
    /**
     * 区切り文字
     * @return
     */
    String separator() default ",";
    
    /**
     * 空またはnullの項目は無視するか指定します。
     * @return
     */
    boolean ignoreEmptyItem() default false;
    
    /** 
     * 配列やリストの要素のクラス型を指定します。
     * <p>プリミティブ型とそのラッパークラスのみ指定できます。
     * <p>省略した場合、Genericsから自動的に判断してます。
     */
    Class<?> itemClass() default Object.class;
    
}
