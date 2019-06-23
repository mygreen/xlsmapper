package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.DefaultCellCommentHandler;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsCommentOption;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * {@link XlsCommentOption}のテスタ。
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class AnnoCommentOptionTest {
    
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
    private File inputFile = new File("src/test/data/anno_CommentOption.xlsx");
    
    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_CommentOption_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_CommentOption_out.xlsx";
    
    /**
     * 読み込みのテスト - 通常のデータ
     */
    @Test
    public void test_load_comment_normal() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);
            
            NormalSheet sheet = errors.getTarget();
            
            assertThat(sheet.getDirectCell1(), is("アドレスを指定"));
            assertThat(sheet.getDirectCell2(), is("アドレスを指定（結合）"));
            
            assertThat(sheet.getLabelledCell1(), is("値1"));
            assertThat(sheet.getLabelledCell2(), is("値2"));
            
            assertThat(sheet.comments, hasEntry("directCell1", "アドレスを指定。"));
            assertThat(sheet.comments, hasEntry("directCell2", "結合したセル。"));

            assertThat(sheet.comments, hasEntry("labelledCell1", "コメントです。\n改行。\nフォント。"));
            assertThat(sheet.comments, hasEntry("labelledCell2", "コメント。"));
            
        }
    }
    
    /**
     * 読み込みのテスト - 処理の独自実装
     */
    @Test
    public void test_load_comment_customHandlerl() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<CustomHandlerSheet> errors = mapper.loadDetail(in, CustomHandlerSheet.class);
            
            CustomHandlerSheet sheet = errors.getTarget();
            
            assertThat(sheet.value1, is("値1"));
            assertThat(sheet.value2, is("値2"));
            
            assertThat(sheet.comments, hasEntry("value1", "あいうえお。\nかきくけこ。"));
            assertThat(sheet.comments, hasEntry("value2", "あいうえお。かきくけこ。"));
            
        }
    }
    
    /**
     * 書込み時のテスト - 通常のデータ
     */
    @Test
    public void test_save_comment_normal() throws Exception {
        
        // テストデータの作成
        NormalSheet outSheet = new NormalSheet();
        outSheet.comments = new HashMap<>();
        outSheet.setDirectCell1Comment("アドレスを指定。");
        outSheet.setDirectCell2Comment("結合したセル。");
        outSheet.comments.put("labelledCell1", "コメントです。\n改行。\nフォント。");
        outSheet.comments.put("labelledCell2", "コメント。");
        
        outSheet.setDirectCell1("アドレスを指定");
        outSheet.setDirectCell2("アドレスを指定（結合）");

        outSheet.setLabelledCell1(null);
        outSheet.setLabelledCell2("値2");
        
        
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

            assertThat(sheet.comments, hasEntry("directCell1", "アドレスを指定。"));
            assertThat(sheet.comments, hasEntry("directCell2", "結合したセル。"));

            assertThat(sheet.comments, hasEntry("labelledCell1", "コメントです。\n改行。\nフォント。"));
            assertThat(sheet.comments, hasEntry("labelledCell2", "コメント。"));

        }
    }
    
    /**
     * 書き込み時のテスト - コメントの表示設定
     */
    @Test
    public void test_save_comment_visible() throws Exception {
        
        // テストデータの作成
        CommentVisibleSheet outSheet = new CommentVisibleSheet();
        outSheet.comments = new HashMap<>();
        
        outSheet.visible = "trueを設定";
        outSheet.invisible = "falseを設定";
        
        outSheet.comments.put("visible", "コメントの表示設定。\nvisible=true");
        outSheet.comments.put("invisible", "コメントの表示設定。\nvisible=false");
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile);
                InputStream in2 = new FileInputStream(outFile)) {
            SheetBindingErrors<CommentVisibleSheet> errors = mapper.loadDetail(in, CommentVisibleSheet.class);

            CommentVisibleSheet sheet = errors.getTarget();

            assertThat(sheet.comments, hasEntry("visible", "コメントの表示設定。\nvisible=true"));
            assertThat(sheet.comments, hasEntry("invisible", "コメントの表示設定。\nvisible=false"));
            
            Workbook book = WorkbookFactory.create(in2);
            Sheet xlsSheet = book.getSheet(sheet.sheetName);
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("visible"));
                Comment comment = cell.getCellComment();
                assertThat(comment.isVisible(), is(true));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("invisible"));
                Comment comment = cell.getCellComment();
                assertThat(comment.isVisible(), is(false));
            }

        }
        
    }
    
    /**
     * 書き込み時のテスト - コメントのサイズ設定
     */
    @Test
    public void test_save_comment_size() throws Exception {
        
        // テストデータの作成
        CommentSizeSheet outSheet = new CommentSizeSheet();
        outSheet.comments = new HashMap<>();
        
        outSheet.autoRow1 = "1行";
        outSheet.autoRowMax = "4行";
        outSheet.autoRowMax1 = "5行";
        
        outSheet.comments.put("autoRow1", "1行");
        outSheet.comments.put("autoRowMax", "1行\n2行\r\n3行\n4行");
        outSheet.comments.put("autoRowMax1", "1行\n2行\r\n3行\n4行\n5行");
        
        outSheet.autoColumn1 = "1列";
        outSheet.autoColumnMax = "3列";
        outSheet.autoColumnMax1 = "4列";
        
        outSheet.comments.put("autoColumn1", "1列-111");
        outSheet.comments.put("autoColumnMax", "1列-1112列-2223列-333");
        outSheet.comments.put("autoColumnMax1", "1列-1112列-2223列-3334列-444");
        
        outSheet.directVertical = "2行3列";
        outSheet.comments.put("directVertical", "1列-1112列-2223列-333\n2行-2222\n3行-333");
        
        outSheet.directHorizontal = "2行3列";
        outSheet.comments.put("directHorizontal", "1列-1112列-2223列-333\n2行-2222\n3行-333");
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile);
                InputStream in2 = new FileInputStream(outFile)) {
            SheetBindingErrors<CommentSizeSheet> errors = mapper.loadDetail(in, CommentSizeSheet.class);

            CommentSizeSheet sheet = errors.getTarget();

            assertThat(sheet.comments, hasEntry("autoRow1", "1行"));
            assertThat(sheet.comments, hasEntry("autoRowMax", "1行\n2行\r\n3行\n4行"));
            assertThat(sheet.comments, hasEntry("autoRowMax1", "1行\n2行\r\n3行\n4行\n5行"));
            
            assertThat(sheet.comments, hasEntry("autoColumn1", "1列-111"));
            assertThat(sheet.comments, hasEntry("autoColumnMax", "1列-1112列-2223列-333"));
            assertThat(sheet.comments, hasEntry("autoColumnMax1", "1列-1112列-2223列-3334列-444"));
            
            assertThat(sheet.comments, hasEntry("directVertical", "1列-1112列-2223列-333\n2行-2222\n3行-333"));
            assertThat(sheet.comments, hasEntry("directHorizontal", "1列-1112列-2223列-333\n2行-2222\n3行-333"));
            
            Workbook book = WorkbookFactory.create(in2);
            Sheet xlsSheet = book.getSheet(sheet.sheetName);
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("autoRow1"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(1));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(1));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("autoRowMax"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(4));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(1));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("autoRowMax1"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(4));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(1));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("autoColumn1"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(1));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(1));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("autoColumnMax"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(1));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(3));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("autoColumnMax1"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(2));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(3));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("directVertical"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(2));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(3));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("directHorizontal"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(4));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(5));
            }
            
        }
    }
    
    /**
     * 書き込み時のテスト - コメントの削除設定
     */
    @Test
    public void test_save_comment_remove() throws Exception {
        
        // テストデータの作成
        CommentRemoveSheet outSheet = new CommentRemoveSheet();
        outSheet.comments = new HashMap<>();
        
        outSheet.value1 = "あああ";
        outSheet.value2 = "いいい";
        outSheet.value3 = null;
        
        outSheet.comments.put("value1", "コメントあり");
        outSheet.comments.put("value3", "コメントあり");
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile);
                InputStream in2 = new FileInputStream(outFile)) {
            SheetBindingErrors<CommentRemoveSheet> errors = mapper.loadDetail(in, CommentRemoveSheet.class);

            CommentRemoveSheet sheet = errors.getTarget();

            assertThat(sheet.comments, hasEntry("value1", "コメントあり"));
            assertThat(sheet.comments, not(hasKey("value2")));
            assertThat(sheet.comments, hasEntry("value3", "コメントあり"));
            
            Workbook book = WorkbookFactory.create(in2);
            Sheet xlsSheet = book.getSheet(sheet.sheetName);
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("value1"));
                Comment comment = cell.getCellComment();
                assertThat(comment, not(nullValue()));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("value2"));
                Comment comment = cell.getCellComment();
                assertThat(comment, nullValue());
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("value3"));
                Comment comment = cell.getCellComment();
                assertThat(comment, not(nullValue()));
            }

        }
        
    }
    
    /**
     * 書き込み時のテスト - 独自の処理
     */
    @Test
    public void test_save_comment_customHandler() throws Exception {
        
        // テストデータの作成
        CustomHandlerSheet outSheet = new CustomHandlerSheet();
        
        outSheet.value1 = "値1";
        outSheet.value2 = "値2";
        
        outSheet.comments = new HashMap<>();
        outSheet.comments.put("value1", "あいうえお。\nかきくけこ。");
        outSheet.comments.put("value2", "あいうえお。\nかきくけこ。");
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile);
                InputStream in2 = new FileInputStream(outFile)) {
            SheetBindingErrors<CustomHandlerSheet> errors = mapper.loadDetail(in, CustomHandlerSheet.class);

            CustomHandlerSheet sheet = errors.getTarget();

            assertThat(sheet.comments, hasEntry("value1", "あいうえお。\nかきくけこ。"));
            assertThat(sheet.comments, hasEntry("value2", "あいうえお。かきくけこ。"));
            
            Workbook book = WorkbookFactory.create(in2);
            Sheet xlsSheet = book.getSheet(sheet.sheetName);
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("value1"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(2));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(2));
            }
            
            {
                Cell cell = POIUtils.getCell(xlsSheet, sheet.positions.get("value2"));
                Comment comment = cell.getCellComment();
                ClientAnchor anchor = comment.getClientAnchor();
                assertThat(anchor.getRow2() - anchor.getRow1(), is(1));
                assertThat(anchor.getCol2() - anchor.getCol1(), is(1));
            }

        }
        
    }
    
    @XlsSheet(name="通常のテスト")
    private static class NormalSheet {
        
        private Map<String, CellPosition> positions;
        
        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsSheetName
        private String sheetName;
        
        @XlsCell(address="B4")
        private String directCell1;
        
        @XlsCell(address="E4")
        private String directCell2;
        
        @XlsLabelledCell(label = "装飾したセル", type = LabelledCellType.Right)
        private String labelledCell1;
        
        @XlsLabelledCell(label = "値が結合したセル", type = LabelledCellType.Right)
        private String labelledCell2;
        
        public String getDirectCell1() {
            return directCell1;
        }
        
        public void setDirectCell1(String directCell1) {
            this.directCell1 = directCell1;
        }
        
        public String getLabelledCell1() {
            return labelledCell1;
        }
        
        public String getDirectCell2() {
            return directCell2;
        }
        
        public void setDirectCell2(String directCell2) {
            this.directCell2 = directCell2;
        }
        
        public void setLabelledCell1(String labelledCell1) {
            this.labelledCell1 = labelledCell1;
        }
        
        public String getLabelledCell2() {
            return labelledCell2;
        }
        
        public void setLabelledCell2(String labelledCell2) {
            this.labelledCell2 = labelledCell2;
        }
        
        public void setDirectCell1Comment(String comment) {
            if(comments == null) {
                this.comments = new HashMap<>();
            }
            
            this.comments.put("directCell1", comment);
        }
        
        public void setDirectCell2Comment(String comment) {
            if(comments == null) {
                this.comments = new HashMap<>();
            }
            
            this.comments.put("directCell2", comment);
        }
    }
    
    @XlsSheet(name = "表示設定")
    private static class CommentVisibleSheet {
        
        private Map<String, CellPosition> positions;
        
        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsSheetName
        private String sheetName;

        @XlsCommentOption(visible = true)
        @XlsLabelledCell(label = "コメントの表示設定（true）", type = LabelledCellType.Right)
        private String visible;
        
        @XlsCommentOption(visible = false)
        @XlsLabelledCell(label = "コメントの表示設定（false）", type = LabelledCellType.Right)
        private String invisible;
        
    }
    
    @XlsSheet(name = "サイズ指定")
    private static class CommentSizeSheet {
        
        private Map<String, CellPosition> positions;
        
        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsSheetName
        private String sheetName;
        
        @XlsLabelledCell(label = "自動（1行）", type = LabelledCellType.Right)
        private String autoRow1;
        
        @XlsLabelledCell(label = "自動（最大行）", type = LabelledCellType.Right)
        private String autoRowMax;
        
        @XlsLabelledCell(label = "自動（最大行+1）", type = LabelledCellType.Right)
        private String autoRowMax1;
        
        @XlsLabelledCell(label = "自動（1列）", type = LabelledCellType.Right)
        private String autoColumn1;

        @XlsLabelledCell(label = "自動（最大列）", type = LabelledCellType.Right)
        private String autoColumnMax;

        @XlsLabelledCell(label = "自動（最大列+1）", type = LabelledCellType.Right)
        private String autoColumnMax1;
        
        @XlsCommentOption(verticalSize = 2)
        @XlsLabelledCell(label = "直接（縦）", type = LabelledCellType.Right)
        private String directVertical;
        
        @XlsCommentOption(horizontalSize = 5)
        @XlsLabelledCell(label = "直接（横）", type = LabelledCellType.Right)
        private String directHorizontal;
        
    }
    
    @XlsSheet(name = "空のとき削除")
    private static class CommentRemoveSheet {
        
        private Map<String, CellPosition> positions;
        
        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsSheetName
        private String sheetName;
        
        @XlsCommentOption(removeIfEmpty = true)
        @XlsLabelledCell(label = "コメントあり、値あり", type = LabelledCellType.Right)
        private String value1;
        
        @XlsCommentOption(removeIfEmpty = true)
        @XlsLabelledCell(label = "コメントあり、値なし", type = LabelledCellType.Right)
        private String value2;
        
        @XlsCommentOption(removeIfEmpty = true)
        @XlsLabelledCell(label = "コメントあり、セルの値なし", type = LabelledCellType.Right)
        private String value3;

        
    }
    
    @XlsSheet(name = "独自実装")
    private static class CustomHandlerSheet {
        
        private Map<String, CellPosition> positions;
        
        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsSheetName
        private String sheetName;
        
        @XlsLabelledCell(label = "標準の処理", type = LabelledCellType.Right)
        private String value1;
        
        @XlsCommentOption(handler = CustomCellCommentHandler.class)
        @XlsLabelledCell(label = "独自実装の処理", type = LabelledCellType.Right)
        private String value2;
        
        static class CustomCellCommentHandler extends DefaultCellCommentHandler {
            
            public CustomCellCommentHandler() {
                super();
                // 初期設定値の変更
                setMaxHorizontalSize(1);
                setMaxVerticalSize(1);
            }
            
            // 読み込み時の処理
            @Override
            public Optional<String> handleLoad(final Cell cell, Optional<XlsCommentOption> commentOption) {
                
                Optional<String> comment = super.handleLoad(cell, commentOption);
                
                // 改行を除去する。
                return comment.map(text -> text.replaceAll("\r|\n|\r\n", ""));
            }
            
            // 書き込み時の処理
            @Override
            public void handleSave(final Cell cell, final Optional<String> text, final Optional<XlsCommentOption> commentOption) {
                
                // 改行を除去する。
                text.map(comment -> comment.replaceAll("\r|\n|\r\n", ""))
                        .ifPresent(comment -> super.handleSave(cell, Optional.of(comment), commentOption));
                
            }
            
        }
    }
}
