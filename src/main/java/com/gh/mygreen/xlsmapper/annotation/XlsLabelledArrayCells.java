package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * セルの見出し用のラベルセルを指定し、左右もしくは下側に連続し隣接するセルをCollection(List, Set)または配列にマッピングします。
 * <p>アノテーション {@link XlsArrayCells} と {@link XlsLabelledCell} を融合したアノテーションとなります。</p>
 *
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #label()} で、見出しとなるセルの値を指定します。</p>
 * <p>属性{@link #type()}で見出しとなるセルから見て、設定されている位置を指定します。</p>
 * <p>属性{@link #direction()}で、連続する隣接するセルの方向を指定します。</p>
 * <p>属性{@link #size()}で、マッピングするセルの個数を指定します。</p>
 * <p>セルが見つからない場合はエラーとなりますが、属性{@link #optional()}を'true'とすることで無視して処理を続行します。</p>
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
 *     // ラベルの右側 + 横方向の隣接するセル
 *     // 属性directionを省略した場合は、ArrayDirection.Horizonを指定したと同じ意味。
 *     {@literal @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, size=6)}
 *     private {@literal List<String>} nameKanas1;
 *
 *     // ラベルの下側 + 横方向の隣接するセル
 *     // 属性optional=trueと設定すると、ラベルセルが見つからなくても処理を続行する
 *     {@literal @XlsLabelledArrayCells(label="ラベル2", type=LabelledCellType.Bottom,}
 *              direction=ArrayDirection.Horizon, size=6, optional=true)
 *     private String[] nameKanas2;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/LabelledArrayCells.png" alt="">
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
 *     {@literal @XlsLabelledArrayCells(label="ふりがな", type=LabelledCellType.Right, size=6)}
 *     {@literal @XlsArrayOption(overOperation=OverOperation.Error, remainedOperation=RemainedOperation.Clear)}
 *     private {@literal List<String>} nameKana;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/LabelledArrayCells_ArrayOption.png" alt="">
 *    <p>書き込み時の制御を行う場合</p>
 * </div>
 *
 * <h3 class="description">位置情報／見出し情報を取得する際の注意事項</h3>
 * <p>マッピング対象のセルのアドレスを取得する際に、フィールド{@literal Map<String, CellPosition> positions}を定義しておけば、
 *    自動的にアドレスがマッピングされます。
 *    <br>通常は、キーにはプロパティ名が記述（フィールドの場合はフィールド名）が入ります。
 *    <br>アノテーション{@link XlsLabelledArrayCells}でマッピングしたセルのキーは、{@literal <プロパティ名>[<インデックス>]}の形式になります。
 * </p>
 * <p>同様に、マッピング対象の見出しを取得する、フィールド{@literal Map<String, String> labels}へのアクセスも、
 *    キーは{@literal <プロパティ名>[<インデックス>]}の形式になります。
 *    <br>ただし、見出し情報の場合は、全ての要素が同じ値になるため、従来通りの {@literal <プロパティ名>} でも取得できます。
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
 *     {@literal @XlsLabelledArrayCells(label="ふりがな", type=LabelledCellType.Right, size=6)}
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
 *
 * // 見出し情報の場合、従来通りのインデックスなしでも取得できる
 * String label = recrod.labeles.get("nameKana");
 * </code></pre>
 *
 *
 * <div class="picture">
 *    <img src="doc-files/LabelledArrayCells_positions.png" alt="">
 *    <p>位置情報・見出し情報の取得</p>
 * </div>
 *
 * <h3 class="description">ラベルセルを正規表現、正規化して指定する場合</h3>
 *
 * <p>シートの構造は同じだが、ラベルのセルが微妙に異なる場合、ラベルセルを正規表現による指定が可能です。
 *   <br>また、空白や改行を除去してラベルセルを比較するように設定することも可能です。</p>
 *
 * <p>正規表現で指定する場合、アノテーションの属性の値を {@code /正規表現/} のように、スラッシュで囲みます。</p>
 * <ul>
 *   <li>スラッシュで囲まない場合、通常の文字列として処理されます。</li>
 *   <li>正規表現の指定機能を有効にするには、システム設定のプロパティ {@link Configuration#setRegexLabelText(boolean)} の値を trueに設定します。</li>
 * </ul>
 *
 * <p>ラベセルの値に改行が空白が入っている場合、それらを除去し正規化してアノテーションの属性値と比較することが可能です。</p>
 * <ul>
 *   <li>正規化とは、空白、改行、タブを除去することを指します。</li>
 *   <li>ラベルを正規化する機能を有効にするには、、システム設定のプロパティ {@link Configuration#setNormalizeLabelText(boolean)} の値を trueに設定します。</li>
 * </ul>
 *
 * <p>これらの指定が可能な属性は、{@link #label()}、{@link #headerLabel()}です。</p>
 *
 * <pre class="highlight"><code class="java">
 * // システム設定
 * XlsMapper xlsMapper = new XlsMapper();
 * xlsMapper.getConfiguration()
 *         .setRegexLabelText(true)        // ラベルを正規表現で指定可能にする機能を有効にする。
 *         .setNormalizeLabelText(true);   // ラベルを正規化して比較する機能を有効にする。
 *
 * // シート用クラス
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // 正規表現による指定
 *     {@literal @XlsLabelledArrayCells(label="/名前.+/", type=LabelledCellType.Right, size=10)}
 *     private {@literal List<String>} names;
 *
 *     {@literal @XlsLabelledArrayCells(label="コメント（オプション）", type=LabelledCellType.Right, size=5)}
 *     private String[] comments;
 *
 * }
 * </code></pre>
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
public @interface XlsLabelledArrayCells {

    /**
     * 連続するセルの個数を指定します。
     * @return 1以上の値を指定します。
     */
    int size();

    /**
     * 値のセルが結合している場合、それを考慮するかどうか指定します。
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
     *     {@literal @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right,size=3, elementMerged=true)}
     *     private {@literal List<String>} words;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledArrayCells_elementMerged.png" alt="">
     *    <p>結合したセルをマッピングする場合</p>
     * </div>
     *
     * @return trueの場合、値のセルが結合されていることを考慮します。
     */
    boolean elementMerged() default true;

    /**
     * 連続し隣接するセルの方向を指定します。
     * <p>セルの位置{@link #type()}を{@link LabelledCellType#Left}のとき、
     *   セルの方向{@link #direction()}を{@link ArrayDirection#Horizon} は、設定できません。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // ラベルの右側 + 横方向の隣接するセル
     *     // 属性direction=ArrayDirection.Verticalを指定すると、縦方向にマッピングします。
     *     {@literal @XlsLabelledArrayCells(label="ラベル3", type=LabelledCellType.Right,}
     *             direction=ArrayDirection.Vertical, size=4)
     *     private {@literal List<String>} nameKanas3;
     *
     *     // ラベルの下側 + 横方向の隣接するセル
     *     {@literal @XlsLabelledArrayCells(label="ラベル4", type=LabelledCellType.Bottom,}
     *              direction=ArrayDirection.Vertical, size=4)
     *     private String[] nameKanas4;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledArrayCells_direction.png" alt="">
     *    <p>属性directionの概要</p>
     * </div>
     *
     *
     * @return セルの方向を指定します。
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
     * 見出しセルから見て値が設定されているセルの位置を指定します。
     */
    LabelledCellType type();

    /**
     * 見出しとなるセルの値を指定します。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     */
    String label() default "";

    /**
     * 見出しとなるセルの行番号を指定します。
     * <p>{@link #labelColumn()}属性とセットで指定します。</p>
     * <p>この属性は、{@link XlsIterateTables}の中で指定したときに、処理内部で使用されるため、通常は設定しません。</p>
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int labelRow() default -1;

    /**
     * 見出しとなるセルの列番号を指定します。
     * <p>{@link #labelRow()}属性とセットで指定します。</p>
     * <p>この属性は、{@link XlsIterateTables}の中で指定したときに、処理内部で使用されるため、通常は設定しません。</p>
     *
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int labelColumn() default -1;

    /**
     * 同じラベルのセルが複数ある場合は、区別するため見出しを属性{@link #headerLabel()} で指定します。
     * <p>属性{@link #headerLabel()}で指定したたセルから、属性{@link #label()}で指定したセルを下方向に検索し、
     *    最初に見つかったセルをラベルセルとして使用します。
     * </p>
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsLabelledCell(label="ふりがな", type=LabelledCellType.Right, size=10, headerLabel="氏名")}
     *     private {@literal List<String>} nameRuby;
     *
     *     {@literal @XlsLabelledCell(label="ふりがな", type=LabelledCellType.Right, size=10, headerLabel="住所")}
     *     private {@literal List<String>} addressRuby;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledArrayCells_headerLabel.png" alt="">
     *    <p>属性headerLabelの概要</p>
     * </div>
     *
     * @return 見出しとなるセルを指定します。指定しない場合は空文字を指定します。
     */
    String headerLabel() default "";

    /**
     * 属性{@link #type()}の位置に向かって指定したセル数分を検索し、最初に発見した空白以外のセルを開始位置としてマッピングします。
     * <p>属性{@link #range()}と{@link #skip()}を同時に指定した場合、まず、skip分セルを読み飛ばし、そこからrangeの範囲で空白以外のセルを検索します。</p>
     * <p>この属性 は、 <strong>読み込み時のみ有効</strong> です。書き込み時に指定しても無視されます。
     *   <br>ただし、データセルが偶然空白のときは、マッピング対象のセルがずれるため、この属性を使用する場合は注意が必要です。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, range=4, size=4)}
     *     private {@literal List<String>} words1;
     *
     *     {@literal @XlsLabelledArrayCells(label="ラベル2", type=LabelledCellType.Bottom, range=5, size=3)}
     *     private {@literal List<String>} words2;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledArrayCells_range.png" alt="">
     *    <p>属性rangeの概要</p>
     * </div>
     *
     * @return 値は1から始まります。指定しない場合は1を指定します。
     */
    int range() default 1;

    /**
     * ラベルセルから指定したセル数分離れたセルを開始位置としてマッピングする際に指定します。
     * <p>属性{@link #type()}の方向に向かってラベルセルから指定したセル数分離れたセルを開始としてマッピングすることができます。</p>
     * <p>属性{@link #range()}と{@link #skip()}を同時に指定した場合、まず、skip分セルを読み飛ばし、そこからrangeの範囲で空白以外のセルを検索します。</p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right, size=3, skip=2)}
     *     private {@literal List<String>} wrods2;
     *
     *     {@literal @XlsLabelledCell(label="ラベル2", type=LabelledCellType.Bottom, size=3, skip=3)}
     *     private {@literal List<String>} wrods2;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledArrayCells_skip.png" alt="">
     *    <p>属性skipの概要</p>
     * </div>
     *
     *
     * @return 値は0から始まります。0以下の値は無視されます。
     */
    int skip() default 0;

    /**
     * ラベルセルが結合している場合を考慮するかどうか指定します。
     * <ul>
     *  <li>値がtrueのとき、結合されているセルを1つのラベルセルとしてマッピングします。</li>
     *  <li>値がfalseの場合は、結合されていても解除した状態と同じマッピング結果となります。</li>
     * </ul>
     * <p>初期値はtrueであるため、結合されているかどうかは特に意識はする必要はありません。</p>
     * <p>{@link #labelMerged()}の値が falseのとき、ラベルセルが結合されていると、
     *    値が設定されているデータセルまでの距離が変わるため、属性{@link #skip()}を併用します。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     // labelMerged=trueは初期値なので、省略可
     *     {@literal @@XlsLabelledArrayCells(label="ラベル1", type=LabelledCellType.Right, size=6)}
     *     private {@literal List<String>} name1;
     *
     *     // labelMerged=falseで、ラベルが結合しているときは、skip属性を併用します。
     *     {@literal  @XlsLabelledCell(label="ラベル1", type=LabelledCellType.Right, size=6, labelMerged=false, skip=2)}
     *     private {@literal List<String>} name2;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/LabelledArrayCells_labelMerged.png" alt="">
     *    <p>属性labelMergedの概要</p>
     * </div>
     *
     * @return trueの場合、ラベルがセルが結合されていることを考慮します。
     */
    boolean labelMerged() default true;

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
