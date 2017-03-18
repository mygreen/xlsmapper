package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.util.CellAddress;


/**
 * {@link FieldError}のインスタンスを組み立てるクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class FieldErrorBuilder {
    
    private String objectName;
    
    private String fieldPath;
    
    private Object fieldValue;
    
    private Class<?> fieldType;
    
    private boolean typeBindFailure;
    
    private String[] codes;
    
    private Object[] args;
    
    private Map<String, ?> vars;
    
    private String sheetName;
    
    private CellAddress cellAddress;
    
    private String label;
    
    private String defaultMessage;
    
    private FieldErrorBuilder() {
        
    }
    
    /**
     * {@link FieldErrorBuilder}のインスタンスを作成する
     * @return
     */
    public static FieldErrorBuilder create() {
        return new FieldErrorBuilder();
    }
    
    /**
     * {@link FieldError}のインスタンスを組み立てる
     * @return
     */
    public FieldError build() {
        
        final FieldError error;
        if(cellAddress == null) {
            if(args != null) {
                error = new FieldError(objectName, fieldPath,
                        fieldType, fieldValue,
                        typeBindFailure,
                        codes, args);
            } else {
                error = new FieldError(objectName, fieldPath,
                        fieldType, fieldValue,
                        typeBindFailure,
                        codes, vars);
            }
        } else {
            if(args != null) {
                error = new CellFieldError(objectName, fieldPath,
                        fieldType, fieldValue,
                        typeBindFailure,
                        codes, args,
                        sheetName, cellAddress);
            } else {
                error = new CellFieldError(objectName, fieldPath,
                        fieldType, fieldValue,
                        typeBindFailure,
                        codes, vars,
                        sheetName, cellAddress);
            }
        }
        
        error.setLabel(label);
        error.setDefaultMessage(defaultMessage);
        
        return error;
    }
    
    public FieldErrorBuilder objectName(final String objectName) {
        this.objectName = objectName;
        return this;
    }
    
    public FieldErrorBuilder fieldPath(final String fieldPath) {
        this.fieldPath = fieldPath;
        return this;
    }
    
    public FieldErrorBuilder fieldPath(final String parentPath, final String childPath) {
        this.fieldPath = parentPath + SheetBindingErrors.PATH_SEPARATOR + childPath;
        return this;
    }
    
    public FieldErrorBuilder fieldType(final Class<?> fieldType) {
        this.fieldType = fieldType;
        return this;
    }
    
    public FieldErrorBuilder fieldValue(final Object fieldValue) {
        this.fieldValue = fieldValue;
        return this;
    }
    
    public FieldErrorBuilder typeBindFailure(final boolean typeBindFailure) {
        this.typeBindFailure = typeBindFailure;
        return this;
    }
    
    public FieldErrorBuilder codes(String[] codes) {
        this.codes = codes;
        return this;
    }
    
    public FieldErrorBuilder codes(String code) {
        this.codes = new String[]{code};
        return this;
    }
    
    public FieldErrorBuilder codes(String[] codes, Object[] args) {
        this.codes = codes;
        this.args = args;
        return this;
    }
    
    public FieldErrorBuilder codes(String[] codes, Map<String, ?> vars) {
        this.codes = codes;
        this.vars = vars;
        return this;
    }
    
    public FieldErrorBuilder sheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    
    public FieldErrorBuilder cellAddress(final Cell cell ) {
        this.cellAddress = CellAddress.of(cell);
        return this;
    }
    
    public FieldErrorBuilder cellAddress(final CellAddress address ) {
        this.cellAddress = address;
        return this;
    }
    
    public FieldErrorBuilder cellAddress(final int column, final int row) {
        this.cellAddress = CellAddress.of(row, column);
        return this;
    }
    
    public FieldErrorBuilder label(final String label) {
        this.label = label;
        return this;
    }
    
    public FieldErrorBuilder defaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
        return this;
    }
    
}
