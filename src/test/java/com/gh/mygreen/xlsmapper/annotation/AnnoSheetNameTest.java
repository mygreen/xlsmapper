package com.gh.mygreen.xlsmapper.annotation;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.SheetBindingErrorsContainer;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.SheetNameProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link SheetNameProcessor}のテスタ
 * アノテーション{@link XlsSheetName}のテスタ。
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoSheetNameTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_load_sheetName_name() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_SheetName.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            assertThat(sheet.sheetName, is("シート名（１）"));
            
        }
        
    }
    
    /**
     * 名前によるシート指定
     *
     */
    @XlsSheet(name="シート名（１）")
    private static class NormalSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    
}
