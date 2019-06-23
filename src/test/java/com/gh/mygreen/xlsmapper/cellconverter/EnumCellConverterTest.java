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
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsEnumConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.impl.EnumCellConverterFactory.EnumCellConverter;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;


/**
 * {@link EnumCellConverter}のテスタ
 *
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class EnumCellConverterTest {

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
     * 列挙型の読み込みテスト
     */
    @Test
    public void test_load_enum() throws Exception {
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<EnumSheet> errors = mapper.loadDetail(in, EnumSheet.class);
            EnumSheet sheet = errors.getTarget();

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

    private void assertRecord(final SimpleRecord record, final SheetBindingErrors<?> errors) {
        if(record.no == 1) {
            // 空文字
            assertThat(record.color, is(nullValue()));
            assertThat(record.operate, is(nullValue()));

        } else if(record.no == 2) {
            // 正しい値
            assertThat(record.color, is(Color.Red));
            assertThat(record.operate, is(Operate.Refer));

        } else if(record.no == 3) {
            // 不正な値
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("color")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字）1 - セル(B8)の値'Blue'は、何れかの値[Red, Green, Yellow]で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("operate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字）2 - セル(C8)の値'Remove'は、何れかの値[Refer, Edit, Delete]で設定してください。"));

            }

        } else if(record.no == 4) {
            // 小文字
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("color")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字）1 - セル(B9)の値'yellow'は、何れかの値[Red, Green, Yellow]で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("operate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字）2 - セル(C9)の値'edit'は、何れかの値[Refer, Edit, Delete]で設定してください。"));

            }

        } else if(record.no == 5) {
            // 空白
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("color")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字）1 - セル(B10)の値' Yellow  'は、何れかの値[Red, Green, Yellow]で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("operate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字）2 - セル(C10)の値' Edit 'は、何れかの値[Refer, Edit, Delete]で設定してください。"));

            }

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }

    private void assertRecord(final FormattedRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.color, is(Color.Red));
            assertThat(record.operate, is(Operate.Refer));

        } else if(record.no == 2) {
            // 正しい値
            assertThat(record.color, is(Color.Red));
            assertThat(record.operate, is(Operate.Refer));

        } else if(record.no == 3) {
            // 不正な値
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("color")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（英字） - セル(B17)の値'Blue'は、何れかの値[Red, Green, Yellow]で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("operate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[列挙型]:Enum型（日本語） - セル(C17)の値'追加'は、何れかの値[参照, 編集, 削除]で設定してください。"));

            }

        } else if(record.no == 4) {
            // 小文字
            assertThat(record.color, is(Color.Yellow));
            assertThat(record.operate, is(Operate.Edit));

        } else if(record.no == 5) {
            // 空白
            assertThat(record.color, is(Color.Yellow));
            assertThat(record.operate, is(Operate.Edit));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    private void assertRecord(final FormulaRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.color, is(nullValue()));
            assertThat(record.operate, is(nullValue()));

        } else if(record.no == 2) {
            // 正しい値
            assertThat(record.color, is(Color.Red));
            assertThat(record.operate, is(Operate.Refer));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    /**
     * 列挙型の書き込みテスト
     */
    @Test
    public void test_save_enum() throws Exception {

        // テストデータの作成
        final EnumSheet outSheet = new EnumSheet();

        // アノテーションなしのデータ作成
        outSheet.add(new SimpleRecord()
                .comment("空文字"));

        outSheet.add(new SimpleRecord()
                .color(Color.Green)
                .operate(Operate.Edit)
                .comment("値"));

        // アノテーションありのデータ作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));

        outSheet.add(new FormattedRecord()
                .color(Color.Green)
                .operate(Operate.Edit)
                .comment("値"));

        // 数式のデータ作成
        outSheet.add(new FormulaRecord().comment("空文字"));
        outSheet.add(new FormulaRecord().comment("R"));

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, "convert_enum.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<EnumSheet> errors = mapper.loadDetail(in, EnumSheet.class);
            EnumSheet sheet = errors.getTarget();

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
        assertThat(inRecord.color, is(outRecord.color));
        assertThat(inRecord.operate, is(outRecord.operate));
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
            assertThat(inRecord.color, is(Color.Red));
            assertThat(inRecord.operate, is(Operate.Refer));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.color, is(outRecord.color));
            assertThat(inRecord.operate, is(outRecord.operate));
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
            assertThat(inRecord.color, is(nullValue()));
            assertThat(inRecord.operate, is(nullValue()));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.color, is(Color.Red));
            assertThat(inRecord.operate, is(Operate.Refer));
            assertThat(inRecord.comment, is(outRecord.comment));
        }
    }

    /**
     * 列挙型 - 単純な列挙型
     */
    private enum Color {
        Red,
        Green,
        Yellow,
        ;
    }

    /**
     * 列挙型 - メソッド、フィールドを持つ
     */
    private enum Operate {
        Refer("参照"),
        Edit("編集"),
        Delete("削除"),
        ;

        private final String localeName;

        private Operate(String localeName) {
            this.localeName = localeName;
        }

        public String localeName() {
            return localeName;
        }

    }

    @XlsSheet(name="列挙型")
    private static class EnumSheet {

        @XlsOrder(1)
        @XlsHorizontalRecords(tableLabel="列挙型（アノテーションなし）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SimpleRecord> simpleRecords;

        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="列挙型（初期値、書式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormattedRecord> formattedRecords;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="列挙型（数式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormulaRecord> formulaRecords;

        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public EnumSheet add(SimpleRecord record) {
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
        public EnumSheet add(FormattedRecord record) {
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
        public EnumSheet add(FormulaRecord record) {
            if(formulaRecords == null) {
                this.formulaRecords = new ArrayList<>();
            }
            this.formulaRecords.add(record);
            record.no(formulaRecords.size());
            return this;
        }
    }

    /**
     * 列挙型 - アノテーションなし
     *
     */
    private static class SimpleRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="Enum型（英字）1")
        private Color color;

        @XlsColumn(columnName="Enum型（英字）2")
        private Operate operate;

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

        public SimpleRecord color(Color color) {
            this.color = color;
            return this;
        }

        public SimpleRecord operate(Operate operate) {
            this.operate = operate;
            return this;
        }

        public SimpleRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }

    /**
     * 列挙型 - 初期値など
     *
     */
    private static class FormattedRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsDefaultValue("Red")
        @XlsTrim
        @XlsEnumConverter(ignoreCase=true)
        @XlsColumn(columnName="Enum型（英字）")
        private Color color;

        @XlsDefaultValue("参照")
        @XlsTrim
        @XlsEnumConverter(ignoreCase=true, aliasMethod="localeName")
        @XlsColumn(columnName="Enum型（日本語）")
        private Operate operate;

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

        public FormattedRecord color(Color color) {
            this.color = color;
            return this;
        }

        public FormattedRecord operate(Operate operate) {
            this.operate = operate;
            return this;
        }

        public FormattedRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    /**
     * 列挙型 - 数式
     *
     */
    private static class FormulaRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsEnumConverter(ignoreCase=true)
        @XlsColumn(columnName="Enum型（英字）")
        @XlsFormula("IF(\\$D{rowNumber}=\"R\",\"Red\",\"\")")
        private Color color;

        @XlsEnumConverter(ignoreCase=true, aliasMethod="localeName")
        @XlsColumn(columnName="Enum型（日本語）")
        @XlsFormula("IF(\\$D{rowNumber}=\"R\",\"参照\",\"\")")
        private Operate operate;

        @XlsColumn(columnName="備考")
        private String comment;

        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }

        public FormulaRecord no(int no) {
            this.no = no;
            return this;
        }

        public FormulaRecord color(Color color) {
            this.color = color;
            return this;
        }

        public FormulaRecord operate(Operate operate) {
            this.operate = operate;
            return this;
        }

        public FormulaRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }
}
