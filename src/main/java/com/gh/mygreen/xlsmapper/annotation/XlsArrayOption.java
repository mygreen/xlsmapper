package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;

/**
 * {@link XlsArrayCells}、{@link XlsLabelledArrayCells}、{@link XlsArrayColumns}の書き込み時の配列・リストの操作を指定するためのアノテーションです。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsArrayOption {

    /**
     * 書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性size()の値が小さく、足りない場合の操作を指定します。
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * // 書き込むデータ
     * String[] data = String[]{"や", "ま", "だ", "　", "た", "ろ", "う"};
     *
     * // マッピングの定義
     * public class SampleSheet {
     *
     *     // ラベルの右側 + 横方向の隣接するセル
     *     {@literal @XlsLabelledArrayCells(columnName="ふりがな", type=LabelledCellType.Right, size=6)}
     *     {@literal @XlsArrayOption(overOperation=OverOperation.Error)}
     *     private {@literal List<String>} nameRuby;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/ArrayOption_overOperation.png" alt="">
     *    <p>属性overOperationの概要</p>
     * </div>
     *
     *
     * @return {@link OverOperation#Break}の場合、足りないセルがあるとそこで処理を終了します。
     */
    OverOperation overOpration() default OverOperation.Break;

    /**
     * 書き込み時にJavaオブジェクトの配列・リストのサイズに対して、属性size()の値が大きく、余っている場合の操作を指定します。
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="Users")}
     * // 書き込むデータ
     * String[] data = String[]{"あ", "べ", "　", "あ", "い"};
     *
     * // マッピングの定義
     * public class SampleSheet {
     *
     *     // ラベルの右側 + 横方向の隣接するセル
     *     {@literal @XlsLabelledArrayCells(columnName="ふりがな", type=LabelledCellType.Right, size=6)}
     *     {@literal @XlsArrayOption(remainedOperation=RemainedOperation.Clear)}
     *     private {@literal List<String>} nameRuby;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/ArrayOption_remainedOperation.png" alt="">
     *    <p>属性remainedOperationの概要</p>
     * </div>
     *
     *
     * @return {@link RemainedOperation#None}の場合、余っているセルがあっても何もしません。
     */
    RemainedOperation remainedOperation() default RemainedOperation.None;

    /**
     * アノテーション {@link XlsArrayCells}や{@link XlsLabelledArrayCells}で、
     * 書き込み時に、配列やリストのデータサイズ数に対して、属性size()の値が小さい場合の操作を指定します。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public enum OverOperation {

        /**
         * 隣接するセルへの書き込みを中断します。
         */
        Break,

        /**
         * 書き込み処理の前に、例外 {@link AnnotationInvalidException}をスローします。
         */
        Error
        ;

    }

    /**
     * アノテーション {@link XlsArrayCells}や{@link XlsLabelledArrayCells}で、
     * 書き込み時に、配列やリストのデータサイズ数に対して、属性size()の値が大きい場合の操作を指定します。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public enum RemainedOperation {

        /**
         * 隣接するセルへの書き込み、その後、何もしません。
         */
        None,

        /**
         * 隣接するセルへの書き込み、その後、余っているセルの値をクリアします。
         */
        Clear
        ;

    }

}
