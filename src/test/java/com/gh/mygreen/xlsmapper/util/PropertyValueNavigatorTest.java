package com.gh.mygreen.xlsmapper.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.util.PropertyAccessException;
import com.gh.mygreen.xlsmapper.util.PropertyValueNavigator;

/**
 * {@link PropertyValueNavigator}のテスタ
 * 
 * @version 2.0
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class PropertyValueNavigatorTest {
    
    private PropertyValueNavigator navigator;
    
    @Before
    public void setUp() throws Exception {
        this.navigator = new PropertyValueNavigator();
    }
    
    @Test
    public void test_getProperty() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        obj.name = "山田太郎";
        obj.age = 20;
        
        {
            String value1 = (String) navigator.getProperty(obj, "name");
            assertThat(value1, is("山田太郎"));
        }
        
        {
            int value2 = (int) navigator.getProperty(obj, "age");
            assertThat(value2, is(20));
        }
        
    }
    
    @Test
    public void test_getProperty_nest() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        obj.nest = new Book("本", 100);
        
        {
            long price = (long)navigator.getProperty(obj, "nest.price");
            assertThat(price, is(100l));
        }
        
    }
    
    @Test
    public void test_getProperty_list() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        {
            Book value1 = (Book) navigator.getProperty(obj, "books[1]");
            assertThat(value1, is(nullValue()));
        }
        
        {
            obj.books = new ArrayList<>();
            Book value2 = (Book) navigator.getProperty(obj, "books[1]");
            assertThat(value2, is(nullValue()));
        }
        
        obj.books.add(new Book("本0", 500));
        obj.books.add(new Book("本1", 600));
        obj.books.add(new Book("本2", 700));
        
        {
            Book value3 = (Book) navigator.getProperty(obj, "books[1]");
            assertThat(value3, is(not(nullValue())));
            assertThat(value3.title, is("本1"));
        }
        
        {
            String value4 = (String) navigator.getProperty(obj, "books[1].title");
            assertThat(value4, is("本1"));
        }
        
    }
    
    @Test
    public void test_getProperty_map() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        {
            Boolean value1 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            assertThat(value1, is(nullValue()));
        }
        
        obj.attend = new LinkedHashMap<String, Boolean>();
        
        {
            Boolean value2 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            assertThat(value2, is(nullValue()));
        }
        
        obj.attend.put("2015-01-01", true);
        obj.attend.put("2015-01-02", false);
        obj.attend.put("2015-01-03", true);
        
        {
            Boolean value3 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            assertThat(value3, is(true));
        }
        
    }
    
    @Test
    public void test_getProperty_map_list() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        {
            Book value1 = (Book) navigator.getProperty(obj, "bookData[history][1]");
            assertThat(value1, is(nullValue()));
        }
        
        obj.bookData = new LinkedHashMap<>();
        
        {
            Book value2 = (Book) navigator.getProperty(obj, "bookData[history][1]");
            assertThat(value2, is(nullValue()));
        }
        
        obj.bookData.put("special", Arrays.asList(new Book("book0", 100), new Book("book1", 200)));
        obj.bookData.put("history", Arrays.asList(new Book("本0", 300), new Book("本1", 400)));
        
        {
            Book value3 = (Book) navigator.getProperty(obj, "bookData[history][1]");
            assertThat(value3, is(not(nullValue())));
            assertThat(value3.title, is("本1"));
        }
        
        {
            String value4 = (String) navigator.getProperty(obj, "bookData[history][1].title");
            assertThat(value4, is("本1"));
        }
    }
    
    @Test
    public void test_option_cacheWithPath() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        navigator.setIgnoreNotFoundKey(true);
        navigator.setCacheWithPath(true);
        
        Person obj = new Person();
        obj.name = "山田太郎";
        obj.age = 20;
        
        {
            String value1 = (String) navigator.getProperty(obj, "name");
            assertThat(value1, is("山田太郎"));
        }
        
        {
            String value2 = (String) navigator.getProperty(obj, "name");
            assertThat(value2, is("山田太郎"));
        }
        
        navigator.clearCache();
        
        {
            String value3 = (String) navigator.getProperty(obj, "name");
            assertThat(value3, is("山田太郎"));
        }
        
    }
    
    @Test
    public void test_option_ignoreNull_true() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(true);
        
        // ルートがnull
        Person obj = null;
        {
            String value1 = (String) navigator.getProperty(obj, "name");
            assertThat(value1, is(nullValue()));
        }
        
        // 末端がnull
        obj = new Person();
        {
            String value2 = (String) navigator.getProperty(obj, "name");
            assertThat(value2, is(nullValue()));
        }
        
        // 途中がnull
        {
            String value3 = (String) navigator.getProperty(obj, "books[0].title");
            assertThat(value3, is(nullValue()));
        }
        
    }
    
    @Test
    public void test_option_ignoreNull_false() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNull(false);
        
        Person obj = null;
        try {
            // ルートがnull
            String value1 = (String) navigator.getProperty(obj, "name");
            fail();
            
        } catch(Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
        
        // 末端がnull
        obj = new Person();
        String value2 = (String) navigator.getProperty(obj, "name");
        assertThat(value2, is(nullValue()));
        
        // 途中がnull
        try {
            String value3 = (String) navigator.getProperty(obj, "books[0].title");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
        
    }
    
    @Test
    public void test_option_ignoreNotFoundKey_collection_true() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        obj.books = new ArrayList<>();
        
        // 空のとき
        {
            Book value1 = (Book) navigator.getProperty(obj, "books[1]");
            assertThat(value1, is(nullValue()));
        }
        
        //  空でない場合
        obj.books.add(new Book("本0", 500));
        obj.books.add(new Book("本1", 600));
        obj.books.add(new Book("本2", 700));
        
        {
            Book value2 = (Book) navigator.getProperty(obj, "books[-1]");
            assertThat(value2, is(nullValue()));
        }
        
        {
            Book value3 = (Book) navigator.getProperty(obj, "books[3]");
            assertThat(value3, is(nullValue()));
        }
        
        {
            Book value4 = (Book) navigator.getProperty(obj, "books[1]");
            assertThat(value4.title, is(not(nullValue())));
        }
        
    }
    
    @Test
    public void test_option_ignoreNotFoundKey_collection_false() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(false);
        
        Person obj = new Person();
        
        // リストの場合
        obj.books = new ArrayList<>();
        
        // リスト - 空のとき
        try {
            Book value1 = (Book) navigator.getProperty(obj, "books[1]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
        
        // リスト - 空でない場合
        obj.books.add(new Book("本0", 500));
        obj.books.add(new Book("本1", 600));
        obj.books.add(new Book("本2", 700));
        
        try {
            Book value2 = (Book) navigator.getProperty(obj, "books[-1]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
        
        try {
            Book value3 = (Book) navigator.getProperty(obj, "books[3]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
        
        {
            Book value4 = (Book) navigator.getProperty(obj, "books[1]");
            assertThat(value4.title, is(not(nullValue())));
        }
        
    }
    
    @Test
    public void test_option_ignoreNotFoundKey_array_true() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        obj.arrayBooks = new Book[0];
        
        // 空のとき
        {
            Book value1 = (Book) navigator.getProperty(obj, "arrayBooks[1]");
            assertThat(value1, is(nullValue()));
        }
        
        // 空でない場合
        obj.arrayBooks = new Book[3];
        obj.arrayBooks[0] = new Book("本0", 500);
        obj.arrayBooks[1] = new Book("本1", 600);
        obj.arrayBooks[2] = new Book("本2", 700);
        
        {
            Book value2 = (Book) navigator.getProperty(obj, "arrayBooks[-1]");
            assertThat(value2, is(nullValue()));
        }
        
        {
            Book value3 = (Book) navigator.getProperty(obj, "arrayBooks[3]");
            assertThat(value3, is(nullValue()));
        }
        
        {
            Book value4 = (Book) navigator.getProperty(obj, "arrayBooks[1]");
            assertThat(value4.title, is(not(nullValue())));
        }
    }
    
    @Test
    public void test_option_ignoreNotFoundKey_array_false() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(false);
        
        Person obj = new Person();
        obj.arrayBooks = new Book[0];
        
        // 空のとき
        try {
            Book value1 = (Book) navigator.getProperty(obj, "arrayBooks[1]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
        
        // 空出ない場合
        obj.arrayBooks = new Book[3];
        obj.arrayBooks[0] = new Book("本0", 500);
        obj.arrayBooks[1] = new Book("本1", 600);
        obj.arrayBooks[2] = new Book("本2", 700);
        
        
        try {
            Book value2 = (Book) navigator.getProperty(obj, "arrayBooks[-1]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
        
        try {
            Book value3 = (Book) navigator.getProperty(obj, "arrayBooks[3]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IndexOutOfBoundsException.class));
        }
        
        {
            Book value4 = (Book) navigator.getProperty(obj, "arrayBooks[1]");
            assertThat(value4.title, is(not(nullValue())));
        }
        
    }
    
    @Test
    public void test_option_ignoreNotFoundKey_map_true() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        obj.attend = new LinkedHashMap<String, Boolean>();
        
        //  空のとき
        {
            Boolean value1 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            assertThat(value1, is(nullValue()));
        }
        
        // 空でない場合
        obj.attend.put("2015-01-01", true);
        obj.attend.put("2015-01-02", false);
        obj.attend.put("2015-01-03", true);
        
        {
            Boolean value2 = (Boolean) navigator.getProperty(obj, "attend[2015-01-04]");
            assertThat(value2, is(nullValue()));
        }
        
        {
            Boolean value3 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            assertThat(value3, is(true));
        }
        
    }
    
    @Test
    public void test_option_ignoreNotFoundKey_map_false() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(false);
        
        Person obj = new Person();
        
        obj.attend = new LinkedHashMap<String, Boolean>();
        
        // 空のとき
        try {
            Boolean value1 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IllegalStateException.class));
        }
        
        // 空でない場合
        obj.attend.put("2015-01-01", true);
        obj.attend.put("2015-01-02", false);
        obj.attend.put("2015-01-03", true);
        
        try {
            Boolean value2 = (Boolean) navigator.getProperty(obj, "attend[2015-01-04]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IllegalStateException.class));
        }
        
        {
            Boolean value3 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
            assertThat(value3, is(true));
        }
        
    }
    
    /**
     * コレクションのキーのトリム、パース処理
     */
    @Test
    public void test_collection_key() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(false);
        
        Person obj = new Person();
        obj.books = new ArrayList<>();
        obj.books.add(new Book("本0", 500));
        obj.books.add(new Book("本1", 600));
        obj.books.add(new Book("本2", 700));
        
        // トリム処理
        Book value1 = (Book) navigator.getProperty(obj, "books[ 1 ]");
        assertThat(value1, is(not(nullValue())));
        
        // 文字列の場合
        try {
            Book value2 = (Book) navigator.getProperty(obj, "books[abc]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(PropertyAccessException.class));
        }
        
    }
    
    /**
     * 配列のキーのトリム、パース処理
     */
    @Test
    public void test_array_key() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(false);
        
        Person obj = new Person();
        obj.arrayBooks = new Book[3];
        obj.arrayBooks[0] = new Book("本0", 500);
        obj.arrayBooks[1] = new Book("本1", 600);
        obj.arrayBooks[2] = new Book("本2", 700);
        
        // トリム処理
        Book value1 = (Book) navigator.getProperty(obj, "arrayBooks[ 1 ]");
        assertThat(value1, is(not(nullValue())));
        
        // 文字列の場合
        try {
            Book value2 = (Book) navigator.getProperty(obj, "arrayBooks[abc]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(PropertyAccessException.class));
        }
        
    }
    
    /**
     * マップのキーのトリム、パース処理
     */
    @Test
    public void test_map_key() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(false);
        
        Person obj = new Person();
        obj.attend = new LinkedHashMap<String, Boolean>();
        obj.attend.put("2015-01-01", true);
        obj.attend.put("2015-01-02", false);
        obj.attend.put("2015-01-03", true);
        
        // トリム処理なし
        Boolean value1 = (Boolean) navigator.getProperty(obj, "attend[2015-01-01]");
        assertThat(value1, is(true));
        
        // 空白を含む場合
        try {
            Boolean value2 = (Boolean) navigator.getProperty(obj, "attend[ 2015-01-01 ]");
            fail();
        } catch(Exception e) {
            assertThat(e, instanceOf(IllegalStateException.class));
        }
        
    }
    
    /**
     * プロパティがメソッドの場合
     */
    @Test
    public void test_property_method() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        obj.checked = true;
        obj.age = 20;
        
        {
            Boolean value1 = (Boolean) navigator.getProperty(obj, "checked");
            assertThat(value1, is(true));
        }
        
        {
            int value2 = (int) navigator.getProperty(obj, "age");
            assertThat(value2, is(20));
        }
        
    }
    
    
    /**
     * プロパティがメソッドの場合
     */
    @Test(expected=PropertyAccessException.class)
    public void test_notFound_property() {
        
        PropertyValueNavigator navigator = new PropertyValueNavigator();
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotFoundKey(true);
        
        Person obj = new Person();
        
        Object value1 = navigator.getProperty(obj, "abc");
        fail();
    }
    
    private static class Person {
        
        private String name;
        
        private int age;
        
        private Book nest;
        
        private List<Book> books;
        
        private Book[] arrayBooks;
        
        private boolean checked;
        
        private Map<String, Boolean> attend;
        
        private Map<String, List<Book>> bookData;
        
        public int getAge() {
            return age;
        }
        
        public boolean isChecked() {
            return true;
        }
        
    }
    
    private static class Book {
        
        private String title;
        
        private long price;
        
        private Book(String title, long price) {
            this.title = title;
            this.price = price;
        }
        
    }
}
