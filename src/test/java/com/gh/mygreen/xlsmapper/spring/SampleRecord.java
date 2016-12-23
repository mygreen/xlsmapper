package com.gh.mygreen.xlsmapper.spring;

import java.awt.Point;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;


/**
 * Spring管理のクラス
 * ・スコープは、プロトタイプにする。
 *
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class SampleRecord {
    
    private Map<String, Point> positions;
    
    private Map<String, String> labels;
    
    @Autowired
    private SampleService sampleService;
    
    @XlsColumn(columnName="No.")
    private int no;
    
    @XlsColumn(columnName="名称")
    private String name;
    
    @XlsColumn(columnName="値")
    private Double value;
    
    @XlsPostLoad
    public void onLoad() {
        
        sampleService.doService();
        
    }
    
    public int getNo() {
        return no;
    }
    
    public void setNo(int no) {
        this.no = no;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public SampleService getSampleService() {
        return sampleService;
    }
    

}
