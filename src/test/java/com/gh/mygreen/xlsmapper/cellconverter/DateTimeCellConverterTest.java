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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
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
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;

/**
 * 日付、時刻型のタイプのチェック
 *
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class DateTimeCellConverterTest {

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

    @Test
    public void test_load_date_time() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<DateTimeSheet> errors = mapper.loadDetail(in, DateTimeSheet.class);
            DateTimeSheet sheet = errors.getTarget();

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
            assertThat(record.utilDate, is(nullValue()));
            assertThat(record.sqlDate, is(nullValue()));
            assertThat(record.sqlTime, is(nullValue()));
            assertThat(record.timestamp, is(nullValue()));

        } else if(record.no == 2) {
            // Excelの日時型
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));

        } else if(record.no == 3) {
            // 文字列型の場合
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-01-02 00:00:00.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("1899-12-31 03:45:06.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));

        } else if(record.no == 4) {
            // 文字列型の場合
            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("utilDate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Dateクラス(util) - セル(B10)の値'a01'は、'yyyy-MM-dd HH:mm:ss'の日時形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("calendar")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Calendarクラス - セル(C10)の値'a01'は、'yyyy-MM-dd HH:mm:ss'の日時形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("sqlDate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Dateクラス(sql) - セル(D10)の値'b02'は、'yyyy-MM-dd'の日付形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("sqlTime")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Timeクラス(sql) - セル(E10)の値'c03'は、'HH:mm:ss'の時刻形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("timestamp")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Timesamp(sql) - セル(F10)の値'd04'は、'yyyy-MM-dd HH:mm:ss.SSS'のタイムスタンプ形式で設定してください。"));

            }

        } else if(record.no == 5) {
            // Excelの日時型（日本語）
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));

        } else if(record.no == 6) {
            // Excelの数値型
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));

        } else if(record.no == 7) {
            // Excelの関数型
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    private void assertRecord(final FormattedRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2000-12-31 03:41:12"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2000-12-31 03:41:12"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2000-12-31 00:00:00.000"))));
            assertThat(record.sqlTime, is(toSqlDate(toTimestamp("1899-12-31 00:00:00.000"))));
            assertThat(record.timestamp, is(toTimestamp("1999-12-31 10:12:00.000")));

        } else if(record.no == 2) {
            // 文字列型の場合（正常）
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-12-31 00:00:00.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("1899-12-31 12:03:00.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:00.000")));

        } else if(record.no == 3) {
            // 文字列型の場合（存在しない日付）

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("utilDate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Dateクラス(util) - セル(B22)の値'2015-13-02 03:45:06'は、'yyyy-MM-dd HH:mm:ss'の日時形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("calendar")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Calendarクラス - セル(C22)の値'2015-13-02 03:45:06'は、'yyyy-MM-dd HH:mm:ss'の日時形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("sqlDate")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Dateクラス(sql) - セル(D22)の値'2015-12-40'は、'yyyy-MM-dd'の日付形式で設定してください。"));

            }

            {
                FieldError fieldError = cellFieldError(errors, cellAddress(record.positions.get("sqlTime")));
                assertThat(fieldError.isConversionFailure(), is(true));

                String message = errorFormatter.format(fieldError);
                assertThat(message, is("[日時型]:Timeクラス(sql) - セル(E22)の値'12時80分'は、'H時m分'の時刻形式で設定してください。"));

            }

            assertThat(record.timestamp, is(toTimestamp("2016-01-02 03:45:00.000")));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    private void assertRecord(final FormulaRecord record, final SheetBindingErrors<?> errors) {

        if(record.no == 1) {
            // 空文字
            assertThat(record.utilDate, is(nullValue()));
            assertThat(record.calendar, is(nullValue()));
            assertThat(record.sqlDate, is(nullValue()));
            assertThat(record.sqlTime, is(nullValue()));
            assertThat(record.timestamp, is(nullValue()));

        } else if(record.no == 2) {
            // 文字列型の場合（正常）
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(record.timestamp, is(toTimestamp("1904-01-26 23:04:00.000")));

        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }

    }

    /**
     * 日時型の書き込みテスト
     */
    @Test
    public void test_save_date_time() throws Exception {

        // テストデータの作成
        final DateTimeSheet outSheet = new DateTimeSheet();

        // アノテーションなしのデータ型の作成
        outSheet.add(new SimpleRecord()
                .comment("空文字"));

        outSheet.add(new SimpleRecord()
            .utilDate(toUtilDate(toTimestamp("2015-01-02 03:45:06.000")))
            .calendar(toCalendar(toTimestamp("2015-01-02 03:45:06.000")))
            .sqlDate(toSqlDate(toTimestamp("2015-01-02 03:45:06.000")))
            .sqlTime(toSqlTime(toTimestamp("1899-12-31 03:45:06.000")))
            .timestamp(toTimestamp("2015-01-02 03:45:06.000"))
            .comment("日時"));

        // アノテーションありのデータ型の作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));

        outSheet.add(new FormattedRecord()
                .utilDate(toUtilDate(toTimestamp("2015-01-02 03:45:06.000")))
                .calendar(toCalendar(toTimestamp("2015-01-02 03:45:06.000")))
                .sqlDate(toSqlDate(toTimestamp("2015-01-02 03:45:06.000")))
                .sqlTime(toSqlTime(toTimestamp("1899-12-31 03:45:06.000")))
                .timestamp(toTimestamp("2015-01-02 03:45:06.000"))
                .comment("日時"));

        // 数式データの作成
        outSheet.add(new FormulaRecord().comment("空文字"));
        outSheet.add(new FormulaRecord().start(toTimestamp("2012-08-01 10:32:00.000")).end(toTimestamp("2016-08-28 09:36:00.000")).comment("日時の差"));

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, "convert_datetime.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<DateTimeSheet> errors = mapper.loadDetail(in, DateTimeSheet.class);
            DateTimeSheet sheet = errors.getTarget();

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
        assertThat(inRecord.utilDate, is(outRecord.utilDate));
        assertThat(inRecord.calendar, is(outRecord.calendar));
        assertThat(inRecord.sqlDate, is(outRecord.sqlDate));
        assertThat(inRecord.sqlTime, is(outRecord.sqlTime));
        assertThat(inRecord.timestamp, is(outRecord.timestamp));
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
            assertThat(inRecord.utilDate, is(toUtilDate(toTimestamp("2000-12-31 03:41:12.000"))));
            assertThat(inRecord.calendar, is(toCalendar(toTimestamp("2000-12-31 03:41:12.000"))));
            assertThat(inRecord.sqlDate, is(toSqlDate(toTimestamp("2000-12-31 00:00:00.000"))));
            assertThat(inRecord.sqlTime, is(toSqlTime(toTimestamp("1899-12-31 00:00:00.000"))));
            assertThat(inRecord.timestamp, is(toTimestamp("1999-12-31 10:12:00.000")));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.utilDate, is(outRecord.utilDate));
            assertThat(inRecord.calendar, is(outRecord.calendar));
            assertThat(inRecord.sqlDate, is(outRecord.sqlDate));
            assertThat(inRecord.sqlTime, is(outRecord.sqlTime));
            assertThat(inRecord.timestamp, is(outRecord.timestamp));
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
            assertThat(inRecord.utilDate, is(nullValue()));
            assertThat(inRecord.calendar, is(nullValue()));
            assertThat(inRecord.sqlDate, is(nullValue()));
            assertThat(inRecord.sqlTime, is(nullValue()));
            assertThat(inRecord.timestamp, is(nullValue()));

            assertThat(inRecord.start, is(outRecord.start));
            assertThat(inRecord.end, is(outRecord.end));
            assertThat(inRecord.comment, is(outRecord.comment));

        } else if(inRecord.no == 2) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.utilDate, is(toUtilDate(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(inRecord.calendar, is(toCalendar(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(inRecord.sqlDate, is(toSqlDate(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(inRecord.sqlTime, is(toSqlTime(toTimestamp("1904-01-26 23:04:00.000"))));
            assertThat(inRecord.timestamp, is(toTimestamp("1904-01-26 23:04:00.000")));

            assertThat(inRecord.start, is(outRecord.start));
            assertThat(inRecord.end, is(outRecord.end));
            assertThat(inRecord.comment, is(outRecord.comment));
        }

    }

    @XlsSheet(name="日時型")
    private static class DateTimeSheet {

        @XlsOrder(1)
        @XlsHorizontalRecords(tableLabel="日時型（アノテーションなし）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<SimpleRecord> simpleRecords;

        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="日付型（初期値、書式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormattedRecord> formattedRecords;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="日時型（数式）", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert)
        private List<FormulaRecord> formulaRecords;

        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public DateTimeSheet add(SimpleRecord record) {
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
        public DateTimeSheet add(FormattedRecord record) {
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
        public DateTimeSheet add(FormulaRecord record) {
            if(formulaRecords == null) {
                this.formulaRecords = new ArrayList<>();
            }
            this.formulaRecords.add(record);
            record.no(formulaRecords.size());
            return this;
        }
    }

    /**
     * 日時型 - Converterのない単純な場合
     *
     */
    private static class SimpleRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="Dateクラス(util)")
        private Date utilDate;

        @XlsColumn(columnName="Calendarクラス")
        private Calendar calendar;

        @XlsColumn(columnName="Dateクラス(sql)")
        private java.sql.Date sqlDate;

        @XlsColumn(columnName="Timeクラス(sql)")
        private Time sqlTime;

        @XlsColumn(columnName="Timesamp(sql)")
        private Timestamp timestamp;

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

        public SimpleRecord utilDate(Date utilDate) {
            this.utilDate = utilDate;
            return this;
        }

        public SimpleRecord calendar(Calendar calendar) {
            this.calendar = calendar;
            return this;
        }

        public SimpleRecord sqlDate(java.sql.Date sqlDate) {
            this.sqlDate = sqlDate;
            return this;
        }

        public SimpleRecord sqlTime(Time sqlTime) {
            this.sqlTime = sqlTime;
            return this;
        }

        public SimpleRecord timestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SimpleRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }

    /**
     * 日時型 - 初期値、書式
     *
     */
    private static class FormattedRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        /** 初期値 */
        @XlsDefaultValue("2000-12-31 03:41:12")
        @XlsColumn(columnName="Dateクラス(util)")
        private Date utilDate;

        @XlsDefaultValue("2000-12-31 03:41:12")
        @XlsColumn(columnName="Calendarクラス")
        private Calendar calendar;

        /** トリム */
        @XlsTrim
        @XlsDefaultValue(" 2000-12-31 ")
        @XlsColumn(columnName="Dateクラス(sql)")
        private java.sql.Date sqlDate;

        /** 書式付き（時間指定） */
        @XlsDefaultValue("0時0分")
        @XlsDateTimeConverter(javaPattern="H時m分", excelPattern="h\"時\"mm\"分\"")
        @XlsColumn(columnName="Timeクラス(sql)")
        private Time sqlTime;

        /** 書式付き(lenient=true) */
        @XlsDefaultValue("1999/12/31 10:12")
        @XlsDateTimeConverter(javaPattern="yyyy/M/d H:mm", lenient=true, locale="ja_JP", excelPattern="yyyy/m/d h:mm")
        @XlsColumn(columnName="Timesamp(sql)")
        private Timestamp timestamp;

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

        public FormattedRecord utilDate(Date utilDate) {
            this.utilDate = utilDate;
            return this;
        }

        public FormattedRecord calendar(Calendar calendar) {
            this.calendar = calendar;
            return this;
        }

        public FormattedRecord sqlDate(java.sql.Date sqlDate) {
            this.sqlDate = sqlDate;
            return this;
        }

        public FormattedRecord sqlTime(Time sqlTime) {
            this.sqlTime = sqlTime;
            return this;
        }

        public FormattedRecord timestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public FormattedRecord comment(String comment) {
            this.comment = comment;
            return this;
        }

    }

    /**
     * 日時型 - 数式
     *
     */
    private static class FormulaRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsColumn(columnName="No.")
        private int no;

        @XlsColumn(columnName="Dateクラス(util)")
        @XlsFormula(methodName="getFormula")
        private Date utilDate;

        @XlsColumn(columnName="Calendarクラス")
        @XlsFormula(methodName="getFormula")
        private Calendar calendar;

        @XlsColumn(columnName="Dateクラス(sql)")
        @XlsFormula(methodName="getFormula")
        private java.sql.Date sqlDate;

        @XlsColumn(columnName="Timeクラス(sql)")
        @XlsFormula(methodName="getFormula")
        private Time sqlTime;

        @XlsColumn(columnName="Timesamp(sql)")
        @XlsFormula(methodName="getFormula")
        private Timestamp timestamp;

        @XlsColumn(columnName="開始日時")
        private Timestamp start;

        @XlsColumn(columnName="終了日時")
        private Timestamp end;

        @XlsColumn(columnName="備考")
        private String comment;

        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }

        public String getFormula(final Point point) {
            if(start == null || end == null) {
                return null;
            }

            final int rowNumber = point.y + 1;

            return String.format("$H%d-$G%d", rowNumber, rowNumber);
        }

        public FormulaRecord no(int no) {
            this.no = no;
            return this;
        }

        public FormulaRecord utilDate(Date utilDate) {
            this.utilDate = utilDate;
            return this;
        }

        public FormulaRecord calendar(Calendar calendar) {
            this.calendar = calendar;
            return this;
        }

        public FormulaRecord sqlDate(java.sql.Date sqlDate) {
            this.sqlDate = sqlDate;
            return this;
        }

        public FormulaRecord sqlTime(Time sqlTime) {
            this.sqlTime = sqlTime;
            return this;
        }

        public FormulaRecord timestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public FormulaRecord start(Timestamp start) {
            this.start = start;
            return this;
        }

        public FormulaRecord end(Timestamp end) {
            this.end = end;
            return this;
        }

        public FormulaRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }
}
