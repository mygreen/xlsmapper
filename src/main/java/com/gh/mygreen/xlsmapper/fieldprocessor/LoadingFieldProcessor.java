package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;


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
     * @param beansObj マッピング対象のBean。
     * @param anno 処理対象のアノテーション。
     * @param adaptor マッピング対象のフィールド情報
     * @param config システム設定
     * @param work 一時オブジェクト
     * @throws XlsMapperException 
     */
    void loadProcess(Sheet sheet, Object beansObj, A anno, FieldAdaptor adaptor, XlsMapperConfig config, LoadingWorkObject work) throws XlsMapperException;
    
}
