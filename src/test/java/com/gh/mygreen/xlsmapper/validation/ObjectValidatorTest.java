package com.gh.mygreen.xlsmapper.validation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthMaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.PatternValidator;

/**
 * {@link ObjectValidatorSupport}のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class ObjectValidatorTest {
    
    private SheetErrorFormatter errorFormatter;
    
    @Before
    public void setUp() throws Exception {
        this.errorFormatter = new SheetErrorFormatter();
    }
    
    /**
     * エラーなし
     */
    @Test
    public void test_success() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/validator_field.xlsx")) {
            
            SheetBindingErrors<SampleSheet> errors = mapper.loadDetail(in, SampleSheet.class);
            
            SampleSheet sheet = errors.getTarget();
            
            // 入力値検証
            SampleSheetValidator validator = new SampleSheetValidator();
            validator.validate(sheet, errors);
            
            assertThat(errors.getAllErrors(), hasSize(0));
            
        }
        
    }
    
    /**
     * エラーあり
     */
    @Test
    public void test_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/validator_field.xlsx")) {
            SheetBindingErrors<SampleSheet> errors = mapper.loadDetail(in, SampleSheet.class);
            
            SampleSheet sheet = errors.getTarget();
            
            // データの書き換え
            sheet.className = null;
            sheet.list.get(1).email = "test";
            sheet.list.get(2).birthday = getDateByDay(new Date(), 1);
            
            // 入力値検証
            SampleSheetValidator validator = new SampleSheetValidator();
            validator.validate(sheet, errors);
            
            printErrors(errors);
            
            {
                String fieldName = "className";
                FieldError fieldError = errors.getFirstFieldError(fieldName).get();
                assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
                assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
                assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
                assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.className));
            }
            
            {
                try {
                    errors.pushNestedPath("list", 1);
                    PersonRecord record = sheet.list.get(1);
                    String fieldName = "email";
                    FieldError fieldError = errors.getFirstFieldError(fieldName).get();
                    assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(record.positions.get(fieldName)));
                    assertThat(fieldError.getLabelAsOptional().get(), is(record.labels.get(fieldName)));
                    assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.pattern"));
                    assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)record.email));
                } finally {
                    errors.popNestedPath();
                }
            }
            
            {
                try {
                    errors.pushNestedPath("list", 2);
                    PersonRecord record = sheet.list.get(2);
                    String fieldName = "birthday";
                    FieldError fieldError = errors.getFirstFieldError(fieldName).get();
                    assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(record.positions.get(fieldName)));
                    assertThat(fieldError.getLabelAsOptional().get(), is(record.labels.get(fieldName)));
                    assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
                    assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)record.birthday));
                    
                } finally {
                    errors.popNestedPath();
                }
            }
            
        }
        
        
    }
    
    /**
     * ヒントの指定あり - デフォルトグループ
     */
    @Test
    public void test_success_group() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/validator_field.xlsx")) {
            
            SheetBindingErrors<SampleSheet> errors = mapper.loadDetail(in, SampleSheet.class);
            
            SampleSheet sheet = errors.getTarget();
            
            // 入力値検証
            SampleSheetValidator validator = new SampleSheetValidator();
            validator.validate(sheet, errors, DefaultGroup.class);
            
            assertThat(errors.getAllErrors(), hasSize(0));
        }
        
    }
    
    /**
     * ヒントの指定あり - エラーあり
     */
    @Test
    public void test_error_group() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConfiguration().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        try(InputStream in = new FileInputStream("src/test/data/validator_field.xlsx")) {
            
            SheetBindingErrors<SampleSheet> errors = mapper.loadDetail(in, SampleSheet.class);
            
            SampleSheet sheet = errors.getTarget();
            
            // 入力値検証
            SampleSheetValidator validator = new SampleSheetValidator();
            validator.validate(sheet, errors, Group1.class);
            
            printErrors(errors);
            
            {
                String fieldName = "className";
                FieldError fieldError = errors.getFirstFieldError(fieldName).get();
                assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
                assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
                assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthMax"));
                assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.className));
            }
            
        }
        
    }
    
    private void printErrors(SheetBindingErrors<?> errors) {
        
        for(ObjectError error : errors.getAllErrors()) {
            String message = errorFormatter.format(error);
            System.out.println(message);
        }
        
    }
    
    @XlsSheet(name="サンプル")
    private static class SampleSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsSheetName
        private String sheetName;
        
        @XlsLabelledCell(label="クラス名", type=LabelledCellType.Right)
        private String className;
        
        @XlsHorizontalRecords(tableLabel="名簿一覧", terminal=RecordTerminal.Border)
        private List<PersonRecord> list;
        
        public String getClassName() {
            return className;
        }
    }
    
    private static class PersonRecord {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        @XlsColumn(columnName="No.")
        private int no;
        
        @XlsColumn(columnName="氏名")
        private String name;
        
        @XlsColumn(columnName="メールアドレス")
        private String email;
        
        @XlsDateTimeConverter(lenient=true, javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="生年月日")
        private Date birthday;
        
        @XlsIgnorable
        public boolean isEmpty() {
            return IsEmptyBuilder.reflectionIsEmpty(this, "positions", "labels", "no");
        }
        
        public int getNo() {
            return no;
        }
        
        public String getName() {
            return name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public Date getBirthday() {
            return birthday;
        }
        
    }
    
    /**
     * {@link SampleSheet}のValidator.
     *
     */
    private static class SampleSheetValidator extends ObjectValidatorSupport<SampleSheet> {
        
        private PersonRecordVaidator personRecordValidator;
        
        public SampleSheetValidator() {
            this.personRecordValidator = new PersonRecordVaidator();
        }
        
        @Override
        public void validate(final SampleSheet targetObj, final SheetBindingErrors<?> errors, final Class<?>... groups) {
            
            CellField<String> classNameField = new CellField<String>("className", errors)
                    .setRequired(true)
                    .add(new LengthMaxValidator(3).addGroup(Group1.class))  // グループ指定
                    .validate(groups);
            
            if(targetObj.list != null) {
                for(int i=0; i < targetObj.list.size();i ++) {
                    invokeNestedValidator(personRecordValidator, targetObj.list.get(i), errors, "list", i, groups);
                }
            }
            
        }
        
    }
    
    /**
     * {@link PersonRecordRecord}のValidator.
     *
     */
    private static class PersonRecordVaidator extends ObjectValidatorSupport<PersonRecord> {
        
        @Override
        public void validate(final PersonRecord targetObj, final SheetBindingErrors<?> errors, final Class<?>... groups) {
            
            CellField<String> nameField = new CellField<String>("name", errors)
                    .setRequired(true)
                    .add(new LengthMaxValidator(10))
                    .validate(groups);
            
            CellField<String> emailField = new CellField<String>("email", errors)
                    .setRequired(true)
                    .add(new PatternValidator(".*@.*", "メールアドレス"))
                    .validate(groups);
            
            CellField<Date> birthdayField = new CellField<Date>("birthday", errors)
                    .setRequired(true)
                    .add(new MaxValidator<Date>(new Date()))
                    .validate(groups);
            
        }
        
    }
    
    private static interface Group1 {}
    private static interface Group2 {}
    private static interface Group3 extends Group1, Group2 {}
    
}
