package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * 数値型に関するテスタ。
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
public class NumberValidatorTest {
    
    /**
     * 数値型の必須チェック - Integer型
     */
    @Test
    public void test_validation_numI_required() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numI";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(true);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        }
        
        {
            // 必須チェック(値が0)
            errors.clearAllErrors();
            sheet.numI = 0;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(true);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        
        }
        
        {
            // 必須チェック(値がある)
            errors.clearAllErrors();
            sheet.numI = 123;
            CellField<Integer>field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(true);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    /**
     * 数値型のオプションチェック - Integer型
     */
    @Test
    public void test_validation_numI_optional() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numI";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値が0)
            errors.clearAllErrors();
            sheet.numI = 0;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値がある)
            errors.clearAllErrors();
            sheet.numI = 123;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    /**
     * 数値型の最大値チェック - Integer型
     */
    @Test
    public void test_validation_numI_max() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numI";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 最大値チェック(値がnull)
            errors.clearAllErrors();
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 最大値チェック(値が不正)
            errors.clearAllErrors();
            sheet.numI = 11;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
        }
        
        {
            // 最大値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.numI = 1234567;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(123456));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)123456));
        }
        
        {
            // 最大値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.numI = 10;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
    }
    
    /**
     * 数値型の最小値チェック - Integer型
     */
    @Test
    public void test_validation_numI_min() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numI";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 最小値チェック(値がnull)
            errors.clearAllErrors();
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 最小値チェック(値が不正)
            errors.clearAllErrors();
            sheet.numI = 9;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)10));
            
        }
        
        {
            // 最小値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.numI = 123456;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(1234567));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)1234567));
        }
        
        {
            // 最小値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.numI = 10;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
    }
    
    /**
     * 数値型の範囲チェック - Integer型
     */
    @Test
    public void test_validation_numI_range() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numI";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 範囲チェック(値がnull)
            errors.clearAllErrors();
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        
        }
        
        {
            // 範囲チェック(値が不正)（小さい）
            errors.clearAllErrors();
            sheet.numI = -1;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
        }
        
        {
            // 範囲チェック(値が不正)（大きい）
            errors.clearAllErrors();
            sheet.numI = 11;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
        }
        
        {
            // 範囲チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.numI = 12345678;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(-1234567, 1234567));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)(-1234567)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)1234567));
        }
        
        {
            // 範囲チェック(値が正しい)
            errors.clearAllErrors();
            sheet.numI = 10;
            CellField<Integer> field = new CellField<>(fieldName, Integer.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
    }
    
    /**
     * 数値型の必須チェック - Double型
     */
    @Test
    public void test_validation_numD_required() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numD";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A6")).addLabel(fieldName, "値2");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(true);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        }
        
        {
            // 必須チェック(値が0)
            errors.clearAllErrors();
            sheet.numD = 0.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(true);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値がある)
            errors.clearAllErrors();
            sheet.numD = 123.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(true);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        
        }
        
    }
    
    /**
     * 数値型のオプションチェック - Double型
     */
    @Test
    public void test_validation_numD_optional() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numD";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A6")).addLabel(fieldName, "値2");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 必須チェック(値がnull)
            errors.clearAllErrors();
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値が0)
            errors.clearAllErrors();
            sheet.numD = 0.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 必須チェック(値がある)
            errors.clearAllErrors();
            sheet.numD = 123.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
    }
    
    /**
     * 数値型の最大値チェック - Double型
     */
    @Test
    public void test_validation_numD_max() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numD";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A6")).addLabel(fieldName, "値2");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 最大値チェック(値がnull)
            errors.clearAllErrors();
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        
        }
        
        {
            // 最大値チェック(値が不正)
            errors.clearAllErrors();
            sheet.numD = 11.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10.0));
            
        }
        
        {
            // 最大値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.numD = 1234567.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(123456.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)123456.0));
            
        }
        
        {
            // 最大値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.numD = 10.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
    
    /**
     * 数値型の最小値チェック - Double型
     */
    @Test
    public void test_validation_numD_min() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numD";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A6")).addLabel(fieldName, "値2");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 最小値チェック(値がnull)
            errors.clearAllErrors();
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 最小値チェック(値が不正)
            errors.clearAllErrors();
            sheet.numD = 9.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)10.0));
        }
        
        {
            // 最小値チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.numD = 123456.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(1234567.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)1234567.0));
        }
        
        {
            // 最小値チェック(値が正しい)
            errors.clearAllErrors();
            sheet.numD = 10.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
    }
    
    /**
     * 数値型の範囲チェック - Double型
     */
    @Test
    public void test_validation_numD_range() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numD";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A6")).addLabel(fieldName, "値2");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        {
            // 範囲チェック(値がnull)
            errors.clearAllErrors();
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // 範囲チェック(値が不正)（小さい）
            errors.clearAllErrors();
            sheet.numD = -1.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0.0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10.0));
        }
        
        {
            // 範囲チェック(値が不正)（大きい）
            errors.clearAllErrors();
            sheet.numD = 11.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0.0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10.0));
        }
        
        {
            // 範囲チェック(値が不正)（フォーマット指定あり）
            errors.clearAllErrors();
            sheet.numD = 12345678.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(-1234567.0, 1234567.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)(-1234567.0)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)1234567.0));
            }
        
        {
            // 範囲チェック(値が正しい)
            errors.clearAllErrors();
            sheet.numD = 10.0;
            CellField<Double> field = new CellField<>(fieldName, Double.class, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0));
            
            field.validate();
            CellFieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError, is(nullValue()));
        
        }
        
    }
    
    private static class SampleSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        private Integer numI;
        
        private Double numD;
        
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
