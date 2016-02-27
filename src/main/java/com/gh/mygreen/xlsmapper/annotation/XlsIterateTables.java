package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 同一の構造の表がシート内で繰り返し出現する場合に使用します。 
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <p>使い方を基本的な例を元に説明していきます。</p>
 * 
 * <div class="picture">
 *    <img src="doc-files/IterateTables.png">
 *    <p>基本的な例</p>
 * </div>
 * 
 * <p>シート用クラスの場合、属性{@link XlsIterateTables#tableLabel()}で、繰り返し部分の表の名称を指定します。</p>
 *    <br>また、属性{@link XlsIterateTables#bottom()}で、アノテーション{@link XlsHorizontalRecords}を
 *        マッピングする表の開始位置が表の名称からどれだけ離れているかを指定します。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="シート名")}
 * public class SampleRecord {
 *     
 *     {@literal @XlsSheet(name="シート名")}
 *     public class SampleSheet {
 *         
 *         {@literal @XlsIterateTables(tableLabel="部門情報", bottom=2)}
 *         private List<SampleTable> tables;
 *     }
 * }
 * </code></pre>
 * 
 * 
 * <p>繰り返し部分に対応するクラスは、以下のように、
 * アノテーション{@link XlsLabelledCell}、{@link XlsHorizontalRecords}を使用することができます。
 * </p>
 * 
 * <p>アノテーション{@link XlsHorizontalRecords} を使用する場合、
 * 属性{@link XlsIterateTables#tableLabel()}と同じ値を指定する必要がある点に注意してください。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // テーブル用クラス
 * public class SampleTable {
 *     
 *     {@literal @XlsLabelledCell(label="部門名", type=LabelledCellType.Right)}
 *     private String deptName;
 *     
 *     {@literal @XlsHorizontalRecords(tableLabel="部門情報")}
 *     private List<SampleRecord> records;
 * }
 * </code></pre>
 * 
 * <p>繰り返し部分に対応するJavaBeanでアノテーション {@link XlsHorizontalRecords} を使用した場合、
 * 通常の場合と同じくアノテーション{@link XlsColumn}や{@link XlsMapColumns}で列とのマッピングを行います。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // レコード用クラス
 * public class SampleRecord {
 *     
 *     {@literal @XlsColumn(columnName="ID")}
 *     private String id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 * }
 * </code></pre>
 * 
 * 
 * @author Mitsuyoshi Hasegawa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface XlsIterateTables {
    
    /**
     * 繰り返し部分の見出しラベルを指定します。
     * @return
     */
    String tableLabel();
    
    /**
     * 繰り返し部分の情報を格納するJavaBeanのクラス。
     * <p>指定しない場合は、Genericsの定義タイプを使用します。
     * @return
     */
    Class<?> tableClass() default Object.class;
    
    /**
     * {@link XlsIterateTables}内で{@link XlsHorizontalRecords}を使用する場合に、 
     * テーブルの開始位置が{@link XlsIterateTables}の見出しセルからどれだけ離れているかを指定します。
     * @return
     */
    int bottom() default -1;
    
    /**
     * 表が見つからなかった場合、無視するか指定します。
     * @return
     */
    boolean optional() default false;
}
