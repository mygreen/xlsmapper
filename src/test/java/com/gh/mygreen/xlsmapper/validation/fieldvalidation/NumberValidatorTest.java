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
 * 数値型に関するテスタ。
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
public class NumberValidatorTest {
    
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
     * 数値型の必須チェック - Integer型
     */
    @Test
    public void test_validation_numI_required() throws Exception {
        
        // フィールド名の定義
        String fieldName = "numI";
        
        // オブジェクトの定義
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Integer> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.numI = null;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
        // 必須チェック(値が0)
        errors.clearAllErrors();
        sheet.numI = 0;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値がある)
        errors.clearAllErrors();
        sheet.numI = 123;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Integer> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.numI = null;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値が0)
        errors.clearAllErrors();
        sheet.numI = 0;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値がある)
        errors.clearAllErrors();
        sheet.numI = 123;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Integer> field;
        CellFieldError fieldError;
        
        // 最大値チェック(値がnull)
        errors.clearAllErrors();
        sheet.numI = null;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Integer>(10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 最大値チェック(値が不正)
        errors.clearAllErrors();
        sheet.numI = 11;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Integer>(10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"11"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)10));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"10"));
        
        // 最大値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.numI = 1234567;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Integer>(123456, "##,##0.0"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"1,234,567.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)123456));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"123,456.0"));
        
        // 最大値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.numI = 10;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Integer>(10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Integer> field;
        CellFieldError fieldError;
        
        // 最小値チェック(値がnull)
        errors.clearAllErrors();
        sheet.numI = null;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Integer>(10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 最小値チェック(値が不正)
        errors.clearAllErrors();
        sheet.numI = 9;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Integer>(10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"9"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)10));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"10"));
        
        // 最小値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.numI = 123456;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Integer>(1234567, "##,##0.0"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"123,456.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)1234567));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"1,234,567.0"));
        
        // 最小値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.numI = 10;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Integer>(10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Integer> field;
        CellFieldError fieldError;
        
        // 範囲チェック(値がnull)
        errors.clearAllErrors();
        sheet.numI = null;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Integer>(0, 10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 範囲チェック(値が不正)（小さい）
        errors.clearAllErrors();
        sheet.numI = -1;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Integer>(0, 10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"-1"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)0));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)10));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"10"));
        
        // 範囲チェック(値が不正)（大きい）
        errors.clearAllErrors();
        sheet.numI = 11;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Integer>(0, 10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"11"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)0));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)10));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"10"));
        
        // 範囲チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.numI = 12345678;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Integer>(-1234567, 1234567, "##,##0.0"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numI));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"12,345,678.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)(-1234567)));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"-1,234,567.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)1234567));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"1,234,567.0"));
        
        // 範囲チェック(値が正しい)
        errors.clearAllErrors();
        sheet.numI = 10;
        field = new CellField<Integer>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Integer>(0, 10));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Double> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.numD = null;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
        // 必須チェック(値が0)
        errors.clearAllErrors();
        sheet.numD = 0.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値がある)
        errors.clearAllErrors();
        sheet.numD = 123.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(true);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Double> field;
        CellFieldError fieldError;
        
        // 必須チェック(値がnull)
        errors.clearAllErrors();
        sheet.numD = null;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値が0)
        errors.clearAllErrors();
        sheet.numD = 0.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 必須チェック(値がある)
        errors.clearAllErrors();
        sheet.numD = 123.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Double> field;
        CellFieldError fieldError;
        
        // 最大値チェック(値がnull)
        errors.clearAllErrors();
        sheet.numD = null;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Double>(10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 最大値チェック(値が不正)
        errors.clearAllErrors();
        sheet.numD = 11.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Double>(10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"11.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)10.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"10.0"));
        
        // 最大値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.numD = 1234567.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Double>(123456.0, "##,##0.0"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"1,234,567.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)123456.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"123,456.0"));
        
        // 最大値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.numD = 10.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MaxValidator<Double>(10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Double> field;
        CellFieldError fieldError;
        
        // 最小値チェック(値がnull)
        errors.clearAllErrors();
        sheet.numD = null;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Double>(10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 最小値チェック(値が不正)
        errors.clearAllErrors();
        sheet.numD = 9.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Double>(10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"9.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)10.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"10.0"));
        
        // 最小値チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.numD = 123456.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Double>(1234567.0, "##,##0.0"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"123,456.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)1234567.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"1,234,567.0"));
        
        // 最小値チェック(値が正しい)
        errors.clearAllErrors();
        sheet.numD = 10.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new MinValidator<Double>(10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<Double> field;
        CellFieldError fieldError;
        
        // 範囲チェック(値がnull)
        errors.clearAllErrors();
        sheet.numD = null;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Double>(0.0, 10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // 範囲チェック(値が不正)（小さい）
        errors.clearAllErrors();
        sheet.numD = -1.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Double>(0.0, 10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"-1.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)0.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"0.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)10.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"10.0"));
        
        // 範囲チェック(値が不正)（大きい）
        errors.clearAllErrors();
        sheet.numD = 11.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Double>(0.0, 10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"11.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)0.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"0.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)10.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"10.0"));
        
        // 範囲チェック(値が不正)（フォーマット指定あり）
        errors.clearAllErrors();
        sheet.numD = 12345678.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Double>(-1234567.0, 1234567.0, "##,##0.0"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.numD));
        assertThat(fieldError.getVars(), hasEntry("formattedValidatedValue", (Object)"12,345,678.0"));
        assertThat(fieldError.getVars(), hasEntry("min", (Object)(-1234567.0)));
        assertThat(fieldError.getVars(), hasEntry("formattedMin", (Object)"-1,234,567.0"));
        assertThat(fieldError.getVars(), hasEntry("max", (Object)1234567.0));
        assertThat(fieldError.getVars(), hasEntry("formattedMax", (Object)"1,234,567.0"));
        
        // 範囲チェック(値が正しい)
        errors.clearAllErrors();
        sheet.numD = 10.0;
        field = new CellField<Double>(sheet, fieldName);
        field.setRequired(false);
        field.add(new RangeValidator<Double>(0.0, 10.0));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
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
