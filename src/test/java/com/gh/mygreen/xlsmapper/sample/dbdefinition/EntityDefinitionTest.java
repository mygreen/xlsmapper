package com.gh.mygreen.xlsmapper.sample.dbdefinition;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageJEXLImpl;
import com.gh.mygreen.xlsmapper.localization.MessageInterpolator;
import com.gh.mygreen.xlsmapper.localization.ResourceBundleMessageResolver;
import com.gh.mygreen.xlsmapper.validation.ObjectError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.beanvalidation.MessageInterpolatorAdapter;
import com.gh.mygreen.xlsmapper.validation.beanvalidation.SheetBeanValidator;

/**
 * エンティティ定義のテスト
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class EntityDefinitionTest {

    private SheetBeanValidator sheetBeanValidator;

    private SheetErrorFormatter errorFormatter;

    @Before
    public void setupBefore() {

        // BeanValidatorの式言語の実装を独自のものにする。
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.usingContext()
                .messageInterpolator(new MessageInterpolatorAdapter(
                        // メッセージリソースの取得方法を切り替える
                        new ResourceBundleMessageResolver(),

                        // EL式の処理を切り替える
                        new MessageInterpolator(new ExpressionLanguageJEXLImpl())))
                .getValidator();

        // BeanValidationのValidatorを渡す
        this.sheetBeanValidator = new SheetBeanValidator(validator);

        this.errorFormatter = new SheetErrorFormatter();

    }

    /**
     * シートの読み込み
     */
    @Test
    public void testLoad() throws Exception {

        XlsMapper xlsMapper = new XlsMapper();

        SheetBindingErrors<EntitySheet> bindingResult;
        try (InputStream xlsIn = Files.newInputStream(Paths.get("src/test/data/sample", "dbentity.xlsm"))) {
            bindingResult = xlsMapper.loadDetail(xlsIn, EntitySheet.class);

        }

        EntitySheet sheet = bindingResult.getTarget();

        // シートの値の検証
        sheetBeanValidator.validate(sheet, bindingResult);
        printBindingErrors(bindingResult.getAllErrors());

        // エラーのチェック
        assertThat(bindingResult.getAllErrors()).isEmpty();

        assertThat(sheet.getTables()).hasSize(5);

        // 初めのテーブル
        {
            TableRecord table = sheet.getTables().get(0);
            assertThat(table.getCategory()).isEqualTo(TableCategory.TABLE);
            assertThat(table.getLogicalName()).isEqualTo("社員");
            assertThat(table.getPhisicalName()).isEqualTo("EMPLOYEES");

            assertThat(table.getColumns()).hasSize(9);
            {
                ColumnRecord column = table.getColumns().get(0);
                assertThat(column.getNo()).isEqualTo(1);
                assertThat(column.getLogicalName()).isEqualTo("社員ID");
                assertThat(column.getPhisicalName()).isEqualTo("EMPLOYEE_ID");

            }
        }

        // 最後のテーブル
        {
            TableRecord table = sheet.getTables().get(4);
            assertThat(table.getCategory()).isEqualTo(TableCategory.VIEW);
            assertThat(table.getLogicalName()).isEqualTo("社員情報");
            assertThat(table.getPhisicalName()).isEqualTo("EMPLOYEES_INFO_VIEW");

            assertThat(table.getColumns()).hasSize(8);
            {
                ColumnRecord column = table.getColumns().get(0);
                assertThat(column.getNo()).isEqualTo(1);
                assertThat(column.getLogicalName()).isEqualTo("社員ID");
                assertThat(column.getPhisicalName()).isEqualTo("EMPLOYEE_ID");

            }
        }

        dumpSheet(sheet);
    }

    /**
     * 書き込みのテスト
     */
    @Test
    public void testSave() throws Exception {

        XlsMapper xlsMapper = new XlsMapper();
        xlsMapper.getConfiguration().setFormulaRecalcurationOnSave(true);

        // サンプルから読み込む
        SheetBindingErrors<EntitySheet> bindingResult;
        try (InputStream xlsIn = Files.newInputStream(Paths.get("src/test/data/sample", "dbentity.xlsm"))) {
            bindingResult = xlsMapper.loadDetail(xlsIn, EntitySheet.class);

        }

        EntitySheet outSheet = bindingResult.getTarget();

        // シートの書き込み
        String outDirPath = createOutDir().toString();

        try(InputStream template = Files.newInputStream(Paths.get("src/test/data/sample", "dbentity_template.xlsm"));
                OutputStream xlsOut = Files.newOutputStream(Paths.get(outDirPath, "dbentity_out.xlsm"))) {

            xlsMapper.save(template, xlsOut, outSheet);

        }

    }

    /**
     * エラーの内容を出力する
     */
    private void printBindingErrors(List<ObjectError> errors) {

        errors.stream().forEach(e -> System.out.println(errorFormatter.format(e)));

    }

    /**
     * シートの内容を出力する
     * @param sheet
     */
    private void dumpSheet(EntitySheet sheet) {

        System.out.printf("テーブル数=%d\n", sheet.getTables().size());

        sheet.getTables().forEach(r -> dumpRecord(r));


    }

    private void dumpRecord(TableRecord record) {

        System.out.println("------------------");

        System.out.printf("種別=%s, 表名(論理)=%s, 表名(物理)=%s\n",
                record.getCategory().name(), record.getLogicalName(), record.getPhisicalName());

        record.getColumns().forEach(r -> dumpRecord(r));

    }

    private void dumpRecord(ColumnRecord record) {

        System.out.printf("No=%d, 列名(論理)=%s, 列名(物理)=%s, データ型=%s, 長さ=%s\n",
                record.getNo(),
                record.getLogicalName(), record.getPhisicalName(),
                record.getDataType(), record.getLength());

    }
}
