package com.gh.mygreen.xlsmapper.cellconvert;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * 日付、時刻型のタイプのチェック
 * 
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
    public void test_load_date_time() {
        
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
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    /**
     * セルのアドレスを指定してエラーを取得する。
     * @param errors
     * @param address
     * @return 見つからない場合はnullを返す。
     */
    private CellFieldError getCellFieldError(final SheetBindingErrors errors, final String address) {
        for(CellFieldError error : errors.getCellFieldErrors()) {
            if(error.getFormattedCellAddress().equalsIgnoreCase(address)) {
                return error;
            }
        }
        
        return null;
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
            assertThat(record.utilDate, is(utilDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(sqlTime(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(timestamp("2015-01-02 03:45:06.000")));
            
        } else if(record.no == 3) {
            // 文字列型の場合
            assertThat(record.utilDate, is(utilDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2015-01-02 00:00:00.000"))));
            assertThat(record.sqlTime, is(sqlTime(timestamp("1970-01-01 03:45:06.000"))));
            assertThat(record.timestamp, is(timestamp("2015-01-02 03:45:06.000")));
            
        } else if(record.no == 4) {
            // 文字列型の場合
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("utilDate"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("sqlDate"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("sqlTime"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("timestamp"))).isTypeBindFailure(), is(true));
            
        } else if(record.no == 5) {
            // Excelの日時型（日本語）
            assertThat(record.utilDate, is(utilDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(sqlTime(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(timestamp("2015-01-02 03:45:06.000")));
            
        } else if(record.no == 6) {
            // Excelの数値型
            assertThat(record.utilDate, is(utilDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(sqlTime(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(timestamp("2015-01-02 03:45:06.000")));
            
        } else if(record.no == 7) {
            // Excelの関数型
            assertThat(record.utilDate, is(utilDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlTime, is(sqlTime(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.timestamp, is(timestamp("2015-01-02 03:45:06.000")));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.utilDate, is(utilDate(timestamp("2000-12-31 03:41:12"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2000-12-31 00:00:00.000"))));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("sqlTime"))).isTypeBindFailure(), is(true));
            assertThat(record.timestamp, is(timestamp("1999-12-31 10:12:00.000")));
            
        } else if(record.no == 2) {
            // 文字列型の場合（正常）
            assertThat(record.utilDate, is(utilDate(timestamp("2015-01-02 03:45:06.000"))));
            assertThat(record.sqlDate, is(sqlDate(timestamp("2015-12-31 00:00:00.000"))));
            assertThat(record.sqlTime, is(sqlTime(timestamp("1970-01-01 12:03:00.000"))));
            assertThat(record.timestamp, is(timestamp("2015-01-02 03:45:00.000")));
            
        } else if(record.no == 3) {
            // 文字列型の場合（存在しない日付）
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("utilDate"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("sqlDate"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("sqlTime"))).isTypeBindFailure(), is(true));
            assertThat(record.timestamp, is(timestamp("2016-01-02 03:45:00.000")));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    /**
     * 任意の時間でフォーマットする。
     * @param pattern
     * @param date
     * @return
     */
    private String format(final String pattern, final Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }
    
    /**
     * 文字列をタイムスタンプに変換する。(yyyy-MM-dd HH:mm:ss.SSS)の形式
     * @param value
     * @return
     */
    private Timestamp timestamp(String value) {
        return Timestamp.valueOf(value);
    }
    
    /**
     * Timestampを{@link java.util.Date}に変換する。
     * @param timestamp
     * @return
     */
    private Date utilDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
    
    /**
     * Timestampを{@link java.sql.Date}に変換する。
     * @param timestamp
     * @return
     */
    private java.sql.Date sqlDate(Timestamp timestamp) {
        return new java.sql.Date(timestamp.getTime());
    }
    
    /**
     * Timestampを{@link java.sql.Time}に変換する。
     * @param timestamp
     * @return
     */
    private java.sql.Time sqlTime(Timestamp timestamp) {
        return new java.sql.Time(timestamp.getTime());
    }
    
    @XlsSheet(name="日時型")
    private static class DateTimeSheet {
        
        @XlsHorizontalRecords(tableLabel="日時型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHorizontalRecords(tableLabel="日付型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
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
        
        /** トリム */
        @XlsConverter(defaultValue=" 2000-12-31 ", trim=true)
        @XlsColumn(columnName="Dateクラス(sql)")
        private java.sql.Date sqlDate;
        
        /** 書式付き（初期値のフォーマットが不正） */
        @XlsConverter(defaultValue="abc")
        @XlsDateConverter(pattern="H時m分")
        @XlsColumn(columnName="Timeクラス(sql)")
        private Time sqlTime;
        
        /** 書式付き(lenient=true) */
        @XlsConverter(defaultValue="1999/12/31 10:12")
        @XlsDateConverter(pattern="yyyy/M/d H:mm", lenient=true, locale="ja_JP")
        @XlsColumn(columnName="Timesamp(sql)")
        private Timestamp timestamp;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
}
