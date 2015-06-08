package com.gh.mygreen.xlsmapper.annotation;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.LabelledCellProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * {@link LabelledCellProcessor}のテスタ
 * アノテーション{@link XlsLabelledCell}のテスタ。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoLabelledCellTest {
    
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
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            assertThat(sheet.posRight,is("右側の値です。"));
            assertThat(sheet.posLeft,is("左側の値です。"));
            assertThat(sheet.posBottom,is("下側の値です。"));
            
            assertThat(sheet.foundNo,is(nullValue()));
            
            assertThat(cellFieldError(errors, cellAddress(sheet.positions.get("wrongFormat"))).isTypeBindFailure(), is(true));
            
            assertThat(sheet.header, is(toUtilDate(toTimestamp("2015-05-09 00:00:00.000"))));
            assertThat(sheet.headerSkip, is(toUtilDate(toTimestamp("2015-04-02 00:00:00.000"))));
            assertThat(sheet.headerRange, is(toUtilDate(toTimestamp("2015-06-13 00:00:00.000"))));
            
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
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
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
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
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
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
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
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(InvalidAnnoSheet2.class);
            
            InvalidAnnoSheet2 sheet = mapper.load(in, InvalidAnnoSheet2.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 読み込みのテスト - 通常のデータ
     */
    @Test
    public void test_save_labelled_cell_normal() throws Exception {
        
        // テストデータの作成
        final NormalSheet outSheet = new NormalSheet();
        
        outSheet.posRight("右側です。")
            .posLeft("左側の値です。")
            .posBottom("下側の値です。")
            .foundNo(123)
            .wrongFormat(123.456)
            .header(toUtilDate(toTimestamp("2015-06-07 08:09:10.000")))
            .headerSkip(toUtilDate(toTimestamp("2012-03-04 05:06:07.000")))
            .headerRange(toUtilDate(toTimestamp("2011-02-03 04:05:06.000")))
            .address1("アドレス指定です。\n右側。")
            .address2("アドレス指定です。\n左側。")
            ;
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_LabelledCell.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_LabelledCell_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            assertThat(sheet.positions, is(outSheet.positions));
            assertThat(sheet.labels, is(outSheet.labels));
            
            assertThat(sheet.posRight, is(outSheet.posRight));
            assertThat(sheet.posLeft, is(outSheet.posLeft));
            assertThat(sheet.posBottom, is(outSheet.posBottom));
            
            assertThat(sheet.foundNo,is(nullValue()));
            assertThat(sheet.wrongFormat, is(outSheet.wrongFormat));
            
            assertThat(sheet.header, is(outSheet.header));
            assertThat(sheet.headerSkip, is(outSheet.headerSkip));
            assertThat(sheet.headerRange, is(outSheet.headerRange));
            
            assertThat(sheet.address1, is(outSheet.address1));
            assertThat(sheet.address2, is(outSheet.address2));
            
            assertThat(sheet.blank, is(outSheet.blank));
            
            
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
        
        /**
         * 値が空の場合
         */
        @XlsLabelledCell(label="値が空の場合", type=LabelledCellType.Right)
        private String blank;
        
        public NormalSheet posRight(String posRight) {
            this.posRight = posRight;
            return this;
        }
        
        public NormalSheet posLeft(String posLeft) {
            this.posLeft = posLeft;
            return this;
        }
        
        public NormalSheet posBottom(String posBottom) {
            this.posBottom = posBottom;
            return this;
        }
        
        public NormalSheet foundNo(Integer foundNo) {
            this.foundNo = foundNo;
            return this;
        }
        
        public NormalSheet wrongFormat(Double wrongFormat) {
            this.wrongFormat = wrongFormat;
            return this;
        }
        
        public NormalSheet header(Date header) {
            this.header = header;
            return this;
        }
        
        public NormalSheet headerSkip(Date headerSkip) {
            this.headerSkip = headerSkip;
            return this;
        }
        
        public NormalSheet headerRange(Date headerRange) {
            this.headerRange = headerRange;
            return this;
        }
        
        public NormalSheet address1(String address1) {
            this.address1 = address1;
            return this;
        }
        
        public NormalSheet address2(String address2) {
            this.address2 = address2;
            return this;
        }
        
        public NormalSheet blank(String blank) {
            this.blank = blank;
            return this;
        }
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
