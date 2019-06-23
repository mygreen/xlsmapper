package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.CellFormatter;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.cellconverter.ConversionException;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * アノテーション{@link XlsFormula}のテスタ。
 * <p>このテスタは、式の解析の確認などのテストを行う。</p>
 * <p>プロセッサとの組み合わせは、それぞれのテスタで行う。</p>
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class AnnoFormulaTest {

    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_Formula_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_Formula_out.xlsx";

    /**
     * 正常 - 式を直接指定
     */
    @Test
    public void test_normal_formula() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("value", "SUM(C2:${x:colToAlpha(columnNumber+2)}3)")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            Workbook book = WorkbookFactory.create(in);
            Sheet sheet = book.getSheet("Formula(通常)");

            Cell cell = POIUtils.getCell(sheet, CellPosition.of("A2"));

            String formula = cell.getCellFormula();
            CellFormatter formatter = mapper.getConfiguration().getCellFormatter();
            String value = formatter.format(cell);

            assertThat(formula, is("SUM(C2:C3)"));
            assertThat(value, is("17.468"));

        }

    }

    /**
     * 正常 - メソッドで式を組み立て
     */
    @Test
    public void test_normal_method() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("methodName", "getC1Formula")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);;

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            Workbook book = WorkbookFactory.create(in);
            Sheet sheet = book.getSheet("Formula(通常)");

            Cell cell = POIUtils.getCell(sheet, CellPosition.of("A2"));

            String formula = cell.getCellFormula();
            CellFormatter formatter = mapper.getConfiguration().getCellFormatter();
            String value = formatter.format(cell);

            assertThat(formula, is("SUM(D2:D3)"));
            assertThat(value, is("579"));

        }

    }

    /**
     * 正常 - 数式を優先
     */
    @Test
    public void test_normal_primay() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        outSheet.c1(12.345d);

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("value", "SUM(C2:C3)")
                                        .attribute("primary", true)
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);;

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            Workbook book = WorkbookFactory.create(in);
            Sheet sheet = book.getSheet("Formula(通常)");

            Cell cell = POIUtils.getCell(sheet, CellPosition.of("A2"));
            assertThat(cell.getCellTypeEnum(), is(CellType.FORMULA));

            String formula = cell.getCellFormula();
            CellFormatter formatter = mapper.getConfiguration().getCellFormatter();
            String value = formatter.format(cell);

            assertThat(formula, is("SUM(C2:C3)"));
            assertThat(value, is("17.468"));

        }

    }

    /**
     * 正常 - 値を優先
     */
    @Test
    public void test_normal_not_primay() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        outSheet.c1(12.345d);

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("value", "SUM(C2:C3)")
                                        .attribute("primary", false)
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);;

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            Workbook book = WorkbookFactory.create(in);
            Sheet sheet = book.getSheet("Formula(通常)");

            Cell cell = POIUtils.getCell(sheet, CellPosition.of("A2"));
            assertThat(cell.getCellTypeEnum(), is(CellType.NUMERIC));

            CellFormatter formatter = mapper.getConfiguration().getCellFormatter();
            String value = formatter.format(cell);

            assertThat(value, is("12.345"));

        }

    }

    /**
     * 正常 - 空の数式を返す場合
     */
    @Test
    public void test_normal_empty_formula() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("methodName", "getEmptyFormula")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            Workbook book = WorkbookFactory.create(in);
            Sheet sheet = book.getSheet("Formula(通常)");

            Cell cell = POIUtils.getCell(sheet, CellPosition.of("A2"));
            assertThat(cell.getCellTypeEnum(), is(CellType.BLANK));

            CellFormatter formatter = mapper.getConfiguration().getCellFormatter();
            String value = formatter.format(cell);

            assertThat(value, is(""));

        }

    }

    /**
     * 式やメソッドが設定されていない場合
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_error_empty() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);;

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        fail();

    }

    /**
     * 式が不正 - 式言語として不正
     */
    @Test(expected=ConversionException.class)
    public void test_error_wrongFormula_forExpression() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("value", "{test} ${hoge}")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        fail();

    }

    /**
     * 式が不正 - Excelの数式として不正
     */
    @Test(expected=ConversionException.class)
    public void test_error_wrongFormula_forExcel() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("value", ")1+TESTA1)")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        fail();

    }

    /**
     * 指定したメソッドが見つからない
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_error_notFoundMethod() throws Exception {

        // テストデータの作成
        final FormulaSheet outSheet = new FormulaSheet();

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(FormulaSheet.class)
                        .field(createField("c1")
                                .override(true)
                                .annotation(createAnnotation(XlsFormula.class)
                                        .attribute("methodName", "hoge")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();


        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setAnnotationMapping(xmlInfo);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        fail();

    }

    /**
     * サンプル
     */
    @Test
    public void test_normal_sample() throws Exception {

        // テストデータの作成
        final SampleSheet outSheet = new SampleSheet();

        // 各人のレコード（合計値の設定は行わない。）
        outSheet.add(new SampleRecord().name("山田太郎").kokugo(90).sansu(85));
        outSheet.add(new SampleRecord().name("鈴木一郎").kokugo(85).sansu(80));
        outSheet.add(new SampleRecord().name("林三郎").kokugo(80).sansu(60));

        // 平均値用のレコード(点数などのデータ部分はなし)
        outSheet.add(new SampleRecord().name("平均"));

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

    }

    @XlsSheet(name="Formula(通常)")
    private static class FormulaSheet {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsCell(address="A2")
        @XlsFormula(value="1+2")
        private Double c1;

        private String getC1Formula(final Sheet sheet, final Cell cell, final Point point, final Configuration config, final Object object) {

            assertThat(sheet, is(notNullValue()));
            assertThat(CellPosition.of(cell).formatAsString(), is("A2"));
            assertThat(CellPosition.of(point).formatAsString(), is("A2"));
            assertThat(config, is(notNullValue()));
            assertThat(object, is(nullValue()));

            return "SUM(D2:D3)";
        }

        public FormulaSheet c1(Double c1) {
            this.c1 = c1;
            return this;
        }

        /**
         * 空の数式を返す。
         * @param point
         * @return
         */
        private String getEmptyFormula(final Point point) {
            return "";
        }
    }

    @XlsSheet(name="Formula(サンプル)")
    private static class SampleSheet {

        // マッピングした位置情報
        private Map<String, Point> positions;

        @XlsHorizontalRecords(tableLabel="成績一覧", bottom=2, terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SampleRecord> records;

        // レコードを追加する
        public void add(SampleRecord record) {
            if(records == null) {
                this.records = new ArrayList<>();
            }

            // 自身のインスタンスを渡す
            record.setParent(this);

            // No.を自動的に振る
            record.setNo(records.size()+1);

            this.records.add(record);
        }

        public List<SampleRecord> getRecords() {
            return records;
        }

    }

    private static class SampleRecord {
        // マッピングした位置情報
        private Map<String, Point> positions;

        // 親のBean情報
        private SampleSheet parent;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="名前")
        private String name;

        @XlsColumn(columnName="国語")
        @XlsFormula(methodName="getKyokaAvgFormula", primary=false)
        private Integer kokugo;

        @XlsColumn(columnName="算数")
        @XlsFormula(methodName="getKyokaAvgFormula", primary=false)
        private Integer sansu;

        @XlsColumn(columnName="合計")
        @XlsFormula(value="SUM(C{rowNumber}:D{rowNumber})", primary=true)
        private Integer sum;

        // 各教科の平均の数式を組み立てる
        public String getKyokaAvgFormula(Point point) {

            // レコード名が平均のときのみ数式を出力する
            if(!name.equals("平均")) {
                return null;
            }

            // レコードのサイズ（平均用のレコード行を覗いた値）
            final int dataSize = parent.getRecords().size() -1;

            // 列名
            final String colAlpha = CellReference.convertNumToColString(point.x);

            // 平均値の開始/終了の行番号
            final int startRowNumber = point.y - dataSize+1;
            final int endRowNumber = point.y;

            return String.format("AVERAGE(%s%d:%s%d)", colAlpha, startRowNumber, colAlpha, endRowNumber);
        }

        // 最後のレコードのときにセルの色を変更
        @XlsPostSave
        public void handlePostSave(final Sheet sheet) {

            if(!name.equals("平均")) {
                return;
            }

            final Workbook book = sheet.getWorkbook();

            for(Point address : positions.values()) {
                Cell cell = POIUtils.getCell(sheet, address);

                CellStyle style = book.createCellStyle();
                style.cloneStyleFrom(cell.getCellStyle());

                // 塗りつぶし
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // 罫線の上部を変更
                style.setBorderTop(BorderStyle.DOUBLE);

                cell.setCellStyle(style);
            }

        }

        public void setParent(SampleSheet parent) {
            this.parent = parent;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public SampleRecord name(final String name) {
            this.name = name;
            return this;
        }

        public SampleRecord kokugo(final Integer kokugo) {
            this.kokugo = kokugo;
            return this;
        }

        public SampleRecord sansu(final Integer sansu) {
            this.sansu = sansu;
            return this;
        }
    }


}
