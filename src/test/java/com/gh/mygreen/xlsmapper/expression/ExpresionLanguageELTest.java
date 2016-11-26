package com.gh.mygreen.xlsmapper.expression;

import static com.gh.mygreen.xlsmapper.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Taglib;
import com.github.mygreen.expression.el.tld.TldLoader;

/**
 * {@link ExpressionLanguageELImpl}のテスタ
 * 
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
public class ExpresionLanguageELTest {
    
    private AbstractExpressionLanguageELImpl el;
    
    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLanguageELImpl3();
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testEL3_empty() {
        
        String expression = "empty label ? '空です' : label";
        
        Map<String, Object> vars = new HashMap<>();
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("空です"));
//        System.out.println(eval);
        
    }
    
    @Test
    public void testEL3_format() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");

        String expression = "formatter.format('%1$tY/%1$tm/%1$td', currentDate)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("currentDate", date);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
//        vars.put("formatter", new Formatter());
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("2015/04/15"));
//        System.out.println(eval);
        
    }
    
    @Test
    public void testEL3_lambda() {
        
        String expression = "sum=0;list.stream().forEach(x->(sum=sum+x));sum";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("list", Arrays.asList(1, 2, 3, 4, 5, 6));
        
        long eval = (long) el.evaluate(expression, vars);
        assertThat(eval, is(21L));
//        System.out.println(val);
        
    }
    
    @Test
    public void testEL3_escape() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");

        String expression = "'Helo World}' += formatter.format('%1.1f', validatedValue)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", 12.34);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("Helo World}12.3"));
//        System.out.println(eval);
        
        
    }
    
    @Test
    public void testEL3_formula() {
        
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
//        System.out.println(eval);
        
        
    }
    
    @Test
    public void testEL3_function() throws Exception {
        
        // EL関数の登録
        TldLoader loader = new TldLoader();
        Taglib taglib = loader.load(ExpresionLanguageELTest.class.getResourceAsStream("/com/gh/mygreen/xlsmapper/expression/xlsmapper.tld"));
        el.register(taglib);
        
        String expression = "x:colToAlpha(columnNumber)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("columnNumber", 1);
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval, is("A"));
        
    }

}
