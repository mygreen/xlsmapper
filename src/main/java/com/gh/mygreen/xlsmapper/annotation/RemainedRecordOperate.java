package com.gh.mygreen.xlsmapper.annotation;


/**
 * アノテーション {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で、
 * 書き込み時にデータのレコード数に対してシートのレコードが余っている際の操作を指定します。
 * 
 * @author T.TSUCHIE
 *
 */
public enum RemainedRecordOperate {
    
    /** セルの値をクリアします */
    Clear,
    
    /** 行または列を削除します */
    Delete,
    
    /** 何もしません */
    None,
    ;
}
