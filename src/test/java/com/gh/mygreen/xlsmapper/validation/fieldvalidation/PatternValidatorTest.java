package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl.PatternValidator;

/**
 * {@link PatternValidator}のテスタ
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class PatternValidatorTest {
    
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
     * 文字列がパターンに一致するかどうか
     */
    @Test
    public void test_validation_str_pattern() throws Exception {
        
        String fieldName = "str";
        
        SampleSheet sheet = new SampleSheet();
        sheet.addPosition(fieldName, toPointAddress("A5")).addLabel(fieldName, "名前");
        
        SheetBindingErrors<SampleSheet> errors = new SheetBindingErrors<>(sheet);
        errors.setSheetName("サンプルシート");
        
        
        {
            // パターン(値がnull)
            errors.clearAllErrors();
            sheet.str = null;
            
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new PatternValidator(".+@.+"));
            field.validate();
            
            FieldError fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // パターン(値が不正)
            errors.clearAllErrors();
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
        }
        
        {
            // パターン(値が正しい)
            errors.clearAllErrors();
            sheet.str = "hoge@example.com";
            
            CellField<String> field = new CellField<>(fieldName, errors);
            field.setRequired(false);
            field.add(new PatternValidator(".+@.+"));
            field.validate();
            
            FieldError  fieldError = errors.getFirstFieldError(fieldName).orElse(null);
            assertThat(fieldError, is(nullValue()));
        }
        
        {
            // パターン名を指定（値が不正）
            errors.clearAllErrors();
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
