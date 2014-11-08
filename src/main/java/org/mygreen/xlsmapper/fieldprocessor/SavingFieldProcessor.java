package org.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.SavingWorkObject;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;


/**
 * アノテーションに対する書き込み処理のインタフェース。
 * @author T.TSUCHIE
 *
 */
public interface SavingFieldProcessor<A extends Annotation> extends FieldProcessor<A>{
    
    /**
     * 書き込み時のアノテーションを処理する。
     * 
     * @param sheet Excelのシート
     * @param obj マッピング対象のBean。
     * @param anno 処理対象のアノテーション。
     * @param adaptor マッピング対象のフィールド情報
     * @param config システム設定
     * @param work 一時オブジェクト
     * @throws XlsMapperException 
     */
    void saveProcess(Sheet sheet, Object obj, A anno, FieldAdaptor adaptor, XlsMapperConfig config, SavingWorkObject work) throws XlsMapperException;
    
}
