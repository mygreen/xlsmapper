package com.gh.mygreen.xlsmapper.fieldprocessor;

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
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsHint;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.LabelledCellProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * {@link LabelledCellProcessor}のテスタ
 * アノテーション{@link XlsLabelledCell}のテスタ。
 * @version 1.5
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
    public void test_load_labelled_cell_normal() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
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
            
            assertThat(sheet.mergedCell, is("結合先の値です。"));
            
        }
    }
    
    /**
     * 読み込みテスト - バインドエラー
     */
    @Test(expected=TypeBindException.class)
    public void test_load_labelled_cell_bind_error() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(false);
        
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
        mapper.getConig().setContinueTypeBindFailure(false);
        
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
        mapper.getConig().setContinueTypeBindFailure(false);
        
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
        mapper.getConig().setContinueTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(InvalidAnnoSheet2.class);
            
            InvalidAnnoSheet2 sheet = mapper.load(in, InvalidAnnoSheet2.class, errors);
            
            fail();
            
        }
        
    }
    
    /**
     * 読み込みのテスト - メソッドにアノテーションを付与
     * @since 1.0
     */
    @Test
    public void test_load_labelled_cell_methodAnno() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = mapper.load(in, MethodAnnoSheet.class, errors);
            
            assertThat(sheet.posRight,is("右側の値です。"));
            assertThat(sheet.posLeft,is("左側の値です。"));
            assertThat(sheet.posBottom,is("下側の値です。"));
            
            assertThat(sheet.foundNo,is(nullValue()));
            
            assertThat(cellFieldError(errors, cellAddress(sheet.wrongFormatPosition)).isTypeBindFailure(), is(true));
            
            assertThat(sheet.header, is(toUtilDate(toTimestamp("2015-05-09 00:00:00.000"))));
            assertThat(sheet.headerSkip, is(toUtilDate(toTimestamp("2015-04-02 00:00:00.000"))));
            assertThat(sheet.headerRange, is(toUtilDate(toTimestamp("2015-06-13 00:00:00.000"))));
            
            assertThat(sheet.address1,is("右側の値です。"));
            assertThat(sheet.address2,is("下側の値です。"));
            
            assertThat(sheet.blank, is(nullValue()));
            
        }
        
    }
    
    /**
     * 読み込みのテスト - 正規表現、正規化によるテスト
     */
    @Test
    public void test_load_labelled_cell_regex() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        
        // エラー確認（正規表現が無効）
        mapper.getConig().setRegexLabelText(false)
            .setNormalizeLabelText(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(RegexSheet.class);
            
            RegexSheet sheet = mapper.load(in, RegexSheet.class, errors);
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(CellNotFoundException.class));
        }
        
        // エラー確認（正規化が無効）
        mapper.getConig().setRegexLabelText(true)
            .setNormalizeLabelText(false);
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(RegexSheet.class);
            
            RegexSheet sheet = mapper.load(in, RegexSheet.class, errors);
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(CellNotFoundException.class));
        }
        
        // 正規表現、正規化の両方が有効
        mapper.getConig().setRegexLabelText(true)
            .setNormalizeLabelText(true);
        try(InputStream in = new FileInputStream("src/test/data/anno_LabelledCell.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(RegexSheet.class);
            
            RegexSheet sheet = mapper.load(in, RegexSheet.class, errors);
            
            // 値の比較
            assertThat(sheet.regexp,is("正規表現の見出し。"));
            assertThat(sheet.notRegexp,is("非正規表現の見出し。"));
            assertThat(sheet.normalize,is("改行がある場合\nです。"));
            assertThat(sheet.headerRegexp,is("見出しが正規表現です。"));
            
            // 見出しの比較
            assertThat(sheet.labels.get("regexp"), is("見出し(1)"));
            assertThat(sheet.labels.get("notRegexp"), is("見出し(a)"));
            assertThat(sheet.labels.get("normalize"), is(" 更新\n日時 "));
            assertThat(sheet.labels.get("headerRegexp"), is("ラベル"));
        }
        
    }
    
    /**
     * 読み込みのテスト - 数式のテスト
     */
    @Test
    public void test_load_labelled_formula() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(new File("src/test/data/anno_LabelledCell.xlsx"))) {
            
            SheetBindingErrors errors = new SheetBindingErrors(FormulaSheet.class);
            
            FormulaSheet sheet = mapper.load(in, FormulaSheet.class, errors);
            
            assertThat(sheet.start, is(toUtilDate(toTimestamp("2016-05-22 00:00:00.000"))));
            assertThat(sheet.end, is(toUtilDate(toTimestamp("2016-10-01 00:00:00.000"))));
            assertThat(sheet.diff, is(132));
            
        }
        
    }
    
    /**
     * 書き込みのテスト - 通常のデータ
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
            .mergedCell("結合先の値です。");
            ;
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_LabelledCell_out.xlsx");
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
            
            assertThat(sheet.mergedCell, is(outSheet.mergedCell));
            
            assertThat(sheet.blank, is(outSheet.blank));
            
            
        }
        
    }
    
    /**
     * 書き込みのテスト - メソッドにアノテーションを付与
     * @since 1.0
     */
    @Test
    public void test_save_labelled_cell_methoAnno() throws Exception {
        
        // テストデータの作成
        final MethodAnnoSheet outSheet = new MethodAnnoSheet();
        
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_LabelledCell_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_LabelledCell_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = mapper.load(in, MethodAnnoSheet.class, errors);
            
            // 位置情報の指定
            assertThat(sheet.posRightPositon, is(outSheet.posRightPositon));
            assertThat(sheet.posLeftPosition, is(outSheet.posLeftPosition));
            assertThat(sheet.posBottomPosition, is(outSheet.posBottomPosition));
            assertThat(sheet.foundNoPosition, is(outSheet.foundNoPosition));
            assertThat(sheet.wrongFormatPosition, is(outSheet.wrongFormatPosition));
            assertThat(sheet.headerPosition, is(outSheet.headerPosition));
            assertThat(sheet.headerSkipPosition, is(outSheet.headerSkipPosition));
            assertThat(sheet.headerRangePosition, is(outSheet.headerRangePosition));
            assertThat(sheet.address1Position, is(outSheet.address1Position));
            assertThat(sheet.address2Position, is(outSheet.address2Position));
            assertThat(sheet.blankPosition, is(outSheet.blankPosition));
            
            
            // ラベル情報
            assertThat(sheet.posRightLabel, is(outSheet.posRightLabel));
            assertThat(sheet.posLeftLabel, is(outSheet.posLeftLabel));
            assertThat(sheet.posBottomLabel, is(outSheet.posBottomLabel));
            assertThat(sheet.foundNoLabel, is(outSheet.foundNoLabel));
            assertThat(sheet.wrongFormatLabel, is(outSheet.wrongFormatLabel));
            assertThat(sheet.headerLabel, is(outSheet.headerLabel));
            assertThat(sheet.headerSkipLabel, is(outSheet.headerSkipLabel));
            assertThat(sheet.headerRangeLabel, is(outSheet.headerRangeLabel));
            assertThat(sheet.address1, is(outSheet.address1));
            assertThat(sheet.address2, is(outSheet.address2));
            
            // 値
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
    
    /**
     * 書き込みのテスト - 正規表現、正規化によるテスト
     */
    @Test
    public void test_save_labelled_cell_regexl() throws Exception {
        
        // テストデータの作成
        final RegexSheet outSheet = new RegexSheet();
        
        outSheet.regexp("正規表現によるマッピング")
            .notRegexp("非正規表現によるマッピング")
            .normalize("正規化によるマッピング")
            .headerRegexp("見出しが正規化によるマッピング")
            ;
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);
        
        File outFile = new File("src/test/out/anno_LabelledCell_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_LabelledCell_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(RegexSheet.class);
            
            RegexSheet sheet = mapper.load(in, RegexSheet.class, errors);
            
            assertThat(sheet.positions, is(outSheet.positions));
            assertThat(sheet.labels, is(outSheet.labels));
            
            // 見出しの比較
            assertThat(sheet.labels.get("regexp"), is("見出し(1)"));
            assertThat(sheet.labels.get("notRegexp"), is("見出し(a)"));
            assertThat(sheet.labels.get("normalize"), is(" 更新\n日時 "));
            assertThat(sheet.labels.get("headerRegexp"), is("ラベル"));
            
            assertThat(sheet.normalize, is(outSheet.normalize));
            assertThat(sheet.notRegexp, is(outSheet.notRegexp));
            assertThat(sheet.normalize, is(outSheet.normalize));
            assertThat(sheet.headerRegexp, is(outSheet.headerRegexp));
            
            
        }
        
    }
    
    /**
     * 書き込みのテスト - 数式のテスト
     */
    @Test
    public void test_save_labelled_formula() throws Exception {
        
        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();
        
        outSheet.start(toUtilDate(toTimestamp("2016-05-22 00:00:00.000")))
            .end(toUtilDate(toTimestamp("2016-10-01 00:00:00.000")));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_LabelledCell_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_LabelledCell_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(FormulaSheet.class);
            
            FormulaSheet sheet = mapper.load(in, FormulaSheet.class, errors);
            
            assertThat(sheet.positions, is(outSheet.positions));
            assertThat(sheet.labels, is(outSheet.labels));
            
            assertThat(sheet.start, is(outSheet.start));
            assertThat(sheet.end, is(outSheet.end));
            assertThat(sheet.diff, is((int)TimeUnit.MILLISECONDS.toDays(outSheet.end.getTime() - outSheet.start.getTime())));
            
            
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
        @XlsLabelledCell(label="見つからない", type=LabelledCellType.Right, optional=true)
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
        
        /**
         * 結合しているセルの指定
         */
        @XlsLabelledCell(label="結合しているセル（ラベル名）", type=LabelledCellType.Right, skip=2)
        private String mergedCell;
        
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
        
        public NormalSheet mergedCell(String mergedCell) {
            this.mergedCell = mergedCell;
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
    
    /**
     * メソッドによるアノテーションの付与
     * @since 1.0
     *
     */
    @XlsSheet(name="LabelledCell(メソッドにアノテーションを付与)")
    private static class MethodAnnoSheet {
        
        /**
         * 位置のテスト - 右側
         */
        private String posRight;
        
        /**
         * 位置のテスト - 左側
         */
        private String posLeft;
        
        /**
         * 位置のテスト - 下側
         */
        private String posBottom;
        
        /**
         * ラベルが見つからない 
         */
        private Integer foundNo;
        
        /**
         * 不正なフォーマット
         */
        private Double wrongFormat;
        
        /**
         * ヘッダーラベル指定
         */
        private Date header;
        
        /**
         * ヘッダーラベル指定 - skip指定
         */
        private Date headerSkip;
        
        /**
         * ヘッダーラベル指定 - range指定
         */
        private Date headerRange;
        
        /**
         * アドレス指定 - labelAddress
         */
        private String address1;
        
        /**
         * アドレス指定 - labelColumn, labelRow
         */
        private String address2;
        
        /**
         * 値が空の場合
         */
        private String blank;
        
        @XlsLabelledCell(label="位置（右側）", type=LabelledCellType.Right)
        public String getPosRight() {
            return posRight;
        }
        
        @XlsLabelledCell(label="位置（右側）", type=LabelledCellType.Right)
        public void setPosRight(String posRight) {
            this.posRight = posRight;
        }
        
        @XlsLabelledCell(label="位置（左側）", type=LabelledCellType.Left)
        public String getPosLeft() {
            return posLeft;
        }
        
        @XlsLabelledCell(label="位置（左側）", type=LabelledCellType.Left)
        public void setPosLeft(String posLeft) {
            this.posLeft = posLeft;
        }
        
        @XlsLabelledCell(label="位置（下側）", type=LabelledCellType.Bottom)
        public String getPosBottom() {
            return posBottom;
        }
        
        @XlsLabelledCell(label="位置（下側）", type=LabelledCellType.Bottom)
        public void setPosBottom(String posBottom) {
            this.posBottom = posBottom;
        }
        
        @XlsLabelledCell(label="見つからない", type=LabelledCellType.Right, optional=true)
        public Integer getFoundNo() {
            return foundNo;
        }
        
        @XlsLabelledCell(label="見つからない", type=LabelledCellType.Right, optional=true)
        public void setFoundNo(Integer foundNo) {
            this.foundNo = foundNo;
        }
        
        @XlsLabelledCell(label="不正なフォーマット", type=LabelledCellType.Right)
        public Double getWrongFormat() {
            return wrongFormat;
        }
        
        @XlsLabelledCell(label="不正なフォーマット", type=LabelledCellType.Right)
        public void setWrongFormat(Double wrongFormat) {
            this.wrongFormat = wrongFormat;
        }
        
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Bottom, headerLabel="見出し１")
        public Date getHeader() {
            return header;
        }
        
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Bottom, headerLabel="見出し１")
        public void setHeader(Date header) {
            this.header = header;
        }
        
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Right, headerLabel="見出し２", skip=2)
        public Date getHeaderSkip() {
            return headerSkip;
        }
        
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Right, headerLabel="見出し２", skip=2)
        public void setHeaderSkip(Date headerSkip) {
            this.headerSkip = headerSkip;
        }
        
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Left, headerLabel="見出し３", range=2)
        public Date getHeaderRange() {
            return headerRange;
        }
        
        @XlsLabelledCell(label="ラベル名", type=LabelledCellType.Left, headerLabel="見出し３", range=2)
        public void setHeaderRange(Date headerRange) {
            this.headerRange = headerRange;
        }
        
        @XlsLabelledCell(labelAddress="B26", type=LabelledCellType.Right)
        public String getAddress1() {
            return address1;
        }
        
        @XlsLabelledCell(labelAddress="B26", type=LabelledCellType.Right)
        public void setAddress1(String address1) {
            this.address1 = address1;
        }
        
        @XlsLabelledCell(labelColumn=1, labelRow=25, type=LabelledCellType.Bottom)
        public String getAddress2() {
            return address2;
        }
        
        @XlsLabelledCell(labelColumn=1, labelRow=25, type=LabelledCellType.Bottom)
        public void setAddress2(String address2) {
            this.address2 = address2;
        }
        
        @XlsLabelledCell(label="値が空の場合", type=LabelledCellType.Right)
        public String getBlank() {
            return blank;
        }
        
        @XlsLabelledCell(label="値が空の場合", type=LabelledCellType.Right)
        public void setBlank(String blank) {
            this.blank = blank;
        }
        
        
        // 位置情報
        private Point posRightPositon;
        
        private Point posLeftPosition;
        
        private Point posBottomPosition;
        
        private Point foundNoPosition;
        
        private Point wrongFormatPosition;
        
        private Point headerPosition;
        
        private Point headerSkipPosition;
        
        private Point headerRangePosition;
        
        private Point address1Position;
        
        private Point address2Position;
        
        private Point blankPosition;
        
        public void setPosRightPositon(Point posRightPositon) {
            this.posRightPositon = posRightPositon;
        }
        
        public void setPosLeftPosition(Point posLeftPosition) {
            this.posLeftPosition = posLeftPosition;
        }
        
        public void setPosBottomPosition(Point posBottomPosition) {
            this.posBottomPosition = posBottomPosition;
        }
        
        public void setFoundNoPosition(Point foundNoPosition) {
            this.foundNoPosition = foundNoPosition;
        }
        
        public void setWrongFormatPosition(Point wrongFormatPosition) {
            this.wrongFormatPosition = wrongFormatPosition;
        }
        
        public void setHeaderPosition(Point headerPosition) {
            this.headerPosition = headerPosition;
        }
        
        public void setHeaderSkipPosition(Point headerSkipPosition) {
            this.headerSkipPosition = headerSkipPosition;
        }
        
        public void setHeaderRangePosition(Point headerRangePosition) {
            this.headerRangePosition = headerRangePosition;
        }
        
        public void setAddress1Position(Point address1Position) {
            this.address1Position = address1Position;
        }
        
        public void setAddress2Position(Point address2Position) {
            this.address2Position = address2Position;
        }
        
        public void setBlankPosition(Point blankPosition) {
            this.blankPosition = blankPosition;
        }
        
        // ラベル情報
        private String posRightLabel;
        
        private String posLeftLabel;
        
        private String posBottomLabel;
        
        private String foundNoLabel;
        
        private String wrongFormatLabel;
        
        private String headerLabel;
        
        private String headerSkipLabel;
        
        private String headerRangeLabel;
        
        private String address1Label;
        
        private String address2Label;
        
        private String blankLabel;
        
        public void setPosRightLabel(String posRightLabel) {
            this.posRightLabel = posRightLabel;
        }
        
        public void setPosLeftLabel(String posLeftLabel) {
            this.posLeftLabel = posLeftLabel;
        }
        
        public void setPosBottomLabel(String posBottomLabel) {
            this.posBottomLabel = posBottomLabel;
        }
        
        public void setFoundNoLabel(String foundNoLabel) {
            this.foundNoLabel = foundNoLabel;
        }
        
        public void setWrongFormatLabel(String wrongFormatLabel) {
            this.wrongFormatLabel = wrongFormatLabel;
        }
        
        public void setHeaderLabel(String headerLabel) {
            this.headerLabel = headerLabel;
        }
        
        public void setHeaderSkipLabel(String headerSkipLabel) {
            this.headerSkipLabel = headerSkipLabel;
        }
        
        public void setHeaderRangeLabel(String headerRangeLabel) {
            this.headerRangeLabel = headerRangeLabel;
        }
        
        public void setAddress1Label(String address1Label) {
            this.address1Label = address1Label;
        }
        
        public void setAddress2Label(String address2Label) {
            this.address2Label = address2Label;
        }
        
        public void setBlankLabel(String blankLabel) {
            this.blankLabel = blankLabel;
        }

        
        //// 値の設定用のメソッド
        
        public MethodAnnoSheet posRight(String posRight) {
            this.posRight = posRight;
            return this;
        }
        
        public MethodAnnoSheet posLeft(String posLeft) {
            this.posLeft = posLeft;
            return this;
        }
        
        public MethodAnnoSheet posBottom(String posBottom) {
            this.posBottom = posBottom;
            return this;
        }
        
        public MethodAnnoSheet foundNo(Integer foundNo) {
            this.foundNo = foundNo;
            return this;
        }
        
        public MethodAnnoSheet wrongFormat(Double wrongFormat) {
            this.wrongFormat = wrongFormat;
            return this;
        }
        
        public MethodAnnoSheet header(Date header) {
            this.header = header;
            return this;
        }
        
        public MethodAnnoSheet headerSkip(Date headerSkip) {
            this.headerSkip = headerSkip;
            return this;
        }
        
        public MethodAnnoSheet headerRange(Date headerRange) {
            this.headerRange = headerRange;
            return this;
        }
        
        public MethodAnnoSheet address1(String address1) {
            this.address1 = address1;
            return this;
        }
        
        public MethodAnnoSheet address2(String address2) {
            this.address2 = address2;
            return this;
        }
        
        public MethodAnnoSheet blank(String blank) {
            this.blank = blank;
            return this;
        }
    }
    
    /**
     * 正規表現や正規化によるマッピング
     *
     */
    @XlsSheet(name="正規表現で一致")
    private static class RegexSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        /** 正規表現によるマッピング */
        @XlsLabelledCell(label="/見出し\\([0-9]+\\)/", type=LabelledCellType.Right)
        private String regexp;
        
        @XlsLabelledCell(label="見出し(a)", type=LabelledCellType.Right)
        private String notRegexp;
        
        /** 正規化による空白などの削除 */
        @XlsLabelledCell(label="更新日時", type=LabelledCellType.Bottom)
        private String normalize;
        
        @XlsLabelledCell(headerLabel="/ヘッダー.*/", label="ラベル", type=LabelledCellType.Right)
        private String headerRegexp;
        
        public RegexSheet regexp(String regexp) {
            this.regexp = regexp;
            return this;
        }
        
        public RegexSheet notRegexp(String notRegexp) {
            this.notRegexp = notRegexp;
            return this;
        }
        
        public RegexSheet normalize(String normalize) {
            this.normalize = normalize;
            return this;
        }
        
        public RegexSheet headerRegexp(String headerRegexp) {
            this.headerRegexp = headerRegexp;
            return this;
        }
        
    }
    
    @XlsSheet(name="数式を指定")
    private static class FormulaSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsHint(order=1)
        @XlsLabelledCell(label="開始日", type=LabelledCellType.Bottom)
        private Date start;
        
        @XlsHint(order=2)
        @XlsLabelledCell(label="終了日", type=LabelledCellType.Bottom)
        private Date end;
        
        @XlsHint(order=3)
        @XlsLabelledCell(label="差", type=LabelledCellType.Bottom)
        @XlsFormula(value="C5-B5")
        private Integer diff;
        
        public FormulaSheet start(Date start) {
            this.start = start;
            return this;
        }
        
        public FormulaSheet end(Date end) {
            this.end = end;
            return this;
        }
        
    }

}
