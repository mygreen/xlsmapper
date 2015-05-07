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
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsEnumConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.EnumCellConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * {@link EnumCellConverter}のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class EnumCellConverterTest {
    
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
     * 列挙型の読み込みテスト
     */
    @Test
    public void test_load_enum() {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(EnumSheet.class);
            
            EnumSheet sheet = mapper.load(in, EnumSheet.class, errors);
            
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
            assertThat(record.color, is(nullValue()));
            assertThat(record.operate, is(nullValue()));
            
        } else if(record.no == 2) {
            // 正しい値
            assertThat(record.color, is(Color.Red));
            assertThat(record.operate, is(Operate.Refer));
            
        } else if(record.no == 3) {
            // 不正な値
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("color"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("operate"))).isTypeBindFailure(), is(true));
            
        } else if(record.no == 4) {
            // 小文字
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("color"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("operate"))).isTypeBindFailure(), is(true));
            
        } else if(record.no == 5) {
            // 空白
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("color"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("operate"))).isTypeBindFailure(), is(true));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) {
        
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
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("color"))).isTypeBindFailure(), is(true));
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("operate"))).isTypeBindFailure(), is(true));
            
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
        
        @XlsHorizontalRecords(tableLabel="列挙型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHorizontalRecords(tableLabel="列挙型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
        
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
        
        @XlsColumn(columnName="Enum型（英字）")
        private Color color;
        
        @XlsColumn(columnName="Enum型（日本語）")
        private Operate operate;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
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
        
        @XlsConverter(defaultValue="Red", trim=true)
        @XlsEnumConverter(ignoreCase=true)
        @XlsColumn(columnName="Enum型（英字）")
        private Color color;
        
        @XlsConverter(defaultValue="参照", trim=true)
        @XlsEnumConverter(ignoreCase=true, valueMethodName="localeName")
        @XlsColumn(columnName="Enum型（日本語）")
        private Operate operate;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
}
