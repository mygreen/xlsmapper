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

import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;

/**
 * {@link PatternValidator}のテスタ
 * 
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
        
        SheetBindingErrors errors = new SheetBindingErrors(SampleSheet.class);
        errors.setSheetName("サンプルシート");
        
        CellField<String> field;
        CellFieldError fieldError;
        
        // パターン(値がnull)
        errors.clearAllErrors();
        sheet.str = null;
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError, is(nullValue()));
        
        // パターン(値が不正)
        errors.clearAllErrors();
        sheet.str = "あいうえ";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+"));
        
        field.validate(errors);
        fieldError = errors.getFirstCellFieldError(fieldName);
        assertThat(fieldError.getCellAddress(), is(sheet.positions.get(fieldName)));
        assertThat(fieldError.getLabel(), is(sheet.labels.get(fieldName)));
        assertThat(fieldError.getCodes(), hasItemInArray("cellFieldError.pattern"));
        
        assertThat(fieldError.getVars(), hasEntry("validatedValue", (Object)sheet.str));
        assertThat(fieldError.getVars(), hasEntry("pattern", (Object)".+@.+"));
        
        // パターン(値が正しい)
        errors.clearAllErrors();
        sheet.str = "hoge@example.com";
        field = new CellField<String>(sheet, fieldName);
        field.setRequired(false);
        field.add(new PatternValidator(".+@.+"));
        
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
