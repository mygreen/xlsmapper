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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * 文字列の変換処理のテスタ
 *
 * @version 1.5
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class TextCellConverterTest {

    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }

    /**
     * 文字列型の読み込みテスト
     */
    @Test
    public void test_load_text() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<TextSheet> errors = mapper.loadDetail(in, TextSheet.class);

            TextSheet sheet = errors.getTarget();

            for(SimpleRecord record : sheet.simpleRecords) {
                assertRecord(record, errors);
            }

            for(FormattedRecord record : sheet.formattedRecords) {
                assertRecord(record, errors);
            }

            for(FormulaRecord record : sheet.formulaRecords) {
                assertRecord(record, errors);
            }

        }
    }

    private void assertRecord(final SimpleRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.t, is(nullValue()));
            assertThat(record.c1, is((char)0));
            assertThat(record.c2, is(nullValue()));

        } else if(record.no == 2) {
            // 通常の文字
            assertThat(record.t, is("こんにちは"));
            assertThat(record.c1, is('あ'));
            assertThat(record.c2, is('か'));

        } else if(record.no == 3) {
            // 改行
            assertThat(record.t, is("こんにちは\n今日はいい天気ですね。"));
            assertThat(record.c1, is('あ'));
            assertThat(record.c2, is('か'));

        } else if(record.no == 4) {
            // 数値
            assertThat(record.t, is("12.34"));
            assertThat(record.c1, is('1'));
            assertThat(record.c2, is('1'));

        } else if(record.no == 5) {
            // 日時
            assertThat(record.t, is("平成２７年１月２日"));
            assertThat(record.c1, is('平'));
            assertThat(record.c2, is('平'));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    private void assertRecord(final FormattedRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.t1, is("Hello"));
            assertThat(record.t2, is(""));
            assertThat(record.c1, is('a'));
            assertThat(record.c2, is('d'));

        } else if(record.no == 2) {
            // 通常の文字
            assertThat(record.t1, is("こんにちは"));
            assertThat(record.t2, is("こんばんは"));
            assertThat(record.c1, is('あ'));
            assertThat(record.c2, is('か'));


        } else if(record.no == 3) {
            // 前後に空白
            assertThat(record.t1, is(" こんにちは   "));
            assertThat(record.t2, is("こんばんは\n今日はいい星空ですね。"));
            assertThat(record.c1, is('あ'));
            assertThat(record.c2, is('か'));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    private void assertRecord(final FormulaRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.t1, is(nullValue()));
            assertThat(record.c1, is('\u0000'));
            assertThat(record.c2, is(nullValue()));

        } else if(record.no == 2) {
            assertThat(record.t1, is("ABCDEF"));
            assertThat(record.c1, is('F'));
            assertThat(record.c2, is('A'));


        } else if(record.no == 3) {
            assertThat(record.t1, is("こんにちは"));
            assertThat(record.c1, is('は'));
            assertThat(record.c2, is('こ'));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    /**
     * 文字列型の書き込みテスト
     */
    @Test
    public void test_save_text() throws Exception {

        // テストデータの作成
        final TextSheet outSheet = new TextSheet();

        // アノテーションなしのデータ作成
        outSheet.add(new SimpleRecord()
                .comment("空文字"));

        outSheet.add(new SimpleRecord()
                .t("こんにちは")
                .c1("あ".charAt(0))
                .c2("か".charAt(0))
                .comment("通常の文字"));

        outSheet.add(new SimpleRecord()
                .t("こんにちは\n今日はいい天気ですね。")
                .c1("\n".charAt(0))
                .c2("\n".charAt(0))
                .comment("改行"));

        // アノテーションありのデータ作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));

        outSheet.add(new FormattedRecord()
                .t1("こんにちは")
                .t2("こんばんは")
                .c1("あ".charAt(0))
                .c2("か".charAt(0))
                .comment("通常の文字"));

        outSheet.add(new FormattedRecord()
                .t1("\n\nこんにちは\n今日はいい天気ですね。   ")
                .t2(" こんばんは\n今日はいい星空ですね。\n")
                .c1(" ".charAt(0))
                .c2("\n".charAt(0))
                .comment("改行＋前後に空白"));

        // 数式のデータ作成
        outSheet.add(new FormulaRecord().comment(null));
        outSheet.add(new FormulaRecord().comment("   AbCdeF   "));
        outSheet.add(new FormulaRecord().comment("   こんにちは   "));

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, "convert_text.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<TextSheet> errors = mapper.loadDetail(in, TextSheet.class);

            TextSheet sheet = errors.getTarget();

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
        assertThat(inRecord.t, is(outRecord.t));
        assertThat(inRecord.c1, is(outRecord.c1));
        assertThat(inRecord.c2, is(outRecord.c2));
        assertThat(inRecord.comment, is(outRecord.comment));

    }

    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final FormattedRecord inRecord, final FormattedRecord outRecord, final SheetBindingErrors<?> errors) {

        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);

        if(inRecord.no == 1) {
            // 初期値の確認
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is("Hello"));
            assertThat(inRecord.t2, is(""));
            assertThat(inRecord.c1, is("abc".charAt(0)));
            assertThat(inRecord.c2, is("def".charAt(0)));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 2) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is(outRecord.t1));
            assertThat(inRecord.t2, is(outRecord.t2));
            assertThat(inRecord.c1, is("あ".charAt(0)));
            assertThat(inRecord.c2, is("か".charAt(0)));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 3) {
            // トリムの確認
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is(outRecord.t1));
            assertThat(inRecord.t2, is(outRecord.t2.trim()));
            assertThat(inRecord.c1, is("abc".charAt(0)));
            assertThat(inRecord.c2, is("def".charAt(0)));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is(outRecord.t1));
            assertThat(inRecord.t2, is(outRecord.t2));
            assertThat(inRecord.c1, is(outRecord.c1));
            assertThat(inRecord.c2, is(outRecord.c2));
            assertThat(inRecord.comment, is(outRecord.comment));
        }


    }

    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final FormulaRecord inRecord, final FormulaRecord outRecord, final SheetBindingErrors<?> errors) {

        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);

        if(inRecord.no == 1) {
            // 空文字の確認
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is(nullValue()));
            assertThat(inRecord.c1, is('\u0000'));
            assertThat(inRecord.c2, is(nullValue()));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 2) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is("ABCDEF"));
            assertThat(inRecord.c1, is('F'));
            assertThat(inRecord.c2, is('A'));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 3) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is("こんにちは"));
            assertThat(inRecord.c1, is('は'));
            assertThat(inRecord.c2, is('こ'));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.t1, is(outRecord.t1));
            assertThat(inRecord.c1, is(outRecord.c1));
            assertThat(inRecord.c2, is(outRecord.c2));
            assertThat(inRecord.comment, is(outRecord.comment));
        }


    }

    @XlsSheet(name="文字列型")
    private static class TextSheet {

        @XlsOrder(1)
        @XlsHorizontalRecords(tableLabel="文字列型（アノテーションなし）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SimpleRecord> simpleRecords;

        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="文字列型（初期値、書式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormattedRecord> formattedRecords;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="文字列型（数式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormulaRecord> formulaRecords;

        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public TextSheet add(SimpleRecord record) {
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
        public TextSheet add(FormattedRecord record) {
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
        public TextSheet add(FormulaRecord record) {
            if(formulaRecords == null) {
                this.formulaRecords = new ArrayList<>();
            }
            this.formulaRecords.add(record);
            record.no(formulaRecords.size());
            return this;
        }
    }

    /**
     * 文字列型 - アノテーションなし
     *
     */
    private static class SimpleRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="String型")
        private String t;

        @XlsColumn(columnName="char型")
        private char c1;

        @XlsColumn(columnName="Character型")
        private Character c2;

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

        public SimpleRecord t(String t) {
            this.t = t;
            return this;
        }

        public SimpleRecord c1(char c1) {
            this.c1 = c1;
            return this;
        }

        public SimpleRecord c2(Character c2) {
            this.c2 = c2;
            return this;
        }

        public SimpleRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    /**
     * 文字列型 - アノテーションあり
     */
    private static class FormattedRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsDefaultValue("Hello")
        @XlsColumn(columnName="String型(初期値)")
        private String t1;

        @XlsTrim
        @XlsColumn(columnName="String型（トリム）")
        private String t2;

        @XlsTrim
        @XlsDefaultValue("abc")
        @XlsColumn(columnName="char型")
        private char c1;

        @XlsTrim
        @XlsDefaultValue("def")
        @XlsColumn(columnName="Character型")
        private Character c2;

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

        public FormattedRecord t1(String t1) {
            this.t1 = t1;
            return this;
        }

        public FormattedRecord t2(String t2) {
            this.t2 = t2;
            return this;
        }

        public FormattedRecord c1(char c1) {
            this.c1 = c1;
            return this;
        }

        public FormattedRecord c2(Character c2) {
            this.c2 = c2;
            return this;
        }

        public FormattedRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    /**
     * 文字列型 - 数式
     */
    private static class FormulaRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="String型")
        @XlsFormula("UPPER(TRIM(E{rowNumber}))")
        private String t1;

        @XlsColumn(columnName="char型")
        @XlsFormula("RIGHT(TRIM(E${rowNumber}))")
        private char c1;

        @XlsColumn(columnName="Character型")
        @XlsFormula("LEFT(TRIM(E${rowNumber}))")
        private Character c2;

        @XlsColumn(columnName="備考")
        private String comment;

        public FormulaRecord no(int no) {
            this.no = no;
            return this;
        }

        public FormulaRecord t1(String t1) {
            this.t1 = t1;
            return this;
        }

        public FormulaRecord c1(char c1) {
            this.c1 = c1;
            return this;
        }

        public FormulaRecord c2(Character c2) {
            this.c2 = c2;
            return this;
        }

        public FormulaRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }
}
