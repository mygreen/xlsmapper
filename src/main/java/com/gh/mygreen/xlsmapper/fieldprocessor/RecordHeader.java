package com.gh.mygreen.xlsmapper.fieldprocessor;

/**
 * 見出し用セルの情報を保持するクラス。
 * 
 * @version 1.4
 *
 */
public class RecordHeader {
    
    /**
     * 見出しの値
     */
    private final String label;
    
    /**
     * 表の開始位置からの距離
     */
    private final int interval;
    
    public RecordHeader(final String label, final int interval) {
        this.label = label;
        this.interval = interval;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RecordHeader.class.getSimpleName())
            .append(" [")
            .append("label=").append(label)
            .append(", interval=").append(interval)
            .append(" ]");
        return sb.toString();
    }
    
    /**
     * 見出しセルの取得
     * @return
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * この見出しが定義されている位置が、表の開始位置から離れている距離。
     * @return
     */
    public int getInterval() {
        return interval;
    }
    
}
