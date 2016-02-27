package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * シートの読み込み前に、このアノテーションを付与した任意のメソッドが実行されます。
 * <p>戻り値なしのpublicメソッドに付与する必要があります。</p>
 * <p>引数は、次の任意の値が指定可能です。定義順は関係ありません。
 *    <br>引数を取らないことも可能です。
 * </p>
 * <ul>
 *   <li>処理対象のシートオブジェクト {@link org.apache.poi.ss.usermodel.Sheet}</li>
 *   <li>XlsMapperの設定オブジェクト {@link com.gh.mygreen.xlsmapper.XlsMapperConfig}</li>
 *   <li>シートのエラー情報を格納するオブジェクト {@link com.gh.mygreen.xlsmapper.validation.SheetBindingErrors}</li>
 * </ul>
 * 
 * <p>シート用クラス、レコード用クラスのどちらにも定義できます。
 *   <br>実行順は、親であるシートクラスの処理が先に処理されます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // シートクラス
 * {@literal @XlsSheet(name="Users")}
 * public class SampleSheet {
 * 
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧")}
 *     private {@literal List<UserRecord>} records;
 *     
 *     {@literal @XlsPreLoad}
 *     public void onPreLoad() {
 *         // 読み込み前に実行される処理
 *     }
 * }
 * 
 * // レコードクラス
 * public class UserRecord {
 *     
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *     
 *     {@literal @XlsPreLoad}
 *     public void onPreLoad(Sheet sheet, XlsMapperConfig config, SheetBindingErrors errors) {
 *         // 読み込み前に実行される処理
 *     }
 *     
 * }
 * </code></pre>
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsPreLoad {
}
