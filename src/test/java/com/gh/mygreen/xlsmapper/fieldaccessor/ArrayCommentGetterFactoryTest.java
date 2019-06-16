package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * {@link ArrayCommentGetterFactory}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ArrayCommentGetterFactoryTest {
    
    /**
     * コメント情報が無い場合
     *
     */
    public static class NotComment {
        
        private ArrayCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new ArrayCommentGetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(SampleRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        
        private static class SampleRecord {
            
        }
    
    }
    
    /**
     * リスト型のフィールドのコメント情報の場合
     *
     */
    public static class ByArrayField {
        
        private ArrayCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new ArrayCommentGetterFactory();
        }
        
        /**
         * フィールドのタイプがArrayではない場合
         */
        @Test
        public void testCreateWithNoArray() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(NoArrayRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        /**
         * リストのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        /**
         * リストのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(commentGetter).isNotEmpty();
            
            {
                // リストのインスタンスがない場合
                StringRecord record = new StringRecord();
                
                ArrayCommentGetter accessor = commentGetter.get();
                
                
                Optional<String> actual = accessor.get(record, 1);
                assertThat(actual).isEmpty();
            }
            
            {
                // コメント情報を取得する
                StringRecord record = new StringRecord();
                
                ArrayCommentGetter accessor = commentGetter.get();
                String comment = "サンプル";
                
                record.comments = new HashMap<>();
                record.comments.put("test[1]", comment);
                
                Optional<String> actual = accessor.get(record, 1);
                
                assertThat(actual).contains(comment);
            }
            
            {
                // コメント情報を取得する - 該当するフィールドの値が存在しない
                StringRecord record = new StringRecord();
                
                ArrayCommentGetter accessor = commentGetter.get();
                
                record.comments = new HashMap<>();
                
                Optional<String> actual = accessor.get(record, 1);
                
                assertThat(actual).isEmpty();
            }
            
        }
        
        /**
         * Array形式ではない場合
         *
         */
        private static class NoArrayRecord {
            
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
        
        private ArrayCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new ArrayCommentGetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(commentGetter).isNotEmpty();
            
            {
                // コメント情報を取得する
                StringRecord record = new StringRecord();
                
                ArrayCommentGetter accessor = commentGetter.get();
                String comment = "サンプル";
                record.commentList = new ArrayList<>();
                Utils.addListWithIndex(record.commentList, comment, 1);
                
                Optional<String> actual = accessor.get(record, 1);
                
                assertThat(actual).contains(comment);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
            
        }
        
        private static class StringRecord {
            
            private List<String> commentList = new ArrayList<>();
            
            public String getTestComment(int index) {
                return commentList.get(index);
            }
            
        }
        
        private static class NotSupportTypeRecord {
            
            private Cell address;
            
            Cell getTestComment(int index) {
                return address;
            }
            
        }
        
    }
    
    /**
     * フィールドによるコメント情報を格納する場合
     *
     */
    public static class ByField {
        
        private ArrayCommentGetterFactory getterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.getterFactory = new ArrayCommentGetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(StringRecord.class, "test");
            assertThat(commentGetter).isNotEmpty();
            
            {
                // フィールドのインスタンスがnullの場合
             // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayCommentGetter accessor = commentGetter.get();
                Optional<String> actual = accessor.get(record, 1);
                
                assertThat(actual).isEmpty();
            }
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayCommentGetter accessor = commentGetter.get();
                String comment = "サンプル";
                record.testComment = new ArrayList<>();
                Utils.addListWithIndex(record.testComment, comment, 1);
                
                Optional<String> actual = accessor.get(record, 1);
                
                assertThat(actual).contains(comment);
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<ArrayCommentGetter> commentGetter = getterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentGetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private List<String> testComment;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private List<Cell> testComment;
            
            
        }
    }
}
