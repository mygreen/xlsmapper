package com.gh.mygreen.xlsmapper.fieldprocessor;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordFinder;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * レコードの開始位置を検索するためのインタフェース。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public interface RecordFinder {
    
    /**
     * データレコードの開始位置を検索します。
     * 
     * @param type 実行時の種別。読み込み時か、書き込み時の判定に使用する。
     * @param args アノテーションで指定した属性{@link XlsRecordFinder#args()}の値
     * @param sheet シート情報
     * @param address 現在のデータレコードの開始位置
     * @param config システム設定
     * @return データレコードの開始位置を返します。
     * @throws CellNotFoundException 開始位置が見つからない場合
     */
    CellPosition find(ProcessType type, String[] args, Sheet sheet, CellPosition address, Configuration config);
}
