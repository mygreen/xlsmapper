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

import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link StringValidatorTest}に関するテスタ
 * 
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
        // 必須チェック(値が空文字)
        errors.clearAllErrors();
        sheet.str = "";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
        // 必須チェック(値がある
        errors.clearAllErrors();
        sheet.str = "あいう";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        
    }
    
    /**
     * 文字列のオプション指定
     */
    @Test
    public void test_validation_str_optional() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // オプション指定(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // オプション指定(値が空文字)
        errors.clearAllErrors();
        sheet.str = "";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // オプション指定(値がある)
        errors.clearAllErrors();
        sheet.str = "あいう";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_exactLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // 文字数(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.exactLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 文字数(値が不正)
        errors.clearAllErrors();
        sheet.str = "あいうえ";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.exactLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.exactLength"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVars(), hasEntry("valueLength", (Object)4));
        assertThat(fieldError.getVars(), hasEntry("length", (Object)5));
        
        // 文字数(値が正しい)
        errors.clearAllErrors();
        sheet.str = "あいうえお";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.exactLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_maxLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // 文字数(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.maxLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 文字数(値が不正)
        errors.clearAllErrors();
        sheet.str = "あいうえおか";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.maxLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.maxLength"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVars(), hasEntry("valueLength", (Object)6));
        assertThat(fieldError.getVars(), hasEntry("maxLength", (Object)5));
        
        // 文字数(値が正しい)
        errors.clearAllErrors();
        sheet.str = "あいうえお";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.maxLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長以上かどうか文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_minLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // 文字数(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.minLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 文字数(値が不正)
        errors.clearAllErrors();
        sheet.str = "あいうえ";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.minLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.minLength"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVars(), hasEntry("valueLength", (Object)4));
        assertThat(fieldError.getVars(), hasEntry("minLength", (Object)5));
        
        // 文字数(値が正しい)
        errors.clearAllErrors();
        sheet.str = "あいうえお";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.minLength(5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか文字列が等しいかどうか
     */
    @Test
    public void test_validation_str_betweenLength() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // 文字数(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.betweenLength(3, 5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 文字数(値が不正)-小さい
        errors.clearAllErrors();
        sheet.str = "あい";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.betweenLength(3, 5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.betweenLength"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVars(), hasEntry("valueLength", (Object)2));
        assertThat(fieldError.getVars(), hasEntry("minLength", (Object)3));
        assertThat(fieldError.getVars(), hasEntry("maxLength", (Object)5));
        
        // 文字数(値が不正)-大きい
        errors.clearAllErrors();
        sheet.str = "あいうえおか";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.betweenLength(3, 5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.betweenLength"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVars(), hasEntry("valueLength", (Object)6));
        assertThat(fieldError.getVars(), hasEntry("minLength", (Object)3));
        assertThat(fieldError.getVars(), hasEntry("maxLength", (Object)5));
        
        // 文字数(値が正しい)
        errors.clearAllErrors();
        sheet.str = "あいうえお";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(StringValidator.betweenLength(3, 5));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
