package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

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
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsHint;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * {@link LabelledCellProcessor}のテスタ
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class LabelledCellProcessorTest {
    
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
     * 読み込みテスト - 通常のテスト
     */
    @Test
    public void test_load_labelled_cell_normal() {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/processor_labelled_cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            assertThat(sheet.posRight,is("右側の値です。"));
            assertThat(sheet.posLeft,is("左側の値です。"));
            assertThat(sheet.posBottom,is("下側の値です。"));
            
            assertThat(sheet.foundNo,is(nullValue()));
            
            assertThat(cellFieldError(errors, cellAddress(sheet.positions.get("wrongFormat"))).isTypeBindFailure(), is(true));
            
            assertThat(sheet.header, is(utilDate(timestamp("2015-05-09 00:00:00.000"))));
            assertThat(sheet.headerSkip, is(utilDate(timestamp("2015-04-02 00:00:00.000"))));
            assertThat(sheet.headerRange, is(utilDate(timestamp("2015-06-13 00:00:00.000"))));
            
            assertThat(sheet.address1,is("右側の値です。"));
            assertThat(sheet.address2,is("下側の値です。"));
            
            assertThat(sheet.blank, is(nullValue()));
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * 読み込みテスト - バインドエラー
     */
    @Test(expected=TypeBindException.class)
    public void test_load_labelled_cell_bind_error() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/processor_labelled_cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            fail();
            
        }
    }
    
    /**
     * 読み込みテスト - ラベルで指定したセルが見つからない。
     */
    @Test(expected=CellNotFoundException.class)
    public void test_load_labelled_cell_notFoundCell() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/processor_labelled_cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NotFounceLabelCellSheet.class);
            
            NotFounceLabelCellSheet sheet = mapper.load(in, NotFounceLabelCellSheet.class, errors);
            
            fail();
            
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - 見出しセルのアドレスの書式が不正
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_labelled_cell_invalid_annotation1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/processor_labelled_cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(InvalidAnnoSheet1.class);
            
            InvalidAnnoSheet1 sheet = mapper.load(in, InvalidAnnoSheet1.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - 見出しセルのアドレスのインデックスが範囲外
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_labelled_cell_invalid_annotation2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/processor_labelled_cell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(InvalidAnnoSheet2.class);
            
            InvalidAnnoSheet2 sheet = mapper.load(in, InvalidAnnoSheet2.class, errors);
            
            fail();
            
        }
        
    }
    
    @XlsSheet(name="LabelledCell(通常)")
    private static class NormalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        /**
         * 位置のテスト - 右側
         */
        @XlsLabelledCell(label="位置（右側）", type=LabelledCellType.Right)
        private String posRight;
        
        /**
         * 位置のテスト - 左側
         */
        @XlsLabelledCell(label="位置（左側）", type=LabelledCellType.Left)
        private String posLeft;
        
        /**
         * 位置のテスト - 下側
         */
        @XlsLabelledCell(label="位置（下側）", type=LabelledCellType.Bottom)
        private String posBottom;
        
        /**
         * ラベルが見つからない 
         */
        @XlsLabelledCell(label="身つからない", type=LabelledCellType.Right, optional=true)
        private Integer foundNo;
        
        /**
         * 不正なフォーマット
         */
        @XlsLabelledCell(label="不正なフォーマット", type=LabelledCellType.Right)
        private Double wrongFormat;
        
        /**
         * ヘッダーラベル指定
         */
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Bottom, headerLabel="見出し１")
        private Date header;
        
        /**
         * ヘッダーラベル指定 - skip指定
         */
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Right, headerLabel="見出し２", skip=2)
        private Date headerSkip;
        
        /**
         * ヘッダーラベル指定 - range指定
         */
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Left, headerLabel="見出し３", range=2)
        private Date headerRange;
        
        /**
         * アドレス指定 - labelAddress
         */
        @XlsLabelledCell(labelAddress="B26", type=LabelledCellType.Right)
        private String address1;
        
        /**
         * アドレス指定 - labelColumn, labelRow
         */
        @XlsLabelledCell(labelColumn=1, labelRow=25, type=LabelledCellType.Bottom)
        private String address2;
        
        @XlsLabelledCell(label="値が空の場合", type=LabelledCellType.Right)
        private String blank;
        
    }
    
    /**
     * ラベルで指定したセルが見つからない場合
     */
    @XlsSheet(name="LabelledCell(通常)")
    private static class NotFounceLabelCellSheet {
        
        /**
         * ラベルが見つからない 
         */
        @XlsLabelledCell(label="身つからない", type=LabelledCellType.Right, optional=false)
        private Integer foundNo;
        
    }
    
    /**
     * アノテーションが不正 - ラベルのアドレスの書式が不正
     *
     */
    @XlsSheet(name="LabelledCell(通常)")
    private static class InvalidAnnoSheet1 {
        
        /**
         * アドレス指定 - labelAddress
         */
        @XlsLabelledCell(labelAddress="aaa", type=LabelledCellType.Right)
        private String address1;
        
    }
    
    /**
     * アノテーションが不正 - ラベルのアドレスの範囲が不正
     *
     */
    @XlsSheet(name="LabelledCell(通常)")
    private static class InvalidAnnoSheet2 {
        
        /**
         * アドレス指定 - labelColumn, labelRow
         */
        @XlsLabelledCell(labelColumn=-1, labelRow=-1, type=LabelledCellType.Bottom)
        private String address2;
        
    }

}
