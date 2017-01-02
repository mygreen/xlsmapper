package com.gh.mygreen.xlsmapper.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;


/**
 * Spring管理のクラス
 * ・スコープは、プロトタイプにする。
 *
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
@XlsSheet(name="Spring管理のBean")
public class SampleSheet {
    
    /** SpringBeanをインジェクションする */
    @Autowired
    private SampleService sampleService;
    
    @XlsHorizontalRecords(tableLabel="一覧")
    private List<SampleRecord> records;
    
    /** 読み込み後に処理を実行する */
    @XlsPostLoad
    public void onLoad() {
        
        sampleService.doService();
        
    }
    
    public SampleService getSampleService() {
        return sampleService;
    }
    
    public List<SampleRecord> getRecords() {
        return records;
    }
    
    public void setRecords(List<SampleRecord> records) {
        this.records = records;
    }
    
}
