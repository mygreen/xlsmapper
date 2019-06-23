package com.gh.mygreen.xlsmapper.fieldprocessor;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledComment;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.RemainedOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.IterateTablesProcessor;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link IterateTablesProcessor}のテスタ
 * アノテーション{@link XlsIterateTables}のテスタ。
 *
 * @version 2.1
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
     * 読み込み用のファイルの定義
     */
    private File inputFile = new File("src/test/data/anno_IterateTables.xlsx");

    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/anno_IterateTables_template.xlsx");

    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "anno_IterateTables_out.xlsx";

    /**
     * 通常の表のテスト
     */
    @Test
    public void test_load_it_normal() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);

            NormalSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<NormalArraySheet> errors = mapper.loadDetail(in, NormalArraySheet.class);

            NormalArraySheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<VerticalSheet> errors = mapper.loadDetail(in, VerticalSheet.class);

            VerticalSheet sheet = errors.getTarget();

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
    @Test
    public void test_load_it_horizontal_vertical() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            assertThatThrownBy(() -> mapper.load(in, HorizontalAndVerticalSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessage("アノテーション'@XlsIterateTables'を設定しているクラス'com.gh.mygreen.xlsmapper.fieldprocessor.AnnoIterateTablesTest$HorizontalAndVerticalSheet$HorizontalAndVerticalClassTable'において、アノテーション'@XlsHorizontalRecords'と'@XlsVerticalRecords'の両方が設定されています。どちらか一方を設定してください。");
        }
    }

    /**
     * 見出し用セルが見つからない
     */
    @Test(expected=CellNotFoundException.class)
    public void test_load_it_not_found_cell() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {

            mapper.load(in, NotFoundCellSheet.class);

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<OptionalCellSheet> errors = mapper.loadDetail(in, OptionalCellSheet.class);

            OptionalCellSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<ConcatTableSheet> errors = mapper.loadDetail(in, ConcatTableSheet.class);

            ConcatTableSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<MethodAnnoSheet> errors = mapper.loadDetail(in, MethodAnnoSheet.class);

            MethodAnnoSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<RegexSheet> errors = mapper.loadDetail(in, RegexSheet.class);

            RegexSheet sheet = errors.getTarget();

            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(RegexSheet.RegexTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }

    }

    /**
     * 配列セルのマッピング
     * @since 2.0
     */
    @Test
    public void test_load_it_arrayCell() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);

        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<ArrayCellSheet> errors = mapper.loadDetail(in, ArrayCellSheet.class);

            ArrayCellSheet sheet = errors.getTarget();

            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(2));
                for(ArrayCellSheet.ArrayCellTable table : sheet.classTables) {
                    assertTable(table, errors);
                }
            }
        }

    }
    
    /**
     * コメント情報のマッピング
     * @since 2.1
     */
    @Test
    public void test_load_it_comment() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<CommentSheet> errors = mapper.loadDetail(in, CommentSheet.class);

            CommentSheet sheet = errors.getTarget();

            if(sheet.tables != null) {
                assertThat(sheet.tables, hasSize(2));
                for(CommentSheet.Table table : sheet.tables) {
                    assertTable(table, errors);
                }
            }
        }
        
    }
    
    
    

    private void assertTable(final HorizontalClassTable table, final SheetBindingErrors<?> errors) {

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

    private void assertTable(final VerticalClassTable table, final SheetBindingErrors<?> errors) {

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

    private void assertTable(final OptionalClassTable table, final SheetBindingErrors<?> errors) {

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

    private void assertTable(final ConcatClassTable table, final SheetBindingErrors<?> errors) {

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

    private void assertTable(final MethodAnnoTable table, final SheetBindingErrors<?> errors) {

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

    private void assertTable(final RegexSheet.RegexTable table, final SheetBindingErrors<?> errors) {

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

    private void assertTable(final ArrayCellSheet.ArrayCellTable table, final SheetBindingErrors<?> errors) {

        if(table.name[0].equals("1年") && table.name[1].equals("2年")) {

            assertThat(table.no, is(1));
            assertThat(table.name, is(arrayContaining("1年", "2組")));

            assertThat(table.persons, hasSize(2));
            for(ArrayCellSheet.ArrayCellRecord record : table.persons) {

                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.telNumber, is(contains("090", "1111", "1111")));

                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.telNumber, is(contains("090", "1111", "2222")));

                }

            }

        } else if(table.name[0].equals("2年") && table.name[1].equals("3年")) {

            assertThat(table.no, is(2));
            assertThat(table.name, is(arrayContaining("2年", "3組")));

            assertThat(table.persons, hasSize(3));
            for(ArrayCellSheet.ArrayCellRecord record : table.persons) {

                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.telNumber, is(contains("090", "2222", "1111")));

                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.telNumber, is(contains("090", "2222", "2222")));

                } else if(record.no == 3) {
                    assertThat(record.name, is("山田太郎"));
                    assertThat(record.telNumber, is(contains("090", "2222", "3333")));

                }

            }

        }



    }
    
    private void assertTable(final CommentSheet.Table table, final SheetBindingErrors<?> errors) {

        if(table.name.equals("1年2組")) {

            assertThat(table.no, is(1));
            assertThat(table.name, is("1年2組"));
            
            assertThat(table.noDesc, is("コメント1"));
            assertThat(table.nameDesc, is(nullValue()));
            
            assertThat(table.comments, hasEntry("name", "コメント3"));

            assertThat(table.records, hasSize(2));
            for(CommentSheet.Record record : table.records) {

                if(record.no == 1) {
                    assertThat(record.name, is("阿部一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))));
                    
                    assertThat(record.comments, hasEntry("no", "コメント5"));

                } else if(record.no == 2) {
                    assertThat(record.name, is("泉太郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))));

                    assertThat(record.comments, hasEntry("birthday", "コメント6"));
                }

            }

        } else if(table.name.equals("2年3組")) {

            assertThat(table.no, is(2));
            assertThat(table.name, is("2年3組"));

            assertThat(table.noDesc, is(nullValue()));
            assertThat(table.nameDesc, is("コメント2"));

            assertThat(table.comments, is(nullValue()));

            assertThat(table.records, hasSize(3));
            for(CommentSheet.Record record : table.records) {

                if(record.no == 1) {
                    assertThat(record.name, is("鈴木一郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))));

                } else if(record.no == 2) {
                    assertThat(record.name, is("林次郎"));
                    assertThat(record.birthday, is(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))));

                    assertThat(record.comments, hasEntry("name", "コメント4"));
                    
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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<NormalSheet> errors = mapper.loadDetail(in, NormalSheet.class);

            NormalSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<NormalArraySheet> errors = mapper.loadDetail(in, NormalArraySheet.class);

            NormalArraySheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<VerticalSheet> errors = mapper.loadDetail(in, VerticalSheet.class);

            VerticalSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<OptionalCellSheet> errors = mapper.loadDetail(in, OptionalCellSheet.class);

            OptionalCellSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<ConcatTableSheet> errors = mapper.loadDetail(in, ConcatTableSheet.class);

            ConcatTableSheet sheet = errors.getTarget();

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
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<MethodAnnoSheet> errors = mapper.loadDetail(in, MethodAnnoSheet.class);

            MethodAnnoSheet sheet = errors.getTarget();

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

        outSheet.add(new RegexSheet.RegexTable().name("1年2組")
                .add(new RegexSheet.RegexRecord().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))))
        );

        outSheet.add(new RegexSheet.RegexTable().name("2年3組")
                .add(new RegexSheet.RegexRecord().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))))
                .add(new RegexSheet.RegexRecord().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<RegexSheet> errors = mapper.loadDetail(in, RegexSheet.class);

            RegexSheet sheet = errors.getTarget();

            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));

                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
                }

            }

        }

    }

    /**
     * 書き込みのテスト - 配列セルの確認
     * @since 2.0
     */
    @Test
    public void test_save_it_arrayCell() throws Exception {

        // テストデータの作成
        ArrayCellSheet outSheet = new ArrayCellSheet();

        outSheet.add(new ArrayCellSheet.ArrayCellTable().name(new String[]{"1年", "2組"})
                .add(new ArrayCellSheet.ArrayCellRecord().name("阿部一郎").telNumber(Arrays.asList("090", "1111", "1111")))
                .add(new ArrayCellSheet.ArrayCellRecord().name("泉太郎").telNumber(Arrays.asList("090", "1111", "2222")))
        );

        outSheet.add(new ArrayCellSheet.ArrayCellTable().name(new String[]{"2年", "3組"})
                .add(new ArrayCellSheet.ArrayCellRecord().name("鈴木一郎").telNumber(Arrays.asList("090", "2222", "1111")))
                .add(new ArrayCellSheet.ArrayCellRecord().name("林次郎").telNumber(Arrays.asList("090", "2222", "2222")))
                .add(new ArrayCellSheet.ArrayCellRecord().name("山田太郎").telNumber(Arrays.asList("090", "2222", "3333")))
        );

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(false)
            .setRegexLabelText(true)
            .setNormalizeLabelText(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<ArrayCellSheet> errors = mapper.loadDetail(in, ArrayCellSheet.class);

            ArrayCellSheet sheet = errors.getTarget();

            if(sheet.classTables != null) {
                assertThat(sheet.classTables, hasSize(outSheet.classTables.size()));

                for(int i=0; i < sheet.classTables.size(); i++) {
                    assertRecord(sheet.classTables.get(i), outSheet.classTables.get(i), errors);
                }

            }

        }

    }
    
    /**
     * 書込みのテスト - コメント情報
     */
    @Test
    public void test_save_it_comment() throws Exception {
        
        // テストデータの作成
        CommentSheet outSheet = new CommentSheet();

        outSheet.add(new CommentSheet.Table().name("1年2組").noDesc("コメント1").comment("name", "コメント3")
                .add(new CommentSheet.Record().name("阿部一郎").birthday(toUtilDate(toTimestamp("2000-04-01 00:00:00.000"))).comment("no", "コメント5"))
                .add(new CommentSheet.Record().name("泉太郎").birthday(toUtilDate(toTimestamp("2000-04-02 00:00:00.000"))))
                .add(new CommentSheet.Record().name("山田花子").birthday(toUtilDate(toTimestamp("2000-04-03 00:00:00.000"))).comment("birthday", "コメント6"))
        );

        outSheet.add(new CommentSheet.Table().name("2年3組").nameDesc("コメント2")
                .add(new CommentSheet.Record().name("鈴木一郎").birthday(toUtilDate(toTimestamp("1999-04-01 00:00:00.000"))))
                .add(new CommentSheet.Record().name("林次郎").birthday(toUtilDate(toTimestamp("1999-04-02 00:00:00.000"))).comment("name", "コメント4"))
                .add(new CommentSheet.Record().name("山田太郎").birthday(toUtilDate(toTimestamp("1999-04-03 00:00:00.000"))))
        );

        // ファイルへの書き込み
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<CommentSheet> errors = mapper.loadDetail(in, CommentSheet.class);

            CommentSheet sheet = errors.getTarget();

            if(sheet.tables != null) {
                assertThat(sheet.tables, hasSize(outSheet.tables.size()));

                for(int i=0; i < sheet.tables.size(); i++) {
                    assertTable(sheet.tables.get(i), outSheet.tables.get(i), errors);
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
    private void assertTable(final CommentSheet.Table inRecord, final CommentSheet.Table outRecord, final SheetBindingErrors<?> errors) {

        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);

        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        
        assertThat(inRecord.noDesc, is(outRecord.noDesc));
        assertThat(inRecord.nameDesc, is(outRecord.nameDesc));
        
        assertThat(inRecord.comments, is(outRecord.comments));

        if(inRecord.records != null) {
            assertThat(inRecord.records, hasSize(outRecord.records.size()));

            for(int i=0; i < inRecord.records.size(); i++) {
                assertRecord(inRecord.records.get(i), outRecord.records.get(i), errors);
            }

        }
    }
    
    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final CommentSheet.Record inRecord, final CommentSheet.Record outRecord, final SheetBindingErrors<?> errors) {

        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);

        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.birthday, is(outRecord.birthday));
        
        assertThat(inRecord.comments, is(outRecord.comments));
    }

    /**
     * 書き込んだレコードを検証するための
     * @param inRecord
     * @param outRecord
     * @param errors
     */
    private void assertRecord(final HorizontalClassTable inRecord, final HorizontalClassTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final VerticalClassTable inRecord, final VerticalClassTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final OptionalClassTable inRecord, final OptionalClassTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final ConcatClassTable inRecord, final ConcatClassTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final PersonRecord inRecord, final PersonRecord outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final ResultRecord inRecord, final ResultRecord outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final MethodAnnoTable inRecord, final MethodAnnoTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final RegexSheet.RegexTable inRecord, final RegexSheet.RegexTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final RegexSheet.RegexRecord inRecord, final RegexSheet.RegexRecord outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final ArrayCellSheet.ArrayCellTable inRecord, final ArrayCellSheet.ArrayCellTable outRecord, final SheetBindingErrors<?> errors) {

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
    private void assertRecord(final ArrayCellSheet.ArrayCellRecord inRecord, final ArrayCellSheet.ArrayCellRecord outRecord, final SheetBindingErrors<?> errors) {

        System.out.printf("%s - assertRecord::%s no=%d\n",
                this.getClass().getSimpleName(), inRecord.getClass().getSimpleName(), inRecord.no);

        assertThat(inRecord.no, is(outRecord.no));
        assertThat(inRecord.name, is(outRecord.name));
        assertThat(inRecord.telNumber, is(outRecord.telNumber));
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

            @XlsOrder(1)
            @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
            private int no;

            @XlsOrder(2)
            @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
            private String name;

            @XlsOrder(3)
            @XlsHorizontalRecords(terminal=RecordTerminal.Border, headerLimit=3)
            @XlsRecordOption(overOperation=OverOperation.Copy, remainedOperation=RemainedOperation.Clear)
            private List<PersonRecord> persons;

            @XlsOrder(4)
            @XlsVerticalRecords(terminal=RecordTerminal.Border, right=4)
            @XlsRecordOption(overOperation=OverOperation.Copy, remainedOperation=RemainedOperation.Clear)
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

        @XlsOrder(1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;

        @XlsOrder(2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
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

        @XlsOrder(1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;

        @XlsOrder(2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;

        @XlsOrder(3)
        @XlsVerticalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Copy, remainedOperation=RemainedOperation.Clear)
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

        @XlsDateTimeConverter(javaPattern="yyyy年M月d日", excelPattern="yyyy/m/d")
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

        @XlsOrder(1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;

        @XlsOrder(2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right, optional=true)
        private String name;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
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

//        @XlsOrder(1)
        @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
        private int no;

//        @XlsOrder(2)
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String name;

        @XlsOrder(3)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border, headerLimit=3)
        @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
        private List<PersonRecord> persons;

        @XlsOrder(4)
        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Empty, range=4)
        @XlsRecordOption(overOperation=OverOperation.Copy, remainedOperation=RemainedOperation.Clear)
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

        @XlsOrder(1)
        public void setNo(int no) {
            this.no = no;
        }

        @XlsOrder(2)
        public String getName() {
            return name;
        }

        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        public void setName(String name) {
            this.name = name;
        }

        @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
        @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
        public List<PersonRecord> getPersons() {
            return persons;
        }

        @XlsOrder(3)
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
        private List<RegexTable> classTables;

        public RegexSheet add(RegexTable table) {

            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }

            this.classTables.add(table);
            table.no(classTables.size());

            return this;
        }

        private static class RegexTable {

            private Map<String, Point> positions;

            private Map<String, String> labels;

            @XlsOrder(1)
            @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
            private int no;

            @XlsOrder(2)
            @XlsLabelledCell(label="/クラス名.*/", type=LabelledCellType.Right)
            private String name;

            @XlsOrder(3)
            @XlsHorizontalRecords(tableLabel="/クラス情報.*/", terminal=RecordTerminal.Border)
            @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
            private List<RegexRecord> persons;

            // 値設定用のメソッド

            public RegexTable no(int no) {
                this.no = no;
                return this;
            }

            public RegexTable name(String name) {
                this.name = name;
                return this;
            }

            public RegexTable add(RegexRecord record) {

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

            @XlsDateTimeConverter(javaPattern="yyyy年M月d日")
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

    @XlsSheet(name="配列カラムの設定")
    private static class ArrayCellSheet {

        private Map<String, CellPosition> positions;

        private Map<String, String> labels;

        @XlsIterateTables(tableLabel="/クラス情報.*/", bottom=3)
        private List<ArrayCellTable> classTables;

        public ArrayCellSheet add(ArrayCellTable table) {

            if(classTables == null) {
                this.classTables = new ArrayList<>();
            }

            this.classTables.add(table);
            table.no(classTables.size());

            return this;
        }

        private static class ArrayCellTable {

            private Map<String, CellPosition> positions;

            private Map<String, String> labels;

            @XlsOrder(1)
            @XlsLabelledCell(label="番号", type=LabelledCellType.Right, optional=true)
            private int no;

            @XlsOrder(2)
            @XlsLabelledArrayCells(label="/クラス名.*/", type=LabelledCellType.Right, size=2)
            private String[] name;

            @XlsOrder(3)
            @XlsHorizontalRecords(tableLabel="/クラス情報.*/", terminal=RecordTerminal.Border)
            @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
            private List<ArrayCellRecord> persons;

            // 値設定用のメソッド

            public ArrayCellTable no(int no) {
                this.no = no;
                return this;
            }

            public ArrayCellTable name(String[] name) {
                this.name = name;
                return this;
            }

            public ArrayCellTable add(ArrayCellRecord record) {

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
        private static class ArrayCellRecord {

            private Map<String, CellPosition> positions;

            private Map<String, String> labels;

            @XlsColumn(columnName="No.")
            private int no;

            @XlsColumn(columnName="/氏名.*/")
            private String name;

            @XlsArrayColumns(columnName="電話番号", size=3)
            private List<String> telNumber;

            @XlsIgnorable
            public boolean isEmpty() {
                return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
            }

            public ArrayCellRecord no(int no) {
                this.no = no;
                return this;
            }

            public ArrayCellRecord name(String name) {
                this.name = name;
                return this;
            }

            public ArrayCellRecord telNumber(List<String> telNumber) {
                this.telNumber = telNumber;
                return this;
            }

        }

    }
    
    @XlsSheet(name="コメント情報")
    private static class CommentSheet {

        private Map<String, Point> positions;

        private Map<String, String> labels;
        
        private Map<String, String> comments;

        @XlsIterateTables(tableLabel="クラス情報", bottom=3)
        private List<Table> tables;

        public CommentSheet add(Table table) {

            if(tables == null) {
                this.tables = new ArrayList<>();
            }

            this.tables.add(table);
            table.no(tables.size());

            return this;
        }
        
        /**
         * 繰り返し用のテーブルの定義 - horizontal
         */
        private static class Table {

            private Map<String, Point> positions;

            private Map<String, String> labels;
            
            private Map<String, String> comments;

            @XlsOrder(1)
            @XlsLabelledCell(label="番号", type=LabelledCellType.Right)
            private int no;

            @XlsOrder(2)
            @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
            private String name;

            @XlsOrder(3)
            @XlsLabelledComment(label = "番号")
            private String noDesc;
            
            @XlsOrder(4)
            @XlsLabelledComment(label = "クラス名")
            private String nameDesc;

            @XlsOrder(5)
            @XlsHorizontalRecords(tableLabel="クラス情報", terminal=RecordTerminal.Border)
            @XlsRecordOption(overOperation=OverOperation.Insert, remainedOperation=RemainedOperation.Delete)
            private List<Record> records;
            
            public Table no(int no) {
                this.no = no;
                return this;
            }

            public Table name(String name) {
                this.name = name;
                return this;
            }
            
            public Table noDesc(String noDesc) {
                this.noDesc = noDesc;
                return this;
            }
            
            public Table nameDesc(String nameDesc) {
                this.nameDesc = nameDesc;
                return this;
            }

            public Table add(Record record) {

                if(records == null) {
                    this.records = new ArrayList<>();
                }

                this.records.add(record);
                record.no(records.size());

                return this;
            }
            
            public Table comment(String key, String text) {
                if(comments == null) {
                    this.comments = new HashMap<String, String>();
                }
                this.comments.put(key, text);
                return this;
            }

        }
        
        private static class Record {

            private Map<String, Point> positions;

            private Map<String, String> labels;
            
            private Map<String, String> comments;

            @XlsColumn(columnName="No.")
            private int no;

            @XlsColumn(columnName="氏名")
            private String name;

            @XlsDateTimeConverter(javaPattern="yyyy年M月d日", excelPattern="yyyy/m/d")
            @XlsColumn(columnName="誕生日")
            private Date birthday;

            @XlsIgnorable
            public boolean isEmpty() {
                return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
            }

            public Record no(int no) {
                this.no = no;
                return this;
            }

            public Record name(String name) {
                this.name = name;
                return this;
            }

            public Record birthday(Date birthday) {
                this.birthday = birthday;
                return this;
            }
            
            public Record comment(String key, String text) {
                if(comments == null) {
                    this.comments = new HashMap<String, String>();
                }
                this.comments.put(key, text);
                return this;
            }

        }

    }


}
