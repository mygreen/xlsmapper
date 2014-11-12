package org.mygreen.xlsmapper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.mygreen.xlsmapper.cellconvert.TypeBindException;
import org.mygreen.xlsmapper.validation.SheetBindingErrors;
import org.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * 読み込み処理中で持ち回すオブジェクトを保持するクラス。
 * 
 * @author T.TSUCHIE
 */
public class LoadingWorkObject {
    
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
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(), e.getMessageVars(),
                position, label);
    }
    
    public void addTypeBindError(final TypeBindException e, final Cell cell, final String fieldName, final String label) {
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(), e.getMessageVars(),
                new Point(cell.getColumnIndex(), cell.getRowIndex()), label);
    }
}