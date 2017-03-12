package com.gh.mygreen.xlsmapper.processor;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.processor.impl.CellProcessor;
import com.gh.mygreen.xlsmapper.processor.impl.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.processor.impl.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.processor.impl.LabelledCellProcessor;
import com.gh.mygreen.xlsmapper.processor.impl.SheetNameProcessor;
import com.gh.mygreen.xlsmapper.processor.impl.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * {@link FieldProcessor}を管理するクラス。
 * 
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class FieldProcessorRegistry {
    
    private Map<Class<? extends Annotation>, LoadingFieldProcessor<?>> loadingPocessorMap = new ConcurrentHashMap<>();
    
    private Map<Class<? extends Annotation>, SavingFieldProcessor<?>> savingPocessorMap = new ConcurrentHashMap<>();
    
    public FieldProcessorRegistry() {
        this.loadingPocessorMap = new ConcurrentHashMap<>();
        
        init();
    }
    
    /**
     * {@link FieldProcessor}の登録状態を初期値に戻します。
     */
    public void init() {
        
        loadingPocessorMap.clear();
        savingPocessorMap.clear();
        
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
     * @param annoClass  取得対象のアノテーションのクラスタイプ。
     * @return 見つからない場合はnullを返す。
     * @throws NullPointerException {@literal annoClass}
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> LoadingFieldProcessor<A> getLoadingProcessor(final Class<A> annoClass) {
        ArgUtils.notNull(annoClass, "annoClass");
        
        return (LoadingFieldProcessor<A>) loadingPocessorMap.get(annoClass);
    }
    
    /**
     * アノテーションに対する{@link SavingFieldProcessor}を取得する。
     * @param annoClass 取得対象のアノテーションのクラスタイプ。
     * @return 見つからない場合はnullを返す。
     * @throws NullPointerException {@literal annoClass}
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
     * @throws NullPointerException {@literal annoClass == null or processor == null.}
     */
    @SuppressWarnings("rawtypes")
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
