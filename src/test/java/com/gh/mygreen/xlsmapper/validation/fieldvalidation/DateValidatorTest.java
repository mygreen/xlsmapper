package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * 日付型に関するテスタ。
 * <p>テスト対象のValidator。
 * <ul>
 *  <li>{@link MinValidator}
 *  <li>{@link MaxValidator}
 *  <li>{@link RangeValidator}
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class DateValidatorTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * 日時型の必須チェック - Date型
     */
    @Test
    public void test_validation_date_required() throws Exception {
        
        // フィールド名の定義
        String fieldName = "date";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Date> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.date = null;
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
        // 必須チェック(値が0)
        errors.clearAllErrors();
        sheet.date = new Date(0);
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値がある)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-01 10:12:13.456");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 日時型のオプションチェック - Date型
     */
    @Test
    public void test_validation_date_optional() throws Exception {
        
        // フィールド名の定義
        String fieldName = "date";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Date> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.date = null;
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値が0)
        errors.clearAllErrors();
        sheet.date = new Date(0);
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値がある)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-01 10:12:13.456");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 日時型の最大値チェック - Date型
     */
    @Test
    public void test_validation_date_max() throws Exception {
        
        // フィールド名の定義
        String fieldName = "date";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Date> field;
        CellFieldError fieldError;
        
        // 最大値チェック(値がnull)
        errors.clearAllErrors();
        sheet.date = null;
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 最大値チェック(値が不正)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-02 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015-06-02 00:00:00.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"2015-06-01 00:00:00.0"));
        
        // 最大値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-02 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), "yyyy年MM月dd日 HH時mm分ss秒"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015年06月02日 00時00分00秒"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"2015年06月01日 00時00分00秒"));
        
        // 最大値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-01 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 日時型の最小値チェック - Date型
     */
    @Test
    public void test_validation_date_min() throws Exception {
        
        // フィールド名の定義
        String fieldName = "date";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Date> field;
        CellFieldError fieldError;
        
        // 最小値チェック(値がnull)
        errors.clearAllErrors();
        sheet.date = null;
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 最小値チェック(値が不正)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-05-31 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015-05-31 00:00:00.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"2015-06-01 00:00:00.0"));
        
        // 最小値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-05-31 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), "yyyy年MM月dd日 HH時mm分ss秒"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015年05月31日 00時00分00秒"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"2015年06月01日 00時00分00秒"));
        
        // 最小値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-01 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 日時型の範囲値チェック - Date型
     */
    @Test
    public void test_validation_date_range() throws Exception {
        
        // フィールド名の定義
        String fieldName = "date";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Date> field;
        CellFieldError fieldError;
        
        // 範囲値チェック(値がnull)
        errors.clearAllErrors();
        sheet.date = null;
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 範囲値チェック(値が不正)（小さい）
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-05-31 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015-05-31 00:00:00.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"2015-06-01 00:00:00.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"2015-06-30 00:00:00.0"));
        
        // 範囲値チェック(値が不正)（大さい）
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-07-01 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015-07-01 00:00:00.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"2015-06-01 00:00:00.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"2015-06-30 00:00:00.0"));
        
        // 範囲値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-05-31 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000"), "yyyy年MM月dd日 HH時mm分ss秒"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.date));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"2015年05月31日 00時00分00秒"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"2015年06月01日 00時00分00秒"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"2015年06月30日 00時00分00秒"));
        
        // 範囲値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.date = toTimestamp("2015-06-01 00:00:00.000");
        field = new CellField<Date>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }

    
    private static class SampleSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        private Date date;
        
        private SampleSheet addPosition(String field, Point position) {
            if(positions == null) {
                this.positions = new LinkedHashMap<>();
            }
            
            this.positions.put(field, position);
            return this;
        }
        
        private SampleSheet addLabel(String field, String label) {
            if(labels == null) {
                this.labels = new LinkedHashMap<>();
            }
            
            this.labels.put(field, label);
            return this;
        }
        
        public Date getDate() {
            return date;
        }

    }
}
