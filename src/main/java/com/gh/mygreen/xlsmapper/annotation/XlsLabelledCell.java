package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ラベルセルを指定し、その左右もしくは下側のセルの値をマッピングします。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #type()}で見出しとなるセルから見て、設定されている位置を指定します。</p>
 * <p>セルが見つからない場合はエラーとなりますが、属性{@link #optional()}を'true'とすることで無視して処理を続行します。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *     
 *     {@literal @XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right)}
 *     private String title;
 *     
 *     // ラベルセルが見つからなくても処理を続行する
 *     {@literal @XlsLabelledCell(label="ラベル2", type=LabelledCellType.Bottom, optional=true)}
 *     private String summary;
 * 
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/LabelledCell.png">
 *    <p>基本的な使い方</p>
 * </div>
 * 
 * 
 * <h3 class="description">ラベルセルから離れたセルを指定する方法（属性{@link #range()}）</h3>
 * <p>属性{@link #range()}を指定すると、属性{@link #type()}の方向に向かって指定したセル数分を検索し、
 *    最初に発見した空白以外のセルの値を取得します。
 * </p>
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *     
 *     {@literal @XlsLabelledCell(label="ラベル", type=LabelledCellType.Right), range=3)}
 *     private String title;
 * 
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/LabelledCell_range.png">
 *    <p>属性rangeの概要</p>
 * </div>
 * 
 * 
 * <h3 class="description">ラベルセルから離れたセルを指定する方法（属性{@link #skip()}）</h3>
 * <p>属性{@link #skip()}を指定すると、属性{@link #type()}の方向に向かってラベルセルから指定したセル数分離れたセルの値をマッピングすることができます。
 *    <br>ラベルセルを結合してる場合、結合しているセル数-1分を指定することでマッピングできます。
 * </p>
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *     
 *     {@literal @XlsLabelledCell(label="ラベル", type=LabelledCellType.Right), skip=2)}
 *     private String title;
 * 
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/LabelledCell_skip.png">
 *    <p>属性skipの概要</p>
 * </div>
 * 
 * 
 * <h3 class="description">ラベルセルが重複するセルを指定する方法</h3>
 * <p>同じラベルのセルが複数ある場合は、領域の見出しを属性{@link #headerLabel()}で指定します。
 *    <br>属性{@link #headerLabel()}で指定されたセルから属性{@link #label()}で指定されたセルを下方向に検索し、最初に見つかったセルをラベルセルとして使用します。
 * </p>
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *     
 *     {@literal @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, headerLabel="アクション")}
 *     private String actionClassName;
 *     
 *     {@literal @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, headerLabel="アクションフォーム")}
 *     private String formClassName;
 * 
 * }
 * </code></pre>
 * 
 * <div class="picture">
 *    <img src="doc-files/LabelledCell_headerLabel.png">
 *    <p>属性headerLabelの概要</p>
 * </div>
 * 
 * 
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsLabelledCell {
    
    /**
     * セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。
     */
    boolean optional() default false;
    
    /**
     * type属性の方向に向かって指定したセル数分を検索し、最初に発見した空白以外のセルの値を取得します。
     */
    int range() default 1;
    
    /**
     * 見出しセルから見て値が設定されているセルの位置・方向を指定します。
     */
    LabelledCellType type();
    
    /**
     * 見出しとなるセルの値を指定します。
     * <p>他の属性{@link #labelColumn()}、{@link #labelRow()}、{@link #labelAddress()}でも指定可能です。
     */
    String label() default "";
    
    /**
     * 見出しとなるセルの行番号を指定します。0から始まります。
     * <p>{@link #labelColumn()}属性とセットで指定します。
     */
    int labelRow() default -1;
    
    /**
     * 見出しとなるセルの列番号を指定します。0から始まります。
     * <p>{@link #labelRow()}属性とセットで指定します。
     */
    int labelColumn() default -1;
    
    /**
     * 見出しとなるセルをアドレス形式で指定します。'A1'などのようにシートのアドレスで指定します。
     * <p>
     */
    String labelAddress() default "";
    
    /** 
     * 同じラベルのセルが複数ある場合に領域の見出しを指定します。 
     * headerLabel属性で指定されたセルからlabel属性で指定されたセルを下方向に検索し、最初に見つかったセルをラベルセルとして使用します。
     */
    String headerLabel() default "";
    
    /**
     * ラベルセルから指定したセル数分離れたセルの値をマッピングする際に指定します。
     */
    int skip() default 0;
}
