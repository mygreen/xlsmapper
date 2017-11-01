package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthBetweenValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthExactValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthMaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthMinValidator;

/**
 * 文字列に関するテスタ
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class StringValidatorTest {
    
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
     * 文字列の必須チェック
     */
    @Test
    public void test_validation_str_required() throws Exception {
        
        // フィールド名の定義
        String fieldName = "str";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
            
        }
        
        {
            // 必須チェック(値が空文字)
            errors.clearAllErrors();
            sheet.str = "";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
        }
        
        {
            // 必須チェック(値がある
            errors.clearAllErrors();
            sheet.str = "あいう";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    /**
     * 文字列のオプション指定
     */
    @Test
    public void test_validation_str_optional() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // オプション指定(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        
        }
        
        {
            // オプション指定(値が空文字)
            errors.clearAllErrors();
            sheet.str = "";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // オプション指定(値がある)
            errors.clearAllErrors();
            sheet.str = "あいう";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        
        }
    }
    
    /**
     * 文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_exactLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 文字数(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthExactValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 文字数(値が不正)
            errors.clearAllErrors();
            sheet.str = "あいうえ";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthExactValidator(5, 10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthExact"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)4));
            assertThat(fieldError.getVariables(), hasEntry("requiredLengths", (Object)new int[]{5, 10}));
        }
        
        {
            // 文字数(値が正しい)
            errors.clearAllErrors();
            sheet.str = "あいうえお";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthExactValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_maxLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 文字数(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthMaxValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 文字数(値が不正)
            errors.clearAllErrors();
            sheet.str = "あいうえおか";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthMaxValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthMax"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)6));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)5));
        
        }
        
        {
            // 文字数(値が正しい)
            errors.clearAllErrors();
            sheet.str = "あいうえお";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthMaxValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        
        }
    }
    
    /**
     * 文字列が指定した文字長以上かどうか文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_minLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 文字数(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthMinValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 文字数(値が不正)
            errors.clearAllErrors();
            sheet.str = "あいうえ";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthMinValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthMin"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)4));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)5));
        }
        
        {
            // 文字数(値が正しい)
            errors.clearAllErrors();
            sheet.str = "あいうえお";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthMinValidator(5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_betweenLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 文字数(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthBetweenValidator(3, 5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 文字数(値が不正)-小さい
            errors.clearAllErrors();
            sheet.str = "あい";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthBetweenValidator(3, 5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthBetween"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)2));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)3));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)5));
        }
        
        {
            // 文字数(値が不正)-大きい
            errors.clearAllErrors();
            sheet.str = "あいうえおか";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthBetweenValidator(3, 5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthBetween"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)6));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)3));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)5));
        }
        
        {
            // 文字数(値が正しい)
            errors.clearAllErrors();
            sheet.str = "あいうえお";
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new LengthBetweenValidator(3, 5));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    private static class SampleSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        private String str;
        
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
