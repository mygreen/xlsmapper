package com.gh.mygreen.xlsmapper.validation;

import java.awt.Point;
import java.util.Map;

import com.gh.mygreen.xlsmapper.Utils;


/**
 * フィールドとセルのエラー情報を保持するクラス。
 *
 * @author T.TSUCHIE
 *
 */
public class CellFieldError extends FieldError {
    
    private final String sheetName;
    
    private final Point cellAddress;
    
    public CellFieldError(final String objectName, final String fieldPath,
            final String sheetName, final Point cellAddress) {
        super(objectName, fieldPath);
        this.sheetName = sheetName;
        this.cellAddress = cellAddress;
    }
    
    public CellFieldError(final String objectName, final String fieldPath,
            final String[] codes, final Object[] args,
            final String sheetName, final Point cellAddress) {
        super(objectName, fieldPath, codes, args);
        this.sheetName = sheetName;
        this.cellAddress = cellAddress;
    }
    
    public CellFieldError(final String objectName, final String fieldPath,
            final String[] codes, final Map<String, ?> vars,
            final String sheetName, final Point cellAddress) {
        super(objectName, fieldPath, codes, vars);
        this.sheetName = sheetName;
        this.cellAddress = cellAddress;
    }
    
    public CellFieldError(final String objectName, final String fieldPath,
            final Class<?> fieldType, final Object fieldValue,
            final boolean typeBindFailure,
            final String[] codes, final Object[] args,
            final String sheetName, final Point cellAddress) {
        super(objectName, fieldPath, fieldType, fieldValue, typeBindFailure, codes, args);
        this.sheetName = sheetName;
        this.cellAddress = cellAddress;
    }
    
    public CellFieldError(final String objectName, final String fieldPath,
            final Class<?> fieldType, final Object fieldValue,
            final boolean typeBindFailure,
            final String[] codes, final Map<String, ?> vars,
            final String sheetName, final Point cellAddress) {
        super(objectName, fieldPath, fieldType, fieldValue, typeBindFailure, codes, vars);
        this.sheetName = sheetName;
        this.cellAddress = cellAddress;
    }
    
    @Override
    public String toString() {
        
        final StringBuilder msg = new StringBuilder();
        
        msg.append(String.format("Fail conversion field '%s'.", getFieldPath()));
        
        if(isTypeBindFailure()) {
            msg.append(String.format("field value '%s' => type '%s'.",
                    Utils.convertToString(getFieldValue()), getFieldType().getName()));
        }
        
        msg.append(String.format(" Sheet '%s'", getSheetName()))
            .append(String.format(" Cell '%s' map to '%s'.", 
                    Utils.formatCellAddress(cellAddress), getObjectName()))
            .toString();
         return msg.toString();
    }
    
    public Point getCellAddress() {
        return cellAddress;
    }
    
    public String getFormattedCellAddress() {
        if(getCellAddress() == null) {
            return null;
        }
        
        return Utils.formatCellAddress(getCellAddress());
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    @Override
    public CellFieldError setDefaultMessage(String defaultMessage) {
        super.setDefaultMessage(defaultMessage);
        return this;
    }
    

}
