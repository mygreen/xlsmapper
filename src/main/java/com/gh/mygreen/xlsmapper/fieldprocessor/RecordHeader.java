package com.gh.mygreen.xlsmapper.fieldprocessor;

/**
 * 見出し用セルの情報を保持するクラス。
 * 
 *
 */
public class RecordHeader {
    
    private final String headerLabel;
    
    private final int headerRange;
    
    public RecordHeader(final String headerLabel, final int headerRange) {
        this.headerLabel = headerLabel;
        this.headerRange = headerRange;
    }
    
    public String getHeaderLabel() {
        return headerLabel;
    }
    
    public int getHeaderRange() {
        return headerRange;
    }
}
