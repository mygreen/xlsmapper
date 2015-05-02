package com.gh.mygreen.xlsmapper.expression.el;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.LocalELContext;

/**
 * EL式のテスタ
 *
 */
public class LocalELContextTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testNormal() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", 3);
        vars.put("currentDate", date);
        vars.put("min", 1);
        vars.put("max", 10);
//        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
        
        try {
            LocalELContext context = new LocalELContext();
            
            ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
            for(Map.Entry<String, Object> entry : vars.entrySet()) {
                
                // ローカル変数の作成
                ValueExpression exp =  expressionFactory.createValueExpression(context,
                        String.format("${%s}", entry.getKey()),
                        Object.class);
                exp.setValue(context, entry.getValue());
                
            }
            
            ValueExpression expFormatter = expressionFactory.createValueExpression(
                    new FormatterWrapper(Locale.getDefault()), FormatterWrapper.class);
            context.setVariable("formatter", expFormatter);
            
            ValueExpression exp2 = expressionFactory.createValueExpression(context, "${min + max}", Object.class);
            long result2 = (long) exp2.getValue(context);
            assertThat(result2, is(11L));
//            System.out.println(result);
            
            ValueExpression exp3 = expressionFactory.createValueExpression(context, "${formatter.format('%1$tY/%1$tm/%1$td', currentDate)}", String.class);
            String result3 = (String) exp3.getValue(context);
            assertThat(result3, is("2015/04/15"));
//            System.out.println(result3);
            
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testNormal2() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", 3);
        vars.put("currentDate", date);
        vars.put("min", 1);
        vars.put("max", 10);
        vars.put("formatter", new FormatterWrapper(Locale.getDefault()));
        
        try {
            LocalELContext context = new LocalELContext();
            
            ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
            
            for(Map.Entry<String, Object> entry : vars.entrySet()) {
                ValueExpression exp =  expressionFactory.createValueExpression(entry.getValue(), Object.class);
                context.setVariable(entry.getKey(), exp);
            }
            
            ValueExpression exp2 = expressionFactory.createValueExpression(context, "${min + max}", Object.class);
            long result2 = (long) exp2.getValue(context);
            assertThat(result2, is(11L));
//            System.out.println(result2);
            
            ValueExpression exp3 = expressionFactory.createValueExpression(context, "${formatter.format('%1$tY/%1$tm/%1$td', currentDate)}", String.class);
            String result3 = (String) exp3.getValue(context);
            assertThat(result3, is("2015/04/15"));
//            System.out.println(result3);
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
}
