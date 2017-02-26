package com.gh.mygreen.xlsmapper.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;


/**
 * Cellの値を変換するインタフェース。
 *
 * @version 1.5
 * @author T.TSUCHIE
 * @param <T> 変換対象のJavaのオブジェクト
 */
public interface CellConverter<T> {
    
    /**
     * シート読み込み時のExcel Cell {@literal =>} Javaオブジェクトに変換する。
     * @param cell 読み込み対象のセル
     * @param adapter マッピング対象のフィールド情報。
     * @param config 設定情報
     * @return 変換したJavaオブジェクト
     * @throws XlsMapperException
     */
    T toObject(Cell cell, FieldAdapter adapter, XlsMapperConfig config) throws XlsMapperException;
    
    
    /**
     * シート書き込み時のJavaオブジェクト {@literal =>} Excel Cellに変換する。
     * @param adapter  マッピング対象のフィールド情報。
     * @param targetValue 書き込み対象のオブジェクト。
     * @param targetBean 書き込み対象のフィールドが設定されているJavaBeanオブジェクト。
     * @param sheet 書き込み先のシート
     * @param column 書き込み先のセルの列番号。0から始まる。
     * @param row 書き込み先のセルの行番号。0から始まる。
     * @param config 設定情報
     * @return 書き込んだセル
     * @throws XlsMapperException 
     */
    Cell toCell(FieldAdapter adapter, T targetValue, Object targetBean, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
    
}
