package com.gh.mygreen.xlsmapper.annotation;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.CellProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link CellProcessor}のテスタ。
 * アノテーション{@link XlsCell}のテスタ。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoCellTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * 読み込みのテスト - 通常のデータ
     */
    @Test
    public void test_load_cell_normal() {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            assertThat(sheet.c1,is("文字列です。\n改行あり。"));
            assertThat(sheet.c2,is(12.345));
            assertThat(sheet.c3,is(utilDate(timestamp("2015-05-09 14:20:00.000"))));
            assertThat(cellFieldError(errors, cellAddress(sheet.positions.get("c4"))).isTypeBindFailure(), is(true));
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * 読み込みのテスト - バインドエラー
     */
    @Test(expected=TypeBindException.class)
    public void test_load_cell_bind_error() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - インデックスが範囲外
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_cell_invalid_annotation1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(InvalidAnno1Sheet1.class);
            
            InvalidAnno1Sheet1 sheet = mapper.load(in, InvalidAnno1Sheet1.class, errors);
            
            fail();
            
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - アドレスが不正
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_cell_invalid_annotation2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_Cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(InvalidAnnoSheet2.class);
            
            InvalidAnnoSheet2 sheet = mapper.load(in, InvalidAnnoSheet2.class, errors);
            
            fail();
            
        }
    }
    
    @XlsSheet(name="Cell(通常)")
    private static class NormalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        /**
         * インデックス指定
         */
        @XlsCell(column=1, row=3)
        private String c1;
        
        /**
         * アドレス指定
         */
        @XlsCell(address="C7")
        private Double c2;
        
        /**
         * インデックス指定+アドレス指定
         */
        @XlsCell(column=0, row=0, address="B10")
        private Date c3;
        
        /**
         * 不正なフォーマット
         */
        @XlsCell(address="D12")
        private Integer c4;
        
    }
    
    /**
     * 不正なアノテーションの使い方 - インデックスが範囲外
     *
     */
    @XlsSheet(name="Cell(通常)")
    private static class InvalidAnno1Sheet1 {
        
        @XlsCell(column=-1, row=-1)
        private String c1;
        
    }
    
    /**
     * 不正なアノテーションの使い方 - アドレスが不正
     * 
     */
    @XlsSheet(name="Cell(通常)")
    private static class InvalidAnnoSheet2 {
        
        @XlsCell(address="あいう")
        private String c1;
        
    }
}
