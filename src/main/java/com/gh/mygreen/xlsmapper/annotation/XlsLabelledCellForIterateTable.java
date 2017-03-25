package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Annotation;

/**
 *
 * @author Mitsuyoshi Hasegawa
 */
public class XlsLabelledCellForIterateTable implements XlsLabelledCell {

    private String _label = null;
    private int _labelColumn = -1;
    private int _labelRow = -1;
    private String _labelAddress = null;
    private boolean _optional = false;
    private int _range = -1;
    private LabelledCellType _type = null;
    private Class<? extends Annotation> _annotationType = null;
    private String _headerLabel = null;
    private int _skip = 0;
    private boolean _labelMerged = false;

    public XlsLabelledCellForIterateTable(XlsLabelledCell labelledCell, int labelRow, int labelColumn, String labelAddress) {
        this._label = "";
        this._labelColumn = labelColumn;
        this._labelRow = labelRow;
        this._labelAddress = labelAddress;
        this._optional = labelledCell.optional();
        this._range = labelledCell.range();
        this._type = labelledCell.type();
        this._annotationType = labelledCell.annotationType();
        this._headerLabel = labelledCell.headerLabel();
        this._skip = labelledCell.skip();
        this._labelMerged = labelledCell.labelMerged();
    }
    
    @Override
    public String label() {
        return this._label;
    }
    
    @Override
    public int labelColumn() {
        return this._labelColumn;
    }
    
    @Override
    public int labelRow() {
        return this._labelRow;
    }
    
    @Override
    public String labelAddress() {
        return this._labelAddress;
    }
    
    @Override
    public boolean optional() {
        return this._optional;
    }
    
    @Override
    public int range() {
        return this._range;
    }
    
    @Override
    public LabelledCellType type() {
        return this._type;
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return this._annotationType;
    }
    
    @Override
    public String headerLabel() {
        return this._headerLabel;
    }
    
    @Override
    public int skip() {
        return this._skip;
    }
    
    @Override
    public boolean labelMerged() {
        return this._labelMerged;
    }
}
