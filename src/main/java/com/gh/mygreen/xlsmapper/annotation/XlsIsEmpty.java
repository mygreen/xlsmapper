package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * レコードの値が空からどうか判定するメソッドに付与します。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <p>シートの読み込み時に、{@link XlsHorizontalRecords}、{@link XlsVerticalRecords} の処理対象のレコードが、
 *    空の場合に読み飛ばしたい時に利用します。
 * </p>
 * <p>このアノテーションを付与するメソッドは、引数なしで、戻り値がbooleanである必要があります。</p>
 * <p>{@link com.gh.mygreen.xlsmapper.IsEmptyBuilder}を利用して簡単に判定することもできます。</p>
 * 
 * 
 * <pre class="highlight"><code class="java">
 * // シート用クラス
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 *     
 *     // レコードの読み飛ばしを有効にします。
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧"), terminal=RecordTerminal.Border, skipEmptyRecord=true)}
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
 *     // レコードが空と判定するためのメソッド
 *     // 列「ID」は空の判定には含まない。
 *     {@literal @XlsIsEmpty}
 *     public boolean isEmpty() {
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
 * <h3 class="description">{@link com.gh.mygreen.xlsmapper.IsEmptyBuilder}を使った記述の簡単化</h3>
 * 
 * アノテーション{@link XlsIsEmpty}を付与したメソッドの実装において、{@link com.gh.mygreen.xlsmapper.IsEmptyBuilder}を使用すると、
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
 *     {@literal @XlsIsEmpty}
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
public @interface XlsIsEmpty {
}
