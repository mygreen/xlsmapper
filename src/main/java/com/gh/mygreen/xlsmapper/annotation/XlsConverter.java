package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.cellconvert.CellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.DefaultCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;


/**
 * 基本的な変換規則を指定するアノテーション。
 * 
 * <h3 class="description">初期値の指定</h3>
 * <p>初期値を設定する場合、属性{@link #defaultValue()}で指定します。</p>
 * 
 * <ul>
 *   <li>日付などの書式がある場合、専用のアノテーションで指定した書式{@link XlsDateConverter#javaPattern()}を元に、
 *       文字列をそのオブジェクトに変換し処理します。
 *   </li>
 *   <li>指定したデフォルト値がマッピング先の型として不正な場合は、
 *       通常の型変換エラーと同様に、例外 {@link TypeBindException} がスローされます。
 *   </li>
 *   <li>属性{@link #defaultValue()}を指定しない場合、プリミティブ型に対して読み込むと、その型の初期値が設定されます。
 *       例えば、int型は0、doubleは0.0、booleanはfalse。char型は、'{@literal \}u0000'。
 *       <br>プリミティブ型のラッパークラスや参照型の場合は、nullが設定されます。
 *   </li>
 *   <li>char型にマッピングする場合、デフォルト値が2文字以上でも、先頭の位置文字がマッピングされます。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 * 
 *     {@literal @XlsColumn(columnName="ID")}
 *     {@literal @XlsConverter(defaultValue="-1")}
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="更新日時")}
 *     {@literal @XlsConverter(defaultValue="2010/01/01")} // 属性javaPatternで指定した書式に沿った値を指定します。
 *     {@literal @XlsDateConverter(javaPattern="yyyy/MM/dd")}
 *     private Date updateTime;
 * 
 * }
 * </code></pre>
 * 
 * <h3 class="description">トリミングの指定</h3>
 * <p>トリミングを行いたい場合、属性{@link #trim()}の値を'true'に指定します。</p>
 * 
 * <ul>
 *   <li>シート上のセルのタイプ（分類）が数値などの文字列以外の場合は、トリム処理は行われません。
 *       <br>ただし、シートのセルタイプが文字列型で、Javaの型がString型以外の数値型やDate型などの場合は、変換する前にトリム処理を行います。
 *   </li>
 *   <li>値が空のセルをString型に読み込む場合、{@link #trim()}がfalseのときはnull設定されますが、
 *        トリムを有効にしていると空文字が設定されます。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 * 
 *     {@literal @XlsColumn(columnName="ID")}
 *     {@literal @XlsConverter(defaultValue=" 123 ", trim=true)} // 属性defaultValueもトリム対象となる。
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     {@literal @XlsConverter(trim=true)} // 空のセルを読み込むと空文字が設定される。
 *     private String name;
 *     
 * }
 * </code></pre>
 *
 *
 * <h3 class="description">書き込み時のセルの文字の制御の指定</h3> 
 * <p>書き込み時にセルの「折り返し設定」「縮小表示」を制御することができます。</p>
 * 
 * <ul>
 *   <li>属性{@link #wrapText()}をtrueにすると、強制的にセルの内の文字表示の設定「折り返して全体を表示する」が有効になります。
 *    <br>falseの場合、テンプレートとなるセルの設定を引き継ぎ、変更はされません。
 *   </li>
 *   <li>属性{@link #shrinkToFit()}をtrueにすると、強制的にセル内の文字表示の設定「縮小して全体を表示する」が有効になります。
 *      <br>falseの場合、テンプレートとなるセルの設定を引き継ぎ、変更はされません。
 *   </li>
 *   <li>属性{@link #wrapText()}と{@link #shrinkToFit()}の両方の値をtrueに指定する場合、
 *       {@link #shrinkToFit()}の設定が優先され、「縮小して全体を表示する」が有効になります。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * public class SampleRecord {
 * 
 *     {@literal @XlsColumn(columnName="ID")}
 *     {@literal @XlsConverter(wrapText=true)} // 「縮小して全体を表示する」が有効になる。
 *     private int id;
 *     
 *     {@literal @XlsColumn(columnName="名前")}
 *     {@literal @XlsConverter(shrinkToFit=true)} //「折り返して全体を表示する」が有効になる。
 *     private String name;
 *     
 *     {@literal @XlsColumn(columnName="備考")}
 *     {@literal @XlsConverter(shrinkToFit=false)} // 設定しない場合は、テンプレート設定が有効になる。
 *     private String comment;
 *     
 * }
 * </code></pre>
 * 
 * <h3 class="description">独自の変換規則の指定</h3> 
 * <p>本ライブラリで対応していない型に変換したい時など、属性{@link #converterClass()}で独自のConverter用クラスを指定します。</p>
 * 
 * <p>Converterクラスは、インタフェース {@link CellConverter}を実装する必要があります。</p>
 * 
 * <p>詳細なサンプルは、本プログラムのパッケージ「com.gh.mygreen.xlsmapper.cellconvert.converter」以下のクラスを参照してください。
 *    <br>または、マニュアル <a href="/xlsmapper/sphinx/extension_cellconverter.html" target="_blank">CellConverterの拡張</a>を参照してください。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // CellConverterの定義
 * public class LocaleDateConverter extends {@literal CellConverter<LocaleDate>} {
 * 
 *     // シート読み込み時のExcel Cell => Javaオブジェクトに変換する。
 *     {@literal @Override}
 *     public LocaleDate toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
 *         throws XlsMapperException {
 *         {@literal //TODO:} 実装する
 *     }
 *     
 *     //シート書き込み時のJavaオブジェクト => Excel Cellに変換する。
 *     {@literal @Override}
 *     Cell toCell(FieldAdaptor adaptor, LocaleDate targetValue, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
 *         {@literal //TODO:} 実装する
 *     }
 * }
 * 
 * // 独自CellConverterの指定
 * public class SampleRecord {
 *
 *     // フィールド独自のCellConveterの設定
 *     {@literal @XlsColumn(columnName="更新日付")}
 *     {@literal @XlsConverter(converterClass=LocaleDateConvereter.class)}
 *     private LocaleDate localeDate;
 *    
 * }
 * </code></pre>
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsConverter {
    
    /**
     * 読み込み時、書き込み時にトリミングします。
     * @return
     */
    boolean trim() default false;
    
    /**
     * 'true'のとき書き込み時にセルの「折り返し設定」を有効にします。
     * 'false'の場合は、既存の折り返し設定は変更せずに、テンプレートファイルの設定を引き継ぎます。
     * @return
     */
    boolean wrapText() default false;
    
    /**
     * 'true'のとき書き込み時にセルの「縮小して表示」を有効にします。
     * 'false'の場合は、既存の縮小して表示は変更せずに、テンプレートファイルの設定を引き継ぎます。
     */
    boolean shrinkToFit() default false;
    
    /**
     * 読み込み時または書き込み時のデフォルト値を指定します。
     * 日付など書式が設定されている場合は、書式に沿った値を設定してください。
     * @return
     */
    String defaultValue() default"";
    
    /**
     * 独自のConverterで処理したい場合に指定します。
     * @return {@link CellConverter}の実装クラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends CellConverter> converterClass() default DefaultCellConverter.class;
    
}
