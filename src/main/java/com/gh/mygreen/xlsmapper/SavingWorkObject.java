package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.converter.TypeBindException;
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
    
    public void addTypeBindError(final TypeBindException e, final Point position, final String fieldName, final String label) {
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(),
                position, label);
    }
    
    public void addTypeBindError(final TypeBindException e, final Cell cell, final String fieldName, final String label) {
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(),
                new Point(cell.getColumnIndex(), cell.getRowIndex()), label);
    }
}
