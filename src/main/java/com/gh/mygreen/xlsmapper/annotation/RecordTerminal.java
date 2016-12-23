package com.gh.mygreen.xlsmapper.annotation;

/**
 * アノテーション{@link XlsHorizontalRecords}や{@link XlsVerticalRecords}などで表を走査する際の基準となる、
 * 表の終端の種類を定義します。
 * 
 * @author Naoki Takezoe
 */
public enum RecordTerminal {
    
    /** 表の値が空の場合 */
    Empty,
    
    /** 終端のセルに罫線がある場合 */
    Border,;
}
