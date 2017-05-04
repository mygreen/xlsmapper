package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;


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
     * @param beansObj マッピング対象のBean。
     * @param anno 処理対象のアノテーション。
     * @param accessor マッピング対象のフィールド情報
     * @param config システム設定
     * @param work 一時オブジェクト
     * @throws XlsMapperException 
     */
    void saveProcess(Sheet sheet, Object beansObj, A anno, FieldAccessor accessor, Configuration config, SavingWorkObject work) throws XlsMapperException;
    
}
