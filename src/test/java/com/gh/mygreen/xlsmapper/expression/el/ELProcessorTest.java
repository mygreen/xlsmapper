package com.gh.mygreen.xlsmapper.expression.el;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.expression.el.ELProcessor;
import com.github.mygreen.expression.el.FormatterWrapper;


public class ELProcessorTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_normal() {
        
        Date date = Timestamp.valueOf("2015-04-15 10:20:30.000");
        
        ELProcessor elProc = new ELProcessor();
        
        elProc.setVariable("currentDate", date);
        elProc.setVariable("formatter", new FormatterWrapper(Locale.getDefault()));
        
        String result = elProc.eval("formatter.format('%1$tY/%1$tm/%1$td', currentDate)", String.class);
        assertThat(result, is("2015/04/15"));
//        System.out.println(result);
        
    }
    
    /**
     * EL関数の登録
     */
    @Test
    public void test_function() {
        
        ELProcessor elProc = new ELProcessor();
        
        try {
            Method method = MyFunction.class.getDeclaredMethod("strlen", String.class);
            
            // EL関数の登録 - メソッドオブジェクトで指定
            elProc.defineFunction("my", "strlength", method);
            
            // EL関数の指定 - 文字列で指定
            String className = ELProcessorTest.MyFunction.class.getName();
            elProc.defineFunction("my", "", className, "int sum(int,int)");
            
            elProc.setVariable("hello", "こんにちは。今日はいい天気ですね。");
            
            int result1 = elProc.eval("my:strlength(hello)", Integer.class);
            assertThat(result1, is(17));
//            System.out.println(result1);
            
            elProc.setVariable("num1", 1);
            int result2 = elProc.eval("my:sum(num1, 5)", Integer.class);
            assertThat(result2, is(6));
//            System.out.println(result2);
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    public static class MyFunction {
        
        public static int strlen(String str) {
            return str.length();
        }
        
        public static int sum(int a, int b) {
            return a + b;
        }
        
    }
}
