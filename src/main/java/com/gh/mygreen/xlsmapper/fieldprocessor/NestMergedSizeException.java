package com.gh.mygreen.xlsmapper.fieldprocessor;

import com.gh.mygreen.xlsmapper.XlsMapperException;

/**
 * ネストしたレコードの場合、結合したセルのサイズが不正な場合にスローされる例外。
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
public class NestMergedSizeException extends XlsMapperException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1458115179311554147L;
    
    /** 処理対象のシート */
    private final String sheetName;
    
    /** 実際の結合サイズ */
    private final int mergedSize;
    
    public NestMergedSizeException(final String sheetName, final int mergedSize, final String message) {
        super(message);
        
        this.sheetName = sheetName;
        this.mergedSize = mergedSize;
        
    }
    
    /**
     * 対象のシート名
     * @return
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * 実際のセルの結合サイズ
     * @return
     */
    public int getMergedSize() {
        return mergedSize;
    }
    
}
