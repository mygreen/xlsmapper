package com.gh.mygreen.xlsmapper.sample.attendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * 作業報告書の日々のレコード
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DayRecord {

    /**
     * セルの座標
     */
    private Map<String, CellPosition> positions;

    @NotNull
    @XlsColumn(columnName="月日", cases=ProcessCase.Load)
    private LocalDate date;

    @XlsColumn(columnName="開始時刻")
    private LocalTime startTime;

    @XlsColumn(columnName="終了時刻")
    private LocalTime endTime;

    @XlsColumn(columnName="休憩時間")
    private LocalTime restTime;

    @XlsColumn(columnName="作業時間", cases=ProcessCase.Load)
    private LocalTime workTime;

    @XlsColumn(columnName="作業内容")
    private String workContent;

    public DayRecord() {

    }

    public DayRecord(LocalTime startTime, LocalTime endTime, LocalTime restTime, String workContent) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.restTime = restTime;
        this.workContent = workContent;
    }

    /**
     * レコードを読み飛ばす条件
     * @return 月日が空の場合
     */
    @XlsIgnorable
    public boolean isEmpty() {
        return date == null;
    }

    public Map<String, CellPosition> getPositions() {
        return positions;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getRestTime() {
        return restTime;
    }

    public void setRestTime(LocalTime restTime) {
        this.restTime = restTime;
    }

    public LocalTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(LocalTime workTime) {
        this.workTime = workTime;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }
}
