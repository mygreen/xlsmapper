package com.gh.mygreen.xlsmapper.fieldaccessor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessor;
import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * {@link FieldProcessor}と{@link FieldAccessor}の組み合わせを保持します。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAccessorProxy {
    
    private final Annotation annotation;
    
    private final FieldProcessor<?> processor;
    
    private final FieldAccessor field;
    
    /**
     * コンストラクタ
     * @param annotation アノテーション
     * @param processor {@link FieldProcessor}
     * @param field フィールド
     * @throws IllegalArgumentException {@literal annotation == null or processor == null or field == null}
     */
    public FieldAccessorProxy(final Annotation annotation, final FieldProcessor<?> processor, final FieldAccessor field) {
        ArgUtils.notNull(annotation, "annotation");
        ArgUtils.notNull(processor, "processor");
        ArgUtils.notNull(field, "field");
        
        this.annotation = annotation;
        this.processor = processor;
        this.field = field;
    }
    
    /**
     * フィールドとメソッドに同じアノテーションが付与されているときに重複を除外するための判定に使用する。
     * そのため、アノテーションのクラスタイプと{@link FieldAccessor#getNameWithClass()}が等しいかで判定します。
     * 
     */
    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof FieldAccessorProxy)) {
            return false;
        }
        
        final FieldAccessorProxy target = (FieldAccessorProxy)obj;
        
        // アノテー書の比較
        if(!annotation.annotationType().equals(target.annotation.annotationType())) {
            return false;
        }
        
        // クラス名#フィールドの比較
        if(!field.getNameWithClass().equals(target.getField().getNameWithClass())) {
            return false;
        }
        
        return true;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void loadProcess(final Sheet sheet, final Object beanObj, final Configuration config, final LoadingWorkObject work)
            throws XlsMapperException {
        
        ((FieldProcessor)processor).loadProcess(sheet, beanObj, getAnnotation(), field, config, work);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void saveProcess(final Sheet sheet, final Object beanObj, final Configuration config, final SavingWorkObject work) 
            throws XlsMapperException {
        
        ((FieldProcessor)processor).saveProcess(sheet, beanObj, getAnnotation(), field, config, work);
    }
    
    /**
     * 処理対象のアノテーションを取得します。
     * @return 処理対象のアノテーション
     */
    public Annotation getAnnotation() {
        return annotation;
    }
    
    /**
     * {@link FieldProcessor}を取得します。
     * @return {@link FieldProcessor}
     */
    public FieldProcessor<?> getProcessor() {
        return processor;
    }
    
    /**
     * フィールド情報を取得します。
     * @return フィールド情報
     */
    public FieldAccessor getField() {
        return field;
    }
    
}
