package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link ArrayLabelSetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ArrayLabelSetterFactoryTest {
    
    /**
     * ラベル情報が無い場合
     *
     */
    public static class NotLabel {
        
        private ArrayLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayLabelSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(SampleRecord.class, "test");
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
        
        private ArrayLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayLabelSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayLabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label, 1);
                
                assertThat(record.labels)
                    .hasSize(1)
                    .containsEntry("test[1]", label);
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
        
        private ArrayLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayLabelSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayLabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label, 1);
                
                assertThat(record.labelList)
                    .hasSize(2)
                    .containsExactly(null, "サンプル");
            }
            
        }
        
        private static class StringRecord {
            
            private List<String> labelList = new ArrayList<>();
            
            public void setTestLabel(int index, String label) {
                Utils.addListWithIndex(labelList, label, index);
            }
            
        }
        
    }
    
    /**
     * フィールドによるラベル情報を格納する場合
     *
     */
    public static class ByField {
        
        private ArrayLabelSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayLabelSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(labelSetter).isNotEmpty();
            
            {
                // ラベル情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayLabelSetter accessor = labelSetter.get();
                String label = "サンプル";
                
                accessor.set(record, label, 1);
                
                assertThat(record.testLabel)
                    .hasSize(2)
                    .containsExactly(null, "サンプル");
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<ArrayLabelSetter> labelSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(labelSetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private List<String> testLabel;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private List<Cell> testLabel;
            
            
        }
    }
    
}
