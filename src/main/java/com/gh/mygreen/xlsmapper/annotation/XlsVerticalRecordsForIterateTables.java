package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.VerticalRecordsProcessor;

/**
 * {@link IterateTablesProcessor}から、{@link VerticalRecordsProcessor}へ{@link XlsVerticalRecords}の情報を渡す際のインタフェース用クラス。
 * <p>アノテーションとしては直接には使用しない。</p>
 * <p>{@link XlsIterateTables}により決定した表の開始位置を渡すために用いる。</p>
 * <p>表の開始位置の指定は、{@link XlsIterateTables}で指定済みなので、{@link #headerColumn()}、{@link #headerRow()}以外での開始位置の指定は無効にする。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsVerticalRecordsForIterateTables implements XlsVerticalRecords {

    private final Class<? extends Annotation> _annotationType;

    private int _headerColumn = -1;
    private int _headerRow = -1;
    private String _headerAddress = "";

    private String _tableLabel = "";
    private boolean _tableLabelAvobe;

    private boolean _optional = false;
    private int _range = -1;
    private Class<?> _recordClass = null;

    private RecordTerminal _terminal = null;
    private String _terminateLabel = null;

    private int _bottom = 1;
    private int _right = 1;

    private int _headerLimit = 0;
    private int _headerRight = 1;

    private ProcessCase[] _cases = {};

    /**
     * アノテーションを元に、インスタンスを作成する。
     * @param anno 元のアノテーション情報
     * @param headerColumn 表の見出しの位置 - 列番号
     * @param headerRow 表の見出しの位置 - 行番号
     */
    public XlsVerticalRecordsForIterateTables(final XlsVerticalRecords anno, int headerColumn, int headerRow) {
        this._annotationType = anno.annotationType();

        this._headerColumn = headerColumn;
        this._headerRow = headerRow;

        // headerColumn、headerRowを指定しているため、headerAddressは空で固定する。
        this._headerAddress = "";

        // 表の開始位置は、headerColumn, headerRowで指定するため、タイトルによる位置指定は無効にする。
        this._tableLabel = "";
        this._tableLabelAvobe = true;
        this._bottom = 1;

        this._optional = anno.optional();
        this._range = anno.range();
        this._recordClass = anno.recordClass();

        this._terminal = anno.terminal();
        this._terminateLabel = anno.terminateLabel();

        this._right = anno.right();

        this._headerLimit = anno.headerLimit();
        this._headerRight = anno.headerRight();

        this._cases = anno.cases();

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return _annotationType;
    }

    @Override
    public boolean optional() {
        return _optional;
    }

    @Override
    public String tableLabel() {
        return _tableLabel;
    }

    @Override
    public boolean tableLabelAbove() {
        return _tableLabelAvobe;
    }

    @Override
    public String terminateLabel() {
        return _terminateLabel;
    }

    @Override
    public int headerColumn() {
        return _headerColumn;
    }

    @Override
    public int headerRow() {
        return _headerRow;
    }

    @Override
    public String headerAddress() {
        return _headerAddress;
    }

    @Override
    public Class<?> recordClass() {
        return _recordClass;
    }

    @Override
    public RecordTerminal terminal() {
        return _terminal;
    }

    @Override
    public int range() {
        return _range;
    }

    @Override
    public int right() {
        return _right;
    }

    @Override
    public int bottom() {
        return _bottom;
    }

    @Override
    public int headerLimit() {
        return _headerLimit;
    }

    @Override
    public int headerRight() {
        return _headerRight;
    }

    @Override
    public ProcessCase[] cases() {
        return _cases;
    }

}
