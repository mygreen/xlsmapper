package com.gh.mygreen.xlsmapper.cellconverter;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.OverRecordOperation;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsIgnored;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * JSR-310('Date and Time API')のテスト
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TemporalCellConverterTest {
    
    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }
    
    @Test
    public void test_load_date_time() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(TemporalSheet.class);
            
            TemporalSheet sheet = mapper.load(in, TemporalSheet.class, errors);
            
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
    
    private void assertRecord(final SimpleRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.localDateTime).isNull();;
            assertThat(record.localDate).isNull();;
            assertThat(record.localTime).isNull();
            
        } else if(record.no == 2) {
            // Excelの日時型
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2015, 01, 02, 03, 45, 06));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2015, 01, 02));
            assertThat(record.localTime).isEqualTo(LocalTime.of(03, 45, 06));
            
        } else if(record.no == 3) {
            // 文字列型の場合
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2015, 01, 02, 03, 45, 06));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2015, 01, 02));
            assertThat(record.localTime).isEqualTo(LocalTime.of(03, 45, 06));
            
        } else if(record.no == 4) {
            // 文字列型の場合（値が不正）
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localDateTime"))).isTypeBindFailure()).isTrue();
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localDate"))).isTypeBindFailure()).isTrue();
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localTime"))).isTypeBindFailure()).isTrue();
            
        } else if(record.no == 5) {
            // Excelの日時型（日本語）
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2015, 01, 02, 03, 45, 06));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2015, 01, 02));
            assertThat(record.localTime).isEqualTo(LocalTime.of(03, 45, 06));
            
        } else if(record.no == 6) {
            // Excelの数値型
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2015, 01, 02, 03, 45, 06));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2015, 01, 02));
            assertThat(record.localTime).isEqualTo(LocalTime.of(03, 45, 06));
            
        } else if(record.no == 7) {
            // Excelの関数型
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2015, 01, 02, 03, 45, 06));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2015, 01, 02));
            assertThat(record.localTime).isEqualTo(LocalTime.of(03, 45, 06));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2000, 12, 31, 03, 41, 12));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2000, 12, 31));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localTime"))).isTypeBindFailure()).isTrue();
            
        } else if(record.no == 2) {
            // 文字列型の場合（正常）
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(2015, 01, 02, 03, 45, 06));
            assertThat(record.localDate).isEqualTo(LocalDate.of(2015, 12, 31));
            assertThat(record.localTime).isEqualTo(LocalTime.of(12, 03));
            
        } else if(record.no == 3) {
            // 文字列型の場合（存在しない日付）
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localDateTime"))).isTypeBindFailure()).isTrue();
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localDate"))).isTypeBindFailure()).isTrue();
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("localTime"))).isTypeBindFailure()).isTrue();
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    private void assertRecord(final FormulaRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.localDateTime).isNull();;
            assertThat(record.localDate).isNull();;
            assertThat(record.localTime).isNull();
            
        } else if(record.no == 2) {
            // 文字列型の場合（正常）
            assertThat(record.localDateTime).isEqualTo(LocalDateTime.of(1904, 01, 26, 23, 04, 00));
            assertThat(record.localDate).isEqualTo(LocalDate.of(1904, 01, 26));
            assertThat(record.localTime).isEqualTo(LocalTime.of(23, 04, 00));
            
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
        final TemporalSheet outSheet = new TemporalSheet();
        
        // アノテーションなしのデータ型の作成
        outSheet.add(new SimpleRecord()
                .comment("空文字"));
        
        outSheet.add(new SimpleRecord()
            .localDateTime(LocalDateTime.of(2015, 01, 02, 03, 45, 06))
            .localDate(LocalDate.of(2015, 01, 02))
            .localTime(LocalTime.of(03, 45, 06))
            .comment("日時"));
        
        // アノテーションありのデータ型の作成
        outSheet.add(new FormattedRecord()
                .comment("空文字"));
        
        outSheet.add(new FormattedRecord()
                .localDateTime(LocalDateTime.of(2015, 01, 02, 03, 45, 06))
                .localDate(LocalDate.of(2015, 01, 02))
                .localTime(LocalTime.of(03, 45, 06))
                .comment("日時"));
        
        // 数式データの作成
        outSheet.add(new FormulaRecord().comment("空文字"));
        outSheet.add(new FormulaRecord()
                .start(LocalDateTime.of(2012, 8, 01, 10, 32))
                .end(LocalDateTime.of(2016, 8, 28, 9, 36))
                .comment("日時の差"));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "convert_jsr310.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(TemporalSheet.class);
            
            TemporalSheet sheet = mapper.load(in, TemporalSheet.class, errors);
            
            if(sheet.simpleRecords != null) {
                assertThat(sheet.simpleRecords).hasSize(outSheet.simpleRecords.size());
                
                for(int i=0; i < sheet.simpleRecords.size(); i++) {
                    assertRecord(sheet.simpleRecords.get(i), outSheet.simpleRecords.get(i), errors);
                }
            }
            
            if(sheet.formattedRecords != null) {
                assertThat(sheet.formattedRecords).hasSize(outSheet.formattedRecords.size());
                
                for(int i=0; i < sheet.formattedRecords.size(); i++) {
                    assertRecord(sheet.formattedRecords.get(i), outSheet.formattedRecords.get(i), errors);
                }
            }
            
            if(sheet.formulaRecords != null) {
                assertThat(sheet.formulaRecords).hasSize(outSheet.formulaRecords.size());
                
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
    private void assertRecord(final SimpleRecord inRecord, final SimpleRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);
        
        assertThat(inRecord.no).isEqualTo(outRecord.no);
        assertThat(inRecord.localDateTime).isEqualTo(outRecord.localDateTime);
        assertThat(inRecord.localDate).isEqualTo(outRecord.localDate);
        assertThat(inRecord.localTime).isEqualTo(outRecord.localTime);
        assertThat(inRecord.comment).isEqualTo(outRecord.comment);
        
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
            assertThat(inRecord.no).isEqualTo(outRecord.no);
            assertThat(inRecord.localDateTime).isEqualTo(LocalDateTime.of(2000, 12, 31, 3, 41, 12));
            assertThat(inRecord.localDate).isEqualTo(LocalDate.of(2000, 12, 31));
            assertThat(cellFieldError(errors, cellAddress(inRecord.positions.get("localTime"))).isTypeBindFailure()).isTrue();
//            assertThat(inRecord.localTime).isEqualTo(LocalTime.of(3, 41, 12));
            assertThat(inRecord.comment).isEqualTo(outRecord.comment);
            
        } else {
            assertThat(inRecord.no).isEqualTo(outRecord.no);
            assertThat(inRecord.localDateTime).isEqualTo(outRecord.localDateTime);
            assertThat(inRecord.localDate).isEqualTo(outRecord.localDate);
            assertThat(inRecord.localTime).isEqualTo(outRecord.localTime);
            assertThat(inRecord.comment).isEqualTo(outRecord.comment);
        }
        
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final FormulaRecord inRecord, final FormulaRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d, comment=%s\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no, inRecord.comment);
        
        if(inRecord.no == 1) {
            assertThat(inRecord.no).isEqualTo(outRecord.no);
            assertThat(inRecord.localDateTime).isNull();
            assertThat(inRecord.localDate).isNull();
            assertThat(inRecord.localTime).isNull();
            
            assertThat(inRecord.start).isEqualTo(outRecord.start);
            assertThat(inRecord.end).isEqualTo(outRecord.end);
            assertThat(inRecord.comment).isEqualTo(outRecord.comment);
            
        } else if(inRecord.no == 2) {
            assertThat(inRecord.no).isEqualTo(outRecord.no);
            assertThat(inRecord.localDateTime).isEqualTo(LocalDateTime.of(1904, 1, 26, 23, 4, 0));
            assertThat(inRecord.localDate).isEqualTo(LocalDate.of(1904, 1, 26));
            assertThat(inRecord.localTime).isEqualTo(LocalTime.of(23, 4, 0));
            
            assertThat(inRecord.start).isEqualTo(outRecord.start);
            assertThat(inRecord.end).isEqualTo(outRecord.end);
            assertThat(inRecord.comment).isEqualTo(outRecord.comment);
            
        }
        
    }
    
    @XlsSheet(name="JSR310")
    private static class TemporalSheet {
        
        @XlsOrder(value=1)
        @XlsHorizontalRecords(tableLabel="日時型（アノテーションなし）", terminal=RecordTerminal.Border, ignoreEmptyRecord=true,
                overRecord=OverRecordOperation.Insert)
        private List<SimpleRecord> simpleRecords;
        
        @XlsOrder(value=2)
        @XlsHorizontalRecords(tableLabel="日付型（初期値、書式）", terminal=RecordTerminal.Border, ignoreEmptyRecord=true,
                overRecord=OverRecordOperation.Insert)
        private List<FormattedRecord> formattedRecords;
        
        @XlsOrder(value=3)
        @XlsHorizontalRecords(tableLabel="日時型（数式）", terminal=RecordTerminal.Border, ignoreEmptyRecord=true,
                overRecord=OverRecordOperation.Insert)
        private List<FormulaRecord> formulaRecords;
        
        /**
         * レコードを追加する。noを自動的に付与する。
         * @param record
         * @return
         */
        public TemporalSheet add(SimpleRecord record) {
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
        public TemporalSheet add(FormattedRecord record) {
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
        public TemporalSheet add(FormulaRecord record) {
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
        
        @XlsColumn(columnName="LocalDateTimeクラス")
        private LocalDateTime localDateTime;
        
        @XlsColumn(columnName="LocalDateクラス")
        private LocalDate localDate;
        
        @XlsColumn(columnName="LocalTimeクラス")
        private LocalTime localTime;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsIgnored
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public SimpleRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public SimpleRecord localDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }
        
        public SimpleRecord localDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }
        
        public SimpleRecord localTime(LocalTime localTime) {
            this.localTime = localTime;
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
        @XlsColumn(columnName="LocalDateTimeクラス")
        private LocalDateTime localDateTime;
        
        @XlsTrim
        @XlsDefaultValue(" 2000-12-31 ")
        @XlsColumn(columnName="LocalDateクラス")
        private LocalDate localDate;
        
        /** 書式付き（初期値のフォーマットが不正） */
        @XlsDefaultValue("abc")
        @XlsDateConverter(javaPattern="H時m分")
        @XlsColumn(columnName="LocalTimeクラス")
        private LocalTime localTime;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsIgnored
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public FormattedRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public FormattedRecord localDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }
        
        public FormattedRecord localDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }
        
        public FormattedRecord localTime(LocalTime localTime) {
            this.localTime = localTime;
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
        
        @XlsFormula(methodName="getFormula")
        @XlsColumn(columnName="LocalDateTimeクラス")
        private LocalDateTime localDateTime;
        
        @XlsFormula(methodName="getFormula")
        @XlsColumn(columnName="LocalDateクラス")
        private LocalDate localDate;
        
        @XlsFormula(methodName="getFormula")
        @XlsColumn(columnName="LocalTimeクラス")
        private LocalTime localTime;
        
        @XlsColumn(columnName="開始日時")
        private LocalDateTime start;
        
        @XlsColumn(columnName="終了日時")
        private LocalDateTime end;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsIgnored
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public String getFormula(final Point point) {
            if(start == null || end == null) {
                return null;
            }
            
            final int rowNumber = point.y + 1;
            
            return String.format("$F%d-$E%d", rowNumber, rowNumber);
        }
        
        public FormulaRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public FormulaRecord localDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }
        
        public FormulaRecord localDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }
        
        public FormulaRecord localTime(LocalTime localTime) {
            this.localTime = localTime;
            return this;
        }
        
        public FormulaRecord start(LocalDateTime start) {
            this.start = start;
            return this;
        }
        
        public FormulaRecord end(LocalDateTime end) {
            this.end = end;
            return this;
        }
        
        public FormulaRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }
}
