package com.gh.mygreen.xlsmapper.cellconverter;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.gh.mygreen.xlsmapper.xml.XmlBuilder.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.xml.bind.AnnotationMappingInfo;

/**
 * 独自のセルの変換クラスのテスト
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CustomCellConverterTest {

    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }

    /**
     * エラーメッセージのコンバーター
     */
    private SheetErrorFormatter errorFormatter;

    @Before
    public void setUp() throws Exception {
        this.errorFormatter = new SheetErrorFormatter();
    }

    /**
     * 読み込みのテスト - Converterアノテーションによるマッピング
     */
    @Test
    public void testLoadWithConvertAnno() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<CustomWithAnnoSheet> errors = mapper.loadDetail(in, CustomWithAnnoSheet.class);
            CustomWithAnnoSheet sheet = errors.getTarget();

            assertThat(sheet.value1.title).isEqualTo("TODOリスト");
            assertThat(sheet.value1.items).containsExactly("部屋の片付け", "買い物");

            assertThat(sheet.value2).isNull();

        }

    }

    /**
     * 読み込みのテスト - ConverterRegisterによるマッピング
     */
    @Test
    public void testLoadWithConverRegistry() throws Exception {

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        mapper.getConfiguration().getConverterRegistry().registerConverter(CustomType.class, new CustomCellConverterFactory());

        try(InputStream in = new FileInputStream("src/test/data/convert.xlsx")) {
            SheetBindingErrors<CustomWithRegisterSheet> errors = mapper.loadDetail(in, CustomWithRegisterSheet.class);
            CustomWithRegisterSheet sheet = errors.getTarget();

            assertThat(sheet.value1.title).isEqualTo("TODOリスト");
            assertThat(sheet.value1.items).containsExactly("部屋の片付け", "買い物");

            assertThat(sheet.value2).isNull();

        }

    }

    /**
     *書き込みのテスト - Converterアノテーションによるマッピング
     */
    @Test
    public void testSaveWithConvertAnno() throws Exception {

        // テストデータの作成
        CustomWithAnnoSheet outSheet = new CustomWithAnnoSheet();
        {
            CustomType value = new CustomType();
            value.setTitle("TODOリスト");
            value.setItems(Arrays.asList("部屋の片付け", "買い物"));

            outSheet.value1 = value;
        }

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        // ファイルへの書き込み
        File outFile = new File(OUT_DIR, "convert_custom.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<CustomWithAnnoSheet> errors = mapper.loadDetail(in, CustomWithAnnoSheet.class);
            CustomWithAnnoSheet sheet = errors.getTarget();

            assertThat(sheet.value1.title).isEqualTo(outSheet.value1.title);
            assertThat(sheet.value1.items).containsExactlyElementsOf(outSheet.value1.items);

            assertThat(sheet.value2).isNull();

        }

    }

    /**
     *書き込みのテスト - ConverterRegisterによるマッピング
     */
    @Test
    public void testSaveWithConvertRegister() throws Exception {

        // テストデータの作成
        CustomWithAnnoSheet outSheet = new CustomWithAnnoSheet();
        {
            CustomType value = new CustomType();
            value.setTitle("TODOリスト");
            value.setItems(Arrays.asList("部屋の片付け", "買い物"));

            outSheet.value1 = value;
        }

        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);

        mapper.getConfiguration().getConverterRegistry().registerConverter(CustomType.class, new CustomCellConverterFactory());

        // ファイルへの書き込み
        File outFile = new File(OUT_DIR, "convert_custom.xlsx");
        try(InputStream template = new FileInputStream("src/test/data/convert_template.xlsx");
                OutputStream out = new FileOutputStream(outFile)) {

            mapper.save(template, out, outSheet);
        }

        // 書き込んだファイルを読み込み値の検証を行う。
        try(InputStream in = new FileInputStream(outFile)) {

            SheetBindingErrors<CustomWithAnnoSheet> errors = mapper.loadDetail(in, CustomWithAnnoSheet.class);
            CustomWithAnnoSheet sheet = errors.getTarget();

            assertThat(sheet.value1.title).isEqualTo(outSheet.value1.title);
            assertThat(sheet.value1.items).containsExactlyElementsOf(outSheet.value1.items);

            assertThat(sheet.value2).isNull();

        }

    }

    /**
     * XlsConverterアノテーションによる指定
     *
     */
    @XlsSheet(name="独自")
    private static class CustomWithAnnoSheet {

        @XlsConverter(CustomCellConverterFactory.class)
        @XlsLabelledCell(label="独自の書式1", type=LabelledCellType.Right)
        private CustomType value1;

        @XlsConverter(CustomCellConverterFactory.class)
        @XlsLabelledCell(label="独自の書式2", type=LabelledCellType.Right)
        private CustomType value2;

    }

    /**
     * Converterの登録による指定 - ConverterRegister
     *
     */
    @XlsSheet(name="独自")
    private static class CustomWithRegisterSheet {

        @XlsLabelledCell(label="独自の書式1", type=LabelledCellType.Right)
        private CustomType value1;

        @XlsLabelledCell(label="独自の書式2", type=LabelledCellType.Right)
        private CustomType value2;

    }

    public static class CustomType {

        /** タイトル */
        private String title;

        /** リスト */
        private List<String> items;

        /**
         * リストの要素を追加する
         * @param item リストの要素
         */
        public CustomType addItem(String item) {
            if(items == null) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);

            return this;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }

    }

    static class CustomCellConverterFactory extends CellConverterFactorySupport<CustomType>
            implements CellConverterFactory<CustomType> {

        @Override
        public CustomCellConverter create(FieldAccessor accessor, Configuration config) {
            final CustomCellConverter cellConverter = new CustomCellConverter(accessor, config);

            // トリムなどの共通の処理を設定する
            setupCellConverter(cellConverter, accessor, config);

            return cellConverter;
        }


        @Override
        protected void setupCustom(BaseCellConverter<CustomType> cellConverter, FieldAccessor field, Configuration config) {
            // 必要があれば実装する。
        }

        /**
         * {@link TextFormatter}のインスタンスを作成する。
         * @param field フィールド情報
         * @param config システム情報
         * @return {@link TextFormatter}のインスタンス
         */
        @Override
        protected TextFormatter<CustomType> createTextFormatter(FieldAccessor field, Configuration config) {

            return new TextFormatter<CustomType>() {

                @Override
                public CustomType parse(String text) throws TextParseException {

                    if(StringUtils.isEmpty(text)) {
                        return null;
                    }

                    // 改行で分割する
                    String[] split = text.split("\r\n|\n");

                    if(split.length <= 1) {
                        // 1行以下しかない場合は、例外とする
                        throw new TextParseException(text, CustomType.class);
                    }

                    CustomType data = new CustomType();
                    data.setTitle(split[0]);

                    for(int i=1; i < split.length; i++) {
                        String item = split[i];
                        if(item.startsWith("- ")) {
                            // リストの記号を削除する
                            item = item.substring(2);
                        }
                        data.addItem(item);
                    }

                    return data;
                }

                @Override
                public String format(CustomType value) {
                    if(value == null) {
                        return "";
                    }

                    StringBuilder text = new StringBuilder();
                    text.append(value.getTitle())
                        .append("\n");

                    // 先頭にハイフン('-')を付与して、改行で繋げる
                    text.append(value.getItems().stream()
                            .map(i -> "- " + i)
                            .collect(Collectors.joining("\n")));
                    return text.toString();
                }

            };
        }

    }

    static class CustomCellConverter extends BaseCellConverter<CustomType> {

        public CustomCellConverter(FieldAccessor field, Configuration config) {
            super(field, config);
        }

        /**
         * セルをJavaのオブジェクト型に変換します。
         * @param evaluatedCell 数式を評価済みのセル
         * @param formattedValue フォーマット済みのセルの値。トリミングなど適用済み。
         * @return 変換した値を返す。
         * @throws TypeBindException 変換に失敗した場合
         */
        @Override
        protected CustomType parseCell(Cell evaluatedCell, String formattedValue) throws TypeBindException {

            try {
                // 文字列を変換するときには、TextFormatter#parseに委譲します。
                return getTextFormatter().parse(formattedValue);
            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }

        }

        /**
         * 書き込み時のセルに値と書式を設定します。
         * @param cell 設定対象のセル
         * @param cellValue 設定対象の値。
         * @throws TypeBindException 変換に失敗した場合
         */
        @Override
        protected void setupCell(Cell cell, Optional<CustomType> cellValue) throws TypeBindException {

            if(cellValue.isPresent()) {
                // 文字列を変換するときには、TextFormatter#formatに委譲します。
                String text = getTextFormatter().format(cellValue.get());
                cell.setCellValue(text);
            } else {
                cell.setCellType(CellType.BLANK);
            }
        }

    }
}
