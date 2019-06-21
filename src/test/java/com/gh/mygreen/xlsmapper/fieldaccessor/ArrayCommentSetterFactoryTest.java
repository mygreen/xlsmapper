package com.gh.mygreen.xlsmapper.fieldaccessor;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
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
 * {@link ArrayCommentSetterFactory}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ArrayCommentSetterFactoryTest {
    
    /**
     * コメント情報が無い場合
     *
     */
    public static class NotComment {
        
        private ArrayCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayCommentSetterFactory();
        }
        
        @Test
        public void testCreate() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(SampleRecord.class, "test");
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
        
        private ArrayCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayCommentSetterFactory();
        }
        
        /**
         * フィールドのタイプがMapではない場合
         */
        @Test
        public void testCreateWithNoMap() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(NoMapRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        /**
         * マップのGenericsのクラスタイプがサポート対象外の場合
         */
        @Test
        public void testCreateWithNoSupportType() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(NoSupportTypeRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        /**
         * マップのタイプが{@link String}の場合
         */
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayCommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment, 1);
                
                assertThat(record.comments)
                    .hasSize(1)
                    .containsEntry("test[1]", comment);
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
        
        private ArrayCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayCommentSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayCommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment, 1);
                
                assertThat(record.commentList)
                    .hasSize(2)
                    .containsExactly(null, "サンプル");
            }
            
        }
        
        private static class StringRecord {
            
            private List<String> commentList = new ArrayList<>();
            
            public void setTestComment(int index, String comment) {
                Utils.addListWithIndex(commentList, comment, index);
            }
            
        }
        
    }
    
    /**
     * フィールドによるコメント情報を格納する場合
     *
     */
    public static class ByField {
        
        private ArrayCommentSetterFactory setterFactory;
        
        @Before
        public void setUp() throws Exception {
            this.setterFactory = new ArrayCommentSetterFactory();
        }
        
        @Test
        public void testCreateWithString() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(StringRecord.class, "test");
            assertThat(commentSetter).isNotEmpty();
            
            {
                // コメント情報を設定する
                StringRecord record = new StringRecord();
                
                ArrayCommentSetter accessor = commentSetter.get();
                String comment = "サンプル";
                
                accessor.set(record, comment, 1);
                
                assertThat(record.testComment)
                    .hasSize(2)
                    .containsExactly(null, "サンプル");
            }
            
        }
        
        @Test
        public void testCreateWithNotSupportType() {
            
            Optional<ArrayCommentSetter> commentSetter = setterFactory.create(NotSupportTypeRecord.class, "test");
            assertThat(commentSetter).isEmpty();
            
        }
        
        private static class StringRecord {
            
            private List<String> testComment;
            
            
        }
        
        private static class NotSupportTypeRecord {
            
            private List<Cell> testComment;
            
            
        }
    }
    
}
