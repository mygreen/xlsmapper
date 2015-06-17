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
    
    @Override
    public String toString() {
        return "RecordHeader [headerLabel=" + headerLabel + ", headerRange=" + headerRange + "]";
    }
    
    /**
     * ヘッダーの見出しの取得
     * @return
     */
    public String getHeaderLabel() {
        return headerLabel;
    }
    
    /**
     * このヘッダーの見出しが定義されている位置が、表の開始位置から離れている距離。
     * @return
     */
    public int getHeaderRange() {
        return headerRange;
    }
    
}
