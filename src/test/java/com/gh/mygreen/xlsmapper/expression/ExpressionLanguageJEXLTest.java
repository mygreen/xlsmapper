package com.gh.mygreen.xlsmapper.expression;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link ExpressionLanguageJEXLImpl}のテスタ。
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLTest {
    
    private ExpressionLanguageJEXLImpl el;

    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLanguageJEXLImpl();
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_empty() {
        
        String expression = "empty(label) ? '空です' : label";
        
        Map<String, Object> vars = new HashMap<>();
        
        String eval1 = (String) el.evaluate(expression, vars);
        assertThat(eval1, is("空です"));
        
        vars.put("label", "Hello world.");
        String eval2 = (String) el.evaluate(expression, vars);
        assertThat(eval2, is("Hello world."));
        
        
    }
    
    @Test(expected=ExpressionEvaluationException.class)
    public void test_error_exp() {
        
        String expression = "aaa ?  label";
        
        Map<String, Object> vars = new HashMap<>();
        
        String eval = (String) el.evaluate(expression, vars);
        fail();
    }
    
    /**
     * 名前空間付きの関数
     */
    @Test
    public void test_function() {
        
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
