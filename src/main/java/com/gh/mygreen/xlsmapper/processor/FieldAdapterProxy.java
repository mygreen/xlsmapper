package com.gh.mygreen.xlsmapper.processor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * {@link FieldProcessor}と{@link FieldAdapter}の組み合わせを保持します。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAdapterProxy {
    
    private final Annotation annotation;
    
    private final FieldProcessor<?> processor;
    
    private final FieldAdapter field;
    
    /**
     * コンストラクタ
     * @param annotation アノテーション
     * @param processor {@link FieldProcessor}
     * @param field フィールド
     * @throws NullPointerException {@literal annotation == null or processor == null or field == null}
     */
    public FieldAdapterProxy(final Annotation annotation, final FieldProcessor<?> processor, final FieldAdapter field) {
        ArgUtils.notNull(annotation, "annotation");
        ArgUtils.notNull(processor, "processor");
        ArgUtils.notNull(field, "field");
        
        this.annotation = annotation;
        this.processor = processor;
        this.field = field;
    }
    
    /**
     * フィールドとメソッドに同じアノテーションが付与されているときに重複を除外するための判定に使用する。
     * そのため、アノテーションのクラスタイプと{@link FieldAdapter#getNameWithClass()}が等しいかで判定します。
     * 
     */
    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof FieldAdapterProxy)) {
            return false;
        }
        
        final FieldAdapterProxy target = (FieldAdapterProxy)obj;
        
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
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void loadProcess(final Sheet sheet, final Object beanObj, final XlsMapperConfig config, final LoadingWorkObject work)
            throws XlsMapperException {
        
        LoadingFieldProcessor p = (LoadingFieldProcessor) processor;
        p.loadProcess(sheet, beanObj, getAnnotation(), field, config, work);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void saveProcess(final Sheet sheet, final Object beanObj, final XlsMapperConfig config, final SavingWorkObject work) 
            throws XlsMapperException {
        
        SavingFieldProcessor p = (SavingFieldProcessor) processor;
        p.saveProcess(sheet, beanObj, getAnnotation(), field, config, work);
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
    public FieldAdapter getField() {
        return field;
    }
    
}
