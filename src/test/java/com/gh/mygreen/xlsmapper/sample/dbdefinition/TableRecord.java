package com.gh.mygreen.xlsmapper.sample.dbdefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;

/**
 * 表の定義のレコード
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TableRecord {

    /**
     * セルの座標
     */
    private Map<String, CellPosition> positions;

    /**
     * ラベル情報
     */
    private Map<String, String> labels;

    @NotNull
    @XlsColumn(columnName="種類(Table/View)")
    private TableCategory category;

    @NotEmpty
    @XlsColumn(columnName="表名(論理)")
    private String logicalName;

    @NotEmpty
    @XlsColumn(columnName="表名(物理)")
    private String phisicalName;

    @NotEmpty
    @XlsNestedRecords
    private List<@Valid ColumnRecord> columns;

    @XlsIgnorable
    public boolean isEmpty() {
        return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels");
    }

    public void addColumn(ColumnRecord record) {
        if(columns == null) {
            this.columns = new ArrayList<>();
        }
        this.columns.add(record);
    }

    public Map<String, CellPosition> getPositions() {
        return positions;
    }

    public TableCategory getCategory() {
        return category;
    }

    public void setCategory(TableCategory category) {
        this.category = category;
    }

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String logicalName) {
        this.logicalName = logicalName;
    }

    public String getPhisicalName() {
        return phisicalName;
    }

    public void setPhisicalName(String phisicalName) {
        this.phisicalName = phisicalName;
    }

    public List<ColumnRecord> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnRecord> columns) {
        this.columns = columns;
    }
}
