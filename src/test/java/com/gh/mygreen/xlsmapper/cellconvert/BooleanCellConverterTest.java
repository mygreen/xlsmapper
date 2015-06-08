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
import java.util.ArrayList;
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
import com.gh.mygreen.xlsmapper.annotation.XlsHint;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.BooleanCellConverter;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link BooleanCellConverter}のテスタ
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class BooleanCellConverterTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * boolean/Boolen型の読み込みテスト
     */
    @Test
    public void test_load_boolean() {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(BooleanSheet.class);
            
            BooleanSheet sheet = mapper.load(in, BooleanSheet.class, errors);
            
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
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * 入力用のシートのレコードの値を検証する。
     * @param record
     * @param errors
     */
    private void assertRecord(final SimpleRecord record, final SheetBindingErrors errors) {
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
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("b1"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("b2"))).isTypeBindFailure(), is(true));
            
        } else if(record.no == 7) {
            // 空白の文字
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("b1"))).isTypeBindFailure(), is(true));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("b2"))).isTypeBindFailure(), is(true));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }
    
    /**
     * 入力用のシートのレコードの値を検証する。
     * @param record
     * @param errors
     */
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
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
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/convert_boolean.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(BooleanSheet.class);
            
            BooleanSheet sheet = mapper.load(in, BooleanSheet.class, errors);
            
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
    private void assertRecord(final FormattedRecord inRecord, final FormattedRecord outRecord, final SheetBindingErrors errors) {
        
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
    
    @XlsSheet(name="ブール型")
    private static class BooleanSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="ブール型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="ブール型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<FormattedRecord> formattedRecords;
        
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
        
        @XlsIsEmpty
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
        
        @XlsConverter(defaultValue="true", trim=true)
        @XlsBooleanConverter(failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="boolean型")
        private boolean b1;
        
        @XlsConverter(defaultValue="true", trim=true)
        @XlsBooleanConverter(failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="Boolean型")
        private Boolean b2;
        
        @XlsConverter(defaultValue="abc", trim=true)
        @XlsBooleanConverter(loadForTrue={"○", "真"}, loadForFalse={"×", "偽", ""}, saveAsTrue="○", saveAsFalse="×",
            failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="boolean型(パターン)")
        private boolean b3;
        
        @XlsConverter(defaultValue="def", trim=true)
        @XlsBooleanConverter(loadForTrue={"OK", "RIGHT"}, loadForFalse={"NOT", "-", ""}, saveAsTrue="OK", saveAsFalse="NOT",
            failToFalse=true, ignoreCase=false)
        @XlsColumn(columnName="Boolean型(パターン)")
        private Boolean b4;
        
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

}
