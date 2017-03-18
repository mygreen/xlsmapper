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
 * {@link LabelGetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class LabelGetterFactoryTest {
    
    /**
     * ラベル情報が無い場合
     *
     */
    public static class NotLabel {
        
        private LabelGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new LabelGetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(SampleRecord.class, "test");
            assertThat(labelGetter).isEmpty();
            
        }
        
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * マップ型のフィールドのラベル情報の場合
     *
     */
    public static class ByMapField {
        
        private LabelGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new LabelGetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(NoMapRecord.class, "test");
            assertThat(labelGetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(labelGetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(labelGetter).isNotEmpty();
            
            {
                // マップのインスタンスがない場合
                StringRecord record = new StringRecord();
                
                LabelGetter accessor = labelGetter.get();
                
                
                Optional<String> actual = accessor.get(record);
                assertThat(actual).isEmpty();
            }
            
            {
                // ラベル情報を取得する
                StringRecord record = new StringRecord();
                
                LabelGetter accessor = labelGetter.get();
                String label = "サンプル";
                
                record.labels = new HashMap<>();
                record.labels.put("test", label);
                
                Optional<String> actual = accessor.get(record);
                
                assertThat(actual).contains(label);
            }
            
            {
                // ラベル情報を取得する - 該当するフィールドの値が存在しない
                StringRecord record = new StringRecord();
                
                LabelGetter accessor = labelGetter.get();
                
                record.labels = new HashMap<>();
                
                Optional<String> actual = accessor.get(record);
                
                assertThat(actual).isEmpty();
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
        
        private LabelGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new LabelGetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(labelGetter).isNotEmpty();
            
            {
                // ラベル情報を取得する
                StringRecord record = new StringRecord();
                
                LabelGetter accessor = labelGetter.get();
                String label = "サンプル";
                record.address = label;
                
                Optional<String> actual = accessor.get(record);
                
                assertThat(actual).contains(label);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(labelGetter).isEmpty();
            
            
        }
        
        private static class StringRecord {
            
            private String address;
            
            public String getTestLabel() {
                return address;
            }
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell address;
            
            Cell getTestLabel() {
                return address;
            }
            
        }
        
    }
    
    /**
     * フィールドによるラベル情報を格納する場合
     *
     */
    public static class ByField {
        
        private LabelGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new LabelGetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(labelGetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                LabelGetter accessor = labelGetter.get();
                String label = "サンプル";
                record.testLabel = label;
                
                Optional<String> actual = accessor.get(record);
                
                assertThat(actual).contains(label);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<LabelGetter> labelGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(labelGetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private String testLabel;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell testLabel;
            
            
        }
    }
}
