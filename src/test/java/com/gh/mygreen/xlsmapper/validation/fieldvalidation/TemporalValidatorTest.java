package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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
 * {@link TemporalAccessor}の子クラスに対するValidatorのテスト
 * 
 * <p>様々な組み合わせのテストは、{@link DateValidatorTest}で実施しているので、メッセージに関するテストのみ実施する。
 * 
 * <p>テスト対象のValidator。
 * <ul>
 *  <li>{@link MinValidator}</li>
 *  <li>{@link MaxValidator}</li>
 *  <li>{@link RangeValidator}</li>
 * </ul>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class TemporalValidatorTest {
    
    /**
     * テスト用のシートクラス
     *
     */
    private static class SampleSheet {
        
        private Map<String, Point> positions;
        
        private Map<String, String> labels;
        
        private LocalDateTime localDateTime;
        
        private LocalDate localDate;
        
        private LocalTime localTime;
        
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
    
    /**
     * {@link LocalDateTime}型に対するテスタ
     *
     */
    public static class LocalDateTimeValidatorTest {
        
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
        
        private FieldFormatter<LocalDateTime> fieldFormatter = new FieldFormatter<LocalDateTime>() {
            
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu年MM月dd日 HH時mm分ss秒");
            
            @Override
            public String format(LocalDateTime value) {
                return formatter.format(value);
            }
        };
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "localDateTime";
            
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
            
            sheet.localDateTime = LocalDateTime.of(2015, 6, 2, 0, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new MaxValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDateTime));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-06-02T00:00'は、'2015-06-01T00:00'より前の日時を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 6, 2, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDateTime));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
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
            
            sheet.localDateTime = LocalDateTime.of(2015, 6, 1, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 5, 31, 0, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new MinValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDateTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-05-31T00:00'は、'2015-06-01T00:00'より後の日時を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 5, 31, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDateTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年05月31日 00時00分00秒'は、'2015年06月01日 00時00分00秒'以降の日時を設定してください。"));
            
            
        }
        
        /**
         * 最小値チェック - 値が正しい
         */
        @Test
        public void test_min_valid() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 6, 1, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - 値が不正
         */
        @Test
        public void test_range_wrong() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 7, 1, 0, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new RangeValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0), LocalDateTime.of(2015, 6, 30, 0, 0, 0), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDateTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDateTime.of(2015, 6, 30, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015-07-01T00:00'は、'2015-06-01T00:00'より後から'2015-06-30T00:00'より前の日時を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 7, 1, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0), LocalDateTime.of(2015, 6, 30, 0, 0, 0)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDateTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDateTime.of(2015, 6, 1, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDateTime.of(2015, 6, 30, 0, 0, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("fieldFormatter", fieldFormatter));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:日時 - セル(A5)の値'2015年07月01日 00時00分00秒'は、'2015年06月01日 00時00分00秒'以降から'2015年06月30日 00時00分00秒'以前の日時を設定してください。"));
            
            
        }
        
        /**
         * 範囲値チェック - 値が正しい
         */
        @Test
        public void test_range_valid() {
            
            sheet.localDateTime = LocalDateTime.of(2015, 6, 1, 0, 0, 0);
            CellField<LocalDateTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator(LocalDateTime.of(2015, 6, 1, 0, 0, 0), LocalDateTime.of(2015, 6, 30, 0, 0, 0)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
    
    /**
     * {@link LocalDate}型に対するテスタ
     *
     */
    public static class LocalDateValidatorTest {
        
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
        
        private FieldFormatter<LocalDate> fieldFormatter = new FieldFormatter<LocalDate>() {
            
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu年MM月dd日");
            
            @Override
            public String format(LocalDate value) {
                return formatter.format(value);
            }
        };
        
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "localDate";
            
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
            
            sheet.localDate = LocalDate.of(2015, 6, 2);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new MaxValidator(LocalDate.of(2015, 6, 1), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDate));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDate.of(2015, 6, 1)));
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
            
            sheet.localDate = LocalDate.of(2015, 6, 2);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator(LocalDate.of(2015, 6, 1)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDate));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDate.of(2015, 6, 1)));
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
            
            sheet.localDate = LocalDate.of(2015, 6, 1);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator(LocalDate.of(2015, 6, 1)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.localDate = LocalDate.of(2015, 5, 31);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new MinValidator(LocalDate.of(2015, 6, 1), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDate.of(2015, 6, 1)));
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
            
            sheet.localDate = LocalDate.of(2015, 5, 31);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator(LocalDate.of(2015, 6, 1)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDate.of(2015, 6, 1)));
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
            
            sheet.localDate = LocalDate.of(2015, 6, 1);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator(LocalDate.of(2015, 6, 1)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - 値が不正
         */
        @Test
        public void test_range_wrong() {
            
            sheet.localDate = LocalDate.of(2015, 7, 1);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new RangeValidator(LocalDate.of(2015, 6, 1), LocalDate.of(2015, 6, 30), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDate.of(2015, 6, 1)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDate.of(2015, 6, 30)));
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
            
            sheet.localDate = LocalDate.of(2015, 7, 1);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator(LocalDate.of(2015, 6, 1), LocalDate.of(2015, 6, 30)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localDate));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalDate.of(2015, 6, 1)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalDate.of(2015, 6, 30)));
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
            
            sheet.localDate = LocalDate.of(2015, 6, 1);
            CellField<LocalDate> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator(LocalDate.of(2015, 6, 1), LocalDate.of(2015, 6, 30)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
    
    /**
     * {@link LocalTime}型に対するテスタ
     *
     */
    public static class LocalTimeValidatorTest {
        
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
        
        private FieldFormatter<LocalTime> fieldFormatter = new FieldFormatter<LocalTime>() {
            
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH時mm分ss秒");
            
            @Override
            public String format(LocalTime value) {
                return formatter.format(value);
            }
        };
        
        @Before
        public void setUp() {
            
            this.errorFormatter = new SheetErrorFormatter();
            
            this.fieldName = "localTime";
            
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
            
            sheet.localTime = LocalTime.of(9, 11, 13);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new MaxValidator(LocalTime.of(9, 10, 0), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localTime));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalTime.of(9, 10, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'09:11:13'は、'09:10'より前の時間を設定してください。"));
            
        }
        
        /**
         * 最大値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_max_wrong_withFormatter() {
            
            sheet.localTime = LocalTime.of(9, 11, 13);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator(LocalTime.of(9, 10, 0)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localTime));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalTime.of(9, 10, 0)));
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
            
            sheet.localTime = LocalTime.of(9, 10, 0);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator(LocalTime.of(9, 10, 0)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 最小値チェック - 値が不正
         */
        @Test
        public void test_min_wrong() {
            
            sheet.localTime = LocalTime.of(9, 9, 13);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new MinValidator(LocalTime.of(9, 10, 0), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalTime.of(9, 10, 0)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'09:09:13'は、'09:10'より後の時間を設定してください。"));
            
        }
        
        /**
         * 最小値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_min_wrong_withFormatter() {
            
            sheet.localTime = LocalTime.of(9, 9, 13);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator(LocalTime.of(9, 10, 0)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.min"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalTime.of(9, 10, 0)));
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
            
            sheet.localTime = LocalTime.of(9, 10, 0);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MinValidator(LocalTime.of(9, 10, 0)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * 範囲値チェック - 値が不正
         */
        @Test
        public void test_range_wrong() {
            
            sheet.localTime = LocalTime.of(11, 00, 00);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            
            field.add(new RangeValidator(LocalTime.of(9, 10, 0), LocalTime.of(10, 59, 59), false));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalTime.of(9, 10, 0)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalTime.of(10, 59, 59)));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)false));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));
            
            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:時間 - セル(A5)の値'11:00'は、'09:10'より後から'10:59:59'より前の時間を設定してください。"));
            
        }
        
        /**
         * 範囲値チェック - 値が不正 - フォーマット指定あり
         */
        @Test
        public void test_range_wrong_withFormatter() {
            
            sheet.localTime = LocalTime.of(11, 00, 00);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator(LocalTime.of(9, 10, 0), LocalTime.of(10, 59, 59)));
            
            field.setFormatter(fieldFormatter);
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.range"));
            
            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.localTime));
            assertThat(fieldError.getVariables(), hasEntry("min", (Object)LocalTime.of(9, 10, 0)));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)LocalTime.of(10, 59, 59)));
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
            
            sheet.localTime = LocalTime.of(9, 10, 0);
            CellField<LocalTime> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new RangeValidator(LocalTime.of(9, 10, 0), LocalTime.of(10, 59, 59)));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
    }
}
