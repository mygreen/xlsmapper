package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * Provides generic utility methods for {@link HorizontalRecordsProcessor} and {@link VerticalRecordsProcessor}.
 * 
 * @version 1.4
 * @author Naoki Takezoe
 */
public class RecordsProcessorUtil {
    
    /**
     * アノテーションXlsColumnの属性columnNameで指定した値が、ヘッダーセルに存在するかチェックする。
     * @param sheet
     * @param recordClass
     * @param headers
     * @param reader
     * @param config
     * @throws Exception
     */
    public static void checkColumns(final Sheet sheet, final Class<?> recordClass,
            final List<RecordHeader> headers, final AnnotationReader reader, final XlsMapperConfig config)
                    throws XlsMapperException {
        
        for(FieldAdaptor property : Utils.getLoadingColumnProperties(recordClass, null, reader, config)) {
            final XlsColumn column = property.getLoadingAnnotation(XlsColumn.class);
            
            if(!column.optional()){
                String columnName = column.columnName();
                boolean find = false;
                for(RecordHeader info: headers){
                    if(Utils.matches(info.getLabel(), columnName, config)){
                        find = true;
                        break;
                    }
                }
                if(!find){
                    throw new CellNotFoundException(sheet.getSheetName(), columnName);
                }
            }
        }
        
    }
    
    /**
     * マッピング対象となるセルの結合サイズが、全て同じかチェックする。
     * 
     * @since 1.4
     * @param sheet
     * @param records
     * @param 結合したセルのサイズを返す。
     * @throws NestMergedSizeException 
     */
    public static int checkNestedMergedSizeRecords(final Sheet sheet, final List<MergedRecord> records) throws NestMergedSizeException {
        
        int mergedSize = -1;
        
        for(MergedRecord record : records) {
            
            if(mergedSize < 0) {
                mergedSize = record.getMergedSize();
                continue;
            }
            
            if(mergedSize != record.getMergedSize()) {
                String message = String.format("Not match merged size. In sheet '%s' with cell '%s', expected merged size=%s, but actual merged size=%s.",
                        sheet.getSheetName(), record.getMergedRange().formatAsString(), mergedSize, record.getMergedSize());
                
                throw new NestMergedSizeException(sheet.getSheetName(), record.getMergedSize(), message);
            }
        }
        
        return mergedSize;
        
    }
    
    /**
     * アノテーション{@link XlsNestedRecords}の定義が、同じBeanに対して、入れ子構造になっていないかチェックする。
     * @since 1.4
     * @param recordClass チェック対象のレコードクラス
     * @param adaptor アノテーションが付与されているフィールド
     * @param reader {@link AnnotationReader}のインスタンス。
     * @throws AnnotationInvalidException 入れ子構造になっている場合
     */
    public static void checkLoadingNestedRecordClass(final Class<?> recordClass, final FieldAdaptor adaptor, 
            final AnnotationReader reader) throws AnnotationInvalidException {
        
        ArgUtils.notNull(recordClass, "recordClass");
        ArgUtils.notNull(adaptor, "adaptor");
        ArgUtils.notNull(reader, "reader");
        
        // 再帰的にチェックしていく。
        List<Class<?>> nestedRecordClasses = new ArrayList<>();
        checkLoadingNestedRecordClass(recordClass, adaptor, reader, nestedRecordClasses);
        
        
    }
    
    private static void checkLoadingNestedRecordClass(final Class<?> recordClass, final FieldAdaptor adaptor, 
            final AnnotationReader reader, final List<Class<?>> nestedRecordClasses) throws AnnotationInvalidException {
        
        if(recordClass == Object.class) {
            return;
        }
        
        if(nestedRecordClasses.contains(recordClass)) {
            XlsNestedRecords anno = adaptor.getLoadingAnnotation(XlsNestedRecords.class);
            throw new AnnotationInvalidException(
                    String.format("With '%s', annotation '@XlsNestedRecords' should only granted not same classes.",
                            adaptor.getNameWithClass()),
                    anno);
        }
        
        final List<FieldAdaptor> nestedProperties = Utils.getLoadingNestedRecordsProperties(recordClass, reader);
        for(FieldAdaptor property : nestedProperties) {
            
            nestedRecordClasses.add(recordClass);
            
            final XlsNestedRecords nestedAnno = property.getLoadingAnnotation(XlsNestedRecords.class);
            final Class<?> clazz = property.getTargetClass();
            
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getLoadingGenericClassType();
                }
                
                checkLoadingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else if(clazz.isArray()) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getLoadingGenericClassType();
                }
                
                checkLoadingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else {
                // mapping by one-to-tone
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getTargetClass();
                }
                
                checkLoadingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
            }
            
        }
        
        
    }
    
    /**
     * アノテーション{@link XlsNestedRecords}の定義が、同じBeanに対して、入れ子構造になっていないかチェックする。
     * @since 1.4
     * @param recordClass チェック対象のレコードクラス
     * @param adaptor アノテーションが付与されているフィールド
     * @param reader {@link AnnotationReader}のインスタンス。
     * @throws AnnotationInvalidException 入れ子構造になっている場合
     */
    public static void checkSavingNestedRecordClass(final Class<?> recordClass, final FieldAdaptor adaptor, 
            final AnnotationReader reader) throws AnnotationInvalidException {
        
        ArgUtils.notNull(recordClass, "recordClass");
        ArgUtils.notNull(adaptor, "adaptor");
        ArgUtils.notNull(reader, "reader");
        
        // 再帰的にチェックしていく。
        List<Class<?>> nestedRecordClasses = new ArrayList<>();
        checkSavingNestedRecordClass(recordClass, adaptor, reader, nestedRecordClasses);
        
        
    }
    
    private static void checkSavingNestedRecordClass(final Class<?> recordClass, final FieldAdaptor adaptor, 
            final AnnotationReader reader, final List<Class<?>> nestedRecordClasses) throws AnnotationInvalidException {
        
        if(recordClass == Object.class) {
            return;
        }
        
        if(nestedRecordClasses.contains(recordClass)) {
            XlsNestedRecords anno = adaptor.getSavingAnnotation(XlsNestedRecords.class);
            throw new AnnotationInvalidException(
                    String.format("With '%s', annotation '@XlsNestedRecords' should only granted not same classes.",
                            adaptor.getNameWithClass()),
                    anno);
        }
        
        final List<FieldAdaptor> nestedProperties = Utils.getSavingNestedRecordsProperties(recordClass, reader);
        for(FieldAdaptor property : nestedProperties) {
            
            nestedRecordClasses.add(recordClass);
            
            final XlsNestedRecords nestedAnno = property.getSavingAnnotation(XlsNestedRecords.class);
            final Class<?> clazz = property.getTargetClass();
            
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getSavingGenericClassType();
                }
                
                checkSavingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else if(clazz.isArray()) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getSavingGenericClassType();
                }
                
                checkSavingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else {
                // mapping by one-to-tone
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getTargetClass();
                }
                
                checkSavingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
            }
            
        }
        
        
    }
}
