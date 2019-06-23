package com.gh.mygreen.xlsmapper.validation.beanvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageJEXLImpl;
import com.gh.mygreen.xlsmapper.localization.EncodingControl;
import com.gh.mygreen.xlsmapper.localization.MessageInterpolator;
import com.gh.mygreen.xlsmapper.localization.MessageResolver;
import com.gh.mygreen.xlsmapper.localization.ResourceBundleMessageResolver;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.ObjectError;

/**
 * {@link SheetBeanValidator}のテスタ
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class SheetBeanValidatorTest {
    
    private SheetErrorFormatter errorFormatter;
    
    @Before
    public void setUp() throws Exception {
        this.errorFormatter = new SheetErrorFormatter();
    }
    
    private Validator getBeanValidator() {
        
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.usingContext()
                .messageInterpolator(new MessageInterpolatorAdapter(
                        new ResourceBundleMessageResolver(), new MessageInterpolator()))
                .getValidator();
        
        return validator;
    }
    
    /**
     * 単純なBeanのテスト - エラーなし
     */
    @Test
    public void test_simple_success() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors<SimpleBeanSheet> errors;
        SimpleBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            
            errors = mapper.loadDetail(in, SimpleBeanSheet.class);
            sheet = errors.getTarget();
            
        }
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        
        // 値の補正
        sheet.updateTime = getDateByDay(new Date(), 1);
        
        sheetValidator.validate(sheet, errors);
        
        assertThat(errors.getAllErrors(), hasSize(0));
        
    }
    
    /**
     * 単純なBeanのテスト - エラーあり
     */
    @Test
    public void test_simple_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors<SimpleBeanSheet> errors;
        SimpleBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            errors = mapper.loadDetail(in, SimpleBeanSheet.class);
            sheet = errors.getTarget();
            
        }
        
        // データの書き換え
        sheet.updateTime = getDateByDay(new Date(), 1); // 正しい値に補正
        sheet.description = "あいうえおかきくけこさ";
        sheet.age = -1;
        sheet.email = "test";
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        sheetValidator.validate(sheet, errors);
        
        printErrors(errors);
        
        
        {
            String fieldName = "description";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Length"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.description));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:説明 - セル(C7)の文字長'11'は、0～10の間で設定してください。"));
        }
        
        {
            String fieldName = "age";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Range"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.age));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0L));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)100L));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:年齢 - セル(B9)の値'-1'は、0から100の間の値を設定してください。"));
            
        }
        
        {
            String fieldName = "email";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Email"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.email));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:e-mail(必須) - セル(B10)の値'test'は、E-mail形式で設定してください。"));
            
        }
        
        
    }
    
    /**
     * リストなBeanのテスト - エラーなし
     */
    @Test
    public void test_list_success() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors<ListBeanSheet> errors;
        ListBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            errors = mapper.loadDetail(in, ListBeanSheet.class);
            sheet = errors.getTarget();
            
        }
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        sheetValidator.validate(sheet, errors);
        
        printErrors(errors);
        
        assertThat(errors.getAllErrors(), hasSize(0));
        
    }
    
    /**
     * リストなBeanのテスト - エラーあり
     */
    @Test
    public void test_list_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors<ListBeanSheet> errors;
        ListBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            errors = mapper.loadDetail(in, ListBeanSheet.class);
            sheet = errors.getTarget();
        }
        
        // データの書き換え
        sheet.className = null;
        sheet.list.get(1).email = "test";
        sheet.list.get(2).birthday = getDateByDay(new Date(), 1);
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        sheetValidator.validate(sheet, errors);
        
        printErrors(errors);
        
        {
            String fieldName = "className";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("NotBlank"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.className));
        
        }
        try {
            errors.pushNestedPath("list", 1);
            PersonRecord record = sheet.list.get(1);
            String fieldName = "email";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(record.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(record.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Email"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)record.email));
        } finally {
            errors.popNestedPath();
        }
        
        try {
            errors.pushNestedPath("list", 2);
            PersonRecord record = sheet.list.get(2);
            String fieldName = "birthday";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(record.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(record.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Past"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)record.birthday));
            
        } finally {
            errors.popNestedPath();
        }
    }
    
    /**
     * メッセージ処理系を独自のものにする。
     * ・式言語処理を独自のものにする。
     */
    @Test
    public void test_interpolator_el() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors<SimpleBeanSheet> errors;
        SimpleBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            errors = mapper.loadDetail(in, SimpleBeanSheet.class);
            sheet = errors.getTarget();
            
        }
        
        // データの書き換え
        sheet.updateTime = toTimestamp("2017-11-01 00:00:00.000");
        sheet.description = "あいうえおかきくけこさ";
        sheet.age = -1;
        sheet.email = "test";
        
        // BeanValidatorの式言語の実装を独自のものにする。
        MessageResolver messageResolver = new ResourceBundleMessageResolver(
                ResourceBundle.getBundle("com.gh.mygreen.xlsmapper.validation.beanvalidation.OtherElMessages", new EncodingControl("UTF-8")));
        
        errorFormatter.setMessageResolver(messageResolver);
        
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator beanValidator = validatorFactory.usingContext()
                .messageInterpolator(
                        new MessageInterpolatorAdapter(messageResolver,
                        new MessageInterpolator(new ExpressionLanguageJEXLImpl())))
                .getValidator();
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(beanValidator);
        sheetValidator.validate(sheet, errors);
        
        printErrors(errors);
        
        {
            String fieldName = "updateTime";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Future"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.updateTime));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:更新日時 - セル(B4)は未来の日付を入力してください。現在の日付「2017/11/01」は過去日です。"));
        }
        
        {
            String fieldName = "description";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Length"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.description));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:説明 - セル(C7)は0～10文字以内で値を入力してください。"));
            
        }
        
        {
            String fieldName = "age";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Range"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.age));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0L));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)100L));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:年齢 - セル(B9)は0から100の間の値を入力してください。"));
        }
        
        {
            String fieldName = "email";
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddress().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Email"));
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.email));
            
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[単純なBean]:e-mail(必須) - セル(B10)はメールアドレスの形式(例:hoge@sample.co.jp)で値を入力してください。"));

        }
        
        
    }
    
    private void printErrors(SheetBindingErrors<?> errors) {
        
        for(ObjectError error : errors.getAllErrors()) {
            String message = errorFormatter.format(error);
            System.out.println(message);
        }
        
    }
    
    @XlsSheet(name="単純なBean")
    private static class SimpleBeanSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @Length(max=10)
        @XlsLabelledCell(label="説明", type=LabelledCellType.Bottom)
        private String description;
        
        @Range(min=0, max=100)
        @XlsLabelledCell(label="年齢", type=LabelledCellType.Right)
        private Integer age;
        
        @NotBlank
        @Email
        @XlsLabelledCell(label="e-mail(必須)", type=LabelledCellType.Right)
        private String email;
        
        @DecimalMin(value="0.0", inclusive=true)
        @XlsLabelledCell(label="得点（平均）", type=LabelledCellType.Right)
        private Double average;
        
        @NotNull
        @Future
        @XlsDateTimeConverter(lenient=true, javaPattern="yyyy年M月d日")
        @XlsLabelledCell(label="更新日時", type=LabelledCellType.Right)
        private Date updateTime;
        
    }
    
    @XlsSheet(name="リストのBean")
    private static class ListBeanSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @NotBlank
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String className;
        
        @Valid
        @XlsHorizontalRecords(tableLabel="名簿一覧", terminal=RecordTerminal.Border)
        private List<PersonRecord> list;
        
    }
    
    private static class PersonRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @Length(max=10)
        @XlsColumn(columnName="氏名")
        private String name;
        
        @NotBlank
        @Email
        @XlsColumn(columnName="メールアドレス")
        private String email;
        
        @NotNull
        @Past
        @XlsDateTimeConverter(lenient=true, javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="生年月日")
        private Date birthday;
        
        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
}
