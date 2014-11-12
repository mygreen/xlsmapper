package com.gh.mygreen.xlsmapper.annotation;


/**
 * 書き込み時にデータのレコード数に対してシートのレコードが足りない場合の操作を指定します。
 * 
 * @author T.TSUCHIE
 *
 */
public enum OverRecordOperate {
    
    /** 前のセルをコピーします */
    Copy,
    
    /** 次のセルの前に行または列を挿入します */
    Insert,
    
    /** レコードの書き込みを中断します*/
    Break,
    ;
}
