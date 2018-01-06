package com.gh.mygreen.xlsmapper.xml;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;

import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;
import com.gh.mygreen.xlsmapper.xml.bind.ClassInfo;
import com.gh.mygreen.xlsmapper.xml.bind.FieldInfo;
import com.gh.mygreen.xlsmapper.xml.bind.MethodInfo;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * {@link XmlBuilder}のテスタ
 *
 * @version 1.4.1
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class XmlBuilderTest {

    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }

    /**
     * アノテーションがないクラスに設定する。
     */
    @Test
    public void test_simple() throws Exception {

        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(SimpleSheet.class)
                        .annotation(createAnnotation(XlsSheet.class)
                                .attribute("name", "単純なシート")
                                .buildAnnotation())
                        .field(createField("sheetName")
                                .annotation(createAnnotation(XlsSheetName.class)
                                        .buildAnnotation())
                                .buildField())
                        .field(createField("name")
                                .annotation(createAnnotation(XlsLabelledCell.class)
                                        .attribute("label", "名称")
                                        .attribute("type", LabelledCellType.Right)
                                        .buildAnnotation())
                                .annotation(createAnnotation(XlsTrim.class)
                                        .buildAnnotation())
                                .annotation(createAnnotation(XlsDefaultValue.class)
                                        .attribute("value", "－")
                                        .buildAnnotation())
                                .buildField())
                        .method(createMethod("setRecords")
                                .annotation(createAnnotation(XlsHorizontalRecords.class)
                                        .attribute("tableLabel", "名簿一覧")
                                        .attribute("terminal", RecordTerminal.Border)
                                        .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();


        System.out.println(xmlInfo.toXml());

        AnnotationReader reader = new AnnotationReader(xmlInfo);

        // クラス定義の読み込み
        Annotation[] classAnnos = reader.getAnnotations(SimpleSheet.class);
        XlsSheet sheetAnno = select(classAnnos, XlsSheet.class);
        assertThat(sheetAnno.name(), is("単純なシート"));

        // クラス定義の読み込み（アノテーションを指定）
        sheetAnno = reader.getAnnotation(SimpleSheet.class, XlsSheet.class);
        assertThat(sheetAnno.name(), is("単純なシート"));

        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(SimpleSheet.class.getDeclaredField("name"));

        XlsLabelledCell labeldCellAnno = select(nameAnnos, XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("名称"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Right));

        XlsTrim trimAnno = select(nameAnnos, XlsTrim.class);
        assertThat(trimAnno, is(not(nullValue())));

        XlsDefaultValue defaultValueAnno = select(nameAnnos, XlsDefaultValue.class);
        assertThat(defaultValueAnno.value(), is("－"));


        // フィールド定義の読み込み（アノテーションを指定）
        labeldCellAnno = reader.getAnnotation(SimpleSheet.class.getDeclaredField("name"), XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("名称"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Right));

        // メソッドの定義の読み込み
        Annotation[] recordsAnnos = reader.getAnnotations(SimpleSheet.class.getDeclaredMethod("setRecords", List.class));

        XlsHorizontalRecords horizontalRecordsAnno = select(recordsAnnos, XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));

        // メソッドの定義の読み込み（アノテーションを指定）
        horizontalRecordsAnno = reader.getAnnotation(SimpleSheet.class.getDeclaredMethod("setRecords", List.class), XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));

    }

    /**
     * クラスに定義されている定義の上書き
     * ・XML中のoverride属性がfalseの場合。
     * @throws Exception
     */
    @Test
    public void test_override() throws Exception {

        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(OrverrideSheet.class)
                        .override(true)
                        .annotation(createAnnotation(XlsSheet.class)
                                .attribute("name", "")
                                .attribute("regex", "リスト.+")
                                .buildAnnotation())
                        .field(createField("name")
                                .override(true)
                                .annotation(createAnnotation(XlsLabelledCell.class)
                                        .attribute("label", "クラス名")
                                        .attribute("type", LabelledCellType.Bottom)
                                        .buildAnnotation())
                                .buildField())
                        .method(createMethod("setRecords")
                                .override(true)
                                .annotation(createAnnotation(XlsHorizontalRecords.class)
                                        .attribute("tableLabel", "名簿一覧")
                                        .attribute("terminal", RecordTerminal.Border)
                                        .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();

        System.out.println(xmlInfo.toXml());

        AnnotationReader reader = new AnnotationReader(xmlInfo);

        // クラス定義の読み込み
        XlsSheet sheetAnno = reader.getAnnotation(OrverrideSheet.class, XlsSheet.class);
        assertThat(sheetAnno.name(), is(""));
        assertThat(sheetAnno.regex(), is("リスト.+"));

        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(OrverrideSheet.class.getDeclaredField("name"));

        // フィールド - XMLに定義している
        XlsLabelledCell labeldCellAnno = select(nameAnnos, XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("クラス名"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Bottom));

        {
            // フィールド - XMLに定義していない
            XlsCellOption cellOptionAnno = select(nameAnnos, XlsCellOption.class);
            assertThat(cellOptionAnno.shrinkToFit(), is(true));

            XlsTrim trimAnno = select(nameAnnos, XlsTrim.class);
            assertThat(trimAnno, is(not(nullValue())));

            XlsDefaultValue defaultValueANno = select(nameAnnos, XlsDefaultValue.class);
            assertThat(defaultValueANno.value(), is("－"));

        }

        XlsOrder hintAnno1 = select(nameAnnos, XlsOrder.class);
        assertThat(hintAnno1.value(), is(1));

        // メソッド定義の読み込み
        Annotation[] recordsAnnos = reader.getAnnotations(OrverrideSheet.class.getDeclaredMethod("setRecords", List.class));

        // メソッド - XMLに定義している
        XlsHorizontalRecords horizontalRecordsAnno = select(recordsAnnos, XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));

        // メソッド - XMLに定義していない
        XlsOrder hintAnno2 = select(recordsAnnos, XlsOrder.class);
        assertThat(hintAnno2.value(), is(2));
    }

    /**
     * InputStreamを取得するためのサンプル。
     */
    @Test
    public void test_sample() throws Exception {

        InputStream xmlIn = createXml()
                .classInfo(createClass(SimpleSheet.class)
                        .override(true)
                        .annotation(createAnnotation(XlsSheet.class)
                                .attribute("name", "サンプル")
                                .buildAnnotation())
                        .buildClass())
                .buildXml()
                .toInputStream();

        assertThat(xmlIn, is(not(nullValue())));
        xmlIn.close();

//        System.out.println(xmlInfo.toXml());

    }

    /**
     * XMLの書き込みテスト
     */
    @Test
    public void test_xml_io() throws Exception {

        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(SimpleSheet.class)
                        .annotation(createAnnotation(XlsSheet.class)
                                .attribute("name", "単純なシート")
                                .buildAnnotation())
                        .field(createField("sheetName")
                                .annotation(createAnnotation(XlsSheetName.class)
                                        .buildAnnotation())
                                .buildField())
                        .field(createField("name")
                                .annotation(createAnnotation(XlsLabelledCell.class)
                                        .attribute("label", "名称")
                                        .attribute("type", LabelledCellType.Right)
                                        .buildAnnotation())
                                .annotation(createAnnotation(XlsConverter.class)
                                        .attribute("trim", true)
                                        .attribute("defaultValue", "－")
                                        .buildAnnotation())
                                .buildField())
                        .method(createMethod("setRecords")
                                .annotation(createAnnotation(XlsHorizontalRecords.class)
                                        .attribute("tableLabel", "名簿一覧")
                                        .attribute("terminal", RecordTerminal.Border)
                                        .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();


        File file = new File(OUT_DIR, "anno_test.xml");
        XmlIO.save(xmlInfo, file, "Windows-31j");

        AnnotationMappingInfo readInfo = XmlIO.load(file, "Windows-31j");
        assertThat(readInfo, is(not(nullValue())));

    }

    /**
     * 同じクラス名を追加した時のテスト
     */
    @Test
    public void test_XmlInfo_class_duplicate() {

        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(SimpleSheet.class)
                        .field(createField("field1").buildField())
                        .buildClass())
                .classInfo(createClass(SimpleSheet.class)
                        .field(createField("field2").buildField())
                        .buildClass())
                .buildXml();

        assertThat(xmlInfo.getClassInfos(), hasSize(1));

        assertThat(xmlInfo.containsClassInfo(SimpleSheet.class.getName()), is(true));
        assertThat(xmlInfo.containsClassInfo(OrverrideSheet.class.getName()), is(false));

        ClassInfo classInfo = xmlInfo.getClassInfo(SimpleSheet.class.getName());
        assertThat(classInfo.getFieldInfo("field1"), is(nullValue()));
        assertThat(classInfo.getFieldInfo("field2"), is(not(nullValue())));

    }

    /**
     * 同じアノテーションを追加した時のテスト
     */
    @Test
    public void test_ClassInfo_annotation_duplicate() {

        ClassInfo classInfo = createClass(SimpleSheet.class)
                .annotation(createAnnotation(XlsSheet.class)
                        .attribute("name", "Sheet1")
                        .buildAnnotation())
                .annotation(createAnnotation(XlsSheet.class)
                        .attribute("regex", "Sheet.+")
                        .buildAnnotation())
                .buildClass();

        assertThat(classInfo.getAnnotationInfos(), hasSize(1));

        assertThat(classInfo.containsAnnotationInfo(XlsSheet.class.getName()), is(true));
        assertThat(classInfo.containsAnnotationInfo(XlsSheetName.class.getName()), is(false));

        AnnotationInfo annoInfo = classInfo.getAnnotationInfo(XlsSheet.class.getName());
        assertThat(annoInfo.getAttribute("name"), is(nullValue()));
        assertThat(annoInfo.getAttribute("regex"), is(not(nullValue())));

    }

    /**
     * 同じフィールドを追加した時のテスト
     */
    @Test
    public void test_ClassInfo_field_duplicate() {

        ClassInfo classInfo = createClass(SimpleSheet.class)
                .field(createField("field1")
                        .annotation(createAnnotation(XlsLabelledCell.class)
                            .attribute("label", "クラス名")
                            .buildAnnotation())
                        .buildField())
                .field(createField("field1")
                        .annotation(createAnnotation(XlsHorizontalRecords.class)
                            .attribute("tableLabel", "名簿一覧")
                            .buildAnnotation())
                        .buildField())
                .buildClass();

        assertThat(classInfo.getFieldInfos(), hasSize(1));

        assertThat(classInfo.containsFieldInfo("field1"), is(true));
        assertThat(classInfo.containsFieldInfo("field2"), is(false));

        FieldInfo fieldInfo = classInfo.getFieldInfo("field1");
        assertThat(fieldInfo.containsAnnotationInfo(XlsLabelledCell.class.getName()), is(false));
        assertThat(fieldInfo.containsAnnotationInfo(XlsHorizontalRecords.class.getName()), is(true));

    }

    /**
     * 同じメソッドを追加した時のテスト
     */
    @Test
    public void test_ClassInfo_method_duplicate() {

        ClassInfo classInfo = createClass(SimpleSheet.class)
                .method(createMethod("method1")
                        .annotation(createAnnotation(XlsLabelledCell.class)
                            .attribute("label", "クラス名")
                            .buildAnnotation())
                        .buildMethod())
                .method(createMethod("method1")
                        .annotation(createAnnotation(XlsHorizontalRecords.class)
                            .attribute("tableLabel", "名簿一覧")
                            .buildAnnotation())
                        .buildMethod())
                .buildClass();

        assertThat(classInfo.getMethodInfos(), hasSize(1));

        assertThat(classInfo.containsMethodInfo("method1"), is(true));
        assertThat(classInfo.containsMethodInfo("method2"), is(false));

        MethodInfo fieldInfo = classInfo.getMethodInfo("method1");
        assertThat(fieldInfo.containsAnnotationInfo(XlsLabelledCell.class.getName()), is(false));
        assertThat(fieldInfo.containsAnnotationInfo(XlsHorizontalRecords.class.getName()), is(true));

    }

    /**
     * 同じアノテーションを追加した時のテスト
     */
    @Test
    public void test_FieldInfo_annotation_duplicate() {

        FieldInfo fieldInfo = createField("field1")
                .annotation(createAnnotation(XlsLabelledCell.class)
                        .attribute("label", "クラス名")
                        .buildAnnotation())
                .annotation(createAnnotation(XlsLabelledCell.class)
                        .attribute("type", LabelledCellType.Bottom)
                        .buildAnnotation())
                .buildField();

        assertThat(fieldInfo.getAnnotationInfos(), hasSize(1));

        assertThat(fieldInfo.containsAnnotationInfo(XlsLabelledCell.class.getName()), is(true));
        assertThat(fieldInfo.containsAnnotationInfo(XlsCell.class.getName()), is(false));

        AnnotationInfo annoInfo = fieldInfo.getAnnotationInfo(XlsLabelledCell.class.getName());
        assertThat(annoInfo.getAttribute("label"), is(nullValue()));
        assertThat(annoInfo.getAttribute("type"), is(not(nullValue())));


    }

    /**
     * 同じアノテーションを追加した時のテスト
     */
    @Test
    public void test_MethodInfo_annotation_duplicate() {

        MethodInfo methodInfo = createMethod("method1")
                .annotation(createAnnotation(XlsLabelledCell.class)
                        .attribute("label", "クラス名")
                        .buildAnnotation())
                .annotation(createAnnotation(XlsLabelledCell.class)
                        .attribute("type", LabelledCellType.Bottom)
                        .buildAnnotation())
                .buildMethod();

        assertThat(methodInfo.getAnnotationInfos(), hasSize(1));

        assertThat(methodInfo.containsAnnotationInfo(XlsLabelledCell.class.getName()), is(true));
        assertThat(methodInfo.containsAnnotationInfo(XlsCell.class.getName()), is(false));

        AnnotationInfo annoInfo = methodInfo.getAnnotationInfo(XlsLabelledCell.class.getName());
        assertThat(annoInfo.getAttribute("label"), is(nullValue()));
        assertThat(annoInfo.getAttribute("type"), is(not(nullValue())));


    }

    /**
     * アノテーションの同じ属性を追加したときのテスト
     */
    @Test
    public void test_AnnotationInfo_attribute_duplicatte() {

        AnnotationInfo annoInfo = createAnnotation(XlsLabelledCell.class)
                .attribute("label", "test1")
                .attribute("label", "test2")
                .buildAnnotation();


        assertThat(annoInfo.getAttributeInfos(), hasSize(1));

        assertThat(annoInfo.containsAttribute("label"), is(true));
        assertThat(annoInfo.containsAttribute("type"), is(false));

        assertThat(annoInfo.getAttribute("label"), is("\"test2\""));


    }

    private <A extends Annotation> A select(Annotation[] annos, Class<A> clazz) {

        for(Annotation anno : annos) {
            if(anno.annotationType().equals(clazz)) {
                return (A) anno;
            }
        }

        return null;

    }

    /**
     * アノテーション定義がない単純シート
     *
     */
    private static class SimpleSheet {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        private String sheetName;

        private String name;

        private List<NormalRecord> records;

        public List<NormalRecord> getRecords() {
            return records;
        }

        public void setRecords(List<NormalRecord> records) {
            this.records = records;
        }

    }

    /**
     * アノテーション定義の上書き
     */
    @XlsSheet(name="テスト")
    private static class OrverrideSheet {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        @XlsSheetName
        private String sheetName;

        @XlsOrder(1)
        @XlsTrim
        @XlsCellOption(shrinkToFit=true)
        @XlsDefaultValue("－")
        @XlsLabelledCell(label="名称", type=LabelledCellType.Right)
        private String name;

        private List<NormalRecord> records;

        public List<NormalRecord> getRecords() {
            return records;
        }

        @XlsOrder(2)
        @XlsHorizontalRecords(tableLabel="クラス名", terminal=RecordTerminal.Empty)
        public void setRecords(List<NormalRecord> records) {
            this.records = records;
        }

    }

    private static class NormalRecord {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        private int no;

        private String name;

        private Date updateTime;

    }
}
