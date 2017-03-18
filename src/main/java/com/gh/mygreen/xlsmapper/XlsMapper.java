package com.gh.mygreen.xlsmapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gh.mygreen.xlsmapper.annotation.OverRecordOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
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
 *   <li>セル「Date」の書き込み時の書式を指定するために、アノテーション {@link XlsDateConverter} に付与します。
 *     <br>属性 {@link XlsDateConverter#excelPattern()} でExcelのセルの書式を設定します。
 *   </li>
 *   <li>表「User List」のレコードを追加する操作を指定するために、アノテーションの属性 {@link XlsHorizontalRecords#overRecord()}を指定します。
 *     <br>テンプレート上は、レコードが1行分しかないですが、実際に書き込むレコード数が2つ以上の場合、足りなくなるため、その際のシートの操作方法を指定します。
 *     <br>今回の{@link OverRecordOperation#Insert}は、行の挿入を行います。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * // シート用のPOJOクラスの定義
 * {@literal @XlsSheet(name="List")}
 * public class UserSheet {
 *
 *     {@literal @XlsLabelledCell(label="Date", type=LabelledCellType.Right)}
 *     {@literal @XlsDateConverter(excelPattern="yyyy/m/d")}
 *     Date createDate;
 *     
 *     {@literal @XlsHorizontalRecords(tableLabel="User List", overRecord=OverRecordOperate.Insert)}
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
 * @author T.TSUCHIE
 *
 */
public class XlsMapper {
    
    private XlsMapperConfig config;
    
    private XlsLoader loader;
    
    private XlsSaver saver;
    
    public XlsMapper() {
        this.config = new XlsMapperConfig();
        this.loader = new XlsLoader(getConig());
        this.saver = new XlsSaver(getConig());
    }
    
    public XlsMapperConfig getConig() {
        return config;
    }
    
    public void setConig(XlsMapperConfig config) {
        this.config = config;
        getLoader().setConfig(config);
        getSaver().setConfig(config);
    }
    
    public XlsLoader getLoader() {
        return loader;
    }
    
    public XlsSaver getSaver() {
        return saver;
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     * 
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param xmlIn XMLによる定義を必要としない場合は、nullを指定する。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz, xmlIn);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     * 
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final SheetBindingErrors errors) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz, errors);
    }
    
    /**
     * Excelファイルの１シートを読み込み、任意のクラスにマッピングする。
     * @param xlsIn 読み込みもとのExcelファイルのストリーム。
     * @param clazz マッピング先のクラスタイプ。
     * @param xmlIn XMLによる定義を必要としない場合は、nullを指定する。
     * @param errors マッピング時のエラー情報。指定しない場合は、nulを指定する。
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     * @throws IllegalArgumentException xlsIn == null.
     * @throws IllegalArgumentException clazz == null.
     */
    public <P> P load(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn, final SheetBindingErrors errors) throws XlsMapperException, IOException {
        return loader.load(xlsIn, clazz, xmlIn, errors);
    }
    
    /**
     * Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz);
    }
    
    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param xmlIn
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz, xmlIn);
    }
    
    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param errorsContainer
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz, errorsContainer);
    }
    
    /**
     * XMLによるマッピングを指定し、Excelファイルの複数シートを読み込み、任意のクラスにマップする。
     * @param xlsIn
     * @param clazz
     * @param xmlIn
     * @param errorsContainer
     * @return
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public <P> P[] loadMultiple(final InputStream xlsIn, final Class<P> clazz, final InputStream xmlIn,
            SheetBindingErrorsContainer errorsContainer) throws XlsMapperException, IOException {
        return loader.loadMultiple(xlsIn, clazz, xmlIn, errorsContainer);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes, final InputStream xmlIn) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes, xmlIn);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes, errorsContainer);
    }
    
    public Object[] loadMultiple(final InputStream xlsIn, final Class<?>[] classes, final InputStream xmlIn,
            final SheetBindingErrorsContainer errorsContainer) throws XlsMapperException {
        return loader.loadMultiple(xlsIn, classes, xmlIn, errorsContainer);
    }
    
    /**
     * JavaのオブジェクトをExeclファイルに出力する。
     * <p>出力するファイルは、引数で指定した雛形となるテンプレート用のExcelファイルをもとに出力する。
     * @param templateXlsIn 雛形となるExcelファイルの
     * @param xlsOut 出力
     * @param beansObj 書き込み元のオブジェクト
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beansObj) throws XlsMapperException, IOException {
        saver.save(templateXlsIn, xlsOut, beansObj);
    }
    
    /**
     * XMLによるマッピングを指定して、JavaのオブジェクトをExcelファイルに出力する。
     * @param templateXlsIn
     * @param xlsOut
     * @param beansObj
     * @param xmlIn
     * @throws XlsMapperException 
     * @throws IOException 
     */
    public void save(final InputStream templateXlsIn, final OutputStream xlsOut, final Object beansObj, final InputStream xmlIn) throws XlsMapperException, IOException {
        saver.save(templateXlsIn, xlsOut, beansObj, xmlIn);
    }
    
    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力
     * @param beanObjs 書き込むオブジェクトの配列。
     * @throws XlsMapperException
     * @throws IOException 
     */
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs) throws XlsMapperException, IOException {
        saver.saveMultiple(templateXlsIn, xlsOut, beanObjs);
    }
    
    /**
     * 複数のオブジェクトをそれぞれのシートへ保存する。
     * @param templateXlsIn 雛形となるExcelファイルの入力
     * @param xlsOut 出力
     * @param beanObjs 書き込むオブジェクトの配列。
     * @param xmlIn アノテーションの定義をしているXMLファイルの入力。
     * @throws XlsMapperException
     * @throws IOException 
     */
    public void saveMultiple(final InputStream templateXlsIn, final OutputStream xlsOut, final Object[] beanObjs, final InputStream xmlIn) throws XlsMapperException, IOException {
        saver.saveMultiple(templateXlsIn, xlsOut, beanObjs, xmlIn);
    }
    
}
