package com.gh.mygreen.xlsmapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.util.CellAddress;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * 書き込み処理中で持ち回すオブジェクトを保持するクラス。
 *
 */
public class SavingWorkObject {
    
    private AnnotationReader annoReader;
    
    private final List<NeedProcess> needPostProcesses = new ArrayList<NeedProcess>();
    
    private SheetBindingErrors errors;
    
    public AnnotationReader getAnnoReader() {
        return annoReader;
    }
    
    public void setAnnoReader(AnnotationReader annoReader) {
        this.annoReader = annoReader;
    }
    
    public void addNeedPostProcess(NeedProcess needProcess) {
        this.needPostProcesses.add(needProcess);
    }
    
    public List<NeedProcess> getNeedPostProcesses() {
        return needPostProcesses;
    }
    
    public SheetBindingErrors getErrors() {
        return errors;
    }
    
    public void setErrors(SheetBindingErrors errors) {
        this.errors = errors;
    }
    
    public void addTypeBindError(final TypeBindException e, final CellAddress adddress, final String fieldName, final String label) {
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(),
                adddress, label);
    }
    
    public void addTypeBindError(final TypeBindException e, final Cell cell, final String fieldName, final String label) {
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(),
                CellAddress.of(cell), label);
    }
}
