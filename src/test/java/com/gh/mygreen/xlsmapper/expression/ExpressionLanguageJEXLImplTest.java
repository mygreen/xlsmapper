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
 * {@link ExpressionLanguageJEXLImpl}のテスタ。
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLImplTest {
    
    private ExpressionLanguageJEXLImpl el;
    
    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLanguageJEXLImpl();
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
    
    @Test
    public void testEvalate_empty() {
        
        String expression = "empty(label) ? '空です' : label";
        
        Map<String, Object> vars = new HashMap<>();
        
        String eval1 = (String) el.evaluate(expression, vars);
        assertThat(eval1, is("空です"));
        
        vars.put("label", "Hello world.");
        String eval2 = (String) el.evaluate(expression, vars);
        assertThat(eval2, is("Hello world."));
        
        
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
     * 名前空間付きの関数
     */
    @Test
    public void testEvalate_function() {
        
        // 関数の登録
        Map<String, Object> funcs = new HashMap<>(); 
        funcs.put("x", CustomFunctions.class);
        el.getJexlEngine().setFunctions(funcs);
        
        String expression = "x:colToAlpha(columnNumber)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("columnNumber", 1);
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("A"));
        
    }
    
    
}
