package com.gh.mygreen.xlsmapper.fieldaccessor;

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
 * {@link CommentSetterFactory}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class CommentSetterFactoryTest {
    
    /**
     * コメント情報が無い場合
     *
     */
    public static class NotComment {
        
        private CommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new CommentSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(SampleRecord.class, "test");
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
        
        private CommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new CommentSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                CommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment);
                
                assertThat(record.comments)
                    .hasSize(1)
                    .containsEntry("test", comment);
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
        
        private CommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new CommentSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                CommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment);
                
                assertThat(record.address).isEqualTo(comment);
            }
            
        }
        
        private static class StringRecord {
            
            private String address;
            
            public void setTestComment(String address) {
                this.address = address;
            }
            
        }
        
    }
    
    /**
     * フィールドによるコメント情報を格納する場合
     *
     */
    public static class ByField {
        
        private CommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new CommentSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                CommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment);
                
                assertThat(record.testComment).isEqualTo(comment);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<CommentSetter> commentSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private String testComment;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell testComment;
            
            
        }
    }
}
