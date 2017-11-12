package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.lang.annotation.Annotation;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;


/**
 * アノテーションを処理するためのインタフェース。
 * 
 * @param <A> サポートするアノテーション
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public interface FieldProcessor<A extends Annotation> {
    
    /**
     * 読み込み時のアノテーションを処理する。
     * 
     * @param sheet Excelのシート
     * @param beansObj マッピング対象のBean。
     * @param anno 処理対象のアノテーション。
     * @param accessor マッピング対象のフィールド情報
     * @param config システム設定
     * @param work 一時オブジェクト
     * @throws XlsMapperException 読み込み時に失敗した場合
     */
    void loadProcess(Sheet sheet, Object beansObj, A anno, FieldAccessor accessor, Configuration config, LoadingWorkObject work)
            throws XlsMapperException;
    
    /**
     * 書き込み時のアノテーションを処理する。
     * 
     * @param sheet Excelのシート
     * @param beansObj マッピング対象のBean。
     * @param anno 処理対象のアノテーション。
     * @param accessor マッピング対象のフィールド情報
     * @param config システム設定
     * @param work 一時オブジェクト
     * @throws XlsMapperException 書き込み時に失敗した場合
     */
    void saveProcess(Sheet sheet, Object beansObj, A anno, FieldAccessor accessor, Configuration config, SavingWorkObject work)
            throws XlsMapperException;
    
}
