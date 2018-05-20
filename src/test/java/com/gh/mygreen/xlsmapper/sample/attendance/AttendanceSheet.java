package com.gh.mygreen.xlsmapper.sample.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * 作業報告書のシート
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@XlsSheet(name="作業報告書")
public class AttendanceSheet {

    /**
     * セルの座標
     */
    private Map<String, CellPosition> positions;

    /**
     * ラベル情報
     */
    private Map<String, String> labels;

    @NotNull
    @XlsLabelledCell(label="対象年月", type=LabelledCellType.Right)
    private LocalDate targetDate;

    @NotEmpty
    @XlsLabelledCell(label="プロジェクト名", type=LabelledCellType.Right)
    private String projectName;

    @NotEmpty
    @XlsLabelledCell(label="作業者", type=LabelledCellType.Right)
    private String workerName;

    @Size(min=1, max=31)
    @Valid
    @XlsHorizontalRecords(tableLabel="■作業時間/作業内容", terminal=RecordTerminal.Border, terminateLabel="作業日数")
    private List<DayRecord> days;

    @XlsLabelledCell(label="作業日数", type=LabelledCellType.Right, cases=ProcessCase.Load)
    private Integer workDaySum;

    @XlsLabelledCell(label="作業合計時間", type=LabelledCellType.Right, cases=ProcessCase.Load)
    private LocalDateTime workTimeSum;

    @XlsLabelledCell(label="■その他連絡事項", type=LabelledCellType.Bottom)
    private String message;

    public void addDay(DayRecord record) {
        if(days == null) {
            this.days = new ArrayList<>();
        }

        days.add(record);
    }

    public Map<String, CellPosition> getPositions() {
        return positions;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public List<DayRecord> getDays() {
        return days;
    }

    public void setDays(List<DayRecord> days) {
        this.days = days;
    }

    public Integer getWorkDaySum() {
        return workDaySum;
    }

    public void setWorkDaySum(Integer workDaySum) {
        this.workDaySum = workDaySum;
    }

    public LocalDateTime getWorkTimeSum() {
        return workTimeSum;
    }

    public void setWorkTimeSum(LocalDateTime workTimeSum) {
        this.workTimeSum = workTimeSum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
