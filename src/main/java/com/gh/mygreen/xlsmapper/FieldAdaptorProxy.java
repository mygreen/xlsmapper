package com.gh.mygreen.xlsmapper;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.LoadingFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.SavingFieldProcessor;


/**
 * フィールドに対するアノテーション、プロセッサーなどを保持するクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class FieldAdaptorProxy {
    
    private final Annotation annotation;
    
    private final FieldProcessor processor;
    
    private final FieldAdaptor adaptor;
    
    public FieldAdaptorProxy(final Annotation annotation, FieldProcessor processor, FieldAdaptor adaptor) {
        this.annotation = annotation;
        this.processor = processor;
        this.adaptor = adaptor;
    }
    
    public boolean equals(final Object obj) {
        if(obj instanceof FieldAdaptor) {
            return ((FieldAdaptor)obj).equals(obj);
        }
        return super.equals(obj);
    }
    
    public Annotation getAnnotation() {
        return annotation;
    }
    
    public FieldProcessor getProcessor() {
        return processor;
    }
    
    public LoadingFieldProcessor getProcessorAsLoading() {
        return (LoadingFieldProcessor) processor;
    }
    
    public SavingFieldProcessor getProcessorAsSaving() {
        return (SavingFieldProcessor) processor;
    }
    
    public FieldAdaptor getAdaptor() {
        return adaptor;
    }
    
    public void loadProcess(final Sheet sheet, final Object beanObj, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        getProcessorAsLoading().loadProcess(sheet, beanObj, getAnnotation(), adaptor, config, work);
    }
    
    public void saveProcess(final Sheet sheet, final Object beanObj, final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        getProcessorAsSaving().saveProcess(sheet, beanObj, getAnnotation(), adaptor, config, work);
    }
}
