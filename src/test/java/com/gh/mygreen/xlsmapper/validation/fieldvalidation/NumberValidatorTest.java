package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MinValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.RangeValidator;


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
@RunWith(Enclosed.class)
public class NumberValidatorTest {
    
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
    
    private static interface Group1 {}
    
    /**
     * Integer型のテスト
     */
    public static class IntegerValidatorTest {
        
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
        
        private FieldFormatter<Integer> fieldFormatter = new FieldFormatter<Integer>() {
            
            @Override
            public String format(Integer value) {
                return new DecimalFormat("###,##0").format(value);
            }
        };
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "numI";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "値1");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            errors.clearAllErrors();
        }
        
        /**
         * 必須チェック - 値がnull
         */
        @Test
        public void test_required_null() {
            
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
            
        }
        
        /**
         * 必須チェック - 値が0
         */
        @Test
        public void test_required_zero() {
            
            sheet.numI = 0;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 必須チェック - 値がある
         */
        @Test
        public void test_required_valid() {
            sheet.numI = 123;
            CellField<Integer>field = new CellField<>(fieldName, errors);
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
            
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 必須チェック - オプション指定 - 値が0
         */
        @Test
        public void test_optional_zero() {
            
            sheet.numI = 0;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 必須チェック - オプション指定 - 値が存在する
         */
        @Test
        public void test_optional_valid() {
            
            sheet.numI = 123;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最大値チェック - 値がnull
         */
        @Test
        public void test_max_null() {
            
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最大値チェック - 値が不正
         */
        @Test
        public void test_max_wrong() {
            
            sheet.numI = 11;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(10, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'11'は、'10'より小さい値を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマッタ指定
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.numI = 1234567;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(123456));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)123456));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'1,234,567'は、'123,456'以下の値を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が正常
         */
        @Test
        public void test_max_valid() {
            
            sheet.numI = 10;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Integer>(10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値がnull
         */
        @Test
        public void test_min_null() {
            
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.numI = 9;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(10, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)10));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'9'は、'10'より大きい値を設定してください。"));
            
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマッターの指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.numI = 123456;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(1234567));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)1234567));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'123,456'は、'1,234,567'以上の値を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が正常
         */
        @Test
        public void test_min_valid() {
            
            sheet.numI = 10;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Integer>(10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲チェック - 値がnull
         */
        @Test
        public void test_range_null() {
            
            sheet.numI = null;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲チェック - 値が不正 - 小さい
         */
        @Test
        public void test_range_wrong_less() {
            
            sheet.numI = -1;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'-1'は、'0'より大きく、'10'より小さい値を設定してください。"));
            
        }
        
        /**
         * 範囲チェック - 値が不正 - 大きい
         */
        @Test
        public void test_range_wrong_greater() {
            
            sheet.numI = 11;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'11'は、'0'より大きく、'10'より小さい値を設定してください。"));
            
        }
        
        /**
         * 範囲チェック - 値が不正 - フォーマット指定
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.numI = 12345678;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(-1234567, 1234567));
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numI));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)(-1234567)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)1234567));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の値'12,345,678'は、'-1,234,567'以上、'1,234,567'以下の値を設定してください。"));
            
        }
        
        /**
         * 範囲チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.numI = 10;
            CellField<Integer> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Integer>(0, 10));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
    
    }
    
    /**
     * Double型のテスト
     *
     */
    public static class DoubleValidatorTest {
        
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
        
        private FieldFormatter<Double> fieldFormatter = new FieldFormatter<Double>() {
            
            @Override
            public String format(Double value) {
                return new DecimalFormat("###,##0.0###").format(value);
            }
        };
        
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "numD";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A6")).addLabel(fieldName, "値2");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            errors.clearAllErrors();
        }
        
        /**
         * 必須チェック - 値がnull
         */
        @Test
        public void test_required_null() {
            
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));
        }
        
        /**
         * 必須チェック - 値が0
         */
        @Test
        public void test_required_zero() {
            
            sheet.numD = 0.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(true);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 必須チェック - 値が正しい
         */
        @Test
        public void test_required_valid() {
            
            errors.clearAllErrors();
            sheet.numD = 123.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
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
            
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 必須チェック - オプション指定 - 値が0
         */
        @Test
        public void test_optional_zero() {
            
            sheet.numD = 0.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 必須チェック - オプション指定 - 値が正しい
         */
        @Test
        public void test_optional_valid() {
            
            sheet.numD = 123.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最大値チェック - 値がnull
         */
        @Test
        public void test_max_null() {
            
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最大値チェック - 値が不正
         */
        @Test
        public void test_max_wrong() {
            
            sheet.numD = 11.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'11.0'は、'10.0'より小さい値を設定してください。"));
            
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマッター指定
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.numD = 1234567.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(123456.0));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)123456.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'1,234,567.0'は、'123,456.0'以下の値を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が正しい
         */
        @Test
        public void test_max_valid() {
            
            sheet.numD = 10.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
            
        }
        
        /**
         * 最大値チェック - グループ指定
         */
        @Test
        public void test_max_group() {
            
            sheet.numD = 11.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Double>(10.0).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * 最小値チェック - 値がnull
         */
        @Test
        public void test_min_null() {
            
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.numD = 9.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)10.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'9.0'は、'10.0'より大きい値を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマッター指定
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.numD = 123456.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(1234567.0));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)1234567.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'123,456.0'は、'1,234,567.0'以上の値を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が正しい
         */
        @Test
        public void test_min_valid() {
            
            sheet.numD = 11.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - グループ指定
         */
        @Test
        public void test_min_group() {
            
            sheet.numD = 9.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Double>(10.0).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * 範囲チェック - 値がnull
         */
        @Test
        public void test_range_null() {
            
            sheet.numD = null;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲チェック - 値が不正 - 小さい
         */
        @Test
        public void test_range_wrong_less() {
            
            sheet.numD = -1.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0.0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'-1.0'は、'0.0'より大きく、'10.0'より小さい値を設定してください。"));
            
        }
        
        /**
         * 範囲チェック - 値が不正 - 大きい
         */
        @Test
        public void test_range_wrong_greater() {
            
            sheet.numD = 11.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0, false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)0.0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)10.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'11.0'は、'0.0'より大きく、'10.0'より小さい値を設定してください。"));
            
        }
        
        /**
         * 範囲チェック - 値が不正 - フォーマッター指定
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.numD = 12345678.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(-1234567.0, 1234567.0));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.numD));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)(-1234567.0)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)1234567.0));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値2 - セル(A6)の値'12,345,678.0'は、'-1,234,567.0'以上、'1,234,567.0'以下の値を設定してください。"));
            
        }
        
        /**
         * 範囲チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.numD = 10.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(0.0, 10.0));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲チェック - グループ指定
         */
        @Test
        public void test_range_group() {
            
            sheet.numD = 12345678.0;
            CellField<Double> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Double>(-1234567.0, 1234567.0).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
    }
    
}
