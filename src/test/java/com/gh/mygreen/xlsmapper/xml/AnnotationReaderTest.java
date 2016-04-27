package com.gh.mygreen.xlsmapper.xml;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHint;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;
import com.gh.mygreen.xlsmapper.xml.bind.ClassInfo;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;

/**
 * {@link XmlIO}、{@link AnnotationReader}のテスタ
 * @version 1.0
 * 
 */
public class AnnotationReaderTest {
    
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
     * XMLの読み込みテスト - 文字コード指定
     */
    @Test
    public void test_loadXml_success1() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
        assertThat(xmlInfo, is(not(nullValue())));
    }
    
    /**
     * XMLの読み込みテスト - ストリーム指定
     */
    @Test
    public void test_loadXml_success2() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(AnnotationReaderTest.class.getResourceAsStream("anno_resource.xml"));
        assertThat(xmlInfo, is(not(nullValue())));
    }
    
    /**
     * XMLの読み込みテスト - ファイルが存在しない
     */
    @Test(expected=XmlOperateException.class)
    public void test_loadXml_error_notFile() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_test_notExist.xml"), "UTF-8");
        fail();
    }
    
    /**
     * XMLの読み込みテスト - XMLが不正
     */
    @Test(expected=XmlOperateException.class)
    public void test_loadXml_error_wrongFile() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_test_wrong.xml"), "UTF-8");
        fail();
    }
    
    /**
     * XMLに定義されている単純な読み込み
     * @throws Exception
     */
    @Test
    public void test_readAnnotation_simple() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
        AnnotationReader reader = new AnnotationReader(xmlInfo);
        
        // クラス定義の読み込み
        Annotation[] classAnnos = reader.getAnnotations(SimpleSheet.class);
        XlsSheet sheetAnno = select(classAnnos, XlsSheet.class);
        assertThat(sheetAnno.name(), is("単純なシート"));
        
        // クラス定義の読み込み（アノテーションを指定）
        sheetAnno = reader.getAnnotation(SimpleSheet.class, XlsSheet.class);
        assertThat(sheetAnno.name(), is("単純なシート"));
        
        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(SimpleSheet.class, SimpleSheet.class.getDeclaredField("name"));
        
        XlsLabelledCell labeldCellAnno = select(nameAnnos, XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("名称"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Right));
        
        XlsConverter converterAnno = select(nameAnnos, XlsConverter.class);
        assertThat(converterAnno.trim(), is(true));
        assertThat(converterAnno.defaultValue(), is("－"));
        
        // フィールド定義の読み込み（アノテーションを指定）
        labeldCellAnno = reader.getAnnotation(SimpleSheet.class, SimpleSheet.class.getDeclaredField("name"), XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("名称"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Right));
        
        // メソッドの定義の読み込み
        Annotation[] recordsAnnos = reader.getAnnotations(SimpleSheet.class,
                SimpleSheet.class.getDeclaredMethod("setRecords", List.class));
        
        XlsHorizontalRecords horizontalRecordsAnno = select(recordsAnnos, XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));
        
        // メソッドの定義の読み込み（アノテーションを指定）
        horizontalRecordsAnno = reader.getAnnotation(SimpleSheet.class,
                SimpleSheet.class.getDeclaredMethod("setRecords", List.class), XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));
    }
    
    /**
     * クラスに定義されている定義の上書き
     * ・XML中のoverride属性がfalseの場合。
     */
    @Test
    public void test_readAnnotation_override() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
        AnnotationReader reader = new AnnotationReader(xmlInfo);
        
        // クラス定義の読み込み
        XlsSheet sheetAnno = reader.getAnnotation(OrverrideSheet.class, XlsSheet.class);
        assertThat(sheetAnno.name(), is(""));
        assertThat(sheetAnno.regex(), is("リスト.+"));
        
        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(OrverrideSheet.class, OrverrideSheet.class.getDeclaredField("name"));
        
        // フィールド - XMLに定義している
        XlsLabelledCell labeldCellAnno = select(nameAnnos, XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("クラス名"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Bottom));
        
        // フィールド - XMLに定義していない
        XlsConverter converterAnno = select(nameAnnos, XlsConverter.class);
        assertThat(converterAnno, is(nullValue()));
        
        XlsHint hintAnno1 = select(nameAnnos, XlsHint.class);
        assertThat(hintAnno1, is(nullValue()));
        
        // メソッド定義の読み込み
        Annotation[] recordsAnnos = reader.getAnnotations(OrverrideSheet.class, OrverrideSheet.class.getDeclaredMethod("setRecords", List.class));
        
        // メソッド - XMLに定義している
        XlsHorizontalRecords horizontalRecordsAnno = select(recordsAnnos, XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));
        
        // メソッド - XMLに定義していない
        XlsHint hintAnno2 = select(recordsAnnos, XlsHint.class);
        assertThat(hintAnno2, is(nullValue()));
        
    }
    
    /**
     * クラスに定義されている定義の上書き
     * ・XML中のoverride属性がtrueの場合。
     */
    @Test
    public void test_readAnnotation_override2() throws Exception {
        
        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_override.xml"), "UTF-8");
        AnnotationReader reader = new AnnotationReader(xmlInfo);
        
        // クラス定義の読み込み
        XlsSheet sheetAnno = reader.getAnnotation(OrverrideSheet.class, XlsSheet.class);
        assertThat(sheetAnno.name(), is(""));
        assertThat(sheetAnno.regex(), is("リスト.+"));
        
        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(OrverrideSheet.class, OrverrideSheet.class.getDeclaredField("name"));
        
        // フィールド - XMLに定義している
        XlsLabelledCell labeldCellAnno = select(nameAnnos, XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("クラス名"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Bottom));
        
        // フィールド - XMLに定義していない
        XlsConverter converterAnno = select(nameAnnos, XlsConverter.class);
        assertThat(converterAnno.trim(), is(true));
        assertThat(converterAnno.shrinkToFit(), is(true));
        assertThat(converterAnno.defaultValue(), is("－"));
        
        XlsHint hintAnno1 = select(nameAnnos, XlsHint.class);
        assertThat(hintAnno1.order(), is(1));
        
        // メソッド定義の読み込み
        Annotation[] recordsAnnos = reader.getAnnotations(OrverrideSheet.class, OrverrideSheet.class.getDeclaredMethod("setRecords", List.class));
        
        // メソッド - XMLに定義している
        XlsHorizontalRecords horizontalRecordsAnno = select(recordsAnnos, XlsHorizontalRecords.class);
        assertThat(horizontalRecordsAnno.tableLabel(), is("名簿一覧"));
        assertThat(horizontalRecordsAnno.terminal(), is(RecordTerminal.Border));
        
        // メソッド - XMLに定義していない
        XlsHint hintAnno2 = select(recordsAnnos, XlsHint.class);
        assertThat(hintAnno2.order(), is(2));
        
    }
    
    /**
     * クラスに定義されている定義の上書き
     * ・XML中のoverride属性がtrueの場合。
     * ・ただし、XMLはJavaオブジェクトから逆生成する。
     * @throws Exception
     */
    @Test
    public void test_readAnnotation_ovverride_dynamicXml() throws Exception {
        
        XmlInfo xmlInfo = new XmlInfo();
        
        // クラス情報の組み立て
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName(OrverrideSheet.class.getName());
        classInfo.setOverride(true);
        
        xmlInfo.addClassInfo(classInfo);
        
        // XlsSheetアノテーションの組み立て
        AnnotationInfo sheetAnnoInfo = new AnnotationInfo();
        sheetAnnoInfo.setClassName(XlsSheet.class.getName());
        sheetAnnoInfo.addAttribute("name", "書き換えたシート");
        classInfo.addAnnotationInfo(sheetAnnoInfo);
        
        
        StringWriter writer = new StringWriter();
        JAXB.marshal(classInfo, writer);
        
        System.out.println(writer.toString());
        
    }

    /**
     * XMLにもクラスにも定義されていない定義を取得しようとすると<code>null</code>が戻る。
     * @throws Exception
     */
    @Test
    public void test_readAnnotation_not_exists() throws Exception {

        XmlInfo xmlInfo = XmlIO.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
        AnnotationReader reader = new AnnotationReader(xmlInfo);
        // クラス定義の読み込み
        Annotation[] classAnnos = reader.getAnnotations(SimpleSheet.class);
        Deprecated notExists = select(classAnnos, Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // クラス定義の読み込み（アノテーションを指定）
        notExists = reader.getAnnotation(SimpleSheet.class, Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(SimpleSheet.class, SimpleSheet.class.getDeclaredField("name"));

        notExists = select(nameAnnos, Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // フィールド定義の読み込み（アノテーションを指定）
        notExists = reader.getAnnotation(SimpleSheet.class, SimpleSheet.class.getDeclaredField("name"), Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // クラスに存在しないフィールド定義の読み込み
        notExists = reader.getAnnotation(SimpleSheet.class, AnnotationReader.class.getDeclaredField("xmlInfo"), Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // メソッドの定義の読み込み
        Annotation[] recordsAnnos = reader.getAnnotations(SimpleSheet.class,
                SimpleSheet.class.getDeclaredMethod("setRecords", List.class));

        notExists = select(recordsAnnos, Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // メソッドの定義の読み込み（アノテーションを指定）
        notExists = reader.getAnnotation(SimpleSheet.class,
                SimpleSheet.class.getDeclaredMethod("setRecords", List.class), Deprecated.class);
        assertThat(notExists, is(nullValue()));

        // クラスに存在しないメソッド定義の読み込み
        notExists = reader.getAnnotation(SimpleSheet.class,
                AnnotationReader.class.getDeclaredMethod("getAnnotationBuilder"), Deprecated.class);
        assertThat(notExists, is(nullValue()));

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
        
        @XlsHint(order=1)
        @XlsConverter(trim=true, shrinkToFit=true, defaultValue="－")
        @XlsLabelledCell(label="名称", type=LabelledCellType.Right)
        private String name;
        
        private List<NormalRecord> records;
        
        public List<NormalRecord> getRecords() {
            return records;
        }
        
        @XlsHint(order=2)
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
