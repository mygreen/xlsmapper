package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsComment;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledComment;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.ArrayCellsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.CellProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.CommentProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledArrayCellsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledCellProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledCommentProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.SheetNameProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * {@link FieldProcessor}を管理するクラス。
 * 
 * @version 2.1
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class FieldProcessorRegistry {
    
    private Map<Class<? extends Annotation>, FieldProcessor<?>> pocessorMap = new ConcurrentHashMap<>();
    
    public FieldProcessorRegistry() {
        this.pocessorMap = new ConcurrentHashMap<>();
        
        init();
    }
    
    /**
     * {@link FieldProcessor}の登録状態を初期値に戻します。
     */
    public void init() {
        
        pocessorMap.clear();
        
        //標準のフィールドプロセッサを登録する。
        registerProcessor(XlsSheetName.class, new SheetNameProcessor());
        registerProcessor(XlsCell.class, new CellProcessor());
        registerProcessor(XlsLabelledCell.class, new LabelledCellProcessor());
        registerProcessor(XlsHorizontalRecords.class, new HorizontalRecordsProcessor());
        registerProcessor(XlsVerticalRecords.class, new VerticalRecordsProcessor());
        registerProcessor(XlsIterateTables.class, new IterateTablesProcessor());
        registerProcessor(XlsArrayCells.class, new ArrayCellsProcessor());
        registerProcessor(XlsLabelledArrayCells.class, new LabelledArrayCellsProcessor());
        registerProcessor(XlsComment.class, new CommentProcessor());
        registerProcessor(XlsLabelledComment.class, new LabelledCommentProcessor());
        
    }
    
    /**
     * アノテーションに対する{@link FieldProcessor}を取得する。
     * @param annoClass  取得対象のアノテーションのクラスタイプ。
     * @return 見つからない場合はnullを返す。
     * @throws NullPointerException {@literal annoClass}
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> FieldProcessor<A> getProcessor(final Class<A> annoClass) {
        ArgUtils.notNull(annoClass, "annoClass");
        
        return (FieldProcessor<A>) pocessorMap.get(annoClass);
    }
    
    /**
     * アノテーションに対する{@link FieldProcessor}を登録する。
     * @param annoClass 登録対象のアノテーションのクラスタイプ。
     * @param processor フィールドプロセッサーのインスタンス。{@link FieldProcessor}を実装している必要がある。
     * @throws NullPointerException {@literal annoClass == null or processor == null.}
     */
    public <A extends Annotation> void registerProcessor(final Class<A> annoClass, final FieldProcessor<A> processor) {
        ArgUtils.notNull(annoClass, "annoClass");
        ArgUtils.notNull(processor, "processor");
        
        pocessorMap.put(annoClass, processor);
        
    }
    
}
