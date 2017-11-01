package com.gh.mygreen.xlsmapper.util;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.util.IsEmptyBuilder;
import com.gh.mygreen.xlsmapper.util.IsEmptyComparator;
import com.gh.mygreen.xlsmapper.util.IsEmptyConfig;

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
        
        assertThat(new IsEmptyBuilder().isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().isNotEmpty(), is(false));
    }
    
    @Test
    public void test_String() {
        
        assertThat(new IsEmptyBuilder().append("").isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append("Hello").isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append(" ").isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(" ", true).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(" abc ", true).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)"Hello").isEmpty(), is(false));
    }
    
    @Test
    public void test_char() {
        
        assertThat(new IsEmptyBuilder().append("a".charAt(0)).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(" ".charAt(0)).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(" ".charAt(0), true).isEmpty(), is(true));
    }
    
    @Test
    public void test_boolean() {
        
        assertThat(new IsEmptyBuilder().append(true).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(false).isEmpty(), is(true));
        
    }
    
    @Test
    public void test_byte() {
        
        assertThat(new IsEmptyBuilder().append((byte)0).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append((byte)0).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append(Byte.parseByte("1")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_short() {
        
        assertThat(new IsEmptyBuilder().append((short)0).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append((short)0).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append((short)1).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_int() {
        
        assertThat(new IsEmptyBuilder().append((int)0).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append((int)0).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append((int)1).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_long() {
        
        assertThat(new IsEmptyBuilder().append((long)0).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append((long)0).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append((long)1).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_float() {
        
        assertThat(new IsEmptyBuilder().append((float)0.0).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append((float)0.0).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append((float)1.0).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_double() {
        
        assertThat(new IsEmptyBuilder().append((double)0.0).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append((double)0.0).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append((double)1.0).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Boolean() {
        
        assertThat(new IsEmptyBuilder().append((Boolean)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Boolean.valueOf("false")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Boolean.valueOf("true")).isEmpty(), is(false));
    }
    
    @Test
    public void test_Character() {
        
        assertThat(new IsEmptyBuilder().append((Character)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Character.valueOf("a".charAt(0))).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Character.valueOf(" ".charAt(0))).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Character.valueOf(" ".charAt(0)), true).isEmpty(), is(true));
        
    }
    
    @Test
    public void test_Byte() {
        
        assertThat(new IsEmptyBuilder().append((Byte)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Byte.valueOf("0")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append(Byte.valueOf("0")).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Byte.valueOf("1")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Short() {
        
        assertThat(new IsEmptyBuilder().append((Short)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Short.valueOf("0")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append(Short.valueOf("0")).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Short.valueOf("1")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Integer() {
        
        assertThat(new IsEmptyBuilder().append((Integer)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Integer.valueOf("0")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append(Integer.valueOf("0")).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Integer.valueOf("1")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Long() {
        
        assertThat(new IsEmptyBuilder().append((Long)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Long.valueOf("0")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append(Long.valueOf("0")).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Long.valueOf("1")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Float() {
        
        assertThat(new IsEmptyBuilder().append((Float)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Float.valueOf("0.0")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append(Float.valueOf("0.0")).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Float.valueOf("1.0")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Double() {
        
        assertThat(new IsEmptyBuilder().append((Double)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Double.valueOf("0.0")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withZeroAsEmpty(false)).append(Double.valueOf("0.0")).isEmpty(), is(false));
        assertThat(new IsEmptyBuilder().append(Double.valueOf("1.0")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_Object() {
        
        assertThat(new IsEmptyBuilder().append((Object)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new Sample()).isEmpty(), is(false));
    }
    
    @Test
    public void test_array() {
        
        assertThat(new IsEmptyBuilder().append((Object[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new Object[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new Object[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new Object[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((String[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new String[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new String[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new String[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new String[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new String[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new String[1]).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_array_boolean() {
        assertThat(new IsEmptyBuilder().append((boolean[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new boolean[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new boolean[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new boolean[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new boolean[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new boolean[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new boolean[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_byte() {
        assertThat(new IsEmptyBuilder().append((byte[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new byte[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new byte[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new byte[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new byte[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new byte[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new byte[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_char() {
        assertThat(new IsEmptyBuilder().append((char[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new char[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new char[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new char[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new char[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new char[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new char[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_short() {
        assertThat(new IsEmptyBuilder().append((short[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new short[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new short[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new short[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new short[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new short[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new short[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_int() {
        assertThat(new IsEmptyBuilder().append((int[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new int[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new int[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new int[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new int[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new int[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new Object[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_long() {
        assertThat(new IsEmptyBuilder().append((long[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new long[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new long[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new int[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new long[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new long[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new int[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_float() {
        assertThat(new IsEmptyBuilder().append((float[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new float[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new float[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new float[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new float[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new float[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new float[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_array_double() {
        assertThat(new IsEmptyBuilder().append((double[])null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new double[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new double[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append(new double[1]).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new double[0]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)new double[1]).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestArrayElement(false)).append((Object)new double[1]).isEmpty(), is(false));
    }
    
    @Test
    public void test_collection() {
        assertThat(new IsEmptyBuilder().append((ArrayList<String>)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new ArrayList<String>()).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(Arrays.asList("1")).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new ArrayList<String>()).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)Arrays.asList("1")).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)Arrays.asList("")).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestCollectionElement(false)).append((Object)Arrays.asList("")).isEmpty(), is(false));
        
    }
    
    @Test
    public void test_map() {
        assertThat(new IsEmptyBuilder().append((Map<String, Integer>)null).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append(new HashMap<String, Integer>()).isEmpty(), is(true));
        
        Map<String, Integer> map1 = new HashMap<String, Integer>();
        map1.put("key1", 1);
        assertThat(new IsEmptyBuilder().append(map1).isEmpty(), is(false));
        
        Map<String, Integer> map2 = new HashMap<String, Integer>();
        map2.put("key1", 0);
        assertThat(new IsEmptyBuilder().append(map2).isEmpty(), is(true));
        
        Map<String, Integer> map3 = new HashMap<String, Integer>();
        map3.put("key1", 0);
        assertThat(new IsEmptyBuilder(IsEmptyConfig.create().withTestMapValue(false)).append(map3).isEmpty(), is(false));
        
        assertThat(new IsEmptyBuilder().append((Object)new HashMap<String, Integer>()).isEmpty(), is(true));
        assertThat(new IsEmptyBuilder().append((Object)map1).isEmpty(), is(false));
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
        
        assertThat(IsEmptyBuilder.reflectionIsEmpty(null), is(true));
        
        Sample obj = new Sample();
        assertThat(IsEmptyBuilder.reflectionIsEmpty(obj), is(true));
        
        // transientも対象とする
        assertThat(IsEmptyBuilder.reflectionIsEmpty(obj, IsEmptyConfig.create().withTestTransient(true)), is(false));
        
        obj.age = 20;
        assertThat(IsEmptyBuilder.reflectionIsEmpty(obj, IsEmptyConfig.create().withTestTransient(false)), is(false));
        assertThat(IsEmptyBuilder.reflectionIsEmpty(obj, IsEmptyConfig.create().withTestTransient(false), "age"), is(true));
    }
    
    private static class Sample {
        
        private static int id = 1;
        
        private transient String name = "taro";
        
        private int no;
        
        protected String address;
        
        public Integer age;
        
    }
}
