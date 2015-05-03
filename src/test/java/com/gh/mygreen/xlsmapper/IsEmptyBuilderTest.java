package com.gh.mygreen.xlsmapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link IsEmptyBuilder}のテスタ。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class IsEmptyBuilderTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test_default() {
        
        assertThat(true, is(new IsEmptyBuilder().isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().isNotEmpty()));
    }
    
    @Test
    public void test_String() {
        
        assertThat(true, is(new IsEmptyBuilder().append("").isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append("Hello").isEmpty()));
        
        assertThat(false, is(new IsEmptyBuilder().append(" ").isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(" ", true).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(" abc ", true).isEmpty()));
        
        assertThat(false, is(new IsEmptyBuilder().append((Object)"Hello").isEmpty()));
    }
    
    @Test
    public void test_char() {
        
        assertThat(false, is(new IsEmptyBuilder().append("a".charAt(0)).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(" ".charAt(0)).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(" ".charAt(0), true).isEmpty()));
    }
    
    @Test
    public void test_boolean() {
        
        assertThat(false, is(new IsEmptyBuilder().append(true).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(false).isEmpty()));
        
    }
    
    @Test
    public void test_byte() {
        
        assertThat(true, is(new IsEmptyBuilder().append((byte)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append((byte)0).isEmpty()));
        
        assertThat(false, is(new IsEmptyBuilder().append(Byte.parseByte("1")).isEmpty()));
        
    }
    
    @Test
    public void test_short() {
        
        assertThat(true, is(new IsEmptyBuilder().append((short)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append((short)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((short)1).isEmpty()));
        
    }
    
    @Test
    public void test_int() {
        
        assertThat(true, is(new IsEmptyBuilder().append((int)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append((int)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((int)1).isEmpty()));
        
    }
    
    @Test
    public void test_long() {
        
        assertThat(true, is(new IsEmptyBuilder().append((long)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append((long)0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((long)1).isEmpty()));
        
    }
    
    @Test
    public void test_float() {
        
        assertThat(true, is(new IsEmptyBuilder().append((float)0.0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append((float)0.0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((float)1.0).isEmpty()));
        
    }
    
    @Test
    public void test_double() {
        
        assertThat(true, is(new IsEmptyBuilder().append((double)0.0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append((double)0.0).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((double)1.0).isEmpty()));
        
    }
    
    @Test
    public void test_Boolean() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Boolean)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Boolean.valueOf("false")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Boolean.valueOf("true")).isEmpty()));
    }
    
    @Test
    public void test_Character() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Character)null).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Character.valueOf("a".charAt(0))).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Character.valueOf(" ".charAt(0))).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Character.valueOf(" ".charAt(0)), true).isEmpty()));
        
    }
    
    @Test
    public void test_Byte() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Byte)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Byte.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append(Byte.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Byte.valueOf("1")).isEmpty()));
        
    }
    
    @Test
    public void test_Short() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Short)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Short.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append(Short.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Short.valueOf("1")).isEmpty()));
        
    }
    
    @Test
    public void test_Integer() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Integer)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Integer.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append(Integer.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Integer.valueOf("1")).isEmpty()));
        
    }
    
    @Test
    public void test_Long() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Long)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Long.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append(Long.valueOf("0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Long.valueOf("1")).isEmpty()));
        
    }
    
    @Test
    public void test_Float() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Float)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Float.valueOf("0.0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append(Float.valueOf("0.0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Float.valueOf("1.0")).isEmpty()));
        
    }
    
    @Test
    public void test_Double() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Double)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(Double.valueOf("0.0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder(false).append(Double.valueOf("0.0")).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Double.valueOf("1.0")).isEmpty()));
        
    }
    
    @Test
    public void test_Object() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)null).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new Sample()).isEmpty()));
    }
    
    @Test
    public void test_array() {
        
        assertThat(true, is(new IsEmptyBuilder().append((Object[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new Object[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new Object[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((String[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new String[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new String[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new String[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new String[1]).isEmpty()));
        
    }
    
    @Test
    public void test_array_boolean() {
        assertThat(true, is(new IsEmptyBuilder().append((boolean[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new boolean[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new boolean[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new boolean[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new boolean[1]).isEmpty()));
    }
    
    @Test
    public void test_array_byte() {
        assertThat(true, is(new IsEmptyBuilder().append((byte[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new byte[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new byte[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new byte[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new byte[1]).isEmpty()));
    }
    
    @Test
    public void test_array_char() {
        assertThat(true, is(new IsEmptyBuilder().append((char[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new char[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new char[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new char[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new char[1]).isEmpty()));
    }
    
    @Test
    public void test_array_short() {
        assertThat(true, is(new IsEmptyBuilder().append((short[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new short[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new short[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new short[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new short[1]).isEmpty()));
    }
    
    @Test
    public void test_array_int() {
        assertThat(true, is(new IsEmptyBuilder().append((int[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new int[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new int[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new int[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new int[1]).isEmpty()));
    }
    
    @Test
    public void test_array_long() {
        assertThat(true, is(new IsEmptyBuilder().append((long[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new long[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new long[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new long[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new long[1]).isEmpty()));
    }
    
    @Test
    public void test_array_float() {
        assertThat(true, is(new IsEmptyBuilder().append((float[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new float[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new float[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new float[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new float[1]).isEmpty()));
    }
    
    @Test
    public void test_array_double() {
        assertThat(true, is(new IsEmptyBuilder().append((double[])null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new double[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(new double[1]).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new double[0]).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)new double[1]).isEmpty()));
    }
    
    @Test
    public void test_collection() {
        assertThat(true, is(new IsEmptyBuilder().append((ArrayList<String>)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new ArrayList<String>()).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append(Arrays.asList("1")).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new ArrayList<String>()).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)Arrays.asList("1")).isEmpty()));
    }
    
    @Test
    public void test_map() {
        assertThat(true, is(new IsEmptyBuilder().append((Map<String, Integer>)null).isEmpty()));
        assertThat(true, is(new IsEmptyBuilder().append(new HashMap<String, Integer>()).isEmpty()));
        
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("key1", 1);
        assertThat(false, is(new IsEmptyBuilder().append(map).isEmpty()));
        
        assertThat(true, is(new IsEmptyBuilder().append((Object)new HashMap<String, Integer>()).isEmpty()));
        assertThat(false, is(new IsEmptyBuilder().append((Object)map).isEmpty()));
    }
    
    @Test
    public void test_compare() {
        
        final String value1 = "@";
        assertThat(true, is(new IsEmptyBuilder().compare(new IsEmptyComparator() {
            
            @Override
            public boolean isEmpty() {
                return value1.equals("@");
            }
        }).isEmpty()));
        
        final String value2 = "a";
        assertThat(false, is(new IsEmptyBuilder().compare(new IsEmptyComparator() {
            
            @Override
            public boolean isEmpty() {
                return value2.equals("@");
            }
        }).isEmpty()));
        
    }
    
    @Test
    public void test_relection() {
        
        assertThat(true, is(IsEmptyBuilder.reflectionIsEmpty(null)));
        
        Sample obj = new Sample();
        assertThat(true, is(IsEmptyBuilder.reflectionIsEmpty(obj)));
        
        // transientも対象とする
        assertThat(false, is(IsEmptyBuilder.reflectionIsEmpty(obj, true)));
        
        obj.age = 20;
        assertThat(false, is(IsEmptyBuilder.reflectionIsEmpty(obj, false)));
        assertThat(true, is(IsEmptyBuilder.reflectionIsEmpty(obj, false, "age")));
    }
    
    private static class Sample {
        
        private static int id = 1;
        
        private transient String name = "taro";
        
        private int no;
        
        protected String address;
        
        public Integer age;
        
    }
}
