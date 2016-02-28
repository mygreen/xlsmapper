package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * アノテーション{@link XlsHorizontalRecords}や{@link XlsVerticalRecords}のレコード用のクラスにおいて、
 * 表の見出しのセルを指定し、クラスのプロパティをカラム名にマッピングします。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #columnName()}で見出しとなるセルのラベルを指定します。</p>
 * <p>セルが見つからない場合はエラーとなりますが、属性{@link #optional()}を'true'とすることで無視して処理を続行します。</p>
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
 *     // 存在しない列の場合は読み飛ばす
 *     {@literal @XlsColumn(columnName="備考", optional=true)}
 *     private String name;
 *     
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/Column.png">
 *    <p>基本的な使い方</p>
 * </div>
 * 
 * 
 * <h3 class="description">データの列が結合されている場合</h3>
 * <p>同じ値がグループごとに結合されているカラムの場合は属性{@link #merged()}をtrueに設定します。 
 *    <br>こうしておくと、前の列の値が引き継がれて設定されます。
 * </p>
 * <p>書き込み時では、属性{@link #merged()}の値が `true` であっても、上部または左側のセルと値が同じでも結合は基本的に行いません。
 *     <br>ただし、システム設定 {@link com.gh.mygreen.xlsmapper.XlsMapperConfig#setMergeCellOnSave(boolean)}の値をtrueに設定することで結合されます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *     
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *     
 *     // 結合されてる可能性がある列
 *     {@literal @XlsColumn(columnName="クラス", merged=true)}
 *     private String className;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *     
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/Column_merged.png">
 *    <p>データの列が結合されている場合</p>
 * </div>
 * 
 * 
 * <h3 class="description">見出し行が結合されている場合</h3>
 * <p>見出し行が結合され、1つの見出しに対して複数の列が存在する場合は属性{@link #headerMerged()}を使用します。
 *    <br>属性{@link #headerMerged()}の値には列見出しから何セル分離れているかを指定します。
 *    <br>属性{@link #columnName()}で指定する見出しのセル名は、結合されているセルと同じ値を指定します。
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
 *     {@literal @XlsColumn(columnName="連絡先")}
 *     private String mailAddress;
 *     
 *     / 結合されている見出しから離れている数を指定する
 *     {@literal @XlsColumn(columnName="連絡先", headerMerged=1)}
 *     private String tel;
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/Column_headerMerged.png">
 *    <p>見出し行が結合されている場合</p>
 * </div>
 * 
 * 
 * @author Naoki Takezoe
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsColumn {
    
    /**
     * 見出しとなるカラム名を設定します。
     * @return
     */
    String columnName();
    
    /**
     * 同じ値がグループごとに結合されている場合は、{@literal merged=true} に設定します。
     * <p>trueにした場合、前の列の値が引き継がれて設定されます。
     * @return
     */
    boolean merged() default false;
    
    /**
     * 見出し行が結合され、1つの見出しに対して複数の列が存在する場合に指定します。
     * <p>{@link #headerMerged()}の値には、列見出しから何セル分離れているかを指定します。 
     * @return 値は0から始まり、指定しない場合は0を指定します。
     */
    int headerMerged() default 0;
    
    /**
     * 該当するカラム（セル）が見つからない場合、trueとすると、無視して処理を続行します。
     * @return
     */
    boolean optional() default false;
}
