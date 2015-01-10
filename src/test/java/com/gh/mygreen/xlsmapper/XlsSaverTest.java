package com.gh.mygreen.xlsmapper;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsSaver;
import com.gh.mygreen.xlsmapper.Employer.UpdateType;


public class XlsSaverTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testSave() throws Exception {
        
        try {
            FileInputStream templateXlsIn = new FileInputStream("./src/test/data/employer.xlsx");
            
            Employer employer = new Employer();
            employer.setCellValue("writeあああああ");
            employer.intValue = 20.1;
            
            employer.setUpdateType(UpdateType.Delete);
            employer.updateType2 = UpdateType.New;
            
            employer.labelledCell = "Amateras, XLSBeans, Extension Library. Test.";
            employer.dateValue = new Date();
            
            List<EmployerHistory> historyList = new ArrayList<EmployerHistory>();
            historyList.add(new EmployerHistory(1, Timestamp.valueOf("2010-01-01 00:00:00.000"), "Value1", true));
            historyList.add(new EmployerHistory(2, Timestamp.valueOf("2010-02-01 00:00:00.000"), "Value2", false));
            historyList.add(new EmployerHistory(3, Timestamp.valueOf("2010-03-01 00:00:00.000"), "Value3", true));
            historyList.add(new EmployerHistory(4, Timestamp.valueOf("2010-04-01 00:00:00.000"), "Value3", false));
            employer.setHistory(historyList);
            
            Map<String, Date> attended1 = new HashMap<String, Date>();
            attended1.put("昨日", Timestamp.valueOf("2010-01-01 00:00:00.000"));
            attended1.put("今日", Timestamp.valueOf("2010-01-02 00:00:00.000"));
            attended1.put("明日", Timestamp.valueOf("2010-01-03 00:00:00.000"));
            historyList.get(0).setAttended(attended1);
            
            Map<String, Date> attended2 = new HashMap<String, Date>();
            attended2.put("昨日", Timestamp.valueOf("2011-02-01 00:00:00.000"));
            attended2.put("今日", Timestamp.valueOf("2011-02-02 00:00:00.000"));
            attended2.put("明日", Timestamp.valueOf("2011-02-03 00:00:00.000"));
            historyList.get(1).setAttended(attended2);
            
            Map<String, Date> attended3 = new HashMap<String, Date>();
            attended3.put("昨日", Timestamp.valueOf("2012-03-01 00:00:00.000"));
            attended3.put("今日", Timestamp.valueOf("2012-03-02 00:00:00.000"));
            attended3.put("明日", Timestamp.valueOf("2012-03-03 00:00:00.000"));
            historyList.get(2).setAttended(attended3);
            
            Map<String, Date> attended4 = new HashMap<String, Date>();
            attended4.put("昨日", Timestamp.valueOf("2013-04-01 00:00:00.000"));
            attended4.put("今日", Timestamp.valueOf("2014-04-02 00:00:00.000"));
            attended4.put("明日", Timestamp.valueOf("2015-04-03 00:00:00.000"));
            historyList.get(3).setAttended(attended4);
            
            employer.setHistoryVertical(historyList);
            
            // set value itelateTables
            List<EmployerInfo> employerInfoList = new ArrayList<EmployerInfo>();
            EmployerInfo info1 = new EmployerInfo();
            info1.setName("Yamada Tarou");
            info1.setYaer(30);
            info1.setHistory(historyList);
            employerInfoList.add(info1);
            
            EmployerInfo info2 = new EmployerInfo();
            info2.setName("Yamada Hanako");
            info2.setYaer(15);
            info2.setHistory(historyList);
            employerInfoList.add(info2);
            
            employer.setEmployerInfoList(employerInfoList);
            
            FileOutputStream outFile = new FileOutputStream("./emploery_out.xlsx");
            XlsSaver saver = new XlsSaver();
//            saver.getConfig().setMergeCellOnSave(true);
            saver.getConfig().setCorrectCellDataValidationOnSave(true);
            saver.getConfig().setCorrectNameRangeOnSave(true);
            saver.save(templateXlsIn, outFile, employer);
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testLoadInputStreamInputStreamClassOfP() {
        fail("Not yet implemented");
    }
}
