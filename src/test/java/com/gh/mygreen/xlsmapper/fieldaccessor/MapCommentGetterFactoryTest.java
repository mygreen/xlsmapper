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
 * {@link MapCommentGetterFactory}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class MapCommentGetterFactoryTest {
    
    /**
     * コメント情報が無い場合
     *
     */
    public static class NotComment {
        
        private MapCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new MapCommentGetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(SampleRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * マップ型のフィールドのコメント情報の場合
     *
     */
    public static class ByMapField {
        
        private MapCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new MapCommentGetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(NoMapRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(commentGetter).isNotEmpty();
            
            {
                // マップのインスタンスがない場合
                StringRecord record = new StringRecord();
                
                MapCommentGetter accessor = commentGetter.get();
                
                
                Optional<String> actual = accessor.get(record, "abc");
                assertThat(actual).isEmpty();
            }
            
            {
                // コメント情報を取得する
                StringRecord record = new StringRecord();
                
                MapCommentGetter accessor = commentGetter.get();
                String comment = "サンプル";
                
                record.comments = new HashMap<>();
                record.comments.put("test[abc]", comment);
                
                Optional<String> actual = accessor.get(record, "abc");
                
                assertThat(actual).contains(comment);
            }
            
            {
                // コメント情報を取得する - 該当するフィールドの値が存在しない
                StringRecord record = new StringRecord();
                
                MapCommentGetter accessor = commentGetter.get();
                
                record.comments = new HashMap<>();
                
                Optional<String> actual = accessor.get(record, "abc");
                
                assertThat(actual).isEmpty();
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
        
        private MapCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new MapCommentGetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(commentGetter).isNotEmpty();
            
            {
                // コメント情報を取得する
                StringRecord record = new StringRecord();
                
                MapCommentGetter accessor = commentGetter.get();
                String comment = "サンプル";
                record.addressMap.put("abc", comment);
                
                Optional<String> actual = accessor.get(record, "abc");
                
                assertThat(actual).contains(comment);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
            
        }
        
        private static class StringRecord {
            
            private Map<String, String> addressMap = new HashMap<>();
            
            public String getTestComment(String key) {
                return addressMap.get(key);
            }
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell address;
            
            Cell getTestComment() {
                return address;
            }
            
        }
        
    }
    
    /**
     * フィールドによるコメント情報を格納する場合
     *
     */
    public static class ByField {
        
        private MapCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new MapCommentGetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(commentGetter).isNotEmpty();
            
            {
                // フィールドのインスタンスがnullの場合
             // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                MapCommentGetter accessor = commentGetter.get();
                Optional<String> actual = accessor.get(record, "abc");
                
                assertThat(actual).isEmpty();
            }
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                MapCommentGetter accessor = commentGetter.get();
                String comment = "サンプル";
                record.testComment = new HashMap<String, String>();
                record.testComment.put("abc" ,comment);
                
                Optional<String> actual = accessor.get(record, "abc");
                
                assertThat(actual).contains(comment);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<MapCommentGetter> commentGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private Map<String, String> testComment;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Map<String, Cell> testComment ;
            
            
        }
    }
}
