package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link LabelSetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class LabelSetterFactoryTest {
    
    /**
     * ラベル情報が無い場合
     *
     */
    public static class NotLabel {
        
        private LabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new LabelSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(SampleRecord.class, "test");
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
        
        private LabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new LabelSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                LabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label);
                
                assertThat(record.labels)
                    .hasSize(1)
                    .containsEntry("test", label);
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
        
        private LabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new LabelSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                LabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label);
                
                assertThat(record.address).isEqualTo(label);
            }
            
        }
        
        private static class StringRecord {
            
            private String address;
            
            public void setTestLabel(String address) {
                this.address = address;
            }
            
        }
        
    }
    
    /**
     * フィールドによるラベル情報を格納する場合
     *
     */
    public static class ByField {
        
        private LabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new LabelSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                LabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label);
                
                assertThat(record.testLabel).isEqualTo(label);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<LabelSetter> labelSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private String testLabel;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell testLabel;
            
            
        }
    }
}
