package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

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
        final Map<String, Object> vars = new LinkedHashMap<>(e.getMessageVars());
        vars.put("validatedValue", e.getTargetValue());
        
        this.errors.rejectSheetTypeBind(fieldName, e.getTargetValue(), e.getBindClass(), vars,
                position, label);
    }
    
    public void addTypeBindError(final TypeBindException e, final Cell cell, final String fieldName, final String label) {
        addTypeBindError(e, new Point(cell.getColumnIndex(), cell.getRowIndex()), fieldName, label);
    }
}
