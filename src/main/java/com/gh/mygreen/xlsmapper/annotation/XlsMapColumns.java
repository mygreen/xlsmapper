package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * アノテーション{@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で指定されたレコード用のクラスにおいて、
 * カラム数が可変の場合にそれらのカラムを{@link java.util.Map}として設定します。
 * 
 * <p>BeanにはMapを引数に取るフィールドまたはメソッドを用意し、このアノテーションを記述します。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #previousColumnName()}で指定された次のカラム以降、カラム名をキーとした{@link java.util.Map}が生成され、Beanにセットされます。</p>
 * <p>マップのキーは必ず{@link String}型に設定してください。</p>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *     
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *     
 *     {@literal @XlsMapColumns(previousColumnName="名前")}
 *     private {@literal Map<String, String>} attendedMap;
 *     
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/MapColumns.png">
 *    <p>基本的な使い方</p>
 * </div>
 *
 * <h3 class="description">型変換する場合</h3>
 * <p>アノテーション{@link com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter}などで型変換を適用するときは、Mapの値が変換対象となります。
 *    <br>マップのキーは必ず{@link String}型に設定してください
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *     
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *     
 *     // 型変換用のアノテーションを指定した場合、Mapの値に適用されます。
 *     {@literal @XlsMapColumns(previousColumnName="名前")}
 *     {@literal @XlsBooleanConverter(loadForTrue={"出席"}, loadForFalse={"欠席"},
 *             saveAsTrue="出席", saveAsFalse"欠席"
 *             failToFalse=true)}
 *     private {@literal Map<String, Boolean>} attendedMap;
 *     
 * }
 * </code></pre>
 *
 * <h3 class="description">位置情報／見出し情報を取得する際の注意事項</h3>
 * <p>マッピング対象のセルのアドレスを取得する際に、フィールド{@literal Map<String, Point> positions}を定義しておけば、
 *    自動的にアドレスがマッピングされます。
 *    <br>通常は、キーにはプロパティ名が記述（フィールドの場合はフィールド名）が入ります。
 *    <br>アノテーション{@link XlsMapColumns}でマッピングしたセルのキーは、{@literal <プロパティ名>[<セルの見出し>]}の形式になります。
 * </p>
 * <p>同様に、マッピング対象の見出しを取得する、フィールド{@literal Map<String, String> labels}へのアクセスも、
 *    キーは{@literal <プロパティ名>[<セルの見出し>]}の形式になります。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *     
 *     // 位置情報
 *     private {@literal Map<String, Point>} positions;
 *     
 *     // 見出し情報
 *     private {@literal Map<String, String>} labels;
 *     
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *     
 *     {@literal @XlsMapColumns(previousColumnName="名前")}
 *     private {@literal Map<String, String>} attendedMap;
 *     
 * }
 * 
 * 
 * // 位置情報・見出し情報へのアクセス
 * SampleRecord record = ...;
 * 
 * Point position = record.positions.get("attendedMap[4月2日]");
 * 
 * String label = recrod.labeles.get("attendedMap[4月2日]");
 * </code></pre>
 * 
 * 
 * <div class="picture">
 *    <img src="doc-files/MapColumns_positions.png">
 *    <p>位置情報・見出し情報の取得</p>
 * </div>
 *
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsMapColumns {
    
    /**
     * この属性で指定した次のカラム以降、カラム名をキーとしたMapが生成され、Beanにセットされます。
     * @return
     */
    String previousColumnName();
    
    /** 
     * マップの値のクラスを指定します。
     * <p>省略した場合、定義されたたGenericsの情報から取得します。
     */
    Class<?> itemClass() default Object.class;
}
