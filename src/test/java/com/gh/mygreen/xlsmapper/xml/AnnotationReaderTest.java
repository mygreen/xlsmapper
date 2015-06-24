package com.gh.mygreen.xlsmapper.xml;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;

/**
 * {@link XmlLoader}、{@link AnnotationReader}のテスタ
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
        
        XmlInfo xmlInfo = XmlLoader.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
        assertThat(xmlInfo, is(not(nullValue())));
    }
    
    /**
     * XMLの読み込みテスト - ストリーム指定
     */
    @Test
    public void test_loadXml_success2() throws Exception {
        
        XmlInfo xmlInfo = XmlLoader.load(AnnotationReaderTest.class.getResourceAsStream("anno_resource.xml"));
        assertThat(xmlInfo, is(not(nullValue())));
    }
    
    /**
     * XMLの読み込みテスト - ファイルが存在しない
     */
    @Test(expected=XmlLoadException.class)
    public void test_loadXml_error_notFile() throws Exception {
        
        XmlInfo xmlInfo = XmlLoader.load(new File("src/test/data/xml/anno_test_notExist.xml"), "UTF-8");
        fail();
    }
    
    /**
     * XMLの読み込みテスト - XMLが不正
     */
    @Test(expected=XmlLoadException.class)
    public void test_loadXml_error_wrongFile() throws Exception {
        
        XmlInfo xmlInfo = XmlLoader.load(new File("src/test/data/xml/anno_test_wrong.xml"), "UTF-8");
        fail();
    }
    
    /**
     * XMLに定義されている単純な読み込み
     * @throws Exception
     */
    @Test
    public void test_readAnnotation_simple() throws Exception {
        
        XmlInfo xmlInfo = XmlLoader.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
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
     */
    @Test
    public void test_readAnnotation_override() throws Exception {
        
        XmlInfo xmlInfo = XmlLoader.load(new File("src/test/data/xml/anno_test.xml"), "UTF-8");
        AnnotationReader reader = new AnnotationReader(xmlInfo);
        
        // クラス定義の読み込み
        XlsSheet sheetAnno = reader.getAnnotation(OrverrideSheet.class, XlsSheet.class);
        assertThat(sheetAnno.name(), is(""));
        assertThat(sheetAnno.regex(), is("リスト.+"));
        
        // フィールド定義の読み込み
        Annotation[] nameAnnos = reader.getAnnotations(OrverrideSheet.class, SimpleSheet.class.getDeclaredField("name"));
        
        XlsLabelledCell labeldCellAnno = select(nameAnnos, XlsLabelledCell.class);
        assertThat(labeldCellAnno.label(), is("クラス名"));
        assertThat(labeldCellAnno.type(), is(LabelledCellType.Bottom));
        
        // XMLに定義していない
        XlsConverter converterAnno = select(nameAnnos, XlsConverter.class);
        assertThat(converterAnno, is(nullValue()));
        
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
        
        @XlsConverter(trim=true, forceShrinkToFit=true, defaultValue="－")
        @XlsLabelledCell(label="名称", type=LabelledCellType.Right)
        private String name;
        
        private List<NormalRecord> records;
        
        public List<NormalRecord> getRecords() {
            return records;
        }
        
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
