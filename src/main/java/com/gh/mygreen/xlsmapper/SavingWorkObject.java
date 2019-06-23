package com.gh.mygreen.xlsmapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * 書き込み処理中で持ち回すオブジェクトを保持するクラス。
 *
 */
public class SavingWorkObject {
    
    private AnnotationReader annoReader;
    
    private final List<NeedProcess> needPostProcesses = new ArrayList<>();
    
    private SheetBindingErrors<?> errors;
    
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
    
    public SheetBindingErrors<?> getErrors() {
        return errors;
    }
    
    public void setErrors(SheetBindingErrors<?> errors) {
        this.errors = errors;
    }
    
    /**
     * 型変換エラーを追加します。
     * @param bindException 型変換エラー
     * @param cell マッピング元となったセル
     * @param fieldName マッピング先のフィールド名
     * @param label ラベル。省略する場合は、nullを指定します。
     */
    public void addTypeBindError(final TypeBindException bindException, final Cell cell, final String fieldName, final String label) {
        addTypeBindError(bindException, CellPosition.of(cell), fieldName, label);
    }
    
    /**
     * 型変換エラーを追加します。
     * @param bindException 型変換エラー
     * @param address マッピング元となったセルのアドレス
     * @param fieldName マッピング先のフィールド名
     * @param label ラベル。省略する場合は、nullを指定します。
     */
    public void addTypeBindError(final TypeBindException bindException, final CellPosition address, final String fieldName, final String label) {
        
        this.errors.createFieldConversionError(fieldName, bindException.getBindClass(), bindException.getTargetValue())
            .variables(bindException.getMessageVars())
            .variables("validatedValue", bindException.getTargetValue())
            .address(address)
            .label(label)
            .buildAndAddError();
        
    }
    
    
}
