package com.gh.mygreen.xlsmapper.expression.el;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.expression.ExpressionLanguageELImpl;
import com.gh.mygreen.xlsmapper.expression.el.FormatterWrapper;
import com.gh.mygreen.xlsmapper.expression.el.LocalELContext;

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
        
        String message = "{validatedValue} は、{min}～{max}の範囲で入力してください。";
        
        int validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("currentDate", new Date());
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
            System.out.println(exp2.getValue(context));
            
            ValueExpression exp3 = expressionFactory.createValueExpression(context, "${formatter.format('%1$tY/%1$tm/%1$td%n', currentDate)}", String.class);
            System.out.println(exp3.getValue(context));
            
            ValueExpression exp4 = expressionFactory.createValueExpression(context, message, String.class);
            System.out.println(exp4.getValue(context));
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testNormal2() {
        
        String message = "{validatedValue} は、{min}～{max}の範囲で入力してください。";
        
        int validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("currentDate", new Date());
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
            System.out.println(exp2.getValue(context));
            
            ValueExpression exp3 = expressionFactory.createValueExpression(context, "${formatter.format('%1$tY/%1$tm/%1$td%n', currentDate)}", String.class);
            System.out.println(exp3.getValue(context));
            
            ValueExpression exp4 = expressionFactory.createValueExpression(context, message, String.class);
            System.out.println(exp4.getValue(context));
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
    
}
