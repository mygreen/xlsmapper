package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;


/**
 * 独自のクラスタイプの変換処理を指定するためのアノテーションです。
 *
 * <p>本ライブラリで対応していない型に変換したい時に、属性{@link #value()}で独自の変換用クラスのファクトリクラスを指定します。</p>
 * <ul>
 *   <li>ファクトリクラスは、インタフェース {@link CellConverterFactory}を実装する必要があります。</li>
 *   <li>セルの変換用クラスは、インタフェース {@link CellConverter}を実装する必要があります。</li>
 * </ul>
 * <p>詳細なサンプルは、本プログラムのパッケージ「com.gh.mygreen.xlsmapper.cellconverter.impl」以下のクラスを参照してください。
 *    <br>または、マニュアル <a href="/xlsmapper/sphinx/extension_cellconverter.html" target="_blank">CellConverterの拡張</a>を参照してください。
 * </p>
 *
 * <pre class="highlight"><code class="java">
 * // CellConverterFactoryの定義
 * public class CustomCellConverterFactory implements {@literal CellConverterFactory<LocalDate>} {
 *
 *      {@literal @Override}
 *      CustomCellConverter create(FieldAccessor field, Configuration config) {
 *          {@literal // CellConverterのインスタンスの作成}
 *          return new CustomCellConverter();
 *      }
 *
 * }
 *
 * // CellConverterの定義
 * public class CustomCellConverter implements {@literal CellConverter<LocaleDate>} {
 *
 *     // シート読み込み時のExcel Cell {@literal =>} Javaオブジェクトに変換する。
 *     {@literal @Override}
 *     public LocaleDate toObject(final Cell cell)
 *         throws XlsMapperException {
 *         {@literal //TODO:} 実装する
 *     }
 *
 *     //シート書き込み時のJavaオブジェクト {@literal =>} Excel Cellに変換する。
 *     {@literal @Override}
 *     Cell toCell(LocalDate targetValue, Object targetBean, Sheet sheet, CellPosition address) throws XlsMapperException;
 *         {@literal //TODO:} 実装する
 *     }
 * }
 *
 * // 独自CellConverterの指定
 * public class SampleRecord {
 *
 *     // フィールド独自のCellConveterの設定
 *     {@literal @XlsColumn(columnName="更新日付")}
 *     {@literal @XlsConverter(CustomCellConverterFactory.class)}
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
     * セルの変換クラスを作成するクラスを指定します。
     * @return {@link CellConverterFactory}の実装クラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends CellConverterFactory> value();

}
