package com.gh.mygreen.xlsmapper.fieldprocessor;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorFactory;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordsProcessorUtil;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;

/**
 * {@link RecordsProcessorUtil}のテスタ
 *
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
public class RecordsProcessorUtilTest {
    
    private AnnotationReader reader;
    private FieldAccessorFactory adpterFactory;
    
    @Before
    public void setupBefore() {
         this.reader = new AnnotationReader(null);
         this.adpterFactory = new FieldAccessorFactory(reader);
    } 
    
    @Test
    public void testCheckLoadingNestedRecordClass_success() throws Exception {
        
        Field field = NormalNestedSheet.class.getDeclaredField("largeRecords");
        field.setAccessible(true);
        FieldAccessor accessor = adpterFactory.create(field);
        
        Class<?> recordClass = NormalNestedSheet.LargeRecord.class;
        RecordsProcessorUtil.checkLoadingNestedRecordClass(recordClass, accessor, reader);
    }
    
    @Test(expected=AnnotationInvalidException.class)
    public void testCheckLoadingNestedRecordClass_wrong() throws Exception {
        
        AnnotationReader reader = new AnnotationReader(null);
        Field field = WrongNestedSheet.class.getDeclaredField("largeRecords");
        field.setAccessible(true);
        FieldAccessor accessor = adpterFactory.create(field);
        
        Class<?> recordClass = WrongNestedSheet.LargeRecord.class;
        RecordsProcessorUtil.checkLoadingNestedRecordClass(recordClass, accessor, reader);
        
        fail();
    }
    
    @Test
    public void testCheckSavingNestedRecordClass_success() throws Exception {
        
        AnnotationReader reader = new AnnotationReader(null);
        Field field = NormalNestedSheet.class.getDeclaredField("largeRecords");
        field.setAccessible(true);
        FieldAccessor accessor = adpterFactory.create(field);
        
        Class<?> recordClass = NormalNestedSheet.LargeRecord.class;
        RecordsProcessorUtil.checkSavingNestedRecordClass(recordClass, accessor, reader);
    }
    
    @Test(expected=AnnotationInvalidException.class)
    public void testCheckSavingNestedRecordClass_wrong() throws Exception {
        
        AnnotationReader reader = new AnnotationReader(null);
        Field field = WrongNestedSheet.class.getDeclaredField("largeRecords");
        field.setAccessible(true);
        FieldAccessor accessor = adpterFactory.create(field);
        
        Class<?> recordClass = WrongNestedSheet.LargeRecord.class;
        RecordsProcessorUtil.checkSavingNestedRecordClass(recordClass, accessor, reader);
        
        fail();
    }
    
    @XlsSheet(name="正常な定義のネストした表")
    private static class NormalNestedSheet {
        
        @XlsHorizontalRecords(tableLabel="機能一覧")
        private List<LargeRecord> largeRecords;
        
        private static class LargeRecord {
            
            @XlsColumn(columnName="大分類")
            private String largeName;
            
            @XlsColumn(columnName="説明（大分類）")
            private String largeDescription;
            
            @XlsNestedRecords
            private MiddleRecord[] middleRecords;
            
        }
        
        private static class MiddleRecord {
            
            @XlsColumn(columnName="中分類")
            private String middleName;
            
            @XlsColumn(columnName="説明（中分類）")
            private String middleDescription;
            
            @XlsNestedRecords
            private SmallRecord smallRecord;
            
        }
        
        private static class SmallRecord {
            
            @XlsColumn(columnName="項目")
            private String name;
            
            @XlsColumn(columnName="値")
            private String value;
            
        }
        
    }
    
    @XlsSheet(name="間違った定義のネストした表")
    private static class WrongNestedSheet {
        
        @XlsHorizontalRecords(tableLabel="機能一覧")
        private List<LargeRecord> largeRecords;
        
        private static class LargeRecord {
            
            @XlsColumn(columnName="大分類")
            private String largeName;
            
            @XlsColumn(columnName="説明（大分類）")
            private String largeDescription;
            
            @XlsNestedRecords
            private MiddleRecord[] middleRecords;
            
        }
        
        private static class MiddleRecord {
            
            @XlsColumn(columnName="中分類")
            private String middleName;
            
            @XlsColumn(columnName="説明（中分類）")
            private String middleDescription;
            
            @XlsNestedRecords
            private LargeRecord smallRecord;
            
        }
        
    }
}
