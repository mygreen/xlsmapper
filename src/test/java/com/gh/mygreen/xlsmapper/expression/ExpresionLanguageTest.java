package com.gh.mygreen.xlsmapper.expression;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExpresionLanguageTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testEL3_empty() {
        
        try {
            ExpressionLanguageELImpl el = new ExpressionLanguageELImpl();
            
            String expression = "empty label ? '空です' : label";
            
            Map<String, Object> vars = new HashMap<>();
            
            String eval = (String) el.evaluateWithEL3(expression, vars);
            assertEquals("空です", eval);
//            System.out.println(eval);
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testEL3_lambda() {
        
        try {
            ExpressionLanguageELImpl el = new ExpressionLanguageELImpl();
            
            String expression = "sum=0;list.stream().forEach(x->(sum=sum+x));sum";
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("list", Arrays.asList(1, 2, 3, 4, 5, 6));
            
            long val = (long) el.evaluateWithEL3(expression, vars);
            assertEquals(21L, val);
            System.out.println(val);
            
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }

}
