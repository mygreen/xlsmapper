package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetMessageConverter;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MinValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.RangeValidator;


/**
 * 日付型に関するテスタ。
 * <p>テスト対象のValidator。
 * <ul>
 *  <li>{@link MinValidator}</li>
 *  <li>{@link MaxValidator}</li>
 *  <li>{@link RangeValidator}</li>
 * </ul>
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class DateValidatorTest {
    
    /**
     * エラーメッセージのコンバーター
     */
    private SheetMessageConverter messageConverter;
    
    @Before
    public void setUp() throws Exception {
        this.messageConverter = new SheetMessageConverter();
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
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        }
        
        {
            // 必須チェック(値が0)
            errors.clearAllErrors();
            sheet.date = new Date(0);
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値がある)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-01 10:12:13.456");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
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
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値が0)
            errors.clearAllErrors();
            sheet.date = new Date(0);
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値がある)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-01 10:12:13.456");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
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
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 最大値チェック(値がnull)
            errors.clearAllErrors();
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 最大値チェック(値が不正)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
        
        }
        
        {
            // 最大値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
        
        }
        
        {
            // 最大値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
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
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 最小値チェック(値がnull)
            errors.clearAllErrors();
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 最小値チェック(値が不正)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            
        }
        
        {
            // 最小値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);;
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
        
        }
        
        {
            // 最小値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        
        }
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
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 範囲値チェック(値がnull)
            errors.clearAllErrors();
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 範囲値チェック(値が不正)（小さい）
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
        
        }
        
        {
            // 範囲値チェック(値が不正)（大さい）
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-07-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
        }
        
        {
            // 範囲値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            
        }
        
        {
            // 範囲値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.date = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        
        }
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
        
    }
}
