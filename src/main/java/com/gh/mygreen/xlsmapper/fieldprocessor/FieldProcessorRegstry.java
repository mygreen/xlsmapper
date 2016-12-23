package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.CellProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.LabelledCellProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.SheetNameProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.VerticalRecordsProcessor;


/**
 * {@link FieldProcessor}を管理するクラス。
 * 
 * @version 1.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class FieldProcessorRegstry {
    
    private Map<Class<? extends Annotation>, LoadingFieldProcessor<?>> loadingPocessorMap;
    
    private Map<Class<? extends Annotation>, SavingFieldProcessor<?>> savingPocessorMap;
    
    public FieldProcessorRegstry() {
        this.loadingPocessorMap = new ConcurrentHashMap<>();
        
        init();
    }
    
    /**
     * 初期化する。
     * <p>アノテーションとプロセッサの初期化を行う。
     */
    protected void init() {
        
        if(loadingPocessorMap == null) {
            this.loadingPocessorMap = new ConcurrentHashMap<Class<? extends Annotation>, LoadingFieldProcessor<?>>();
        } else {
            loadingPocessorMap.clear();
        }
        
        if(savingPocessorMap == null) {
            this.savingPocessorMap = new ConcurrentHashMap<Class<? extends Annotation>, SavingFieldProcessor<?>>();
        } else {
            savingPocessorMap.clear();
        }
        
        //標準のフィールドプロセッサを登録する。
        registerProcessor(XlsSheetName.class, new SheetNameProcessor());
        registerProcessor(XlsCell.class, new CellProcessor());
        registerProcessor(XlsLabelledCell.class, new LabelledCellProcessor());
        registerProcessor(XlsHorizontalRecords.class, new HorizontalRecordsProcessor());
        registerProcessor(XlsVerticalRecords.class, new VerticalRecordsProcessor());
        registerProcessor(XlsIterateTables.class, new IterateTablesProcessor());
        
    }
    
    /**
     * アノテーションに対する{@link LoadingFieldProcessor}を取得する。
     * @param anno 取得対象のアノテーションのインスタンス。
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> LoadingFieldProcessor<A> getLoadingProcessor(final Annotation anno) {
        ArgUtils.notNull(anno, "anno");
        
        return (LoadingFieldProcessor<A>) loadingPocessorMap.get(anno.annotationType());
    }
    
    /**
     * アノテーションに対する{@link LoadingFieldProcessor}を取得する。
     * @param annoClass  取得対象のアノテーションのクラスタイプ。
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> LoadingFieldProcessor<A> getLoadingProcessor(final Class<A> annoClass) {
        ArgUtils.notNull(annoClass, "annoClass");
        
        return (LoadingFieldProcessor<A>) loadingPocessorMap.get(annoClass);
    }
    
    /**
     * アノテーションに対する{@link SavingFieldProcessor}を取得する。
     * @param anno 取得対象のアノテーションのインスタンス。
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> SavingFieldProcessor<A> getSavingProcessor(final Annotation anno) {
        ArgUtils.notNull(anno, "anno");
        
        return (SavingFieldProcessor<A>) getSavingProcessor(anno.annotationType());
    }
    
    /**
     * アノテーションに対する{@link SavingFieldProcessor}を取得する。
     * @param annoClass 取得対象のアノテーションのクラスタイプ。
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> SavingFieldProcessor<A> getSavingProcessor(final Class<A> annoClass) {
        ArgUtils.notNull(annoClass, "annoClass");
        
        return (SavingFieldProcessor<A>) savingPocessorMap.get(annoClass);
    }
    
    /**
     * アノテーションに対する{@link FieldProcessor}を登録する。
     * @param annoClass 登録対象のアノテーションのクラスタイプ。
     * @param processor フィールドプロセッサーのインスタンス。
     *                  {@link LoadingFieldProcessor}または{@link SavingFieldProcessor} を実装している必要がある。
     */
    public <A extends Annotation> void registerProcessor(final Class<A> annoClass, final FieldProcessor<A> processor) {
        ArgUtils.notNull(annoClass, "annoClass");
        ArgUtils.notNull(processor, "processor");
        
        if(processor instanceof LoadingFieldProcessor) {
            loadingPocessorMap.put(annoClass, (LoadingFieldProcessor) processor);
        }
        
        if(processor instanceof SavingFieldProcessor) {
            savingPocessorMap.put(annoClass, (SavingFieldProcessor) processor);
        }
    }
    
}
