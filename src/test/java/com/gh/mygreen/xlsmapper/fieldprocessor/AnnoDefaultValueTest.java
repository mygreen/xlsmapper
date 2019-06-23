package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
import static org.assertj.core.api.Assertions.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * アノテーション{@link XlsDefaultValue}のテスタ。
 * <p>このテスタは、初期値の基本的な処理を確認するためのテスタです。
 *  <br>そのため、各クラスタイプの処理は、それぞれのコンバータのテスタで確認してください。
 * </p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AnnoDefaultValueTest {

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
    private File inputFile = new File("src/test/data/anno_DefaultValue.xlsx");

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_DefaultValue_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_DefaultValue_out.xlsx";

    /**
     * 初期値の通常の読み込みのテスト
     */
    @Test
    public void test_load_normal() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalValueSheet> errors = mapper.loadDetail(in, NormalValueSheet.class);
            NormalValueSheet sheet = errors.getTarget();

            assertThat(sheet.existWithDefault).isEqualTo("あいう");
            assertThat(sheet.blank).isNull();
            assertThat(sheet.blankWithDefault).isEqualTo("  初期値  ");
            assertThat(sheet.blankWithDefaultTrim).isEqualTo("初期値");
            assertThat(sheet.blankWithDefaultFormat).isEqualTo(LocalDate.of(2017, 8, 20));
            assertThat(sheet.blankWithLoadCase).isEqualTo("初期値");
            assertThat(sheet.blankWithSaveCase).isNull();


        }

    }

    /**
     * 初期値のフォーマットが不正なときのテスト
     */
    @Test
    public void test_load_invalidFormat() throws Exception {

        // アノテーションの組み立て
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(NormalValueSheet.class)
                        .field(createField("blankWithDefaultFormat")
                                .override(true)
                                .annotation(createAnnotation(XlsDefaultValue.class)
                                        .attribute("value", "abc")
                                        .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        try(InputStream in = new FileInputStream(inputFile)) {

            assertThatThrownBy(() -> mapper.loadDetail(in, NormalValueSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoDefaultValueTest$NormalValueSheet#blankWithDefaultFormat'において、アノテーション'@XlsDefaultValue'の値'abc'を'java.time.LocalDate'に変換でませんでした。");


        }

    }

    /**
     * 初期値の通常の書き込み
     */
    @Test
    public void test_save_normal() throws Exception {

        // データ作成
        NormalValueSheet outSheet = new NormalValueSheet();
        outSheet.existWithDefault = "あいう";

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

            Sheet sheet = WorkbookFactory.create(in).getSheet("通常のテスト");
            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C4"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo("あいう");
            }

            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C5"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo("");
            }

            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C6"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo("  初期値  ");
            }

            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C7"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo("初期値");
            }

            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C8"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo("2017-08-20");
            }

            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C9"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEmpty();
            }

            {
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C10"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo("初期値");
            }

        }

    }

    @XlsSheet(name="通常のテスト")
    private static class NormalValueSheet {

        private Map<String, Point> positions;

        @XlsDefaultValue(value="初期値")
        @XlsLabelledCell(label="値があるセル（初期値設定あり）", type=LabelledCellType.Right)
        private String existWithDefault;

        @XlsLabelledCell(label="空のセル", type=LabelledCellType.Right)
        private String blank;

        @XlsDefaultValue(value="  初期値  ")
        @XlsLabelledCell(label="空のセル（初期値設定あり）", type=LabelledCellType.Right)
        private String blankWithDefault;

        @XlsDefaultValue(value="  初期値  ")
        @XlsTrim
        @XlsLabelledCell(label="空のセル（初期値設定あり）(トリム指定)", type=LabelledCellType.Right)
        private String blankWithDefaultTrim;

        @XlsDefaultValue(value="2017-08-20")
        @XlsLabelledCell(label="空のセル（初期値設定あり）(書式指定あり)", type=LabelledCellType.Right)
        @XlsDateTimeConverter(javaPattern="uuuu-MM-dd")
        private LocalDate blankWithDefaultFormat;

        @XlsDefaultValue(value="初期値", cases=ProcessCase.Load)
        @XlsLabelledCell(label="空のセル（初期値設定あり）(読み込み時)", type=LabelledCellType.Right)
        private String blankWithLoadCase;

        @XlsDefaultValue(value="初期値", cases=ProcessCase.Save)
        @XlsLabelledCell(label="空のセル（初期値設定あり）(書き込み時)", type=LabelledCellType.Right)
        private String blankWithSaveCase;


    }

}
