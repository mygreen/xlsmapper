package com.gh.mygreen.xlsmapper.processor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsOrder;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link FieldAdapterBuilder}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class FieldAdapterBuilderTest {
    
    /**
     * 基本構成のテスト
     */
    public static class Basic {
        
        private FieldAdapterBuilder builder;
        
        @Before
        public void setUp() throws Exception {
            this.builder = new FieldAdapterBuilder(new AnnotationReader(null));
        }
        
        /**
         * フィールドしか存在しない場合
         */
        @Test
        public void testOfField_onlyField() throws Exception {
            
            Field field = BasicClass.class.getDeclaredField("onlyField");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("onlyField");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(String.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isEmpty();
            assertThat(adapter.getSetter()).isEmpty();
            
            assertThat(adapter.hasAnnotation(XlsCell.class)).isTrue();
        }
        
        /**
         * アクセッサが存在する場合
         */
        @Test
        public void testOfField_existAccessor() throws Exception {
            
            Field field = BasicClass.class.getDeclaredField("existAccessor");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("existAccessor");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(LocalDate.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsCellOption.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsOrder.class)).isTrue();
            
        }
        
        /**
         * プリミティブ型のbooleanが存在する場合
         */
        @Test
        public void testOfField_primitiveBoolean() throws Exception {
            
            Field field = BasicClass.class.getDeclaredField("primitiveBool");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("primitiveBool");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(boolean.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsDefaultValue.class)).isTrue();
            
            
        }
        
        /**
         * getterメソッド
         */
        @Test
        public void testOfGetter_existAccessor() throws Exception {
            
            Method method = BasicClass.class.getMethod("getExistAccessor");
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("existAccessor");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(LocalDate.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsCellOption.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsOrder.class)).isTrue();
            
        }
        
        /**
         * setterメソッド
         */
        @Test
        public void testOfSetter_existAccessor() throws Exception {
            
            Method method = BasicClass.class.getMethod("setExistAccessor", LocalDate.class);
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("existAccessor");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(LocalDate.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsCellOption.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsOrder.class)).isTrue();
            
        }
        
        /**
         * プリミティブ型のbooleanが存在する場合
         */
        @Test
        public void testOfGetter_primitiveBoolean() throws Exception {
            
            Method method = BasicClass.class.getDeclaredMethod("isPrimitiveBool");
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("primitiveBool");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(boolean.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsDefaultValue.class)).isTrue();
            
            
        }
        
        /**
         * プリミティブ型のbooleanが存在する場合
         */
        @Test
        public void testOfSetter_primitiveBoolean() throws Exception {
            
            Method method = BasicClass.class.getDeclaredMethod("setPrimitiveBool", boolean.class);
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("primitiveBool");
            assertThat(adapter.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(adapter.getType()).isEqualTo(boolean.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(adapter.hasAnnotation(XlsDefaultValue.class)).isTrue();
            
            
        }
        
        /**
         * 基本情報をテストするためのクラス
         *
         */
        public static class BasicClass {
            
            /**
             * フィールドしかない場合
             */
            @XlsCell(address="A1")
            private String onlyField;
            
            /**
             * アクセッサメソッドが存在する場合
             */
            @XlsLabelledCell(label="テスト", type=LabelledCellType.Right)
            private LocalDate existAccessor;
            
            /**
             * プリミティブ型のboolean
             */
            @XlsLabelledCell(label="boolean", type=LabelledCellType.Left)
            private boolean primitiveBool;
            
            @XlsCellOption(shrinkToFit=true)
            public LocalDate getExistAccessor() {
                return existAccessor;
            }
            
            @XlsOrder(1)
            public void setExistAccessor(LocalDate existAccessor) {
                this.existAccessor = existAccessor;
            }
            
            @XlsDefaultValue("true")
            public boolean isPrimitiveBool() {
                return primitiveBool;
            }
            
            public void setPrimitiveBool(boolean primitiveBool) {
                this.primitiveBool = primitiveBool;
            }
            
        }
    
    }
    
    public static class Generics {
        
        private FieldAdapterBuilder builder;
        
        @Before
        public void setUp() throws Exception {
            this.builder = new FieldAdapterBuilder(new AnnotationReader(null));
        }
        
        @Test
        public void testOfField_collection() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("collection");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("collection");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Collection.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfField_list() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("list");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("list");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(List.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfField_set() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("set");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("set");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Set.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfField_array() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("array");
            
            FieldAdapter adapter = builder.of(field);
            
            assertThat(adapter.getName()).isEqualTo("array");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Record[].class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_collection() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getCollection");
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("collection");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Collection.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_list() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getList");
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("list");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(List.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_set() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getSet");
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("set");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Set.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_array() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getArray");
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("array");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Record[].class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getGetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_collection() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setCollection", Collection.class);
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("collection");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Collection.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_list() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setList", List.class);
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("list");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(List.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_set() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setSet", Set.class);
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("set");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Set.class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_array() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setArray", Record[].class);
            
            FieldAdapter adapter = builder.of(method);
            
            assertThat(adapter.getName()).isEqualTo("array");
            assertThat(adapter.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(adapter.getType()).isEqualTo(Record[].class);
            
            assertThat(adapter.getField()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            assertThat(adapter.getSetter()).isNotEmpty();
            
            assertThat(adapter.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(adapter.getComponentType()).isEqualTo(Record.class);
            
        }
        
        
        /**
         * Genericsのタイプ
         */
        public static class GenericsClass {
            
            @XlsHorizontalRecords(tableLabel="collection")
            private Collection<Record> collection;
            
            @XlsHorizontalRecords(tableLabel="list")
            private List<Record> list;
            
            @XlsHorizontalRecords(tableLabel="set")
            private Set<Record> set;
            
            @XlsHorizontalRecords(tableLabel="array")
            private Record[] array;
            
            public Collection<Record> getCollection() {
                return collection;
            }
            
            public void setCollection(Collection<Record> collection) {
                this.collection = collection;
            }
            
            public List<Record> getList() {
                return list;
            }
            
            public void setList(List<Record> list) {
                this.list = list;
            }
            
            public Set<Record> getSet() {
                return set;
            }
            
            public void setSet(Set<Record> set) {
                this.set = set;
            }
            
            public Record[] getArray() {
                return array;
            }
            
            public void setArray(Record[] array) {
                this.array = array;
            }
            
        }
        
        private static class Record {
            
            @XlsColumn(columnName="id")
            private long id;
            
            @XlsColumn(columnName="name")
            private String name;
            
            @XlsMapColumns(previousColumnName="name")
            private Map<String, String> map;
            
        }
    
    }
    
    /**
     * エラーの場合や特殊なケース
     *
     */
    public static class Special {
        
        private FieldAdapterBuilder builder;
        
        @Before
        public void setUp() throws Exception {
            this.builder = new FieldAdapterBuilder(new AnnotationReader(null));
        }
        
        @Test
        public void testOfMethod_notAccessor() throws Exception {
            
            Method method = SampleCalss.class.getMethod("executeTest");
            
            assertThatThrownBy(() -> builder.of(method))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("'%s#%s' は、アクセッサーの形式でないメソッドです。", SampleCalss.class.getName(), "executeTest"));
            
        }
        
        public static class SampleCalss {
            
            /**
             * アクセッサではない場合
             */
            public void executeTest() {
                
            }
        
        }
        
    }
}
