package com.gh.mygreen.xlsmapper;

import java.lang.reflect.Constructor;

import com.gh.mygreen.xlsmapper.cellconvert.CellConverterRegistry;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessorRegstry;


/**
 * マッピングする際の設定などを保持するクラス。
 * 
 * @version 0.4
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
    
    /** 書き込み時に名前の定義範囲を修正するかどうか */
    private boolean correctNameRangeOnSave = false;
    
    /** 書き込み時に名前のセルの入力規則を修正するかどうか */
    private boolean correctCellDataValidationOnSave = false;
    
    /** 書き込み時にセルのコメントを修正するかどうか */
    private boolean correctCellCommentOnSave = false;
    
    /** POIのセルの値のフォーマッター */
    private CellFormatter cellFormatter = new DefaultCellFormatter();
    
    private FieldProcessorRegstry fieldProcessorRegistry = new FieldProcessorRegstry();
    
    private CellConverterRegistry converterRegistry = new CellConverterRegistry();
    
    /** 読み込み時のBeanのインスタンスの作成クラス */
    private FactoryCallback<Class<?>, Object> beanFactory = new FactoryCallback<Class<?>, Object>() {
        
        @Override
        public Object create(final Class<?> clazz) {
            try {
                Constructor<?> cons = clazz.getDeclaredConstructor();
                cons.setAccessible(true);
                return cons.newInstance();
            } catch (ReflectiveOperationException  e) {
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
     * シートが見つからなくても無視するかどうか。
     * @return　初期値は、'false'です。
     */
    public boolean isIgnoreSheetNotFound() {
        return ignoreSheetNotFound;
    }
    
    /**
     * シートが見つからなくても無視するかどうか設定します。
     * @param ignoreSheetNotFound 初期値は、'false'です。
     * @return
     */
    public XlsMapperConfig setIgnoreSheetNotFound(boolean ignoreSheetNotFound) {
        this.ignoreSheetNotFound = ignoreSheetNotFound;
        return this;
    }
    
    /**
     * 型変換エラーが発生しても処理を続けるかどうか。
     * @return 初期値は、'false'です。
     */
    public boolean isSkipTypeBindFailure() {
        return skipTypeBindFailure;
    }
    
    /**
     * 型変換エラーが発生しても処理を続けるかどうか設定します。
     * @param skipTypeBindFailure 初期値は、'false'です。
     * @return
     */
    public XlsMapperConfig setSkipTypeBindFailure(boolean skipTypeBindFailure) {
        this.skipTypeBindFailure = skipTypeBindFailure;
        return this;
    }
    
    /**
     * 保存時にセルの結合を行うかどうか
     * @return 初期値は、'false'です。
     */
    public boolean isMergeCellOnSave() {
        return mergeCellOnSave;
    }
    
    /**
     * 保存時にセルの結合を行うかどうか設定します。
     * @param mergeCellOnSave 初期値は、'false'です。
     * @return
     */
    public XlsMapperConfig setMergeCellOnSave(boolean mergeCellOnSave) {
        this.mergeCellOnSave = mergeCellOnSave;
        return this;
    }
    
    /**
     * 書き込み時に名前の定義範囲を修正するかどうか
     * @since 0.3
     * @return 初期値は、'false'です。
     */
    public boolean isCorrectNameRangeOnSave() {
        return correctNameRangeOnSave;
    }
    
    /**
     * 書き込み時に名前の定義範囲を修正するかどうか設定します。
     * @since 0.3
     * @param correctNameRangeOnSave 初期値は、'false'です。
     * @return
     */
    public XlsMapperConfig setCorrectNameRangeOnSave(boolean correctNameRangeOnSave) {
        this.correctNameRangeOnSave = correctNameRangeOnSave;
        return this;
    }
    
    /**
     * 書き込み時に名前のセルの入力規則を修正するかどうか。
     * @since 0.3
     * @return 初期値は、'false'です。
     */
    public boolean isCorrectCellDataValidationOnSave() {
        return correctCellDataValidationOnSave;
    }
    
    /**
     * 書き込み時に名前のセルの入力規則を修正するかどうか設定します。
     * <p>trueの場合（修正する場合）、POI-3.11以上が必要になります。
     * <p>POI-3.10以前の場合、データの修正は行われません。
     * @param correctCellDataValidationOnSave 初期値は、'false'です。
     */
    public void setCorrectCellDataValidationOnSave(boolean correctCellDataValidationOnSave) {
        this.correctCellDataValidationOnSave = correctCellDataValidationOnSave;
    }
    
    /**
     * 書き込み時にセルノコメントを修正するかどうか設定します。
     * <p>POI-3.10以上の場合、コメント付きのシートに対して行を追加すると、ファイルが壊れるため、それらを補正します。
     * <p>アノテーションXlsHorizontalRecordsで行の追加などを行うときに補正します。
     * <p>ただし、この機能を有効にするとシートのセルを全て走査するため処理時間がかかります。
     * @return 初期値は、'false'です。
     */
    public boolean isCorrectCellCommentOnSave() {
        return correctCellCommentOnSave;
    }
    
    /**
     * 書き込み時にセルノコメントを修正するかどうか設定します。
     * <p>'true'の場合、修正します。
     * <p>POI-3.10以上の場合、コメント付きのシートに対して行を追加すると、ファイルが壊れるため、それらを補正します。
     * <p>アノテーションXlsHorizontalRecordsで行の追加などを行うときに補正します。
     * <p>ただし、この機能を有効にするとシートのセルを全て走査するため処理時間がかかります。
     * @param correctCellCommentOnSave
     */
    public void setCorrectCellCommentOnSave(boolean correctCellCommentOnSave) {
        this.correctCellCommentOnSave = correctCellCommentOnSave;
    }
    
    /**
     * POIのセルのフォーマッターを取得します。
     * @return
     */
    public CellFormatter getCellFormatter() {
        return cellFormatter;
    }
    
    /**
     * POIのセルのフォーマッターを指定します。
     * @param cellFormatter
     * @return
     */
    public XlsMapperConfig setCellFormatter(CellFormatter cellFormatter) {
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
