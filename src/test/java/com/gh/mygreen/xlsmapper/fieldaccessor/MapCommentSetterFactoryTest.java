package com.gh.mygreen.xlsmapper.fieldaccessor;

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
 * {@link MapCommentSetterFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class MapCommentSetterFactoryTest {
    
    /**
     * コメント情報が無い場合
     *
     */
    public static class NotComment {
        
        private MapCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapCommentSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(SampleRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * マップ型のフィールドのコメント情報の場合
     *
     */
    public static class ByMapField {
        
        private MapCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapCommentSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                MapCommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment, "abc");
                
                assertThat(record.comments)
                    .hasSize(1)
                    .containsEntry("test[abc]", comment);
            }
            
        }
        
        /**
         * Map形式ではない場合
         *
         */
        private static class NoMapRecord {
            
            String comments;
            
        }
        
        /**
         * 値がサポートしていないクラスタイプの場合
         *
         */
        private static class NoSupportTypeRecord {
            
            Map<String, CellReference> comments;
            
        }
        
        private static class StringRecord {
            
            Map<String, String> comments;
            
        }
        
    }
    
    
    /**
     * メソッドによるコメント情報を格納する場合
     *
     */
    public static class ByMethod {
        
        private MapCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapCommentSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                MapCommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment, "abc");
                
                assertThat(record.addressMap)
                    .hasSize(1)
                    .containsEntry("abc", comment);
            }
            
        }
        
        private static class StringRecord {
            
            private Map<String, String> addressMap = new HashMap<>();
            
            public void setTestComment(String key, String address) {
                this.addressMap.put(key, address);
            }
            
        }
        
    }
    
    /**
     * フィールドによるコメント情報を格納する場合
     *
     */
    public static class ByField {
        
        private MapCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new MapCommentSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                MapCommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment, "abc");
                
                assertThat(record.testComment)
                    .hasSize(1)
                    .containsEntry("abc", comment);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<MapCommentSetter> commentSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private Map<String, String> testComment;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Map<String, Cell> testComment;
            
            
        }
    }

}
