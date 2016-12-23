package com.gh.mygreen.xlsmapper.expression;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.el.MethodNotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Taglib;
import com.github.mygreen.expression.el.tld.TldLoader;


/**
 * {@link ExpressionLangaugeEL2Impl}のテスタ
 *
 * @since 1.6
 * @author T.TSUCHIE
 *
 */
public class ExpressionLangaugeEL2ImplTest {
    
    private ExpressionLangaugeEL2Impl el;
    
    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLangaugeEL2Impl();
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
     * empty句のテスト
     */
    @Test
    public void testEvaluate_empty() {
        
        String expression = "empty label ? '空です' : label";
        
        Map<String, Object> vars = new HashMap<>();
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("空です"));
        
    }
    
    /**
     * formatterのテスト
     */
    @Test
    public void testEvaluate_format() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        String expression = "formatter.format('%1$tY/%1$tm/%1$td', currentDate)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("currentDate", date);
        vars.put("formatter", new Formatter(Locale.getDefault()));
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("2015/04/15"));
        
    }
    
    /**
     * formatterのテスト - {@link FormatterWrapper}でラップしてある
     */
    @Test
    public void testEvaluate_format_wrap() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        String expression = "formatter.format('%1$tY/%1$tm/%1$td', currentDate)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("currentDate", date);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("2015/04/15"));
        
    }
    
    /**
     * ラムダ式のテスト - EL2.xでは非さぽーとのため失敗する
     */
    @Test(expected=ExpressionEvaluationException.class)
    public void testEvaluate_lambda() {
        
        String expression = "sum=0;list.stream().forEach(x->(sum=sum+x));sum";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("list", Arrays.asList(1, 2, 3, 4, 5, 6));
        
        try {
            el.evaluate(expression, vars);
            fail();
            
        } catch(Exception e) {
            assertThat(e, is(instanceOf(ExpressionEvaluationException.class)));
            assertThat(e.getCause(), is(instanceOf(MethodNotFoundException.class)));
            
            throw e;
        }
        
    }
    
    /**
     * 式中のエスケープのテスト
     */
    @Test
    public void testEvaluate_escape() {
        
        String expression = "'Helo World}' += formatter.format('%1.1f', validatedValue)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", 12.34);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("Helo World}12.3"));
        
        
    }
    
    /**
     * EL式でExcelの数式を組み立てた場合のエスケープの確認
     */
    @Test
    public void testEvaluate_buildFormula() {
        
        String expression = "columnNumber == 7 ? 'COUNTIF(D' += rowNumber += ':F' += rowNumber += ', \"出席\")' : ''";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("columnNumber", 6);
        vars.put("rowNumber", 10);
        
        String eval1 = (String) el.evaluate(expression, vars);
        assertThat(eval1, is(""));
        
        vars.clear();
        vars.put("columnNumber", 7);
        vars.put("rowNumber", 10);
        
        String eval2 = (String) el.evaluate(expression, vars);
        assertThat(eval2, is("COUNTIF(D10:F10, \"出席\")"));
        
        
    }
    
    /**
     * EL関数の確認
     */
    @Test
    public void testEvaluate_function() throws Exception {
        
        // EL関数の登録
        TldLoader loader = new TldLoader();
        Taglib taglib = loader.load(ExpressionLangaugeEL2ImplTest.class.getResourceAsStream("/com/gh/mygreen/xlsmapper/expression/xlsmapper.tld"));
        el.register(taglib);
        
        String expression = "x:colToAlpha(columnNumber)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("columnNumber", 1);
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("A"));
        
    }
    
    /**
     * EL関数の確認 - 存在しないクラスの場合
     */
    @Test(expected=ExpressionEvaluationException.class)
    public void testEvaluate_function_notFound() throws Exception {
        
        // EL関数の登録
        TldLoader loader = new TldLoader();
        Taglib taglib = loader.load(ExpressionLangaugeEL2ImplTest.class.getResourceAsStream("/error_notFoundClass.tld"));
        el.register(taglib);
        
        String expression = "1 + 2";
        Map<String, Object> vars = new HashMap<>();
        try {
            el.evaluate(expression, vars);
            
            fail();
            
        } catch(Exception e) {
            assertThat(e, is(instanceOf(ExpressionEvaluationException.class)));
            assertThat(e.getCause(), is(instanceOf(ClassNotFoundException.class)));
            
            throw e;
        }
    }
    
    /**
     * 不正なEL式
     */
    @Test(expected=ExpressionEvaluationException.class)
    public void testEvaluate_invalidExp() {
        
        String expression = "y:test(D + C + 1, 23)";
        
        Map<String, Object> vars = new HashMap<>();
        
        el.evaluate(expression, vars);
        
        fail();
        
    }
}
