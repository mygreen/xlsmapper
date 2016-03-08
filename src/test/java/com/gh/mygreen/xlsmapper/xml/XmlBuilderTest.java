package com.gh.mygreen.xlsmapper.xml;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;
import java.util.Map;



import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;


import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHint;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.xml.bind.XmlInfo;

/**
 * {@link XmlBuilder}のテスタ
 *
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class XmlBuilderTest {
    
    /**
     * アノテーションがないクラスに設定する。
     */
    @Test
    public void test_simple() throws Exception {
        
        XmlInfo xmlInfo = createXml()
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
     * @throws Exception
     */
    @Test
    public void test_override() throws Exception {
        
        XmlInfo xmlInfo = createXml()
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
        
//        System.out.println(xmlInfo.toXml());
        
    }
    
    /**
     * XMLの書き込みテスト
     */
    @Test
    public void test_xml_io() throws Exception {
        
        XmlInfo xmlInfo = createXml()
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
        
        
        File file = new File("src/test/out/anno_test.xml");
        XmlIO.save(xmlInfo, file, "Windows-31j");
        
        XmlInfo readInfo = XmlIO.load(file, "Windows-31j");
        assertThat(readInfo, is(not(nullValue())));
        
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
