package com.gh.mygreen.xlsmapper.annotation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.IsEmptyComparator;
import com.gh.mygreen.xlsmapper.IsEmptyConfig;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.github.mygreen.cellformatter.lang.Utils;

/**
 * {@link HorizontalRecordsProcessor}のテスタ
 * アノテーション{@link XlsHorizontalRecords}のテスタ。
 * @version 1.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoHorizontalRecordsTest {
    
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
     * 開始位置の指定のテスト
     */
    @Test
    public void test_load_hr_startedPosition() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionSheet.class);
            
            StartedPositionSheet sheet = mapper.load(in, StartedPositionSheet.class, errors);
            
            if(sheet.normalRecords1 != null) {
                assertThat(sheet.normalRecords1, hasSize(2));
                for(NormalRecord record : sheet.normalRecords1) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.normalRecords2 != null) {
                assertThat(sheet.normalRecords2, hasSize(2));
                for(NormalRecord record : sheet.normalRecords2) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.normalRecords3 != null) {
                assertThat(sheet.normalRecords3, hasSize(2));
                for(NormalRecord record : sheet.normalRecords3) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.normalRecords4 != null) {
                assertThat(sheet.normalRecords4, hasSize(2));
                for(NormalRecord record : sheet.normalRecords4) {
                    assertRecord(record, errors);
                }
            }
            
            assertThat(sheet.normalRecords5, is(nullValue()));
            
        }
    }
    
    /**
     * 開始位置の指定のテスト - ラベル指定が不正
     */
    @Test(expected=CellNotFoundException.class)
    public void test_load_hr_startedPosition_error1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionError1Sheet.class);
            
            StartedPositionError1Sheet sheet = mapper.load(in, StartedPositionError1Sheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 開始位置の指定のテスト - アドレス指定の書式が不正
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_hr_startedPosition_error2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionError2Sheet.class);
            
            StartedPositionError2Sheet sheet = mapper.load(in, StartedPositionError2Sheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 開始位置の指定のテスト - インデックス指定の書式が不正
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_hr_startedPosition_error3() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionError3Sheet.class);
            
            StartedPositionError3Sheet sheet = mapper.load(in, StartedPositionError3Sheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 終了位置の指定のテスト
     */
    @Test
    public void test_load_hr_endPosition() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(EndPositionSheet.class);
            
            EndPositionSheet sheet = mapper.load(in, EndPositionSheet.class, errors);
            
            if(sheet.normalRecords1 != null) {
                assertThat(sheet.normalRecords1, hasSize(2));
                for(NormalRecord record : sheet.normalRecords1) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.normalRecords2 != null) {
                assertThat(sheet.normalRecords2, hasSize(3));
                for(NormalRecord record : sheet.normalRecords2) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.normalRecords3 != null) {
                assertThat(sheet.normalRecords3, hasSize(2));
                for(NormalRecord record : sheet.normalRecords3) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.normalRecords4 != null) {
                assertThat(sheet.normalRecords4, hasSize(2));
                for(NormalRecord record : sheet.normalRecords4) {
                    assertRecord(record, errors);
                }
            }
            
        }
    }
    
    /**
     * カラムの設定テスト
     */
    @Test
    public void test_load_hr_columnSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(ColumnSettingSheet.class);
            
            ColumnSettingSheet sheet = mapper.load(in, ColumnSettingSheet.class, errors);
            
            if(sheet.mergedRecords != null) {
                assertThat(sheet.mergedRecords, hasSize(7));
                for(MergedRecord record : sheet.mergedRecords) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.headerMergedRecords != null) {
                assertThat(sheet.headerMergedRecords, hasSize(2));
                for(HeaderMergedRecord record : sheet.headerMergedRecords) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.optionalRecords1 != null) {
                assertThat(sheet.optionalRecords1, hasSize(2));
                for(OptionalRecord record : sheet.optionalRecords1) {
                    assertRecord(record, errors, true);
                }
            }
            
            if(sheet.optionalRecords2 != null) {
                assertThat(sheet.optionalRecords2, hasSize(2));
                for(OptionalRecord record : sheet.optionalRecords2) {
                    assertRecord(record, errors, false);
                }
            }
            
            if(sheet.convertedRecord != null) {
                assertThat(sheet.convertedRecord, hasSize(2));
                for(ConvertedRecord record : sheet.convertedRecord) {
                    assertRecord(record, errors, false);
                }
            }
            
        }
    }
    
    /**
     * カラムの設定テスト - バインドエラー
     */
    @Test(expected=TypeBindException.class)
    public void test_load_hr_columnSetting_bind_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(ColumnSettingSheet.class);
            
            ColumnSettingSheet sheet = mapper.load(in, ColumnSettingSheet.class, errors);
            
            fail();
            
        }
    }
    
    /**
     * カラムの設定テスト
     */
    @Test
    public void test_load_hr_mapColumnSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(MapColumnSettingSheet.class);
            
            MapColumnSettingSheet sheet = mapper.load(in, MapColumnSettingSheet.class, errors);
            
            if(sheet.mapRecords1 != null) {
                assertThat(sheet.mapRecords1, hasSize(2));
                for(MapRecord record : sheet.mapRecords1) {
                    assertRecord(record, errors, false);
                }
            }
            
            if(sheet.mapRecords2 != null) {
                assertThat(sheet.mapRecords2, hasSize(2));
                for(MapConvertedRecord record : sheet.mapRecords2) {
                    assertRecord(record, errors, false);
                }
            }
            
        }
    }
    
    /**
     * カラムの設定テスト - バインドエラー
     */
    @Test(expected=TypeBindException.class)
    public void test_load_hr_mapColumnSetting_bind_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(MapColumnSettingSheet.class);
            
            MapColumnSettingSheet sheet = mapper.load(in, MapColumnSettingSheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 様々なレコード
     * ・空のレコードのスキップ確認
     * ・配列型の確認
     */
    @Test
    public void test_load_hr_recordSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(RecodSettingSheet.class);
            
            RecodSettingSheet sheet = mapper.load(in, RecodSettingSheet.class, errors);
            
            if(sheet.skipList != null) {
                assertThat(sheet.skipList, hasSize(3));
                for(EmptySkipRecord record : sheet.skipList) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.skipArray != null) {
                assertThat(sheet.skipArray, arrayWithSize(3));
                for(EmptySkipRecord record : sheet.skipArray) {
                    assertRecord(record, errors);
                }
            }
            
        }
    }
    
    /**
     * 読み込み時のテスト - メソッドに付与したアノテーション
     * @since 1.0
     */
    @Test
    public void test_load_hr_methodAnno() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = mapper.load(in, MethodAnnoSheet.class, errors);
            
            if(sheet.records != null) {
                assertThat(sheet.records, hasSize(3));
                for(MethodAnnoRecord record : sheet.records) {
                    assertRecord(record, errors);
                }
            }
            
            if(sheet.mapRecords != null) {
                assertThat(sheet.mapRecords, arrayWithSize(2));
                for(MethodAnnoMapRecord record : sheet.mapRecords) {
                    assertRecord(record, errors, false);
                }
            }
            
        }
        
    }
    
    /**
     * 連結した表のテスト
     * @throws Exception
     */
    @Test
    public void test_load_hr_concatTable() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_HorizonalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(ConcatSheet.class);
            
            ConcatSheet sheet = mapper.load(in, ConcatSheet.class, errors);
            
            if(sheet.userRecords != null) {
                
                assertThat(sheet.userRecords, hasSize(2));
                for(ConcatSheet.UserRecord record : sheet.userRecords) {
                    assertRecord(record, errors);
                }
                
            }
            
            if(sheet.resultRecords != null) {
                
                assertThat(sheet.resultRecords, hasSize(3));
                for(ConcatSheet.ResultRecord record : sheet.resultRecords) {
                    assertRecord(record, errors);
                }
                
                
            }
        }
        
    }
    
    private void assertRecord(final NormalRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.name, is("名前1"));
            assertThat(record.value, is(12.456));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("名前2"));
            assertThat(record.value, is(-12.0));
        }
        
    }
    
    private void assertRecord(final MergedRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.category, is(Category.Info));
            assertThat(record.description, is("説明1"));
            
        } else if(record.no == 2) {
            assertThat(record.category, is(Category.Info));
            assertThat(record.description, is("説明2"));
            
        } else if(record.no == 3) {
            assertThat(record.category, is(Category.Warn));
            assertThat(record.description, is("説明2"));
            
        } else if(record.no == 4) {
            assertThat(record.category, is(Category.Warn));
            assertThat(record.description, is("説明2"));
            
        } else if(record.no == 5) {
            assertThat(record.category, is(Category.Error));
            assertThat(record.description, is("説明3"));
            
        } else if(record.no == 6) {
            assertThat(record.category, is(Category.Info));
            assertThat(record.description, is(nullValue()));
            
        } else if(record.no == 7) {
            assertThat(record.category, is(Category.Info));
            assertThat(record.description, is(nullValue()));
            
        }
        
    }
    
    private void assertRecord(final HeaderMergedRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.mail, is("taro.yamada@example.com"));
            assertThat(record.tel, is("0000-1111-2222"));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.mail, is("jiro.suzuki@example.com"));
            assertThat(record.tel, is("0000-3333-4444"));
        }
        
    }
    
    private void assertRecord(final OptionalRecord record, final SheetBindingErrors errors, boolean hasCell) {
        
        if(record.no == 1) {
            assertThat(record.name, is("名前1"));
            assertThat(record.value, is(12.456));
            
            if(hasCell) {
                assertThat(record.comment, is("コメント1"));
            } else {
                assertThat(record.comment, is(nullValue()));
            }
            
        } else if(record.no == 2) {
            assertThat(record.name, is("名前2"));
            assertThat(record.value, is(-12.0));
            
            if(hasCell) {
                assertThat(record.comment, is("コメント2"));
            } else {
                assertThat(record.comment, is(nullValue()));
            }
        }
        
    }
    
    private void assertRecord(final ConvertedRecord record, final SheetBindingErrors errors, boolean hasCell) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("birthday"))).isTypeBindFailure(), is(true));
            assertThat(record.age, is(14));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("age"))).isTypeBindFailure(), is(true));
            
        }
        
    }
    
    private void assertRecord(final MapRecord record, final SheetBindingErrors errors, boolean hasCell) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.dateAttended.get("4月1日"), is("出席"));
            assertThat(record.dateAttended.get("4月2日"), is("出席"));
            assertThat(record.dateAttended.get("4月3日"), is(nullValue()));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.dateAttended.get("4月1日"), is("欠席"));
            assertThat(record.dateAttended.get("4月2日"), is("-"));
            assertThat(record.dateAttended.get("4月3日"), is("出席"));
        }
        
    }
    
    private void assertRecord(final MapConvertedRecord record, final SheetBindingErrors errors, boolean hasCell) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.dateAttended.get("4月1日"), is(true));
            assertThat(record.dateAttended.get("4月2日"), is(true));
            assertThat(record.dateAttended.get("4月3日"), is(nullValue()));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.dateAttended.get("4月1日"), is(false));
            assertThat(cellFieldError(errors, cellAddress(record.positions.get("dateAttended[4月2日]"))).isTypeBindFailure(), is(true));
            assertThat(record.dateAttended.get("4月3日"), is(true));
        }
        
    }
    
    private void assertRecord(final EmptySkipRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1989-01-02 00:00:00.000"))));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
            
        } else if(record.no == 4) {
            assertThat(record.name, is("林三郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1992-04-14 00:00:00.000"))));
        }
        
    }
    
    private void assertRecord(final MethodAnnoRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1989-01-02 00:00:00.000"))));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
            
        } else if(record.no == 4) {
            assertThat(record.name, is("林三郎"));
            assertThat(record.birthday, is(toUtilDate(toTimestamp("1992-04-14 00:00:00.000"))));
        }
        
    }
    
    private void assertRecord(final MethodAnnoMapRecord record, final SheetBindingErrors errors, boolean hasCell) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.dateAttended.get("4月1日"), is(true));
            assertThat(record.dateAttended.get("4月2日"), is(true));
            assertThat(record.dateAttended.get("4月3日"), is(nullValue()));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.dateAttended.get("4月1日"), is(false));
            assertThat(cellFieldError(errors, cellAddress(record.dateAttendedPosition.get("4月2日"))).isTypeBindFailure(), is(true));
            assertThat(record.dateAttended.get("4月3日"), is(true));
        }
        
    }
    
    private void assertRecord(final ConcatSheet.UserRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.name, is("山田太郎"));
            assertThat(record.birthDady, is(toUtilDate(Timestamp.valueOf("2000-04-01 00:00:00.000"))));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.birthDady, is(toUtilDate(Timestamp.valueOf("2000-04-02 00:00:00.000"))));
        }
        
    }
    
    private void assertRecord(final ConcatSheet.ResultRecord record, final SheetBindingErrors errors) {
        
        if(record.no == 1) {
            assertThat(record.sansu, is(90));
            assertThat(record.kokugo, is(70));
            assertThat(record.sum, is(160));
            
        } else if(record.no == 2) {
            assertThat(record.sansu, is(80));
            assertThat(record.kokugo, is(90));
            assertThat(record.sum, is(170));
            
        } else if(record.no == 99) {
            // 平均
            assertThat(record.sansu, is(85));
            assertThat(record.kokugo, is(80));
            assertThat(record.sum, is(165));
            
        }
        
    }
    
    /**
     * 書き込みのテスト - 開始位置の判定
     */
    @Test
    public void test_save_hr_startedPositoin() throws Exception {
        
        // テストデータの作成
        StartedPositionSheet outSheet = new StartedPositionSheet();
        
        outSheet.add1(new NormalRecord().name("1-名前1").value(12.345));
        outSheet.add1(new NormalRecord().name("1-名前2").value(-54.321));
        
        outSheet.add2(new NormalRecord().name("2-名前1").value(12.345));
        outSheet.add2(new NormalRecord().name("2-名前2").value(-54.321));
        
        outSheet.add3(new NormalRecord().name("3-名前1").value(12.345));
        outSheet.add3(new NormalRecord().name("3-名前2").value(-54.321));
        
        outSheet.add4(new NormalRecord().name("4-名前1").value(12.345));
        outSheet.add4(new NormalRecord().name("4-名前2").value(-54.321));
        
        outSheet.add5(new NormalRecord().name("5-名前1").value(12.345));
        outSheet.add5(new NormalRecord().name("5-名前2").value(-54.321));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionSheet.class);
            
            StartedPositionSheet sheet = mapper.load(in, StartedPositionSheet.class, errors);
            
            if(sheet.normalRecords1 != null) {
                assertThat(sheet.normalRecords1, hasSize(outSheet.normalRecords1.size()));
                
                for(int i=0; i < sheet.normalRecords1.size(); i++) {
                    assertRecord(sheet.normalRecords1.get(i), outSheet.normalRecords1.get(i), errors);
                }
                
            }
            
            if(sheet.normalRecords2 != null) {
                assertThat(sheet.normalRecords2, hasSize(outSheet.normalRecords2.size()));
                
                for(int i=0; i < sheet.normalRecords2.size(); i++) {
                    assertRecord(sheet.normalRecords2.get(i), outSheet.normalRecords2.get(i), errors);
                }
                
            }
            
            if(sheet.normalRecords3 != null) {
                assertThat(sheet.normalRecords3, hasSize(outSheet.normalRecords3.size()));
                
                for(int i=0; i < sheet.normalRecords3.size(); i++) {
                    assertRecord(sheet.normalRecords3.get(i), outSheet.normalRecords3.get(i), errors);
                }
                
            }
            
            if(sheet.normalRecords4 != null) {
                assertThat(sheet.normalRecords4, hasSize(outSheet.normalRecords4.size()));
                
                for(int i=0; i < sheet.normalRecords4.size(); i++) {
                    assertRecord(sheet.normalRecords4.get(i), outSheet.normalRecords4.get(i), errors);
                }
                
            }
            
            assertThat(sheet.normalRecords5, is(nullValue()));
            
        }
        
    }
    
    /**
     * 書き込むのテスト -  開始位置の指定のテスト - ラベル指定が不正
     * @throws Exception
     */
    @Test(expected=CellNotFoundException.class)
    public void test_save_hr_startedPosition_errors1() throws Exception {
        
        // テストデータの作成
        StartedPositionError1Sheet outSheet = new StartedPositionError1Sheet();
        
        outSheet.add(new NormalRecord().name("名前1").value(12.345));
        outSheet.add(new NormalRecord().name("名前2").value(-54.321));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
        
    }
    
    /**
     * 書き込むのテスト -  開始位置の指定のテスト - アドレス指定の書式が不正
     * @throws Exception
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_save_hr_startedPosition_errors2() throws Exception {
        
        // テストデータの作成
        StartedPositionError2Sheet outSheet = new StartedPositionError2Sheet();
        
        outSheet.add(new NormalRecord().name("名前1").value(12.345));
        outSheet.add(new NormalRecord().name("名前2").value(-54.321));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
        
    }
    
    /**
     * 書き込むのテスト -  開始位置の指定のテスト - インデックス指定の書式が不正
     * @throws Exception
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_save_hr_startedPosition_errors3() throws Exception {
        
        // テストデータの作成
        StartedPositionError3Sheet outSheet = new StartedPositionError3Sheet();
        
        outSheet.add(new NormalRecord().name("名前1").value(12.345));
        outSheet.add(new NormalRecord().name("名前2").value(-54.321));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
            
            fail();
        }
        
    }
    
    /**
     * 書き込みのテスト - 終了位置の指定の確認
     */
    @Test
    public void test_save_hr_endPosition() throws Exception {
        
        // テストデータの作成
        EndPositionSheet outSheet = new EndPositionSheet();
        
        outSheet.add1(new NormalRecord().name("1-名前1").value(12.345));
        outSheet.add1(new NormalRecord().name("1-名前2").value(-54.321));
        
        outSheet.add2(new NormalRecord().name("2-名前1").value(12.345));
        outSheet.add2(new NormalRecord().name("2-名前2").value(-54.321));
        
        outSheet.add3(new NormalRecord().name("3-名前1").value(12.345));
        outSheet.add3(new NormalRecord().name("3-名前2").value(-54.321));
        
        outSheet.add4(new NormalRecord().name("4-名前1").value(12.345));
        outSheet.add4(new NormalRecord().name("4-名前2").value(-54.321));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(EndPositionSheet.class);
            
            EndPositionSheet sheet = mapper.load(in, EndPositionSheet.class, errors);
            
            if(sheet.normalRecords1 != null) {
                assertThat(sheet.normalRecords1, hasSize(outSheet.normalRecords1.size()));
                
                for(int i=0; i < sheet.normalRecords1.size(); i++) {
                    assertRecord(sheet.normalRecords1.get(i), outSheet.normalRecords1.get(i), errors);
                }
                
            }
            
            if(sheet.normalRecords2 != null) {
                assertThat(sheet.normalRecords2, hasSize(outSheet.normalRecords2.size()));
                
                for(int i=0; i < sheet.normalRecords2.size(); i++) {
                    assertRecord(sheet.normalRecords2.get(i), outSheet.normalRecords2.get(i), errors);
                }
                
            }
            
            if(sheet.normalRecords3 != null) {
                assertThat(sheet.normalRecords3, hasSize(outSheet.normalRecords3.size()));
                
                for(int i=0; i < sheet.normalRecords3.size(); i++) {
                    assertRecord(sheet.normalRecords3.get(i), outSheet.normalRecords3.get(i), errors);
                }
                
            }
            
            if(sheet.normalRecords4 != null) {
                assertThat(sheet.normalRecords4, hasSize(outSheet.normalRecords4.size()));
                
                for(int i=0; i < sheet.normalRecords4.size(); i++) {
                    assertRecord(sheet.normalRecords4.get(i), outSheet.normalRecords4.get(i), errors);
                }
                
            }
            
        }
    }
    
    /**
     * 書き込みのテスト - カラム設定のテスト
     * @throws Exception
     */
    @Test
    public void test_save_hr_columnSetting1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        test_save_hr_columnSetting(mapper);
    }
    
    /**
     * 書き込みのテスト - カラム設定のテスト
     * - 結合を有効
     * @throws Exception
     */
    @Test
    public void test_save_hr_columnSetting2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true)
            .setMergeCellOnSave(true);
        
        test_save_hr_columnSetting(mapper);
    }
    
    /**
     * 書き込みのテスト - カラム設定のテスト
     */
    private void test_save_hr_columnSetting(final XlsMapper mapper) throws Exception {
        
        // テストデータの作成
        final ColumnSettingSheet outSheet = new ColumnSettingSheet();
        
        // 結合セルの作成
        outSheet.addMerged(new MergedRecord()
                .category(Category.Info).description("説明1"));
        
        outSheet.addMerged(new MergedRecord()
                .category(Category.Info).description("説明2"));
        
        outSheet.addMerged(new MergedRecord()
                .category(Category.Warn).description("説明2"));
        
        outSheet.addMerged(new MergedRecord()
                .category(Category.Warn).description("説明2"));
        
        outSheet.addMerged(new MergedRecord()
                .category(Category.Error).description("説明3"));
        
        outSheet.addMerged(new MergedRecord()
                .category(Category.Info));
        
        outSheet.addMerged(new MergedRecord()
                .category(Category.Info));
        
        // 見出しが結合
        outSheet.addHeaderMerged(new HeaderMergedRecord()
                .name("山田太郎").mail("taro.yamada@example.com").tel("0000-1111-2222"));
        
        outSheet.addHeaderMerged(new HeaderMergedRecord()
                .name("鈴木次郎").mail("jiro.suzuki@example.com").tel("0000-3333-4444"));
        
        // オプションのセル（セルがある）
        outSheet.addOptional1(new OptionalRecord()
                .name("名前1").value(12.345).comment("コメント1"));
        outSheet.addOptional1(new OptionalRecord()
                .name("名前2").value(-12.0).comment("コメント2"));
        
        // オプションのセル（セルがない）
        outSheet.addOptional2(new OptionalRecord()
                .name("名前1").value(12.345).comment("コメント1"));
        outSheet.addOptional2(new OptionalRecord()
                .name("名前2").value(-12.0).comment("コメント2"));
        
        // Converterがある
        outSheet.addConverted(new ConvertedRecord()
                .name("山田太郎").birthday(toUtilDate(toTimestamp("1981-02-01 00:00:00.000"))).age(14));
        
        outSheet.addConverted(new ConvertedRecord()
                .name("  鈴木次郎  ").birthday(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
        
        // ファイルへの書き込み
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(ColumnSettingSheet.class);
            
            ColumnSettingSheet sheet = mapper.load(in, ColumnSettingSheet.class, errors);
            
            if(sheet.mergedRecords != null) {
                assertThat(sheet.mergedRecords, hasSize(outSheet.mergedRecords.size()));
                
                for(int i=0; i < sheet.mergedRecords.size(); i++) {
                    assertRecord(sheet.mergedRecords.get(i), outSheet.mergedRecords.get(i), errors);
                }
                
            }
            
            if(sheet.headerMergedRecords != null) {
                assertThat(sheet.headerMergedRecords, hasSize(outSheet.headerMergedRecords.size()));
                
                for(int i=0; i < sheet.headerMergedRecords.size(); i++) {
                    assertRecord(sheet.headerMergedRecords.get(i), outSheet.headerMergedRecords.get(i), errors);
                }
                
            }
            
            if(sheet.optionalRecords1 != null) {
                assertThat(sheet.optionalRecords1, hasSize(outSheet.optionalRecords1.size()));
                
                for(int i=0; i < sheet.optionalRecords1.size(); i++) {
                    assertRecord(sheet.optionalRecords1.get(i), outSheet.optionalRecords1.get(i), errors, true);
                }
                
            }
            
            if(sheet.optionalRecords2 != null) {
                assertThat(sheet.optionalRecords2, hasSize(outSheet.optionalRecords2.size()));
                
                for(int i=0; i < sheet.optionalRecords2.size(); i++) {
                    assertRecord(sheet.optionalRecords2.get(i), outSheet.optionalRecords2.get(i), errors, false);
                }
                
            }
            
            if(sheet.convertedRecord != null) {
                assertThat(sheet.convertedRecord, hasSize(outSheet.convertedRecord.size()));
                
                for(int i=0; i < sheet.convertedRecord.size(); i++) {
                    assertRecord(sheet.convertedRecord.get(i), outSheet.convertedRecord.get(i), errors);
                }
                
            }
            
        }
        
    }
    
    /**
     * 書き込みテスト - Map形式のカラムの設定テスト
     */
    @Test
    public void test_save_hr_mapColumnSetting() throws Exception {
        
        // テストデータの作成
        final MapColumnSettingSheet outSheet = new MapColumnSettingSheet();
        
        // マップカラム（文字列）
        outSheet.add(new MapRecord()
                    .name("山田太郎")
                    .addDateAttended("4月1日", "出席").addDateAttended("4月2日", "出席"));
        
        outSheet.add(new MapRecord()
                .name("鈴木次郎")
                .addDateAttended("4月1日", "欠席").addDateAttended("4月2日", "-").addDateAttended("4月3日", "出席"));
        
        // マップカラム（Converterあり）
        outSheet.add(new MapConvertedRecord()
                    .name("山田太郎")
                    .addDateAttended("4月1日", true).addDateAttended("4月2日", true));
        
        outSheet.add(new MapConvertedRecord()
                .name("鈴木次郎")
                .addDateAttended("4月1日", false).addDateAttended("4月2日", false).addDateAttended("4月3日", true));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(MapColumnSettingSheet.class);
            
            MapColumnSettingSheet sheet = mapper.load(in, MapColumnSettingSheet.class, errors);
            
            if(sheet.mapRecords1 != null) {
                assertThat(sheet.mapRecords1, hasSize(outSheet.mapRecords1.size()));
                
                for(int i=0; i < sheet.mapRecords1.size(); i++) {
                    assertRecord(sheet.mapRecords1.get(i), outSheet.mapRecords1.get(i), errors);
                }
                
            }
            
            if(sheet.mapRecords2 != null) {
                assertThat(sheet.mapRecords2, hasSize(outSheet.mapRecords2.size()));
                
                for(int i=0; i < sheet.mapRecords2.size(); i++) {
                    assertRecord(sheet.mapRecords2.get(i), outSheet.mapRecords2.get(i), errors);
                }
                
            }
            
        }
    }
    
    /**
     * 書き込みのテスト - 様々なレコード
     * ・空のレコードのスキップ確認
     * ・配列型の確認
     */
    @Test
    public void test_save_hr_recordSetting() throws Exception {
        
        // テストデータの作成
        final RecodSettingSheet outSheet = new RecodSettingSheet();
        
        // 名簿（リスト）
        outSheet.addList(new EmptySkipRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1989-01-02 00:00:00.000"))));
        outSheet.addList(new EmptySkipRecord().name("鈴木次郎").birthday(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
        outSheet.addList(new EmptySkipRecord());
        outSheet.addList(new EmptySkipRecord().name("林三郎").birthday(toUtilDate(toTimestamp("1992-04-14 00:00:00.000"))));
        outSheet.addList(new EmptySkipRecord());
        
        // 名簿（配列）
        outSheet.addArray(new EmptySkipRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1989-01-02 00:00:00.000"))));
        outSheet.addArray(new EmptySkipRecord().name("鈴木次郎").birthday(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
        outSheet.addArray(new EmptySkipRecord());
        outSheet.addArray(new EmptySkipRecord().name("林三郎").birthday(toUtilDate(toTimestamp("1992-04-14 00:00:00.000"))));
        outSheet.addArray(new EmptySkipRecord());
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(RecodSettingSheet.class);
            
            RecodSettingSheet sheet = mapper.load(in, RecodSettingSheet.class, errors);
            
            if(sheet.skipList != null) {
                int emptyRecordCount = 0;
                for(int i=0; i < outSheet.skipList.size(); i++) {
                    if(outSheet.skipList.get(i).isEmpty()) {
                        emptyRecordCount++;
                        continue;
                    }
                    assertRecord(sheet.skipList.get(i - emptyRecordCount), outSheet.skipList.get(i), errors);
                }
                
                assertThat(sheet.skipList, hasSize(outSheet.skipList.size() - emptyRecordCount));
                
            }
            
            if(sheet.skipArray != null) {
                int emptyRecordCount = 0;
                for(int i=0; i < outSheet.skipArray.length; i++) {
                    if(outSheet.skipArray[i].isEmpty()) {
                        emptyRecordCount++;
                        continue;
                    }
                    assertRecord(sheet.skipArray[i - emptyRecordCount], outSheet.skipArray[i], errors);
                }
                
                assertThat(sheet.skipArray, arrayWithSize(outSheet.skipArray.length - emptyRecordCount));
                
            }
            
        }
    }
    
    /**
     * 書き込みのテスト - 余分なレコード／足りないレコードの制御
     */
    @Test
    public void test_save_hr_over_remained_record() throws Exception {
        
        // テストデータの作成
        final RemainedOverSheet outSheet = new RemainedOverSheet();
        
        // 足りないレコード（Break）
        outSheet.addOverBreak(new RemainedOverRecord().name("山田太郎").addDateAttended("A", "○").addDateAttended("B", "×"));
        outSheet.addOverBreak(new RemainedOverRecord().name("鈴木次郎").addDateAttended("A", "-").addDateAttended("B", "-"));
        outSheet.addOverBreak(new RemainedOverRecord().name("山本花子").addDateAttended("A", "×").addDateAttended("B", "レ"));
        
        // 足りないレコード（Inert）
        outSheet.addOverInsert(new RemainedOverRecord().name("山田太郎").addDateAttended("A", "○").addDateAttended("B", "×"));
        outSheet.addOverInsert(new RemainedOverRecord().name("鈴木次郎").addDateAttended("A", "-").addDateAttended("B", "-"));
        outSheet.addOverInsert(new RemainedOverRecord().name("山本花子").addDateAttended("A", "×").addDateAttended("B", "レ"));
        
        // 足りないレコード（Copy）
        outSheet.addOverCopy(new RemainedOverRecord().name("山田太郎").addDateAttended("A", "○").addDateAttended("B", "×"));
        outSheet.addOverCopy(new RemainedOverRecord().name("鈴木次郎").addDateAttended("A", "-").addDateAttended("B", "-"));
        outSheet.addOverCopy(new RemainedOverRecord().name("山本花子").addDateAttended("A", "×").addDateAttended("B", "レ"));
        
        // 余分なレコード（None）
        outSheet.addRemainedNone(new RemainedOverRecord().name("山田太郎").addDateAttended("A", "○").addDateAttended("B", "×"));
        outSheet.addRemainedNone(new RemainedOverRecord().name("鈴木次郎").addDateAttended("A", "-").addDateAttended("B", "-"));
        
        // 余分なレコード（Clear）
        outSheet.addRemainedClear(new RemainedOverRecord().name("山田太郎").addDateAttended("A", "○").addDateAttended("B", "×"));
        outSheet.addRemainedClear(new RemainedOverRecord().name("鈴木次郎").addDateAttended("A", "-").addDateAttended("B", "-"));
        
        // 余分なレコード（Delete）
        outSheet.addRemainedDelete1(new RemainedOverRecord().name("山田太郎").addDateAttended("A", "○").addDateAttended("B", "×"));
        outSheet.addRemainedDelete1(new RemainedOverRecord().name("鈴木次郎").addDateAttended("A", "-").addDateAttended("B", "-"));
        
        // 余分なレコード（Delete）(データなし)
        outSheet.remainedDeleteRecrods2 = new ArrayList<>();
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true)
            .setCorrectCellDataValidationOnSave(true)
            .setCorrectNameRangeOnSave(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(RemainedOverSheet.class);
            
            RemainedOverSheet sheet = mapper.load(in, RemainedOverSheet.class, errors);
            
            if(sheet.overBreakRecrods != null) {
                assertThat(sheet.overBreakRecrods, hasSize(2));
                
                for(int i=0; i < sheet.overBreakRecrods.size(); i++) {
                    assertRecord(sheet.overBreakRecrods.get(i), outSheet.overBreakRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.overInsertRecrods != null) {
                assertThat(sheet.overInsertRecrods, hasSize(outSheet.overInsertRecrods.size()));
                
                for(int i=0; i < sheet.overInsertRecrods.size(); i++) {
                    assertRecord(sheet.overInsertRecrods.get(i), outSheet.overInsertRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.overCopyRecrods != null) {
                assertThat(sheet.overCopyRecrods, hasSize(outSheet.overCopyRecrods.size()));
                
                for(int i=0; i < sheet.overCopyRecrods.size(); i++) {
                    assertRecord(sheet.overCopyRecrods.get(i), outSheet.overCopyRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.remainedNoneRecrods != null) {
                assertThat(sheet.remainedNoneRecrods, hasSize(outSheet.remainedNoneRecrods.size()));
                
                for(int i=0; i < sheet.remainedNoneRecrods.size(); i++) {
                    assertRecord(sheet.remainedNoneRecrods.get(i), outSheet.remainedNoneRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.remainedClearRecrods != null) {
                assertThat(sheet.remainedClearRecrods, hasSize(outSheet.remainedClearRecrods.size()));
                
                for(int i=0; i < sheet.remainedClearRecrods.size(); i++) {
                    assertRecord(sheet.remainedClearRecrods.get(i), outSheet.remainedClearRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.remainedDeleteRecrods1 != null) {
                assertThat(sheet.remainedDeleteRecrods1, hasSize(outSheet.remainedDeleteRecrods1.size()));
                
                for(int i=0; i < sheet.remainedDeleteRecrods1.size(); i++) {
                    assertRecord(sheet.remainedDeleteRecrods1.get(i), outSheet.remainedDeleteRecrods1.get(i), errors);
                }
                
            }
            
            if(sheet.remainedDeleteRecrods2 != null) {
                assertThat(sheet.remainedDeleteRecrods2, hasSize(outSheet.remainedDeleteRecrods2.size()));
                
                for(int i=0; i < sheet.remainedDeleteRecrods2.size(); i++) {
                    assertRecord(sheet.remainedDeleteRecrods2.get(i), outSheet.remainedDeleteRecrods2.get(i), errors);
                }
                
            }
            
        }
    }
    
    /**
     * 書き込みのテスト - 入力規則
     */
    @Test
    public void test_save_hr_option_dataValidation() throws Exception {
        
        // テストデータの作成
        final ValidationRuleSheet outSheet = new ValidationRuleSheet();
        
        // 入力規則（レコードの挿入）
        outSheet.addInsert(new DataValidationRecord().selectRule(true).refRule("ユーザ管理").addCategory("A", true).addCategory("B", false));
        outSheet.addInsert(new DataValidationRecord().selectRule(false).refRule("ファイルアップロード").addCategory("A", null).addCategory("B", true));
        outSheet.addInsert(new DataValidationRecord());
        
        // 名前の定義
        outSheet.add(new NameDefRecord().functionName("ユーザ管理"));
        outSheet.add(new NameDefRecord().functionName("ファイルアップロード"));
        outSheet.add(new NameDefRecord().functionName("データ管理"));
        outSheet.add(new NameDefRecord().functionName("帳票出力"));
        outSheet.add(new NameDefRecord());
        
        // 入力規則（レコードの削除）
        outSheet.addDelete(new DataValidationRecord().selectRule(true).refRule("作成").addCategory("A", true).addCategory("B", true));
        outSheet.addDelete(new DataValidationRecord().selectRule(false).refRule("削除").addCategory("A", null).addCategory("B", false));
        outSheet.addDelete(new DataValidationRecord());
        
        // 入力規則（レコードの削除）（データなし）
        outSheet.nonDeleteValidationRecrods = new ArrayList<>();
        
        // 入力規則（レコードのコピー）
        outSheet.addCopy(new DataValidationRecord().selectRule(true).refRule("参照").addCategory("A", null).addCategory("B", null));
        outSheet.addCopy(new DataValidationRecord().selectRule(false).refRule("更新").addCategory("A", true).addCategory("B", true));
        outSheet.addCopy(new DataValidationRecord());
        
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true)
            .setCorrectCellDataValidationOnSave(true)
            .setCorrectNameRangeOnSave(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(ValidationRuleSheet.class);
            
            ValidationRuleSheet sheet = mapper.load(in, ValidationRuleSheet.class, errors);
            
            if(sheet.insertValidationRecrods != null) {
                assertThat(sheet.insertValidationRecrods, hasSize(outSheet.insertValidationRecrods.size()-1));
                
                for(int i=0; i < sheet.insertValidationRecrods.size(); i++) {
                    assertRecord(sheet.insertValidationRecrods.get(i), outSheet.insertValidationRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.nameRecords != null) {
                assertThat(sheet.nameRecords, hasSize(outSheet.nameRecords.size()));
                
                for(int i=0; i < sheet.nameRecords.size(); i++) {
                    assertRecord(sheet.nameRecords.get(i), outSheet.nameRecords.get(i), errors);
                }
                
            }
            
            if(sheet.deleteValidationRecrods != null) {
                assertThat(sheet.deleteValidationRecrods, hasSize(1));
                
                for(int i=0; i < sheet.deleteValidationRecrods.size(); i++) {
                    assertRecord(sheet.deleteValidationRecrods.get(i), outSheet.deleteValidationRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.nonDeleteValidationRecrods != null) {
                assertThat(sheet.nonDeleteValidationRecrods, hasSize(outSheet.nonDeleteValidationRecrods.size()));
                
                for(int i=0; i < sheet.nonDeleteValidationRecrods.size(); i++) {
                    assertRecord(sheet.nonDeleteValidationRecrods.get(i), outSheet.nonDeleteValidationRecrods.get(i), errors);
                }
                
            }
            
            if(sheet.copyValidationRecrods != null) {
                assertThat(sheet.copyValidationRecrods, hasSize(outSheet.copyValidationRecrods.size()-1));
                
                for(int i=0; i < sheet.copyValidationRecrods.size(); i++) {
                    assertRecord(sheet.copyValidationRecrods.get(i), outSheet.copyValidationRecrods.get(i), errors);
                }
                
            }
            
        }
        
    }
    
    /**
     * 書き込みのテスト - コメント
     */
    @Test
    public void test_save_hr_option_comment() throws Exception {
        
        // テストデータの作成
        final CommentSheet outSheet = new CommentSheet();
        
        // 文字装飾のコメント
        outSheet.value1 = "今日はいい天気ですね。\n明日も晴れるといいですね。";
        
        // コメントがある表（行の追加）
        outSheet.addInsert(new CommentRecord().name("リンゴ").value(100.0));
        outSheet.addInsert(new CommentRecord().name("みかん").value(200.0));
        outSheet.addInsert(new CommentRecord().name("バナナ").value(300.0));
        
        // コメントがある表（行の追加）
        outSheet.addDelete(new CommentRecord().name("きゅうり").value(10.0));
        outSheet.addDelete(new CommentRecord().name("トマト").value(20.0));
        outSheet.addDelete(new CommentRecord().name("キャベツ").value(30.0));
        
        // コメントがある表（行のコピー）
        outSheet.addCopy(new CommentRecord().name("ジュース").value(1000.0));
        outSheet.addCopy(new CommentRecord().name("日本酒").value(2000.0));
        outSheet.addCopy(new CommentRecord().name("梅酒").value(3000.0));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true)
            .setCorrectCellCommentOnSave(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(CommentSheet.class);
            
            CommentSheet sheet = mapper.load(in, CommentSheet.class, errors);
            
            if(sheet.insertRecords != null) {
                assertThat(sheet.insertRecords, hasSize(outSheet.insertRecords.size()));
                
                for(int i=0; i < sheet.insertRecords.size(); i++) {
                    assertRecord(sheet.insertRecords.get(i), outSheet.insertRecords.get(i), errors);
                }
                
            }
            
            if(sheet.deleteRecords != null) {
                assertThat(sheet.deleteRecords, hasSize(outSheet.deleteRecords.size()));
                
                for(int i=0; i < sheet.deleteRecords.size(); i++) {
                    assertRecord(sheet.deleteRecords.get(i), outSheet.deleteRecords.get(i), errors);
                }
                
            }
            
            if(sheet.copyRecords != null) {
                assertThat(sheet.copyRecords, hasSize(outSheet.copyRecords.size()));
                
                for(int i=0; i < sheet.copyRecords.size(); i++) {
                    assertRecord(sheet.copyRecords.get(i), outSheet.copyRecords.get(i), errors);
                }
                
            }
            
        }
        
    }
    
    /**
     * 書き込みのテスト - メソッドにアノテーションを定義
     * @since 1.0
     */
    @Test
    public void test_save_hr_methodAnno() throws Exception {
        
        // テストデータの作成
        final MethodAnnoSheet outSheet = new MethodAnnoSheet();
        
        // 名簿
        outSheet.add(new MethodAnnoRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1989-01-02 00:00:00.000"))));
        outSheet.add(new MethodAnnoRecord().name("鈴木次郎").birthday(toUtilDate(toTimestamp("1990-02-28 00:00:00.000"))));
        outSheet.add(new MethodAnnoRecord());
        outSheet.add(new MethodAnnoRecord().name("林三郎").birthday(toUtilDate(toTimestamp("1992-04-14 00:00:00.000"))));
        outSheet.add(new MethodAnnoRecord());
        
        // 出欠
        outSheet.add(new MethodAnnoMapRecord()
                .name("山田太郎")
                .addDateAttended("4月1日", true).addDateAttended("4月2日", true));
        
        outSheet.add(new MethodAnnoMapRecord()
                .name("鈴木次郎")
                .addDateAttended("4月1日", false).addDateAttended("4月2日", false).addDateAttended("4月3日", true));
        
        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        File outFile = new File("src/test/out/anno_HorizonalRecords_out.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/anno_HorizonalRecords_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            
            SheetBindingErrors errors = new SheetBindingErrors(MethodAnnoSheet.class);
            
            MethodAnnoSheet sheet = mapper.load(in, MethodAnnoSheet.class, errors);
            
            if(sheet.records != null) {
                int emptyRecordCount = 0;
                for(int i=0; i < outSheet.records.size(); i++) {
                    if(outSheet.records.get(i).isEmpty()) {
                        emptyRecordCount++;
                        continue;
                    }
                    assertRecord(sheet.records.get(i - emptyRecordCount), outSheet.records.get(i), errors);
                }
                
                assertThat(sheet.records, hasSize(outSheet.records.size() - emptyRecordCount));
                
            }
            
            if(sheet.mapRecords != null) {
                int emptyRecordCount = 0;
                for(int i=0; i < outSheet.mapRecords.length; i++) {
                    if(outSheet.mapRecords[i].isEmpty()) {
                        emptyRecordCount++;
                        continue;
                    }
                    assertRecord(sheet.mapRecords[i - emptyRecordCount], outSheet.mapRecords[i], errors);
                }
                
                assertThat(sheet.mapRecords, arrayWithSize(outSheet.mapRecords.length - emptyRecordCount));
                
            }
            
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final NormalRecord inRecord, final NormalRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.value, is(outRecord.value));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final MergedRecord inRecord, final MergedRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.category, is(outRecord.category));
        assertThat(inRecord.description, is(outRecord.description));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final HeaderMergedRecord inRecord, final HeaderMergedRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.mail, is(outRecord.mail));
        assertThat(inRecord.tel, is(outRecord.tel));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     * @param hasCell オプションのセルを持つかどうか。
     */
    private void assertRecord(final OptionalRecord inRecord, final OptionalRecord outRecord, final SheetBindingErrors errors, boolean hasCell) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.value, is(outRecord.value));
        
        if(hasCell) {
            assertThat(inRecord.comment, is(outRecord.comment));
        } else {
            assertThat(inRecord.comment, is(nullValue()));
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final ConvertedRecord inRecord, final ConvertedRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(trim(outRecord.name)));
        assertThat(inRecord.birthday, is(outRecord.birthday));
        assertThat(inRecord.age, is(outRecord.age));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final MapRecord inRecord, final MapRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        if(inRecord.no == 1) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.name, is(trim(outRecord.name)));
            
            Map<String, String> expected = new LinkedHashMap<>();
            expected.put("4月1日", "出席");
            expected.put("4月2日", "出席");
            expected.put("4月3日", null);
            
            assertThat(inRecord.dateAttended, is(expected));
            
        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.name, is(trim(outRecord.name)));
            assertThat(inRecord.dateAttended, is(outRecord.dateAttended));
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final MapConvertedRecord inRecord, final MapConvertedRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        if(inRecord.no == 1) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.name, is(trim(outRecord.name)));
            
            Map<String, Boolean> expected = new LinkedHashMap<>();
            expected.put("4月1日", true);
            expected.put("4月2日", true);
            expected.put("4月3日", null);
            
            assertThat(inRecord.dateAttended, is(expected));
            
        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.name, is(trim(outRecord.name)));
            assertThat(inRecord.dateAttended, is(outRecord.dateAttended));
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final EmptySkipRecord inRecord, final EmptySkipRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(trim(outRecord.name)));
        assertThat(inRecord.birthday, is(outRecord.birthday));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final RemainedOverRecord inRecord, final RemainedOverRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(trim(outRecord.name)));
        assertThat(inRecord.dateAttended, is(outRecord.dateAttended));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final DataValidationRecord inRecord, final DataValidationRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        if(inRecord.no == 3) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.selectRule, is(false));
            assertThat(inRecord.refRule, is(outRecord.refRule));
            
        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.selectRule, is(outRecord.selectRule));
            assertThat(inRecord.refRule, is(outRecord.refRule));
        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final NameDefRecord inRecord, final NameDefRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.functionName, is(outRecord.functionName));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final CommentRecord inRecord, final CommentRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.value, is(outRecord.value));
        assertThat(inRecord.comment, is(outRecord.comment));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final MethodAnnoRecord inRecord, final MethodAnnoRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(trim(outRecord.name)));
        assertThat(inRecord.birthday, is(outRecord.birthday));
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final MethodAnnoMapRecord inRecord, final MethodAnnoMapRecord outRecord, final SheetBindingErrors errors) {
        
        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);
        
        if(inRecord.no == 1) {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.name, is(trim(outRecord.name)));
            
            Map<String, Boolean> expected = new LinkedHashMap<>();
            expected.put("4月1日", true);
            expected.put("4月2日", true);
            expected.put("4月3日", null);
            
            assertThat(inRecord.dateAttended, is(expected));
            
        } else {
            assertThat(inRecord.no, is(outRecord.no));
            assertThat(inRecord.name, is(trim(outRecord.name)));
            assertThat(inRecord.dateAttended, is(outRecord.dateAttended));
        }
    }
    
    /**
     * 開始位置の指定
     *
     */
    @XlsSheet(name="開始位置の指定")
    private static class StartedPositionSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="○×一覧", skipEmptyRecord=true)
        private List<NormalRecord> normalRecords1;
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(headerAddress="B9", skipEmptyRecord=true)
        private List<NormalRecord> normalRecords2;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(headerColumn=2, headerRow=13, skipEmptyRecord=true)
        private List<NormalRecord> normalRecords3;
        
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="◆△一覧", bottom=2, skipEmptyRecord=true)
        private List<NormalRecord> normalRecords4;
        
        @XlsHint(order=5)
        @XlsHorizontalRecords(tableLabel="存在しない", optional=true, skipEmptyRecord=true)
        private List<NormalRecord> normalRecords5;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionSheet add1(NormalRecord record) {
            if(normalRecords1 == null) {
                this.normalRecords1 = new ArrayList<>();
            }
            
            this.normalRecords1.add(record);
            record.no(normalRecords1.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionSheet add2(NormalRecord record) {
            if(normalRecords2 == null) {
                this.normalRecords2 = new ArrayList<>();
            }
            
            this.normalRecords2.add(record);
            record.no(normalRecords2.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionSheet add3(NormalRecord record) {
            if(normalRecords3 == null) {
                this.normalRecords3 = new ArrayList<>();
            }
            
            this.normalRecords3.add(record);
            record.no(normalRecords3.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionSheet add4(NormalRecord record) {
            if(normalRecords4 == null) {
                this.normalRecords4 = new ArrayList<>();
            }
            
            this.normalRecords4.add(record);
            record.no(normalRecords4.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionSheet add5(NormalRecord record) {
            if(normalRecords5 == null) {
                this.normalRecords5 = new ArrayList<>();
            }
            
            this.normalRecords5.add(record);
            record.no(normalRecords5.size());
            
            return this;
        }
        
    }
    
    /**
     * 開始位置の指定 - 見出し指定で見つからない場合
     *
     */
    @XlsSheet(name="開始位置の指定")
    private static class StartedPositionError1Sheet {
        
        @XlsHorizontalRecords(tableLabel="存在しない", optional=false)
        private List<NormalRecord> normalRecords5;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionError1Sheet add(NormalRecord record) {
            if(normalRecords5 == null) {
                this.normalRecords5 = new ArrayList<>();
            }
            
            this.normalRecords5.add(record);
            record.no(normalRecords5.size());
            
            return this;
        }
    }
    
    /**
     * 開始位置の指定 - アドレス指定でフォーマット不正な場合
     *
     */
    @XlsSheet(name="開始位置の指定")
    private static class StartedPositionError2Sheet {
        
        @XlsHorizontalRecords(headerAddress="_B9")
        private List<NormalRecord> normalRecords2;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionError2Sheet add(NormalRecord record) {
            if(normalRecords2 == null) {
                this.normalRecords2 = new ArrayList<>();
            }
            
            this.normalRecords2.add(record);
            record.no(normalRecords2.size());
            
            return this;
        }
    }
    
    /**
     * 開始位置の指定 - アドレス指定でインデックスが不正な場合
     *
     */
    @XlsSheet(name="開始位置の指定")
    private static class StartedPositionError3Sheet {
        
        @XlsHorizontalRecords(headerColumn=-1, headerRow=-1)
        private List<NormalRecord> normalRecords3;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public StartedPositionError3Sheet add(NormalRecord record) {
            if(normalRecords3 == null) {
                this.normalRecords3 = new ArrayList<>();
            }
            
            this.normalRecords3.add(record);
            record.no(normalRecords3.size());
            
            return this;
        }
    }
    
    /**
     * 表の終了位置の指定
     *
     */
    @XlsSheet(name="終了位置の指定")
    private static class EndPositionSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="終端レコードの指定（Empty）", terminal=RecordTerminal.Empty)
        private List<NormalRecord> normalRecords1;
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="終端レコードの指定（Border）", terminal=RecordTerminal.Border)
        private List<NormalRecord> normalRecords2;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="終端セルの指定", terminal=RecordTerminal.Border, terminateLabel="合計")
        private List<NormalRecord> normalRecords3;
        
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="見出しセルの個数指定", terminal=RecordTerminal.Border, headerLimit=3)
        private List<NormalRecord> normalRecords4;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public EndPositionSheet add1(NormalRecord record) {
            if(normalRecords1 == null) {
                this.normalRecords1 = new ArrayList<>();
            }
            
            this.normalRecords1.add(record);
            record.no(normalRecords1.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public EndPositionSheet add2(NormalRecord record) {
            if(normalRecords2 == null) {
                this.normalRecords2 = new ArrayList<>();
            }
            
            this.normalRecords2.add(record);
            record.no(normalRecords2.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public EndPositionSheet add3(NormalRecord record) {
            if(normalRecords3 == null) {
                this.normalRecords3 = new ArrayList<>();
            }
            
            this.normalRecords3.add(record);
            record.no(normalRecords3.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public EndPositionSheet add4(NormalRecord record) {
            if(normalRecords4 == null) {
                this.normalRecords4 = new ArrayList<>();
            }
            
            this.normalRecords4.add(record);
            record.no(normalRecords4.size());
            
            return this;
        }
    }
    
    private static class NormalRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="名称")
        private String name;
        
        @XlsColumn(columnName="値")
        private Double value;
        
        public NormalRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public NormalRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public NormalRecord value(Double value) {
            this.value = value;
            return this;
        }
        
    }
    
    /**
     * カラムの様々な指定のシート
     */
    @XlsSheet(name="カラムの設定")
    private static class ColumnSettingSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="結合セル", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<MergedRecord> mergedRecords;
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="見出しが結合", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<HeaderMergedRecord> headerMergedRecords;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="オプションのセル（セルがある）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<OptionalRecord> optionalRecords1;
        
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="オプションのセル（セルがない）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<OptionalRecord> optionalRecords2;
        
        @XlsHint(order=5)
        @XlsHorizontalRecords(tableLabel="Converterがある", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<ConvertedRecord> convertedRecord;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ColumnSettingSheet addMerged(MergedRecord record) {
            if(mergedRecords == null) {
                this.mergedRecords = new ArrayList<>();
            }
            
            this.mergedRecords.add(record);
            record.no(mergedRecords.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ColumnSettingSheet addHeaderMerged(HeaderMergedRecord record) {
            if(headerMergedRecords == null) {
                this.headerMergedRecords = new ArrayList<>();
            }
            
            this.headerMergedRecords.add(record);
            record.no(headerMergedRecords.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ColumnSettingSheet addOptional1(OptionalRecord record) {
            if(optionalRecords1 == null) {
                this.optionalRecords1 = new ArrayList<>();
            }
            
            this.optionalRecords1.add(record);
            record.no(optionalRecords1.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ColumnSettingSheet addOptional2(OptionalRecord record) {
            if(optionalRecords2 == null) {
                this.optionalRecords2 = new ArrayList<>();
            }
            
            this.optionalRecords2.add(record);
            record.no(optionalRecords2.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ColumnSettingSheet addConverted(ConvertedRecord record) {
            if(convertedRecord == null) {
                this.convertedRecord = new ArrayList<>();
            }
            
            this.convertedRecord.add(record);
            record.no(convertedRecord.size());
            
            return this;
        }
        
    }
    
    /**
     * 結合したセルのレコード
     */
    private static class MergedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="分類", merged=true)
        private Category category;
        
        @XlsColumn(columnName="説明", merged=true)
        private String description;
        
        public MergedRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public MergedRecord category(Category category) {
            this.category = category;
            return this;
        }
        
        public MergedRecord description(String description) {
            this.description = description;
            return this;
        }
        
    }
    
    private enum Category {
        Info, Warn, Error
        ;
    }
    
    /**
     * 見出し用セルが結合されている場合
     */
    private static class HeaderMergedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsColumn(columnName="連絡先")
        private String mail;
        
        @XlsColumn(columnName="連絡先", headerMerged=1)
        private String tel;
        
        public HeaderMergedRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public HeaderMergedRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public HeaderMergedRecord mail(String mail) {
            this.mail = mail;
            return this;
        }
        
        public HeaderMergedRecord tel(String tel) {
            this.tel = tel;
            return this;
        }
        
    }
    
    /**
     * オプションのセル
     */
    private static class OptionalRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="名称")
        private String name;
        
        @XlsColumn(columnName="値")
        private Double value;
        
        @XlsColumn(columnName="備考", optional=true)
        private String comment;
        
        public OptionalRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public OptionalRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public OptionalRecord value(Double value) {
            this.value = value;
            return this;
        }
        
        public OptionalRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
    }
    
    /**
     * Converterの設定がある
     *
     */
    private static class ConvertedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsConverter(trim=true)
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsDateConverter(javaPattern="yyyy年M月d日", lenient=true)
        @XlsColumn(columnName="生年月日")
        private Date birthday;
        
        @XlsColumn(columnName="年齢")
        private int age;
        
        public ConvertedRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public ConvertedRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public ConvertedRecord birthday(Date birthday) {
            this.birthday = birthday;
            return this;
        }
        
        public ConvertedRecord age(int age) {
            this.age = age;
            return this;
        }
        
    }
    
    /**
     * {@link XlsMapColumns}を使用したシート
     *
     */
    @XlsSheet(name="マップカラムの設定")
    private static class MapColumnSettingSheet {
        
        @XlsHorizontalRecords(tableLabel="マップカラム（文字列）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<MapRecord> mapRecords1;
        
        @XlsHorizontalRecords(tableLabel="マップカラム（Converterあり）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<MapConvertedRecord> mapRecords2;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public MapColumnSettingSheet add(MapRecord record) {
            if(mapRecords1 == null) {
                this.mapRecords1 = new ArrayList<>();
            }
            
            this.mapRecords1.add(record);
            record.no(mapRecords1.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public MapColumnSettingSheet add(MapConvertedRecord record) {
            if(mapRecords2 == null) {
                this.mapRecords2 = new ArrayList<>();
            }
            
            this.mapRecords2.add(record);
            record.no(mapRecords2.size());
            
            return this;
        }
    }
    
    /**
     * マップのセル（文字列）
     */
    private static class MapRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsMapColumns(previousColumnName="氏名")
        private Map<String, String> dateAttended;
        
        public MapRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public MapRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public MapRecord dateAttended(Map<String, String> dateAttended) {
            this.dateAttended = dateAttended;
            return this;
        }
        
        public MapRecord addDateAttended(final String key, final String value) {
            if(dateAttended == null) {
                this.dateAttended = new LinkedHashMap<>();
            }
            
            this.dateAttended.put(key, value);
            
            return this;
        }
        
    }
    
    /**
     * マップのセル（Converterあり）
     */
    private static class MapConvertedRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsBooleanConverter(loadForTrue="出席", loadForFalse="欠席", saveAsTrue="出席", saveAsFalse="欠席")
        @XlsMapColumns(previousColumnName="氏名")
        private Map<String, Boolean> dateAttended;
        
        public MapConvertedRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public MapConvertedRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public MapConvertedRecord dateAttended(Map<String, Boolean> dateAttended) {
            this.dateAttended = dateAttended;
            return this;
        }
        
        public MapConvertedRecord addDateAttended(final String key, final Boolean value) {
            if(dateAttended == null) {
                this.dateAttended = new LinkedHashMap<>();
            }
            
            this.dateAttended.put(key, value);
            
            return this;
        }
    }
    
    /**
     * 様々なレコードの設定
     */
    @XlsSheet(name="レコードの設定")
    private static class RecodSettingSheet {
        
        /**
         * 空のレコードをスキップ（）
         */
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="名簿（リスト）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<EmptySkipRecord> skipList;
        
        /**
         * 配列
         */
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="名簿（配列）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private EmptySkipRecord[] skipArray;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RecodSettingSheet addList(EmptySkipRecord record) {
            if(skipList == null) {
                this.skipList = new ArrayList<>();
            }
            
            this.skipList.add(record);
            record.no(skipList.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RecodSettingSheet addArray(EmptySkipRecord record) {
            
            final List<EmptySkipRecord> list;
            if(skipArray == null) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>(Arrays.asList(skipArray));
            }
            
            list.add(record);
            record.no(list.size());
            
            this.skipArray = list.toArray(new EmptySkipRecord[list.size()]);
            
            return this;
        }
    }
    
    /**
     * 空のレコードをスキップ可能なレコード
     *
     */
    private static class EmptySkipRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsDateConverter(javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="生年月日")
        private Date birthday;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public EmptySkipRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public EmptySkipRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public EmptySkipRecord birthday(Date birthday) {
            this.birthday = birthday;
            return this;
        }
    }
    
    /**
     * 余分／不足なレコード制御用のシート
     *
     */
    @XlsSheet(name="余分なレコードの制御")
    private static class RemainedOverSheet {
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="足りないレコード（Break）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Break)
        private List<RemainedOverRecord> overBreakRecrods;
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="足りないレコード（Insert）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<RemainedOverRecord> overInsertRecrods;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="足りないレコード（Copy）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Copy)
        private List<RemainedOverRecord> overCopyRecrods;
        
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="余分なレコード（None）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                remainedRecord=RemainedRecordOperate.None)
        private List<RemainedOverRecord> remainedNoneRecrods;
        
        @XlsHint(order=5)
        @XlsHorizontalRecords(tableLabel="余分なレコード（Clear）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                remainedRecord=RemainedRecordOperate.Clear)
        private List<RemainedOverRecord> remainedClearRecrods;
        
        @XlsHint(order=6)
        @XlsHorizontalRecords(tableLabel="余分なレコード（Delete）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                remainedRecord=RemainedRecordOperate.Delete)
        private List<RemainedOverRecord> remainedDeleteRecrods1;
        
        @XlsHint(order=7)
        @XlsHorizontalRecords(tableLabel="余分なレコード（Delete）（データなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                remainedRecord=RemainedRecordOperate.Delete)
        private List<RemainedOverRecord> remainedDeleteRecrods2;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addOverBreak(RemainedOverRecord record) {
            if(overBreakRecrods == null) {
                this.overBreakRecrods = new ArrayList<>();
            }
            
            this.overBreakRecrods.add(record);
            record.no(overBreakRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addOverInsert(RemainedOverRecord record) {
            if(overInsertRecrods == null) {
                this.overInsertRecrods = new ArrayList<>();
            }
            
            this.overInsertRecrods.add(record);
            record.no(overInsertRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addOverCopy(RemainedOverRecord record) {
            if(overCopyRecrods == null) {
                this.overCopyRecrods = new ArrayList<>();
            }
            
            this.overCopyRecrods.add(record);
            record.no(overCopyRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addRemainedNone(RemainedOverRecord record) {
            if(remainedNoneRecrods == null) {
                this.remainedNoneRecrods = new ArrayList<>();
            }
            
            this.remainedNoneRecrods.add(record);
            record.no(remainedNoneRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addRemainedClear(RemainedOverRecord record) {
            if(remainedClearRecrods == null) {
                this.remainedClearRecrods = new ArrayList<>();
            }
            
            this.remainedClearRecrods.add(record);
            record.no(remainedClearRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addRemainedDelete1(RemainedOverRecord record) {
            if(remainedDeleteRecrods1 == null) {
                this.remainedDeleteRecrods1 = new ArrayList<>();
            }
            
            this.remainedDeleteRecrods1.add(record);
            record.no(remainedDeleteRecrods1.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public RemainedOverSheet addRemmainedDelete2(RemainedOverRecord record) {
            if(remainedDeleteRecrods2 == null) {
                this.remainedDeleteRecrods2 = new ArrayList<>();
            }
            
            this.remainedDeleteRecrods2.add(record);
            record.no(remainedDeleteRecrods2.size());
            
            return this;
        }
    }
    
    private static class RemainedOverRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsMapColumns(previousColumnName="氏名")
        private Map<String, String> dateAttended;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            IsEmptyBuilder builder = new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(true));
            builder.append(name);
            builder.compare(new IsEmptyComparator() {
                
                @Override
                public boolean isEmpty() {
                    if(dateAttended == null || dateAttended.isEmpty()) {
                        return true;
                    }
                    
                    for(String value : dateAttended.values()) {
                        if(Utils.isNotEmpty(value)) {
                            return false;
                        }
                    }
                    
                    return true;
                }
            });
            
            return builder.isEmpty();
        }
        
        public RemainedOverRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public RemainedOverRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public RemainedOverRecord dateAttended(Map<String, String> dateAttended) {
            this.dateAttended = dateAttended;
            return this;
        }
        
        public RemainedOverRecord addDateAttended(final String key, final String value) {
            if(dateAttended == null) {
                this.dateAttended = new LinkedHashMap<>();
            }
            
            this.dateAttended.put(key, value);
            
            return this;
        }
        
    }
    
    /**
     * 入力規則用のシート
     */
    @XlsSheet(name="オプション設定（入力規則）")
    private static class ValidationRuleSheet {
        
        /**
         * 入力定義が設定されてた表（挿入用）
         */
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="入力規則（レコードの挿入）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<DataValidationRecord> insertValidationRecrods;
        
        /**
         * 名前の定義用の表
         */
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="名前の定義", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Insert)
        private List<NameDefRecord> nameRecords;
        
        /**
         * 入力定義が設定されてた表（削除用）
         */
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="入力規則（レコードの削除）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Break, remainedRecord=RemainedRecordOperate.Delete)
        private List<DataValidationRecord> deleteValidationRecrods;
        
        /**
         * 入力定義が設定されてた表（削除用）（データなし）
         */
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="入力規則（レコードの削除）（データなし）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Break, remainedRecord=RemainedRecordOperate.Delete)
        private List<DataValidationRecord> nonDeleteValidationRecrods;
        
        /**
         * 入力定義が設定されてた表（コピー用）
         */
        @XlsHint(order=5)
        @XlsHorizontalRecords(tableLabel="入力規則（レコードのコピー）", terminal=RecordTerminal.Border, skipEmptyRecord=true,
                overRecord=OverRecordOperate.Copy, remainedRecord=RemainedRecordOperate.Delete)
        private List<DataValidationRecord> copyValidationRecrods;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ValidationRuleSheet addInsert(DataValidationRecord record) {
            if(insertValidationRecrods == null) {
                this.insertValidationRecrods = new ArrayList<>();
            }
            
            this.insertValidationRecrods.add(record);
            record.no(insertValidationRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ValidationRuleSheet addDelete(DataValidationRecord record) {
            if(deleteValidationRecrods == null) {
                this.deleteValidationRecrods = new ArrayList<>();
            }
            
            this.deleteValidationRecrods.add(record);
            record.no(deleteValidationRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ValidationRuleSheet addCopy(DataValidationRecord record) {
            if(copyValidationRecrods == null) {
                this.copyValidationRecrods = new ArrayList<>();
            }
            
            this.copyValidationRecrods.add(record);
            record.no(copyValidationRecrods.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public ValidationRuleSheet add(NameDefRecord record) {
            if(nameRecords == null) {
                this.nameRecords = new ArrayList<>();
            }
            
            this.nameRecords.add(record);
            record.no(nameRecords.size());
            
            return this;
        }
    }
    
    /**
     * 入力規則を設定されたレコード
     *
     */
    private static class DataValidationRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsBooleanConverter(loadForTrue="○", loadForFalse={"×", "-", ""}, saveAsTrue="○", saveAsFalse="×")
        @XlsColumn(columnName="リスト形式")
        private Boolean selectRule;
        
        @XlsColumn(columnName="参照形式")
        private String refRule;
        
        @XlsBooleanConverter(loadForTrue="レ", loadForFalse={"-", ""}, saveAsTrue="レ", saveAsFalse="-")
        @XlsMapColumns(previousColumnName="参照形式")
        private Map<String, Boolean> category;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            IsEmptyBuilder builder = new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(true));
            builder.append(selectRule);
            builder.compare(new IsEmptyComparator() {
                
                @Override
                public boolean isEmpty() {
                    if(category == null || category.isEmpty()) {
                        return true;
                    }
                    
                    for(Boolean value : category.values()) {
                        if(value != null && value == true) {
                            return false;
                        }
                    }
                    
                    return true;
                }
            });
            
            return builder.isEmpty();
        }
        
        public DataValidationRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public DataValidationRecord selectRule(Boolean selectRule) {
            this.selectRule = selectRule;
            return this;
        }
        
        public DataValidationRecord refRule(String refRule) {
            this.refRule = refRule;
            return this;
        }
        
        public DataValidationRecord category(Map<String, Boolean> category) {
            this.category = category;
            return this;
        }
        
        public DataValidationRecord addCategory(final String key, final Boolean value) {
            if(category == null) {
                this.category = new LinkedHashMap<>();
            }
            
            this.category.put(key, value);
            
            return this;
        }
        
    }
    
    /**
     * 名前の定義用のレコード
     *
     */
    private static class NameDefRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="機能名")
        private String functionName;
        
        public NameDefRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public NameDefRecord functionName(String functionName) {
            this.functionName = functionName;
            return this;
        }
        
    }
    
    /**
     * コメント付きのシート
     */
    @XlsSheet(name="オプション指定（コメント）")
    private static class CommentSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsHint(order=1)
        @XlsConverter(forceWrapText=true)
        @XlsLabelledCell(label="文字装飾のコメント", type=LabelledCellType.Right)
        private String value1;
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="コメントがある表（行の追加）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Insert)
        private List<CommentRecord> insertRecords;
        
        @XlsHint(order=3)
        @XlsHorizontalRecords(tableLabel="コメントがある表（行の削除）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Break, remainedRecord=RemainedRecordOperate.Delete)
        private List<CommentRecord> deleteRecords;
        
        @XlsHint(order=4)
        @XlsHorizontalRecords(tableLabel="コメントがある表（行のコピー）", terminal=RecordTerminal.Border,
                overRecord=OverRecordOperate.Copy)
        private List<CommentRecord> copyRecords;
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public CommentSheet addInsert(CommentRecord record) {
            if(insertRecords == null) {
                this.insertRecords = new ArrayList<>();
            }
            
            this.insertRecords.add(record);
            record.no(insertRecords.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public CommentSheet addDelete(CommentRecord record) {
            if(deleteRecords == null) {
                this.deleteRecords = new ArrayList<>();
            }
            
            this.deleteRecords.add(record);
            record.no(deleteRecords.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public CommentSheet addCopy(CommentRecord record) {
            if(copyRecords == null) {
                this.copyRecords = new ArrayList<>();
            }
            
            this.copyRecords.add(record);
            record.no(copyRecords.size());
            
            return this;
        }
    
    }
    
    /**
     * 見出しにコメントが付与されているレコード
     */
    private static class CommentRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="項目")
        private String name;
        
        @XlsColumn(columnName="値")
        private Double value;
        
        @XlsColumn(columnName="備考")
        private String comment;
        
        public CommentRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public CommentRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public CommentRecord value(Double value) {
            this.value = value;
            return this;
        }
        
        public CommentRecord comment(String comment) {
            this.comment = comment;
            return this;
        }
        
    }
    
    /**
     * メソッドにアノテーションを付与するシート
     * @since 1.0
     *
     */
    @XlsSheet(name="メソッドにアノテーションを設定")
    private static class MethodAnnoSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        private List<MethodAnnoRecord> records;
        
        private MethodAnnoMapRecord[] mapRecords;
        
        @XlsHint(order=1)
        @XlsHorizontalRecords(tableLabel="名簿", overRecord=OverRecordOperate.Insert)
        public List<MethodAnnoRecord> getRecords() {
            return records;
        }
        
        @XlsHorizontalRecords(tableLabel="名簿", skipEmptyRecord=true)
        public void setRecords(List<MethodAnnoRecord> records) {
            this.records = records;
        }
        
        @XlsHint(order=2)
        @XlsHorizontalRecords(tableLabel="出欠", overRecord=OverRecordOperate.Insert)
        public MethodAnnoMapRecord[] getMapRecords() {
            return mapRecords;
        }
        
        @XlsHorizontalRecords(tableLabel="出欠", skipEmptyRecord=true)
        public void setMapRecords(MethodAnnoMapRecord[] mapRecords) {
            this.mapRecords = mapRecords;
        }
        
        public MethodAnnoSheet add(MethodAnnoRecord record) {
            if(records == null) {
                this.records = new ArrayList<>();
            }
            
            this.records.add(record);
            record.no(records.size());
            
            return this;
        }
        
        /**
         * noを自動的に付与する。
         * @param record
         * @return 自身のインスタンス
         */
        public MethodAnnoSheet add(MethodAnnoMapRecord record) {
            
            final List<MethodAnnoMapRecord> list;
            if(mapRecords == null) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>(Arrays.asList(mapRecords));
            }
            
            list.add(record);
            record.no(list.size());
            
            this.mapRecords = list.toArray(new MethodAnnoMapRecord[list.size()]);
            
            return this;
        }
    }
    
    /**
     * メソッドにアノテーションを付与したレコード
     * @since 1.0
     */
    public static class MethodAnnoRecord {
        
        private int no;
        
        private String name;
        
        private Date birthday;
        
        private Point noPosition;
        
        private Point namePosition;
        
        private Point birthdayPosition;
        
        private String noLabel;
        
        private String nameLabel;
        
        private String birthdayLabel;
        
        @XlsColumn(columnName="No.")
        public int getNo() {
            return no;
        }
        
        @XlsColumn(columnName="No.")
        public void setNo(int no) {
            this.no = no;
        }
        
        @XlsColumn(columnName="氏名")
        public String getName() {
            return name;
        }
        
        @XlsColumn(columnName="氏名")
        public void setName(String name) {
            this.name = name;
        }
        
        @XlsColumn(columnName="生年月日")
        public Date getBirthday() {
            return birthday;
        }
        
        @XlsDateConverter(javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="生年月日")
        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
        
        public void setNoPosition(int x, int y) {
            this.noPosition = new Point(x, y);
        }
        
        public void setNamePosition(int x, int y) {
            this.namePosition = new Point(x, y);
        }
        
        public void setBirthdayPosition(int x, int y) {
            this.birthdayPosition = new Point(x, y);
        }
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return new IsEmptyBuilder()
                .append(name)
                .append(birthday)
                .isEmpty();
        }
        
        public MethodAnnoRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public MethodAnnoRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public MethodAnnoRecord birthday(Date birthday) {
            this.birthday = birthday;
            return this;
        }
        
        public void setNoLabel(String noLabel) {
            this.noLabel = noLabel;
        }
        
        public void setNameLabel(String nameLabel) {
            this.nameLabel = nameLabel;
        }
        
        public void setBirthdayLabel(String birthdayLabel) {
            this.birthdayLabel = birthdayLabel;
        }
        
    }
    
    /**
     * メソッドにアノテーションを付与したレコード - マップ
     * @since 1.0
     */
    public static class MethodAnnoMapRecord {
        
        private int no;
        
        private String name;
        
        private Map<String, Boolean> dateAttended;
        
        private Point noPosition;
        
        private Point namePosition;
        
        private Map<String, Point> dateAttendedPosition;
        
        private String noLabel;
        
        private String nameLabel;
        
        private Map<String, String> dateAttendedLabel;
        
        @XlsColumn(columnName="No.")
        public int getNo() {
            return no;
        }
        
        @XlsColumn(columnName="No.")
        public void setNo(int no) {
            this.no = no;
        }
        
        @XlsColumn(columnName="氏名")
        public String getName() {
            return name;
        }
        
        @XlsColumn(columnName="氏名")
        public void setName(String name) {
            this.name = name;
        }
        
        @XlsBooleanConverter(saveAsTrue="出席", saveAsFalse="欠席")
        @XlsMapColumns(previousColumnName="氏名")
        public Map<String, Boolean> getDateAttended() {
            return dateAttended;
        }
        
        @XlsBooleanConverter(loadForTrue="出席", loadForFalse="欠席")
        @XlsMapColumns(previousColumnName="氏名")
        public void setDateAttended(Map<String, Boolean> dateAttended) {
            this.dateAttended = dateAttended;
        }
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return new IsEmptyBuilder()
                .append(name)
                .append(dateAttended)
                .isEmpty();
        }
        
        public MethodAnnoMapRecord no(int no) {
            this.no = no;
            return this;
        }
        
        public MethodAnnoMapRecord name(String name) {
            this.name = name;
            return this;
        }
        
        public MethodAnnoMapRecord dateAttended(Map<String, Boolean> dateAttended) {
            this.dateAttended = dateAttended;
            return this;
        }
        
        public MethodAnnoMapRecord addDateAttended(final String key, final Boolean value) {
            if(dateAttended == null) {
                this.dateAttended = new LinkedHashMap<>();
            }
            
            this.dateAttended.put(key, value);
            
            return this;
        }
        
        public void setNoPosition(int x, int y) {
            this.noPosition = new Point(x, y);
        }
        
        public void setNamePosition(int x, int y) {
            this.namePosition = new Point(x, y);
        }
        
        public void setDateAttendedPosition(String key, int x, int y) {
            if(this.dateAttendedPosition == null) {
                this.dateAttendedPosition = new LinkedHashMap<>();
            }
            this.dateAttendedPosition.put(key, new Point(x, y));
        }
        
        public void setNoLabel(String noLabel) {
            this.noLabel = noLabel;
        }
        
        public void setNameLabel(String nameLabel) {
            this.nameLabel = nameLabel;
        }
        
        public void setDateAttendedLabel(String key, String label) {
            if(this.dateAttendedLabel == null) {
                this.dateAttendedLabel = new LinkedHashMap<>();
            }
            this.dateAttendedLabel.put(key, label);
        }
        
    }
    
    /**
     * 連結した表
     *
     */
    @XlsSheet(name="連結した表")
    private static class ConcatSheet {
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, terminateLabel="平均", headerLimit=3, skipEmptyRecord=true)
        private List<UserRecord> userRecords;
        
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, range=4, skipEmptyRecord=true)
        private List<ResultRecord> resultRecords;
        
        
        /**
         * 名簿情報
         */
        private static class UserRecord {
            
            private Map<String, Point> positions;
            
            private Map<String, String> labels;
            
            @XlsColumn(columnName="No.", optional=true)
            private int no;
            
            @XlsColumn(columnName="氏名")
            private String name;
            
            @XlsColumn(columnName="生年月日")
            @XlsDateConverter(javaPattern="yyyy年M月d日")
            private Date birthDady;
            
            @XlsIsEmpty
            public boolean isEmpty() {
                return IsEmptyBuilder.reflectionIsEmpty(this, "positoins", "labels", "no");
            }
        }
        
        /**
         * テスト結果情報
         *
         */
        private static class ResultRecord {
            
            private Map<String, Point> positions;
            
            private Map<String, String> labels;
            
            @XlsConverter(defaultValue="99")
            @XlsColumn(columnName="No.", optional=true)
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
            
        }
        
    }
    
}
