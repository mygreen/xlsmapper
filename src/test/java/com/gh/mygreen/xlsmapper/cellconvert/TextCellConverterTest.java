package com.gh.mygreen.xlsmapper.cellconvert;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
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
     * ・変換用アノテーションなし。
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
    
    @XlsSheet(name="文字列型")
    private static class TextSheet {
        
        @XlsHorizontalRecords(tableLabel="文字列型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHorizontalRecords(tableLabel="文字列型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
        
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
        
    }
}
