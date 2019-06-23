package com.gh.mygreen.xlsmapper.fieldprocessor;

import static org.junit.Assert.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption.HorizontalAlign;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption.VerticalAlign;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;

/**
 * セルのオプション用のアノテーション {@link XlsCellOption}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AnnoCellOptionTest {

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
    private File inputFile = new File("src/test/data/anno_CellOption.xlsx");

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_CellOption_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_CellOption_out.xlsx";

    /**
     * 文字制御（折り返し設定、縮小して表示）のテスト
     */
    @Test
    public void test_save_cellOption_TextControl() throws Exception {

        // テストデータの作成
        String value = "あいうえおかきくけこ１２３４５６７８９０";
        TextControlSheet outSheet = new TextControlSheet();
        outSheet.wrapText1 = value;
        outSheet.wrapText2 = value;
        outSheet.wrapText3 = value;
        outSheet.wrapText4 = value;

        outSheet.shrinkToFit1 = value;
        outSheet.shrinkToFit2 = value;
        outSheet.shrinkToFit3 = value;
        outSheet.shrinkToFit4 = value;

        outSheet.wrapToTextAndShrinktToFit = value;

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

            Sheet sheet = WorkbookFactory.create(in).getSheet("文字の制御");

            {
                // 折り返し設定(Java側なし)(Excel側なし)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C5"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(false);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(false);
            }

            {
                // 折り返し設定(Java側なし)(Excel側あり)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C6"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(true);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(false);
            }

            {
                // 折り返し設定(Java側あり)(Excel側なし)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C7"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(true);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(false);
            }

            {
                // 折り返し設定(Java側あり)(Excel側あり)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C8"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(true);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(false);
            }

            {
                // 縮小して表示(Java側なし)(Excel側なし)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C12"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(false);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(false);
            }

            {
                // 縮小して表示(Java側なし)(Excel側あり)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C13"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(false);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(true);
            }

            {
                // 縮小して表示(Java側あり)(Excel側なし)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C14"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(false);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(true);
            }

            {
                // 縮小して表示(Java側あり)(Excel側あり)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C15"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(false);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(true);
            }

            {
                // 折り返し設定＋縮小して表示
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C18"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getWrapText()).isEqualTo(false);
                assertThat(cell.getCellStyle().getShrinkToFit()).isEqualTo(true);
            }


        }

    }

    /**
     * 横位置、縦位置のテスト
     */
    @Test
    public void test_save_cellOption_Align() throws Exception {

        // テストデータの作成
        String value = "あいうえおかきくけこ１２３４５６７８９０";
        AlignSheet outSheet = new AlignSheet();
        outSheet.horizontalDefault = value;
        outSheet.horizontalGeneral = value;
        outSheet.horizontalLeft = value;
        outSheet.horizontalLeftIndent = value;
        outSheet.horizontalCenter = value;
        outSheet.horizontalRight = value;
        outSheet.horizontalRightIndent = value;
        outSheet.horizontalFill = value;
        outSheet.horizontalJustify = value;
        outSheet.horizontalCenterSelection = value;
        outSheet.horizontalDistributed = value;
        outSheet.horizontalDistributedIndent = value;

        outSheet.verticalDefault = value;
        outSheet.verticalTop = value;
        outSheet.verticalCenter = value;
        outSheet.verticalBottom = value;
        outSheet.verticalJustify = value;
        outSheet.verticalDistibuted = value;

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

            Sheet sheet = WorkbookFactory.create(in).getSheet("位置の制御");

            {
                // 横位置(デフォルト)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C4"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.CENTER);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.CENTER);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(標準)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C5"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.GENERAL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(左詰め)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C6"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.LEFT);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(左詰め)(インデント)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C7"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.LEFT);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)2);
            }

            {
                // 横位置(中央揃え)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C8"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.CENTER);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(右詰め)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C9"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.RIGHT);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(右詰め)(インデント)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C10"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.RIGHT);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)2);
            }

            {
                // 繰り返し
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C11"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.FILL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(両端揃え)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C12"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.JUSTIFY);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(選択範囲内で中央)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C13"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.CENTER_SELECTION);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(均等割り付け)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C14"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.DISTRIBUTED);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 横位置(均等割り付け)(インデント)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C15"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.DISTRIBUTED);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)2);
            }

            {
                // 縦位置(デフォルト)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C19"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.CENTER);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.CENTER);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 縦位置(上詰め)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C20"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.GENERAL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.TOP);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 縦位置(中央揃え)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C21"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.GENERAL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.CENTER);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 縦位置(下詰め)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C22"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.GENERAL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.BOTTOM);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 縦位置(両端揃え)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C23"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.GENERAL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.JUSTIFY);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }

            {
                // 縦位置(均等割り付け)
                Cell cell = POIUtils.getCell(sheet, CellPosition.of("C24"));
                String text = POIUtils.getCellContents(cell, mapper.getConfiguration().getCellFormatter());
                assertThat(text).isEqualTo(value);

                assertThat(cell.getCellStyle().getAlignmentEnum()).isEqualTo(HorizontalAlignment.GENERAL);
                assertThat(cell.getCellStyle().getVerticalAlignmentEnum()).isEqualTo(VerticalAlignment.DISTRIBUTED);
                assertThat(cell.getCellStyle().getIndention()).isEqualTo((short)0);
            }
        }

    }

    @XlsSheet(name="文字の制御")
    private static class TextControlSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsLabelledCell(label="折り返し設定(Java側なし)(Excel側なし)", type=LabelledCellType.Right)
        @XlsCellOption
        private String wrapText1;

        @XlsLabelledCell(label="折り返し設定(Java側なし)(Excel側あり)", type=LabelledCellType.Right)
        @XlsCellOption
        private String wrapText2;

        @XlsLabelledCell(label="折り返し設定(Java側あり)(Excel側なし)", type=LabelledCellType.Right)
        @XlsCellOption(wrapText=true)
        private String wrapText3;

        @XlsLabelledCell(label="折り返し設定(Java側あり)(Excel側あり)", type=LabelledCellType.Right)
        @XlsCellOption(wrapText=true)
        private String wrapText4;

        @XlsLabelledCell(label="縮小して表示(Java側なし)(Excel側なし)", type=LabelledCellType.Right)
        @XlsCellOption
        private String shrinkToFit1;

        @XlsLabelledCell(label="縮小して表示(Java側なし)(Excel側あり)", type=LabelledCellType.Right)
        @XlsCellOption
        private String shrinkToFit2;


        @XlsLabelledCell(label="縮小して表示(Java側あり)(Excel側なし)", type=LabelledCellType.Right)
        @XlsCellOption(shrinkToFit=true)
        private String shrinkToFit3;


        @XlsLabelledCell(label="縮小して表示(Java側あり)(Excel側あり)", type=LabelledCellType.Right)
        @XlsCellOption(shrinkToFit=true)
        private String shrinkToFit4;

        @XlsLabelledCell(label="折り返し設定＋縮小して表示", type=LabelledCellType.Right)
        @XlsCellOption(wrapText=true, shrinkToFit=true)
        private String wrapToTextAndShrinktToFit;

    }

    @XlsSheet(name="位置の制御")
    public static class AlignSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsLabelledCell(label="横位置(デフォルト)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Default)
        private String horizontalDefault;

        @XlsLabelledCell(label="横位置(標準)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.General)
        private String horizontalGeneral;

        @XlsLabelledCell(label="横位置(左詰め)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Left)
        private String horizontalLeft;

        @XlsLabelledCell(label="横位置(左詰め)(インデント)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Left, indent=2)
        private String horizontalLeftIndent;

        @XlsLabelledCell(label="横位置(中央揃え)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Center)
        private String horizontalCenter;

        @XlsLabelledCell(label="横位置(右詰め)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Right)
        private String horizontalRight;

        @XlsLabelledCell(label="横位置(右詰め)(インデント)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Right, indent=2)
        private String horizontalRightIndent;

        @XlsLabelledCell(label="横位置(繰り返し)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Fill)
        private String horizontalFill;

        @XlsLabelledCell(label="横位置(両端揃え)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Justify)
        private String horizontalJustify;

        @XlsLabelledCell(label="横位置(選択範囲内で中央)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.CenterSelection)
        private String horizontalCenterSelection;

        @XlsLabelledCell(label="横位置(均等割り付け)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Distributed)
        private String horizontalDistributed;

        @XlsLabelledCell(label="横位置(均等割り付け)(インデント)", type=LabelledCellType.Right)
        @XlsCellOption(horizontalAlign=HorizontalAlign.Distributed, indent=2)
        private String horizontalDistributedIndent;

        @XlsLabelledCell(label="縦位置(デフォルト)", type=LabelledCellType.Right)
        @XlsCellOption(verticalAlign=VerticalAlign.Default)
        private String verticalDefault;

        @XlsLabelledCell(label="縦位置(上詰め)", type=LabelledCellType.Right)
        @XlsCellOption(verticalAlign=VerticalAlign.Top)
        private String verticalTop;

        @XlsLabelledCell(label="縦位置(中央揃え)", type=LabelledCellType.Right)
        @XlsCellOption(verticalAlign=VerticalAlign.Center)
        private String verticalCenter;

        @XlsLabelledCell(label="縦位置(下詰め)", type=LabelledCellType.Right)
        @XlsCellOption(verticalAlign=VerticalAlign.Bottom)
        private String verticalBottom;

        @XlsLabelledCell(label="縦位置(両端揃え)", type=LabelledCellType.Right)
        @XlsCellOption(verticalAlign=VerticalAlign.Justify)
        private String verticalJustify;

        @XlsLabelledCell(label="縦位置(均等割り付け)", type=LabelledCellType.Right)
        @XlsCellOption(verticalAlign=VerticalAlign.Distibuted)
        private String verticalDistibuted;


    }
}
