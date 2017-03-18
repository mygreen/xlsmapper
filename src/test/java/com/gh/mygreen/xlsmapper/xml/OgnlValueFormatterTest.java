package com.gh.mygreen.xlsmapper.xml;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import org.junit.Test;

import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * {@link OgnlValueFormatter}のテスタ
 *
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class OgnlValueFormatterTest {
    
    @Test
    public void test_null() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format((Object)null);
        assertThat(exp, is("null"));
        
        assertThat((Object)evalOgnl(exp), is(nullValue()));
    }
    
    @Test
    public void test_primitive_boolean() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(true);
        assertThat(exp, is("true"));
        
        assertThat((boolean)evalOgnl(exp), is(true));
    }
    
    @Test
    public void test_primitive_byte() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format((byte)-123);
        assertThat(exp, is("@Byte@valueOf('-123').byteValue()"));
        
        assertThat((byte)evalOgnl(exp), is((byte)-123));
    }
    
    @Test
    public void test_primitive_char() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format('a');
        assertThat(exp, is("'\\u0061'"));
        
        assertThat((char)evalOgnl(exp), is('a'));
    }
    
    @Test
    public void test_primitive_short() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format((short)-123);
        assertThat(exp, is("@Short@valueOf('-123').shortValue()"));
        
        assertThat((short)evalOgnl(exp), is((short)-123));
    }
    
    @Test
    public void test_primitive_int() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(-123);
        assertThat(exp, is("-123"));
        
        assertThat((int)evalOgnl(exp), is(-123));
    }
    
    @Test
    public void test_primitive_long() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(-123L);
        assertThat(exp, is("-123L"));
        
        assertThat((long)evalOgnl(exp), is(-123L));
    }
    
    @Test
    public void test_primitive_float() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(-123.456f);
        assertThat(exp, is("-123.456F"));
        
        assertThat((float)evalOgnl(exp), is(-123.456f));
    }
    
    @Test
    public void test_primitive_double() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(-123.456d);
        assertThat(exp, is("-123.456D"));
        
        assertThat((double)evalOgnl(exp), is(-123.456d));
    }
    
    @Test
    public void test_Boolean() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Boolean.TRUE);
        assertThat(exp, is("true"));
        
        assertThat((boolean)evalOgnl(exp), is(true));
    }
    
    @Test
    public void test_Byte() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format((Byte.valueOf((byte)-123)));
        assertThat(exp, is("@Byte@valueOf('-123').byteValue()"));
        
        assertThat((byte)evalOgnl(exp), is((byte)-123));
    }
    
    @Test
    public void test_Character() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Character.valueOf('a'));
        assertThat(exp, is("'\\u0061'"));
        
        assertThat((char)evalOgnl(exp), is('a'));
    }
    
    @Test
    public void test_Short() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Short.valueOf((short)-123));
        assertThat(exp, is("@Short@valueOf('-123').shortValue()"));
        
        assertThat((short)evalOgnl(exp), is((short)-123));
    }
    
    @Test
    public void test_Integer() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Integer.valueOf(-123));
        assertThat(exp, is("-123"));
        
        assertThat((int)evalOgnl(exp), is(-123));
    }
    
    @Test
    public void test_Long() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Long.valueOf(-123L));
        assertThat(exp, is("-123L"));
        
        assertThat((long)evalOgnl(exp), is(-123L));
    }
    
    @Test
    public void test_Float() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Float.valueOf(-123.456f));
        assertThat(exp, is("-123.456F"));
        
        assertThat((float)evalOgnl(exp), is(-123.456f));
    }
    
    @Test
    public void test_Double() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(Double.valueOf(-123.456d));
        assertThat(exp, is("-123.456D"));
        
        assertThat((double)evalOgnl(exp), is(-123.456d));
    }
    
    @Test
    public void test_String() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format("Hello\"こんにちは");
        assertThat(exp, is("\"Hello\\\"こんにちは\""));
        
        assertThat((String)evalOgnl(exp), is("Hello\"こんにちは"));
    }
    
    @Test
    public void test_Enum() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(RecordTerminal.Border);
        assertThat(exp, is("@com.gh.mygreen.xlsmapper.annotation.RecordTerminal@Border"));
        
        assertThat((RecordTerminal)evalOgnl(exp), is(RecordTerminal.Border));
    }
    
    @Test
    public void test_Class() throws Exception {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(String.class);
        assertThat(exp, is("@java.lang.String@class"));
        
        assertThat(evalOgnl(exp).equals(String.class), is(true));
        
    }
    
    @Test
    public void test_boolean_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new boolean[]{true, false, true});
        assertThat(exp, is("new boolean[] {true, false, true}"));
        
        assertThat((boolean[])evalOgnl(exp), is(instanceOf(boolean[].class)));
        
    }
    
    @Test
    public void test_char_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new char[]{'a', 'b', 'c'});
        assertThat(exp, is("new char[] {'\\u0061', '\\u0062', '\\u0063'}"));
        
        assertThat((char[])evalOgnl(exp), is(instanceOf(char[].class)));
        
    }
    
    @Test
    public void test_byte_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new byte[]{'a', 'b', 'c'});
        assertThat(exp, is("new byte[] {@Byte@valueOf('97').byteValue(), @Byte@valueOf('98').byteValue(), @Byte@valueOf('99').byteValue()}"));
        
        assertThat((byte[])evalOgnl(exp), is(instanceOf(byte[].class)));
        
    }
    
    @Test
    public void test_short_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new short[]{1, 2, 3});
        assertThat(exp, is("new short[] {@Short@valueOf('1').shortValue(), @Short@valueOf('2').shortValue(), @Short@valueOf('3').shortValue()}"));
        
        assertThat((short[])evalOgnl(exp), is(instanceOf(short[].class)));
        
    }
    
    @Test
    public void test_int_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new int[]{1, 2, 3});
        assertThat(exp, is("new int[] {1, 2, 3}"));
        
        assertThat((int[])evalOgnl(exp), is(instanceOf(int[].class)));
        
    }
    
    @Test
    public void test_long_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new long[]{1L, 2L, 3L});
        assertThat(exp, is("new long[] {1L, 2L, 3L}"));
        
        assertThat((long[])evalOgnl(exp), is(instanceOf(long[].class)));
        
    }
    
    @Test
    public void test_float_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new float[]{1.23f, -1.23f, 0.0f});
        assertThat(exp, is("new float[] {1.23F, -1.23F, 0.0F}"));
        
        assertThat((float[])evalOgnl(exp), is(instanceOf(float[].class)));
        
    }
    
    @Test
    public void test_double_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new double[]{1.23d, -1.23d, 0.0d});
        assertThat(exp, is("new double[] {1.23D, -1.23D, 0.0D}"));
        
        assertThat((double[])evalOgnl(exp), is(instanceOf(double[].class)));
        
    }
    
    @Test
    public void test_String_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new String[]{"Hello" ,"こ\"んにちは"});
        assertThat(exp, is("new String[] {\"Hello\", \"こ\\\"んにちは\"}"));
        
        assertArrayEquals((String[])evalOgnl(exp), new String[]{"Hello" ,"こ\"んにちは"});
        
    }
    
    @Test
    public void test_String_array_empty() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String exp = formatter.format(new String[]{});
        assertThat(exp, is("new String[] {}"));
        
        assertArrayEquals((String[])evalOgnl(exp), new String[]{});
        
    }
    
    
    @Test
    public void test_String_array_null() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        String[] obj = null;
        
        String exp = formatter.format(obj);
        assertThat(exp, is("null"));
        
        assertThat((Object)evalOgnl(exp), is(nullValue()));
        
    }
    
    @Test
    public void test_Enum_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        RecordTerminal[] obj = new RecordTerminal[]{RecordTerminal.Border, RecordTerminal.Empty};
        
        String exp = formatter.format(obj);
        assertThat(exp, is("new com.gh.mygreen.xlsmapper.annotation.RecordTerminal[] {@com.gh.mygreen.xlsmapper.annotation.RecordTerminal@Border, @com.gh.mygreen.xlsmapper.annotation.RecordTerminal@Empty}"));
        
        RecordTerminal[] result = (RecordTerminal[])evalOgnl(exp);
        assertThat(result, is(instanceOf(RecordTerminal[].class)));
    }
    
    @Test
    public void test_Class_array() {
        
        OgnlValueFormatter formatter = new OgnlValueFormatter();
        
        Class<?>[] obj = new Class[]{String.class, CellConverter.class};
        
        String exp = formatter.format(obj);
        assertThat(exp, is("new java.lang.Class[] {@java.lang.String@class, @com.gh.mygreen.xlsmapper.cellconverter.CellConverter@class}"));
        
        Class<?>[] result = (Class<?>[])evalOgnl(exp);
        assertThat(result, is(instanceOf(Class[].class)));
    }
    
    @SuppressWarnings("unchecked")
    private <A> A evalOgnl(final String expression) {
        
        try {
            OgnlContext context = new OgnlContext();
            Object obj = Ognl.getValue(expression, context, new Object());
            
            return (A) obj;
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }
}
