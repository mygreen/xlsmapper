package com.gh.mygreen.xlsmapper;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsFieldProcessor;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldProcessor;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * {@link XlsMapper}のテスタ。
 * 主にエラー系のテストを行う。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class XlsMapperTest {
    
    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }
    
    /**
     * Excelファイルでない不正なファイル
     */
    private File noExecelFile = new File("src/test/data/no_excel.xlsx");
    
    /**
     * 読み込み用のファイルの定義
     */
    private File inputFile = new File("src/test/data/mapper.xlsx");
    
    /**
     * 出力用のテンプレートファイルの定義
     */
    private File templateFile = new File("src/test/data/mapper.xlsx");
    
    /**
     * 出力用のファイル名の定義
     */
    private String outFilename = "mapper_out.xlsx";
    
    private XlsMapper mapper;
    
    @Before
    public void setUp() throws Exception {
        this.mapper = new XlsMapper();
    }
    
    /**
     * 不正なファイルの場合
     */
    @Test
    public void testLoad_notSupportFileType() throws Exception {
        
        try(InputStream in = new FileInputStream(noExecelFile)) {
            assertThatThrownBy(() -> mapper.load(in, SampleSheet.class))
                .isInstanceOf(XlsMapperException.class)
                .hasMessageContaining("サポートしていないExcelファイルの形式のため読み込みに失敗しました。");
            
        }
        
    }
    
    /**
     * 不正なファイルの場合
     */
    @Test
    public void testLoadMultilple_notSupportFileType() throws Exception {
        
        try(InputStream in = new FileInputStream(noExecelFile)) {
            assertThatThrownBy(() -> mapper.loadMultiple(in, SampleSheet.class))
                .isInstanceOf(XlsMapperException.class)
                .hasMessageContaining("サポートしていないExcelファイルの形式のため読み込みに失敗しました。");
            
        }
        
    }
    
    /**
     * 不正なファイルの場合
     */
    @Test
    public void testLoadMultilple_notSupportFileType2() throws Exception {
        
        try(InputStream in = new FileInputStream(noExecelFile)) {
            assertThatThrownBy(() -> mapper.loadMultiple(in, new Class[]{SampleSheet.class}))
                .isInstanceOf(XlsMapperException.class)
                .hasMessageContaining("サポートしていないExcelファイルの形式のため読み込みに失敗しました。");
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - フィールドに付与
     */
    @Test
    public void testLoad_customProcessorAnno_withField() throws Exception {
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .field(createField("customProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<CustomAnnoSheet> errors = mapper.loadDetail(in, CustomAnnoSheet.class);
            
            CustomAnnoSheet sheet = errors.getTarget();
            
            assertThat(sheet.customProcessor).isEqualTo("カスタムアノテーション");
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - フィールドに付与 - プロセッサーの指定が無い
     */
    @Test
    public void testLoad_customProcessorAnno_withField_noProcessor() throws Exception {
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .field(createField("customProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno2.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            
            assertThatThrownBy(() ->  mapper.loadDetail(in, CustomAnnoSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessageContaining("アノテーション '@CustomProcessorAnno2' に対するFieldProcessorが解決できませんでした。");
            
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - setterメソッドに付与
     */
    @Test
    public void testLoad_customProcessorAnno_withSetter() throws Exception {
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .method(createMethod("setCustomProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            SheetBindingErrors<CustomAnnoSheet> errors = mapper.loadDetail(in, CustomAnnoSheet.class);
            
            CustomAnnoSheet sheet = errors.getTarget();
            
            assertThat(sheet.customProcessor).isEqualTo("カスタムアノテーション");
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - setterメソッドに付与 - プロセッサーの指定が無い
     */
    @Test
    public void testLoad_customProcessorAnno_withSetter_noProcessor() throws Exception {
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .method(createMethod("setCustomProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno2.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        try(InputStream in = new FileInputStream(inputFile)) {
            
            
            assertThatThrownBy(() ->  mapper.loadDetail(in, CustomAnnoSheet.class))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessageContaining("アノテーション '@CustomProcessorAnno2' に対するFieldProcessorが解決できませんでした。");
            
            
        }
        
    }
    
    /**
     * 不正なファイルの場合
     */
    @Test
    public void testSave_notSupportFileType() throws Exception {
        
        SampleSheet outSheet = new SampleSheet();
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(noExecelFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            assertThatThrownBy(() -> mapper.save(template, out, outSheet))
                .isInstanceOf(XlsMapperException.class)
                .hasMessageContaining("サポートしていないExcelファイルの形式のためテンプレートファイルの読み込みに失敗しました。");
            
        }
        
    }
    
    /**
     * 不正なファイルの場合
     */
    @Test
    public void testSaveMultiple_notSupportFileType() throws Exception {
        
        SampleSheet outSheet = new SampleSheet();
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(noExecelFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            assertThatThrownBy(() -> mapper.saveMultiple(template, out, new Object[]{outSheet}))
                .isInstanceOf(XlsMapperException.class)
                .hasMessageContaining("サポートしていないExcelファイルの形式のためテンプレートファイルの読み込みに失敗しました。");
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - フィールドに付与
     */
    @Test
    public void testSave_customProcessorAnno_withField() throws Exception {
        
        CustomAnnoSheet outSheet = new CustomAnnoSheet();
        outSheet.setCustomProcessor("カスタムプロセッサー（書き込み）");
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .field(createField("customProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<CustomAnnoSheet> errors = mapper.loadDetail(in, CustomAnnoSheet.class);
            
            CustomAnnoSheet sheet = errors.getTarget();
            
            assertThat(sheet.customProcessor).isEqualTo(outSheet.customProcessor);
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - フィールドに付与 - プロセッサーの指定が無い
     */
    @Test
    public void testSave_customProcessorAnno_withField_noProcessor() throws Exception {
        
        CustomAnnoSheet outSheet = new CustomAnnoSheet();
        outSheet.setCustomProcessor("カスタムプロセッサー（書き込み）");
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .field(createField("customProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno2.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildField())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            assertThatThrownBy(() ->  mapper.save(template, out, outSheet))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessageContaining("アノテーション '@CustomProcessorAnno2' に対するFieldProcessorが解決できませんでした。");
        }
        
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - getterメソッドに付与
     */
    @Test
    public void testSave_customProcessorAnno_withGetter() throws Exception {
        
        CustomAnnoSheet outSheet = new CustomAnnoSheet();
        outSheet.setCustomProcessor("カスタムプロセッサー（書き込み）");
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .method(createMethod("getCustomProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            mapper.save(template, out, outSheet);
        }
        
        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {
            SheetBindingErrors<CustomAnnoSheet> errors = mapper.loadDetail(in, CustomAnnoSheet.class);
            
            CustomAnnoSheet sheet = errors.getTarget();
            
            assertThat(sheet.customProcessor).isEqualTo(outSheet.customProcessor);
            
        }
        
    }
    
    /**
     * 独自のアノテーションによるマッピング - getterメソッドに付与 - プロセッサーの指定が無い
     */
    @Test
    public void testSave_customProcessorAnno_withGetter_noProcessor() throws Exception {
        
        CustomAnnoSheet outSheet = new CustomAnnoSheet();
        outSheet.setCustomProcessor("カスタムプロセッサー（書き込み）");
        
        AnnotationMappingInfo xmlInfo = createXml()
                .classInfo(createClass(CustomAnnoSheet.class)
                        .method(createMethod("getCustomProcessor")
                                .annotation(createAnnotation(CustomProcessorAnno2.class)
                                        .attribute("address", "B3")
                                    .buildAnnotation())
                                .buildMethod())
                        .buildClass())
                .buildXml();
        
        mapper.getConfiguration().setAnnotationMapping(xmlInfo);
        
        File outFile = new File(OUT_DIR, outFilename);
        try(InputStream template = new FileInputStream(templateFile);
                OutputStream out = new FileOutputStream(outFile)) {
            
            assertThatThrownBy(() ->  mapper.save(template, out, outSheet))
                .isInstanceOf(AnnotationInvalidException.class)
                .hasMessageContaining("アノテーション '@CustomProcessorAnno2' に対するFieldProcessorが解決できませんでした。");
        }
        
        
    }
    
    /**
     * アノテーション{@literal @XlsSheet}の付与がない
     *
     */
    private static class NoGrandSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    @XlsSheet(name="テスト")
    private static class SampleSheet {
        
        @XlsSheetName
        private String sheetName;
        
    }
    
    /**
     * 独自のアノテーションのマッピング
     *
     */
    @XlsSheet(name="テスト")
    private static class CustomAnnoSheet {
        
        @XlsSheetName
        private String sheetName;
        
        private String customProcessor;
        
        public String getCustomProcessor() {
            return customProcessor;
        }
        
        public void setCustomProcessor(String customProcessor) {
            this.customProcessor = customProcessor;
        }
    
    }
    
    /**
     * 独自のマッピングアノテーション
     *
     */
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @XlsFieldProcessor(value={CustomFieldProcessor.class})
    private static @interface CustomProcessorAnno {
        
        String address();
        
    }
    
    /**
     * 独自のマッピングアノテーション - Processorの指定が無い
     *
     */
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @XlsFieldProcessor(value={})
    private static @interface CustomProcessorAnno2 {
        
        String address();
    }
    
    private static class CustomFieldProcessor implements FieldProcessor<CustomProcessorAnno>{
        
        @Override
        public void loadProcess(Sheet sheet, Object beansObj, CustomProcessorAnno anno,
                FieldAccessor accessor, Configuration config, LoadingWorkObject work)
                throws XlsMapperException {
            
            Cell cell = POIUtils.getCell(sheet, CellPosition.of(anno.address()));
            String value = POIUtils.getCellContents(cell, config.getCellFormatter());
            accessor.setValue(beansObj, value);
            
        }

        @Override
        public void saveProcess(Sheet sheet, Object beansObj, CustomProcessorAnno anno,
                FieldAccessor accessor, Configuration config, SavingWorkObject work)
                throws XlsMapperException {
            
            String value = (String)accessor.getValue(beansObj);
            Cell cell = POIUtils.getCell(sheet, CellPosition.of(anno.address()));
            cell.setCellValue(value);
        }
        
    }
}
