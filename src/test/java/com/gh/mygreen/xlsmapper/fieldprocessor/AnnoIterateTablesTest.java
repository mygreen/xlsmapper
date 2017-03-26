package com.gh.mygreen.xlsmapper.fieldprocessor;

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

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOperator;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOperator.OverOperate;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOperator.RemainedOperate;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link IterateTablesProcessor}のテスタ
 * アノテーション{@link XlsIterateTables}のテスタ。
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoIterateTablesTest {
    
    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }
    
    /**
     * 通常の表のテスト
     */
    @Test
    public void test_load_it_normal() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            NormalSheet sheet = mapper.load(in, NormalSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(HorizontalClassTable table : sheet.classTables) {
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalArraySheet.class);
            
            NormalArraySheet sheet = mapper.load(in, NormalArraySheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, arrayWithSize(2));
                for(HorizontalClassTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 縦方向の表のテスト
     */
    @Test
    public void test_load_it_vertical() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            VerticalSheet sheet = mapper.load(in, VerticalSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(VerticalClassTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
    }
    
    /**
     * 横＋縦方向の表のテスト
     * ※v2.0の段階では、対応していない
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_it_horizontal_vertical() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            try {
                mapper.load(in, HorizontalAndVerticalSheet.class, errors);
                
                fail();
            } catch(AnnotationInvalidException e) {
//                e.printStackTrace();
                throw e;
            }
        }
    }
    
    /**
     * 見出し用セルが見つからない
     */
    @Test(expected=CellNotFoundException.class)
    public void test_load_it_not_found_cell() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
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
    
    /**
     * 正規表現、正規化で一致
     * @since 1.1
     */
    @Test
    public void test_load_it_regex() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_IterateTables.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            RegexSheet sheet = mapper.load(in, RegexSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(RegexSheet.RegexpTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }
        
    }
    
    private void assertTable(final HorizontalClassTable table, final SheetBindingErrors errors) {
        
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
    
    private void assertTable(final VerticalClassTable table, final SheetBindingErrors errors) {
        
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
    
    private void assertTable(final RegexSheet.RegexpTable table, final SheetBindingErrors errors) {
        
        if(table.name.equals("1年2組")) {
            
            assertThat(table.no, is(1));
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.persons, hasSize(2));
            for(RegexSheet.RegexRecord record : table.persons) {
                
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
            for(RegexSheet.RegexRecord record : table.persons) {
                
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
        
        outSheet.add(new HorizontalClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new HorizontalClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
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
        
        outSheet.add(new HorizontalClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new HorizontalClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
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
     * 書き込みのテスト - 縦方向の表のテスト
     */
    @Test
    public void test_save_it_vertical() throws Exception {
        
        // テストデータの作成
        VerticalSheet outSheet = new VerticalSheet();
        
        outSheet.add(new VerticalClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new VerticalClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(NormalSheet.class);
            
            VerticalSheet sheet = mapper.load(in, VerticalSheet.class, errors);
            
            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));
                
                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
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
        
        outSheet.add(new HorizontalClassTable().name("1年2組")
                .add(new PersonRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        
        outSheet.add(new HorizontalClassTable().name("2年3組")
                .add(new PersonRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new PersonRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new PersonRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
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
     * 書き込みのテスト - ラベルの正規表現、正規化のテスト
     * @since 1.1
     */
    @Test
    public void test_save_it_regex() throws Exception {
        
        // テストデータの作成
        RegexSheet outSheet = new RegexSheet();
        
        outSheet.add(new RegexSheet.RegexpTable().name("1年2組")
                .add(new RegexSheet.RegexRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );
        
        outSheet.add(new RegexSheet.RegexpTable().name("2年3組")
                .add(new RegexSheet.RegexRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);
        
        File outFile = new File(OUT_DIR, "anno_IterateTables_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_IterateTables_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(RegexSheet.class);
            
            RegexSheet sheet = mapper.load(in, RegexSheet.class, errors);
            
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
    private void assertRecord(final HorizontalClassTable inRecord, final HorizontalClassTable outRecord, final SheetBindingErrors errors) {
        
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
    private void assertRecord(final VerticalClassTable inRecord, final VerticalClassTable outRecord, final SheetBindingErrors errors) {
        
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
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final RegexSheet.RegexpTable inRecord, final RegexSheet.RegexpTable outRecord, final SheetBindingErrors errors) {
        
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
    private void assertRecord(final RegexSheet.RegexRecord inRecord, final RegexSheet.RegexRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.birthday, is(outRecord.birthday));
    }
    
    @XlsSheet(name="通常の表")
    private static class NormalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<HorizontalClassTable> classTables;
        
        public NormalSheet add(HorizontalClassTable table) {
            
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
        private HorizontalClassTable[] classTables;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public NormalArraySheet add(HorizontalClassTable table) {
            
            final List<HorizontalClassTable> list;
            if(classTables == null) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>(Arrays.asList(classTables));
            }
            
            list.add(table);
            table.no(list.size());
            
            this.classTables = list.toArray(new HorizontalClassTable[list.size()]);
            
            return this;
        }
        
    }
    
    @XlsSheet(name="縦方向の表")
    private static class VerticalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<VerticalClassTable> classTables;
        
        public VerticalSheet add(VerticalClassTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
        
    }
    
    /**
     * 横と縦方向の2つの表は対応していない。
     *
     * @since 2.0
     *
     */
    @XlsSheet(name="横と縦方向の表")
    private static class HorizontalAndVerticalSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<HorizontalAndVerticalClassTable> classTables;
        
        private static class HorizontalAndVerticalClassTable {
            
            @XlsOrder(value=1)
            @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
            private int no;
            
            @XlsOrder(value=2)
            @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
            private String name;
            
            @XlsOrder(value=3)
            @XlsHorizontalRecords(terminal=RecordTerminal.Border, headerLimit=3)
            @XlsRecordOperator(overCase=OverOperate.Copy, remainedCase=RemainedOperate.Clear)
            private List<PersonRecord> persons;
            
            @XlsOrder(value=4)
            @XlsVerticalRecords(terminal=RecordTerminal.Border, right=4)
            @XlsRecordOperator(overCase=OverOperate.Copy, remainedCase=RemainedOperate.Clear)
            private List<AttendRecord> attends;
        }
        
        private static class AttendRecord {
            
            @XlsColumn(columnName="出席")
            private int attendances;
            
            @XlsColumn(columnName="欠席")
            private int absences;
            
            @XlsColumn(columnName="合計")
            private int amount;
            
        }
    }
    
    @XlsSheet(name="見出しセルがない")
    private static class NotFoundCellSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<HorizontalClassTable> classTables;
        
        public NotFoundCellSheet add(HorizontalClassTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
        
    }
    
    /**
     * 繰り返し用のテーブルの定義 - horizontal
     */
    private static class HorizontalClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsOrder(value=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;
        
        @XlsOrder(value=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;
        
        @XlsOrder(value=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOperator(overCase=OverOperate.Insert, remainedCase=RemainedOperate.Delete)
        private List<PersonRecord> persons;
        
        public HorizontalClassTable no(int no) {
            this.no = no;
            return this;
        }
        
        public HorizontalClassTable name(String name) {
            this.name = name;
            return this;
        }
        
        public HorizontalClassTable add(PersonRecord record) {
            
            if(persons == null) {
                this.persons = new ArrayList<>();
            }
            
            this.persons.add(record);
            record.no(persons.size());
            
            return this;
        }
        
    }
    
    /**
     * 繰り返し用のテーブルの定義 - vertical
     */
    private static class VerticalClassTable {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsOrder(value=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;
        
        @XlsOrder(value=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;
        
        @XlsOrder(value=3)
        @XlsVerticalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOperator(overCase=OverOperate.Copy, remainedCase=RemainedOperate.Clear)
        private List<PersonRecord> persons;
        
        public VerticalClassTable no(int no) {
            this.no = no;
            return this;
        }
        
        public VerticalClassTable name(String name) {
            this.name = name;
            return this;
        }
        
        public VerticalClassTable add(PersonRecord record) {
            
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
        
        @XlsDateConverter(javaPattern="yyyy年M月d日", excelPattern="yyyy/m/d")
        @XlsColumn(columnName="誕生日")
        private Date birthday;
        
        @XlsIgnorable
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
        
        @XlsOrder(value=1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;
        
        @XlsOrder(value=2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, optional=true)
        private String name;
        
        @XlsOrder(value=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOperator(overCase=OverOperate.Insert, remainedCase=RemainedOperate.Delete)
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
        
        @XlsOrder(value=3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, headerLimit=3)
        @XlsRecordOperator(overCase=OverOperate.Insert, remainedCase=RemainedOperate.Delete)
        private List<PersonRecord> persons;
        
        @XlsOrder(value=4)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Empty, range=4)
        @XlsRecordOperator(overCase=OverOperate.Copy, remainedCase=RemainedOperate.Clear)
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
        
        @XlsIgnorable
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
        
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        public int getNo() {
            return no;
        }
        
        @XlsOrder(value=1)
        public void setNo(int no) {
            this.no = no;
        }
        
        @XlsOrder(value=2)
        public String getName() {
            return name;
        }
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        public void setName(String name) {
            this.name = name;
        }
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOperator(overCase=OverOperate.Insert, remainedCase=RemainedOperate.Delete)
        public List<PersonRecord> getPersons() {
            return persons;
        }
        
        @XlsOrder(value=3)
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
    
    @XlsSheet(name="正規表現で一致")
    private static class RegexSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsIterateTables(tableLabel="/クラス情報.*/", bottom=3)
        private List<RegexpTable> classTables;
        
        public RegexSheet add(RegexpTable table) {
            
            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }
            
            this.classTables.add(table);
            table.no(classTables.size());
            
            return this;
        }
        
        private static class RegexpTable {
            
            
            // 位置情報／ラベル情報
            private Point classTablesPoint;
            
            private String classTablesLabel;
            
            @XlsOrder(value=1)
            @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
            private int no;
            
            @XlsOrder(value=2)
            @XlsLabelledCell(label="/クラス名.*/", type=LabelledCellType.Right)
            private String name;
            
            @XlsOrder(value=3)
            @XlsHorizontalRecords(tableLabel="/クラス情報.*/", terminal=RecordTerminal.Border)
            @XlsRecordOperator(overCase=OverOperate.Insert, remainedCase=RemainedOperate.Delete)
            private List<RegexRecord> persons;
            
            // 値設定用のメソッド
            
            public RegexpTable no(int no) {
                this.no = no;
                return this;
            }
            
            public RegexpTable name(String name) {
                this.name = name;
                return this;
            }
            
            public RegexpTable add(RegexRecord record) {
                
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
        private static class RegexRecord {
            
            private Map<String, Point> positions;
            
            private Map<String, String> labels;
            
            @XlsColumn(columnName="No.")
            private int no;
            
            @XlsColumn(columnName="/氏名.*/")
            private String name;
            
            @XlsDateConverter(javaPattern="yyyy年M月d日")
            @XlsColumn(columnName="誕生日")
            private Date birthday;
            
            @XlsIgnorable
            public boolean isEmpty() {
                return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
            }
            
            public RegexRecord no(int no) {
                this.no = no;
                return this;
            }
            
            public RegexRecord name(String name) {
                this.name = name;
                return this;
            }
            
            public RegexRecord birthday(Date birthday) {
                this.birthday = birthday;
                return this;
            }
            
        }
    }
    
    
    
}
