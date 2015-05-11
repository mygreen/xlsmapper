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

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.SheetBindingErrorsContainer;
import com.gh.mygreen.xlsmapper.SheetNotFoundException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * アノテーション{@link XlsSheet}のテスト
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoSheetTest {
    
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
    public void test_load_sheet_name() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NamedSheet.class);
            
            NamedSheet sheet = mapper.load(in, NamedSheet.class, errors);
            
            assertThat(sheet.sheetName, is("シート名（１）"));
            
        }
        
    }
    
    @Test(expected=SheetNotFoundException.class)
    public void test_load_sheet_name_nofFound() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NamedSheet2.class);
            
            NamedSheet2 sheet = mapper.load(in, NamedSheet2.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * シートが見つからなくてもスキップする設定
     * 
     */
    @Test
    public void test_load_sheet_skip_notFound() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true)
            .setIgnoreSheetNotFound(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NamedSheet2.class);
            
            NamedSheet2 sheet = mapper.load(in, NamedSheet2.class, errors);
            
            assertThat(sheet, is(nullValue()));
            
        }
        
    }
    
    @Test
    public void test_load_sheet_indexed() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(IndexedSheet.class);
            
            IndexedSheet sheet = mapper.load(in, IndexedSheet.class, errors);
            
            assertThat(sheet.sheetName, is("あいう"));
            
        }
        
    }
    
    @Test(expected=SheetNotFoundException.class)
    public void test_load_sheet_indexed_nofFound() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(IndexedSheet2.class);
            
            IndexedSheet2 sheet = mapper.load(in, IndexedSheet2.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 正規表現指定 - シート１つ
     */
    @Test
    public void test_load_sheet_regexp_single() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            
            SheetBindingErrors errors = new SheetBindingErrors(IndexedSheet.class);
            
            RegexpSheet sheet = mapper.load(in, RegexpSheet.class, errors);
            
            assertThat(sheet.sheetName, is("編集条件（1）"));
            
        }
        
    }
    
    /**
     * 正規表現指定 - シート１つ - シートが見つからない場合
     */
    @Test(expected=SheetNotFoundException.class)
    public void test_load_sheet_regexp_single_notFound() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            
            SheetBindingErrors errors = new SheetBindingErrors(RegexpSheet2.class);
            
            RegexpSheet2 sheet = mapper.load(in, RegexpSheet2.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 正規表現指定 - シート複数
     */
    @Test
    public void test_load_sheetName_regexp_multiple() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            
            SheetBindingErrorsContainer errorsContainer = new SheetBindingErrorsContainer(RegexpSheet.class);
            
            RegexpSheet[] sheet = mapper.loadMultiple(in, RegexpSheet.class, errorsContainer);
            
            assertThat(sheet[0].sheetName, is("編集条件（1）"));
            assertThat(sheet[1].sheetName, is("編集条件（2）"));
            
        }
        
    }
    
    /**
     * アノテーションにシートの指定がない場合
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_sheet_noSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NoSettingSheet.class);
            
            NoSettingSheet sheet = mapper.load(in, NoSettingSheet.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 名前によるシート指定
     *
     */
    @XlsSheet(name="シート名（１）")
    private static class NamedSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * 名前によるシート指定 - 存在しないシート
     *
     */
    @XlsSheet(name="シート名（２）")
    private static class NamedSheet2 {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * インデックス番号によるシート指定
     *
     */
    @XlsSheet(number=1)
    private static class IndexedSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * インデックス番号によるシート指定 - 存在しないインデックス
     *
     */
    @XlsSheet(number=10)
    private static class IndexedSheet2 {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * 正規表現によるシート指定
     *
     */
    @XlsSheet(regex="編集条件.+")
    private static class RegexpSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * 正規表現によるシート指定 - 存在しない名前
     *
     */
    @XlsSheet(regex="チェック条件.+")
    private static class RegexpSheet2 {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * シートの設定が何もされていない場合
     *
     */
    @XlsSheet
    private static class NoSettingSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
}
