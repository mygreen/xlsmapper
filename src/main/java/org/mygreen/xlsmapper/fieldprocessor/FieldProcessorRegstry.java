package org.mygreen.xlsmapper.fieldprocessor;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.mygreen.xlsmapper.ArgUtils;
import org.mygreen.xlsmapper.FactoryCallback;
import org.mygreen.xlsmapper.annotation.XlsCell;
import org.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import org.mygreen.xlsmapper.annotation.XlsIterateTables;
import org.mygreen.xlsmapper.annotation.XlsLabelledCell;
import org.mygreen.xlsmapper.annotation.XlsSheetName;
import org.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import org.mygreen.xlsmapper.fieldprocessor.processor.CellProcessor;
import org.mygreen.xlsmapper.fieldprocessor.processor.HorizontalRecordsProcessor;
import org.mygreen.xlsmapper.fieldprocessor.processor.IterateTablesProcessor;
import org.mygreen.xlsmapper.fieldprocessor.processor.LabelledCellProcessor;
import org.mygreen.xlsmapper.fieldprocessor.processor.SheetNameProcessor;
import org.mygreen.xlsmapper.fieldprocessor.processor.VerticalRecordsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link FieldProcessor}を管理するクラス。
 * 
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class FieldProcessorRegstry {
    
    public static final String INIT_PROPERTY_NAME = "xlsbeans.properties";
    
    private static Logger logger = LoggerFactory.getLogger(FieldProcessorRegstry.class);
    
    private Map<Class<? extends Annotation>, LoadingFieldProcessor<?>> loadingPocessorMap;
    
    private Map<Class<? extends Annotation>, SavingFieldProcessor<?>> savingPocessorMap;
    
    /** {@link FieldProcessor}のインスタンスを作成する */
    private FactoryCallback<Class<FieldProcessor>, FieldProcessor> fieldProcessorFactory;
    
    public FieldProcessorRegstry() {
        this.loadingPocessorMap = new ConcurrentHashMap<>();
        
        init();
    }
    
    /**
     * 初期化する。
     * <p>アノテーションとプロセッサの初期化を行う。
     */
    protected void init() {
        
        if(fieldProcessorFactory == null) {
            this.fieldProcessorFactory = new FactoryCallback<Class<FieldProcessor>, FieldProcessor>() {
                @Override
                public FieldProcessor create(final Class<FieldProcessor> clazz) {
                    try {
                        return clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(String.format("failcreate FieldProcessor instance of '%s'", clazz.getName()), e);
                    }
                }
            };
        }
        
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
        
        //TODO:
        loadProcessorWithProperties();
        
    }
    
    @SuppressWarnings("rawtypes")
    public FieldProcessor createFieldProcessor(final Class<FieldProcessor> clazz) {
        return fieldProcessorFactory.create(clazz);
    }
    
    public void setCellConverterFactory(FactoryCallback<Class<FieldProcessor>, FieldProcessor> fieldProcessorFactory) {
        this.fieldProcessorFactory = fieldProcessorFactory;
    }
    
    /**
     * プロパティファイルから、取得する。
     */
    protected void loadProcessorWithProperties() {
        try {
            final InputStream in = FieldProcessorRegstry.class.getResourceAsStream(INIT_PROPERTY_NAME);
            if(in != null){
                Properties props = new Properties();
                props.load(in);
                
                ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
                if (clsLoader == null) {
                    clsLoader = FieldProcessorRegstry.class.getClassLoader();
                }
                
                for(Map.Entry<Object, Object> entry : props.entrySet()){
                    try {
                        Class<? extends Annotation> annoClazz = 
                                clsLoader.loadClass((String)entry.getKey()).asSubclass(Annotation.class);                 
                        
                        Class<? extends FieldProcessor> procClazz = 
                                clsLoader.loadClass((String)entry.getValue()).asSubclass(FieldProcessor.class);
                        
                        registerProcessor(annoClazz, createFieldProcessor((Class<FieldProcessor>) procClazz));
                    } catch(Exception e){
                        logger.warn("fail load FieldProcessor" ,e);
                    }
                }
            }
        } catch(Exception e){
            logger.warn(String.format("fail load property file '%s'", INIT_PROPERTY_NAME), e);
        }
    }
    
    /**
     * アノテーションに対する{@link LoadingFieldProcessor}を取得する。
     * @param anno
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> LoadingFieldProcessor<A> getLoadingProcessor(final Annotation anno) {
        ArgUtils.notNull(anno, "anno");
        
        return (LoadingFieldProcessor<A>) loadingPocessorMap.get(anno.annotationType());
    }
    
    /**
     * アノテーションに対する{@link SavingFieldProcessor}を取得する。
     * @param anno
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> SavingFieldProcessor<A> getSavingProcessor(final Annotation anno) {
        ArgUtils.notNull(anno, "anno");
        
        return (SavingFieldProcessor<A>) savingPocessorMap.get(anno.annotationType());
    }
    
    /**
     * アノテーションに対する{@link FieldProcessor}を登録する。
     * @param anno
     * @param processor
     */
    public <A extends Annotation> void registerProcessor(final Class<A> anno, final FieldProcessor<A> processor) {
        ArgUtils.notNull(anno, "anno");
        ArgUtils.notNull(processor, "processor");
        
        if(processor instanceof LoadingFieldProcessor) {
            loadingPocessorMap.put(anno, (LoadingFieldProcessor) processor);
        }
        
        if(processor instanceof SavingFieldProcessor) {
            savingPocessorMap.put(anno, (SavingFieldProcessor) processor);
        }
    }
    
    public Map<Class<? extends Annotation>, LoadingFieldProcessor<?>> getLoadingProcessorMap() {
        return loadingPocessorMap;
    }
    
    public Map<Class<? extends Annotation>, SavingFieldProcessor<?>> getSavingProcessorMap() {
        return savingPocessorMap;
    }
    
    public void setProcessorMap(Map<Class<? extends Annotation>, FieldProcessor<?>> processorMap) {
        for(Map.Entry<Class<? extends Annotation>, FieldProcessor<?>> entry : processorMap.entrySet()) {
            if(entry.getValue() instanceof LoadingFieldProcessor) {
                loadingPocessorMap.put(entry.getKey(), (LoadingFieldProcessor) entry.getValue());
            }
            
            if(entry.getValue() instanceof SavingFieldProcessor) {
                savingPocessorMap.put(entry.getKey(), (SavingFieldProcessor) entry.getValue());
            }
        }
    }
    
}
