package com.gh.mygreen.xlsmapper.sample.dbdefinition;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.gh.mygreen.xlsmapper.annotation.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;

/**
 * カラムの定義のレコード
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ColumnRecord {

    /**
     * セルの座標
     */
    private Map<String, CellPosition> positions;

    /**
     * ラベル情報
     */
    private Map<String, String> labels;

    @NotNull
    @XlsColumn(columnName="列No.")
    private Integer no;

    @NotEmpty
    @XlsColumn(columnName="列名(論理)")
    private String logicalName;

    @NotEmpty
    @XlsColumn(columnName="列名(物理)")
    private String phisicalName;

    @XlsColumn(columnName="データ型")
    private String dataType;

    @XlsColumn(columnName="長さ")
    private String length;

    @XlsColumn(columnName="必須")
    @XlsBooleanConverter(loadForTrue={"Yes","1"}, loadForFalse={"No", ""}, saveAsTrue="Yes", saveAsFalse="No")
    private Boolean required;

    @XlsColumn(columnName="主キー")
    @XlsBooleanConverter(loadForTrue={"Yes", "1"}, loadForFalse={"No", ""}, saveAsTrue="Yes", saveAsFalse="No")
    private Boolean primaryKey;

    @XlsColumn(columnName="外部キー")
    @XlsBooleanConverter(loadForTrue={"Yes", "1"}, loadForFalse={"No", ""}, saveAsTrue="Yes", saveAsFalse="No")
    private Boolean foreignKey;

    @XlsColumn(columnName="参照表(列)名")
    private String reference;

    @XlsColumn(columnName="依存")
    @XlsBooleanConverter(loadForTrue={"Yes", "1"}, loadForFalse={"No", ""}, saveAsTrue="Yes", saveAsFalse="No")
    private Boolean depend;

    @XlsColumn(columnName="規定値")
    private String defaultValue;

    @XlsIgnorable
    public boolean isEmpty() {
        return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels");
    }

    public Map<String, CellPosition> getPositions() {
        return positions;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(Boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getDepend() {
        return depend;
    }

    public void setDepend(Boolean depend) {
        this.depend = depend;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
