package org.mygreen.xlsmapper;

import java.awt.Point;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.annotation.LabelledCellType;
import org.mygreen.xlsmapper.annotation.OverRecordOperate;
import org.mygreen.xlsmapper.annotation.RecordTerminal;
import org.mygreen.xlsmapper.annotation.RemainedRecordOperate;
import org.mygreen.xlsmapper.annotation.XlsCell;
import org.mygreen.xlsmapper.annotation.XlsColumn;
import org.mygreen.xlsmapper.annotation.XlsHint;
import org.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import org.mygreen.xlsmapper.annotation.XlsIterateTables;
import org.mygreen.xlsmapper.annotation.XlsLabelledCell;
import org.mygreen.xlsmapper.annotation.XlsPostLoad;
import org.mygreen.xlsmapper.annotation.XlsPostSave;
import org.mygreen.xlsmapper.annotation.XlsPreLoad;
import org.mygreen.xlsmapper.annotation.XlsPreSave;
import org.mygreen.xlsmapper.annotation.XlsSheet;
import org.mygreen.xlsmapper.annotation.XlsSheetName;
import org.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import org.mygreen.xlsmapper.annotation.converter.XlsArrayConverter;
import org.mygreen.xlsmapper.annotation.converter.XlsConverter;
import org.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import org.mygreen.xlsmapper.annotation.converter.XlsEnumConverter;
import org.mygreen.xlsmapper.annotation.converter.XlsNumberConverter;

@XlsSheet(name="情報")
public class Employer {
    
    protected enum UpdateType {
        New("新規"),Update("更新"),Delete("削除");
        private String alias;
        
        private UpdateType(String alias) {
            this.alias = alias;
        }
        
        public String alias() {
            return alias;
        }
    }
    
    public Map<String, Point> positions;
    
    public Map<String, String> labels;
    
    @XlsSheetName
    public String fieldSheetName;
    
    public String methodSheetName;
    
//    @XlsCell(column=1, row=1)
    @XlsCell(address="B2")
    @Size(max=2)
    private String cellValue;
    
    @XlsCell(address="B3")
    @XlsNumberConverter(pattern="#,##0.00;-#,##0.00")
    public Double intValue;
    
    @XlsCell(address="A8")
//    @XlsLabelledCell(labelAddress="A7", type = LabelledCellType.Bottom)
    @XlsDateConverter(pattern="yyyy年M月d日")
    protected Date dateValue;
    
    @XlsLabelledCell(label="組織情報", type=LabelledCellType.Right)
    @XlsConverter(forceShrinkToFit=true)
    public String labelledCell;
    
    @XlsLabelledCell(label="更新タイプ", type=LabelledCellType.Bottom)
    @XlsEnumConverter(ignoreCase=true)
    private UpdateType updateType;
    
    @XlsLabelledCell(label="更新タイプ(日本語)", type=LabelledCellType.Bottom)
    @XlsEnumConverter(ignoreCase=true, valueMethodName="alias")
    public UpdateType updateType2;
    
    @XlsLabelledCell(label="区切り文字", type=LabelledCellType.Right)
    @XlsConverter(trim=true)
    @XlsArrayConverter(separator=",")
    @XlsHint(order=0)
    private String[] split;
    
    @XlsHorizontalRecords(tableLabel="履歴", terminal=RecordTerminal.Border,
            overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete)
    @XlsHint(order=1)
    @Valid
    private List<EmployerHistory> history;
    
    @XlsVerticalRecords(tableLabel="履歴垂直", terminal=RecordTerminal.Border, overRecord=OverRecordOperate.Copy)
    private List<EmployerHistory> historyVertical;
    
    @XlsIterateTables(tableLabel="人物情報", bottom=3, optional=true)
    private List<EmployerInfo> employerInfoList;
    
    public String getMethodSheetName() {
        return methodSheetName;
    }
    
    @XlsSheetName
    public void setMethodSheetName(String methodSheetName) {
        this.methodSheetName = methodSheetName;
    }
    
    @XlsPreLoad
    public void handlePreLoad() {
//        System.out.println("preLoad=" + this.getClass().getName());
    }
    
    @XlsPostLoad
    public void handlePostLoad(Sheet sheet) {
//        System.out.println("postLoad=" + this.getClass().getName());
//        System.out.printf("...postLoad, sheet name=%s.\n", sheet.getSheetName());
        
    }
    
    public String getCellValue() {
        return cellValue;
    }
    
    public List<EmployerHistory> getHistory() {
        return history;
    }
    
    public void setHistory(List<EmployerHistory> history) {
        this.history = history;
    }
    
    public List<EmployerHistory> getHistoryVertical() {
        return historyVertical;
    }
    
    public void setHistoryVertical(List<EmployerHistory> historyVertical) {
        this.historyVertical = historyVertical;
    }
    
    public List<EmployerInfo> getEmployerInfoList() {
        return employerInfoList;
    }
    
    public void setEmployerInfoList(List<EmployerInfo> employerInfoList) {
        this.employerInfoList = employerInfoList;
    }
    
    public UpdateType getUpdateType() {
        return updateType;
    }
    
    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }
    
    public String[] getSplit() {
        return split;
    }
    
    public void setSplit(String[] split) {
        this.split = split;
    }
    
    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }
}
