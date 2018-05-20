package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * アノテーション{@link XlsHorizontalRecords}や{@link XlsVerticalRecords}のレコード用のクラスにおいて、
 * ツリー構造のように入れ子になっている表をマッピングする際に使用します。
 *
 * <h3 class="description">一対多の関係</h3>
 * <p>一対多の関係を表現する際には、Collection(List/Set)または、配列で指定します。</p>
 *
 * <ul>
 *   <li>親子関係は、結合しているかで表現します。</li>
 *   <li>親に指定しているJavaBeanクラスは、子や孫には指定することができません。</li>
 *   <li>属性{@link XlsHorizontalRecords#terminateLabel()}などの
 *     終端や空のレコードの判定は、入れ子になったレコードごとに判定されます。</li>
 *   <li>読み込みの際、アノテーション{@link XlsIgnorable}で、空のレコードを読み飛ばした結果、
 *     レコード数が0件となった場合は、要素数0個リストや配列が設定されます。</li>
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="機能")}
 * public class SampleSheet {
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="機能覧")}
 *     private {@literal List<CategoryRecord>} categories;
 * }
 *
 * // レコード用クラス（分類）
 * public class CategoryRecord {
 *
 *     {@literal @XlsColumn(columnName="分類")}
 *     private String name;
 *
 *     {@literal @XlsColumn(columnName="説明（分類）")}
 *     private String description;
 *
 *     // ネストしたレコードのマッピング
 *     {@literal @XlsNestedRecords}
 *     private {@literal List<FunctionRecord>} functions;
 *
 * }
 *
 * // レコード用クラス（機能）
 * public class FunctionRecord {
 *
 *     {@literal @XlsColumn(columnName="機能名")}
 *     private String name;
 *
 *     {@literal @XlsColumn(columnName="説明（機能）")}
 *     private String description;
 *
 *     // ネストしたレコードのマッピング
 *     {@literal @XlsNestedRecords}
 *     private {@literal List<DetailRecord>} details;
 *
 * }
 *
 * // レコード用クラス（詳細）
 * public class DetailRecord {
 *
 *     {@literal @XlsColumn(columnName="項目")}
 *     private String name;
 *
 *     {@literal @XlsColumn(columnName="値")}
 *     private String value;
 *
 * }
 * </code></pre>
 *
 * <div class="picture">
 *    <img src="doc-files/NestedRecords_oneToMany.png" alt="">
 *    <p>一対多の関係</p>
 * </div>
 *
 *
 * <h3 class="description">一対一の関係</h3>
 * <p>一対一の関係をマッピングする際には、ネストしたクラスを直接指定します。</p>
 * <p>クラス定義などの制約は、基本的に一対多のときと同じです。</p>
 *
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="学期末テスト")}
 * public class SampleSheet {
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="テスト結果", bottom=2)}
 *     private {@literal List<UserRecord>} users;
 * }
 *
 * // レコード用クラス（生徒情報）
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="No.")}
 *     private int no;
 *
 *     {@literal @XlsColumn(columnName="クラス", merged=true)}
 *     private String className;
 *
 *     {@literal @XlsColumn(columnName="氏名")}
 *     private String name;
 *
 *     // ネストしたレコードのマッピング
 *     {@literal @XlsNestedRecords}
 *     private ResultRecord result;
 *
 * }
 *
 * // レコード用クラス（テスト結果）
 * public class ResultRecord {
 *
 *     {@literal @XlsColumn(columnName="国語")}
 *     private int kokugo;
 *
 *     {@literal @XlsColumn(columnName="算数")}
 *     private int sansu;
 *
 *     {@literal @XlsColumn(columnName="合計")}
 *     private int sum;
 *
 * }
 * </code></pre>
 *
 *
 * <div class="picture">
 *    <img src="doc-files/NestedRecords_oneToOne.png" alt="">
 *    <p>一対一の関係</p>
 * </div>
 *
 * @version 2.0
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsNestedRecords {

    /**
     * レコードのマッピング先のクラスを指定します。
     * <p>指定しない場合は、Genericsの定義タイプが自動的に採用されます。</p>
     */
    Class<?> recordClass() default Object.class;

    /**
     * 適用するケースを指定します。
     * @since 2.0
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    ProcessCase[] cases() default {};

}
