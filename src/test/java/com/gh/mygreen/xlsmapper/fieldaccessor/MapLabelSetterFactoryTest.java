package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;


/**
 * {@link MapLabelSetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class MapLabelSetterFactoryTest {
    
    /**
     * ラベル情報が無い場合
     *
     */
    public static class NotLabel {
        
        private MapLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapLabelSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(SampleRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * マップ型のフィールドのラベル情報の場合
     *
     */
    public static class ByMapField {
        
        private MapLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapLabelSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                MapLabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label, "abc");
                
                assertThat(record.labels)
                    .hasSize(1)
                    .containsEntry("test[abc]", label);
            }
            
        }
        
        /**
         * Map形式ではない場合
         *
         */
        private static class NoMapRecord {
            
            String labels;
            
        }
        
        /**
         * 値がサポートしていないクラスタイプの場合
         *
         */
        private static class NoSupportTypeRecord {
            
            Map<String, CellReference> labels;
            
        }
        
        private static class StringRecord {
            
            Map<String, String> labels;
            
        }
        
    }
    
    
    /**
     * メソッドによるラベル情報を格納する場合
     *
     */
    public static class ByMethod {
        
        private MapLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapLabelSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                MapLabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label, "abc");
                
                assertThat(record.addressMap)
                    .hasSize(1)
                    .containsEntry("abc", label);
            }
            
        }
        
        private static class StringRecord {
            
            private Map<String, String> addressMap = new HashMap<>();
            
            public void setTestLabel(String key, String address) {
                this.addressMap.put(key, address);
            }
            
        }
        
    }
    
    /**
     * フィールドによるラベル情報を格納する場合
     *
     */
    public static class ByField {
        
        private MapLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapLabelSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                MapLabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label, "abc");
                
                assertThat(record.testLabel)
                    .hasSize(1)
                    .containsEntry("abc", label);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<MapLabelSetter> labelSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private Map<String, String> testLabel;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Map<String, Cell> testLabel;
            
            
        }
    }

}
