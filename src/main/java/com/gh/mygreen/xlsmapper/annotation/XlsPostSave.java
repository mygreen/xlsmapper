package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;


/**
 * シートの書き込み後に、このアノテーションを付与した任意のメソッドが実行されます。
 * <p>実装方法として、JavaBeanに直接処理を実装する方法と、リスナークラスを指定して別のクラスで実装する方法の2種類があります。</p>
 * <p>戻り値なしのpublicメソッドに付与する必要があります。</p>
 * <p>引数は、次の任意の値が指定可能です。定義順は関係ありません。
 *    <br>引数を取らないことも可能です。
 * </p>
 * <ul>
 *   <li>処理対象のシートオブジェクト {@link org.apache.poi.ss.usermodel.Sheet}</li>
 *   <li>XlsMapperの設定オブジェクト {@link com.gh.mygreen.xlsmapper.Configuration}</li>
 *   <li>シートのエラー情報を格納するオブジェクト {@link com.gh.mygreen.xlsmapper.validation.SheetBindingErrors}</li>
 *   <li>処理対象のBeanオブジェクト</li>
 * </ul>
 *
 * <h3 class="description">JavaBeanクラスに実装する場合</h3>
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
 *     {@literal @XlsPostSave}
 *     public void onPostSave(Sheet sheet) {
 *         // 書き込み後に実行される処理
 *         // 本ライブラリで提供されていない、数式やセルの補正などを実装します。
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
 *     {@literal @XlsPostSave}
 *     public void onPostSave(Sheet sheet, Configuration config, SheetBindingErrors errors) {
 *         // 書き込み後に実行される処理
 *     }
 *
 * }
 * </code></pre>
 *
 * <h3 class="description">リスナークラスに実装する場合</h3>
 * <p>クラスにアノテーション{@link XlsListener#value()} で処理が実装されたクラスを指定します。 </p>
 * <p>インスタンスは、システム設定{@link Configuration#getBeanFactory()}経由で作成されるため、
 *   SpringFrameworkのコンテナからインスタンスを取得することもできます。
 * </p>
 *
 *
 * <pre class="highlight"><code class="java">
 * // シートクラス
 * {@literal @XlsSheet(name="Users")}
 * {@literal @XlsListener(SampleSheetListener.class)}
 * public class SampleSheet {
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="ユーザ一覧")}
 *     private {@literal List<UserRecord>} records;
 * }
 *
 * // SampleSheetクラスのリスナー
 * public static class SampleSheetListener {
 *
 *     {@literal @XlsPostSave}
 *     public void onPostSave(SampleSheet targetObj, Sheet sheet) {
 *         // 書き込み後に実行される処理
 *         // 本ライブラリで提供されていない、数式やセルの補正などを実装します。
 *     }
 * }
 *
 * // レコードクラス
 * {@literal @XlsListener(UserRecordListener.class)}
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     private int id;
 *
 *     {@literal @XlsColumn(columnName="名前")}
 *     private String name;
 *
 * }
 *
 * // UserRecordクラスのリスナー
 * public static class UserRecordListener {
 *
 *     {@literal @XlsPostSave}
 *     public void onPostSave(UserRecord targetObj, Sheet sheet, Configuration config, SheetBindingErrors errors) {
 *         // 書き込み後に実行される処理
 *     }
 * }
 * </code></pre>
 *
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsPostSave {
}
