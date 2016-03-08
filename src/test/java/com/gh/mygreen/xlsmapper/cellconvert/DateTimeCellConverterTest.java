package com.gh.mygreen.xlsmapper.cellconvert;

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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.OverRecordOperate;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHint;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * 日付、時刻型のタイプのチェック
 * 
 * @version 1.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class DateTimeCellConverterTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_load_date_time() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(DateTimeSheet.class);
            
            DateTimeSheet sheet = mapper.load(in, DateTimeSheet.class, errors);
            
            for(SimpleRecord record : sheet.simpleRecords) {
                assertRecord(record, errors);
            }
            
            for(FormattedRecord record : sheet.formattedRecords) {
                assertRecord(record, errors);
            }
            
        }
        
    }
    
    private void assertRecord(final SimpleRecord record, final SheetBindingErrors errors) {
        
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
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));
            
        } else if(record.no == 3) {
            // 文字列型の場合
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-01-02 00:00:00.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("1970-01-01 03:45:06.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:06.000")));
            
        } else if(record.no == 4) {
            // 文字列型の場合
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("utilDate"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("calendar"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("sqlDate"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("sqlTime"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("timestamp"))).isTypeBindFailure(), is(true));
            
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
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2000-12-31 03:41:12"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2000-12-31 03:41:12"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2000-12-31 00:00:00.000"))));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("sqlTime"))).isTypeBindFailure(), is(true));
            assertThat(record.timestamp, is(toTimestamp("1999-12-31 10:12:00.000")));
            
        } else if(record.no == 2) {
            // 文字列型の場合（正常）
            assertThat(record.utilDate, is(toUtilDate(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.calendar, is(toCalendar(toTimestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(toSqlDate(toTimestamp("2015-12-31 00:00:00.000"))));
            assertThat(record.sqlTime, is(toSqlTime(toTimestamp("1970-01-01 12:03:00.000"))));
            assertThat(record.timestamp, is(toTimestamp("2015-01-02 03:45:00.000")));
            
        } else if(record.no == 3) {
            // 文字列型の場合（存在しない日付）
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("utilDate"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("calendar"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("sqlDate"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("sqlTime"))).isTypeBindFailure(), is(true));
            assertThat(record.timestamp, is(toTimestamp("2016-01-02 03:45:00.000")));
            
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
            .sqlTime(toSqlTime(toTimestamp("2015-01-02 03:45:06.000")))
            .timestamp(toTimestamp("2015-01-02 03:45:06.000"))
            .comment("日時"));
        
        // アノテーションありのデータ型の作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));
        
        outSheet.add(new FormattedRecord()
                .utilDate(toUtilDate(toTimestamp("2015-01-02 03:45:06.000")))
                .calendar(toCalendar(toTimestamp("2015-01-02 03:45:06.000")))
                .sqlDate(toSqlDate(toTimestamp("2015-01-02 03:45:06.000")))
                .sqlTime(toSqlTime(toTimestamp("2015-01-02 03:45:06.000")))
                .timestamp(toTimestamp("2015-01-02 03:45:06.000"))
                .comment("日時"));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/convert_datetime.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(DateTimeSheet.class);
            
            DateTimeSheet sheet = mapper.load(in, DateTimeSheet.class, errors);
            
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
            
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final SimpleRecord inRecord, final SimpleRecord outRecord, final SheetBindingErrors errors) {
        
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
    private void assertRecord(final FormattedRecord inRecord, final FormattedRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);
        
        if(inRecord.no == 1) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.utilDate, is(toUtilDate(toTimestamp("2000-12-31 03:41:12.000"))));
            assertThat(inRecord.calendar, is(toCalendar(toTimestamp("2000-12-31 03:41:12.000"))));
            assertThat(inRecord.sqlDate, is(toSqlDate(toTimestamp("2000-12-31 00:00:00.000"))));
            assertThat(inRecord.sqlTime, is(nullValue()));
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
    
    @XlsSheet(name="日時型")
    private static class DateTimeSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="日時型（アノテーションなし）", terminal=RecordTerminal.Border, ignoreEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="日付型（初期値、書式）", terminal=RecordTerminal.Border, ignoreEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<FormattedRecord> formattedRecords;
        
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
        
        @XlsIsEmpty
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
     * @author T.TSUCHIE
     *
     */
    private static class FormattedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        /** 初期値 */
        @XlsConverter(defaultValue="2000-12-31 03:41:12")
        @XlsColumn(columnName="Dateクラス(util)")
        private Date utilDate;
        
        @XlsConverter(defaultValue="2000-12-31 03:41:12")
        @XlsColumn(columnName="Calendarクラス")
        private Calendar calendar;
        
        /** トリム */
        @XlsConverter(defaultValue=" 2000-12-31 ", trim=true)
        @XlsColumn(columnName="Dateクラス(sql)")
        private java.sql.Date sqlDate;
        
        /** 書式付き（初期値のフォーマットが不正） */
        @XlsConverter(defaultValue="abc")
        @XlsDateConverter(javaPattern="H時m分")
        @XlsColumn(columnName="Timeクラス(sql)")
        private Time sqlTime;
        
        /** 書式付き(lenient=true) */
        @XlsConverter(defaultValue="1999/12/31 10:12")
        @XlsDateConverter(javaPattern="yyyy/M/d H:mm", lenient=true, locale="ja_JP", excelPattern="yyyy/m/d h:mm")
        @XlsColumn(columnName="Timesamp(sql)")
        private Timestamp timestamp;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
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
}
