package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;


/**
 * 独自にExcelのセルとJavaオブジェクトをマッピングしたい場合に指定するアノテーションです。
 * 
 * <p>本ライブラリで対応していない型に変換したい時など、属性{@link #converterClass()}で独自の変換用クラスを指定します。</p>
 * <p>変換用クラスは、インタフェース {@link CellConverter}を実装する必要があります。</p>
 * <p>詳細なサンプルは、本プログラムのパッケージ「com.gh.mygreen.xlsmapper.cellconvert.impl」以下のクラスを参照してください。
 *    <br>または、マニュアル <a href="/xlsmapper/sphinx/extension_cellconverter.html" target="_blank">CellConverterの拡張</a>を参照してください。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // CellConverterの定義
 * public class LocaleDateConverter extends {@literal CellConverter<LocaleDate>} {
 * 
 *     // シート読み込み時のExcel Cell {@literal =>} Javaオブジェクトに変換する。
 *     {@literal @Override}
 *     public LocaleDate toObject(final Cell cell, final FieldAccessor accessor, final XlsMapperConfig config)
 *         throws XlsMapperException {
 *         {@literal //TODO:} 実装する
 *     }
 *     
 *     //シート書き込み時のJavaオブジェクト {@literal =>} Excel Cellに変換する。
 *     {@literal @Override}
 *     Cell toCell(FieldAccessor accessor, LocaleDate targetValue, Sheet sheet, CellAddress address, XlsMapperConfig config) throws XlsMapperException;
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
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsConverter {
    
    /**
     * セルの変換クラスを指定します。
     * @return {@link CellConverter}の実装クラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends CellConverter> converterClass();
    
}
