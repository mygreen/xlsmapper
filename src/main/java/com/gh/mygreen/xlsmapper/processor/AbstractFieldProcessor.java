package com.gh.mygreen.xlsmapper.processor;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.converter.CellConverter;
import com.gh.mygreen.xlsmapper.converter.ConversionException;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;


/**
 * 各種アノテーションを処理するためのクラスの抽象クラス。
 * <p>通常はこのクラスを継承して作成します。</p>
 * 
 * @param <A> サポートするアノテーション
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractFieldProcessor<A extends Annotation> implements LoadingFieldProcessor<A>, SavingFieldProcessor<A> {
    
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
     * @param adapter フィールド情報
     * @param config システム情報設定。
     * @return {@link CellConverter}のインスタンス
     * @throws ConversionException {@link CellConverter}が見つからない場合。
     */
    protected CellConverter<?> getCellConverter(final FieldAdapter adapter, final XlsMapperConfig config) throws ConversionException {
        
        final CellConverter<?> converter;
        
        if(adapter.hasAnnotation(XlsConverter.class)) {
            XlsConverter converterAnno = adapter.getAnnotation(XlsConverter.class).get();
            converter = config.createBean(converterAnno.converterClass());
            
        } else {
            converter = config.getConverterRegistry().getConverter(adapter.getType());
            if(converter == null) {
                throw newNotFoundCellConverterExpcetion(adapter.getType());
            }
        }
        
        return converter;
    }
    
}
