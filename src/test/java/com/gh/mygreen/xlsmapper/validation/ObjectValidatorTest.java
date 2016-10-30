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
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.MaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.PatternValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.StringValidator;

/**
 * {@link AbstractObjectValidator}のテスタ
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class ObjectValidatorTest {
    
    private SheetMessageConverter messageConverter;
    
    @Before
    public void setUp() throws Exception {
        this.messageConverter = new SheetMessageConverter();
    }
    
    /**
     * エラーなし
     */
    @Test
    public void test_success() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);;
        SampleSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_field.xlsx")) {
            
            sheet = mapper.load(in, SampleSheet.class, errors);
            
        }
        
        // 入力値検証
        SampleSheetValidator validator = new SampleSheetValidator();
        validator.validate(sheet, errors);
        
        assertThat(errors.getAllErrors(), hasSize(0));
        
    }
    
    /**
     * エラーあり
     */
    @Test
    public void test_error() throws Exception {
        
        XlsMapper mapper = new XlsMapper();
        mapper.getConig().setContinueTypeBindFailure(true);
        
        // シートの読み込み
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);;
        SampleSheet sheet;
        try(InputStream in = new FileInputStream("src/test/data/validator_field.xlsx")) {
            
            sheet = mapper.load(in, SampleSheet.class, errors);
            
        }
        
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
            CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
            assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
            assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.className));
        }
        
        {
            try {
                errors.pushNestedPath("list", 1);
                PersonRecord record = sheet.list.get(1);
                String fieldName = "email";
                CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
                assertThat(fieldError.getCellAddress(), is(record.positions.get(fieldName)));
                assertThat(fieldError.getLabel(), is(record.labels.get(fieldName)));
                assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.pattern"));
                assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)record.email));
            } finally {
                errors.popNestedPath();
            }
        }
        
        {
            try {
                errors.pushNestedPath("list", 2);
                PersonRecord record = sheet.list.get(2);
                String fieldName = "birthday";
                CellFieldError fieldError = errors.getFirstCellFieldError(fieldName);
                assertThat(fieldError.getCellAddress(), is(record.positions.get(fieldName)));
                assertThat(fieldError.getLabel(), is(record.labels.get(fieldName)));
                assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
                assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)record.birthday));
                
            } finally {
                errors.popNestedPath();
            }
        }
        
    }
    
    private void printErrors(SheetBindingErrors errors) {
        
        for(ObjectError error : errors.getAllErrors()) {
            String message = messageConverter.convertMessage(error);
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
        
        @XlsHorizontalRecords(tableLabel="名簿一覧", terminal=RecordTerminal.Border, ignoreEmptyRecord=true)
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
        
        @XlsDateConverter(lenient=true, javaPattern="yyyy年M月d日")
        @XlsColumn(columnName="生年月日")
        private Date birthday;
        
        @XlsIsEmpty
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
    private static class SampleSheetValidator extends AbstractObjectValidator<SampleSheet> {
        
        private PersonRecordVaidator personRecordValidator;
        
        public SampleSheetValidator() {
            this.personRecordValidator = new PersonRecordVaidator();
        }
        
        @Override
        public void validate(final SampleSheet targetObj, final SheetBindingErrors errors) {
            
            CellField<String> classNameField = new CellField<String>(targetObj, "className")
                    .setRequired(true)
                    .validate(errors);
            
            for(int i=0; i < targetObj.list.size();i ++) {
                invokeNestedValidator(personRecordValidator, targetObj.list.get(i), errors, "list", i);
            }
            
        }
        
    }
    
    /**
     * {@link PersonRecordRecord}のValidator.
     *
     */
    private static class PersonRecordVaidator extends AbstractObjectValidator<PersonRecord> {
        
        @Override
        public void validate(final PersonRecord targetObj, final SheetBindingErrors errors) {
            
            CellField<String> nameField = new CellField<String>(targetObj, "name")
                    .setRequired(true)
                    .add(StringValidator.maxLength(10))
                    .validate(errors);
            
            CellField<String> emailField = new CellField<String>(targetObj, "email")
                    .setRequired(true)
                    .add(new PatternValidator(".*@.*", "メールアドレス"))
                    .validate(errors);
            
            CellField<Date> birthdayField = new CellField<Date>(targetObj, "birthday")
                    .setRequired(true)
                    .add(new MaxValidator<Date>(new Date(), "yyyy年M月d日"))
                    .validate(errors);
            
        }
        
    }
}
