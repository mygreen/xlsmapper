/*
 * EmployerHistory.java
 * created in 2010/01/31
 *
 * (C) Copyright 2003-2010 GreenDay Project. All rights reserved.
 */
package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.util.Date;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsNumberConverter;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class EmployerHistory {
	
    private Map<String, Point> positions;
    
    private Map<String, String> labels;
    
	@XlsColumn(columnName="No.", optional=true)
	@XlsNumberConverter(pattern="##.0##")
	private int index;
	
	@XlsColumn(columnName="日付", merged=true)
	@XlsDateConverter(pattern="yyyy-MM-dd")
	private Date historyDate;
	
	@NotEmpty
	@XlsColumn(columnName="項目", merged=true)
	private String comment;
	
	@XlsColumn(columnName="使用有無")
	@XlsBooleanConverter(loadForTrue="○", loadForFalse={"×", "-"}, saveAsTrue="○", saveAsFalse="", failToFalse=true)
	private boolean used;
	
	@XlsMapColumns(previousColumnName="使用有無"/*, itemClass=Date.class*/)
	@XlsDateConverter(pattern="(yyyy)-MM-dd")
	private Map<String, Date> attended;
	
	public EmployerHistory() {
		
	}
	
	public EmployerHistory(int index, Date historyDate, String comment, boolean used) {
		this.index = index;
		this.historyDate = historyDate;
		this.comment = comment;
        this.used = used;
	}
	
	@XlsPreLoad
    public void handlePreLoad() {
//        System.out.println("preLoad=" + this.getClass().getName());
    }
    
    @XlsPostLoad
    public void handlePostLoad() {
//        System.out.println("postLoad=" + this.getClass().getName());
        
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public Date getHistoryDate() {
        return historyDate;
    }
    
    public void setHistoryDate(Date historyDate) {
        this.historyDate = historyDate;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
    public Map<String, Date> getAttended() {
        return attended;
    }
    
    public void setAttended(Map<String, Date> attended) {
        this.attended = attended;
    }
    
}
