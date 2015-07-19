package com.gh.mygreen.xlsmapper.cellconvert;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * Cellの値を変換するインタフェース。
 *
 * @version 1.0
 * @author T.TSUCHIE
 * @param <T> 変換対象のJavaのオブジェクト
 */
public interface CellConverter<T> {
    
    /**
     * シート読み込み時のExcel Cell => Javaオブジェクトに変換する。
     * @param cell 読み込み対象のセル
     * @param adaptor マッピング対象のフィールド情報。
     * @param config 設定情報
     * @return 変換したJavaオブジェクト
     * @throws XlsMapperException
     */
    T toObject(Cell cell, FieldAdaptor adaptor, XlsMapperConfig config) throws XlsMapperException;
    
    
    /**
     * シート書き込み時のJavaオブジェクト => Excel Cellに変換する。
     * @param adaptor  マッピング対象のフィールド情報。
     * @param targetValue 書き込み対象のオブジェクト。
     * @param sheet 書き込み先のシート
     * @param column 書き込み先のセルの列番号。0から始まる。
     * @param row 書き込み先のセルの行番号。0から始まる。
     * @param config 設定情報
     * @return  書き込んだセル
     * @throws XlsMapperException 
     */
    Cell toCell(FieldAdaptor adaptor, T targetValue, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
    
}
