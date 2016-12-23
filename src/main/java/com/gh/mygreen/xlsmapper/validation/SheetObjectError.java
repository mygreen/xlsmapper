package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;


/**
 * シートのオブジェクトのエラーとしてのグローバルエラーを表現するエラーオブジェクト。
 * 
 * @author T.TSUCHIE
 *
 */
public class SheetObjectError extends ObjectError {
    
    /** シート名 */
    private final String sheetName;
    
    public SheetObjectError(final String objectName, final String sheetName) {
        super(objectName);
        this.sheetName = sheetName;
    }
    
    public SheetObjectError(final String objectName, final String[] codes, final Object[] args, final String sheetName) {
        super(objectName, codes, args);
        this.sheetName = sheetName;
    }
    
    public SheetObjectError(final String objectName, final String[] codes, final Map<String, ?> vars, final String sheetName) {
        super(objectName, codes, vars);
        this.sheetName = sheetName;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    @Override
    public SheetObjectError setDefaultMessage(String defaultMessage) {
        super.setDefaultMessage(defaultMessage);
        return this;
    }
}
