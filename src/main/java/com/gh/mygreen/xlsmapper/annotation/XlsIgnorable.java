package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;


/**
 * 空のレコードなどの無駄なレコードをマッピングしないよう、無視する条件を判定するメソッドに付与します。
 *
 * <h3 class="description">基本的な使い方</h3>
 *
 * <p>シートの読み込み時に、{@link XlsHorizontalRecords}、{@link XlsVerticalRecords} の処理対象のレコードが、
 *    空の場合に読み飛ばしたい時に利用します。
 * </p>
 * <p>このアノテーションを付与するメソッドは、引数なしで、戻り値がbooleanである必要があります。</p>
 * <p>空のレコードかどうかを判定する際には、{@link com.gh.mygreen.xlsmapper.util.IsEmptyBuilder}を利用して簡単に判定することができます。</p>
 *
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧"), terminal=RecordTerminal.Border)}
 *     private {@literal List<UserRecord>} records;
 *
 * }
 *
 * // レコード用クラス
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *
 *     {@literal @XlsColumn(columnName="住所")}
 *     private String address;
 *
 *     // レコードを無視する判定をするためのメソッド
 *     // 列「ID」は空の判定には含まない。
 *     {@literal @XlsIsIgnored}
 *     public boolean isIgnored() {
 *         if(name != null || !name.isEmpty()) {
 *             return false;
 *         }
 *
 *         if(address != null || !address.isEmpty()) {
 *             return false;
 *         }
 *     }
 * }
 * </code></pre>
 *
 * <h3 class="description">{@link com.gh.mygreen.xlsmapper.util.IsEmptyBuilder}を使った記述の簡単化</h3>
 *
 * アノテーション{@link XlsIgnorable}を付与したメソッドの実装において、{@link com.gh.mygreen.xlsmapper.util.IsEmptyBuilder}を使用すると、
 * より簡潔に記述することができます。
 *
 *  * <pre class="highlight"><code class="java">
 * // レコード用クラス
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *
 *     {@literal @XlsColumn(columnName="住所")}
 *     private String address;
 *
 *     // レコードが空と判定するためのメソッド
 *     {@literal @XlsIgnorable}
 *     public boolean isEmpty() {
 *         // 列「ID」は空の判定には含まないよう除外する。
 *         return IsEmptyBuilder.reflectionIsEmpty(this, "id");
 *     }
 * }
 * </code></pre>
 *
 *
 * @since 0.2
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsIgnorable {

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};
}
