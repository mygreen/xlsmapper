package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}の書き込み時のレコードの操作を指定するためのアノテーションです。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsRecordOption {

    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
     * <p>値は、列挙型{@link OverOperation}で指定し、行の挿入や、上部のセルをコピーするなど指定ができます。
     *    <br>デフォルトでは何もしません。
     * </p>
     * <p>ただし、{@link XlsVerticalRecords}の場合、{@link OverOperation#Insert}は対応していません。</p>
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧")}
     *     {@literal @XlsRecordOption(overOpration=OverOperation.Insert)}
     *     private {@literal List<UserRecord>} records;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_overRecord.png" alt="">
     *    <p>表の書き込み時の不足するレコードの操作の指定</p>
     * </div>
     *
     * @return {@link OverOperation#Break}の場合、足りないレコードがあるとそこで処理を終了します。
     */
    OverOperation overOperation() default OverOperation.Break;

    /**
     * 書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
     * <p>値は、列挙型{@link RemainedOperation}で指定し、行の値のクリアや、行の削除を指定することができます。
     *   <br>デフォルトでは何もしません。
     * </p>
     * <p>ただし、{@link XlsVerticalRecords}の場合、{@link RemainedOperation#Delete}は対応していません。</p>
     *
     * <pre class="highlight"><code class="java">
     * // シート用クラス
     * {@literal @XlsSheet(name="Users")}
     * public class SampleSheet {
     *
     *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧")}
     *     {@literal @XlsRecordOption(remainedOperation=RemainedOperation.Clear)}
     *     private {@literal List<UserRecord>} records;
     *
     * }
     * </code></pre>
     *
     * <div class="picture">
     *    <img src="doc-files/HorizontalRecord_remainedRecord.png" alt="">
     *    <p>表の書き込み時の余分なレコードの操作の指定</p>
     * </div>
     *
     * @return {@link RemainedOperation#None}の場合、余っているレコードがあっても何もしません。
     */
    RemainedOperation remainedOperation() default RemainedOperation.None;

    /**
     * アノテーション {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で、
     * 書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static enum OverOperation {

        /** 前のセルをコピーします */
        Copy,

        /** 次のセルの前に行または列を挿入します */
        Insert,

        /** レコードの書き込みを中断します */
        Break,
        ;

    }

    /**
     * アノテーション {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で、
     * 書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static enum RemainedOperation {

        /** セルの値をクリアします */
        Clear,

        /** 行または列を削除します */
        Delete,

        /** 何もしません */
        None,
        ;

    }
}
