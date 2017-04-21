package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.FieldAccessorUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;
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
     * @throws XlsMapperException
     */
    public static void checkColumns(final Sheet sheet, final Class<?> recordClass,
            final List<RecordHeader> headers, final AnnotationReader reader, final Configuration config)
                    throws XlsMapperException {
        
        List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(recordClass, reader, XlsColumn.class);
        
        for(FieldAccessor property : properties) {
            final XlsColumn column = property.getAnnotation(XlsColumn.class).get();
            
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
     * @since 1.4
     * @param sheet
     * @param records
     * @return 結合したセルのサイズを返す。
     * @throws NestMergedSizeException
     */
    public static int checkNestedMergedSizeRecords(final Sheet sheet, final List<MergedRecord> records) 
            throws NestMergedSizeException {
        
        int mergedSize = -1;
        
        for(MergedRecord record : records) {
            
            if(mergedSize < 0) {
                mergedSize = record.getMergedSize();
                continue;
            }
            
            if(mergedSize != record.getMergedSize()) {
                
                String message = MessageBuilder.create("anno.XlsNestedRecords.mergeSizeNoMatch")
                        .var("sheetName", sheet.getSheetName())
                        .var("address", record.getMergedRange().formatAsString())
                        .var("actualMergeSize", record.getMergedSize())
                        .var("expectedMergeSize", mergedSize)
                        .format();
                throw new NestMergedSizeException(sheet.getSheetName(), record.getMergedSize(), message);
            }
        }
        
        return mergedSize;
        
    }
    
    /**
     * アノテーション{@link XlsNestedRecords}の定義が、同じBeanに対して、入れ子構造になっていないかチェックする。
     * @since 1.4
     * @param recordClass チェック対象のレコードクラス
     * @param accessor アノテーションが付与されているフィールド
     * @param reader {@link AnnotationReader}のインスタンス。
     * @throws AnnotationInvalidException 入れ子構造になっている場合
     */
    public static void checkLoadingNestedRecordClass(final Class<?> recordClass, final FieldAccessor accessor, 
            final AnnotationReader reader) throws AnnotationInvalidException {
        
        ArgUtils.notNull(recordClass, "recordClass");
        ArgUtils.notNull(accessor, "accessor");
        ArgUtils.notNull(reader, "reader");
        
        // 再帰的にチェックしていく。
        List<Class<?>> nestedRecordClasses = new ArrayList<>();
        checkLoadingNestedRecordClass(recordClass, accessor, reader, nestedRecordClasses);
        
        
    }
    
    private static void checkLoadingNestedRecordClass(final Class<?> recordClass, final FieldAccessor accessor, 
            final AnnotationReader reader, final List<Class<?>> nestedRecordClasses) throws AnnotationInvalidException {
        
        if(recordClass == Object.class) {
            return;
        }
        
        if(nestedRecordClasses.contains(recordClass)) {
            throw new AnnotationInvalidException(MessageBuilder.create("anno.XlsNestedRecords.recursive")
                    .var("property", accessor.getNameWithClass())
                    .format());
        }
        
        final List<FieldAccessor> nestedProperties = FieldAccessorUtils.getPropertiesWithAnnotation(recordClass, reader, XlsNestedRecords.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());
        
        for(FieldAccessor property : nestedProperties) {
            
            nestedRecordClasses.add(recordClass);
            
            final XlsNestedRecords nestedAnno = property.getAnnotation(XlsNestedRecords.class).get();
            final Class<?> clazz = property.getType();
            
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getComponentType();
                }
                
                checkLoadingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else if(clazz.isArray()) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getComponentType();
                }
                
                checkLoadingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else {
                // mapping by one-to-tone
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getType();
                }
                
                checkLoadingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
            }
            
        }
        
        
    }
    
    /**
     * アノテーション{@link XlsNestedRecords}の定義が、同じBeanに対して、入れ子構造になっていないかチェックする。
     * @since 1.4
     * @param recordClass チェック対象のレコードクラス
     * @param accessor アノテーションが付与されているフィールド
     * @param reader {@link AnnotationReader}のインスタンス。
     * @throws AnnotationInvalidException 入れ子構造になっている場合
     */
    public static void checkSavingNestedRecordClass(final Class<?> recordClass, final FieldAccessor accessor, 
            final AnnotationReader reader) throws AnnotationInvalidException {
        
        ArgUtils.notNull(recordClass, "recordClass");
        ArgUtils.notNull(accessor, "accessor");
        ArgUtils.notNull(reader, "reader");
        
        // 再帰的にチェックしていく。
        List<Class<?>> nestedRecordClasses = new ArrayList<>();
        checkSavingNestedRecordClass(recordClass, accessor, reader, nestedRecordClasses);
        
        
    }
    
    private static void checkSavingNestedRecordClass(final Class<?> recordClass, final FieldAccessor accessor, 
            final AnnotationReader reader, final List<Class<?>> nestedRecordClasses) throws AnnotationInvalidException {
        
        if(recordClass == Object.class) {
            return;
        }
        
        if(nestedRecordClasses.contains(recordClass)) {
            throw new AnnotationInvalidException(MessageBuilder.create("anno.XlsNestedRecords.recursive")
                    .var("property", accessor.getNameWithClass())
                    .format());
        }
        
        final List<FieldAccessor> nestedProperties = FieldAccessorUtils.getPropertiesWithAnnotation(recordClass, reader, XlsNestedRecords.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());
        
        for(FieldAccessor property : nestedProperties) {
            
            nestedRecordClasses.add(recordClass);
            
            final XlsNestedRecords nestedAnno = property.getAnnotation(XlsNestedRecords.class).get();
            final Class<?> clazz = property.getType();
            
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getComponentType();
                }
                
                checkSavingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else if(clazz.isArray()) {
                // mapping by one-to-many
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getComponentType();
                }
                
                checkSavingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
                
            } else {
                // mapping by one-to-tone
                
                Class<?> nestedDecordClass = nestedAnno.recordClass();
                if(nestedDecordClass == Object.class) {
                    nestedDecordClass = property.getType();
                }
                
                checkSavingNestedRecordClass(nestedDecordClass, property, reader, nestedRecordClasses);
            }
            
        }
        
        
    }
}
