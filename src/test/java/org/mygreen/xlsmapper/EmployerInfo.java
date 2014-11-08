package org.mygreen.xlsmapper;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.annotation.LabelledCellType;
import org.mygreen.xlsmapper.annotation.RecordTerminal;
import org.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import org.mygreen.xlsmapper.annotation.XlsLabelledCell;
import org.mygreen.xlsmapper.annotation.XlsPostLoad;
import org.mygreen.xlsmapper.annotation.XlsPostSave;
import org.mygreen.xlsmapper.annotation.XlsPreLoad;
import org.mygreen.xlsmapper.annotation.XlsPreSave;
import org.mygreen.xlsmapper.annotation.converter.XlsNumberConverter;


public class EmployerInfo {
	
    public Map<String, Point> positions;
    
    public Map<String, String> labels;
    
	@XlsLabelledCell(label="お名前", type=LabelledCellType.Right)
	private String name;

	@XlsLabelledCell(label="年齢", type=LabelledCellType.Right)
	@XlsNumberConverter(pattern="##0.00##")
	private Integer yaer;
	
	@XlsHorizontalRecords(tableLabel="人物情報", terminal=RecordTerminal.Border)
	private List<EmployerHistory> history;
	
	@XlsPreLoad
//	@XlsPreSave
    public void handlePreLoad() {
        System.out.println("preLoad=" + this.getClass().getName());
    }
    
    @XlsPostLoad
//    @XlsPostSave
    public void handlePostLoad(Sheet sheet) {
        System.out.println("postLoad=" + this.getClass().getName());
        System.out.printf("...postLoad, sheet name=%s.\n", sheet.getSheetName());
        
    }
	
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public Integer getYaer() {
		return yaer;
	}

	
	public void setYaer(Integer yaer) {
		this.yaer = yaer;
	}


	
	public List<EmployerHistory> getHistory() {
		return history;
	}


	
	public void setHistory(List<EmployerHistory> history) {
		this.history = history;
	}
	
	
}
