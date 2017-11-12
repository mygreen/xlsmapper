package com.gh.mygreen.xlsmapper.cellconverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * ExcelのCellの値とJavaオブジェクトを相互に変換するインタフェース。
 *
 * @version 2.0
 * @author T.TSUCHIE
 * @param <T> 変換対象のJavaのオブジェクトのタイプ
 */
public interface CellConverter<T> {
    
    /**
     * シート読み込み時のExcel Cell {@literal =>} Javaオブジェクトに変換する。
     * @param cell 読み込み対象のセル
     * @return 変換したJavaオブジェクト
     * @throws XlsMapperException 変換に失敗した場合
     */
    T toObject(Cell cell) throws XlsMapperException;
    
    /**
     * シート書き込み時のJavaオブジェクト {@literal =>} Excel Cellに変換する。
     * @param targetValue 書き込み対象のオブジェクト。
     * @param targetBean 書き込み対象のフィールドが設定されているJavaBeanオブジェクト。
     * @param sheet 書き込み先のシート
     * @param address 書き込み先のセルのアドレス
     * @return 書き込んだセル
     * @throws XlsMapperException 変換に失敗した場合
     */
    Cell toCell(T targetValue, Object targetBean, Sheet sheet, CellPosition address)
            throws XlsMapperException;
    
}
