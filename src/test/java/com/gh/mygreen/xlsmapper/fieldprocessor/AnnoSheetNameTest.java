package com.gh.mygreen.xlsmapper.fieldprocessor;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.SheetNameProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link SheetNameProcessor}のテスタ
 * アノテーション{@link XlsSheetName}のテスタ。
 *
 * @version 1.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoSheetNameTest {

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
    private File inputFile = new File("src/test/data/anno_SheetName.xlsx");

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_SheetName_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_SheetName_out.xlsx";

    /**
     * 読み込み時のテスト
     * @since 1.0
     */
    @Test
    public void test_load_sheetName_name() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);

            NormalSheet sheet = errors.getTarget();

            assertThat(sheet.sheetName, is("シート名（１）"));

        }

    }

    /**
     * 読み込み時のテスト - メソッドに付与したアノテーション
     * @since 1.0
     */
    @Test
    public void test_load_sheetName_methodAnno() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<MethodAnnoSheet> errors = mapper.loadDetail(in, MethodAnnoSheet.class);

            MethodAnnoSheet sheet = errors.getTarget();

            assertThat(sheet.sheetName, is("メソッドに付与したアノテーション"));

        }

    }

    /**
     * 書き込みのテスト - 通常のデータ
     */
    @Test
    public void test_save_sheetName_name() throws Exception {

        // テストデータの作成
        final NormalSheet outSheet = new NormalSheet();

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

            assertThat(outSheet.sheetName, is("シート名（１）"));
            assertThat(sheet.sheetName, is(outSheet.sheetName));

        }

    }

    /**
     * 書き込みのテスト - メソッドに付与したアノテーション
     * @since 1.0
     */
    @Test
    public void test_save_sheetName_methodAnno() throws Exception {

        // テストデータの作成
        final MethodAnnoSheet outSheet = new MethodAnnoSheet();

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

            assertThat(outSheet.sheetName, is("メソッドに付与したアノテーション"));
            assertThat(sheet.sheetName, is(outSheet.sheetName));

        }

    }

    /**
     * 名前によるシート指定
     *
     */
    @XlsSheet(name="シート名（１）")
    private static class NormalSheet {

        @XlsSheetName
        private String sheetName;

    }

    /**
     * メソッドにアノテーションを付与。
     * @since 1.0
     *
     */
    @XlsSheet(name="メソッドに付与したアノテーション")
    private static class MethodAnnoSheet {

        private String sheetName;

        @XlsSheetName
        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

    }

}
