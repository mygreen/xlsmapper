package com.gh.mygreen.xlsmapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.validation.MultipleSheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * ExcelのシートとJavaオブジェクトをマッピングする機能を提供する。
 *
 * <h3 class="description">マッピングの基本</h3>
 * <p>次のような表のExcelシートをマッピングする例を説明します。</p>
 *
 * <div class="picture">
 *    <img src="doc-files/howto_load.png" alt="">
 *    <p>基本的なマッピング</p>
 * </div>
 *
 * <p>まず、シート1つに対して、POJOクラスを作成します。</p>
 * <ul>
 *   <li>シート名を指定するために、アノテーション {@link XlsSheet} をクラスに付与します。</li>
 *   <li>見出し付きのセル「Date」をマッピングするフィールドに、アノテーション {@link XlsLabelledCell} に付与します。</li>
 *   <li>表「User List」をマッピングするListのフィールドに、アノテーション {@link XlsHorizontalRecords} を付与します。</li>
 * </ul>
 *
 *
 * <pre class="highlight"><code class="java">
 * // シート用のPOJOクラスの定義
 * {@literal @XlsSheet(name="List")}
 * public class UserSheet {
 *
 *     {@literal @XlsLabelledCell(label="Date", type=LabelledCellType.Right)}
 *     Date createDate;
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="User List")}
 *     {@literal List<UserRecord>} users;
 *
 * }
 * </code></pre>
 *
 *
 * <p>続いて、表「User List」の1レコードをマッピングするための、POJOクラスを作成します。</p>
 * <ul>
 *   <li>レコードの列をマッピングするために、アノテーション {@link XlsColumn} をフィールドに付与します。</li>
 *   <li>フィールドのクラスタイプが、intや列挙型の場合もマッピングできます。</li>
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * // レコード用のPOJOクラスの定義
 * public class UserRecord {
 *
 *     {@literal @XlsColumn(columnName="ID")}
 *     int no;
 *
 *     {@literal @XlsColumn(columnName="Class", merged=true)}
 *     String className;
 *
 *     {@literal @XlsColumn(columnName="Name")}
 *     String name;
 *
 *     {@literal @XlsColumn(columnName="Gender")}
 *     Gender gender;
 *
 * }
 *
 * // 性別を表す列挙型の定義
 * public enum Gender {
 *    male, female;
 * }
 * </code></pre>
 *
 *
 * <p>作成したPOJOを使ってシートを読み込むときは、XlsMapper#load メソッドを利用します。</p>
 *
 * <pre class="highlight"><code class="java">
 * // シートの読み込み
 * XlsMapper xlsMapper = new XlsMapper();
 * UserSheet sheet = xlsMapper.load(
 *         new FileInputStream("example.xls"), // 読み込むExcelファイル。
 *         UserSheet.class                     // シートマッピング用のPOJOクラス。
 *   );
 * </code></pre>
 *
 *
 * <h3 class="description">書き込み方の基本</h3>
 * <p>同じシートの形式を使って、書き込み方を説明します。
 *   <br>まず、書き込み先のテンプレートとなるExcelシートを用意します。 レコードなどは空を設定します。
 * </p>
 *
 * <div class="picture">
 *    <img src="doc-files/howto_save.png" alt="">
 *    <p>データが空のテンプレートファイル</p>
 * </div>
 *
 * <p>続いて、読み込み時に作成したシート用のマッピングクラスに、書き込み時の設定を付け加えるために修正します。</p>
 * <ul>
 *   <li>セル「Date」の書き込み時の書式を指定するために、アノテーション {@link XlsDateTimeConverter} に付与します。
 *     <br>属性 {@link XlsDateTimeConverter#excelPattern()} でExcelのセルの書式を設定します。
 *   </li>
 *   <li>表「User List」のレコードを追加する操作を指定するために、アノテーションの属性 {@link XlsRecordOption#overOperation()}を指定します。
 *     <br>テンプレート上は、レコードが1行分しかないですが、実際に書き込むレコード数が2つ以上の場合、足りなくなるため、その際のシートの操作方法を指定します。
 *     <br>今回の{@link OverOperation#Insert}は、行の挿入を行います。
 *   </li>
 * </ul>
 *
 * <pre class="highlight"><code class="java">
 * // シート用のPOJOクラスの定義
 * {@literal @XlsSheet(name="List")}
 * public class UserSheet {
 *
 *     {@literal @XlsLabelledCell(label="Date", type=LabelledCellType.Right)}
 *     {@literal @XlsDateTimeConverter(excelPattern="yyyy/m/d")}
 *     Date createDate;
 *
 *     {@literal @XlsHorizontalRecords(tableLabel="User List")}
 *     {@literal @XlsRecordOperation(overCase=OverOperation#Insert)}
 *     {@literal List<UserRecord>} users;
 *
 * }
 * </code></pre>
 *
 * <p>修正したPOJOを使ってシートを書き込むときは、 XlsMapper#save メソッドを利用します。</p>
 *
 * <pre class="highlight"><code class="java">
 * // 書き込むシート情報の作成
 * UserSheet sheet = new UserSheet();
 * sheet.createDate = new Date();
 *
 * {@literal List<UserRecord>} users = new {@literal ArrayList<>}();
 *
 * // 1レコード分の作成
 * UserRecord record1 = new UserRecord();
 * record1.no = 1;
 * record1.className = "A";
 * record1.name = "Ichiro";
 * record1.gender = Gender.male;
 * users.add(record1);
 *
 * UserRecord record2 = new UserRecord();
 * // ... 省略
 * users.add(record2);
 *
 * sheet.users = users;
 *
 * // シートの書き込み
 * XlsMapper xlsMapper = new XlsMapper();
 * xlsMapper.save(
 *     new FileInputStream("template.xls"), // テンプレートのExcelファイル
 *     new FileOutputStream("out.xls"),     // 書き込むExcelファイル
 *     sheet                                // 作成したデータ
 *     );
 * </code></pre>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsMapper {

    private Configuration configuration;

    private XlsLoader loader;

    private XlsSaver saver;

    /**
     * デフォルトコンストラクタ
     */
    public XlsMapper() {
        this.configuration = new Configuration();
        this.loader = new XlsLoader(getConfiguration());
        this.saver = new XlsSaver(getConfiguration());
    }

    /**
     * システム情報を取得します。
     * @return 現在のシステム情報
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * システム情報を設定します。
     * @param configuration システム情報
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        getLoader().setConfiguration(configuration);
        getSaver().setConfiguration(configuration);
    }

    /**
     * 読み込み用クラスを取得します。
     * @return 読み込み用クラス
     */
    public XlsLoader getLoader() {
        return loader;
    }

    /**
     * 保存用クラスを取得します。
     * @return 保存用クラス。
     */
    public XlsSaver getSaver() {
        return saver;
    }

    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return シートをマッピングしたオブジェクト。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、nullを返します。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException Excelファイルのマッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     *
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz);
    }

    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return シートのマッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、nullを返します。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException Excelファイルのマッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public <P> SheetBindingErrors<P> loadDetail(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        return loader.loadDetail(xlsIn, clazz);
    }

    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * <p>{@link XlsSheet#regex()}により、複数のシートが同じ形式で、同じクラスにマッピングすする際に使用します。</p>
     *
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return マッピングした複数のシート。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz);
    }

    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * <p>{@link XlsSheet#regex()}により、複数のシートが同じ形式で、同じクラスにマッピングすする際に使用します。</p>
     *
     * @param <P> シートをマッピングするクラスタイプ
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return 複数のシートのマッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or clazz == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public <P> MultipleSheetBindingErrors<P> loadMultipleDetail(final InputStream xlsIn, final Class<P> clazz)
            throws XlsMapperException, IOException {
        return loader.loadMultipleDetail(xlsIn, clazz);
    }

    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * <p>複数のシートの形式を一度に読み込む際に使用します。</p>
     *
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param classes マッピング先のクラスタイプの配列。
     * @return マッピングした複数のシート。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or classes == null}
     * @throws IllegalArgumentException {@literal calsses.length == 0}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, classes);
    }

    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * <p>複数のシートの形式を一度に読み込む際に使用します。</p>
     *
     * @param xlsIn 読み込み元のExcelファイルのストリーム。
     * @param classes マッピング先のクラスタイプの配列。
     * @return マッピングした複数のシートの結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、マッピング結果には含まれません。
     * @throws IllegalArgumentException {@literal xlsIn == null or classes == null}
     * @throws IllegalArgumentException {@literal calsses.length == 0}
     * @throws IOException ファイルの読み込みに失敗した場合
     * @throws XlsMapperException マッピングに失敗した場合
     */
    public MultipleSheetBindingErrors<Object> loadMultipleDetail(final InputStream xlsIn, final Class<?>[] classes)
            throws XlsMapperException, IOException {
        return loader.loadMultipleDetail(xlsIn, classes);
    }

    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。</p>
     *
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力先のストリーム
     * @param beanObj 書き込むBeanオブジェクト
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObj == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beanObj) throws XlsMapperException, IOException {
        saver.save(templateXlsIn, xlsOut, beanObj);
    }

    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。</p>
     *
     * @param <P> マッピング対象のクラスタイプ
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力先のストリーム
     * @param beanObjs 書き込むBeanオブジェクト
     * @return マッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、nullを返します。
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObj == null}
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public <P> SheetBindingErrors<P> saveDetail(final InputStream templateXlsIn, final OutputStream xlsOut, final P beanObjs) throws XlsMapperException, IOException {
        return saver.saveDetail(templateXlsIn, xlsOut, beanObjs);
    }

    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut xlsOut 出力先のストリーム
     * @param beanObj 書き込むオブジェクトの配列。
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObjs == null}
     * @throws IllegalArgumentException {@literal }
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObj) throws XlsMapperException, IOException {
        saver.saveMultiple(templateXlsIn, xlsOut, beanObj);
    }

    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut xlsOut 出力先のストリーム
     * @param beanObjs 書き込むオブジェクトの配列。
     * @return マッピング結果。
     *         {@link Configuration#isIgnoreSheetNotFound()}の値がtrueで、シートが見つからない場合、結果に含まれません。
     * @throws IllegalArgumentException {@literal templateXlsIn == null or xlsOut == null or beanObjs == null}
     * @throws IllegalArgumentException {@literal }
     * @throws XlsMapperException マッピングに失敗した場合
     * @throws IOException テンプレｰトのファイルの読み込みやファイルの出力に失敗した場合
     */
    public MultipleSheetBindingErrors<Object> saveMultipleDetail(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs) throws XlsMapperException, IOException {
        return saver.saveMultipleDetail(templateXlsIn, xlsOut, beanObjs);
    }

}
