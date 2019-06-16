package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * 連続し隣接するセルをCollection(List, Set)または配列にマッピングします。
 *
 * <h3 class="description">基本的な使い方</h3>
 *
 * <p>セルの開始位置をインデックス形式の{@link #column()}と{@link #row()}か、アドレス形式の{@link #address()}のどちらか一方の形式を指定します。
 *    両方を指定した場合、{@link #address()}の設定値が優先されます。
 * </p>
 * <p>属性{@link #direction()}で、連続する隣接するセルの方向を指定します。</p>
 * <p>属性{@link #size()}で、マッピングするセルの個数を指定します。</p>
 * <p>配列または、{@link java.util.Collection}({@link java.util.List}/{@link java.util.Set})にマッピングします。</p>
 * <p>{@link java.util.Collection}型のインタフェースを指定している場合、読み込み時のインスタンスは次のクラスが指定されます。</p>
 * <ul>
 *   <li>{@link java.util.List}の場合、{@link java.util.ArrayList}がインスタンスのクラスとなります。
 *   <li>{@link java.util.Set} の場合、{@link java.util.LinkedHashSet}がインスタンスのクラスとなります。
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // インデックス形式、横方向で指定する場合
 *     // 属性directionを省略した場合は、ArrayDirection.Horizonを指定したと同じ意味。
 *     {@literal @XlsArrayCells(column=0, row=0, size=6)}
 *     private {@literal List<String>} nameKanas1;
 *
 *     // アドレス形式、配列にマッピング
 *     {@literal @XlsArrayCells(address="A1", size=6, direction=ArrayDirection.Vertical)}
 *     private String[] nameKanas2;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/ArrayCells.png" alt="">
 *    <p>基本的な使い方</p>
 * </div>
 *
 *
 * <h3 class="description">書き込み時に配列・リストのサイズが不足、または余分である場合</h3>
 * アノテーション {@link XlsArrayOption} を指定することで、書き込み時のセルの制御を指定することができます。
 *
 * <p>属性 {@link XlsArrayOption#overOpration()} で、書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性 {@link #size()} の値が小さく、足りない場合の操作を指定します。
 * <p>属性 {@link XlsArrayOption#remainedOperation()} で、書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性 {@link #size()} の値が大きく、余っている場合の操作を指定します。
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsArrayCells(address="B3", size=6)}
 *     {@literal @XlsArrayOption(overOperation=OverOperation.Error, remainedOperation=RemainedOperation.Clear)}
 *     private {@literal List<String>} nameKana;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/ArrayCells_ArrayOption.png" alt="">
 *    <p>書き込み時の制御を行う場合</p>
 * </div>
 *
 * <h3 class="description">位置情報／見出し情報を取得する際の注意事項</h3>
 * <p>マッピング対象のセルのアドレスを取得する際に、フィールド{@literal Map<String, CellPosition> positions}を定義しておけば、
 *    自動的にアドレスがマッピングされます。
 *    <br>通常は、キーにはプロパティ名が記述（フィールドの場合はフィールド名）が入ります。
 *    <br>アノテーション{@link XlsArrayCells}でマッピングしたセルのキーは、{@literal <プロパティ名>[<インデックス>]}の形式になります。
 * </p>
 * <p>同様に、マッピング対象の見出しを取得する、フィールド{@literal Map<String, String> labels}へのアクセスも、
 *    キーは{@literal <プロパティ名>[<インデックス>]}の形式になります。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 *
 *     // 位置情報
 *     private {@literal Map<String, CellPosition>} positions;
 *
 *     // 見出し情報
 *     private {@literal Map<String, String>} labels;
 *
 *     {@literal @XlsArrayCells(address="B3", size=6)}
 *     private {@literal List<String>} nameKana;
 *
 * }
 *
 *
 * // 位置情報・見出し情報へのアクセス
 * SampleRecord record = ...;
 *
 * CellPosition position = record.positions.get("nameKana[2]");
 *
 * String label = recrod.labeles.get("nameKana[2]");
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/ArrayCells_positions.png" alt="">
 *    <p>位置情報・見出し情報の取得</p>
 * </div>
 *
 *
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@XlsFieldProcessor(value={})
public @interface XlsArrayCells {

    /**
     * 連続するセルの個数を指定します。
     *
     * @return 1以上の値を指定します。
     */
    int size();

    /**
     * 値のセルの結合を考慮するかどうか指定します。
     * この値により、属性{@link #size()}の指定方法が変わります。
     * <p>セル結合されている場合は、結合後の個数を指定します。</p>
     * <ul>
     *   <li>trueの場合は、結合されているセルを1つのセルとしてマッピングします。</li>
     *   <li>falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
     *     <br>ただし、書き込む際には、結合が解除されます。
     *   </li>
     * </ul>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // elementMerged=trueは初期値なので、省略可
     *     {@literal @XlsArrayCells(address="B3", size=3)}
     *     private {@literal List<String>} words;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/ArrayCells_elementMerged.png" alt="">
     *    <p>結合したセルをマッピングする場合</p>
     * </div>
     *
     *
     * @return trueの場合、値のセルが結合されていることを考慮します。
     */
    boolean elementMerged() default true;

    /**
     * 連続し隣接するセルの方向を指定します。
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // 縦方向の隣接するセル
     *     // 属性direction=ArrayDirection.Verticalを指定すると、縦方向にマッピングします。
     *     {@literal @XlsLabelledArrayCells((address="B3", direction=ArrayDirection.Vertical, size=4)}
     *     private {@literal List<String>} names;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/ArrayCells_direction.png" alt="">
     *    <p>属性directionの概要</p>
     * </div>
     *
     *
     * @return セルの方向を指定します。
     */
    ArrayDirection direction() default ArrayDirection.Horizon;

    /**
     * 配列またはリスト要素の値のクラスを指定します。
     * <p>省略した場合、定義されたGenericsタイプから取得します。</p>
     * @return 要素のクラスタイプを指定します。
     */
    Class<?> elementClass() default Object.class;

    /**
     * セルの行番号を指定します。
     * {@link #column()}属性とセットで指定します。
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int row() default -1;

    /**
     * セルの列番号を指定します。
     * {@link #row()}属性とセットで指定します。
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     *
     */
    int column() default -1;

    /**
     * セルのアドレスを指定します。
     * <p>{@link #row()}、{@link #column()}属性のどちらか一方を指定します。</p>
     *
     * @return 'A1'の形式で指定します。空文字は無視されます。
     */
    String address() default "";

    /**
     * 適用するケースを指定します。
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};

}
