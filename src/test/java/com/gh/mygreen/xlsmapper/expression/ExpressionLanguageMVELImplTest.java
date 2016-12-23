package com.gh.mygreen.xlsmapper.expression;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link ExpressionLanguageMVELImpl}のテスタ
 *
 * @since 1.6
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageMVELImplTest {
    
    private ExpressionLanguageMVELImpl el;
    
    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLanguageMVELImpl();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluate_exprNull() {
        
        Map<String, Object> vars = new HashMap<>();
        
        el.evaluate(null, vars);
        fail();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluate_exprEmpty() {
        
        Map<String, Object> vars = new HashMap<>();
        
        el.evaluate("", vars);
        fail();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluate_valueNull() {
        
        {
            // empty
            Map<String, Object> vars = new HashMap<>();
            el.evaluate("1+2", vars);
        }
        
        {
            el.evaluate("1+2", null);
            fail();
        
        }
    }
    
    /**
     * empty句の比較テスト
     */
    @Test
    public void testEvaluate_empty() {
        
        String expression = "label == empty ? '空です' : label";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("label", null);
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("空です"));
        
    }
    
    /**
     * fomratterのテスト
     */
    @Test
    public void testEvaluate_formatter() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        String expression = "formatter.format('%1$tY/%1$tm/%1$td', currentDate)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("currentDate", date);
        vars.put("formatter", new Formatter(Locale.getDefault()));
        
        String eval = (String) el.evaluate(expression, vars).toString();
        assertThat(eval, is("2015/04/15"));
        
    }
    
    @Test(expected=ExpressionEvaluationException.class)
    public void testEvalate_error_exp() {
        
        String expression = "aaa ?  label";
        
        Map<String, Object> vars = new HashMap<>();
        
        el.evaluate(expression, vars);
        fail();
    }
    
    /**
     * 独自の関数の利用
     */
    @Test
    public void testEvaluate_function() {
        
        // staticメソッドの登録
        el.getParserConfiguration().addImport("f", CustomFunctions.class);
        
        String expression = "f.colToAlpha(columnNumber)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("columnNumber", 1);
        
        String eval = (String) el.evaluate(expression, vars).toString();
        assertThat(eval, is("A"));
        
    }
}
