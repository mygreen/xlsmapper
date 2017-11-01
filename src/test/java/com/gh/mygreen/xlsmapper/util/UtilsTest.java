package com.gh.mygreen.xlsmapper.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.junit.Test;

import com.gh.mygreen.xlsmapper.BeanFactory;
import com.gh.mygreen.xlsmapper.Configuration;

/**
 * {@link Utils}のテスタ
 * 
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class UtilsTest {
    
    /**
     * {@link Utils#capitalize(String)}
     */
    @Test
    public void testCapitalize() {
        
        assertThat(Utils.capitalize(null), is(nullValue()));
        assertThat(Utils.capitalize(""), is(""));
        assertThat(Utils.capitalize("cat"), is("Cat"));
        assertThat(Utils.capitalize("cAt"), is("CAt"));
        
    }
    
    /**
     * {@link Utils#uncapitalize(String)}
     */
    @Test
    public void testUncapitalize() {
        assertThat(Utils.uncapitalize(null), is(nullValue()));
        assertThat(Utils.uncapitalize(""), is(""));
        assertThat(Utils.uncapitalize("CAT"), is("cAT"));
        assertThat(Utils.uncapitalize("Cat"), is("cat"));
    }
    
    /**
     * {@link Utils#matches(String, String, Configuration)}
     * @since 1.1
     */
    @Test
    public void testMatches_normalize(){
        String rawText        = "a bc　\t  de\nfg   h  ";
        String normalizedText = "a bc defg h";
        
        Configuration config = new Configuration();
        config.setNormalizeLabelText(false);
        
        assertFalse(Utils.matches(rawText, normalizedText, config));
        
        config.setNormalizeLabelText(true);
        assertTrue(Utils.matches(rawText, normalizedText, config));
    }
    
    /**
     * {@link Utils#matches(String, String, Configuration)}
     * @since 1.1
     */
    @Test
    public void testMatches_regex(){
        Configuration config = new Configuration();
        
        config.setNormalizeLabelText(false);
        config.setRegexLabelText(true);
        assertTrue(Utils.matches("test_data", "/test.*/", config));
        assertFalse(Utils.matches("test_data", "/nottest.*/", config));
    }
    
    @Test
    public void testIsEmpty() {
        
        // 文字列の場合
        assertThat(Utils.isEmpty(""), is(true));
        assertThat(Utils.isEmpty((String)null), is(true));
        assertThat(Utils.isEmpty(" "), is(false));
        assertThat(Utils.isEmpty("ab"), is(false));
        assertThat(Utils.isEmpty(String.valueOf('\u0000')), is(true));
        
    }
    
    @Test
    public void testIsNotEmpty() {
        
        // 文字列の場合
        assertThat(Utils.isNotEmpty(""), is(false));
        assertThat(Utils.isNotEmpty((String)null), is(false));
        assertThat(Utils.isNotEmpty(" "), is(true));
        assertThat(Utils.isNotEmpty("ab"), is(true));
        assertThat(Utils.isNotEmpty(String.valueOf('\u0000')), is(false));
    }
    
    @Test
    public void testConvertListToCollection() {
        
        final BeanFactory<Class<?>, Object> beanFactory = new Configuration().getBeanFactory();
        
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("efg");
        
        Class toClass;
        Collection<String> value;
        
        // 変換先がCollectionインタフェースの場合
        toClass = Collection.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(ArrayList.class)));
        
        // 変換先がListインタフェースの場合
        toClass = List.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(ArrayList.class)));
        
        // 変換先がArrayListで元のインスタンスと同じ場合。
        toClass = ArrayList.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(ArrayList.class)));
        
        // 変換先がVectorの場合
        toClass = Vector.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(Vector.class)));
        
        // 変換先がSetインタフェースの場合
        toClass = Set.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(LinkedHashSet.class)));
        
        // 変換先がQueueインタフェースの場合
        toClass = Queue.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(LinkedList.class)));
        
        // 変換先がDequeインタフェースの場合
        toClass = Deque.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(LinkedList.class)));
        
        // 変換先がLinkedListの場合
        toClass = LinkedList.class;
        value = Utils.convertListToCollection(list, toClass, beanFactory);
        assertThat(value, is(instanceOf(LinkedList.class)));
        
        
    }
    
    @Test
    public void testConvertCollectionToList() {
        
        List<String> data = Arrays.asList("abc", "efg");
        
        Collection<String> collection;
        List<String> value;
        
        // 変換元がリストの場合
        collection = new ArrayList<>(data);
        value = Utils.convertCollectionToList(collection);
        assertThat(value, is(instanceOf(ArrayList.class)));
        
        // 変換元がSetの場合
        collection = new LinkedHashSet<>(data);
        value = Utils.convertCollectionToList(collection);
        assertThat(value, is(instanceOf(ArrayList.class)));
        
        // 変換元がLinkedListの場合
        collection = new LinkedList<>(data);
        value = Utils.convertCollectionToList(collection);
        assertThat(value, is(instanceOf(LinkedList.class)));
        
        // 変換元がArrayDequeの場合
        collection = new ArrayDeque<>(data);
        value = Utils.convertCollectionToList(collection);
        assertThat(value, is(instanceOf(ArrayList.class)));
        
    }
    
    @Test
    public void testAddListWithIndex() {
        
        {
            // 引数の確認
            List<String> list = null;
            assertThatThrownBy(() -> Utils.addListWithIndex(list, "abc", 0)).isInstanceOf(IllegalArgumentException.class);
        
        }
        
        {
            // 引数の確認 - インデックスが-1
            List<String> list = new ArrayList<>();
            assertThatThrownBy(() -> Utils.addListWithIndex(list, "abc", -1)).isInstanceOf(IllegalArgumentException.class);
        
        }
        
        {
            List<String> list = new ArrayList<>();
            
            // サイズが1つ足りない - サイズとインデックス番号が同じ
            Utils.addListWithIndex(list, "abc", 0);
            assertThat(list).hasSize(1).containsExactly("abc");
            
            // サイズが足りない
            Utils.addListWithIndex(list, "efg", 2);
            assertThat(list).hasSize(3).containsExactly("abc", null, "efg");
            
            // サイズが足りている
            Utils.addListWithIndex(list, "hij", 1);
            assertThat(list).hasSize(3).containsExactly("abc", "hij", "efg");
            
            
        }

    }
    
    @Test
    public void testGetPrimitiveDefaultValue() {
        
        {
            // null
            assertThatThrownBy(() -> Utils.getPrimitiveDefaultValue(null)).isInstanceOf(IllegalArgumentException.class);
        }
        
        {
            // non-primitive
            assertThat(Utils.getPrimitiveDefaultValue(Integer.class)).isNull();
        }
        
        {
            // primitive
            assertThat(Utils.getPrimitiveDefaultValue(boolean.class)).isEqualTo(false);
            assertThat(Utils.getPrimitiveDefaultValue(char.class)).isEqualTo('\u0000');
            assertThat(Utils.getPrimitiveDefaultValue(byte.class)).isEqualTo((byte)0);
            assertThat(Utils.getPrimitiveDefaultValue(short.class)).isEqualTo((short)0);
            assertThat(Utils.getPrimitiveDefaultValue(int.class)).isEqualTo(0);
            assertThat(Utils.getPrimitiveDefaultValue(long.class)).isEqualTo(0L);
            assertThat(Utils.getPrimitiveDefaultValue(float.class)).isEqualTo(0.0f);
            assertThat(Utils.getPrimitiveDefaultValue(double.class)).isEqualTo(0.0d);
            
            
        }
        
    
    }
    
    @Test
    public void testAsList() {
        
        {
            // 引数 - データがnullの場合
            assertThat(Utils.asList(null, Integer.class))
                .isNotNull()
                .hasSize(0);
        }
        
        {
            // 引数 - 型がnullの場合
            assertThatThrownBy(() -> Utils.asList(new int[]{123, 456}, null))
                .isInstanceOf(IllegalArgumentException.class);
        }
        
        {
            // 引数が配列出ない場合
            assertThatThrownBy(() -> Utils.asList("text", String.class))
                .isInstanceOf(IllegalArgumentException.class);
        }
        
        {
            // 非プリミティブ型の配列の場合
            assertThat(Utils.asList(new String[]{"a", "b"}, String.class))
                .hasSize(2)
                .containsExactly("a", "b");
        }
        
        {
            // プリミティブ型(boolean)の配列の場合
            assertThat(Utils.asList(new boolean[]{true, false}, boolean.class))
                .hasSize(2)
                .containsExactly(true, false);
        }
        
        {
            // プリミティブ型(char)の配列の場合
            assertThat(Utils.asList(new char[]{'a', 'b'}, char.class))
                .hasSize(2)
                .containsExactly('a', 'b');
        }
        
        {
            // プリミティブ型(byte)の配列の場合
            assertThat(Utils.asList(new byte[]{1, 2}, byte.class))
                .hasSize(2)
                .containsExactly((byte)1, (byte)2);
        }
        
        {
            // プリミティブ型(short)の配列の場合
            assertThat(Utils.asList(new short[]{1, 2}, short.class))
                .hasSize(2)
                .containsExactly((short)1, (short)2);
        }
        
        {
            // プリミティブ型(int)の配列の場合
            assertThat(Utils.asList(new int[]{1, 2}, int.class))
                .hasSize(2)
                .containsExactly(1, 2);
        }
        
        {
            // プリミティブ型(long)の配列の場合
            assertThat(Utils.asList(new long[]{1l, 2l}, long.class))
                .hasSize(2)
                .containsExactly(1l, 2l);
        }
        
        {
            // プリミティブ型(float)の配列の場合
            assertThat(Utils.asList(new float[]{1.2f, 2.3f}, float.class))
                .hasSize(2)
                .containsExactly(1.2f, 2.3f);
        }
        
        {
            // プリミティブ型(double)の配列の場合
            assertThat(Utils.asList(new double[]{1.2d, 2.3d}, double.class))
                .hasSize(2)
                .containsExactly(1.2d, 2.3d);
        }
        
        
    }
}
