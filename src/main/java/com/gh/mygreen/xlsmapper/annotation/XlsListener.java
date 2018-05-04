package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.Configuration;

/**
 * ライフサイクルコールバック用のリスナークラスを指定するためのアノテーション。
 * <p>アノテーション {@link XlsPreLoad}、{@link XlsPostLoad}、{@link XlsPreSave}、{@link XlsPostSave} を使用し、処理を実装します。
 *
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
 *     {@literal @XlsPostLoad}
 *     public void onPostLoad(SampleSheet targetObj) {
 *         // 読み込み後に実行される処理
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
 *     {@literal @XlsPostLoad}
 *     public void onPostLoad(UserRecord targetObj, Sheet sheet, Configuration config, SheetBindingErrors errors) {
 *         // 読み込み後に実行される処理
 *         // 入力値チェックなどを行う
 *     }
 * }
 * </code></pre>
 *
 * @version 2.0
 * @since 1.3
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsListener {

    /**
     * リスナークラスの指定。
     * @return 任意の実装クラスを指定します。
     */
    Class<?>[] value();

}
