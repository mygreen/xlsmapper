package com.gh.mygreen.xlsmapper.cellconvert;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.gh.mygreen.xlsmapper.cellconvert.converter.URICellConverter;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * リンクの変換テスト。
 * <p>下記のConverterのテスタ
 * <ol>
 *   <li>{@link URICellConverter}
 *   <li>{@link CellLink}
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class LinkCellConverterTest {
    
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
     * リンクの読み込みテスト
     */
    @Test
    public void test_load_link() {
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(LinkSheet.class);
            
            LinkSheet sheet = mapper.load(in, LinkSheet.class, errors);
            
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
    
    private void assertRecord(final SimpleRecord record, final SheetBindingErrors errors) throws URISyntaxException {
        if(record.no == 1) {
            // 空文字
            assertThat(record.uri, is(nullValue()));
            assertThat(record.link, is(nullValue()));
            
        } else if(record.no == 2) {
            // URL（ラベルが同じ）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "http://www.google.co.jp/")));
            
        } else if(record.no == 3) {
            // URL（ラベルが異なる）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "Googleサイト")));
            
        } else if(record.no == 4) {
            // 文字列
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink(null, "http://www.google.co.jp/")));
            
        } else if(record.no == 5) {
            // メールアドレス
            assertThat(record.uri, is(new URI("mailto:hoge@google.com")));
            assertThat(record.link, is(new CellLink("mailto:hoge@google.com", "hoge@google.com")));
            
        } else if(record.no == 6) {
            // ファイルパス
            assertThat(record.uri, is(new URI("convert.xlsx")));
            assertThat(record.link, is(new CellLink("convert.xlsx", ".\\convert.xlsx")));
            
        } else if(record.no == 7) {
            // セルへのリンク
            assertThat(record.uri, is(new URI("リンク型!A1")));
            assertThat(record.link, is(new CellLink("リンク型!A1", "セルへのリンク")));
            
        } else if(record.no == 8) {
            // 不正なリンク＋ラベルに空白を含む
            assertThat(record.uri, is(new URI("http://invalid.uri")));
            assertThat(record.link, is(new CellLink("http://invalid.uri", "  空白を含むリンク  ")));
            
        } else if(record.no == 9) {
            // 空白の文字列
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("uri"))).isTypeBindFailure(), is(true));
            assertThat(record.link, is(new CellLink(null, "  http://www.google.co.jp/  ")));
            
        } else if(record.no == 10) {
            // URL（ラベルが空白）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "  ")));
            
        } else if(record.no == 11) {
            // 空白の文字
            assertThat(getCellFieldError(errors, POIUtils.formatCellAddress(record.positions.get("uri"))).isTypeBindFailure(), is(true));
            assertThat(record.link, is(new CellLink(null, "   ")));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
        
    }
    
    private void assertRecord(final FormattedRecord record, final SheetBindingErrors errors) throws URISyntaxException {
        if(record.no == 1) {
            // 空文字
            assertThat(record.uri, is(new URI("http://myhome.com/")));
            assertThat(record.link, is(new CellLink("http://myhome.com/", "http://myhome.com/")));
            
        } else if(record.no == 2) {
            // URL（ラベルが同じ）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "http://www.google.co.jp/")));
            
        } else if(record.no == 3) {
            // URL（ラベルが異なる）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "Googleサイト")));
            
        } else if(record.no == 4) {
            // 文字列
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink(null, "http://www.google.co.jp/")));
            
        } else if(record.no == 5) {
            // メールアドレス
            assertThat(record.uri, is(new URI("mailto:hoge@google.com")));
            assertThat(record.link, is(new CellLink("mailto:hoge@google.com", "hoge@google.com")));
            
        } else if(record.no == 6) {
            // ファイルパス
            assertThat(record.uri, is(new URI("convert.xlsx")));
            assertThat(record.link, is(new CellLink("convert.xlsx", ".\\convert.xlsx")));
            
        } else if(record.no == 7) {
            // セルへのリンク
            assertThat(record.uri, is(new URI("リンク型!A1")));
            assertThat(record.link, is(new CellLink("リンク型!A1", "セルへのリンク")));
            
        } else if(record.no == 8) {
            // 不正なリンク＋ラベルに空白を含む
            assertThat(record.uri, is(new URI("http://invalid.uri")));
            assertThat(record.link, is(new CellLink("http://invalid.uri", "空白を含むリンク")));
            
        } else if(record.no == 9) {
            // 空白の文字列
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink(null, "http://www.google.co.jp/")));
            
        } else if(record.no == 10) {
            // URL（ラベルが空白）
            assertThat(record.uri, is(new URI("http://www.google.co.jp/")));
            assertThat(record.link, is(new CellLink("http://www.google.co.jp/", "")));
            
        } else if(record.no == 11) {
            // 空白の文字
            assertThat(record.uri, is(nullValue()));
            assertThat(record.link, is(nullValue()));
            
        } else {
            fail(String.format("not support test case. No=%d.", record.no));
        }
    }
    
    @XlsSheet(name="リンク型")
    private static class LinkSheet {
        
        @XlsHorizontalRecords(tableLabel="リンク型（アノテーションなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<SimpleRecord> simpleRecords;
        
        @XlsHorizontalRecords(tableLabel="リンク型（初期値、書式）", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        private List<FormattedRecord> formattedRecords;
        
    }
    
    /**
     * リンク型 - アノテーションなし
     *
     */
    private static class SimpleRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="URI")
        private URI uri;
        
        @XlsColumn(columnName="CellLink")
        private CellLink link;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
    }
    
    /**
     * リンク型 - 初期値など
     *
     */
    private static class FormattedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsConverter(trim=true, defaultValue="http://myhome.com/")
        @XlsColumn(columnName="URI")
        private URI uri;
        
        @XlsConverter(trim=true, defaultValue="http://myhome.com/")
        @XlsColumn(columnName="CellLink")
        private CellLink link;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
    
}
