package org.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.LoadingWorkObject;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;


/**
 * 各種アノテーションに対して、読み込み処理のインタフェース。
 * @author T.TSUCHIE
 *
 */
public interface LoadingFieldProcessor<A extends Annotation> extends FieldProcessor<A> {
    
    /**
     * 読み込み時のアノテーションを処理する。
     * 
     * @param sheet Excelのシート
     * @param obj マッピング対象のBean。
     * @param anno 処理対象のアノテーション。
     * @param adaptor マッピング対象のフィールド情報
     * @param config システム設定
     * @param work 一時オブジェクト
     * @throws XlsMapperException 
     */
    void loadProcess(Sheet sheet, Object obj, A anno, FieldAdaptor adaptor, XlsMapperConfig config, LoadingWorkObject work) throws XlsMapperException;
    
}
