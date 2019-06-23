package com.gh.mygreen.xlsmapper.sample.attendance;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
 * 勤務表のシートのテスト
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AttendanceTest {

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

        SheetBindingErrors<AttendanceSheet> bindingResult;
        try (InputStream xlsIn = Files.newInputStream(Paths.get("src/test/data/sample", "attendance.xlsx"))) {
            bindingResult = xlsMapper.loadDetail(xlsIn, AttendanceSheet.class);

        }

        AttendanceSheet sheet = bindingResult.getTarget();

        // シートの値の検証
        sheetBeanValidator.validate(sheet, bindingResult);
        printBindingErrors(bindingResult.getAllErrors());

        // エラーのチェック
        assertThat(bindingResult.getAllErrors()).isEmpty();

        assertThat(YearMonth.from(sheet.getTargetDate())).isEqualTo(YearMonth.of(2018, 4));
        assertThat(sheet.getProjectName()).isEqualTo("○×の開発");
        assertThat(sheet.getWorkerName()).isEqualTo("山田　太郎");

        assertThat(sheet.getMessage()).isEqualTo("スケジュール通り、４月中に、結合テストが完了しました。\n品質は特に問題ありません。");

        // 日のレコード数のチェック
        assertThat(sheet.getWorkDaySum()).isEqualTo(19);
//        assertThat(sheet.getWorkTimeSum())

        assertThat(sheet.getDays()).hasSize(30);

        {
            //4 月２日
            DayRecord record = sheet.getDays().get(1);

            assertThat(record.getDate()).isEqualTo(LocalDate.of(2018, 4, 2));

            assertThat(record.getStartTime()).isEqualTo(LocalTime.of(9, 0));
            assertThat(record.getEndTime()).isEqualTo(LocalTime.of(18, 0));
            assertThat(record.getRestTime()).isEqualTo(LocalTime.of(1, 0));
            assertThat(record.getWorkTime()).isEqualTo(LocalTime.of(8, 0));

            assertThat(record.getWorkContent()).isEqualTo("要件定義の資料作成。");

        }

        dumpSheet(sheet);

    }

    /**
     * シートの書き込み
     */
    @Test
    public void testSave() throws Exception {

        // データの作成
        AttendanceSheet sheet = new AttendanceSheet();
        sheet.setTargetDate(LocalDate.of(2018, 4, 1));
        sheet.setProjectName("○×の開発");
        sheet.setWorkerName("山田　太郎");

        sheet.setMessage("スケジュール通り、４月中に、結合テストが完了しました。\n品質は特に問題ありません。");

        sheet.addDay(new DayRecord());
        sheet.addDay(new DayRecord(LocalTime.of(9, 0), LocalTime.of(18, 0), LocalTime.of(1, 0), "基本設計の作成。"));
        sheet.addDay(new DayRecord(LocalTime.of(10, 0), LocalTime.of(20, 0), LocalTime.of(1, 0), "基本設計のレビュー。"));

        XlsMapper xlsMapper = new XlsMapper();
        xlsMapper.getConfiguration().setFormulaRecalcurationOnSave(true);

        String outDirPath = createOutDir().toString();

        try(InputStream template = Files.newInputStream(Paths.get("src/test/data/sample", "attendance_template.xlsx"));
                OutputStream xlsOut = Files.newOutputStream(Paths.get(outDirPath, "attendance_out.xlsx"))) {

            xlsMapper.save(template, xlsOut, sheet);

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
    private void dumpSheet(AttendanceSheet sheet) {

        System.out.printf("対象年月日=%s\n", YearMonth.from(sheet.getTargetDate()));
        System.out.printf("プロジェクト名=%s\n", sheet.getProjectName());
        System.out.printf("作業者=%s\n", sheet.getWorkerName());

        System.out.println("------------------");
        sheet.getDays().forEach(r -> dumpRecord(r));
        System.out.println("------------------");

        System.out.printf("作業日数=%d\n", sheet.getWorkDaySum());
        System.out.printf("作業合計時間=%s\n", sheet.getWorkTimeSum());

        System.out.printf("その他連絡事項=%s\n", sheet.getMessage());

    }

    private void dumpRecord(DayRecord record) {

        System.out.printf("月日=%s, 開始時刻=%s, 終了時刻=%s, 休憩時間=%s, 作業時間=%s, 作業内容=%s\n",
                record.getDate(),
                record.getStartTime(), record.getEndTime(), record.getRestTime(), record.getWorkTime(),
                record.getWorkContent());

    }
}
