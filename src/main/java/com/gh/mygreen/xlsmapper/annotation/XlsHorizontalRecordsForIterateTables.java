package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.IterateTablesProcessor;


/**
 * {@link IterateTablesProcessor}から、{@link HorizontalRecordsProcessor}へ{@link XlsHorizontalRecords}の情報を渡す際のインタフェース用クラス。
 * <p>アノテーションとしては直接には使用しない。</p>
 * <p>{@link XlsIterateTables}により決定した表の開始位置を渡すために用いる。</p>
 * <p>表の開始位置の指定は、{@link XlsIterateTables}で指定済みなので、{@link #headerColumn()}、{@link #headerRow()}以外での開始位置の指定は無効にする。</p>
 *
 * @version 2.0
 * @author Mitsuyoshi Hasegawa
 * @author T.TSUCHIE
 */
public class XlsHorizontalRecordsForIterateTables implements XlsHorizontalRecords {

    private final Class<? extends Annotation> _annotationType;

    private int _headerColumn = -1;
    private int _headerRow = -1;
    private String _headerAddress = "";
    private boolean _optional = false;
    private int _range = -1;
    private Class<?> _recordClass = null;
    private String _tableLabel = "";
    private RecordTerminal _terminal = null;
    private String _terminateLabel = null;
    private int _bottom = 1;
    private int _headerLimit = 0;
    private int _headerBottom = 1;
    private ProcessCase[] _cases = {};

    /**
     * アノテーションを元に、インスタンスを作成する。
     * @param anno 元のアノテーション情報
     * @param headerColumn 表の見出しの位置 - 列番号
     * @param headerRow 表の見出しの位置 - 行番号
     */
    public XlsHorizontalRecordsForIterateTables(final XlsHorizontalRecords anno, int headerColumn, int headerRow) {
        this._annotationType = anno.annotationType();

        this._headerColumn = headerColumn;
        this._headerRow = headerRow;

        // headerColumn、headerRowを指定しているため、headerAddressは空で固定する。
        this._headerAddress = "";

        // 表の開始位置は、headerColumn, headerRowで指定するため、タイトルによる位置指定は無効にする。
        this._tableLabel = "";
        this._bottom = 1;

        this._optional = anno.optional();
        this._range = anno.range();
        this._recordClass = anno.recordClass();

        this._terminal = anno.terminal();
        this._terminateLabel = anno.terminateLabel();

        this._headerLimit = anno.headerLimit();
        this._headerBottom = anno.headerBottom();

        this._cases = anno.cases();

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
    public ProcessCase[] cases() {
        return this._cases;
    }

}
