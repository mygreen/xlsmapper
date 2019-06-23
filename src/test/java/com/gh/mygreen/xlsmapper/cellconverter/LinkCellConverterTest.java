package com.gh.mygreen.xlsmapper.cellconverter;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.impl.URICellConverterFactory.URICellConverter;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;

/**
 * リンクの変換テスト。
 * <p>下記のConverterのテスタ
 * <ol>
 *   <li>{@link URICellConverter}</li>
 *   <li>{@link CellLink}</li>
 * </ol>
 *
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class LinkCellConverterTest {

    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }

    /**
     * エラーメッセージのコンバーター
     */
    private SheetErrorFormatter errorFormatter;

    @Before
    public void setUp() throws Exception {
        this.errorFormatter = new SheetErrorFormatter();
    }

    /**
     * リンクの読み込みテスト
     */
    @Test
    public void test_load_link() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<LinkSheet> errors = mapper.loadDetail(in, LinkSheet.class);

            LinkSheet sheet = errors.getTarget();

            if(sheet.simpleRecords != null) {
                for(SimpleRecord record : sheet.simpleRecords) {
                    assertRecord(record, errors);
                }
            }

            if(sheet.formattedRecords != null) {
                for(FormattedRecord record : sheet.formattedRecords) {
                    assertRecord(record, errors);
                }
            }

            if(sheet.formulaRecords != null) {
                for(FormulaRecord record : sheet.formulaRecords) {
                    assertRecord(record, errors);
                }
            }

        }
    }

    private void assertRecord(final SimpleRecord record, final SheetBindingErrors<?> errors) throws URISyntaxException {
        if(record.no == 1) {
            // 空文字
            assertThat(record.uri, is(nullValue()));
            assertThat(record.link, is(nullValue()));

        } else if(record.no == 2) {
            // URL（ラベルが同じ）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "http://www.google.co.jp/")));

        } else if(record.no == 3) {
            // URL（ラベルが異なる）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "Googleサイト")));

        } else if(record.no == 4) {
            // 文字列
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink(null, "http://www.google.co.jp/")));

        } else if(record.no == 5) {
            // メールアドレス
            assertThat(record.uri, is(new URI("mailto:hoge@google.com")));
            assertThat(record.link, is(new CellLink("mailto:hoge@google.com", "hoge@google.com")));

        } else if(record.no == 6) {
            // ファイルパス
            assertThat(record.uri, is(new URI("convert.xlsx")));
            assertThat(record.link, is(new CellLink("convert.xlsx", ".\\convert.xlsx")));

        } else if(record.no == 7) {
            // セルへのリンク
            assertThat(record.uri, is(new URI("リンク型!A1")));
            assertThat(record.link, is(new CellLink("リンク型!A1", "セルへのリンク")));

        } else if(record.no == 8) {
            // 不正なリンク＋ラベルに空白を含む
            assertThat(record.uri, is(new URI("http://invalid.uri")));
            assertThat(record.link, is(new CellLink("http://invalid.uri", "  空白を含むリンク  ")));

        } else if(record.no == 9) {
            // 空白の文字列
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("uri")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[リンク型]:URI - セル(B13)の値'  http://www.google.co.jp/  'は、URI(Uniform Resource Identifier)の形式として不正です。"));
            }

            assertThat(record.link, is(new CellLink(null, "  http://www.google.co.jp/  ")));

        } else if(record.no == 10) {
            // URL（ラベルが空白）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "  ")));

        } else if(record.no == 11) {
            // 空白の文字
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("uri")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[リンク型]:URI - セル(B15)の値'   'は、URI(Uniform Resource Identifier)の形式として不正です。"));

            }
            assertThat(record.link, is(new CellLink(null, "   ")));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    private void assertRecord(final FormattedRecord record, final SheetBindingErrors<?> errors) throws URISyntaxException {
        if(record.no == 1) {
            // 空文字
            assertThat(record.uri, is(new URI("http://myhome.com/")));
            assertThat(record.link, is(new CellLink("http://myhome.com", "http://myhome.com")));

        } else if(record.no == 2) {
            // URL（ラベルが同じ）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "http://www.google.co.jp/")));

        } else if(record.no == 3) {
            // URL（ラベルが異なる）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "Googleサイト")));

        } else if(record.no == 4) {
            // 文字列
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink(null, "http://www.google.co.jp/")));

        } else if(record.no == 5) {
            // メールアドレス
            assertThat(record.uri, is(new URI("mailto:hoge@google.com")));
            assertThat(record.link, is(new CellLink("mailto:hoge@google.com", "hoge@google.com")));

        } else if(record.no == 6) {
            // ファイルパス
            assertThat(record.uri, is(new URI("convert.xlsx")));
            assertThat(record.link, is(new CellLink("convert.xlsx", ".\\convert.xlsx")));

        } else if(record.no == 7) {
            // セルへのリンク
            assertThat(record.uri, is(new URI("リンク型!A1")));
            assertThat(record.link, is(new CellLink("リンク型!A1", "セルへのリンク")));

        } else if(record.no == 8) {
            // 不正なリンク＋ラベルに空白を含む
            assertThat(record.uri, is(new URI("http://invalid.uri")));
            assertThat(record.link, is(new CellLink("http://invalid.uri", "空白を含むリンク")));

        } else if(record.no == 9) {
            // 空白の文字列
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink(null, "http://www.google.co.jp/")));

        } else if(record.no == 10) {
            // URL（ラベルが空白）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "")));

        } else if(record.no == 11) {
            // 空白の文字
            assertThat(record.uri, is(new URI("http://myhome.com/")));
            assertThat(record.link, is(new CellLink("http://myhome.com", "http://myhome.com")));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }

    private void assertRecord(final FormulaRecord record, final SheetBindingErrors<?> errors) throws URISyntaxException {
        if(record.no == 1) {
            // 空文字
            assertThat(record.uri, is(nullValue()));
            assertThat(record.link, is(nullValue()));

        } else if(record.no == 2) {
            // URL（ラベルが同じ）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "リンク2")));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }

    /**
     * リンク型の書き込みテスト
     */
    @Test
    public void test_save_link() throws Exception {

        // テストデータの作成
        final LinkSheet outSheet = new LinkSheet();

        // アノテーションなしのデータ作成
        outSheet.add(new SimpleRecord()
                .comment("空文字"));

        outSheet.add(new SimpleRecord()
                .uri(new URI("http://www.google.co.jp/"))
                .link(new CellLink("http://www.google.co.jp/", "http://www.google.co.jp/"))
                .comment("URL（ラベルが同じ）"));

        outSheet.add(new SimpleRecord()
                .uri(new URI("http://www.google.co.jp/"))
                .link(new CellLink("http://www.google.co.jp/", "Googleサイト"))
                .comment("URL（ラベルが異なる）"));

        outSheet.add(new SimpleRecord()
                .uri(new URI("hoge@google.com"))
                .link(new CellLink("hoge@google.com", "hoge@google.com"))
                .comment("URL（メールアドレス）"));

        outSheet.add(new SimpleRecord()
                .uri(new URI("convert.xlsx"))
                .link(new CellLink("convert.xlsx", ".\\convert.xlsx"))
                .comment("ファイルパス"));

        outSheet.add(new SimpleRecord()
                .uri(new URI("A1"))
                .link(new CellLink("A1", "セルへのリンク"))
                .comment("セルへのリンク"));

        outSheet.add(new SimpleRecord()
                .uri(new URI("http://www.google.co.jp/"))
                .link(new CellLink("http://www.google.co.jp/", " 空白を含むリンク "))
                .comment("空白を含むリンク"));

        outSheet.add(new SimpleRecord()
            .uri(new URI("http://www.google.co.jp/"))
            .link(new CellLink("http://www.google.co.jp/", "  "))
            .comment("ラベルが空白"));

        // アノテーションありのデータ作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));

        outSheet.add(new FormattedRecord()
                .uri(new URI("http://www.google.co.jp/"))
                .link(new CellLink("http://www.google.co.jp/", "http://www.google.co.jp/"))
                .comment("URL（ラベルが同じ）"));

        outSheet.add(new FormattedRecord()
                .uri(new URI("http://www.google.co.jp/"))
                .link(new CellLink("http://www.google.co.jp/", "Googleサイト"))
                .comment("URL（ラベルが異なる）"));

        outSheet.add(new FormattedRecord()
                .uri(new URI("hoge@google.com"))
                .link(new CellLink("hoge@google.com", "hoge@google.com"))
                .comment("URL（メールアドレス）"));

        outSheet.add(new FormattedRecord()
                .uri(new URI("convert.xlsx"))
                .link(new CellLink("convert.xlsx", ".\\convert.xlsx"))
                .comment("ファイルパス"));

        outSheet.add(new FormattedRecord()
                .uri(new URI("A1"))
                .link(new CellLink("A1", "セルへのリンク"))
                .comment("セルへのリンク"));

        outSheet.add(new FormattedRecord()
                .uri(new URI("http://www.google.co.jp/"))
                .link(new CellLink("http://www.google.co.jp/", " 空白を含むリンク "))
                .comment("空白を含むリンク"));

        outSheet.add(new FormattedRecord()
            .uri(new URI("http://www.google.co.jp/"))
            .link(new CellLink("http://www.google.co.jp/", "  "))
            .comment("ラベルが空白"));


        // 数式のデータ
        outSheet.add(new FormulaRecord().comment("空文字"));
        outSheet.add(new FormulaRecord().comment("http://www.google.co.jp/"));

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, "convert_link.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<LinkSheet> errors = mapper.loadDetail(in, LinkSheet.class);

            LinkSheet sheet = errors.getTarget();

            if(sheet.simpleRecords != null) {
                assertThat(sheet.simpleRecords, hasSize(outSheet.simpleRecords.size()));

                for(int i=0; i < sheet.simpleRecords.size(); i++) {
                    assertRecord(sheet.simpleRecords.get(i), outSheet.simpleRecords.get(i), errors);
                }
            }

            if(sheet.formattedRecords != null) {
                assertThat(sheet.formattedRecords, hasSize(outSheet.formattedRecords.size()));

                for(int i=0; i < sheet.formattedRecords.size(); i++) {
                    assertRecord(sheet.formattedRecords.get(i), outSheet.formattedRecords.get(i), errors);
                }
            }

            if(sheet.formulaRecords != null) {
                assertThat(sheet.formulaRecords, hasSize(outSheet.formulaRecords.size()));

                for(int i=0; i < sheet.formulaRecords.size(); i++) {
                    assertRecord(sheet.formulaRecords.get(i), outSheet.formulaRecords.get(i), errors);
                }
            }

        }

    }

    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final SimpleRecord inRecord, final SimpleRecord outRecord, final SheetBindingErrors<?> errors) {

        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);

        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.uri, is(outRecord.uri));
        assertThat(inRecord.link, is(outRecord.link));
        assertThat(inRecord.comment, is(outRecord.comment));
    }

    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     * @throws URISyntaxException
     */
    private void assertRecord(final FormattedRecord inRecord, final FormattedRecord outRecord, final SheetBindingErrors<?> errors) throws URISyntaxException {

        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);

        if(inRecord.no == 1) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.uri, is(new URI("http://myhome.com/")));
            assertThat(inRecord.link, is(new CellLink("http://myhome.com", "http://myhome.com")));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 7) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(inRecord.link, is(new CellLink("http://www.google.co.jp/", "空白を含むリンク")));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 8) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(inRecord.link, is(new CellLink("http://www.google.co.jp/", "")));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.uri, is(outRecord.uri));
            assertThat(inRecord.link, is(outRecord.link));
            assertThat(inRecord.comment, is(outRecord.comment));

        }
    }

    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     * @throws URISyntaxException
     */
    private void assertRecord(final FormulaRecord inRecord, final FormulaRecord outRecord, final SheetBindingErrors<?> errors) throws URISyntaxException {

        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);

        if(inRecord.no == 1) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.uri, is(nullValue()));
            assertThat(inRecord.link, is(nullValue()));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 2) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(inRecord.link, is(new CellLink("http://www.google.co.jp/", "リンク2")));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            fail(String.format("not support test case. No=%d.", inRecord.no));
        }
    }

    @XlsSheet(name="リンク型")
    private static class LinkSheet {

        @XlsOrder(1)
        @XlsHorizontalRecords(tableLabel="リンク型（アノテーションなし）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SimpleRecord> simpleRecords;

        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="リンク型（初期値、書式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormattedRecord> formattedRecords;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="リンク型（数式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormulaRecord> formulaRecords;

        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public LinkSheet add(SimpleRecord record) {
            if(simpleRecords == null) {
                this.simpleRecords = new ArrayList<>();
            }
            this.simpleRecords.add(record);
            record.no(simpleRecords.size());
            return this;
        }

        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public LinkSheet add(FormattedRecord record) {
            if(formattedRecords == null) {
                this.formattedRecords = new ArrayList<>();
            }
            this.formattedRecords.add(record);
            record.no(formattedRecords.size());
            return this;
        }

        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public LinkSheet add(FormulaRecord record) {
            if(formulaRecords == null) {
                this.formulaRecords = new ArrayList<>();
            }
            this.formulaRecords.add(record);
            record.no(formulaRecords.size());
            return this;
        }
    }

    /**
     * リンク型 - アノテーションなし
     *
     */
    private static class SimpleRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="URI")
        private URI uri;

        @XlsColumn(columnName="CellLink")
        private CellLink link;

        @XlsColumn(columnName="備考")
        private String comment;

        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }

        public SimpleRecord no(int no) {
            this.no = no;
            return this;
        }

        public SimpleRecord uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public SimpleRecord link(CellLink link) {
            this.link = link;
            return this;
        }

        public SimpleRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    /**
     * リンク型 - 初期値など
     *
     */
    private static class FormattedRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsDefaultValue("http://myhome.com/")
        @XlsTrim
        @XlsColumn(columnName="URI")
        private URI uri;

        @XlsDefaultValue("http://myhome.com")
        @XlsTrim
        @XlsColumn(columnName="CellLink")
        private CellLink link;

        @XlsColumn(columnName="備考")
        private String comment;

        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }

        public FormattedRecord no(int no) {
            this.no = no;
            return this;
        }

        public FormattedRecord uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public FormattedRecord link(CellLink link) {
            this.link = link;
            return this;
        }

        public FormattedRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    /**
     * リンク型 - 数式な
     *
     */
    private static class FormulaRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="URI")
        @XlsFormula(methodName="getFormula1")
        private URI uri;

        @XlsColumn(columnName="CellLink")
        @XlsFormula(methodName="getFormula2")
        private CellLink link;

        @XlsColumn(columnName="備考")
        private String comment;

        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }

        public String getFormula1(Point point) {
            if(Utils.equals(comment, "空文字")) {
                return null;

            }

            final int rowNumber = point.y + 1;
            return String.format("HYPERLINK(D%d)", rowNumber);
        }

        public String getFormula2(Point point, Cell cell) {

            if(Utils.equals(comment, "空文字")) {
                return null;

            }

            // ダミーでリンクも設定する
            final CreationHelper helper = cell.getSheet().getWorkbook().getCreationHelper();
            final Hyperlink link = helper.createHyperlink(HyperlinkType.URL);
            link.setAddress(comment);
            cell.setHyperlink(link);

            final int rowNumber = point.y + 1;
            return String.format("HYPERLINK(D%s,\"リンク\"&A%s)", rowNumber, rowNumber);
        }

        public FormulaRecord no(int no) {
            this.no = no;
            return this;
        }

        public FormulaRecord uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public FormulaRecord link(CellLink link) {
            this.link = link;
            return this;
        }

        public FormulaRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

}
