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
import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * Boolen型の変換のテスト
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
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b1"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b2"))).isTypeBindFailure(), is(true));
            
        } else if(record.no == 7) {
            // 空白の文字
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b1"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("b2"))).isTypeBindFailure(), is(true));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }
    
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
    
    @XlsSheet(name="ブール型")
    private static class BooleanSheet {
        
        @XlsHorizontalRecords(tableLabel="ブール型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHorizontalRecords(tableLabel="ブール型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
        
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
        @XlsBooleanConverter(loadForTrue={"○", "真"}, loadForFalse={"×", "偽", ""}, failToFalse=true, ignoreCase=true)
        @XlsColumn(columnName="boolean型(パターン)")
        private boolean b3;
        
        @XlsConverter(defaultValue="def", trim=true)
        @XlsBooleanConverter(loadForTrue={"OK", "RIGHT"}, loadForFalse={"NOT", "-", ""}, failToFalse=true, ignoreCase=false)
        @XlsColumn(columnName="Boolean型(パターン)")
        private Boolean b4;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }

}
