package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;

/**
 * ラベルセルを指定し、その左右もしくは下側の隣接するセルを配列またはリストにマッピングします。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsLabelledArrayCells {
    
    /**
     * 連続するセルの個数を指定します。
     * @return 1以上の値を指定します。
     */
    int size();
    
    /**
     * 値のセルが結合しているかどうか考慮するかどうか指定します。
     * この値により、属性{@link #size()}の指定方法が変わります。
     * <p>trueの場合は、結合されているセルを1つのセルとしてマッピングします。</p>
     * <p>falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
     *  <br>ただし、書き込む際には、結合を解除して書き込まれます。
     * </p>
     * 
     * @return trueの場合、値のセルが結合されていることを考慮する。
     */
    boolean elementMerged() default true;
    
    //TODO: direction.Horizonとtype.Leftの組みあわせはサポートしないことを明記する。
    /**
     * 連続する隣接するセルの方向を指定します。
     * @return セルの方向
     */
    ArrayDirection direction() default ArrayDirection.Horizon;
    
    /** 
     * 配列またはリスト要素の値のクラスを指定します。
     * <p>省略した場合、定義されたたGenericsの情報から取得します。
     */
    Class<?> elementClass() default Object.class;
    
    /**
     * セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。
     * <p>falseを指定し、セルが見つからない場合は、例外{@link CellNotFoundException}がスローされます。</p>
     */
    boolean optional() default false;
    
    /**
     * 属性{@link #type()}の方向に向かって指定したセル数分を検索し、最初に発見した空白以外のセルの値を取得します。
     * <p>ラベルセルから離れたセルを指定する場合に使用します。</p>
     * 
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
     *    <img src="doc-files/LabelledCell_range.png" alt="">
     *    <p>属性rangeの概要</p>
     * </div>
     * 
     * @return 値は1から始まり、指定しない場合は1を指定します。
     */
    int range() default 1;
    
    /**
     * 見出しセルから見て値が設定されているセルの位置・方向を指定します。
     */
    LabelledCellType type();
    
    /**
     * 見出しとなるセルの値を指定します。
     * <p>他の属性{@link #labelColumn()}、{@link #labelRow()}、{@link #labelAddress()}でも指定可能です。</p>
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     */
    String label() default "";
    
    /**
     * 見出しとなるセルの行番号を指定します。
     * <p>{@link #labelColumn()}属性とセットで指定します。</p>
     * @return 値は0から始まり、指定しない場合は-1を指定します。
     */
    int labelRow() default -1;
    
    /**
     * 見出しとなるセルの列番号を指定します。
     * <p>{@link #labelRow()}属性とセットで指定します。</p>
     * @return 値は0から始まり、指定しない場合は1を指定します。
     */
    int labelColumn() default -1;
    
    /**
     * 見出しとなるセルをアドレス形式で指定します。'A1'などのようにシートのアドレスで指定します。
     * @return 
     */
    String labelAddress() default "";
    
    /** 
     * 同じラベルのセルが複数ある場合に領域の見出しを指定します。 
     * <p>属性{@link #headerLabel()}で指定されたセルから、属性{@link #label()}で指定されたセルを下方向に検索し、
     *   最初に見つかったセルをラベルセルとして使用します。
     * </p>
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * 
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
     *    <img src="doc-files/LabelledCell_headerLabel.png" alt="">
     *    <p>属性headerLabelの概要</p>
     * </div>
     * 
     * @return 
     */
    String headerLabel() default "";
    
    /**
     * ラベルセルから指定したセル数分離れたセルの値をマッピングする際に指定します。
     * <p>属性{@link #type()}の方向に向かってラベルセルから指定したセル数分離れたセルの値をマッピングすることができます。
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
     *    <img src="doc-files/LabelledCell_skip.png" alt="">
     *    <p>属性skipの概要</p>
     * </div>
     * 
     * 
     * @return 値は0から始まり、指定しない場合は0を指定します。
     */
    int skip() default 0;
    
    /**
     * ラベルセルが結合している場合を考慮するかどうか指定します。
     * <p>属性{@link #label()}でラベルを指定しますが、そのセルが結合されている場合、
     *    データセルまでの距離を属性{@link #skip()}で指定する必要がありす。
     *    <br>しかし、{@link #labelMerged()}の値をtrueにすると、属性{@link #skip()}の値を指定する必要がなく、
     *    結合分を自動的に考慮して値をマッピングします。
     * </p>
     * 
     * @return trueの場合、ラベルが指定されているセルが結合されていることを考慮する。
     */
    boolean labelMerged() default false;
}
