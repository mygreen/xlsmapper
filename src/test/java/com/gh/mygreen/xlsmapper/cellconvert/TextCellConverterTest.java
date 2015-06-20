package com.gh.mygreen.xlsmapper.cellconvert;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

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
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * 文字列の変換処理のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class TextCellConverterTest {
    
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
     * 文字列型の読み込みテスト
     */
    @Test
    public void test_load_text() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(TextSheet.class);
            
            TextSheet sheet = mapper.load(in, TextSheet.class, errors);
            
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
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
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
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/convert_text.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(TextSheet.class);
            
            TextSheet sheet = mapper.load(in, TextSheet.class, errors);
            
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
    private void assertRecord(final FormattedRecord inRecord, final FormattedRecord outRecord, final SheetBindingErrors errors) {
        
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
    
    @XlsSheet(name="文字列型")
    private static class TextSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="文字列型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="文字列型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<FormattedRecord> formattedRecords;
        
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
        
        @XlsIsEmpty
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
        
        @XlsConverter(defaultValue="Hello")
        @XlsColumn(columnName="String型(初期値)")
        private String t1;
        
        @XlsConverter(trim=true)
        @XlsColumn(columnName="String型（トリム）")
        private String t2;
        
        @XlsConverter(defaultValue="abc", trim=true)
        @XlsColumn(columnName="char型")
        private char c1;
        
        @XlsConverter(defaultValue="def", trim=true)
        @XlsColumn(columnName="Character型")
        private Character c2;
        
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
}
