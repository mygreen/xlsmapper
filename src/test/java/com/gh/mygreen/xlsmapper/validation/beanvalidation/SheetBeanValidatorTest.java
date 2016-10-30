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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.XlsMapper;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageELImpl;
import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.MessageInterpolator;
import com.gh.mygreen.xlsmapper.validation.ObjectError;
import com.gh.mygreen.xlsmapper.validation.ResourceBundleMessageResolver;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetMessageConverter;

/**
 * {@link SheetBeanValidator}のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class SheetBeanValidatorTest {
    
    private SheetMessageConverter messageConverter;
    
    @Before
    public void setUp() throws Exception {
        this.messageConverter = new SheetMessageConverter();
    }
    
    private Validator getBeanValidator() {
        
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.usingContext()
                .messageInterpolator(new MessageResolverInterpolator(new ResourceBundleMessageResolver()))
                .getValidator();
        
        return validator;
    }
    
    /**
     * 単純なBeanのテスト - エラーなし
     */
    @Test
    public void test_simple_success() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors;
        SimpleBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            
            errors = new SheetBindingErrors(SimpleBeanSheet.class);
            sheet = mapper.load(in, SimpleBeanSheet.class, errors);
            
        }
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        sheetValidator.validate(sheet, errors);
        
        assertThat(errors.getAllErrors(), hasSize(0));
        
    }
    
    /**
     * 単純なBeanのテスト - エラーあり
     */
    @Test
    public void test_simple_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors = new SheetBindingErrors(SimpleBeanSheet.class);;
        SimpleBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            
            sheet = mapper.load(in, SimpleBeanSheet.class, errors);
            
        }
        
        // データの書き換え
        sheet.description = "あいうえおかきくけこさ";
        sheet.age = -1;
        sheet.email = "test";
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        sheetValidator.validate(sheet, errors);
        
//        printErrors(errors);
        
        
        {
            String fieldName = "description";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Length"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.description));
            assertThat(fieldError.getVars(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVars(), hasEntry("max", (Object)10));
        }
        
        {
            String fieldName = "age";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Range"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.age));
            assertThat(fieldError.getVars(), hasEntry("min", (Object)0L));
            assertThat(fieldError.getVars(), hasEntry("max", (Object)100L));
        }
        
        {
            String fieldName = "email";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Email"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.email));
        }
        
        
    }
    
    /**
     * リストなBeanのテスト - エラーなし
     */
    @Test
    public void test_list_success() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors = new SheetBindingErrors(ListBeanSheet.class);
        ListBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            sheet = mapper.load(in, ListBeanSheet.class, errors);
            
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors = new SheetBindingErrors(ListBeanSheet.class);
        ListBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            sheet = mapper.load(in, ListBeanSheet.class, errors);
            
        }
        
        // データの書き換え
        sheet.className = null;
        sheet.list.get(1).email = "test";
        sheet.list.get(2).birthday = getDateByDay(new Date(), 1);
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(getBeanValidator());
        sheetValidator.validate(sheet, errors);
        
//        printErrors(errors);
        
        {
            String fieldName = "className";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("NotBlank"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.className));
        
        }
        try {
            errors.pushNestedPath("list", 1);
            PersonRecord record = sheet.list.get(1);
            String fieldName = "email";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(record.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(record.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Email"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)record.email));
        } finally {
            errors.popNestedPath();
        }
        
        try {
            errors.pushNestedPath("list", 2);
            PersonRecord record = sheet.list.get(2);
            String fieldName = "birthday";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(record.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(record.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Past"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)record.birthday));
            
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
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors = new SheetBindingErrors(SimpleBeanSheet.class);;
        SimpleBeanSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_bean.xlsx")) {
            
            sheet = mapper.load(in, SimpleBeanSheet.class, errors);
            
        }
        
        // データの書き換え
        sheet.updateTime = getDateByDay(new Date(), 1);
        sheet.description = "あいうえおかきくけこさ";
        sheet.age = -1;
        sheet.email = "test";
        
        // BeanValidatorの式言語の実装を独自のものにする。
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator beanValidator = validatorFactory.usingContext()
                .messageInterpolator(new MessageInterpolatorAdapter(
                        new ResourceBundleMessageResolver(ResourceBundle.getBundle("com.gh.mygreen.xlsmapper.validation.beanvalidation.OtherElMessages")),
                        new MessageInterpolator(new ExpressionLanguageELImpl())))
                .getValidator();
        
        // 入力値検証
        SheetBeanValidator sheetValidator = new SheetBeanValidator(beanValidator);
        sheetValidator.validate(sheet, errors);
        
        printErrors(errors);
        
        {
            String fieldName = "updateTime";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Past"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.updateTime));
        }
        
        {
            String fieldName = "description";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Length"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.description));
            assertThat(fieldError.getVars(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVars(), hasEntry("max", (Object)10));
        }
        
        {
            String fieldName = "age";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Range"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.age));
            assertThat(fieldError.getVars(), hasEntry("min", (Object)0L));
            assertThat(fieldError.getVars(), hasEntry("max", (Object)100L));
        }
        
        {
            String fieldName = "email";
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("Email"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.email));
        }
        
        
    }
    
    private void printErrors(SheetBindingErrors errors) {
        
        for(ObjectError error : errors.getAllErrors()) {
            String message = messageConverter.convertMessage(error);
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
        @Past
        @XlsDateConverter(lenient=true, javaPattern="yyyy年M月d日")
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
        @XlsHorizontalRecords(tableLabel="名簿一覧", terminal=RecordTerminal.Border, ignoreEmptyRecord=true)
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
        @XlsDateConverter(lenient=true, javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="生年月日")
        private Date birthday;
        
        @XlsIsEmpty
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
    }
}
