package com.gh.mygreen.xlsmapper;

import com.gh.mygreen.xlsmapper.cellconvert.CellConverterRegistry;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessorRegstry;


/**
 * マッピングする際の設定などを保持するクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class XlsMapperConfig {
    
    /** シートが見つからなくても無視するかどうか */
    private boolean ignoreSheetNotFound = false;
    
    /** 型変換エラーが発生しても処理を続けるかどうか */
    private boolean skipTypeBindFailure = false;
    
    /** 保存時にセルの結合を行うかどうか */
    private boolean mergeCellOnSave = false;
    
    /** POIのセルの値のフォーマッター */
    private POICellFormatter cellFormatter = new POICellFormatter();
    
    private FieldProcessorRegstry fieldProcessorRegistry = new FieldProcessorRegstry();
    
    private CellConverterRegistry converterRegistry = new CellConverterRegistry();
    
    /** 読み込み時のBeanのインスタンスの作成クラス */
    private FactoryCallback<Class<?>, Object> beanFactory = new FactoryCallback<Class<?>, Object>() {
        
        @Override
        public Object create(final Class<?> clazz) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(String.format("fail create Bean instance of '%s'", clazz.getName()), e);
            }
        }
        
    };
    
    public XlsMapperConfig() {
    }
    
    /**
     * 指定したクラスタイプのインスタンスを作成する
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <P> P createBean(final Class<P> clazz) {
        return (P) beanFactory.create(clazz);
    }
    
    /**
     * シートが見つからなくても無視するかどうか
     * @return
     */
    public boolean isIgnoreSheetNotFound() {
        return ignoreSheetNotFound;
    }
    
    /**
     * シートが見つからなくても無視するかどうか設定します。
     * @param ignoreSheetNotFound
     * @return
     */
    public XlsMapperConfig setIgnoreSheetNotFound(boolean ignoreSheetNotFound) {
        this.ignoreSheetNotFound = ignoreSheetNotFound;
        return this;
    }
    
    /**
     * 型変換エラーが発生しても処理を続けるかどうか
     * @return
     */
    public boolean isSkipTypeBindFailure() {
        return skipTypeBindFailure;
    }
    
    /**
     * 型変換エラーが発生しても処理を続けるかどうか設定します。
     * @param skipTypeBindFailure
     * @return
     */
    public XlsMapperConfig setSkipTypeBindFailure(boolean skipTypeBindFailure) {
        this.skipTypeBindFailure = skipTypeBindFailure;
        return this;
    }
    
    /**
     * 保存時にセルの結合を行うかどうか
     * @return
     */
    public boolean isMergeCellOnSave() {
        return mergeCellOnSave;
    }
    
    /**
     * 保存時にセルの結合を行うかどうか設定します。
     * @param mergeCellOnSave
     * @return
     */
    public XlsMapperConfig setMergeCellOnSave(boolean mergeCellOnSave) {
        this.mergeCellOnSave = mergeCellOnSave;
        return this;
    }
    
    /**
     * POIのセルのフォーマッターを取得します。
     * @return
     */
    public POICellFormatter getCellFormatter() {
        return cellFormatter;
    }
    
    /**
     * POIのセルのフォーマッターを指定します。
     * @param cellFormatter
     * @return
     */
    public XlsMapperConfig setCellFormatter(POICellFormatter cellFormatter) {
        this.cellFormatter = cellFormatter;
        return this;
    }
    
    /**
     * セルの値の型変換の管理クラスを取得します。
     * @return
     */
    public CellConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }
    
    /**
     * セルの値の型変換の管理クラスを設定します。
     * @param converterRegistry
     * @return
     */
    public XlsMapperConfig setConverterRegistry(CellConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
        return this;
    }
    
    /**
     * アノテーションを処理するプロセッサの管理クラスを取得します。
     * @return
     */
    public FieldProcessorRegstry getFieldProcessorRegistry() {
        return fieldProcessorRegistry;
    }
    
    /**
     * アノテーションを処理するプロセッサの管理クラスを設定します。
     * @return
     */
    public XlsMapperConfig setFieldProcessorRegistry(FieldProcessorRegstry fieldProcessorRegistry) {
        this.fieldProcessorRegistry = fieldProcessorRegistry;
        return this;
    }
    
    /**
     * BeanのFactoryクラスを設定します。
     * @return
     */
    public XlsMapperConfig setBeanFactory(FactoryCallback<Class<?>, Object> beanFactory) {
        this.beanFactory = beanFactory;
        return this;
    }
    
}
