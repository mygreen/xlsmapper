package com.gh.mygreen.xlsmapper.fieldaccessor;

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
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link FieldAccessorFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class FieldAccessorFactoryTest {
    
    /**
     * 基本構成のテスト
     */
    public static class Basic {
        
        private FieldAccessorFactory accessorFactory;
        
        @Before
        public void setUp() throws Exception {
            this.accessorFactory = new FieldAccessorFactory(new AnnotationReader(null));
        }
        
        /**
         * フィールドしか存在しない場合
         */
        @Test
        public void testOfField_onlyField() throws Exception {
            
            Field field = BasicClass.class.getDeclaredField("onlyField");
            
            FieldAccessor accessor = accessorFactory.create(field);
            
            assertThat(accessor.getName()).isEqualTo("onlyField");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(String.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isEmpty();
            assertThat(accessor.getSetter()).isEmpty();
            
            assertThat(accessor.hasAnnotation(XlsCell.class)).isTrue();
        }
        
        /**
         * アクセッサが存在する場合
         */
        @Test
        public void testOfField_existAccessor() throws Exception {
            
            Field field = BasicClass.class.getDeclaredField("existAccessor");
            
            FieldAccessor accessor = accessorFactory.create(field);
            
            assertThat(accessor.getName()).isEqualTo("existAccessor");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(LocalDate.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsCellOption.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsOrder.class)).isTrue();
            
        }
        
        /**
         * プリミティブ型のbooleanが存在する場合
         */
        @Test
        public void testOfField_primitiveBoolean() throws Exception {
            
            Field field = BasicClass.class.getDeclaredField("primitiveBool");
            
            FieldAccessor accessor = accessorFactory.create(field);
            
            assertThat(accessor.getName()).isEqualTo("primitiveBool");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(boolean.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsDefaultValue.class)).isTrue();
            
            
        }
        
        /**
         * getterメソッド
         */
        @Test
        public void testOfGetter_existAccessor() throws Exception {
            
            Method method = BasicClass.class.getMethod("getExistAccessor");
            
            FieldAccessor accessor = accessorFactory.create(method);
            
            assertThat(accessor.getName()).isEqualTo("existAccessor");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(LocalDate.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsCellOption.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsOrder.class)).isTrue();
            
        }
        
        /**
         * setterメソッド
         */
        @Test
        public void testOfSetter_existAccessor() throws Exception {
            
            Method method = BasicClass.class.getMethod("setExistAccessor", LocalDate.class);
            
            FieldAccessor accessor = accessorFactory.create(method);
            
            assertThat(accessor.getName()).isEqualTo("existAccessor");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(LocalDate.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsCellOption.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsOrder.class)).isTrue();
            
        }
        
        /**
         * プリミティブ型のbooleanが存在する場合
         */
        @Test
        public void testOfGetter_primitiveBoolean() throws Exception {
            
            Method method = BasicClass.class.getDeclaredMethod("isPrimitiveBool");
            
            FieldAccessor accessor = accessorFactory.create(method);
            
            assertThat(accessor.getName()).isEqualTo("primitiveBool");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(boolean.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsDefaultValue.class)).isTrue();
            
            
        }
        
        /**
         * プリミティブ型のbooleanが存在する場合
         */
        @Test
        public void testOfSetter_primitiveBoolean() throws Exception {
            
            Method method = BasicClass.class.getDeclaredMethod("setPrimitiveBool", boolean.class);
            
            FieldAccessor accessor = accessorFactory.create(method);
            
            assertThat(accessor.getName()).isEqualTo("primitiveBool");
            assertThat(accessor.getDeclaringClass()).isEqualTo(BasicClass.class);
            assertThat(accessor.getType()).isEqualTo(boolean.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsLabelledCell.class)).isTrue();
            assertThat(accessor.hasAnnotation(XlsDefaultValue.class)).isTrue();
            
            
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
        
        private FieldAccessorFactory builder;
        
        @Before
        public void setUp() throws Exception {
            this.builder = new FieldAccessorFactory(new AnnotationReader(null));
        }
        
        @Test
        public void testOfField_collection() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("collection");
            
            FieldAccessor accessor = builder.create(field);
            
            assertThat(accessor.getName()).isEqualTo("collection");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Collection.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfField_list() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("list");
            
            FieldAccessor accessor = builder.create(field);
            
            assertThat(accessor.getName()).isEqualTo("list");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(List.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfField_set() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("set");
            
            FieldAccessor accessor = builder.create(field);
            
            assertThat(accessor.getName()).isEqualTo("set");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Set.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfField_array() throws Exception {
            
            Field field = GenericsClass.class.getDeclaredField("array");
            
            FieldAccessor accessor = builder.create(field);
            
            assertThat(accessor.getName()).isEqualTo("array");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Record[].class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_collection() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getCollection");
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("collection");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Collection.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_list() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getList");
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("list");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(List.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_set() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getSet");
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("set");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Set.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfGetter_array() throws Exception {
            
            Method method = GenericsClass.class.getMethod("getArray");
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("array");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Record[].class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getGetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_collection() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setCollection", Collection.class);
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("collection");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Collection.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_list() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setList", List.class);
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("list");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(List.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_set() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setSet", Set.class);
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("set");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Set.class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
        }
        
        @Test
        public void testOfSetter_array() throws Exception {
            
            Method method = GenericsClass.class.getMethod("setArray", Record[].class);
            
            FieldAccessor accessor = builder.create(method);
            
            assertThat(accessor.getName()).isEqualTo("array");
            assertThat(accessor.getDeclaringClass()).isEqualTo(GenericsClass.class);
            assertThat(accessor.getType()).isEqualTo(Record[].class);
            
            assertThat(accessor.getField()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            assertThat(accessor.getSetter()).isNotEmpty();
            
            assertThat(accessor.hasAnnotation(XlsHorizontalRecords.class)).isTrue();
            
            assertThat(accessor.getComponentType()).isEqualTo(Record.class);
            
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
        
        private FieldAccessorFactory builder;
        
        @Before
        public void setUp() throws Exception {
            this.builder = new FieldAccessorFactory(new AnnotationReader(null));
        }
        
        @Test
        public void testOfMethod_notAccessor() throws Exception {
            
            Method method = SampleCalss.class.getMethod("executeTest");
            
            assertThatThrownBy(() -> builder.create(method))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("'%s#%s'は、アクセッサーの形式でないメソッドです。", SampleCalss.class.getName(), "executeTest"));
            
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
