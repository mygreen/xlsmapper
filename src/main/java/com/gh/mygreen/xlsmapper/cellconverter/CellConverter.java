package com.gh.mygreen.xlsmapper.cellconverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.util.CellAddress;


/**
 * ExcelのCellの値とJavaオブジェクトを相互に変換するインタフェース。
 *
 * @version 2.0
 * @author T.TSUCHIE
 * @param <T> 変換対象のJavaのオブジェクト
 */
public interface CellConverter<T> {
    
    /**
     * シート読み込み時のExcel Cell {@literal =>} Javaオブジェクトに変換する。
     * @param cell 読み込み対象のセル
     * @param accessor マッピング対象のフィールド情報。
     * @param config 設定情報
     * @return 変換したJavaオブジェクト
     * @throws XlsMapperException 変換に失敗した場合
     */
    T toObject(Cell cell, FieldAccessor accessor, XlsMapperConfig config) throws XlsMapperException;
    
    /**
     * シート書き込み時のJavaオブジェクト {@literal =>} Excel Cellに変換する。
     * @param accessor  マッピング対象のフィールド情報。
     * @param targetValue 書き込み対象のオブジェクト。
     * @param targetBean 書き込み対象のフィールドが設定されているJavaBeanオブジェクト。
     * @param sheet 書き込み先のシート
     * @param address 書き込み先のセルのアドレス
     * @param config 設定情報
     * @return 書き込んだセル
     * @throws XlsMapperException 変換に失敗した場合
     */
    Cell toCell(FieldAccessor accessor, T targetValue, Object targetBean, Sheet sheet, CellAddress address, XlsMapperConfig config)
            throws XlsMapperException;
    
}
