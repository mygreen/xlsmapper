package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledComment;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.LabelledCommentProcessor;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link LabelledCommentProcessor}のテスタ
 * アノテーション{@link XlsLabelledComment}のテスタ。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class AnnoLabelledCommentTest {
    
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
    private File inputFile = new File("src/test/data/anno_LabelledComment.xlsx");

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_LabelledComment_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_LabelledComment_out.xlsx";
    
    /**
     * 読み込みのテスト - 通常
     */
    @Test
    public void test_load_labelled_comment() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);

            NormalSheet sheet = errors.getTarget();

            assertThat(sheet.positions)
                .containsEntry("label1", CellPosition.of("B3"))
                .containsEntry("label2", CellPosition.of("C7"));
            
            assertThat(sheet.labels)
                .containsEntry("label1", "ラベル名")
                .containsEntry("label2", "ラベル名");
            
            assertThat(sheet.comments).isNull();
            
            assertThat(sheet.label1).isEqualTo("コメント1");
            assertThat(sheet.label2).isEqualTo("コメント2");
            assertThat(sheet.label3).isNull();

        }
        
    }
    
    /**
     * 読み込みのテスト - ラベルのセルが見つからない
     */
    @Test
    public void test_load_labelled_comment_notFoundCell() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            assertThatThrownBy(() -> mapper.loadDetail(in, NotFoundCellSheet.class))
                .isInstanceOf(CellNotFoundException.class)
                .hasMessage("シート'通常'において、ラベル'見つからない'を持つセルが見つかりません。");
            
        }
        
    }
    
    /**
     * 読み込みのテスト - ラベルの指定がない
     */
    @Test
    public void test_load_labelled_comment_invalidAnno() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            assertThatThrownBy(() -> mapper.loadDetail(in, InvalidAnnoSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoLabelledCommentTest$InvalidAnnoSheet#label1'において、アノテーション'@XlsLabelledComment'の属性'labelRow'の値'-1'は、0以上の値を設定してください。");
            
        }
        
    }
    
    /**
     * 書込みのテスト - 通常
     */
    @Test
    public void test_save_comment() throws Exception {
        
        // テストデータの作成
        final NormalSheet outSheet = new NormalSheet();
        
        outSheet.label1("コメント1")
            .label2("コメント2");
        
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
            
            assertThat(sheet.positions)
                .containsEntry("label1", outSheet.positions.get("label1"))
                .containsEntry("label2", outSheet.positions.get("label2"));
        
            assertThat(sheet.labels)
                .containsEntry("label1", outSheet.labels.get("label1"))
                .containsEntry("label2", outSheet.labels.get("label2"));
            
            assertThat(sheet.comments).isNull();
            
            assertThat(sheet.label1).isEqualTo(outSheet.label1);
            assertThat(sheet.label2).isEqualTo(outSheet.label2);
            assertThat(sheet.label3).isNull();
        
        }
        
    }
    
    @XlsSheet(name = "通常")
    private static class NormalSheet {
        
        private Map<String, CellPosition> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsLabelledComment(label = "ラベル名")
        private String label1;
        
        @XlsLabelledComment(label = "ラベル名", headerLabel = "見出し1")
        private String label2;
        
        @XlsLabelledComment(label = "存在しない", optional = true)
        private String label3;
        
        public NormalSheet label1(String label1) {
            this.label1 = label1;
            return this;
        }
        
        public NormalSheet label2(String label2) {
            this.label2 = label2;
            return this;
        }

        public NormalSheet comment(String key, String text) {
            if(comments == null) {
                this.comments = new HashMap<String, String>();
            }
            this.comments.put(key, text);
            return this;
        }
        
    }
    
    @XlsSheet(name = "通常")
    private static class NotFoundCellSheet {
        
        private Map<String, CellPosition> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsLabelledComment(label = "見つからない")
        private String label1;
        
    }
    
    @XlsSheet(name = "通常")
    private static class InvalidAnnoSheet {
        
        private Map<String, CellPosition> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsLabelledComment()
        private String label1;
        
    }
}
