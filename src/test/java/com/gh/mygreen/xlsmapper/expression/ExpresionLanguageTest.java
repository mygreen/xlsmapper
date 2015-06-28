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


public class ExpresionLanguageTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testEL3_empty() {
        
        ExpressionLanguageELImpl el = new ExpressionLanguageELImpl();
        
        String expression = "empty label ? '空です' : label";
        
        Map<String, Object> vars = new HashMap<>();
        
        String eval = (String) el.evaluateWithEL3(expression, vars);
        assertThat(eval, is("空です"));
//        System.out.println(eval);
        
    }
    
    @Test
    public void testEL3_format() {
        
        ExpressionLanguageELImpl el = new ExpressionLanguageELImpl();
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");

        String expression = "formatter.format('%1$tY/%1$tm/%1$td', currentDate)";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("currentDate", date);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
//        vars.put("formatter", new Formatter());
        
        String eval = (String) el.evaluateWithEL3(expression, vars);
        assertThat(eval, is("2015/04/15"));
//        System.out.println(eval);
        
    }
    
    @Test
    public void testEL3_lambda() {
        
        ExpressionLanguageELImpl el = new ExpressionLanguageELImpl();
        
        String expression = "sum=0;list.stream().forEach(x->(sum=sum+x));sum";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("list", Arrays.asList(1, 2, 3, 4, 5, 6));
        
        long eval = (long) el.evaluateWithEL3(expression, vars);
        assertThat(eval, is(21L));
//        System.out.println(val);
        
    }

}
