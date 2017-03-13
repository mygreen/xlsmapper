package com.gh.mygreen.xlsmapper.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.BeanFactory;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;

/**
 * {@link Utils}のテスタ
 * 
 * @version 1.1
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class UtilsTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
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
     * {@link Utils#matches(String, String, XlsMapperConfig)}
     * @since 1.1
     */
    @Test
    public void testMatches_normalize(){
        String rawText        = "a bc　\t  de\nfg   h  ";
        String normalizedText = "a bc defg h";
        
        XlsMapperConfig config = new XlsMapperConfig();
        config.setNormalizeLabelText(false);
        
        assertFalse(Utils.matches(rawText, normalizedText, config));
        
        config.setNormalizeLabelText(true);
        assertTrue(Utils.matches(rawText, normalizedText, config));
    }
    
    /**
     * {@link Utils#matches(String, String, XlsMapperConfig)}
     * @since 1.1
     */
    @Test
    public void testMatches_regex(){
        XlsMapperConfig config = new XlsMapperConfig();
        
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
    public void testParseCellAddress() {
        {
            CellAddress address = Utils.parseCellAddress("A1");
            assertEquals(0, address.getRow());
            assertEquals(0, address.getColumn());
        }
        
        {
            CellAddress address = Utils.parseCellAddress("AX232");
            assertEquals(231, address.getRow());
            assertEquals(49, address.getColumn());
        }
        
        {
            CellAddress address = Utils.parseCellAddress("a32A132");
            assertNull(address);
        }

    }
    
    @Test
    public void testConvertListToCollection() {
        
        final BeanFactory<Class<?>, Object> beanFactory = new XlsMapperConfig().getBeanFactory();
        
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
}
