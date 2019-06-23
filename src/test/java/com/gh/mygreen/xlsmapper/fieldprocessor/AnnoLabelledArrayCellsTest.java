package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.ArrayDirection;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledArrayCellsProcessor;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;


/**
 * {@link LabelledArrayCellsProcessor}のテスタ。
 * アノテーション{@link XlsLabelledArrayCells}のテスタ。
 *
 * @version 2.1
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AnnoLabelledArrayCellsTest {


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
    private File inputFile = new File("src/test/data/anno_LabelledArrayCells.xlsx");

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_LabelledArrayCells_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_LabelledArrayCells_out.xlsx";

    /**
     * 読み込みテスト - 通常のテスト
     */
    @Test
    public void test_load_labelled_array_cell_normal() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);

            NormalSheet sheet = errors.getTarget();

            {
                // 右側＋水平
                assertThat(sheet.rightHorizon)
                    .hasSize(5)
                    .containsExactly("あ", "い", "う", "え", "お");

                assertThat(sheet.positions)
                    .containsEntry("rightHorizon[0]", CellPosition.of("C4"))
                    .containsEntry("rightHorizon[1]", CellPosition.of("D4"))
                    .containsEntry("rightHorizon[2]", CellPosition.of("E4"))
                    .containsEntry("rightHorizon[3]", CellPosition.of("F4"))
                    .containsEntry("rightHorizon[4]", CellPosition.of("G4"))
                    ;

                String label = "右側＋水平";
                assertThat(sheet.labels)
                    .containsEntry("rightHorizon", label)
                    .containsEntry("rightHorizon[0]", label)
                    .containsEntry("rightHorizon[1]", label)
                    .containsEntry("rightHorizon[2]", label)
                    .containsEntry("rightHorizon[3]", label)
                    .containsEntry("rightHorizon[4]", label)
                    ;
            }

            {
                // 下側＋水平
                assertThat(sheet.bottomHorizon)
                    .hasSize(5)
                    .containsExactly("あ", "い", "う", "え", "お");

                assertThat(sheet.positions)
                    .containsEntry("bottomHorizon[0]", CellPosition.of("B7"))
                    .containsEntry("bottomHorizon[1]", CellPosition.of("C7"))
                    .containsEntry("bottomHorizon[2]", CellPosition.of("D7"))
                    .containsEntry("bottomHorizon[3]", CellPosition.of("E7"))
                    .containsEntry("bottomHorizon[4]", CellPosition.of("F7"))
                    ;

                String label = "下側＋水平";
                assertThat(sheet.labels)
                    .containsEntry("bottomHorizon", label)
                    .containsEntry("bottomHorizon[0]", label)
                    .containsEntry("bottomHorizon[1]", label)
                    .containsEntry("bottomHorizon[2]", label)
                    .containsEntry("bottomHorizon[3]", label)
                    .containsEntry("bottomHorizon[4]", label)
                    ;
            }

            {
                // 右側+垂直
                assertThat(sheet.rightVertical)
                    .hasSize(3)
                    .containsExactly(LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2), LocalDate.of(2017, 4, 3));

                assertThat(sheet.positions)
                    .containsEntry("rightVertical[0]", CellPosition.of("C10"))
                    .containsEntry("rightVertical[1]", CellPosition.of("C11"))
                    .containsEntry("rightVertical[2]", CellPosition.of("C12"))
                    ;

                String label = "右側＋垂直";
                assertThat(sheet.labels)
                    .containsEntry("rightVertical", label)
                    .containsEntry("rightVertical[0]", label)
                    .containsEntry("rightVertical[1]", label)
                    .containsEntry("rightVertical[2]", label)
                    ;

            }

            {
                // 左側+垂直
                assertThat(sheet.leftVertical)
                    .hasSize(3)
                    .containsExactly(LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2), LocalDate.of(2017, 4, 3));

                assertThat(sheet.positions)
                    .containsEntry("leftVertical[0]", CellPosition.of("E10"))
                    .containsEntry("leftVertical[1]", CellPosition.of("E11"))
                    .containsEntry("leftVertical[2]", CellPosition.of("E12"))
                    ;

                String label = "左側＋垂直";
                assertThat(sheet.labels)
                    .containsEntry("leftVertical", label)
                    .containsEntry("leftVertical[0]", label)
                    .containsEntry("leftVertical[1]", label)
                    .containsEntry("leftVertical[2]", label)
                    ;

            }

            {
                // 下側+垂直
                assertThat(sheet.bottomVertical)
                    .hasSize(3)
                    .containsExactly(LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2), LocalDate.of(2017, 4, 3));

                assertThat(sheet.positions)
                    .containsEntry("bottomVertical[0]", CellPosition.of("H11"))
                    .containsEntry("bottomVertical[1]", CellPosition.of("H12"))
                    .containsEntry("bottomVertical[2]", CellPosition.of("H13"))
                    ;

                String label = "下側＋垂直";
                assertThat(sheet.labels)
                    .containsEntry("bottomVertical", label)
                    .containsEntry("bottomVertical[0]", label)
                    .containsEntry("bottomVertical[1]", label)
                    .containsEntry("bottomVertical[2]", label)
                    ;
            }

            {
                // 見出し+水平
                assertThat(sheet.headerHorizon)
                    .hasSize(5)
                    .containsExactly("か", "き", "く", "け", "こ");

                assertThat(sheet.positions)
                    .containsEntry("headerHorizon[0]", CellPosition.of("B18"))
                    .containsEntry("headerHorizon[1]", CellPosition.of("C18"))
                    .containsEntry("headerHorizon[2]", CellPosition.of("D18"))
                    .containsEntry("headerHorizon[3]", CellPosition.of("E18"))
                    .containsEntry("headerHorizon[4]", CellPosition.of("F18"))
                    ;

                String label = "ラベル名";
                assertThat(sheet.labels)
                    .containsEntry("headerHorizon", label)
                    .containsEntry("headerHorizon[0]", label)
                    .containsEntry("headerHorizon[1]", label)
                    .containsEntry("headerHorizon[2]", label)
                    .containsEntry("headerHorizon[3]", label)
                    .containsEntry("headerHorizon[4]", label)
                    ;
            }

            {
                // 見出し+垂直
                assertThat(sheet.headerVertical)
                    .hasSize(3)
                    .containsExactly(LocalDate.of(2017, 5, 1), LocalDate.of(2017, 5, 2), LocalDate.of(2017, 5, 3));

                assertThat(sheet.positions)
                    .containsEntry("headerVertical[0]", CellPosition.of("H18"))
                    .containsEntry("headerVertical[1]", CellPosition.of("H19"))
                    .containsEntry("headerVertical[2]", CellPosition.of("H20"))
                    ;

                String label = "ラベル名";
                assertThat(sheet.labels)
                    .containsEntry("headerVertical", label)
                    .containsEntry("headerVertical[0]", label)
                    .containsEntry("headerVertical[1]", label)
                    .containsEntry("headerVertical[2]", label)
                    ;
            }
        }
    }

    /**
     * 読み込みのテスト - 不正なアノテーション - サポートしていないタイプの場合
     */
    @Test
    public void test_load_labelled_array_cell_invalidAnno_notSupportType() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false);

        // アノテーションの変更 - タイプが不正
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(InvalidAnnoSheet.class)
                        .field(createField("field2")
                                .annotation(createAnnotation(XlsLabelledArrayCells.class)
                                        .attribute("label", "右側＋水平")
                                        .attribute("type", LabelledCellType.Right)
                                        .attribute("size", 3)
                                        .buildAnnotation())
                                .buildField())
                    .buildClass())
                .buildXml();
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        try(InputStream in = new FileInputStream(inputFile)) {

            assertThatThrownBy(() -> mapper.load(in, InvalidAnnoSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoLabelledArrayCellsTest$InvalidAnnoSheet#field2'において、アノテーション'@XlsLabelledArrayCells'を付与したタイプ'java.lang.String'はサポートしていません。'Collection(List/Set) or Array'で設定してください。");
        }

    }

    /**
     * 読み込みのテスト - optional
     */
    @Test
    public void test_load_labelled_array_cell_optinal() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false);

        // アノテーションの変更 - タイプが不正
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(InvalidAnnoSheet.class)
                        .field(createField("field1")
                                .annotation(createAnnotation(XlsLabelledArrayCells.class)
                                        .attribute("label", "あいう")
                                        .attribute("type", LabelledCellType.Right)
                                        .attribute("size", 3)
                                        .attribute("optional", true)
                                        .buildAnnotation())
                                .buildField())
                    .buildClass())
                .buildXml();
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        try(InputStream in = new FileInputStream(inputFile)) {

            SheetBindingErrors<InvalidAnnoSheet> errors = mapper.loadDetail(in, InvalidAnnoSheet.class);

            InvalidAnnoSheet sheet = errors.getTarget();

            assertThat(sheet.field1).isNull();
        }

    }

    /**
     * 読み込みのテスト - 不正なアノテーション - サポートしていないt属性typeとdirectionの組み合わせ
     */
    @Test
    public void test_load_labelled_array_cell_invalidAnno_notSupporTypeAndDirection() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false);

        // アノテーションの変更 - タイプが不正
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(InvalidAnnoSheet.class)
                        .field(createField("field1")
                                .annotation(createAnnotation(XlsLabelledArrayCells.class)
                                        .attribute("label", "右側＋水平")
                                        .attribute("type", LabelledCellType.Left)
                                        .attribute("direction", ArrayDirection.Horizon)
                                        .attribute("size", 3)
                                        .buildAnnotation())
                                .buildField())
                    .buildClass())
                .buildXml();
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        try(InputStream in = new FileInputStream(inputFile)) {

            assertThatThrownBy(() -> mapper.load(in, InvalidAnnoSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoLabelledArrayCellsTest$InvalidAnnoSheet#field1'において、アノテーション'@XlsLabelledArrayCells'の属性'type'の値が'LabelledCellType#Left'の時は、属性'direction'は、'ArrayDirection#Horizon'の値以外を設定してください。");
        }

    }

    /**
     * 読み込みのテスト - 見出しから距離がある場合
     * @since 2.0
     */
    @Test
    public void test_load_labelled_array_cell_distance() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<DistanceSheet> errors = mapper.loadDetail(in, DistanceSheet.class);

            DistanceSheet sheet = errors.getTarget();

            assertThat(sheet.skipRight, is(contains("右側1", "右側2")));
            assertThat(sheet.skipLeft, is(contains("左側1", "左側a")));
            assertThat(sheet.skipBottom, is(contains("下側1", "下側2")));

            assertThat(sheet.rangeRight, is(contains("右側1", "右側2")));
            assertThat(sheet.rangeLeft, is(contains("左側1", "左側a")));
            assertThat(sheet.rangeBottom, is(contains("下側1", "下側2")));

            assertThat(sheet.skipRangeRight, is(contains("右側2", "右側3")));
            assertThat(sheet.skipRangeLeft, is(contains("左側2", "左側a")));
            assertThat(sheet.skipRangeBottom, is(contains("下側2", "下側3")));

        }
    }

    /**
     * 読み込みテスト - 結合の考慮
     */
    @Test
    public void test_load_Labelled_array_cell_merged() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<MergedSheet> errors = mapper.loadDetail(in, MergedSheet.class);

            MergedSheet sheet = errors.getTarget();

            {
                // ラベルが結合
                assertThat(sheet.labelMerged)
                    .hasSize(3)
                    .containsExactly("あ", "い", "う");

                assertThat(sheet.positions)
                    .containsEntry("labelMerged[0]", CellPosition.of("D5"))
                    .containsEntry("labelMerged[1]", CellPosition.of("E5"))
                    .containsEntry("labelMerged[2]", CellPosition.of("F5"))
                    ;

                String label = "ラベルが結合";
                assertThat(sheet.labels)
                    .containsEntry("labelMerged[0]", label)
                    .containsEntry("labelMerged[1]", label)
                    .containsEntry("labelMerged[2]", label)
                    ;
            }

            {
                // 値が結合
                assertThat(sheet.valueMerged)
                    .hasSize(3)
                    .containsExactly("今日は良い天気", "ですね。", "明日も晴れると良いですね。");

                assertThat(sheet.positions)
                    .containsEntry("valueMerged[0]", CellPosition.of("C7"))
                    .containsEntry("valueMerged[1]", CellPosition.of("E7"))
                    .containsEntry("valueMerged[2]", CellPosition.of("F7"))
                    ;

                String label = "値が結合";
                assertThat(sheet.labels)
                    .containsEntry("valueMerged[0]", label)
                    .containsEntry("valueMerged[1]", label)
                    .containsEntry("valueMerged[2]", label)
                    ;
            }

            {
                // ラベルと値が結合
                assertThat(sheet.labelAndValueMerged)
                    .hasSize(3)
                    .containsExactly("ABCD", "EFG", "HIJ");

                assertThat(sheet.positions)
                    .containsEntry("labelAndValueMerged[0]", CellPosition.of("B12"))
                    .containsEntry("labelAndValueMerged[1]", CellPosition.of("B13"))
                    .containsEntry("labelAndValueMerged[2]", CellPosition.of("B15"))
                    ;

                String label = "ラベルと値が結合";
                assertThat(sheet.labels)
                    .containsEntry("labelAndValueMerged[0]", label)
                    .containsEntry("labelAndValueMerged[1]", label)
                    .containsEntry("labelAndValueMerged[2]", label)
                    ;
            }

        }

    }

    /**
     * 読み込みのテスト - 正規表現でラベルを一致
     */
    @Test
    public void test_load_labelled_array_cell_regex() throws Exception {

        {
            // エラー確認（正規表現が無効）
            XlsMapper mapper = new XlsMapper();

            mapper.getConfiguration()
                .setRegexLabelText(false)
                .setNormalizeLabelText(true)
                .setContinueTypeBindFailure(true);

            try(InputStream in = new FileInputStream(inputFile)) {
                assertThatThrownBy(() -> mapper.loadDetail(in, RegexSheet.class))
                    .isInstanceOf(CellNotFoundException.class)
                    .hasMessage("シート'正規表現で一致'において、ラベル'/ヘッダー.*/'を持つセルが見つかりません。");


            }

        }

        {
            // エラー確認（正規化が無効）
            XlsMapper mapper = new XlsMapper();

            mapper.getConfiguration()
                .setRegexLabelText(true)
                .setNormalizeLabelText(false)
                .setContinueTypeBindFailure(true);

            try(InputStream in = new FileInputStream(inputFile)) {
                assertThatThrownBy(() -> mapper.loadDetail(in, RegexSheet.class))
                    .isInstanceOf(CellNotFoundException.class)
                    .hasMessage("シート'正規表現で一致'において、ラベル'/ヘッダー.*/'を持つセルが見つかりません。");


            }

        }

        {
         // 正規表現、正規化の両方が有効
            XlsMapper mapper = new XlsMapper();

            mapper.getConfiguration()
                .setRegexLabelText(true)
                .setNormalizeLabelText(true)
                .setContinueTypeBindFailure(true);

            try(InputStream in = new FileInputStream(inputFile)) {

                SheetBindingErrors<RegexSheet> errors = mapper.loadDetail(in, RegexSheet.class);

                RegexSheet sheet = errors.getTarget();

                {
                    assertThat(sheet.regexp)
                        .hasSize(3)
                        .containsExactly("正規表現の見出し。", "A", "1");

                    String label = "見出し(1)";
                    assertThat(sheet.labels)
                        .containsEntry("regexp[0]", label)
                        .containsEntry("regexp[1]", label)
                        .containsEntry("regexp[2]", label)
                        ;
                }

                {
                    assertThat(sheet.notRegexp)
                        .hasSize(3)
                        .containsExactly("非正規表現の見出し。", "B", "2");

                    String label = "見出し(a)";
                    assertThat(sheet.labels)
                        .containsEntry("notRegexp[0]", label)
                        .containsEntry("notRegexp[1]", label)
                        .containsEntry("notRegexp[2]", label)
                        ;
                }

                {
                    assertThat(sheet.normalize)
                        .hasSize(3)
                        .containsExactly("改行がある場合\nです。", "C", "3");

                    String label = " 更新\n日時 ";
                    assertThat(sheet.labels)
                        .containsEntry("normalize[0]", label)
                        .containsEntry("normalize[1]", label)
                        .containsEntry("normalize[2]", label)
                        ;

                }

                {
                    assertThat(sheet.headerRegexp)
                        .hasSize(3)
                        .containsExactly("見出しが正規表現です。", "D", "4");

                    String label = "ラベル";
                    assertThat(sheet.labels)
                        .containsEntry("headerRegexp[0]", label)
                        .containsEntry("headerRegexp[1]", label)
                        .containsEntry("headerRegexp[2]", label)
                        ;
                }

            }
        }

    }

    /**
     * 読み込みのテスト - 数式
     */
    @Test
    public void test_load_labelled_array_cell_formula() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<FormulaSheet> errors = mapper.loadDetail(in, FormulaSheet.class);

            FormulaSheet sheet = errors.getTarget();

            {
                assertThat(sheet.continueNumber)
                    .containsExactly(1, 2, 3);

                String label = "連番";
                assertThat(sheet.labels)
                    .containsEntry("continueNumber[0]", label)
                    .containsEntry("continueNumber[1]", label)
                    .containsEntry("continueNumber[2]", label)
                    ;
            }

            {
                assertThat(sheet.dateList)
                    .containsExactly(LocalDate.of(2017, 5, 5), LocalDate.of(2017, 5, 6), LocalDate.of(2017, 5, 7));

                String label = "日付";
                assertThat(sheet.labels)
                    .containsEntry("dateList[0]", label)
                    .containsEntry("dateList[1]", label)
                    .containsEntry("dateList[2]", label)
                    ;
            }
        }
    }
    
    /**
     * 読み込みのテスト - コメント情報
     * @since 2.1
     */
    @Test
    public void test_load_labelled_array_cell_comment() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<CommentSheet> errors = mapper.loadDetail(in, CommentSheet.class);

            CommentSheet sheet = errors.getTarget();

            assertThat(sheet.hArray)
                .hasSize(3)
                .containsExactly("今日は", "、", "いい天気ですね。");

            assertThat(sheet.comments)
                .containsEntry("hArray[0]", "コメント1")
                .doesNotContainKey("hArray[1]")
                .containsEntry("hArray[2]", "コメント3")
                ;

            // vertical
            assertThat(sheet.vArray)
                .hasSize(3)
                .containsExactly("Hello", ",", "World");

            assertThat(sheet.comments)
                .doesNotContainKey("vArray[0]")
                .containsEntry("vArray[1]", "コメント2")
                .doesNotContainKey("vArray[2]")
                ;


        }
        
    }


    /**
     * 書き込みテスト - 通常のテスト
     */
    @Test
    public void test_save_labelled_array_cell_normal() throws Exception {

        // テストデータの作成
        final NormalSheet outSheet = new NormalSheet();

        outSheet.rightHorizon = Arrays.asList("あ", "い", "う", "え", "お");
        outSheet.bottomHorizon = Arrays.asList("あ", "い", "う", "え", "お");

        outSheet.rightVertical = new LocalDate[]{LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2), LocalDate.of(2017, 4, 3)};
        outSheet.leftVertical = new LocalDate[]{LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2), LocalDate.of(2017, 4, 3)};
        outSheet.bottomVertical = new LocalDate[]{LocalDate.of(2017, 4, 1), LocalDate.of(2017, 4, 2), LocalDate.of(2017, 4, 3)};

        outSheet.headerHorizon = Arrays.asList("か", "き", "く", "け", "こ");
        outSheet.headerVertical = new LocalDate[]{LocalDate.of(2017, 5, 1), LocalDate.of(2017, 5, 2), LocalDate.of(2017, 5, 3)};

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

            assertThat(sheet.positions).containsAllEntriesOf(outSheet.positions);
            assertThat(sheet.labels).containsAllEntriesOf(outSheet.labels);

            assertThat(sheet.rightHorizon).containsExactlyElementsOf(outSheet.rightHorizon);
            assertThat(sheet.bottomHorizon).containsExactlyElementsOf(outSheet.bottomHorizon);

            assertThat(sheet.rightVertical).containsExactly(outSheet.rightVertical);
            assertThat(sheet.leftVertical).containsExactly(outSheet.leftVertical);
            assertThat(sheet.bottomVertical).containsExactly(outSheet.bottomVertical);

            assertThat(sheet.headerHorizon).containsExactlyElementsOf(outSheet.headerHorizon);
            assertThat(sheet.headerVertical).containsExactly(outSheet.headerVertical);
        }

    }

    /**
     * 書き込みのテスト - 不正なアノテーション - サポートしていないタイプの場合
     */
    @Test
    public void test_save_labelled_array_cell_invalidAnno_notSupportType() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false);

        // テストデータの作成
        final InvalidAnnoSheet outSheet = new InvalidAnnoSheet();
        outSheet.field1 = Arrays.asList("あ", "い", "う");
        outSheet.field2 = "あいう";

        // アノテーションの変更 - タイプが不正
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(InvalidAnnoSheet.class)
                        .field(createField("field2")
                                .annotation(createAnnotation(XlsLabelledArrayCells.class)
                                        .attribute("label", "右側＋水平")
                                        .attribute("type", LabelledCellType.Right)
                                        .attribute("size", 3)
                                        .buildAnnotation())
                                .buildField())
                    .buildClass())
                .buildXml();
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            assertThatThrownBy(() -> mapper.save(template, out, outSheet))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoLabelledArrayCellsTest$InvalidAnnoSheet#field2'において、アノテーション'@XlsLabelledArrayCells'を付与したタイプ'java.lang.String'はサポートしていません。'Collection(List/Set) or Array'で設定してください。");

        }

    }

    /**
     * 書き込みみのテスト - optional
     */
    @Test
    public void test_save_labelled_array_cell_optinal() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false);

        // テストデータの作成
        final InvalidAnnoSheet outSheet = new InvalidAnnoSheet();
        outSheet.field1 = Arrays.asList("あ", "い", "う");

        // アノテーションの変更 - タイプが不正
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(InvalidAnnoSheet.class)
                        .field(createField("field1")
                                .annotation(createAnnotation(XlsLabelledArrayCells.class)
                                        .attribute("label", "あいう")
                                        .attribute("type", LabelledCellType.Right)
                                        .attribute("size", 3)
                                        .attribute("optional", true)
                                        .buildAnnotation())
                                .buildField())
                    .buildClass())
                .buildXml();
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        // ファイルへの書き込み
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);

        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<InvalidAnnoSheet> errors = mapper.loadDetail(in, InvalidAnnoSheet.class);

            InvalidAnnoSheet sheet = errors.getTarget();
            assertThat(sheet.field1).isNull();

        }
    }

    /**
     * 書き込みのテスト - 見出しから距離がある場合
     * @since 2.0
     */
    @Test
    public void test_save_labelled_array_cell_distance() throws Exception {

        // テストデータの作成
        final DistanceSheet outSheet = new DistanceSheet();

        outSheet.skipRight = Arrays.asList("右側1", "右側2");
        outSheet.skipLeft = Arrays.asList("左側1", "左側2");
        outSheet.skipBottom = Arrays.asList("下側1", "下側2");

        outSheet.rangeRight = Arrays.asList("右側1", "右側2");
        outSheet.rangeLeft = Arrays.asList("左側1", "左側2");
        outSheet.rangeBottom = Arrays.asList("下側1", "下側2");

        outSheet.skipRangeRight = Arrays.asList("右側1", "右側2");
        outSheet.skipRangeLeft = Arrays.asList("左側1", "左側2");
        outSheet.skipRangeBottom = Arrays.asList("下側1", "下側2");

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
            SheetBindingErrors<DistanceSheet> errors = mapper.loadDetail(in, DistanceSheet.class);

            DistanceSheet sheet = errors.getTarget();

            assertThat(sheet.positions).containsAllEntriesOf(outSheet.positions);
            assertThat(sheet.labels).containsAllEntriesOf(outSheet.labels);

            assertThat(sheet.skipRight).containsExactlyElementsOf(outSheet.skipRight);
            assertThat(sheet.skipLeft).containsExactlyElementsOf(outSheet.skipLeft);
            assertThat(sheet.skipBottom).containsExactlyElementsOf(outSheet.skipBottom);

            assertThat(sheet.positions)
                .containsEntry("skipRight[0]", CellPosition.of("K5"))
                .containsEntry("skipRight[1]", CellPosition.of("L5"))

                .containsEntry("skipLeft[0]", CellPosition.of("E5"))
                .containsEntry("skipLeft[1]", CellPosition.of("E6"))

                .containsEntry("skipBottom[0]", CellPosition.of("H8"))
                .containsEntry("skipBottom[1]", CellPosition.of("H9"))

            ;

            assertThat(sheet.rangeRight).containsExactlyElementsOf(outSheet.rangeRight);
            assertThat(sheet.rangeLeft).containsExactlyElementsOf(outSheet.rangeLeft);
            assertThat(sheet.rangeBottom).containsExactlyElementsOf(outSheet.rangeBottom);

            assertThat(sheet.positions)
                .containsEntry("rangeRight[0]", CellPosition.of("I14"))
                .containsEntry("rangeRight[1]", CellPosition.of("J14"))

                .containsEntry("rangeLeft[0]", CellPosition.of("G14"))
                .containsEntry("rangeLeft[1]", CellPosition.of("G15"))

                .containsEntry("rangeBottom[0]", CellPosition.of("H15"))
                .containsEntry("rangeBottom[1]", CellPosition.of("H16"))

            ;

            assertThat(sheet.skipRangeRight).containsExactlyElementsOf(outSheet.skipRangeRight);
            assertThat(sheet.skipRangeLeft).containsExactlyElementsOf(outSheet.skipRangeLeft);
            assertThat(sheet.skipRangeBottom).containsExactlyElementsOf(outSheet.skipRangeBottom);

            assertThat(sheet.positions)
            .containsEntry("skipRangeRight[0]", CellPosition.of("L23"))
                .containsEntry("skipRangeRight[1]", CellPosition.of("M23"))

                .containsEntry("skipRangeLeft[0]", CellPosition.of("D23"))
                .containsEntry("skipRangeLeft[1]", CellPosition.of("D24"))

                .containsEntry("skipRangeBottom[0]", CellPosition.of("H27"))
                .containsEntry("skipRangeBottom[1]", CellPosition.of("H28"))

            ;

        }

    }

    /**
     * 書き込みテスト - 結合の考慮
     */
    @Test
    public void test_save_labelled_array_cell_merged() throws Exception {

        // テストデータの作成
        final MergedSheet outSheet = new MergedSheet();

        outSheet.labelMerged = Arrays.asList("あ", "い", "う");
        outSheet.valueMerged = Arrays.asList("今日は良い天気", "ですね。", "明日も晴れると良いですね。");
        outSheet.labelAndValueMerged = Arrays.asList("ABCD", "EFG", "HIJ");

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
            SheetBindingErrors<MergedSheet> errors = mapper.loadDetail(in, MergedSheet.class);

            MergedSheet sheet = errors.getTarget();

            assertThat(sheet.positions).containsAllEntriesOf(outSheet.positions);
            assertThat(sheet.labels).containsAllEntriesOf(outSheet.labels);

            assertThat(sheet.labelMerged).containsExactlyElementsOf(outSheet.labelMerged);
            assertThat(sheet.valueMerged).containsExactlyElementsOf(outSheet.valueMerged);
            assertThat(sheet.labelAndValueMerged).containsExactlyElementsOf(outSheet.labelAndValueMerged);

        }
    }

    /**
     * 書き込みテスト - 見出しを正規表現で一致
     */
    @Test
    public void test_save_labelled_array_cell_regex() throws Exception {

        // テストデータの作成
        final RegexSheet outSheet = new RegexSheet();

        outSheet.regexp = Arrays.asList("正規表現の見出し。", "A", "1");
        outSheet.notRegexp = Arrays.asList("非正規表現の見出し。", "B", "2");
        outSheet.normalize = Arrays.asList("改行がある場合\nです。", "C", "3");
        outSheet.headerRegexp = Arrays.asList("見出しが正規表現です。", "D", "4");

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration()
            .setRegexLabelText(true)
            .setNormalizeLabelText(true)
            .setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<RegexSheet> errors = mapper.loadDetail(in, RegexSheet.class);

            RegexSheet sheet = errors.getTarget();

            assertThat(sheet.positions).containsAllEntriesOf(outSheet.positions);
            assertThat(sheet.labels).containsAllEntriesOf(outSheet.labels);

            assertThat(sheet.regexp).containsExactlyElementsOf(outSheet.regexp);
            assertThat(sheet.notRegexp).containsExactlyElementsOf(outSheet.notRegexp);
            assertThat(sheet.normalize).containsExactlyElementsOf(outSheet.normalize);
            assertThat(sheet.headerRegexp).containsExactlyElementsOf(outSheet.headerRegexp);
        }
    }

    /**
     * 書き込みのテスト - 数式のテスト
     */
    @Test
    public void test_save_labelled_array_cell_formula() throws Exception {

        // テストデータの作成
        FormulaSheet outSheet = new FormulaSheet();

        outSheet.continueNumber = new int[3];
        outSheet.dateList = Arrays.asList(null, null, null);

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

            assertThat(sheet.positions).containsAllEntriesOf(outSheet.positions);
            assertThat(sheet.labels).containsAllEntriesOf(outSheet.labels);

            assertThat(sheet.continueNumber)
                .containsExactly(1, 2, 3);

            assertThat(sheet.dateList)
            .containsExactly(LocalDate.of(2017, 5, 5), LocalDate.of(2017, 5, 6), LocalDate.of(2017, 5, 7));

        }

    }
    
    /**
     * 書き込みのテスト - コメントのテスト
     * @since 2.1
     */
    @Test
    public void test_save_labelled_array_cell_comment() throws Exception {

        // テストデータの作成
        CommentSheet outSheet = new CommentSheet();

        outSheet.hArray = Arrays.asList("今日は", "、", "いい天気ですね。");
        outSheet.vArray = Arrays.asList("Hello", ",", "World");
        
        outSheet.comment("hArray[0]", "コメント1").comment("hArray[2]", "コメント3")
            .comment("vArray[1]", "コメント2");
        

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

            assertThat(sheet.hArray).containsExactlyElementsOf(outSheet.hArray);
            assertThat(sheet.vArray).containsExactlyElementsOf(outSheet.vArray);

            assertThat(sheet.comments).containsAllEntriesOf(outSheet.comments);
            
        }

    }

    @XlsSheet(name="通常")
    private static class NormalSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsLabelledArrayCells(label="右側＋水平", type=LabelledCellType.Right, size=5)
        private List<String> rightHorizon;

        @XlsLabelledArrayCells(label="下側＋水平", type=LabelledCellType.Bottom, size=5)
        private List<String> bottomHorizon;

        @XlsLabelledArrayCells(label="右側＋垂直", type=LabelledCellType.Right, direction=ArrayDirection.Vertical, size=3)
        private LocalDate[] rightVertical;

        @XlsLabelledArrayCells(label="左側＋垂直", type=LabelledCellType.Left, direction=ArrayDirection.Vertical, size=3)
        private LocalDate[] leftVertical;

        @XlsLabelledArrayCells(label="下側＋垂直", type=LabelledCellType.Bottom, direction=ArrayDirection.Vertical, size=3)
        private LocalDate[] bottomVertical;

        @XlsLabelledArrayCells(label="ラベル名", headerLabel="見出し１", type=LabelledCellType.Bottom, direction=ArrayDirection.Horizon, size=5)
        private List<String> headerHorizon;

        @XlsLabelledArrayCells(label="ラベル名", headerLabel="見出し２", type=LabelledCellType.Bottom, direction=ArrayDirection.Vertical, size=3)
        private LocalDate[] headerVertical;

    }

    /**
     * 不正なアノテーションの場合のテスト
     * <p>XMLによる属性変更で値を設定する
     */
    @XlsSheet(name="通常")
    private static class InvalidAnnoSheet {

        @XlsLabelledArrayCells(label="右側＋水平", type=LabelledCellType.Right, size=5)
        private List<String> field1;

        private String field2;

    }

    @XlsSheet(name="結合の考慮")
    private static class MergedSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsLabelledArrayCells(label="ラベルが結合", type=LabelledCellType.Right, labelMerged=true, size=3)
        private List<String> labelMerged;

        @XlsLabelledArrayCells(label="値が結合", type=LabelledCellType.Right, elementMerged=true, size=3)
        private List<String> valueMerged;

        @XlsLabelledArrayCells(label="ラベルと値が結合", type=LabelledCellType.Bottom, labelMerged=true, elementMerged=true, direction=ArrayDirection.Vertical, size=3)
        private List<String> labelAndValueMerged;

    }

    /**
     * 属性 skip, rangeのテスト
     *
     */
    @XlsSheet(name="見出しから離れたセル")
    private static class DistanceSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsOrder(1)
        @XlsLabelledArrayCells(label="ラベル(skip)", type=LabelledCellType.Right, size=2, skip=2)
        private List<String> skipRight;

        @XlsOrder(2)
        @XlsLabelledArrayCells(label="ラベル(skip)", type=LabelledCellType.Left, size=2, skip=2, direction=ArrayDirection.Vertical)
        private List<String> skipLeft;

        @XlsOrder(3)
        @XlsLabelledArrayCells(label="ラベル(skip)", type=LabelledCellType.Bottom, size=2, skip=2, direction=ArrayDirection.Vertical)
        private List<String> skipBottom;

        @XlsOrder(4)
        @XlsLabelledArrayCells(label="ラベル(range)", type=LabelledCellType.Right, size=2, range=4)
        private List<String> rangeRight;

        @XlsOrder(5)
        @XlsLabelledArrayCells(label="ラベル(range)", type=LabelledCellType.Left, size=2, range=4, direction=ArrayDirection.Vertical)
        private List<String> rangeLeft;

        @XlsOrder(6)
        @XlsLabelledArrayCells(label="ラベル(range)", type=LabelledCellType.Bottom, size=2, range=4, direction=ArrayDirection.Vertical)
        private List<String> rangeBottom;

        @XlsOrder(7)
        @XlsLabelledArrayCells(label="ラベル(skip+range)", type=LabelledCellType.Right, size=2, skip=3, range=4)
        private List<String> skipRangeRight;

        @XlsOrder(8)
        @XlsLabelledArrayCells(label="ラベル(skip+range)", type=LabelledCellType.Left, size=2, skip=3, range=4, direction=ArrayDirection.Vertical)
        private List<String> skipRangeLeft;

        @XlsOrder(9)
        @XlsLabelledArrayCells(label="ラベル(skip+range)", type=LabelledCellType.Bottom, size=2, skip=3, range=4, direction=ArrayDirection.Vertical)
        private List<String> skipRangeBottom;

    }

    @XlsSheet(name="正規表現で一致")
    private static class RegexSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        /** 正規表現によるマッピング */
        @XlsLabelledArrayCells(label="/見出し\\([0-9]+\\)/", type=LabelledCellType.Right, size=3)
        private List<String> regexp;

        @XlsLabelledArrayCells(label="見出し(a)", type=LabelledCellType.Right, size=3)
        private List<String> notRegexp;

        /** 正規化による空白などの削除 */
        @XlsLabelledArrayCells(label="更新日時", type=LabelledCellType.Bottom, size=3, direction=ArrayDirection.Vertical)
        private List<String> normalize;

        @XlsLabelledArrayCells(headerLabel="/ヘッダー.*/", label="ラベル", type=LabelledCellType.Right, size=3)
        private List<String> headerRegexp;

    }

    @XlsSheet(name="数式を指定")
    private static class FormulaSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsLabelledArrayCells(label="連番", type=LabelledCellType.Bottom, size=3, direction=ArrayDirection.Vertical)
        @XlsFormula(value="ROW()-4", primary=true)
        private int[] continueNumber;

        @XlsLabelledArrayCells(label="日付", type=LabelledCellType.Bottom, size=3)
        @XlsDateTimeConverter(excelPattern="yyyy/m/d;@", javaPattern="yyyy/M/d")
        @XlsFormula("\\$I\\$5+{columnNumber}")
        private List<LocalDate> dateList;
    }
    
    @XlsSheet(name="コメント情報")
    private static class CommentSheet {
        
        private Map<String, CellPosition> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsLabelledArrayCells(label = "挨拶", type = LabelledCellType.Right, direction=ArrayDirection.Horizon, size=3, elementMerged=true)
        private List<String> hArray;

        @XlsLabelledArrayCells(label = "挨拶", type = LabelledCellType.Bottom, direction=ArrayDirection.Vertical, size=3, elementMerged=true)
        private List<String> vArray;
        
        public CommentSheet comment(String key, String text) {
            if(comments == null) {
                this.comments = new HashMap<String, String>();
            }
            this.comments.put(key, text);
            return this;
        }
        
    }

}
