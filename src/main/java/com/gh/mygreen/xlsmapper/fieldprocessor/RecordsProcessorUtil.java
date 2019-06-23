package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.impl.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.FieldAccessorUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * Provides generic utility methods for {@link HorizontalRecordsProcessor} and {@link VerticalRecordsProcessor}.
 * 
 * @version 1.4
 * @author Naoki Takezoe
 */
public class RecordsProcessorUtil {
    
    /**
     * アノテーション{@link XlsColumn}の属性columnNameで指定した値が、ヘッダーセルに存在するかチェックする。
     * @param sheet
     * @param recordClass
     * @param headers
     * @param reader
     * @param config
     * @throws CellNotFoundException セルが見つからない場合
     */
    public static void checkColumns(final Sheet sheet, final Class<?> recordClass,
            final List<RecordHeader> headers, final AnnotationReader reader, final Configuration config)
                    throws CellNotFoundException {
        
        List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(recordClass, reader, XlsColumn.class);
        
        for(FieldAccessor property : properties) {
            final XlsColumn column = property.getAnnotationNullable(XlsColumn.class);
            
            if(column.optional()){
                continue;
            }
            
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
    
    /**
     * アノテーション{@link XlsMapColumns}の属性previousColumnName、nextColumnNameで指定した値がヘッダーセルに存在するかチェックする。
     * @since 2.0
     * @param sheet
     * @param recordClass
     * @param headers
     * @param reader
     * @param config
     * @throws CellNotFoundException セルが見つからない場合
     */
    public static void checkMapColumns(final Sheet sheet, final Class<?> recordClass,
            final List<RecordHeader> headers, final AnnotationReader reader, final Configuration config)
                    throws CellNotFoundException {
        
        List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(recordClass, reader, XlsMapColumns.class);
        
        for(FieldAccessor property : properties) {
            final XlsMapColumns mapColumns = property.getAnnotationNullable(XlsMapColumns.class);
            if(mapColumns.optional()) {
                continue;
            }
            
            final String previousColumnName = mapColumns.previousColumnName();
            boolean foundPrevious = headers.stream()
                .filter(info -> Utils.matches(info.getLabel(), previousColumnName, config))
                .findFirst()
                .isPresent();
            
            if(!foundPrevious) {
                throw new CellNotFoundException(sheet.getSheetName(), previousColumnName);
            }
            
            final String nextColumnName = mapColumns.nextColumnName();
            if(!nextColumnName.isEmpty()) {
                boolean foundNext = headers.stream()
                        .filter(info -> Utils.matches(info.getLabel(), nextColumnName, config))
                        .findFirst()
                        .isPresent();
                
                if(!foundNext) {
                    throw new CellNotFoundException(sheet.getSheetName(), nextColumnName);
                }
            }
            
        }
    }
    
    /**
     * アノテーション{@link XlsArrayColumns}の属性columnNameで指定した値がヘッダーセルに存在するかチェックする。
     * @since 2.0
     * @param sheet
     * @param recordClass
     * @param headers
     * @param reader
     * @param config
     * @throws CellNotFoundException セルが見つからない場合
     */
    public static void checkArrayColumns(final Sheet sheet, final Class<?> recordClass,
            final List<RecordHeader> headers, final AnnotationReader reader, final Configuration config) {
        
        List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(recordClass, reader, XlsArrayColumns.class);
        
        for(FieldAccessor property : properties) {
            final XlsArrayColumns arrayColumns = property.getAnnotationNullable(XlsArrayColumns.class);
            if(arrayColumns.optional()) {
                continue;
            }
            
            final String columnName = arrayColumns.columnName();
            boolean found = headers.stream()
                .filter(info -> Utils.matches(info.getLabel(), columnName, config))
                .findFirst()
                .isPresent();
            
            if(!found) {
                throw new CellNotFoundException(sheet.getSheetName(), columnName);
            }
            
        }
        
    }
    
    /**
     * マッピング対象となるセルの結合サイズが、全て同じかチェックする。
     * @since 1.4
     * @param sheet
     * @param records
     * @return 結合したセルのサイズを返す。
     * @throws NestedRecordMergedSizeException
     */
    public static int checkNestedMergedSizeRecords(final Sheet sheet, final List<MergedRecord> records) 
            throws NestedRecordMergedSizeException {
        
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
                throw new NestedRecordMergedSizeException(sheet.getSheetName(), record.getMergedSize(), message);
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
            
            final XlsNestedRecords nestedAnno = property.getAnnotationNullable(XlsNestedRecords.class);
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
            
            final XlsNestedRecords nestedAnno = property.getAnnotationNullable(XlsNestedRecords.class);
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
