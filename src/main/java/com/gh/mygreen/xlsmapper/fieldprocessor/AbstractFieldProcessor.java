package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.ConversionException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;


/**
 * 各種アノテーションを処理するためのクラスの抽象クラス。
 * <p>通常はこのクラスを継承して作成します。</p>
 *
 * @param <A> サポートするアノテーション
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractFieldProcessor<A extends Annotation> implements FieldProcessor<A> {

    /**
     * 該当するタイプのConverterが見つからないときの例外のインスタンスを作成する。
     * @param targetType クラスタイプ
     * @return {@link ConversionException} のインスタンス
     */
    protected ConversionException newNotFoundCellConverterExpcetion(final Class<?> targetType) {
        return new ConversionException(
                MessageBuilder.create("cellConverter.notFound")
                    .varWithClass("classType", targetType)
                    .format(),
                targetType);
    }

    /**
     * 指定したタイプに対する{@link CellConverter}を取得します。
     * <p>アノテーション「{@link XlsConverter}」が付与されている場合、そちらの設定値を優先します。</p>
     *
     * @param accessor フィールド情報
     * @param config システム情報設定。
     * @return {@link CellConverter}のインスタンス
     * @throws ConversionException {@link CellConverter}が見つからない場合。
     */
    protected CellConverter<?> getCellConverter(final FieldAccessor accessor, final Configuration config) throws ConversionException {

        final CellConverter<?> converter;

        if(accessor.hasAnnotation(XlsConverter.class)) {
            XlsConverter converterAnno = accessor.getAnnotationNullable(XlsConverter.class);
            converter = config.createBean(converterAnno.value()).create(accessor, config);

        } else {
            CellConverterFactory<?> converterFactory = config.getConverterRegistry().getConverterFactory(accessor.getType());
            if(converterFactory == null) {
                throw newNotFoundCellConverterExpcetion(accessor.getType());
            }
            converter = converterFactory.create(accessor, config);
        }

        return converter;
    }

    /**
     * コンポーネントタイプを指定して、指定したタイプに対する{@link CellConverter}を取得します。
     * <p>アノテーション「{@link XlsConverter}」が付与されている場合、そちらの設定値を優先します。</p>
     *
     * @param componentType コンポーネントのクラスタイプ
     * @param accessor フィールド情報
     * @param config システム情報設定。
     * @return {@link CellConverter}のインスタンス
     * @throws ConversionException {@link CellConverter}が見つからない場合。
     */
    protected CellConverter<?> getCellConverter(final Class<?> componentType, final FieldAccessor accessor, final Configuration config)
            throws ConversionException {

        final CellConverter<?> converter;

        if(accessor.hasAnnotation(XlsConverter.class)) {
            XlsConverter converterAnno = accessor.getAnnotationNullable(XlsConverter.class);
            converter = config.createBean(converterAnno.value()).create(accessor, config);

        } else {
            CellConverterFactory<?> converterFactory = config.getConverterRegistry().getConverterFactory(componentType);
            if(converterFactory == null) {
                throw newNotFoundCellConverterExpcetion(componentType);
            }
            converter = converterFactory.create(accessor, config);
        }

        return converter;
    }

}
