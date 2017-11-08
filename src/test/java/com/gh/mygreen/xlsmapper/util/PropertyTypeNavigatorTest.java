package com.gh.mygreen.xlsmapper.util;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link PropertyTypeNavigator}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PropertyTypeNavigatorTest {
    
    private PropertyTypeNavigator navigator;
    
    @Before
    public void setUp() throws Exception {
        this.navigator = new PropertyTypeNavigator();
    }
    
    @Test
    public void test_getPropertyType() {
        
        navigator.setAllowPrivate(true);
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "name");
            assertThat(type).isEqualTo(String.class);
        }
        
        {
            // getter
            Class<?> type = navigator.getPropertyType(Person.class, "age");
            assertThat(type).isEqualTo(int.class);
        }
        
        {
            // booleanのgetter
            Class<?> type = navigator.getPropertyType(Person.class, "checked");
            assertThat(type).isEqualTo(boolean.class);
        }
        
    }
    
    @Test
    public void test_getProperty_nest() {
        
        navigator.setAllowPrivate(true);
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "nest.price");
            assertThat(type).isEqualTo(long.class);
        }
    }
    
    @Test
    public void test_getProperty_list() {
        
        navigator.setAllowPrivate(true);
        
        {
            // リストそのまま
            Class<?> type = navigator.getPropertyType(Person.class, "books");
            assertThat(type).isEqualTo(List.class);
        }
        
        {
            // リストの要素指定
            Class<?> type = navigator.getPropertyType(Person.class, "books[0]");
            assertThat(type).isEqualTo(Book.class);
        }
        
        {
            // リストの要素の先の指定
            Class<?> type = navigator.getPropertyType(Person.class, "books[0].price");
            assertThat(type).isEqualTo(long.class);
        }
        
    }
    
    @Test
    public void test_getProperty_map() {
        
        navigator.setAllowPrivate(true);
        
        {
            // マップそのまま
            Class<?> type = navigator.getPropertyType(Person.class, "attend");
            assertThat(type).isEqualTo(Map.class);
        }
        
        {
            // マップの要素指定
            Class<?> type = navigator.getPropertyType(Person.class, "attend[2015-01-01]");
            assertThat(type).isEqualTo(Boolean.class);
        }
        
    }
    
    @Test
    public void test_getProperty_map_list() {
        
        navigator.setAllowPrivate(true);
        
        {
            // マップの要素指定
            Class<?> type = navigator.getPropertyType(Person.class, "mapList");
            assertThat(type).isEqualTo(Map.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "mapList[2015-01-01]");
            assertThat(type).isEqualTo(List.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "mapList[2015-01-01][0]");
            assertThat(type).isEqualTo(Book.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "mapList[2015-01-01][0].price");
            assertThat(type).isEqualTo(long.class);
        }
        
    }
    
    @Test
    public void test_getProperty_list_map() {
        
        navigator.setAllowPrivate(true);
        
        {
            // マップの要素指定
            Class<?> type = navigator.getPropertyType(Person.class, "listMap");
            assertThat(type).isEqualTo(List.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "listMap[0]");
            assertThat(type).isEqualTo(Map.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "listMap[0][2015-01-01]");
            assertThat(type).isEqualTo(Book.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "listMap[0][2015-01-01].price");
            assertThat(type).isEqualTo(long.class);
        }
        
    }
    
    @Test
    public void test_option_cacheWithPath() {
        
        navigator.setAllowPrivate(true);
        navigator.setCacheWithPath(true);
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "nest.price");
            assertThat(type).isEqualTo(long.class);
        }
        
        {
            Class<?> type = navigator.getPropertyType(Person.class, "nest.price");
            assertThat(type).isEqualTo(long.class);
        }
        
    }
    
    @Test
    public void test_option_allowPrivate_false() {
        navigator.setAllowPrivate(false);
        
        // publicフィールド
        {
            Class<?> type = navigator.getPropertyType(Person.class, "attend[0]");
            assertThat(type).isEqualTo(Boolean.class);
        }
        
        // privateフィールドアクセス
        assertThatThrownBy(() -> navigator.getPropertyType(Person.class, "nest.price"))
            .isInstanceOf(PropertyAccessException.class);
        
    }
    
    @Test
    public void test_option_ignoreNotResolveType_false() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotResolveType(false);
        
        {
            // Genericsのクラスタイプ定義がない
            assertThatThrownBy(() -> navigator.getPropertyType(Person.class, "noGenerics[0]"))
                .isInstanceOf(IllegalStateException.class);
        }
    }
    
    @Test
    public void test_option_ignoreNotResolveType_true() {
        
        navigator.setAllowPrivate(true);
        navigator.setIgnoreNotResolveType(true);
        
        {
            // Genericsのクラスタイプ定義がない
            Class<?> type = navigator.getPropertyType(Person.class, "noGenerics[0]");
            assertThat(type).isNull();
        }
        
    }
    
    private static class Person {
        
        private String name;
        
        private int age;
        
        private Book nest;
        
        private List<Book> books;
        
        private Set noGenerics;
        
        private Book[] arrayBooks;
        
        private boolean checked;
        
        public Map<String, Boolean> attend;
        
        private Map<String, List<Book>> mapList;
        
        private List<Map<String, Book>> listMap;
        
        public int getAge() {
            return age;
        }
        
        public boolean isChecked() {
            return true;
        }
        
        public List<Map<String, Book>> getListMap() {
            return listMap;
        }
        
    }
    
    private static class Book {
        
        private String title;
        
        private long price;
        
        private Book(String title, long price) {
            this.title = title;
            this.price = price;
        }
        
        public String getTitle() {
            return title;
        }
        
    }
}
