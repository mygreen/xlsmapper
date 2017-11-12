package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * {@link Date}型とその子クラスに対するValidatorのテスト。
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
public class DateValidatorTest {
    
    /**
     * テスト用のシートクラス
     *
     */
    private static class SampleSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        private Date date;
        
        private java.sql.Date sqlDate;
        
        private Time sqlTime;
        
        private Timestamp sqlTimestamp;
        
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
     * {@link Date}型に対するValidatorのテスト
     *
     */
    public static class UtilDateValidatorTest {
    
        /**
         * エラーメッセージのコンバーター
         */
        private SheetErrorFormatter errorFormatter;
        
        /**
         * サンプルシート
         */
        private SampleSheet sheet;
        
        /**
         * フィールド名の定義
         */
        private String fieldName;
        
        /**
         * シートエラー情報
         */
        private SheetBindingErrors<SampleSheet> errors;
        
        private FieldFormatter<Date> fieldFormatter = new FieldFormatter<Date>() {
            
            @Override
            public String format(Date value) {
                return new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒").format(value);
            }
        };
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "date";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            // エラーをリセットする
            errors.clearAllErrors();
        }
        
        /**
         * 必須チェック - 値がnull
         */
        @Test
        public void test_required_null() {
            
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
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
            
            sheet.date = new Date(0);
            CellField<Date> field = new CellField<>(fieldName, errors);
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
            
            sheet.date = toTimestamp("2015-06-01 10:12:13.456");
            CellField<Date> field = new CellField<>(fieldName, errors);
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
            
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
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
            
            sheet.date = new Date(0);
            CellField<Date> field = new CellField<>(fieldName, errors);
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
            sheet.date = toTimestamp("2015-06-01 10:12:13.456");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        /**
         * 最大値チェック - 値がNull
         */
        @Test
        public void test_max_null() {
            
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最大値チェック - 値が不正
         */
        @Test
        public void test_max_wrong() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-06-02 00:00:00.0'は、'2015-06-01 00:00:00.0'より前の日時を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年06月02日 00時00分00秒'は、'2015年06月01日 00時00分00秒'以前の日時を設定してください。"));
            
            
        }
        
        /**
         * 最大値チェック - 値が正しい
         */
        @Test
        public void test_max_valid() {
            
            sheet.date = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最大値チェック - グループ指定
         */
        @Test
        public void test_max_group() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * 最小値チェック - 値がnull
         */
        @Test
        public void test_min_null() {
            
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
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
            
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-05-31 00:00:00.0'は、'2015-06-01 00:00:00.0'以降の日時を設定してください。"));
            
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);;
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), false));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年05月31日 00時00分00秒'は、'2015年06月01日 00時00分00秒'より後の日時を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が正しい
         */
        public void test_min_valid() {
            
            sheet.date = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - グループ指定
         */
        @Test
        public void test_min_group() {
            
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * 範囲値チェック - 値がnull
         */
        @Test
        public void test_range_null() {
            
            sheet.date = null;
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        /**
         * 範囲値チェック - 値が不正 - 小さい
         */
        @Test
        public void test_range_wrong_less() {
            
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000"), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-05-31 00:00:00.0'は、'2015-06-01 00:00:00.0'より後から'2015-06-30 00:00:00.0'より前の日時を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - 大きい
         */
        @Test
        public void test_range_wrong_greater() {
            
            sheet.date = toTimestamp("2015-07-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000"), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-07-01 00:00:00.0'は、'2015-06-01 00:00:00.0'より後から'2015-06-30 00:00:00.0'より前の日時を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - フォーマッター指定
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.date));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年05月31日 00時00分00秒'は、'2015年06月01日 00時00分00秒'以降から'2015年06月30日 00時00分00秒'以前の日時を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.date = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - グループ指定
         */
        @Test
        public void test_range_group() {
            
            sheet.date = toTimestamp("2015-05-31 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
    }
    
    /**
     * {@link java.sql.Date}型に対するバリデーションのテスタ
     * 
     */
    public static class SqlDateValidatorTest {
        
        /**
         * エラーメッセージのコンバーター
         */
        private SheetErrorFormatter errorFormatter;
        
        /**
         * サンプルシート
         */
        private SampleSheet sheet;
        
        /**
         * フィールド名の定義
         */
        private String fieldName;
        
        /**
         * シートエラー情報
         */
        private SheetBindingErrors<SampleSheet> errors;
        
        private FieldFormatter<Date> fieldFormatter = new FieldFormatter<Date>() {
            
            @Override
            public String format(Date value) {
                return new SimpleDateFormat("yyyy年MM月dd日").format(value);
            }
        };
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "sqlDate";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日付");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            // エラーをリセットする
            errors.clearAllErrors();
        }
        
        /**
         * 最大値チェック - 値が不正
         */
        @Test
        public void test_max_wrong() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-06-02 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000")), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlDate));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日付 - セル(A5)の値'2015-06-02'は、'2015-06-01'より前の日付を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-06-02 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlDate));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日付 - セル(A5)の値'2015年06月02日'は、'2015年06月01日'以前の日付を設定してください。"));
            
            
        }
        
        /**
         * 最大値チェック - 値が正しい
         */
        @Test
        public void test_max_valid() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-06-01 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-05-31 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000")), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日付 - セル(A5)の値'2015-05-31'は、'2015-06-01'より後の日付を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-05-31 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日付 - セル(A5)の値'2015年05月31日'は、'2015年06月01日'以降の日付を設定してください。"));
            
            
        }
        
        /**
         * 最小値チェック - 値が正しい
         */
        @Test
        public void test_min_valid() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-06-01 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - 値が不正
         */
        @Test
        public void test_range_wrong() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-07-01 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000")), toSqlDate(toTimestamp("2015-06-30 00:00:00.000")), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlDate(toTimestamp("2015-06-30 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日付 - セル(A5)の値'2015-07-01'は、'2015-06-01'より後から'2015-06-30'より前の日付を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-07-01 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000")), toSqlDate(toTimestamp("2015-06-30 00:00:00.000"))));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlDate(toTimestamp("2015-06-01 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlDate(toTimestamp("2015-06-30 00:00:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日付 - セル(A5)の値'2015年07月01日'は、'2015年06月01日'以降から'2015年06月30日'以前の日付を設定してください。"));
            
            
        }
        
        /**
         * 範囲値チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.sqlDate = toSqlDate(toTimestamp("2015-06-01 00:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toSqlDate(toTimestamp("2015-06-01 00:00:00.000")), toSqlDate(toTimestamp("2015-06-30 00:00:00.000"))));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
    
    /**
     * {@link Time}型に対するバリデーションのテスタ
     * 
     */
    public static class SqlTimeValidatorTest {
        
        /**
         * エラーメッセージのコンバーター
         */
        private SheetErrorFormatter errorFormatter;
        
        /**
         * サンプルシート
         */
        private SampleSheet sheet;
        
        /**
         * フィールド名の定義
         */
        private String fieldName;
        
        /**
         * シートエラー情報
         */
        private SheetBindingErrors<SampleSheet> errors;
        
        private FieldFormatter<Date> fieldFormatter = new FieldFormatter<Date>() {
            
            @Override
            public String format(Date value) {
                return new SimpleDateFormat("HH時mm分ss秒").format(value);
            }
        };
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "sqlTime";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "時間");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            // エラーをリセットする
            errors.clearAllErrors();
        }
        
        /**
         * 最大値チェック - 値が不正
         */
        @Test
        public void test_max_wrong() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:11:13.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000")), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTime));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'09:11:13'は、'09:10:00'より前の時間を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:11:13.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTime));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'09時11分13秒'は、'09時10分00秒'以前の時間を設定してください。"));
            
            
        }
        
        /**
         * 最大値チェック - 値が正しい
         */
        @Test
        public void test_max_valid() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:10:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:09:13.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000")), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'09:09:13'は、'09:10:00'より後の時間を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:09:13.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'09時09分13秒'は、'09時10分00秒'以降の時間を設定してください。"));
            
            
        }
        
        /**
         * 最小値チェック - 値が正しい
         */
        @Test
        public void test_min_valid() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:10:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - 値が不正
         */
        @Test
        public void test_range_wrong() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 11:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000")), toSqlTime(toTimestamp("2015-06-01 10:59:59.000")), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlTime(toTimestamp("2015-06-01 10:59:59.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'11:00:00'は、'09:10:00'より後から'10:59:59'より前の時間を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 11:00:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000")), toSqlTime(toTimestamp("2015-06-01 10:59:59.000"))));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toSqlTime(toTimestamp("2015-06-01 09:10:00.000"))));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toSqlTime(toTimestamp("2015-06-01 10:59:59.000"))));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'11時00分00秒'は、'09時10分00秒'以降から'10時59分59秒'以前の時間を設定してください。"));
            
            
        }
        
        /**
         * 範囲値チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.sqlTime = toSqlTime(toTimestamp("2015-06-01 09:10:00.000"));
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toSqlTime(toTimestamp("2015-06-01 09:10:00.000")), toSqlTime(toTimestamp("2015-06-01 10:59:59.000"))));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
    
    /**
     *  {@link Timestamp}型に対するバリデーションのテスタ
     */
    public static class SqlTimestampValidatorTest {
        
        /**
         * エラーメッセージのコンバーター
         */
        private SheetErrorFormatter errorFormatter;
        
        /**
         * サンプルシート
         */
        private SampleSheet sheet;
        
        /**
         * フィールド名の定義
         */
        private String fieldName;
        
        /**
         * シートエラー情報
         */
        private SheetBindingErrors<SampleSheet> errors;
        
        private FieldFormatter<Date> fieldFormatter = new FieldFormatter<Date>() {
            
            @Override
            public String format(Date value) {
                return new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss.SSS秒").format(value);
            }
        };
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "sqlTimestamp";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            // エラーをリセットする
            errors.clearAllErrors();
        }
        
        /**
         * 最大値チェック - 値が不正
         */
        @Test
        public void test_max_wrong() {
            
            sheet.sqlTimestamp = toTimestamp("2015-06-02 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTimestamp));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-06-02 00:00:00.0'は、'2015-06-01 00:00:00.0'より前の日時を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.sqlTimestamp = toTimestamp("2015-06-02 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTimestamp));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年06月02日 00時00分00.000秒'は、'2015年06月01日 00時00分00.000秒'以前の日時を設定してください。"));
            
            
        }
        
        /**
         * 最大値チェック - 値が正しい
         */
        @Test
        public void test_max_valid() {
            
            sheet.sqlTimestamp = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.sqlTimestamp = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTimestamp));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-05-31 00:00:00.0'は、'2015-06-01 00:00:00.0'以降の日時を設定してください。"));
            
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.sqlTimestamp = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);;
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), false));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTimestamp));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年05月31日 00時00分00.000秒'は、'2015年06月01日 00時00分00.000秒'より後の日時を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が正しい
         */
        public void test_min_valid() {
            
            sheet.sqlTimestamp = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - 大きい
         */
        @Test
        public void test_range_wrong_greater() {
            
            sheet.sqlTimestamp = toTimestamp("2015-07-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000"), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTimestamp));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-07-01 00:00:00.0'は、'2015-06-01 00:00:00.0'より後から'2015-06-30 00:00:00.0'より前の日時を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - フォーマッター指定
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.sqlTimestamp = toTimestamp("2015-05-31 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.sqlTimestamp));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)toTimestamp("2015-06-01 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)toTimestamp("2015-06-30 00:00:00.000")));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年05月31日 00時00分00.000秒'は、'2015年06月01日 00時00分00.000秒'以降から'2015年06月30日 00時00分00.000秒'以前の日時を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.sqlTimestamp = toTimestamp("2015-06-01 00:00:00.000");
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator<Date>(toTimestamp("2015-06-01 00:00:00.000"), toTimestamp("2015-06-30 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
    

}
