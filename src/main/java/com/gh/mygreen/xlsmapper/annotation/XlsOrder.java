package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * マッピング用のアノテーションの処理順序を定義します。
 *
 * <p>セルに対するマッピング情報を指定したアノテーションが付与された、
 *    フィールドまたはメソッドの処理順序は、Javaの実行環境に依存するため、
 *    処理順序を一定にしたいときに付与します。
 * </p>
 * <p>そのため、書き込み時に、アノテーション{@link XlsHorizontalRecords}などでマッピングし、
 *    行の追加、削除を伴う操作が行われるときには、アノテーションで指定したセルの位置がずれ正しく処理できない場合があります。
 *    <br>また、フィールド{@literal Map<String, CellPosition> positions}などで保持するセルの位置情報もずれます。
 * </p>
 *
 * <p>このような時は、アノテーション{@link XlsOrder}を付与し、処理順序は属性{@link #value()}で指定します。
 *  <br>属性 value はJavaの仕様により省略が可能です。
 * </p>
 * <p>{@link XlsOrder} を付与しないフィールドは、付与しているフィールドよりも後から処理が実行されます。
 *    <br>属性{@link #value()}が同じ値を設定されているときは、 フィールド名の昇順で優先度を決めて処理されます。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     // セルの位置情報
 *     private {@literal Map<String, CellPosition>} positions;
 *
 *     {@literal @XlsOrder(1)}
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧", terminal=RecordTerminal.Border)}
 *     {@literal @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)}
 *     private {@literal List<UserRecord>} records;
 *
 *     // 属性valueは省略が可能
 *     {@literal @XlsOrder(2)}
 *     {@literal @XlsLabelledCell(label="更新日", type=LabelledCellType.Right)}
 *     private Date updateTime;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/Hint.png" alt="">
 *    <p>書き込み時にセルの位置がずれる場合</p>
 * </div>
 *
 * <ul>
 *  <li>同じ値を設定した場合は、第2処理順としてフィールド名の昇順を使用します。</li>
 *  <li>このアノテーションの付与、または属性の値が-1以下の場合、付与しているいフィールドよりも処理順序は後になります。</li>
 * </ul>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsOrder {

    /**
     * オブジェクト内でのフィールドの処理順序を定義します。
     *
     * @return 値が大きいほど後から実行されます。
     */
    int value();
}
