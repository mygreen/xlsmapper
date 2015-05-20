package com.gh.mygreen.xlsmapper.annotation;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link IterateTablesProcessor}のテスタ
 * アノテーション{@link XlsIterateTables}のテスタ。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoIterateTablesTest {
    
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
     * 通常の表のテスト
     */
    @Test
    public void test_load_normal() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            if(sheet.classeTables != null) {
                assertThat(sheet.classeTables, hasSize(2));
                for(ClassTable table : sheet.classeTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 通常の表のテスト
     * ・配列型による定義
     */
    @Test
    public void test_load_normal_array() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalArraySheet.class);
            
            NormalArraySheet sheet = mapper.load(in, NormalArraySheet.class, errors);
            
            if(sheet.classeTables != null) {
                assertThat(sheet.classeTables, arrayWithSize(2));
                for(ClassTable table : sheet.classeTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 見出し用セルが見つからない
     */
    @Test(expected=CellNotFoundException.class)
    public void test_load_not_found_cell() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NotFoundCellSheet.class);
            
            NotFoundCellSheet sheet = mapper.load(in, NotFoundCellSheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 見出しセルがない2
     * ・見出し用セルなどがオプション指定の場合
     */
    @Test
    public void test_load_optional() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(OptionalCellSheet.class);
            
            OptionalCellSheet sheet = mapper.load(in, OptionalCellSheet.class, errors);
            
            if(sheet.classeTables != null) {
                assertThat(sheet.classeTables, hasSize(2));
                for(OptionalClassTable table : sheet.classeTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 連結した表
     */
    @Test
    public void test_concat_table() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(ConcatTableSheet.class);
            
            ConcatTableSheet sheet = mapper.load(in, ConcatTableSheet.class, errors);
            
            if(sheet.classeTables != null) {
                assertThat(sheet.classeTables, hasSize(2));
                for(ConcatClassTable table : sheet.classeTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    private void assertTable(final ClassTable table, final SheetBindingErrors errors) {
        
        if(table.name.equals("1年2組")) {
            
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
        } else if(table.name.equals("2年3組")) {
            
            assertThat(table.name, is("2年3組"));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
            
        }
        
    }
    
    private void assertTable(final OptionalClassTable table, final SheetBindingErrors errors) {
        
        if(equalsStr(table.name, "1年2組")) {
            
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
        } else if(equalsStr(table.name, null)) {
            
            assertThat(table.name, is(nullValue()));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
        }
    }
    
    private void assertTable(final ConcatClassTable table, final SheetBindingErrors errors) {
        
        if(table.name.equals("1年2組")) {
            
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
            assertThat(table.results, hasSize(3));
            for(ResultRecord record : table.results) {
                if(record.no == 1) {
                    assertThat(record.sansu, is(90));
                    assertThat(record.kokugo, is(70));
                    assertThat(record.sum, is(160));
                    
                } else if(record.no == 2) {
                    assertThat(record.sansu, is(80));
                    assertThat(record.kokugo, is(90));
                    assertThat(record.sum, is(170));
                    
                } else if(record.no == 0) {
                    // デフォルト値（合計）
                    assertThat(record.sansu, is(85));
                    assertThat(record.kokugo, is(80));
                    assertThat(record.sum, is(165));
                }
                
            }
            
        } else if(table.name.equals("2年3組")) {
            
            assertThat(table.name, is("2年3組"));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(utilDate(timestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
            
            assertThat(table.results, hasSize(4));
            for(ResultRecord record : table.results) {
                if(record.no == 1) {
                    assertThat(record.sansu, is(90));
                    assertThat(record.kokugo, is(70));
                    assertThat(record.sum, is(160));
                    
                } else if(record.no == 2) {
                    assertThat(record.sansu, is(80));
                    assertThat(record.kokugo, is(90));
                    assertThat(record.sum, is(170));
                    
                } else if(record.no == 3) {
                    assertThat(record.sansu, is(70));
                    assertThat(record.kokugo, is(60));
                    assertThat(record.sum, is(130));
                    
                } else if(record.no == 0) {
                    // デフォルト値（合計）
                    assertThat(record.sansu, is(80));
                    assertThat(record.kokugo, is(73));
                    assertThat(record.sum, is(153));
                }
                
            }
            
            
        }
        
    }
    
    @XlsSheet(name="通常の表")
    private static class NormalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=2)
        private List<ClassTable> classeTables;
        
    }
    
    /**
     * 配列による定義
     *
     */
    @XlsSheet(name="通常の表")
    private static class NormalArraySheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=2)
        private ClassTable[] classeTables;
        
    }
    
    @XlsSheet(name="見出しセルがない")
    private static class NotFoundCellSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=2)
        private List<ClassTable> classeTables;
        
    }
    
    /**
     * 繰り返し用のテーブルの定義
     */
    private static class ClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        private List<PersonRecord> persons;
        
    }
    
    /**
     * HorizontalRecordsのレコードの定義
     */
    private static class PersonRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsDateConverter(pattern="yyyy年M月d日")
        @XlsColumn(columnName="誕生日")
        private Date birthday;
        
    }
    
    /**
     * ラベルと表がオプション設定の場合
     */
    @XlsSheet(name="見出しセルがない")
    private static class OptionalCellSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=2)
        private List<OptionalClassTable> classeTables;
        
    }
    
    /**
     * 繰り返し用のテーブルの定義
     * ・セルをオプション設定。
     */
    private static class OptionalClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, optional=true)
        private String name;
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, optional=true)
        private List<PersonRecord> persons;
        
    }
    
    @XlsSheet(name="連結した表")
    private static class ConcatTableSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=2)
        private List<ConcatClassTable> classeTables;
        
    }
    /**
     * 連結したテーブルの定義
     */
    private static class ConcatClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, bottom=2, headerLimit=3)
        private List<PersonRecord> persons;
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Empty, bottom=2, range=4)
        private List<ResultRecord> results;
        
    }
    
    private static class ResultRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="算数")
        private int sansu;
        
        @XlsColumn(columnName="国語")
        private int kokugo;
        
        @XlsColumn(columnName="合計")
        private int sum;
        
    }
    
}
