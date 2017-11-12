package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthBetweenValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthExactValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthMaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthMinValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.PatternValidator;

/**
 * 文字列に関するテスタ
 * 
 * <p>テスト対象のValidator。
 * <ul>
 *  <li>{@link LengthExactValidator}</li>
 *  <li>{@link LengthMaxValidator}</li>
 *  <li>{@link LengthMinValidator}</li>
 *  <li>{@link LengthBetweenValidator}</li>
 *  <li>{@link PatternValidator}</li>
 * </ul>
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class StringValidatorTest {
    
    /**
     * テスト用のシートクラス
     *
     */
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
    
    private static interface Group1 {}
    
    /**
     * エラーメッセージのコンバーター
     */
    private SheetErrorFormatter errorFormatter;
    
    /**
     * フィールド名の定義
     */
    private String fieldName;
    
    /**
     * サンプルシート
     */
    private SampleSheet sheet;
    
    /**
     * シートエラー情報
     */
    private SheetBindingErrors<SampleSheet> errors;
    
    @Before
    public void setUp() {
        
        this.errorFormatter = new SheetErrorFormatter();
        
        this.fieldName = "str";
        
        this.sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        this.errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        errors.clearAllErrors();
    }
    
    /**
     * 必須チェック - 値がnull
     */
    @Test
    public void test_required_null() {
        
        sheet.str = null;
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(true);
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).get();
        assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
    }
    
    /**
     * 必須チェック - 値が空文字
     */
    @Test
    public void test_required_empty() {
        
        sheet.str = "";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(true);
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).get();
        assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        
    }
    
    /**
     * 必須チェック - 正しい
     */
    @Test
    public void test_required_wrong() {
        
        sheet.str = "あいう";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(true);
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 必須チェック - オプション指定 - 値がnull
     */
    @Test
    public void test_optional_null() {
        
        sheet.str = null;
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 必須チェック - オプション指定 - 値が空文字
     */
    @Test
    public void test_optional_empty() {
        
        sheet.str = "";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 必須チェック - オプション指定 - 値がある
     */
    @Test
    public void test_optional_valid() {
        
        sheet.str = "あいう";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字数が等しいかチェック - 値がNull
     */
    @Test
    public void test_lengthExact_null() {
        
        sheet.str = null;
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthExactValidator(5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字数が等しいかチェック - 値がNull
     */
    @Test
    public void test_lengthExact_wrong() {
        
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
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の文字長'4'は、[5, 10]文字の何れかでなければなりません。"));
        
        
    }
    
    /**
     * 文字数が等しいかチェック - 値が正しい
     */
    @Test
    public void test_lengthExact_valid() {
        
        sheet.str = "あいうえお";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthExactValidator(5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字数が等しいかチェック - グループ指定
     */
    @Test
    public void test_lengthExact_group() {
        
        sheet.str = "あいうえ";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthExactValidator(5, 10).addGroup(Group1.class));
        
        field.validate(Group1.class);
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(not(nullValue())));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - 値がnull
     */
    @Test
    public void test_lengthMax_null() {
        
        sheet.str = null;
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthMaxValidator(5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - 値が不正
     */
    @Test
    public void test_lengthMax_wrong() {
        
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
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の文字長'6'は、5文字以内で値を設定してください。"));
        
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - 値が正しい
     */
    @Test
    public void test_lengthMax_valid() {
        
        sheet.str = "あいうえお";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthMaxValidator(5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - グループ指定
     */
    @Test
    public void test_lengthMax_group() {
        
        sheet.str = "あいうえおか";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthMaxValidator(5).addGroup(Group1.class));
        
        field.validate(Group1.class);
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(not(nullValue())));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - 値がnull
     */
    @Test
    public void test_lengthMin_null() {
        
        sheet.str = null;
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthMinValidator(5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - 値が不正
     */
    @Test
    public void test_lengthMin_wrong() {
        
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
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の文字長'4'は、5文字以上で値を設定してください。"));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - 値が正しい
     */
    @Test
    public void test_lengthMin_valid() {
        
        sheet.str = "あいうえお";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthMinValidator(5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長以内かどうか - グループ指定
     */
    @Test
    public void test_lengthMin_group() {
        
        sheet.str = "あいうえ";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthMinValidator(5).addGroup(Group1.class));
        
        field.validate(Group1.class);
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(not(nullValue())));
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか - 値がnull
     */
    @Test
    public void test_lengthBetween_null() {
        
        sheet.str = null;
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthBetweenValidator(3, 5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか - 値が不正 - 小さい
     */
    @Test
    public void test_lengthBetween_wrong_less() {
        
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
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の文字長'2'は、3～5文字以内で値を入設定してください。"));
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか - 値が不正 - 大きい
     */
    @Test
    public void test_lengthBetween_wrong_greater() {
        
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
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の文字長'6'は、3～5文字以内で値を入設定してください。"));
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか - 値が正しい
     */
    @Test
    public void test_lengthBetween_valid() {
        
        sheet.str = "あいうえお";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthBetweenValidator(3, 5));
        
        field.validate();
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * 文字列が指定した文字長の範囲内かどうか - グループ指定
     */
    @Test
    public void test_lengthBwetween_group() {
        
        sheet.str = "あいうえおか";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new LengthBetweenValidator(3, 5).addGroup(Group1.class));
        
        field.validate(Group1.class);
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(not(nullValue())));
        
    }
    
    /**
     * パターン - 値がnull
     */
    @Test
    public void test_pattern_null() {
        
        sheet.str = null;
        
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+"));
        field.validate();
        
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * パターン - 値が不正
     */
    @Test
    public void test_pattern_wrong() {
        
        sheet.str = "あいうえ";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+"));
        field.validate();
        
        FieldError fieldError = errors.getFirstFieldError(fieldName).get();
        assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.pattern"));
        
        assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVariables(), hasEntry("pattern", (Object)".+@.+"));
        assertThat(fieldError.getVariables(), hasEntry("description", null));
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の値'あいうえ'は、'.+@.+'に一致していません。"));
        
        
    }
    
    /**
     * パターン - 値が正しい - パターン名を指定
     */
    @Test
    public void test_pattern_wrong_withDescription() {
        
        sheet.str = "あいうえ";
        
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+", "メールアドレスの書式"));
        field.validate();
        
        FieldError  fieldError = errors.getFirstFieldError(fieldName).get();
        assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.pattern"));
        
        assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVariables(), hasEntry("pattern", (Object)".+@.+"));
        assertThat(fieldError.getVariables(), hasEntry("description", (Object)"メールアドレスの書式"));
        
        assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
        
        // メッセージ
        String message = errorFormatter.format(fieldError);
        assertThat(message, is("[サンプルシート]:名前 - セル(A5)の値'あいうえ'は、'メールアドレスの書式'に一致していません。"));
        
    }
    
    /**
     * パターン - 値が正しい
     */
    @Test
    public void test_pattern_valid() {
        
        sheet.str = "hoge@example.com";
        
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+"));
        field.validate();
        
        FieldError  fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(nullValue()));
        
    }
    
    /**
     * パターン - グループ指定
     */
    @Test
    public void test_pattern_group() {
        
        sheet.str = "あいうえ";
        CellField<String> field = new CellField<>(fieldName, errors);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+").addGroup(Group1.class));
        
        field.validate(Group1.class);
        FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
        assertThat(fieldError, is(not(nullValue())));
        
    }
    
    
}
