package com.gh.mygreen.xlsmapper.fieldprocessor;

import org.apache.poi.ss.util.CellRangeAddress;

/**
 * ネストしたレコードを処理するために、親のレコードの結合情報を保持しておくためのクラス。
 * 
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
public class MergedRecord {
    
    /**
     * 該当する見出し情報
     */
    private final RecordHeader headerRecord;
    
    /**
     * 結合された領域の情報
     */
    private final CellRangeAddress mergedRange;
    
    /**
     * 結合されたサイズ
     */
    private final int mergedSize;
    
    public MergedRecord(final RecordHeader headerRecord, final CellRangeAddress mergedRange, int mergedSize) {
        this.headerRecord = headerRecord;
        this.mergedRange = mergedRange;
        this.mergedSize = mergedSize;
    }
    
    /**
     * 該当する見出しの情報を取得する。
     * @return
     */
    public RecordHeader getHeaderRecord() {
        return headerRecord;
    }
    
    /**
     * 結合情報を取得する。
     * @return
     */
    public CellRangeAddress getMergedRange() {
        return mergedRange;
    }
    
    /**
     * 結合された行の幅を取得する。
     * @return
     */
    public int getRowSpan() {
        return mergedRange.getLastRow() - mergedRange.getFirstRow() + 1;
    }
    
    /**
     * 結合された列の幅を取得する。
     * @return
     */
    public int getColSpan() {
        return mergedRange.getLastColumn() - mergedRange.getFirstColumn() + 1;
    }
    
    /**
     * 結合されたサイズを取得する。
     * <p>Horizontalの場合は縦幅、Verticalの場合は横幅が設定される。
     * @return
     */
    public int getMergedSize() {
        return mergedSize;
    }
    
}
