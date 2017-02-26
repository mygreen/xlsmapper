package com.gh.mygreen.xlsmapper.processor;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.converter.CellConverter;
import com.gh.mygreen.xlsmapper.converter.CellConverterRegistry;
import com.gh.mygreen.xlsmapper.converter.ConversionException;


/**
 * 各種アノテーションを処理するためのクラスの抽象クラス。
 * <p>通常はこのクラスを継承して作成する。
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractFieldProcessor<A extends Annotation> implements LoadingFieldProcessor<A>, SavingFieldProcessor<A> {
    
    /**
     * 該当するタイプのConverterが見つからないときの例外のインスタンスを作成する。
     * @param targetType クラスタイプ
     * @return
     */
    protected ConversionException newNotFoundConverterExpcetion(final Class<?> targetType) {
        return new ConversionException(
                String.format("not found CellConverter for type '%s'.", targetType.getName()),
                targetType);
    }
    
    /**
     * 読み込み時用のConveterを取得する。
     * <p>アノテーション「{@link XlsConverter#converterClass()}」が設定されていた場合を考慮した、個別のConverterを考慮する。
     * 
     * @param adapter フィールド情報
     * @param converterResolver Converterを登録しているクラス。
     * @param config XlsMapperの設定クラス。Converterクラスのインスタンスを生成する際に利用する。
     * @return
     * @throws XlsMapperException Converterが見つからない場合。
     */
    protected CellConverter<?> getCellConverter(final FieldAdapter adapter, final CellConverterRegistry converterResolver,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsConverter> converterAnno = adapter.getAnnotation(XlsConverter.class);
        final CellConverter<?> converter;
        
        if(converterAnno.isPresent()) {
            converter = config.createBean(converterAnno.get().converter());
            
        } else {
            converter = converterResolver.getConverter(adapter.getType());
            if(converter == null) {
                throw newNotFoundConverterExpcetion(adapter.getType());
            }
        }
        
        return converter;
    }
    
}
