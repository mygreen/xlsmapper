package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.util.CellReference;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsNumberConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.CellProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link CellProcessor}のテスタ。
 * アノテーション{@link XlsCell}のテスタ。
 * 
 * @version 2.1
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoCellTest {
    
    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }
    
    /**
     * 読み込み用のファイルの定義
     */
    private File inputFile = new File("src/test/data/anno_Cell.xlsx");
    
    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_Cell_template.xlsx");
    
    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_Cell_out.xlsx";
    
    /**
     * 読み込みのテスト - 通常のデータ
     */
    @Test
    public void test_load_cell_normal() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);
            
            NormalSheet sheet = errors.getTarget();
            
            assertThat(sheet.c1,is("文字列です。\n改行あり。"));
            assertThat(sheet.c2,is(12.345));
            assertThat(sheet.c3,is(toUtilDate(toTimestamp("2015-05-09 14:20:00.000"))));
            assertThat(cellFieldError(errors, cellAddress(sheet.positions.get("c4"))).isConversionFailure(), is(true));
            
        }
    }
    
    /**
     * 読み込みのテスト - バインドエラー
     */
    @Test
    public void test_load_cell_bind_error() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            assertThatThrownBy(() -> mapper.load(in, NormalSheet.class))
                .isInstanceOf(TypeBindException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoCellTest$NormalSheet#c4'において、セル(D12)の値'abc'を'java.lang.Integer'に変換できませんでした。");
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - インデックスが範囲外
     */
    @Test
    public void test_load_cell_invalid_annotation1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            assertThatThrownBy(() -> mapper.load(in, InvalidAnno1Sheet1.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoCellTest$InvalidAnno1Sheet1#c1'において、アノテーション'@XlsCell'の属性'row'の値'-1'は、0以上の値を設定してください。");
            
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - アドレスが不正
     */
    @Test
    public void test_load_cell_invalid_annotation2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            assertThatThrownBy(() -> mapper.load(in, InvalidAnnoSheet2.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoCellTest$InvalidAnnoSheet2#c1'において、アノテーション'@XlsCell'の属性'address'の値'あいう'は、セルのアドレスの書式として不正です。");
            
        }
    }
    
    /**
     * 読み込みのテスト - メソッドにアノテーションを付与
     * @since 1.0
     */
    @Test
    public void test_load_cell_methodAnno() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<MethodAnnoSheet> errors = mapper.loadDetail(in, MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = errors.getTarget();
            
            assertThat(sheet.c1,is("文字列です。\n改行あり。"));
            assertThat(sheet.c2,is(12.345));
            assertThat(sheet.c3,is(toUtilDate(toTimestamp("2015-05-09 14:20:00.000"))));
            assertThat(cellFieldError(errors, cellAddress(sheet.c4Position)).isConversionFailure(), is(true));
            
        }
    }
    
    /**
     * 読み込みのテスト - 式を指定
     * @since 1.5
     */
    @Test
    public void test_load_cell_formula() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<FormulaSheet> errors = mapper.loadDetail(in, FormulaSheet.class);
            
            FormulaSheet sheet = errors.getTarget();
            
            assertThat(sheet.c1,is("ABCDEFG"));
            assertThat(sheet.c2,is(135.144d));
            assertThat(sheet.c3,is(toUtilDate(toTimestamp("1900-01-07 20:00:00.000"))));
            assertThat(sheet.c4, is(6));
            
        }
    }    
    
    /**
     * 読み込みのテスト - コメント情報
     * @since 2.1
     */
    @Test
    public void test_load_cell_comment() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<CommentSheet> errors = mapper.loadDetail(in, CommentSheet.class);

            CommentSheet sheet = errors.getTarget();

            assertThat(sheet.name, is("山田太郎"));
            assertThat(sheet.comments, hasEntry("name", "氏名を入力してください。"));

        }
        
    }
    
    /**
     * 書き込みのテスト - 通常のデータ
     */
    @Test
    public void test_save_cell_normal() throws Exception {
        
        // テストデータの作成
        final NormalSheet outSheet = new NormalSheet();
        
        outSheet.c1("文字列です。改行あり")
                .c2(12.345)
                .c3(toUtilDate(toTimestamp("2015-06-06 10:12:13.000")))
                .c4(-12345);
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);
            
            NormalSheet sheet = errors.getTarget();
            
            assertThat(sheet.positions, is(outSheet.positions));
            assertThat(sheet.labels, is(outSheet.labels));
            
            assertThat(sheet.c1, is(outSheet.c1));
            assertThat(sheet.c2, is(outSheet.c2));
            assertThat(sheet.c3, is(outSheet.c3));
            assertThat(sheet.c4, is(outSheet.c4));
            
        }
        
    }
    
    
    /**
     * 書き込みのテスト - メソッドにアノテーションを付与
     * @since 1.0
     */
    @Test
    public void test_save_cell_methodAnno() throws Exception {
        
        // テストデータの作成
        final MethodAnnoSheet outSheet = new MethodAnnoSheet();
        
        outSheet.c1("文字列です。改行あり")
                .c2(12.345)
                .c3(toUtilDate(toTimestamp("2015-06-06 10:12:13.000")))
                .c4(-12345);
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<MethodAnnoSheet> errors = mapper.loadDetail(in, MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = errors.getTarget();
            
            assertThat(sheet.c1Position, is(outSheet.c1Position));
            assertThat(sheet.c2Position, is(outSheet.c2Position));
            assertThat(sheet.c3Position, is(outSheet.c3Position));
            assertThat(sheet.c4Position, is(outSheet.c4Position));
            
            assertThat(sheet.c1Label, is(outSheet.c1Label));
            assertThat(sheet.c2Label, is(outSheet.c2Label));
            assertThat(sheet.c3Label, is(outSheet.c3Label));
            assertThat(sheet.c4Label, is(outSheet.c4Label));
            
            assertThat(sheet.c1, is(outSheet.c1));
            assertThat(sheet.c2, is(outSheet.c2));
            assertThat(sheet.c3, is(outSheet.c3));
            assertThat(sheet.c4, is(outSheet.c4));
            
        }
        
    }
    
    /**
     * 書き込みのテスト - 式を定義
     * @since 1.5
     */
    @Test
    public void test_save_formula() throws Exception {
        
        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();
        
        outSheet.c3(toUtilDate(toTimestamp("2015-06-06 10:12:13.000")))
                .c4(1234);
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<FormulaSheet> errors = mapper.loadDetail(in, FormulaSheet.class);
            
            FormulaSheet sheet = errors.getTarget();
            
            assertThat(sheet.positions, is(outSheet.positions));
            assertThat(sheet.labels, is(outSheet.labels));
            
            assertThat(sheet.c1,is("ABCDEFG"));
            assertThat(sheet.c2,is(135.144d));
            assertThat(sheet.c3, is(toUtilDate(toTimestamp("1900-01-07 20:00:00.000"))));
            assertThat(sheet.c4, is(1234));
            
        }
        
    }
    
    /**
     * 書込みのテスト - コメント情報
     * @since 2.1
     */
    @Test
    public void test_save_cell_comment() throws Exception {
        
        // テストデータの作成
        final CommentSheet outSheet = new CommentSheet();
        
        outSheet.name("山田太郎")
            .comment("name", "氏名を入力してください。");
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<CommentSheet> errors = mapper.loadDetail(in, CommentSheet.class);

            CommentSheet sheet = errors.getTarget();

            assertThat(sheet.name, is(outSheet.name));
            assertThat(sheet.comments, hasEntry("name", outSheet.comments.get("name")));
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
        
        public NormalSheet c1(String c1) {
            this.c1 = c1;
            return this;
        }
        
        public NormalSheet c2(Double c2) {
            this.c2 = c2;
            return this;
        }
        
        public NormalSheet c3(Date c3) {
            this.c3 = c3;
            return this;
        }
        
        public NormalSheet c4(Integer c4) {
            this.c4 = c4;
            return this;
        }
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
    
    /**
     * メソッドにアノテーションを付与
     * @since 1.0
     *
     */
    @XlsSheet(name="Cell(メソッドにアノテーションを付与)")
    private static class MethodAnnoSheet {
        
        /**
         * インデックス指定
         */
        private String c1;
        
        /**
         * アドレス指定
         */
        private Double c2;
        
        /**
         * 不正なフォーマット
         */
        private Integer c4;
        
        /**
         * インデックス指定+アドレス指定
         */
        private Date c3;
        
        private Point c1Position;
        
        private Point c2Position;
        
        private Point c3Position;
        
        private Point c4Position;
        
        private String c1Label;
        
        private String c2Label;
        
        private String c3Label;
        
        private String c4Label;
        
        @XlsCell(column=1, row=3)
        public String getC1() {
            return c1;
        }
        
        public void setC1(String c1) {
            this.c1 = c1;
        }
        
        @XlsCell(address="C7")
        public Double getC2() {
            return c2;
        }
        
        public void setC2(Double c2) {
            this.c2 = c2;
        }
        
        @XlsCell(column=0, row=0, address="B10")
        public Date getC3() {
            return c3;
        }
        
        public void setC3(Date c3) {
            this.c3 = c3;
        }
        
        @XlsCell(address="D12")
        public Integer getC4() {
            return c4;
        }
        
        public void setC4(Integer c4) {
            this.c4 = c4;
        }
        
        public void setC1Position(int x, int y) {
            this.c1Position = new Point(x, y);
        }
        
        public void setC2Position(int x, int y) {
            this.c2Position = new Point(x, y);
        }
        
        public void setC3Position(int x, int y) {
            this.c3Position = new Point(x, y);
        }
        
        public void setC4Position(int x, int y) {
            this.c4Position = new Point(x, y);
        }
        
        public MethodAnnoSheet c1(String c1) {
            this.c1 = c1;
            return this;
        }
        
        public MethodAnnoSheet c2(Double c2) {
            this.c2 = c2;
            return this;
        }
        
        public MethodAnnoSheet c3(Date c3) {
            this.c3 = c3;
            return this;
        }
        
        public MethodAnnoSheet c4(Integer c4) {
            this.c4 = c4;
            return this;
        }
        
        public void setC1Label(String c1Label) {
            this.c1Label = c1Label;
        }
        
        public void setC2Label(String c2Label) {
            this.c2Label = c2Label;
        }
        
        public void setC3Label(String c3Label) {
            this.c3Label = c3Label;
        }
        
        public void setC4Label(String c4Label) {
            this.c4Label = c4Label;
        }
    }
    
    @XlsSheet(name="数式を指定")
    private static class FormulaSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        /**
         * 数式の指定
         */
        @XlsCell(address="B4")
        @XlsFormula("UPPER(F4)")
        private String c1;
        
        /**
         * メソッドで指定
         */
        @XlsCell(address="C7")
        @XlsFormula(methodName="getC2Formula")
        @XlsNumberConverter(excelPattern="0.000;\"▲ \"0.000")
        private Double c2;
        
        /**
         * 優先 = true
         */
        @XlsCell(address="B10")
        @XlsFormula(value="F10-G10", primary=true)
        private Date c3;
        
        /**
         * 優先 = false
         */
        @XlsCell(address="D12")
        @XlsFormula(value="F12+G12", primary=false)
        private Integer c4;
        
        public FormulaSheet c1(String c1) {
            this.c1 = c1;
            return this;
        }
        
        public FormulaSheet c2(Double c2) {
            this.c2 = c2;
            return this;
        }
        
        public FormulaSheet c3(Date c3) {
            this.c3 = c3;
            return this;
        }
        
        public FormulaSheet c4(Integer c4) {
            this.c4 = c4;
            return this;
        }
        
        private String getC2Formula(final Point address) {
            
            String formula = String.format("SUM(%s%s:%s%s)",
                    CellReference.convertNumToColString(address.x + 3),
                    address.y + 1,
                    CellReference.convertNumToColString(address.x + 6),
                    address.y + 1);
            
            return formula;
        }
    }
    
    @XlsSheet(name="コメント情報")
    private static class CommentSheet {
        
        private Map<String, Point> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsCell(address = "B5")
        private String name;
        
        public CommentSheet name(String name) {
            this.name = name;
            return this;
        }

        public CommentSheet comment(String key, String text) {
            if(comments == null) {
                this.comments = new HashMap<String, String>();
            }
            this.comments.put(key, text);
            return this;
        }
    }
}
