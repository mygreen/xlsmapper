package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.ArraySizeValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.LengthMaxValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MaxValidator;


/**
 * 配列や{@link Collection}型に対するValidatorのテスト。
 * <p>テスト対象のValidator。
 * <ul>
 *  <li>{@link ArraySizeValidator}</li>
 * </ul>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ArrayValidatorTest {

    /**
     * テスト用のシートクラス
     *
     */
    private static class SampleSheet {

        private Map<String, Point> positions;

        private Map<String, String> labels;

        private String[] arrayStr;

        private int[] arrayInt;

        private List<String> listStr;

        private List<Integer> listInt;

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
     * 配列型のテスト
     */
    public static class ArrayTest {

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

            this.fieldName = "arrayStr";

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

            sheet.arrayStr = null;
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

        }

        /**
         * 必須チェック - 値が空の配列
         */
        @Test
        public void test_required_empty() {

            sheet.arrayStr = new String[0];
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

        }

        /**
         * 必須チェック - 値がある
         */
        @Test
        public void test_required_valid() {

            sheet.arrayStr = new String[] {"", "abc", "あいう"};
            ArrayCellField<String>field = new ArrayCellField<>(fieldName, errors);
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

            sheet.arrayStr = null;
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(false);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 必須チェック - オプション指定 - 配列のサイズが0
         */
        @Test
        public void test_optional_empty() {

            sheet.arrayStr = new String[0];
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
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

            sheet.arrayStr = new String[] {"", "abc", "あいう"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(false);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素数のサイズのチェック - 値が不正
         */
        @Test
        public void test_arraySize_wrong() {

            sheet.arrayStr = new String[] {"", "abc", "あいう"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<>(0, 2));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.arraySize"));

            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.arrayStr));
            assertThat(fieldError.getVariables(), hasEntry("min", 0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)2));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の要素数'3'は、0～2個以内で値を設定してください。"));

        }

        /**
         * 要素数のサイズのチェック - 値が正常
         */
        @Test
        public void test_arraySize_valid() {

            sheet.arrayStr = new String[] {"", "abc"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<>(0, 2));

            field.validate();

            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素数のサイズのチェック - グループ指定(一致しない)
         */
        @Test
        public void test_arraySize_group_skip() {

            sheet.arrayStr = new String[] {"", "abc", "あいう"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<String>(0, 2).addGroup(Group1.class));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素数のサイズのチェック - グループ指定(一致する)
         */
        @Test
        public void test_arraySize_group_match() {

            sheet.arrayStr = new String[] {"", "abc", "あいう"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<String>(0, 2).addGroup(Group1.class));

            field.validate(Group1.class);

            FieldError fieldError = errors.getFirstFieldError(fieldName).get();

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の要素数'3'は、0～2個以内で値を設定してください。"));


        }

        /**
         * 要素の必須チェック - 値がnull
         */
        @Test
        public void test_element_required_null() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayStr = new String[] {"123", "abc", null};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequiredElement(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の値は必須です。"));

        }

        /**
         * 要素の必須チェック - 値が空文字
         */
        @Test
        public void test_element_required_empty() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayStr = new String[] {"123", "abc", ""};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequiredElement(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の値は必須です。"));

        }

        /**
         * 要素の必須チェック - 値が正常
         */
        @Test
        public void test_element_required_valid() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayStr = new String[] {"123", "abc", "あいう"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequiredElement(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素の最大文字長のチェック - 値が不正
         */
        @Test
        public void test_elemnt_lengthMax_wrong() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayStr = new String[] {"", "abc", "あいうえお"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new LengthMaxValidator(3));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthMax"));

            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.arrayStr[2]));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)5));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)3));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の文字長'5'は、3文字以内で値を設定してください。"));

        }

        /**
         * 要素の最大文字長のチェック - 値が正常
         */
        @Test
        public void test_elemnt_lengthMax_valid() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayStr = new String[] {"", "abc", "あいう"};
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new LengthMaxValidator(3));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

    }

    /**
     * プリミティブ型の配列のテスト
     *
     */
    public static class PrimitiveArrayTest {

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

            this.fieldName = "arrayInt";

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

            sheet.arrayInt = null;
            ArrayCellField<Integer> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

        }

        /**
         * 必須チェック - 値が空の配列
         */
        @Test
        public void test_required_empty() {

            sheet.arrayInt = new int[0];
            ArrayCellField<Integer> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

        }

        /**
         * 必須チェック - 値がある
         */
        @Test
        public void test_required_valid() {

            sheet.arrayInt = new int[] {1, 2, 3};
            ArrayCellField<Integer>field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }

        /**
         * 要素数のサイズのチェック - 値が不正
         */
        @Test
        public void test_arraySize_wrong() {

            sheet.arrayInt = new int[] {1, 2, 3};
            ArrayCellField<Integer> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<>(0, 2));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.arraySize"));

            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.arrayInt));
            assertThat(fieldError.getVariables(), hasEntry("min", 0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)2));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の要素数'3'は、0～2個以内で値を設定してください。"));

        }

        /**
         * 要素数のサイズのチェック - 値が正常
         */
        @Test
        public void test_arraySize_valid() {

            sheet.arrayInt = new int[] {1, 2};
            ArrayCellField<Integer> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<>(0, 2));

            field.validate();

            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素の最大のチェック - 値が不正
         */
        @Test
        public void test_elemnt_max_wrong() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayInt = new int[] {1, 2, 5};
            ArrayCellField<Integer> field = new ArrayCellField<>(fieldName, errors);
            field.add(new MaxValidator<>(3));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.max"));

            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.arrayInt[2]));
            assertThat(fieldError.getVariables(), hasEntry("inclusive", (Object)true));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)3));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の値'5'は、'3'以下の値を設定してください。"));

        }

        /**
         * 要素の最大のチェック - 値が正常
         */
        @Test
        public void test_elemnt_max_valid() {

         // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.arrayInt = new int[] {1, 2, 3};
            ArrayCellField<Integer> field = new ArrayCellField<>(fieldName, errors);
            field.add(new MaxValidator<>(3));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

    }

    /**
     * リストのテスト
     */
    public static class ListTest {

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

            this.fieldName = "listStr";

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

            sheet.listStr = null;
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

        }

        /**
         * 必須チェック - 値が空の配列
         */
        @Test
        public void test_required_empty() {

            sheet.listStr = Collections.emptyList();
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

        }

        /**
         * 必須チェック - 値がある
         */
        @Test
        public void test_required_valid() {

            sheet.listStr = Arrays.asList("", "abc", "あいう");
            ArrayCellField<String>field = new ArrayCellField<>(fieldName, errors);
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

            sheet.listStr = null;
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(false);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 必須チェック - オプション指定 - 配列のサイズが0
         */
        @Test
        public void test_optional_empty() {

            sheet.listStr = Collections.emptyList();
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
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

            sheet.listStr =  Arrays.asList("", "abc", "あいう");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequired(false);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素数のサイズのチェック - 値が不正
         */
        @Test
        public void test_arraySize_wrong() {

            sheet.listStr =  Arrays.asList("", "abc", "あいう");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<>(0, 2));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(fieldName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(fieldName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.arraySize"));

            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.listStr));
            assertThat(fieldError.getVariables(), hasEntry("min", 0));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)2));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の要素数'3'は、0～2個以内で値を設定してください。"));

        }

        /**
         * 要素数のサイズのチェック - 値が正常
         */
        @Test
        public void test_arraySize_valid() {

            sheet.listStr =  Arrays.asList("", "abc");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<>(0, 2));

            field.validate();

            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素数のサイズのチェック - グループ指定(一致しない)
         */
        @Test
        public void test_arraySize_group_skip() {

            sheet.listStr =  Arrays.asList("", "abc", "あいう");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<String>(0, 2).addGroup(Group1.class));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素数のサイズのチェック - グループ指定(一致する)
         */
        @Test
        public void test_arraySize_group_match() {

            sheet.listStr =  Arrays.asList("", "abc", "あいう");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new ArraySizeValidator<String>(0, 2).addGroup(Group1.class));

            field.validate(Group1.class);

            FieldError fieldError = errors.getFirstFieldError(fieldName).get();

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A5)の要素数'3'は、0～2個以内で値を設定してください。"));


        }

        /**
         * 要素の必須チェック - 値がnull
         */
        @Test
        public void test_element_required_null() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.listStr =  Arrays.asList("123", "abc", null);
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequiredElement(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の値は必須です。"));

        }

        /**
         * 要素の必須チェック - 値が空文字
         */
        @Test
        public void test_element_required_empty() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.listStr =  Arrays.asList("123", "abc", "");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequiredElement(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.required"));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の値は必須です。"));

        }

        /**
         * 要素の必須チェック - 値が正常
         */
        @Test
        public void test_element_required_valid() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.listStr =  Arrays.asList("123", "abc", "あいう");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.setRequiredElement(true);

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

        /**
         * 要素の最大文字長のチェック - 値が不正
         */
        @Test
        public void test_elemnt_lengthMax_wrong() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.listStr =  Arrays.asList("", "abc", "あいうえお");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new LengthMaxValidator(3));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(elementName).get();
            assertThat(fieldError.getAddressAsOptional().get().toPoint(), is(sheet.positions.get(elementName)));
            assertThat(fieldError.getLabelAsOptional().get(), is(sheet.labels.get(elementName)));
            assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.lengthMax"));

            assertThat(fieldError.getVariables(), hasEntry("validatedValue", (Object)sheet.listStr.get(2)));
            assertThat(fieldError.getVariables(), hasEntry("length", (Object)5));
            assertThat(fieldError.getVariables(), hasEntry("max", (Object)3));
            assertThat(fieldError.getVariables(), not(hasKey("fieldFormatter")));

            // メッセージ
            String message = errorFormatter.format(fieldError);
            assertThat(message, is("[サンプルシート]:値1 - セル(A7)の文字長'5'は、3文字以内で値を設定してください。"));

        }

        /**
         * 要素の最大文字長のチェック - 値が正常
         */
        @Test
        public void test_elemnt_lengthMax_valid() {

            // 要素の情報を設定する
            String elementName = String.format("%s[%d]", fieldName, 2);
            sheet.addPosition(elementName, toPointAddress("A7")).addLabel(elementName, "値1");

            sheet.listStr =  Arrays.asList("", "abc", "あいう");
            ArrayCellField<String> field = new ArrayCellField<>(fieldName, errors);
            field.add(new LengthMaxValidator(3));

            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));

        }

    }
}
