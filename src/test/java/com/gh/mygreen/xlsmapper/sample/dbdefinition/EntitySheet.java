package com.gh.mygreen.xlsmapper.sample.dbdefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * エンティティを定義するシート
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@XlsSheet(name="SAMPLE DDL")
public class EntitySheet {

    /**
     * セルの座標
     */
    private Map<String, CellPosition> positions;

    /**
     * ラベル情報
     */
    private Map<String, String> labels;

    @NotEmpty
    @XlsRecordOption(overOperation=OverOperation.Insert)
    @XlsHorizontalRecords(headerAddress="A2", terminal=RecordTerminal.Border)
    private List<@Valid TableRecord> tables;

    public void addTable(TableRecord record) {
        if(tables == null) {
            this.tables = new ArrayList<>();
        }
        this.tables.add(record);
    }

    public Map<String, CellPosition> getPositions() {
        return positions;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public List<TableRecord> getTables() {
        return tables;
    }

    public void setTables(List<TableRecord> tables) {
        this.tables = tables;
    }
}
