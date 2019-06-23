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
import com.gh.mygreen.xlsmapper.annotation.XlsComment;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.CommentProcessor;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link CommentProcessor}のテスタ。
 * アノテーション{@link XlsComment}のテスタ。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class AnnoCommentTest {
    
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
    private File inputFile = new File("src/test/data/anno_Comment.xlsx");
    
    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_Comment_template.xlsx");
    
    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_Comment_out.xlsx";
    
    /**
     * 読み込みのテスト - 通常
     */
    @Test
    public void test_load_comment() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);

            NormalSheet sheet = errors.getTarget();

            assertThat(sheet.positions)
                .containsEntry("comment1", CellPosition.of("B3"))
                .containsEntry("comment2", CellPosition.of("C5"));
            
            assertThat(sheet.labels).isNull();
            
            assertThat(sheet.comments).isNull();
            
            assertThat(sheet.comment1).isEqualTo("コメント1");
            assertThat(sheet.comment2).isEqualTo("コメント2");

        }
        
    }

    /**
     * 読み込みのテスト - 不正なアノテーション - インデックスが範囲外
     */
    @Test
    public void test_load_comment_invalid_annotation1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            assertThatThrownBy(() -> mapper.load(in, InvalidAnno1Sheet1.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoCommentTest$InvalidAnno1Sheet1#c1'において、アノテーション'@XlsComment'の属性'row'の値'-1'は、0以上の値を設定してください。");
            
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - アドレスが不正
     */
    @Test
    public void test_load_comment_invalid_annotation2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            assertThatThrownBy(() -> mapper.load(in, InvalidAnnoSheet2.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoCommentTest$InvalidAnnoSheet2#c1'において、アノテーション'@XlsComment'の属性'address'の値'あいう'は、セルのアドレスの書式として不正です。");
            
        }
    }
    
    /**
     * 読み込みのテスト - 不正なアノテーション - String以外に付与している場合
     */
    @Test
    public void test_load_comment_invalid_annotation3() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            assertThatThrownBy(() -> mapper.load(in, InvalidAnnoSheet3.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoCommentTest$InvalidAnnoSheet3#c1'において、アノテーション'@XlsComment'を付与したタイプ'java.lang.Integer'はサポートしていません。'String'で設定してください。");
            
        }
    }
    
    /**
     * 読み込みのテスト - メソッドにアノテーションを付与
     */
    @Test
    public void test_load_comment_method_anno() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<MethodAnnoSheet> errors = mapper.loadDetail(in, MethodAnnoSheet.class);

            MethodAnnoSheet sheet = errors.getTarget();

            assertThat(sheet.comment1Position).isEqualTo(CellPosition.of("B3"));
            assertThat(sheet.comment2Position).isEqualTo(CellPosition.of("C5"));
            
            assertThat(sheet.labels).isNull();
            
            assertThat(sheet.comments).isNull();
            
            assertThat(sheet.comment1).isEqualTo("コメント1");
            assertThat(sheet.comment2).isEqualTo("コメント2");

        }
        
    }
    
    /**
     * 書込みのテスト - 通常
     */
    @Test
    public void test_save_comment() throws Exception {
        
        // テストデータの作成
        final NormalSheet outSheet = new NormalSheet();
        
        outSheet.comment1("コメント1")
            .comment2("コメント2");
        
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
                .containsEntry("comment1", CellPosition.of("B3"))
                .containsEntry("comment2", CellPosition.of("C5"));
        
            assertThat(sheet.labels).isNull();
            
            assertThat(sheet.comments).isNull();

            assertThat(sheet.comment1).isEqualTo(outSheet.comment1);
            assertThat(sheet.comment2).isEqualTo(outSheet.comment2);
        }
            
    }
    
    /**
     * 書込みのテスト - メソッドにアノテーションを付与
     */
    @Test
    public void test_save_comment_method_anno() throws Exception {
        
        // テストデータの作成
        final MethodAnnoSheet outSheet = new MethodAnnoSheet();
        
        outSheet.comment1("コメント1")
            .comment2("コメント2");
        
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
            
            assertThat(sheet.comment1Position).isEqualTo(CellPosition.of("B3"));
            assertThat(sheet.comment2Position).isEqualTo(CellPosition.of("C5"));
        
            assertThat(sheet.labels).isNull();
            
            assertThat(sheet.comments).isNull();

            assertThat(sheet.comment1).isEqualTo(outSheet.comment1);
            assertThat(sheet.comment2).isEqualTo(outSheet.comment2);
        }
            
    }
    
    @XlsSheet(name = "通常")
    private static class NormalSheet {
        
        private Map<String, CellPosition> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        @XlsComment(address = "B3")
        private String comment1;
        
        @XlsComment(row = 4, column = 2)
        private String comment2;
        
        public NormalSheet comment1(String comment1) {
            this.comment1 = comment1;
            return this;
        }
        
        public NormalSheet comment2(String comment2) {
            this.comment2 = comment2;
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
    
    /**
     * 不正なアノテーションの使い方 - インデックスが範囲外
     *
     */
    @XlsSheet(name = "通常")
    private static class InvalidAnno1Sheet1 {
        
        @XlsComment(column=-1, row=-1)
        private String c1;
        
    }
    
    /**
     * 不正なアノテーションの使い方 - アドレスが不正
     * 
     */
    @XlsSheet(name = "通常")
    private static class InvalidAnnoSheet2 {
        
        @XlsComment(address="あいう")
        private String c1;
        
    }
    
    /**
     * 不正なアノテーションの使い方 - String以外に付与する場合
     * 
     */
    @XlsSheet(name = "通常")
    private static class InvalidAnnoSheet3 {
        
        @XlsComment(address="B3")
        private Integer c1;
        
    }
    
    /**
     * メソッドにアノテーションを付与
     *
     *
     * @author T.TSUCHIE
     *
     */
    @XlsSheet(name="通常")
    private static class MethodAnnoSheet {
        
        private Map<String, String> labels;
        
        private Map<String, String> comments;
        
        private String comment1;
        
        private String comment2;
        
        private CellPosition comment1Position;
        
        private CellPosition comment2Position;
        
        @XlsComment(address = "B3")
        public String getComment1() {
            return comment1;
        }
        
        @XlsComment(row = 4, column = 2)
        public String getComment2() {
            return comment2;
        }
        
        public void setComment1Position(int x, int y) {
            this.comment1Position = CellPosition.of(y, x);
        }
        
        public void setComment2Position(int x, int y) {
            this.comment2Position = CellPosition.of(y, x);
        }

        
        public MethodAnnoSheet comment1(String comment1) {
            this.comment1 = comment1;
            return this;
        }
        
        public MethodAnnoSheet comment2(String comment2) {
            this.comment2 = comment2;
            return this;
        }

        public MethodAnnoSheet comment(String key, String text) {
            if(comments == null) {
                this.comments = new HashMap<String, String>();
            }
            this.comments.put(key, text);
            return this;
        }

    }
    
}
