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

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsBooleanConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsDateConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link VerticalRecordsProcessor}のテスタ
 * アノテーション{@link XlsVerticalRecords}のテスタ。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnoVerticalRecordsTest {
    
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
    public void test_load_vr_startedPosition() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
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
    public void test_load_vr_startedPosition_error1() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionError1Sheet.class);
            
            StartedPositionError1Sheet sheet = mapper.load(in, StartedPositionError1Sheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 開始位置の指定のテスト - アドレス指定の書式が不正
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_vr_startedPosition_error2() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionError2Sheet.class);
            
            StartedPositionError2Sheet sheet = mapper.load(in, StartedPositionError2Sheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 開始位置の指定のテスト - インデックス指定の書式が不正
     */
    @Test(expected=AnnotationInvalidException.class)
    public void test_load_vr_startedPosition_error3() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(StartedPositionError3Sheet.class);
            
            StartedPositionError3Sheet sheet = mapper.load(in, StartedPositionError3Sheet.class, errors);
            
            fail();
        }
    }
    
    /**
     * 終了位置の指定のテスト
     */
    @Test
    public void test_load_vr_endPosition() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
            SheetBindingErrors errors = new SheetBindingErrors(EndPositionSheet.class);
            
            EndPositionSheet sheet = mapper.load(in, EndPositionSheet.class, errors);
            
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
            
        }
    }
    
    /**
     * カラムの設定テスト
     */
    @Test
    public void test_load_vr_columnSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
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
     * カラムの設定テスト
     */
    @Test
    public void test_load_vr_mapColumnSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
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
    public void test_load_vr_mapColumnSetting_bind_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(false);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
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
    public void test_load_vr_recordSetting() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setSkipTypeBindFailure(true);
        
        try(InputStream in = new FileInputStream("src/test/data/anno_VerticalRecords.xlsx")) {
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
            assertThat(record.birthday, is(utilDate(timestamp("1990-02-28 00:00:00.000"))));
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
            assertThat(record.birthday, is(utilDate(timestamp("1989-01-02 00:00:00.000"))));
            
        } else if(record.no == 2) {
            assertThat(record.name, is("鈴木次郎"));
            assertThat(record.birthday, is(utilDate(timestamp("1990-02-28 00:00:00.000"))));
            
        } else if(record.no == 4) {
            assertThat(record.name, is("林三郎"));
            assertThat(record.birthday, is(utilDate(timestamp("1992-04-14 00:00:00.000"))));
        }
        
    }
    
    /**
     * 開始位置の指定
     *
     */
    @XlsSheet(name="開始位置の指定")
    private static class StartedPositionSheet {
        
        @XlsVerticalRecords(tableLabel="○×一覧")
        private List<NormalRecord> normalRecords1;
        
        @XlsVerticalRecords(headerAddress="C9")
        private List<NormalRecord> normalRecords2;
        
        @XlsVerticalRecords(headerColumn=3, headerRow=13)
        private List<NormalRecord> normalRecords3;
        
//        @XlsVerticalRecords(tableLabel="◆△一覧", bottom=2)
        private List<NormalRecord> normalRecords4;
        
        @XlsVerticalRecords(tableLabel="存在しない", optional=true)
        private List<NormalRecord> normalRecords5;
        
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
    }
    
   /**
    * 開始位置の指定 - 見出し指定で見つからない場合
    *
    */
   @XlsSheet(name="開始位置の指定")
   private static class StartedPositionError1Sheet {
       
       @XlsVerticalRecords(tableLabel="存在しない", optional=false)
       private List<NormalRecord> normalRecords5;
       
   }
   
   /**
    * 開始位置の指定 - アドレス指定でフォーマット不正な場合
    *
    */
   @XlsSheet(name="開始位置の指定")
   private static class StartedPositionError2Sheet {
       
       @XlsVerticalRecords(headerAddress="_C7")
       private List<NormalRecord> normalRecords2;
       
   }
   
   /**
    * 開始位置の指定 - アドレス指定でインデックスが不正な場合
    *
    */
   @XlsSheet(name="開始位置の指定")
   private static class StartedPositionError3Sheet {
       
       @XlsVerticalRecords(headerColumn=-1, headerRow=-1)
       private List<NormalRecord> normalRecords3;
       
   }
   
   /**
    * 表の終了位置の指定
    *
    */
   @XlsSheet(name="終了位置の指定")
   private static class EndPositionSheet {
       
       @XlsVerticalRecords(tableLabel="終端レコードの指定（Empty）", terminal=RecordTerminal.Empty)
       private List<NormalRecord> normalRecords1;
       
       @XlsVerticalRecords(tableLabel="終端レコードの指定（Border）", terminal=RecordTerminal.Border)
       private List<NormalRecord> normalRecords2;
       
       @XlsVerticalRecords(tableLabel="終端セルの指定", terminal=RecordTerminal.Border, terminateLabel="合計")
       private List<NormalRecord> normalRecords3;
       
       @XlsVerticalRecords(tableLabel="見出しセルの個数指定", terminal=RecordTerminal.Border, headerLimit=3)
       private List<NormalRecord> normalRecords4;
       
   }
   
   /**
    * カラムの様々な指定のシート
    */
   @XlsSheet(name="カラムの設定")
   private static class ColumnSettingSheet {
       
       @XlsVerticalRecords(tableLabel="結合セル", terminal=RecordTerminal.Border)
       private List<MergedRecord> mergedRecords;
       
       @XlsVerticalRecords(tableLabel="見出しが結合", terminal=RecordTerminal.Border)
       private List<HeaderMergedRecord> headerMergedRecords;
       
       @XlsVerticalRecords(tableLabel="オプションのセル（セルがある）", terminal=RecordTerminal.Border)
       private List<OptionalRecord> optionalRecords1;
       
       @XlsVerticalRecords(tableLabel="オプションのセル（セルがない）", terminal=RecordTerminal.Border)
       private List<OptionalRecord> optionalRecords2;
       
       @XlsVerticalRecords(tableLabel="Converterがある", terminal=RecordTerminal.Border)
       private List<ConvertedRecord> convertedRecord;
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
       
       @XlsDateConverter(pattern="yyyy年M月d日", lenient=true)
       @XlsColumn(columnName="生年月日")
       private Date birthday;
       
       @XlsColumn(columnName="年齢")
       private int age;
       
   }
   
   /**
    * {@link XlsMapColumns}を使用したシート
    *
    */
   @XlsSheet(name="マップカラムの設定")
   private static class MapColumnSettingSheet {
       
       @XlsVerticalRecords(tableLabel="マップカラム（文字列）", terminal=RecordTerminal.Border)
       private List<MapRecord> mapRecords1;
       
       @XlsVerticalRecords(tableLabel="マップカラム（Converterあり）", terminal=RecordTerminal.Border)
       private List<MapConvertedRecord> mapRecords2;
       
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
       
       @XlsBooleanConverter(loadForTrue="出席", loadForFalse="欠席")
       @XlsMapColumns(previousColumnName="氏名")
       private Map<String, Boolean> dateAttended;
       
   }
   
   /**
    * 様々なレコードの設定
    */
   @XlsSheet(name="レコードの設定")
   private static class RecodSettingSheet {
       
       /**
        * 空のレコードをスキップ（）
        */
       @XlsVerticalRecords(tableLabel="名簿", terminal=RecordTerminal.Border, skipEmptyRecord=true)
       private List<EmptySkipRecord> skipList;
       
       /**
        * 配列
        */
       @XlsVerticalRecords(tableLabel="名簿", terminal=RecordTerminal.Border, skipEmptyRecord=true)
       private EmptySkipRecord[] skipArray;
       
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
       
       @XlsDateConverter(pattern="yyyy年M月d日")
       @XlsColumn(columnName="生年月日")
       private Date birthday;
       
       @XlsIsEmpty
       public boolean isEmpty() {
           return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
       }
   }
   
}
