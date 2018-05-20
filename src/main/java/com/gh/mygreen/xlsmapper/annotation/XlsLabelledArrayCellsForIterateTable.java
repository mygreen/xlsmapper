package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.IterateTablesProcessor;

/**
 * {@link XlsLabelledArrayCells}を{@link IterateTablesProcessor}にブリッジするためのアノテーションクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsLabelledArrayCellsForIterateTable implements XlsLabelledArrayCells {

    private final Class<? extends Annotation> annotationType;

    private final int labelRow;
    private final int labelColumn;

    private final int size;
    private final boolean elementMerged;
    private final ArrayDirection direction;
    private final Class<?> elementClass;
    private final boolean optional;
    private final int range;
    private final LabelledCellType type;
    private final int skip;
    private final boolean labelMarged;
    private final ProcessCase[] cases;


    public XlsLabelledArrayCellsForIterateTable(XlsLabelledArrayCells anno, int labelRow, int labelColumn) {
        this.annotationType = anno.annotationType();
        this.labelRow = labelRow;
        this.labelColumn = labelColumn;

        this.size = anno.size();
        this.elementMerged = anno.elementMerged();
        this.direction = anno.direction();
        this.elementClass = anno.elementClass();
        this.optional = anno.optional();
        this.range = anno.range();
        this.type = anno.type();
        this.skip = anno.skip();
        this.labelMarged = anno.labelMerged();
        this.cases = anno.cases();

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean elementMerged() {
        return elementMerged;
    }

    @Override
    public ArrayDirection direction() {
        return direction;
    }

    @Override
    public Class<?> elementClass() {
        return elementClass;
    }

    @Override
    public boolean optional() {
        return optional;
    }

    @Override
    public int range() {
        return range;
    }

    @Override
    public LabelledCellType type() {
        return type;
    }

    @Override
    public String label() {
        return "";
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
        return "";
    }

    @Override
    public int skip() {
        return skip;
    }

    @Override
    public boolean labelMerged() {
        return labelMarged;
    }

    @Override
    public ProcessCase[] cases() {
       return cases;
    }

}
