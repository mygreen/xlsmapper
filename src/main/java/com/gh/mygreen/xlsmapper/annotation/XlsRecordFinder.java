package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.RecordFinder;

/**
 * {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}の読み込み時において、
 * データレコードの開始位置が既存のアノテーションの属性だと表現できない場合に
 * 任意の実装方法を指定するようにします。
 *
 * <p>属性{@link #value()} で、レコードの開始位置を検索する {@link RecordFinder} の実装クラスを指定します。</p>
 * <p>属性{@link #args()} で、レコードの開始位置を検索する実装クラスに渡す引数を指定します。</p>
 *
 * <pre class="highlight"><code class="java">
 * {@literal @XlsSheet(name="Users")}
 * // マッピングの定義
 * public class SampleSheet {
 *
 *     // クラスAに対するマッピング定義
 *     {@literal @XlsOrder(1)}
 *     // マッピングの終了条件が、「クラスB」であるため、terminalLabelを指定します。汎用的に正規表現で指定します。
 *     {@literal @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border, terminateLabel="/クラス.+/")}
 *     // クラスAの見出しを探すために、属性argsでクラス名を指定します。
 *     {@literal @XlsRecordFinder(value=ClassNameRecordFinder.class, args="クラスA")}
 *     private {@literal List<UserRecord>} classA;
 *
 *     // クラスBに対するマッピング定義
 *     {@literal @XlsOrder(2)}
 *     // マッピングの終了条件が、終端のセルに罫線があるのため、terminalを指定します。
 *     {@literal @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border, terminateLabel="/クラス.+/")}
 *     // クラスAの見出しを探すために、属性argsでクラス名を指定します。
 *     {@literal @XlsRecordFinder(value=ClassNameRecordFinder.class, args="クラスB")}
 *     private {@literal List<UserRecord>} classB;
 * }
 *
 * // クラス用の見出しのレコードを探すクラス
 * public class ClassNameRecordFinder implements RecordFinder {
 *
 *     {@literal @Override}
 *     public CellPosition find(ProcessCase processCase, String[] args, Sheet sheet,
 *             CellPosition initAddress, Object beanObj, Configuration config) {
 *
 *         // アノテーション {@literal XlsRecordFinder}の属性argsで指定された値を元にセルを検索します。
 *         final String className = args[0];
 *         Cell classNameCell = CellFinder.query(sheet, className, config)
 *                 .startPosition(initAddress)
 *                 .findWhenNotFoundException();
 *
 *         // 見出し用のセルから1つ下がデータレコードの開始位置
 *         return CellPosition.of(classNameCell.getRowIndex()+1, initAddress.getColumn());
 *     }
 *
 * }
 *
 * // ユーザレコードの定義
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="No.", optional=true)}
 *     private int no;
 *
 *     {@literal @XlsColumn(columnName="氏名")}
 *     private String name;
 *
 *     {@literal @XlsColumn(columnName="算数")}
 *     private Integer sansu;
 *
 *     {@literal @XlsColumn(columnName="国語")}
 *     private Integer kokugo;
 *
 *     {@literal @XlsColumn(columnName="合計")}
 *     {@literal @XlsFormula(value="SUM(D{rowNumber}:E{rowNumber})", primary=true)}
 *     private Integer sum;
 *
 *     {@literal @XlsIgnorable}
 *     public boolean isEmpty() {
 *         return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
 *     }
 *
 *     // getter、setterは省略
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/RecordFinder.png" alt="">
 *    <p>任意の位置のレコードをマッピングする場合</p>
 * </div>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsRecordFinder {

    /**
     * {@link RecordFinder}の実装クラスを指定します。
     * @return {@link RecordFinder}の実装クラス。
     */
    Class<? extends RecordFinder> value();

    /**
     * {@link RecordFinder}に渡す引数を指定します。
     * @return {@link RecordFinder}に渡す引数。
     */
    String[] args() default {};

}
