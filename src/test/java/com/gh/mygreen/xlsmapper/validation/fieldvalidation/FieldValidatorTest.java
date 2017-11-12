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
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.validation.DefaultGroup;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.SheetErrorFormatter;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.MaxValidator;

/**
 * {@link FieldValidator}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class FieldValidatorTest {
    
    /**
     * テスト用のシートクラス
     *
     */
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
    
    private static interface Group1 {}
    private static interface Group2 {}
    private static interface Group3 extends Group1, Group2 {}
    
    /**
     * グループ指定のテスト
     *
     */
    public static class GroupTest {
        
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
            
            this.fieldName = "date";
            
            this.sheet = new SampleSheet();
            sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "日時");
            
            this.errors = new SheetBindingErrors<>(sheet);
            errors.setSheetName("サンプルシート");
            
            errors.clearAllErrors();
        }
        
        /**
         * グループ指定 - グループ設定なし -  グループ指定しない
         */
        @Test
        public void test_group_setNo_noGroup() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * グループ指定 - グループ設定なし -  デフォルトグループ
         */
        @Test
        public void test_group_setNo_default() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")));
            
            field.validate(DefaultGroup.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * グループ指定 - デフォルトグループ設定 -  指定しない
         */
        @Test
        public void test_group_setDefault_no() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(DefaultGroup.class));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
        }
        
        /**
         * グループ指定 - デフォルトグループ設定-  デフォルトグループ指定
         */
        @Test
        public void test_group_setDefault_default() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(DefaultGroup.class));
            
            field.validate(DefaultGroup.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
        }
        
        /**
         * グループ指定 - 指定なし -  該当しない
         */
        @Test
        public void test_group_noSet_notMatch() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(Group1.class));
            
            field.validate();
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        /**
         * グループ指定 - 指定あり -  該当しない
         */
        @Test
        public void test_group_set_notMatch() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(Group1.class));
            
            field.validate(Group2.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
            
        }
        
        /**
         * グループ指定あり - 該当する
         */
        @Test
        public void test_group_set_match() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(Group1.class));
            
            field.validate(Group1.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
        
        /**
         * グループ指定あり - 該当する - 継承
         */
        @Test
        public void test_group_set_match_inherit() {
            
            sheet.date = toTimestamp("2015-06-02 00:00:00.000");
            
            CellField<Date> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new MaxValidator<Date>(toTimestamp("2015-06-01 00:00:00.000")).addGroup(Group1.class));
            
            field.validate(Group3.class);
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(not(nullValue())));
            
        }
    
    }
}
