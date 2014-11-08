package org.mygreen.xlsmapper.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mygreen.xlsmapper.expression.ExpressionEvaluationException;
import org.mygreen.xlsmapper.validation.MessageInterpolator;
import org.mygreen.xlsmapper.validation.MessageParseException;


@SuppressWarnings("unused")
public class MessageInterporlatorTest {
    
    
    @Test
    public void testInterpolate_normal() {
        
        try {
            MessageInterpolator interpolator = new MessageInterpolator();
            
            String message = "{validatedValue} は、{min}～{max}の範囲で入力してください。";
            
            int validatedValue = 3;
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("validatedValue", validatedValue);
            vars.put("min", 1);
            vars.put("max", 10);
            
            String actual = interpolator.interpolate(message, vars);
            assertEquals("3 は、1～10の範囲で入力してください。", actual);
    //        System.out.println(actual);
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testInterpolate_el() {
        
        try {
            MessageInterpolator interpolator = new MessageInterpolator();
            
            String message = "${formatter.format('%1.1f', validatedValue)}は、${min}～${max}の範囲で入力してください。";
            
            double validatedValue = 3;
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("validatedValue", validatedValue);
            vars.put("min", 1);
            vars.put("max", 10);
            
            String actual = interpolator.interpolate(message, vars);
            assertEquals("3.0は、1～10の範囲で入力してください。", actual);
//            System.out.println(actual);
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testInterpolate_escape_01() {
        
        try {
            MessageInterpolator interpolator = new MessageInterpolator();
            
            String message = "\\${formatter.format('%1.1f',validatedValue)}は、\\{min}～${max}の範囲で入力してください。";
            
            double validatedValue = 3;
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("validatedValue", validatedValue);
            vars.put("min", 1);
            vars.put("max", 10);
            
            String actual = interpolator.interpolate(message, vars);
            assertEquals("${formatter.format('%1.1f',validatedValue)}は、{min}～10の範囲で入力してください。", actual);
//            System.out.println(actual);
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testInterpolate_escape_02() {
        
        try {
            MessageInterpolator interpolator = new MessageInterpolator();
            
            String message = "${'Helo Workd\\}' + formatter.format('%1.1f', validatedValue)}は、{min}～${max}の範囲で入力してください。";
            
            double validatedValue = 3;
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("validatedValue", validatedValue);
            vars.put("min", 1);
            vars.put("max", 10);
            
            String actual = interpolator.interpolate(message, vars);
            assertEquals("Helo Workd}3.0は、1～10の範囲で入力してください。", actual);
//            System.out.println(actual);
        } catch(Throwable e) {
            e.printStackTrace();
            fail();
        }
        
    }
}
