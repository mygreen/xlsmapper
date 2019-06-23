package com.gh.mygreen.xlsmapper;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.util.CellFinder;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * マニュアルなどに明記するためのサンプル
 *
 * @author T.TSUCHIE
 *
 */
public class SampleTest {

    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }

    /**
     * マッピングの基本 - 読み込み
     */
    @Test
    public void test_HowTo_Load() throws Exception {

        // シートの読み込み
        XlsMapper xlsMapper = new XlsMapper();
        UserSheet sheet = xlsMapper.load(
            new FileInputStream("src/test/data/sample.xlsx"), // 読み込むExcelファイル。
            UserSheet.class                     // シートマッピング用のPOJOクラス。
            );

        // assertion
        assertThat(sheet.createDate, is(toUtilDate(toTimestamp("2016-03-08 00:00:00.000"))));
        assertThat(sheet.users, hasSize(4));
        for(UserRecord record : sheet.users) {
            if(record.no == 1) {
                assertThat(record.className, is("A"));
                assertThat(record.name, is("Ichiro"));
                assertThat(record.gender, is(Gender.male));

            } else if(record.no == 2) {
                assertThat(record.className, is("A"));
                assertThat(record.name, is("Hanako"));
                assertThat(record.gender, is(Gender.female));

            } else if(record.no == 3) {
                assertThat(record.className, is("A"));
                assertThat(record.name, is("Taro"));
                assertThat(record.gender, is(Gender.male));

            } else if(record.no == 4) {
                assertThat(record.className, is("B"));
                assertThat(record.name, is("Jiro"));
                assertThat(record.gender, is(Gender.male));

            }
        }

    }

    /**
     * マッピングの基本 - 書き込み
     */
    @Test
    public void test_HowTo_Save() throws Exception {

        UserSheet sheet = new UserSheet();
        sheet.createDate = new Date();

        List<UserRecord> users = new ArrayList<>();

        // 1レコード分の作成
        UserRecord record1 = new UserRecord();
        record1.no = 1;
        record1.className = "A";
        record1.name = "Ichiro";
        record1.gender = Gender.male;
        users.add(record1);

        UserRecord record2 = new UserRecord();
        record2.no = 2;
        record2.className = "A";
        record2.name = "Hanako";
        record2.gender = Gender.female;
        users.add(record2);

        UserRecord record3 = new UserRecord();
        record3.no = 3;
        record3.className = "B";
        record3.name = "Taro";
        record3.gender = Gender.female;
        users.add(record3);


        sheet.users = users;

        // シートの書き込み
        XlsMapper xlsMapper = new XlsMapper();
        xlsMapper.save(
            new FileInputStream("src/test/data/sample_template.xlsx"), // テンプレートのExcelファイル
            new FileOutputStream(new File(OUT_DIR, "sample_out.xlsx")),     // 書き込むExcelファイル
            sheet                                // 作成したデータ
            );


        // assertion
        UserSheet inSheet = xlsMapper.load(new FileInputStream(new File(OUT_DIR, "sample_out.xlsx")), UserSheet.class);
        assertThat(inSheet.createDate, is(sheet.createDate));
        assertThat(inSheet.users, hasSize(sheet.users.size()));
        for(int i=0; i < inSheet.users.size(); i++) {
            UserRecord inRecord = inSheet.users.get(i);
            assertThat(inRecord.className, is(sheet.users.get(i).className));
            assertThat(inRecord.name, is(sheet.users.get(i).name));
            assertThat(inRecord.gender, is(sheet.users.get(i).gender));
        }

    }

    // シート用のPOJOクラスの定義
    @XlsSheet(name="List")
    private static class UserSheet {

        @XlsLabelledCell(label="Date", type=LabelledCellType.Right)
        @XlsDateTimeConverter(excelPattern="yyyy/m/d")
        Date createDate;

        @XlsHorizontalRecords(tableLabel="User List")
        @XlsRecordOption(overOperation=OverOperation.Insert)
        List<UserRecord> users;

    }

    // レコード用のPOJOクラスの定義
    private static class UserRecord {

        @XlsColumn(columnName="ID")
        int no;

        @XlsColumn(columnName="Class", merged=true)
        String className;

        @XlsColumn(columnName="Name")
        String name;

        @XlsColumn(columnName="Gender")
        Gender gender;

    }

    // 性別を表す列挙型の定義
    private enum Gender {
        male, female;
    }

    /**
     * MapColumnsで書き込み時に、動的にカラムを増やす場合
     */
    @Test
    public void test_MapColumn_dynamic_save() throws Exception {

        // create data
        MapColumnsDynamicSheet sheet = new MapColumnsDynamicSheet();
        List<MapColumnsDynamicSheet.SampleRecord> records = new ArrayList<>();

        // record1
        MapColumnsDynamicSheet.SampleRecord record1 = new MapColumnsDynamicSheet.SampleRecord();
        record1.id = 1;
        record1.name = "Taro Yamada";
        record1.addAttendedMap("4月1日", "出席").addAttendedMap("4月2日", "出席").addAttendedMap("4月3日", "欠席");
        records.add(record1);

        sheet.records = records;

        // シートの書き込み
        XlsMapper xlsMapper = new XlsMapper();
        xlsMapper.save(
            new FileInputStream("src/test/data/sample_template.xlsx"),
            new FileOutputStream(new File(OUT_DIR, "sample_out.xlsx")),
            sheet
            );

    }

//    @Test
    public void test_mofidy_sheet() throws Exception {

        MapColumnsDynamicSheet.SampleRecord record1 = new MapColumnsDynamicSheet.SampleRecord();
        record1.addAttendedMap("4月1日", "出席").addAttendedMap("4月2日", "出席").addAttendedMap("4月3日", "欠席");

        Workbook workbook = WorkbookFactory.create(new FileInputStream("src/test/data/sample_template.xlsx"));
        Sheet sheet = workbook.getSheet("MapColumn(dynamic)");

        Configuration config = new Configuration();

        // 日付のセル[日付]を取得する
        Cell baseHeaderCell = CellFinder.query(sheet, "[日付]", config).findWhenNotFoundException();
        List<String> dateHeaders = new ArrayList<>(record1.attendedMap.keySet());

        // 1つ目の見出しの書き換え
        baseHeaderCell.setCellValue(dateHeaders.get(0));

        // ２つ目以降の見出し列の追加
        Row headerRow = baseHeaderCell.getRow();
        for(int i=1; i < dateHeaders.size(); i++) {
            Cell headerCell = headerRow.createCell(baseHeaderCell.getColumnIndex() + i);

            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(baseHeaderCell.getCellStyle());
            headerCell.setCellStyle(style);
            headerCell.setCellValue(dateHeaders.get(i));
        }

        // データ行の列の追加
        Row valueRow = sheet.getRow(baseHeaderCell.getRowIndex() + 1);
        Cell baseValueCell = valueRow.getCell(baseHeaderCell.getColumnIndex());
        for(int i=1; + i < dateHeaders.size(); i++) {
            Cell valueCell = valueRow.createCell(baseValueCell.getColumnIndex() + i);

            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(baseValueCell.getCellStyle());
            valueCell.setCellStyle(style);

        }

        workbook.write(new FileOutputStream(new File(OUT_DIR, "sample_out.xlsx")));
    }

    // シート用クラス
    @XlsSheet(name="MapColumn(dynamic)")
    private static class MapColumnsDynamicSheet {

        @XlsHorizontalRecords(tableLabel="ユーザ一覧")
        @XlsRecordOption(overOperation=OverOperation.Insert)
        List<SampleRecord> records;

        // XlsMapColumnsのマッピング用のセルを作成する
        @XlsPreSave
        public void onPreSave(final Sheet sheet, final Configuration config) {

            try {
                final Workbook workbook = sheet.getWorkbook();

                // 基準となる日付のセル[日付]を取得する
                Cell baseHeaderCell = CellFinder.query(sheet, "[日付]", config).findWhenNotFoundException();

                // 書き換えるための見出しの値の取得
                List<String> dateHeaders = new ArrayList<>(records.get(0).attendedMap.keySet());

                // 1つ目の見出しの書き換え
                baseHeaderCell.setCellValue(dateHeaders.get(0));

                // ２つ目以降の見出し列の追加
                Row headerRow = baseHeaderCell.getRow();
                for(int i=1; i < dateHeaders.size(); i++) {
                    Cell headerCell = headerRow.createCell(baseHeaderCell.getColumnIndex() + i);

                    CellStyle style = workbook.createCellStyle();
                    style.cloneStyleFrom(baseHeaderCell.getCellStyle());
                    headerCell.setCellStyle(style);
                    headerCell.setCellValue(dateHeaders.get(i));

                }

                // 2つ目以降のデータ行の列の追加
                Row valueRow = sheet.getRow(baseHeaderCell.getRowIndex() + 1);
                Cell baseValueCell = valueRow.getCell(baseHeaderCell.getColumnIndex());
                for(int i=1; + i < dateHeaders.size(); i++) {
                    Cell valueCell = valueRow.createCell(baseValueCell.getColumnIndex() + i);

                    CellStyle style = workbook.createCellStyle();
                    style.cloneStyleFrom(baseValueCell.getCellStyle());
                    valueCell.setCellStyle(style);

                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        // レコード用クラス
        static class SampleRecord {

            @XlsColumn(columnName="ID")
            private int id;

            @XlsColumn(columnName="名前")
            private String name;

            @XlsMapColumns(previousColumnName="名前")
            private Map<String, String> attendedMap;

            public SampleRecord addAttendedMap(final String key, final String value) {
                if(attendedMap == null) {
                    this.attendedMap = new LinkedHashMap<>();
                }

                this.attendedMap.put(key, value);

                return this;
            }

        }

    }

    /**
     * {@link XlsSheet} - 正規表現の場合のシートのコピー
     */
    @Test
    public void test_Sheet_regex_clone() throws Exception {

        // 正規表現による複数のシートを出力する場合。
        // 書き込み時に、シート名を設定して、一意に関連づけます。
        SheetRegexClone sheet1 = new SheetRegexClone();
        sheet1.sheetName = "Sheet_1"; // シート名の設定

        SheetRegexClone sheet2 = new SheetRegexClone();
        sheet2.sheetName = "Sheet_2"; // シート名の設定

        SheetRegexClone sheet3 = new SheetRegexClone();
        sheet3.sheetName = "Sheet_3"; // シート名の設定

        SheetRegexClone[] sheets = new SheetRegexClone[]{sheet1, sheet2, sheet3};

        // シートのクローン
        Workbook workbook = WorkbookFactory.create(new FileInputStream("src/test/data/sample_template.xlsx"));
        Sheet templateSheet = workbook.getSheet("XlsSheet(regexp)");
        for(SheetRegexClone sheetObj : sheets) {
            int sheetIndex = workbook.getSheetIndex(templateSheet);
            Sheet cloneSheet = workbook.cloneSheet(sheetIndex);
            workbook.setSheetName(workbook.getSheetIndex(cloneSheet), sheetObj.sheetName);
        }

        // コピー元のシートを削除する
        workbook.removeSheetAt(workbook.getSheetIndex(templateSheet));

        // クローンしたシートファイルを、一時ファイルに一旦出力する。
        File cloneTemplateFile = File.createTempFile("template", ".xlsx");
        workbook.write(new FileOutputStream(cloneTemplateFile));

        // 複数のシートの書き込み
        XlsMapper xlsMapper = new XlsMapper();
        xlsMapper.saveMultiple(
                new FileInputStream(cloneTemplateFile), // クローンしたシートを持つファイルを指定する
                new FileOutputStream(new File(OUT_DIR, "sample_out.xlsx")),
                sheets);

    }

    @XlsSheet(regex="Sheet_[0-9]+")
    private static class SheetRegexClone {

        @XlsSheetName
        @XlsLabelledCell(label="シート名", type=LabelledCellType.Right)
        private String sheetName;
    }

    /**
     * アノテーションのシート名の動的な変更
     */
    @Test
    public void test_overide_sheetName() throws Exception {

        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(OverrideSheetName.class)
                        .override(true)   // アノテーションを差分だけ反映する設定を有効にします。
                        .annotation(createAnnotation(XlsSheet.class)
                                .attribute("name", "シート2")
                                .buildAnnotation())
                        .buildClass())
                .buildXml();

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);

        // XmlMapperクラスに直接渡せます。
        OverrideSheetName sheet = mapper.load(
            new FileInputStream("src/test/data/sample_overrideSheetName.xlsx"),
            OverrideSheetName.class);

        assertThat(sheet.sheetName, is("シート2"));
        assertThat(sheet.description, is("変更後のシートです。"));

    }

    @XlsSheet(name="シート1")
    private static class OverrideSheetName {

        @XlsSheetName
        private String sheetName;

        @XlsLabelledCell(label="説明", type=LabelledCellType.Right)
        private String description;

    }

}
