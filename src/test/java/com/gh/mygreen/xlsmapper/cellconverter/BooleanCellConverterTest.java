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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsBooleanConverter;
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
import com.gh.mygreen.xlsmapper.cellconverter.impl.BooleanCellConverterFactory.BooleanCellConverter;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;

/**
 * {@link BooleanCellConverter}のテスタ
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class BooleanCellConverterTest {

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
     * boolean/Boolen型の読み込みテスト
     */
    @Test
    public void test_load_boolean() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<BooleanSheet> errors = mapper.loadDetail(in, BooleanSheet.class);
            BooleanSheet sheet = errors.getTarget();

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

    /**
     * 入力用のシートのレコードの値を検証する。
     * @param record
     * @param errors
     */
    private void assertRecord(final SimpleRecord record, final SheetBindingErrors<?> errors) {
        if(record.no == 1) {
            // 空文字
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(nullValue()));

        } else if(record.no == 2) {
            // Excelの型(true)
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));

        } else if(record.no == 3) {
            // Excelの型(false)
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(Boolean.FALSE));

        } else if(record.no == 4) {
            // 文字列(yes)
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));

        } else if(record.no == 5) {
            // 文字列(false)
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(Boolean.FALSE));

        } else if(record.no == 6) {
            // 不正な文字
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("b1")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[ブール型]:boolean型 - セル(B11)の値'abc'は、trueの値[true, 1, yes, on, y, t]、またはfalseの値[false, 0, no, off, f, n]の何れかの値で設定してください。"));
            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("b2")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[ブール型]:Boolean型 - セル(C11)の値' def'は、trueの値[true, 1, yes, on, y, t]、またはfalseの値[false, 0, no, off, f, n]の何れかの値で設定してください。"));

            }

        } else if(record.no == 7) {
            // 空白の文字
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("b1"))).isConversionFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("b2"))).isConversionFailure(), is(true));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }

    /**
     * 入力用のシートのレコードの値を検証する。
     * @param record
     * @param errors
     */
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));
            assertThat(record.b3, is(false));
            assertThat(record.b4, is(Boolean.FALSE));

        } else if(record.no == 2) {
            // Excelの型(true)
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));
            assertThat(record.b3, is(true));
            assertThat(record.b4, is(Boolean.TRUE));

        } else if(record.no == 3) {
            // Excelの型(false)
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(Boolean.FALSE));
            assertThat(record.b3, is(false));
            assertThat(record.b4, is(Boolean.FALSE));

        } else if(record.no == 4) {
            // 文字列(yes)
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));
            assertThat(record.b3, is(true));
            assertThat(record.b4, is(Boolean.TRUE));

        } else if(record.no == 5) {
            // 文字列(false)
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(Boolean.FALSE));
            assertThat(record.b3, is(false));
            assertThat(record.b4, is(Boolean.FALSE));

        } else if(record.no == 6) {
            // 不正な文字
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(Boolean.FALSE));
            assertThat(record.b3, is(false));
            assertThat(record.b4, is(Boolean.FALSE));

        } else if(record.no == 7) {
            // 空白の文字
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));
            assertThat(record.b3, is(true));
            assertThat(record.b4, is(Boolean.TRUE));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    /**
     * 入力用のシートのレコードの値を検証する。
     * @param record
     * @param errors
     */
    private void assertRecord(final FormulaRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(nullValue()));

        } else if(record.no == 2) {
            // Excelの型(true)
            assertThat(record.b1, is(true));
            assertThat(record.b2, is(Boolean.TRUE));

        } else if(record.no == 3) {
            // Excelの型(false)
            assertThat(record.b1, is(false));
            assertThat(record.b2, is(Boolean.FALSE));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    /**
     * boolean/Boolean型の書き込みテスト
     */
    @Test
    public void test_save_boolean() throws Exception {

        // テストデータの作成
        BooleanSheet outSheet = new BooleanSheet();

        // Converterアノテーションなしのデータ作成
        outSheet.add(new SimpleRecord()
                .comment("空文字"));

        outSheet.add(new SimpleRecord()
                .b1(true)
                .b2(true)
                .comment("Trueの値"));

        outSheet.add(new SimpleRecord()
                .b1(false)
                .b2(false)
                .comment("falseの値"));

        // Converterアノテーションありのデータ作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));

        outSheet.add(new FormattedRecord()
                .b1(true)
                .b2(true)
                .b3(true)
                .b4(true)
                .comment("Trueの値"));

        outSheet.add(new FormattedRecord()
                .b1(false)
                .b2(false)
                .b3(false)
                .b4(false)
                .comment("falseの値"));

        // 数式のデータ作成
        outSheet.add(new FormulaRecord().comment("1つ目"));
        outSheet.add(new FormulaRecord().comment("2つ目"));

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, "convert_boolean.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<BooleanSheet> errors = mapper.loadDetail(in, BooleanSheet.class);
            BooleanSheet sheet = errors.getTarget();

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
        assertThat(inRecord.b1, is(outRecord.b1));
        assertThat(inRecord.b2, is(outRecord.b2));
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
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.b1, is(false));
            assertThat(inRecord.b2, is(true));
            assertThat(inRecord.b3, is(false));
            assertThat(inRecord.b4, is(false));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.b1, is(outRecord.b1));
            assertThat(inRecord.b2, is(outRecord.b2));
            assertThat(inRecord.b3, is(outRecord.b3));
            assertThat(inRecord.b4, is(outRecord.b4));
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
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.b1, is(false));
            assertThat(inRecord.b2, is(Boolean.FALSE));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 2) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.b1, is(true));
            assertThat(inRecord.b2, is(Boolean.TRUE));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.b1, is(outRecord.b1));
            assertThat(inRecord.b2, is(outRecord.b2));
            assertThat(inRecord.comment, is(outRecord.comment));
        }

    }

    @XlsSheet(name="ブール型")
    private static class BooleanSheet {

        @XlsOrder(1)
        @XlsHorizontalRecords(tableLabel="ブール型（アノテーションなし）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SimpleRecord> simpleRecords;

        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="ブール型（初期値、書式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormattedRecord> formattedRecords;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="ブール型（数式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormulaRecord> formulaRecords;

        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public BooleanSheet add(SimpleRecord record) {
            if(simpleRecords == null) {
                this.simpleRecords = new ArrayList<>();
            }

            this.simpleRecords.add(record);
            record.no(simpleRecords.size());

            return this;
        }

        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public BooleanSheet add(FormattedRecord record) {
            if(formattedRecords == null) {
                this.formattedRecords = new ArrayList<>();
            }

            this.formattedRecords.add(record);
            record.no(formattedRecords.size());

            return this;
        }

        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public BooleanSheet add(FormulaRecord record) {
            if(formulaRecords == null) {
                this.formulaRecords = new ArrayList<>();
            }

            this.formulaRecords.add(record);
            record.no(formulaRecords.size());

            return this;
        }

    }

    /**
     * ブール値 - アノテーションなし
     *
     */
    private static class SimpleRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="boolean型")
        private boolean b1;

        @XlsColumn(columnName="Boolean型")
        private Boolean b2;

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

        public SimpleRecord b1(boolean b1) {
            this.b1 = b1;
            return this;
        }

        public SimpleRecord b2(Boolean b2) {
            this.b2 = b2;
            return this;
        }

        public SimpleRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    private static class FormattedRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsDefaultValue("true")
        @XlsTrim
        @XlsBooleanConverter(failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="boolean型")
        private boolean b1;

        @XlsDefaultValue("true")
        @XlsTrim
        @XlsBooleanConverter(failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="Boolean型")
        private Boolean b2;

        @XlsDefaultValue("abc")
        @XlsTrim
        @XlsBooleanConverter(loadForTrue={"○", "真"}, loadForFalse={"×", "偽", ""}, saveAsTrue="○", saveAsFalse="×",
            failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="boolean型(パターン)")
        private boolean b3;

        @XlsDefaultValue("def")
        @XlsTrim
        @XlsBooleanConverter(loadForTrue={"OK", "RIGHT"}, loadForFalse={"NOT", "-", ""}, saveAsTrue="OK", saveAsFalse="NOT",
            failToFalse=true, ignoreCase=false)
        @XlsColumn(columnName="Boolean型(パターン)")
        private Boolean b4;

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

        public FormattedRecord b1(boolean b1) {
            this.b1 = b1;
            return this;
        }

        public FormattedRecord b2(Boolean b2) {
            this.b2 = b2;
            return this;
        }

        public FormattedRecord b3(boolean b3) {
            this.b3 = b3;
            return this;
        }

        public FormattedRecord b4(Boolean b4) {
            this.b4 = b2;
            return this;
        }

        public FormattedRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }

    private static class FormulaRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsDefaultValue("false")
        @XlsTrim
        @XlsBooleanConverter(failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="boolean型")
        @XlsFormula(value="MOD(A${rowNumber},2)=0", primary=true)
        private boolean b1;

        @XlsTrim
        @XlsBooleanConverter(loadForTrue={"偶数"}, loadForFalse={"奇数"}, saveAsTrue="偶数", saveAsFalse="奇数",
            failToFalse=false, ignoreCase=false)
        @XlsColumn(columnName="Boolean型(パターン)")
        @XlsFormula(value="IF(MOD(A{rowNumber},2)=0, \"偶数\", \"奇数\")")
        private Boolean b2;

        @XlsColumn(columnName="備考")
        private String comment;

        public FormulaRecord no(int no) {
            this.no = no;
            return this;
        }

        public FormulaRecord b1(boolean b1) {
            this.b1 = b1;
            return this;
        }

        public FormulaRecord b2(Boolean b2) {
            this.b2 = b2;
            return this;
        }

        public FormulaRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }

}
