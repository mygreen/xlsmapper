package com.gh.mygreen.xlsmapper.fieldprocessor;

import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;


/**
 * マッピング対象のCellが見つからない場合にスローする例外。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class CellNotFoundException extends XlsMapperException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -3913407241079675756L;
    
    /** 検索元のシート */
    private final String sheetName;
    
    /** 検索対象のセルのラベル */
    private final String label;
    
    /**
     * 指定したラベルを持つセルが見つからない場合
     * @param sheetName シート名
     * @param label ラベル名
     */
    public CellNotFoundException(final String sheetName, final String label) {
        super(MessageBuilder.create("cell.notNotFound.label")
                .var("sheetName", sheetName)
                .var("label", label)
                .format());
        this.sheetName = sheetName;
        this.label = label;
        
    }
    
    /**
     * シート名を取得する
     * @return シート名
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * ラベル名を取得する
     * @return ラベル名
     */
    public String getLabel() {
        return label;
    }
    
}
