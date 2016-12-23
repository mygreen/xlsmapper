package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Annotation;


/**
 * {@link XlsIterateTables}をProcessorへ渡す際のインタフェース用クラス。
 * <p>アノテーションとしては直接には使用しない。
 * 
 * @version 1.1
 * @author Mitsuyoshi Hasegawa
 * @author T.TSUCHIE
 */
public class XlsHorizontalRecordsForIterateTables implements XlsHorizontalRecords {
    
    private int _headerColumn = -1;
    private int _headerRow = -1;
    private String _headerAddress = "";
    private boolean _optional = false;
    private int _range = -1;
    private Class<?> _recordClass = null;
    private String _tableLabel = "";
    private RecordTerminal _terminal = null;
    private Class<? extends Annotation> _annotationType = null;
    private String _terminateLabel = null;
    private int _bottom = 1;
    private int _headerLimit = 0;
    private int _headerBottom = 1;
    private OverRecordOperate _orverRecord = null;
    private RemainedRecordOperate _remainedRecord = null;
    private boolean _skipEmptyRecord = false;
    
    public XlsHorizontalRecordsForIterateTables(final XlsHorizontalRecords rec, int headerColumn, int headerRow) {
        this._headerColumn = headerColumn;
        this._headerRow = headerRow;
        
        this._headerAddress = rec.headerAddress();
        
        this._optional = rec.optional();
        this._range = rec.range();
        this._recordClass = rec.recordClass();
        
        // Tablelabel is permanent empty.
        this._tableLabel = "";
        this._terminal = rec.terminal();
        this._annotationType = rec.annotationType();
        this._terminateLabel = rec.terminateLabel();
        this._bottom = 1;
        this._headerLimit = rec.headerLimit();
        this._headerBottom = rec.headerBottom();
        
        this._orverRecord = rec.overRecord();
        this._remainedRecord = rec.remainedRecord();
        this._skipEmptyRecord = rec.ignoreEmptyRecord();
    }
    
    @Override
    public int headerColumn() {
        return this._headerColumn;
    }
    
    @Override
    public int headerRow() {
        return this._headerRow;
    }
    
    @Override
    public String headerAddress() {
        return this._headerAddress;
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
    public Class<?> recordClass() {
        return this._recordClass;
    }
    
    @Override
    public String tableLabel() {
        return this._tableLabel;
    }
    
    @Override
    public RecordTerminal terminal() {
        return this._terminal;
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return this._annotationType;
    }
    
    @Override
    public String terminateLabel() {
        return this._terminateLabel;
    }
    
    @Override
    public int bottom() {
        return this._bottom;
    }
    
    @Override
    public int headerLimit() {
        return this._headerLimit;
    }
    
    @Override
    public int headerBottom() {
        return this._headerBottom;
    }
    
    @Override
    public OverRecordOperate overRecord() {
        return this._orverRecord;
    }
    @Override
    public RemainedRecordOperate remainedRecord() {
        return this._remainedRecord;
    }
    
    @Override
    public boolean ignoreEmptyRecord() {
        return this._skipEmptyRecord;
    }
}
