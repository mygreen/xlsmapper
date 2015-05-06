package com.gh.mygreen.xlsmapper.cellconvert;

import static org.junit.Assert.*;
//import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.gh.mygreen.xlsmapper.annotation.converter.XlsArrayConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * リスト/集合/配列型の
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class CollectionCellConveterTest {
    
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
     * リスト、集合、配列型の読み込みテスト
     */
    @Test
    public void test_load_collection() {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(CollectionSheet.class);
            
            CollectionSheet sheet = mapper.load(in, CollectionSheet.class, errors);
            
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
            assertThat(record.listText, empty());
            assertThat(record.listInteger, empty());
            
            assertThat(record.arrayText, emptyArray());
            assertThat(record.arrayInteger, emptyArray());
            
            assertThat(record.setText, empty());
            assertThat(record.setInteger, empty());
            
        } else if(record.no == 2) {
            // 項目が１つ
            assertThat(record.listText, contains("abc"));
            assertThat(record.listInteger, contains(123));
            
            assertThat(record.arrayText, arrayContaining("abc"));
            assertThat(record.arrayInteger, arrayContaining(123));
            
            assertThat(record.setText, contains("abc"));
            assertThat(record.setInteger, contains(123));
            
        } else if(record.no == 3) {
            // 項目が2つ
            assertThat(record.listText, contains("abc", "def"));
            assertThat(record.listInteger, contains(123, 456));
            
            assertThat(record.arrayText, arrayContaining("abc", "def"));
            assertThat(record.arrayInteger, arrayContaining(123, 456));
            
            assertThat(record.setText, contains("abc", "def"));
            assertThat(record.setInteger, contains(123, 456));
            
        } else if(record.no == 4) {
            // 区切り文字のみ
            assertThat(record.listText, empty());
            assertThat(record.listInteger, empty());
            
            assertThat(record.arrayText, emptyArray());
            assertThat(record.arrayInteger, emptyArray());
            
            assertThat(record.setText, empty());
            assertThat(record.setInteger, empty());
            
        } else if(record.no == 5) {
            // 空の項目がある
            assertThat(record.listText, contains("abc", null, "def"));
            assertThat(record.listInteger, contains(123, null, 456));
            
            assertThat(record.arrayText, arrayContaining("abc", null, "def"));
            assertThat(record.arrayInteger, arrayContaining(123,null,  456));
            
            assertThat(record.setText, contains("abc", null, "def"));
            assertThat(record.setInteger, contains(123, null, 456));
            
        } else if(record.no == 6) {
            // 空の項目がある
            assertThat(record.listText, contains("  abc", " def "));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("listInteger"))).isTypeBindFailure(), is(true));
            
            assertThat(record.arrayText, arrayContaining("  abc", " def "));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("arrayInteger"))).isTypeBindFailure(), is(true));
            
            assertThat(record.setText, contains("  abc", " def "));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("setInteger"))).isTypeBindFailure(), is(true));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            // 空文字
            assertThat(record.listText, empty());
            assertThat(record.listInteger, contains(0));
            
            assertThat(record.arrayText, emptyArray());
            assertThat(record.arrayInteger, arrayContaining(0));
            
            assertThat(record.setText, empty());
            assertThat(record.setInteger, contains(0));
            
        } else if(record.no == 2) {
            // 項目が１つ
            assertThat(record.listText, contains("abc"));
            assertThat(record.listInteger, contains(123));
            
            assertThat(record.arrayText, arrayContaining("abc"));
            assertThat(record.arrayInteger, arrayContaining(123));
            
            assertThat(record.setText, contains("abc"));
            assertThat(record.setInteger, contains(123));
            
        } else if(record.no == 3) {
            // 項目が2つ
            assertThat(record.listText, contains("abc", "def"));
            assertThat(record.listInteger, contains(123, 456));
            
            assertThat(record.arrayText, arrayContaining("abc", "def"));
            assertThat(record.arrayInteger, arrayContaining(123, 456));
            
            assertThat(record.setText, contains("abc", "def"));
            assertThat(record.setInteger, contains(123, 456));
            
        } else if(record.no == 4) {
            // 区切り文字のみ
            assertThat(record.listText, empty());
            assertThat(record.listInteger, empty());
            
            assertThat(record.arrayText, emptyArray());
            assertThat(record.arrayInteger, emptyArray());
            
            assertThat(record.setText, empty());
            assertThat(record.setInteger, empty());
            
        } else if(record.no == 5) {
            // 区切り文字、空白
            assertThat(record.listText, contains("abc", "def"));
            assertThat(record.listInteger, contains(123, 456));
            
            assertThat(record.arrayText, arrayContaining("abc", "def"));
            assertThat(record.arrayInteger, arrayContaining(123, 456));
            
            assertThat(record.setText, contains("abc", "def"));
            assertThat(record.setInteger, contains(123, 456));
            
        } else if(record.no == 6) {
            // 空白がある
            assertThat(record.listText, contains("abc", "def"));
            assertThat(record.listInteger, contains(123, 456));
            
            assertThat(record.arrayText, arrayContaining("abc", "def"));
            assertThat(record.arrayInteger, arrayContaining(123, 456));
            
            assertThat(record.setText, contains("abc", "def"));
            assertThat(record.setInteger, contains(123, 456));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    @XlsSheet(name="リスト型")
    private static class CollectionSheet {
        
        @XlsHorizontalRecords(tableLabel="リスト型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHorizontalRecords(tableLabel="リスト型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
        
    }
    
    /**
     * リスト型 - アノテーションなし。
     *
     */
    private static class SimpleRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="List（文字列）")
        private List<String> listText;
        
        @XlsColumn(columnName="List（数値）")
        private List<Integer> listInteger;
        
        @XlsColumn(columnName="Array（文字列）")
        private String[] arrayText;
        
        @XlsColumn(columnName="Array（数値）")
        private Integer[] arrayInteger;
        
        @XlsColumn(columnName="Set（文字列）")
        private Set<String> setText;
        
        @XlsColumn(columnName="Set（数値）")
        private Set<Integer> setInteger;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
    }
    
    private static class FormattedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsConverter(trim=true)
        @XlsArrayConverter(separator="\n", ignoreEmptyItem=true)
        @XlsColumn(columnName="List（文字列）")
        private List<String> listText;
        
        @XlsConverter(trim=true, defaultValue="0")
        @XlsArrayConverter(separator=";", ignoreEmptyItem=true)
        @XlsColumn(columnName="List（数値）")
        private List<Integer> listInteger;
        
        @XlsConverter(trim=true)
        @XlsArrayConverter(separator="\n", ignoreEmptyItem=true)
        @XlsColumn(columnName="Array（文字列）")
        private String[] arrayText;
        
        @XlsConverter(trim=true, defaultValue="0")
        @XlsArrayConverter(separator=";", ignoreEmptyItem=true)
        @XlsColumn(columnName="Array（数値）")
        private Integer[] arrayInteger;
        
        @XlsConverter(trim=true)
        @XlsArrayConverter(separator="\n", ignoreEmptyItem=true)
        @XlsColumn(columnName="Set（文字列）")
        private Set<String> setText;
        
        @XlsConverter(trim=true, defaultValue="0")
        @XlsArrayConverter(separator=";", ignoreEmptyItem=true)
        @XlsColumn(columnName="Set（数値）")
        private Set<Integer> setInteger;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
}
