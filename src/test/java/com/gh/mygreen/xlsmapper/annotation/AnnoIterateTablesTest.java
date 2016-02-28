package com.gh.mygreen.xlsmapper.annotation;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link IterateTablesProcessor}のテスタ
 * アノテーション{@link XlsIterateTables}のテスタ。
 * @version 1.0
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
    public void test_load_it_normal() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(ClassTable table : sheet.classTables) {
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
    public void test_load_it_normal_array() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalArraySheet.class);
            
            NormalArraySheet sheet = mapper.load(in, NormalArraySheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, arrayWithSize(2));
                for(ClassTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 見出し用セルが見つからない
     */
    @Test(expected=CellNotFoundException.class)
    public void test_load_it_not_found_cell() throws Exception {
        
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
    public void test_load_it_optional() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(OptionalCellSheet.class);
            
            OptionalCellSheet sheet = mapper.load(in, OptionalCellSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(OptionalClassTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 連結した表
     */
    @Test
    public void test_load_it_concat_table() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(ConcatTableSheet.class);
            
            ConcatTableSheet sheet = mapper.load(in, ConcatTableSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(ConcatClassTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * メソッドにアノテーションを付与
     * @since 1.0
     */
    @Test
    public void test_load_it_methodAnno() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = mapper.load(in, MethodAnnoSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(MethodAnnoTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
        
    }
    
    private void assertTable(final ClassTable table, final SheetBindingErrors errors) {
        
        if(table.name.equals("1年2組")) {
            
            assertThat(table.no, is(1));
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
        } else if(table.name.equals("2年3組")) {
            
            assertThat(table.no, is(2));
            assertThat(table.name, is("2年3組"));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
            
        }
        
    }
    
    private void assertTable(final OptionalClassTable table, final SheetBindingErrors errors) {
        
        if(equalsStr(table.name, "1年2組")) {
            
            assertThat(table.no, is(1));
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
        } else if(equalsStr(table.name, null)) {
            
            assertThat(table.no, is(2));
            assertThat(table.name, is(nullValue()));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
        }
    }
    
    private void assertTable(final ConcatClassTable table, final SheetBindingErrors errors) {
        
        if(table.name.equals("1年2組")) {
            
            assertThat(table.no, is(1));
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
            assertThat(table.results, hasSize(3));
            for(int i=0; i < table.results.size(); i++) {
                ResultRecord record = table.results.get(i);
                int index = i+1;
                
                if(index == 1) {
                    assertThat(record.sansu, is(90));
                    assertThat(record.kokugo, is(70));
                    assertThat(record.sum, is(160));
                    
                } else if(index == 2) {
                    assertThat(record.sansu, is(80));
                    assertThat(record.kokugo, is(90));
                    assertThat(record.sum, is(170));
                    
                } else if(index == 3) {
                    // デフォルト値（合計）
                    assertThat(record.sansu, is(85));
                    assertThat(record.kokugo, is(80));
                    assertThat(record.sum, is(165));
                }
                
            }
            
        } else if(table.name.equals("2年3組")) {
            
            assertThat(table.no, is(2));
            assertThat(table.name, is("2年3組"));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
            
            assertThat(table.results, hasSize(4));
            for(int i=0; i < table.results.size(); i++) {
                ResultRecord record = table.results.get(i);
                int index = i+1;
                
                if(index == 1) {
                    assertThat(record.sansu, is(90));
                    assertThat(record.kokugo, is(70));
                    assertThat(record.sum, is(160));
                    
                } else if(index == 2) {
                    assertThat(record.sansu, is(80));
                    assertThat(record.kokugo, is(90));
                    assertThat(record.sum, is(170));
                    
                } else if(index == 3) {
                    assertThat(record.sansu, is(70));
                    assertThat(record.kokugo, is(60));
                    assertThat(record.sum, is(130));
                    
                } else if(index == 4) {
                    // デフォルト値（合計）
                    assertThat(record.sansu, is(80));
                    assertThat(record.kokugo, is(73));
                    assertThat(record.sum, is(153));
                }
                
            }
            
            
        }
        
    }
    
    private void assertTable(final MethodAnnoTable table, final SheetBindingErrors errors) {
        
        if(table.name.equals("1年2組")) {
            
            assertThat(table.no, is(1));
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))));
                    
                }
                
            }
            
        } else if(table.name.equals("2年3組")) {
            
            assertThat(table.no, is(2));
            assertThat(table.name, is("2年3組"));
            
            assertThat(table.persons, hasSize(3));
            for(PersonRecord record : table.persons) {
                
                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))));
                    
                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))));
                    
                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))));
                    
                }
                
            }
            
        }
        
    }
    
    /**
     * 書き込みのテスト - 通常の表のテスト
     */
    @Test
    public void test_save_it_normal() throws Exception {
        
        // テストデータの作成
        NormalSheet outSheet = new NormalSheet();
        
        outSheet.add(new ClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new ClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));
                
                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
                }
                
            }
            
        }
        
    }
    
    /**
     * 書き込みのテスト - 通常の表のテスト(配列形式)
     */
    @Test
    public void test_save_it_normal_array() throws Exception {
        
        // テストデータの作成
        NormalArraySheet outSheet = new NormalArraySheet();
        
        outSheet.add(new ClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new ClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(NormalArraySheet.class);
            
            NormalArraySheet sheet = mapper.load(in, NormalArraySheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, arrayWithSize(outSheet.classTables.length));
                
                for(int i=0; i < sheet.classTables.length; i++) {
                    assertRecord(sheet.classTables[i], outSheet.classTables[i], errors);
                }
                
            }
            
        }
        
    }
    
    /**
     * 書き出しのテスト - 見出し用セルが見つからない
     */
    @Test(expected=CellNotFoundException.class)
    public void test_save_it_not_found_cell() throws Exception {
        
        // テストデータの作成
        NotFoundCellSheet outSheet = new NotFoundCellSheet();
        
        outSheet.add(new ClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        
        outSheet.add(new ClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
    }
    
    /**
     * 書き出しのテスト - 見出しセルがない2
     * ・見出し用セルなどがオプション指定の場合
     */
    @Test
    public void test_save_it_optional() throws Exception {
        
        // テストデータの作成
        OptionalCellSheet outSheet = new OptionalCellSheet();
        
        outSheet.add(new OptionalClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        
        outSheet.add(new OptionalClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(OptionalCellSheet.class);
            
            OptionalCellSheet sheet = mapper.load(in, OptionalCellSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));
                
                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
                }
                
            }
            
        }
    }
    
    /**
     * 書き込みのテスト - 連結した表
     */
    @Test
    public void test_save_it_concat_table() throws Exception {
        
        // テストデータの作成
        ConcatTableSheet outSheet = new ConcatTableSheet();
        
        outSheet.add(new ConcatClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                
                .add(new ResultRecord().sansu(90).kokugo(70).setupSum())
                .add(new ResultRecord().sansu(80).kokugo(90).setupSum())
                .add(new ResultRecord().sansu(85).kokugo(80).setupSum())

        );
        
        outSheet.add(new ConcatClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
                
                .add(new ResultRecord().sansu(90).kokugo(70).setupSum())
                .add(new ResultRecord().sansu(80).kokugo(90).setupSum())
                .add(new ResultRecord().sansu(70).kokugo(50).setupSum())
                .add(new ResultRecord().sansu(80).kokugo(73).setupSum())
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(ConcatTableSheet.class);
            
            ConcatTableSheet sheet = mapper.load(in, ConcatTableSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));
                
                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
                }
                
            }
            
        }
    }
    
    /**
     * 書き込みのテスト - メソッドにアノテーションを付与
     * @since 1.0
     */
    @Test
    public void test_save_it_methodAnno() throws Exception {
        
        // テストデータの作成
        MethodAnnoSheet outSheet = new MethodAnnoSheet();
        
        outSheet.add(new MethodAnnoTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new MethodAnnoTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = mapper.load(in, MethodAnnoSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));
                
                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
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
    private void assertRecord(final ClassTable inRecord, final ClassTable outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        
        if(inRecord.persons != null) {
            assertThat(inRecord.persons, hasSize(outRecord.persons.size()));
            
            for(int i=0; i < inRecord.persons.size(); i++) {
                assertRecord(inRecord.persons.get(i), outRecord.persons.get(i), errors);
            }
            
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final OptionalClassTable inRecord, final OptionalClassTable outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        
        assertThat(inRecord.no, is(outRecord.no));
        
        if(inRecord.no == 2) {
            assertThat(inRecord.name, is(nullValue()));
        } else {
            assertThat(inRecord.name, is(outRecord.name));
            
        }
        
        if(inRecord.persons != null) {
            assertThat(inRecord.persons, hasSize(outRecord.persons.size()));
            
            for(int i=0; i < inRecord.persons.size(); i++) {
                assertRecord(inRecord.persons.get(i), outRecord.persons.get(i), errors);
            }
            
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final ConcatClassTable inRecord, final ConcatClassTable outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        
        if(inRecord.persons != null) {
            assertThat(inRecord.persons, hasSize(outRecord.persons.size()));
            
            for(int i=0; i < inRecord.persons.size(); i++) {
                assertRecord(inRecord.persons.get(i), outRecord.persons.get(i), errors);
            }
            
        }
        
        if(inRecord.results != null) {
            assertThat(inRecord.results, hasSize(outRecord.results.size()));
            
            for(int i=0; i < inRecord.results.size(); i++) {
                assertRecord(inRecord.results.get(i), outRecord.results.get(i), errors);
            }
            
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final PersonRecord inRecord, final PersonRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.birthday, is(outRecord.birthday));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final ResultRecord inRecord, final ResultRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
//        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.sansu, is(outRecord.sansu));
        assertThat(inRecord.kokugo, is(outRecord.kokugo));
        assertThat(inRecord.sum, is(outRecord.sum));
        
        
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final MethodAnnoTable inRecord, final MethodAnnoTable outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        
        if(inRecord.persons != null) {
            assertThat(inRecord.persons, hasSize(outRecord.persons.size()));
            
            for(int i=0; i < inRecord.persons.size(); i++) {
                assertRecord(inRecord.persons.get(i), outRecord.persons.get(i), errors);
            }
            
        }
    }
    
    @XlsSheet(name="通常の表")
    private static class NormalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<ClassTable> classTables;
        
        public NormalSheet add(ClassTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
        
    }
    
    /**
     * 配列による定義
     *
     */
    @XlsSheet(name="通常の表")
    private static class NormalArraySheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private ClassTable[] classTables;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public NormalArraySheet add(ClassTable table) {
            
            final List<ClassTable> list;
            if(classTables == null) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>(Arrays.asList(classTables));
            }
            
            list.add(table);
            table.no(list.size());
            
            this.classTables = list.toArray(new ClassTable[list.size()]);
            
            return this;
        }
        
    }
    
    @XlsSheet(name="見出しセルがない")
    private static class NotFoundCellSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<ClassTable> classTables;
        
        public NotFoundCellSheet add(ClassTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
        
    }
    
    /**
     * 繰り返し用のテーブルの定義
     */
    private static class ClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsHint(order=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;
        
        @XlsHint(order=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete)
        private List<PersonRecord> persons;
        
        public ClassTable no(int no) {
            this.no = no;
            return this;
        }
        
        public ClassTable name(String name) {
            this.name = name;
            return this;
        }
        
        public ClassTable add(PersonRecord record) {
            
            if(persons == null) {
                this.persons = new ArrayList<>();
            }
            
            this.persons.add(record);
            record.no(persons.size());
            
            return this;
        }
        
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
        
        @XlsDateConverter(javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="誕生日")
        private Date birthday;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public PersonRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public PersonRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public PersonRecord birthday(Date birthday) {
            this.birthday = birthday;
            return this;
        }
        
    }
    
    /**
     * ラベルと表がオプション設定の場合
     */
    @XlsSheet(name="見出しセルがない")
    private static class OptionalCellSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<OptionalClassTable> classTables;
        
        public OptionalCellSheet add(OptionalClassTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
    }
    
    /**
     * 繰り返し用のテーブルの定義
     * ・セルをオプション設定。
     */
    private static class OptionalClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsHint(order=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;
        
        @XlsHint(order=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, optional=true)
        private String name;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete)
        private List<PersonRecord> persons;
        
        public OptionalClassTable no(int no) {
            this.no = no;
            return this;
        }
        
        public OptionalClassTable name(String name) {
            this.name = name;
            return this;
        }
        
        public OptionalClassTable add(PersonRecord record) {
            
            if(persons == null) {
                this.persons = new ArrayList<>();
            }
            
            this.persons.add(record);
            record.no(persons.size());
            
            return this;
        }
        
    }
    
    @XlsSheet(name="連結した表")
    private static class ConcatTableSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<ConcatClassTable> classTables;
        
        public ConcatTableSheet add(ConcatClassTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
    }
    /**
     * 連結したテーブルの定義
     */
    private static class ConcatClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
//        @XlsHint(order=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;
        
//        @XlsHint(order=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, headerLimit=3, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete)
        private List<PersonRecord> persons;
        
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Empty, range=4, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Copy, remainedRecord=RemainedRecordOperate.Clear)
        private List<ResultRecord> results;
        
        public ConcatClassTable no(int no) {
            this.no = no;
            return this;
        }
        
        public ConcatClassTable name(String name) {
            this.name = name;
            return this;
        }
        
        public ConcatClassTable add(PersonRecord record) {
            
            if(persons == null) {
                this.persons = new ArrayList<>();
            }
            
            this.persons.add(record);
            record.no(persons.size());
            
            return this;
        }
        
        public ConcatClassTable add(ResultRecord record) {
            
            if(results == null) {
                this.results = new ArrayList<>();
            }
            
            this.results.add(record);
            record.no(results.size());
            
            return this;
        }
        
    }
    
    private static class ResultRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
//        @XlsColumn(columnName="index", optional=true)
        private int no;
        
        @XlsColumn(columnName="算数")
        private int sansu;
        
        @XlsColumn(columnName="国語")
        private int kokugo;
        
        @XlsColumn(columnName="合計")
        private int sum;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public ResultRecord setupSum() {
            this.sum = sansu + kokugo;
            return this;
        }
        
        public ResultRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public ResultRecord sansu(int sansu) {
            this.sansu = sansu;
            return this;
        }
        
        public ResultRecord kokugo(int kokugo) {
            this.kokugo = kokugo;
            return this;
        }
        
        public ResultRecord sum(int sum) {
            this.sum = sum;
            return this;
        }
        
    }
    
    @XlsSheet(name="メソッドにアノテーションを付与")
    private static class MethodAnnoSheet {
        
        private List<MethodAnnoTable> classTables;
        
        public MethodAnnoSheet add(MethodAnnoTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        public List<MethodAnnoTable> getClassTables() {
            return classTables;
        }
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        public void setClassTables(List<MethodAnnoTable> classTables) {
            this.classTables = classTables;
        }
        
        // 位置情報／ラベル情報
        private Point classTablesPoint;
        
        private String classTablesLabel;
        
        public void setClassTablesPoint(Point classTablesPoint) {
            this.classTablesPoint = classTablesPoint;
        }
        
        public void setClassTablesLabel(String classTablesLabel) {
            this.classTablesLabel = classTablesLabel;
        }
        
        
    }
    
    /**
     * メソッドにアノテーションを付与したのテーブルの定義
     */
    private static class MethodAnnoTable {
        
        private int no;
        
        private String name;
        
        private List<PersonRecord> persons;
        
        @XlsHint(order=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        public int getNo() {
            return no;
        }
        
        @XlsHint(order=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        public void setNo(int no) {
            this.no = no;
        }
        
        @XlsHint(order=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        public String getName() {
            return name;
        }
        
        @XlsHint(order=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        public void setName(String name) {
            this.name = name;
        }
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert, remainedRecord=RemainedRecordOperate.Delete)
        public List<PersonRecord> getPersons() {
            return persons;
        }
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, skipEmptyRecord=true)
        public void setPersons(List<PersonRecord> persons) {
            this.persons = persons;
        }
        
        // 位置、ラベル情報
        private Point noPosition;
        private Point namePosition;
        private Point personsPosition;
        
        private String noLabel;
        private String nameLabel;
        private String personsLabel;
        
        public void setNoPosition(Point noPosition) {
            this.noPosition = noPosition;
        }
        
        public void setNamePosition(Point namePosition) {
            this.namePosition = namePosition;
        }
        
        public void setPersonsPosition(Point personsPosition) {
            this.personsPosition = personsPosition;
        }
        
        public void setNoLabel(String noLabel) {
            this.noLabel = noLabel;
        }
        
        public void setNameLabel(String nameLabel) {
            this.nameLabel = nameLabel;
        }
        
        public void setPersonsLabel(String personsLabel) {
            this.personsLabel = personsLabel;
        }
        
        // 値設定用のメソッド
        
        public MethodAnnoTable no(int no) {
            this.no = no;
            return this;
        }
        
        public MethodAnnoTable name(String name) {
            this.name = name;
            return this;
        }
        
        public MethodAnnoTable add(PersonRecord record) {
            
            if(persons == null) {
                this.persons = new ArrayList<>();
            }
            
            this.persons.add(record);
            record.no(persons.size());
            
            return this;
        }
        
    }
    
}
