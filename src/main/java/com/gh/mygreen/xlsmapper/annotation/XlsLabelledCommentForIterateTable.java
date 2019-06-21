package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;

/**
 * {@link XlsIterateTables}内の{@link XlsLabelledComment}をブリッジするクラス。
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class XlsLabelledCommentForIterateTable implements XlsLabelledComment {

    private Class<? extends Annotation> annotationType = null;
    
    private String label = null;
    private int labelColumn = -1;
    private int labelRow = -1;
    private boolean optional = false;
    private String headerLabel = null;
    private ProcessCase[] cases = {};
    
    public XlsLabelledCommentForIterateTable(XlsLabelledComment anno, int labelRow, int labelColumn) {
        this.annotationType = anno.annotationType();
        this.label = "";
        this.labelRow = labelRow;
        this.labelColumn = labelColumn;
        this.optional = anno.optional();
        this.headerLabel = anno.headerLabel();
        this.cases = anno.cases();
        
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    @Override
    public boolean optional() {
        return optional;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public int labelRow() {
        return labelRow;
    }

    @Override
    public int labelColumn() {
        return labelColumn;
    }

    @Override
    public String headerLabel() {
        return headerLabel;
    }

    @Override
    public ProcessCase[] cases() {
        return cases;
    }
}
