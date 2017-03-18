package com.gh.mygreen.xlsmapper.fieldprocessor;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
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
    
    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }
    
    /**
     * 読み込みのテスト - シート名の指定
     */
    @Test
    public void test_load_sheet_name() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NamedSheet.class);
            
            NamedSheet sheet = mapper.load(in, NamedSheet.class, errors);
            
            assertThat(sheet.sheetName, is("シート名（１）"));
            
        }
        
    }
    
    @Test(expected=SheetNotFoundException.class)
    public void test_load_sheet_name_notFound() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
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
        mapper.getConig().setContinueTypeBindFailure(true)
            .setIgnoreSheetNotFound(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NamedSheet2.class);
            
            NamedSheet2 sheet = mapper.load(in, NamedSheet2.class, errors);
            
            assertThat(sheet, is(nullValue()));
            
        }
        
    }
    
    @Test
    public void test_load_sheet_indexed() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(IndexedSheet.class);
            
            IndexedSheet sheet = mapper.load(in, IndexedSheet.class, errors);
            
            assertThat(sheet.sheetName, is("あいう"));
            
        }
        
    }
    
    @Test(expected=SheetNotFoundException.class)
    public void test_load_sheet_indexed_nofFound() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Sheet.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NoSettingSheet.class);
            
            NoSettingSheet sheet = mapper.load(in, NoSettingSheet.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 書き込みのテスト - シート名の指定
     */
    @Test
    public void test_save_sheet_name() throws Exception {
        
        // テストデータの作成
        final NamedSheet outSheet = new NamedSheet();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(NamedSheet.class);
            
            NamedSheet sheet = mapper.load(in, NamedSheet.class, errors);
            
            assertThat(sheet.sheetName, is(outSheet.sheetName));
            
        }
        
    }
    
    /**
     * 書き込みのテスト - シート名の指定：指定したシートが存在しない。
     */
    @Test(expected=SheetNotFoundException.class)
    public void test_save_sheet_name_nofFound() throws Exception {
        
        // テストデータの作成
        final NamedSheet2 outSheet = new NamedSheet2();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            fail();
        }
        
    }
    
    /**
     * 書き込みのテスト - シートが見つからなくてもスキップする設定
     */
    @Test
    public void test_save_sheet_skip_notFound() throws Exception {
        
        // テストデータの作成
        final NamedSheet2 outSheet = new NamedSheet2();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true)
            .setIgnoreSheetNotFound(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            assertThat(outSheet.sheetName, is(nullValue()));
        }
        
    }
    
    /**
     * 書き込みのテスト - インデックス指定
     */
    @Test
    public void test_save_sheet_indexed() throws Exception {
        
        // テストデータの作成
        final IndexedSheet outSheet = new IndexedSheet();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true)
            .setIgnoreSheetNotFound(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            assertThat(outSheet.sheetName, is("あいう"));
        }
        
    }
    
    /**
     * 書き込みのテスト - インデックス指定。シートが見つからない場合
     */
    @Test(expected=SheetNotFoundException.class)
    public void test_save_sheet_indexed_notFound() throws Exception {
        
        // テストデータの作成
        final IndexedSheet2 outSheet = new IndexedSheet2();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
        
    }
    
    /**
     * 書き込みのテスト - 正規表現の指定。シートが1つ。
     */
    @Test
    public void test_save_sheet_regexp_single() throws Exception {
        
        // テストデータの作成
        final RegexpSheet outSheet = new RegexpSheet();
        outSheet.sheetName = "編集条件（1）";
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            assertThat(outSheet.sheetName, is("編集条件（1）"));
        }
        
    }
    
    /**
     * 書き込みのテスト - 正規表現の指定。シートが見つからない場合
     */
    @Test(expected=SheetNotFoundException.class)
    public void test_save_sheet_regexp_single_notFound() throws Exception {
        
        // テストデータの作成
        final RegexpSheet2 outSheet = new RegexpSheet2();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
        
    }
    
    /**
     * 書き込みのテスト - 正規表現の指定。複数のシートがヒットした場合。
     */
    @Test(expected=SheetNotFoundException.class)
    public void test_save_sheet_regexp_single_notFound2() throws Exception {
        
        // テストデータの作成
        final RegexpSheet outSheet = new RegexpSheet();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
        
    }
    
    /**
     * 書き込みのテスト - 正規表現の指定。シートが2つ。
     */
    @Test
    public void test_save_sheetName_regexp_multiple() throws Exception {
        
        // テストデータの作成
        final RegexpSheet outSheet1 = new RegexpSheet();
        outSheet1.sheetName = "編集条件（1）";
        
        final RegexpSheet outSheet2 = new RegexpSheet();
        outSheet2.sheetName = "編集条件（2）";

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.saveMultiple(template, out, new Object[]{outSheet1, outSheet2});
            
            assertThat(outSheet1.sheetName, is("編集条件（1）"));
            assertThat(outSheet2.sheetName, is("編集条件（2）"));
        }
        
    }
    
    /**
     * 書き込みのテスト - アノテーションにシートの指定がない場合
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_save_sheet_noSetting() throws Exception {
        
        // テストデータの作成
        final NoSettingSheet outSheet = new NoSettingSheet();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_Sheet_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_Sheet_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
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
