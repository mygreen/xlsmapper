package com.gh.mygreen.xlsmapper.xml;

import java.lang.annotation.Annotation;

import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;
import com.gh.mygreen.xlsmapper.xml.bind.ClassInfo;
import com.gh.mygreen.xlsmapper.xml.bind.FieldInfo;
import com.gh.mygreen.xlsmapper.xml.bind.MethodInfo;

/**
 * アノテーション用のXMLのオブジェクト（{@link AnnotationMappingInfo}）を組み立てるためのヘルパークラス。
 *
 * <pre class="highlight"><code class="java">
 * // static import をすると使いやすくなります。
 * import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;
 *
 * public void sample() {
 *     AnnotationMappingInfo annotationMapping = createXml()
 *               .classInfo(createClass(SimpleSheet.class)
 *                       .annotation(createAnnotationBuilder(XlsSheet.class) // クラスに対するアノテーションの定義
 *                               .attribute("name", "単純なシート")
 *                               .buildAnnotation())
 *                       .field(createField("sheetName") // フィールドに対するアノテーションの定義
 *                               .annotation(createAnnotation(XlsSheetName.class)
 *                                       .buildAnnotation())
 *                               .buildField())
 *                       .field(createField("name")
 *                               .annotation(createAnnotation(XlsLabelledCell.class)
 *                                       .attribute("label", "名称")
 *                                       .attribute("type", LabelledCellType.Right)
 *                                       .buildAnnotation())
 *                               .annotation(createAnnotation(XlsTrim.class)
 *                                       .buildAnnotation())
 *                               .annotation(createAnnotation(XlsDefaultValue.class)
 *                                       .attribute("value", "ー")
 *                                       .buildAnnotation())
 *                               .buildField())
 *                       .method(createMethod("setRecords") // メソッドに対するアノテーションの定義
 *                               .annotation(createAnnotation(XlsHorizontalRecords.class)
 *                                       .attribute("tableLabel", "名簿一覧")
 *                                       .attribute("terminal", RecordTerminal.Border)
 *                                       .buildAnnotation())
 *                               .buildMethod())
 *                       .buildClass())
 *               .buildXml();
 *
 *     // ファイルへの保存
 *     XmlIO.save(annotationMapping, new File("anno_simple.xml"), "UTF-8");
 *
 *     // システム設定へ渡す。
 *     XlsMapper xlsMapper = new XlsMapper();
 *     xlsMapper.getConfiguration.setAnnotationMapping(annotaionMapping);
 *
 * }
 * </code></pre>
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class XmlBuilder {

    /**
     * JavaオブジェクトをOGNL式に変換するためのクラス。
     */
    private static OgnlValueFormatter valueFormatter = new OgnlValueFormatter();

    /**
     * {@link AnnotationMappingInfo}のビルダクラスの{@link AnnotationMappingInfo.Builder}インスタンスを作成する。
     * @return
     */
    public static AnnotationMappingInfo.Builder createXml() {
        return AnnotationMappingInfo.builder();
    }

    /**
     * {@link ClassInfo}のビルダクラスの{@link ClassInfo.Builder}インスタンスを作成する。
     * @param clazz マッピング対象のJavaのクラス情報。
     * @return
     */
    public static ClassInfo.Builder createClass(final Class<?> clazz) {
        return ClassInfo.builder().name(clazz);
    }

    /**
     * {@link MethodInfo}のビルダクラスの{@link MethodInfo.Builder}インスタンスを作成する。
     * @param methodName メソッド名
     * @return
     */
    public static MethodInfo.Builder createMethod(final String methodName) {
        return MethodInfo.builder().name(methodName);
    }

    /**
     * {@link FieldInfo}のビルダクラスの{@link FieldInfo.Builder}インスタンスを作成する。
     * @param fieldName フィールド名
     * @return
     */
    public static FieldInfo.Builder createField(final String fieldName) {
        return FieldInfo.builder().name(fieldName);
    }

    /**
     * {@link AnnotationInfo}のビルダクラスの{@link AnnotationInfo.Builder}インスタンスを作成する。
     * <p>JavaオブジェクトをOGNL式に変換するクラス{@link OgnlValueFormatter}はデフォルトの物が使用される。
     *    独自のものを設定したい場合は、{@link #setValueFormatter(OgnlValueFormatter)}を予め呼び変更しておく必要がある。
     * @param clazz アノテーションのクラス
     * @return
     */
    public static AnnotationInfo.Builder createAnnotation(Class<? extends Annotation> clazz) {
        return AnnotationInfo.builder(valueFormatter).name(clazz);
    }

    /**
     * JavaオブジェクトをOGNL式に変換するためのクラスを取得する。
     * @return OGNL式のフォーマッタを返す。
     */
    public synchronized static OgnlValueFormatter getValueFormatter() {
        return valueFormatter;
    }

    /**
     * JavaオブジェクトをOGNL式に変換するためのクラスを設定する。
     *
     * @param valueFormatter
     */
    public synchronized static void setValueFormatter(final OgnlValueFormatter valueFormatter) {
        XmlBuilder.valueFormatter = valueFormatter;
    }

}
