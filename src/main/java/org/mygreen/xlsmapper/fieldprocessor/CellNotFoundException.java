package org.mygreen.xlsmapper.fieldprocessor;

import org.mygreen.xlsmapper.XlsMapperException;


/**
 * マッピング対象のExcelのCellが見つからない場合にスローする例外。
 * 
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
    
    public CellNotFoundException(final String sheetName, final String label) {
        super(String.format("Cell '%s' not found in sheet '%s'", label, sheetName));
        this.sheetName = sheetName;
        this.label = label;
        
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public String getLabel() {
        return label;
    }
    
}
