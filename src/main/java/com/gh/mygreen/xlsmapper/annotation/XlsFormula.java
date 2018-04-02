package com.gh.mygreen.xlsmapper.annotation;

import java.awt.Point;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.expression.CustomFunctions;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * 書き込み時にセルの数式を定義するためのアノテーションです。
 *
 * <h3 class="description">式言語処理のカスタマイズ</h3>
 *
 * <p>数式を直接指定する場合は、EL式の1つの実装である <a href="http://commons.apache.org/proper/commons-jexl/" target="_blnak">JEXL</a>
 *    が利用できますが、実装を切り替えたり、デフォルトの関数を登録したりとカスタマイズができます。
 * </p>
 * <p>設定を変更したい場合は、{@link Configuration#getFormulaFormatter()} の値を変更します。</p>
 *
 * <pre class="highlight"><code class="java">
 * // 数式をフォーマットする際のEL関数を登録する。
 * ExpressionLanguageJEXLImpl formulaEL = new ExpressionLanguageJEXLImpl();
 * {@literal Map<String, Object>} funcs = new {@literal HashMap<>}();
 * funcs.put("x", CustomFunctions.class);
 * formulaEL.getJexlEngine().setFunctions(funcs);
 *
 * // 数式をフォーマットするEL式の実装を変更する
 * XlsMapper mapper = new XlsMapper();
 * mapper.getConig().getFormulaFormatter().setExpressionLanguage(formulaEL);
 * </code></pre>
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsFormula {

    /**
     * 数式を直接指定します。
     * <p>数式を指定する際に、メッセージファイルと同様に、変数やEL式が利用可能です。</p>
     * <ul>
     *   <li>変数は {@literal {変数名}} で定義します。</li>
     *   <li>EL式は {@literal ${EL式}} で定義します。
     *     <ul>
     *        <li>EL式は、<a href="http://commons.apache.org/proper/commons-jexl/" target="_blank">JEXL(Java Expression Language)</a>の形式で指定します。</li>
     *        <li>JEXLの仕様は、<a href="http://commons.apache.org/proper/commons-jexl/reference/syntax.html" target="_blank">JEXL Reference</a> を参照してください。</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>EL式の中では、予め次の変数が登録されており、セルの値ごとに変わります。</p>
     * <ul>
     *   <li>rowIndex : 処理対象のセルの行のインデックス。0から始まります。</li>
     *   <li>columnIndex : 処理対象のセルの列のインデックス。0から始まります。</li>
     *   <li>rowNumber : 処理対象のセルの行番号。1から始まります。</li>
     *   <li>columnNumber : 処理対象のセルの列番号。1から始まります。</li>
     *   <li>columnAlpha : 処理対象のセルの列の名前。Aから始まります。</li>
     *   <li>address : 処理対象のセルのアドレス。 A1 の形式です。</li>
     *   <li>targetBean : 処理対象のプロパティが定義されているJavaBeanのオブジェクトです。</li>
     *   <li>cell : 処理対象のセルのオブジェクトです。POIのクラス{@link Cell}のオブジェクトです。</li>
     * </ul>
     *
     * <p>さらに、よく使う関数が登録されており、呼び出すことができます。
     *   <br>関数の実態は、 {@link CustomFunctions}です。
     *   <br>名前空間{@code x}で登録されており、{@literal x:colTlAlpha(...)}のように呼び出すことができます。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="サンプル")}
     * public class SampleSheet {
     *
     *     // 数式の指定
     *     {@literal @XlsOrder(1)}
     *     {@literal @XlsLabelledCell(label="更新日付", type=LabelledCellType.Right)}
     *     {@literal @XlsFormula("TODAY()")}
     *     private Date date;
     *
     *     {@literal @XlsOrder(2)}
     *     {@literal @XlsHorizontalRecords(tableLabel="レコード", terminal=RecordTerminal.Border)}
     *     {@literal @XlsRecordOption(overOperation=OverOperation.Insert}
     *     private {@literal List<SampleRecord>} records;
     * }
     *
     * public class SampleRecord {
     *
     *     // マッピングした位置情報
     *     private {@literal Map<String, CellPosition>} positions;
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     private String name;
     *
     *     {@literal @XlsColumn(columnName="国語")}
     *     private int kokugo;
     *
     *     {@literal @XlsColumn(columnName="算数")}
     *     private int sansu;
     *
     *     // 数式の指定（変数、EL式を使用して指定）
     *     {@literal @XlsColumn(columnName="合計")}
     *     {@literal @XlsFormula(value="SUM(${x:colToAlpha(targetBean.kokugoColNum)}{rowNumber}:${x:colToAlpha(targetBean.sansuColNum)}{rowNumber})", primary=true)}
     *     private int sum;
     *
     *     // プロパティ「kokugo」の列番号を返す。
     *     public String getKokugoColNum() {
     *         CellPosition position = positions.get("kokugo");
     *         return position.addRow(1);
     *
     *     }
     *
     *     // プロパティ「sansu」の列番号を返す。
     *     public String getSansuColNum() {
     *         CellPosition position = positions.get("sansu");
     *         return position.addRow(1);
     *     }
     *
     * }
     * </code></pre>
     *
     * @return Excelの数式を返す。(例. {@literal SUM(A{rowNumber},B{rowNumber})})
     */
    String value() default "";

    /**
     * 数式を別メソッドで組み立てる場合に、そのメソッド名を指定します。
     * <p>条件により数式を変更するような場合や、複雑な数式を組み立てる場合、数式を組み立てるメソッドを指定することができます。</p>
     * <p>メソッドの条件は次のようになります。</p>
     * <ul>
     *    <li>定義位置は、プロパティが定義してあるJavaBeanのクラスと同じ箇所。</li>
     *    <li>修飾子は、public/private/protected などなんでもよい。</li>
     *    <li>引数は、指定しないか、または次の値が指定可能。順番は任意。
     *      <ul>
     *        <li>セルのオブジェクト : {@link Cell}</li>
     *        <li>シートのオブジェクト : {@link Sheet}</li>
     *        <li>セルの座標 : {@link Point}。0から始まります。</li>
     *        <li>セルの座標 : {@link CellPosition}。</li>
     *        <li>セルの座標 : {@link org.apache.poi.ss.util.CellAddress}。</li>
     *        <li>システム設定 : {@link Configuration}</li>
     *      </ul>
     *    </li>
     *    <li>戻り値は、String型。
     *      <ul>
     *        <li>nullまたは空文字を返すと、ブランクセルとして出力されます。</li>
     *      </ul>
     *    </li>
     * </ul>
     *
     * <pre class="highlight"><code class="java">
     * {@literal @XlsSheet(name="サンプル")}
     * public class SampleSheet {
     *
     *     // 数式のメソッドの指定
     *     {@literal @XlsOrder(1)}
     *     {@literal @XlsLabelledCell(label="更新日付", type=LabelledCellType.Right)}
     *     {@literal @XlsFormula(methodName="getDateFormula")}
     *     private Date date;
     *
     *     {@literal @XlsOrder(2)}
     *     {@literal @XlsHorizontalRecords(tableLabel="レコード", terminal=RecordTerminal.Border)}
     *     {@literal @XlsRecordOption(overOperation=OverOperation.Insert}
     *     private {@literal List<SampleRecord>} records;
     *
     *     // 数式を組み立てるメソッド
     *     public String getDateFormula() {
     *         return "TODAY()"
     *     }
     * }
     *
     * public class SampleRecord {
     *
     *     // マッピングした位置情報
     *     private {@literal Map<String, CellPosition>} positions;
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     private String name;
     *
     *     {@literal @XlsColumn(columnName="国語")}
     *     private int kokugo;
     *
     *     {@literal @XlsColumn(columnName="算数")}
     *     private int sansu;
     *
     *     // 数式の指定（メソッドを指定）
     *     {@literal @XlsColumn(columnName="合計")}
     *     {@literal @XlsFormula(methodName="getSumFormula", primary=true)}
     *     private int sum;
     *
     *     // 数式を組み立てるメソッド
     *     private String getSumFormula(CellPosition position) {
     *
     *         int rowNumber = CellPosition.addRow(1);
     *         String colKokugo = CellReference.convertNumToColString(positions.get("kokugo").y);
     *         String colSansu = CellReference.convertNumToColString(positions.get("sansu").y);
     *
     *         return String.format("SUM(%s%d:%s%d)", colKokugo, rowNumber, colSansu, rowNumber);
     *     }
     *
     * }
     * </code></pre>
     *
     * @return JavaBeanに定義されているメソッド名を返す。
     */
    String methodName() default "";

    /**
     * 出力する際に、値が設定されていても、数式を優先するかどうかを指定します。
     * <p>出力するオブジェクトのプロパティに値が設定されている場合、アノテーション {@link XlsFormula} を指定していても、デフォルトでは値が設定されます。</p>
     * <p>属性 {@literal primary=true} を指定すると数式が優先されます。
     *   <br>特に、プリミティブ型など初期値が入っている場合や、アノテーション {@literal @XlsConverter(defaultValue="<初期値>")} で初期値を指定している場合には、注意が必要です。
     * </p>
     *
     * <pre class="highlight"><code class="java">
     * public class SampleRecord {
     *
     *     // マッピングした位置情報
     *     private {@literal Map<String, CellPosition>} positions;
     *
     *     {@literal @XlsColumn(columnName="名前")}
     *     private String name;
     *
     *     {@literal @XlsColumn(columnName="国語")}
     *     private int kokugo;
     *
     *     {@literal @XlsColumn(columnName="算数")}
     *     private int sansu;
     *
     *     // 数式の指定（数式を優先する場合）
     *     {@literal @XlsColumn(columnName="合計")}
     *     {@literal @XlsFormula(value="SUM(B{rowNumber}:C{rowNumber})", primary=true)
     *     private int sum;
     *
     * }
     * </code></pre>
     *
     * @return 'true'の場合、数式の設定を優先します。
     *         'false'の場合、値が設定されていると、その値が出力されます。
     */
    boolean primary() default false;

}
