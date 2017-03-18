package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.NeedProcess;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecordsForIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCellForIterateTable;
import com.gh.mygreen.xlsmapper.annotation.XlsListener;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxy;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxyComparator;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.util.FieldAdapterUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;


/**
 * アノテーション{@link XlsIterateTables}を処理する。
 * 
 * @version 1.3
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class IterateTablesProcessor extends AbstractFieldProcessor<XlsIterateTables> {

    @Override
    public void loadProcess(final Sheet sheet, final Object obj, final XlsIterateTables anno,
            final FieldAccessor accessor, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final Class<?> clazz = accessor.getType();
        
        // create multi-table objects.
        if(List.class.isAssignableFrom(clazz)) {
            
            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }
            
            List<?> value = loadTables(sheet, anno, accessor, tableClass, config, work);
            if(value != null) {
                accessor.setValue(obj, value);
            }
            
        } else if(clazz.isArray()) {
            
            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }
            
            final List<?> value = loadTables(sheet, anno, accessor, tableClass, config, work);
            if(value != null) {
                
                final Object array = Array.newInstance(tableClass, value.size());
                for(int i=0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }
                
                accessor.setValue(obj, array);
            }
            
        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSpportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsIterateTables.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }
        
    }
    
    protected List<?> loadTables(final Sheet sheet, final XlsIterateTables iterateTablesAnno, final FieldAccessor accessor,
            final Class<?> tableClass, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final List<Object> resultTableList = new ArrayList<>();
        
        Cell after = null;
        Cell currentCell = null;
        
        final String label = iterateTablesAnno.tableLabel();
        currentCell = Utils.getCell(sheet, label, after, false, !iterateTablesAnno.optional(), config);
        
        while(currentCell != null) {
            // 1 table object instance
            final Object tableObj = config.createBean(tableClass);
            
            // パスの位置の変更
            work.getErrors().pushNestedPath(accessor.getName(), resultTableList.size());
            
            // execute PreProcess listener
            final XlsListener listenerAnno = work.getAnnoReader().getAnnotation(tableObj.getClass(), XlsListener.class);
            if(listenerAnno != null) {
                Object listenerObj = config.createBean(listenerAnno.listenerClass());
                for(Method method : listenerObj.getClass().getMethods()) {
                    final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPreLoad.class);
                    if(preProcessAnno != null) {
                        Utils.invokeNeedProcessMethod(listenerObj, method, tableObj, sheet, config, work.getErrors());
                    }
                }
            }
            
            // execute PreProcess method
            for(Method method : tableObj.getClass().getMethods()) {
                final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPreLoad.class);
                if(preProcessAnno != null) {
                    Utils.invokeNeedProcessMethod(tableObj, method, tableObj, sheet, config, work.getErrors());                    
                }
            }
            
            // process single label.
            loadSingleLabelledCell(sheet, tableObj, currentCell, config, work);
            
            // process horizontal table.
            loadMultipleHorizontalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, work);
            
            // process vertial table
            //TODO: verticalの場合は、走査方向が違うのでできないので、例外をスローすべき。
//            loadMultipleVerticalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, setting);
            
            resultTableList.add(tableObj);
            after = currentCell;
            currentCell = Utils.getCell(sheet, label, after, false, false, config);
            
            // set PostProcess listener
            if(listenerAnno != null) {
                Object listenerObj = config.createBean(listenerAnno.listenerClass());
                for(Method method : listenerObj.getClass().getMethods()) {
                    final XlsPostLoad postProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPostLoad.class);
                    if(postProcessAnno != null) {
                        work.addNeedPostProcess(new NeedProcess(tableObj, listenerObj, method));
                    }
                }
            }
            
            // set PostProcess method
            for(Method method : tableObj.getClass().getMethods()) {
                final XlsPostLoad postProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPostLoad.class);
                if(postProcessAnno != null) {
                    work.addNeedPostProcess(new NeedProcess(tableObj, tableObj, method));
                }
            }
            
            // パスの位置の変更
            work.getErrors().popNestedPath();
        }
        
        return resultTableList;
        
    }
    
    /**
     * XlsLabelledCellによる繰り返しを処理する。
     * @param sheet
     * @param tableObj
     * @param headerCell
     * @param config
     * @throws XlsMapperException
     */
    protected void loadSingleLabelledCell(final Sheet sheet, final Object tableObj, 
            final Cell headerCell, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final LabelledCellProcessor labelledCellProcessor = 
                (LabelledCellProcessor) config.getFieldProcessorRegistry().getLoadingProcessor(XlsLabelledCell.class);
        
        final List<FieldAccessor> properties = FieldAdapterUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledCell.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());
        
        for(FieldAccessor property : properties) {
            final XlsLabelledCell ann = property.getAnnotation(XlsLabelledCell.class).get();
            
            Cell titleCell = null;
            try {
                titleCell = Utils.getCell(sheet, ann.label(), headerCell, config);
            } catch (CellNotFoundException e) {
                if (ann.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }
            
            final XlsLabelledCell labelledCell = new XlsLabelledCellForIterateTable(
                    ann, titleCell.getRowIndex(), titleCell.getColumnIndex(),
                    Utils.formatCellAddress(titleCell.getRowIndex(), titleCell.getColumnIndex()));
            
            labelledCellProcessor.loadProcess(sheet, tableObj, labelledCell, property, config, work);
        }
    }
    
    protected void loadMultipleHorizontalTableCell(final Sheet sheet, final Object tableObj, 
            final Cell headerCell, final XlsIterateTables iterateTablesAnno,
            final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        int headerColumn = headerCell.getColumnIndex();
        int headerRow = headerCell.getRowIndex();
        
        if (iterateTablesAnno.bottom() > 0) {
            // if positive value set to bottom(), row index of table header move
            headerRow += iterateTablesAnno.bottom();
        }
        
        final HorizontalRecordsProcessor processor = 
                (HorizontalRecordsProcessor) config.getFieldProcessorRegistry().getLoadingProcessor(XlsHorizontalRecords.class);
        
        final List<FieldAccessor> properties = FieldAdapterUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsHorizontalRecords.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());
        
        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        for(FieldAccessor property : properties) {
            final XlsHorizontalRecords anno = property.getAnnotation(XlsHorizontalRecords.class).get();
            
            if(iterateTablesAnno.tableLabel().equals(anno.tableLabel())) {
                
                final XlsHorizontalRecords recordsAnno = new XlsHorizontalRecordsForIterateTables(anno, headerColumn, headerRow);
                accessorProxies.add(new FieldAccessorProxy(recordsAnno, processor, property));
            }
        }
        
        // 順番を並び替えて保存処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy adaptorProxy : accessorProxies) {
            adaptorProxy.loadProcess(sheet, tableObj, config, work);
        }
        
    }
    
//    protected void loadMultipleVerticalTableCell(final Sheet sheet, final Object tableObj, 
//            final Cell headerCell, final XlsIterateTables iterateTables,
//            final XlsMapperConfig config, LoadingProcessSetting setting) throws XlsMapperException {
//        
//        int headerColumn = headerCell.getColumnIndex();
//        int headerRow = headerCell.getRowIndex();
//        
//        if (iterateTables.bottom() > 0) {
//            // if positive value set to bottom(), column index of table header move
//            headerColumn += iterateTables.bottom();
//        }
//        
//        final VerticalRecordsProcessor processor = new VerticalRecordsProcessor();
//        final List<FieldAdaptor> properties = Utils.getLoadingPropertiesWithAnnotation(
//                tableObj.getClass(), setting.getAnnoReader(), XlsVerticalRecords.class);
//        
//        for(FieldAdaptor property : properties) {
//            final XlsVerticalRecords ann = property.getLoadingAnnotation(XlsVerticalRecords.class);
//            
//            if(iterateTables.tableLabel().equals(ann.tableLabel())) {
//                
//                final XlsVerticalRecords records = new XlsVerticalRecordsForIterateTables(ann, headerColumn, headerRow);
//                
//                // vertical record-mapping
//                processor.loadProcess(sheet, tableObj, records, property, config, setting);
//            }
//        }
//        
//    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object obj, final XlsIterateTables anno, final FieldAccessor accessor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final Object result = accessor.getValue(obj);
        final Class<?> clazz = accessor.getType();
        
        // create multi-table objects.
        if(List.class.isAssignableFrom(clazz)) {
            
            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }
            
            final List<Object> list = (result == null ? new ArrayList<Object>() : (List<Object>) result);
            saveTables(sheet, anno, accessor, tableClass, list, config, work);
            
        } else if(clazz.isArray()) {
            
            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }
            
            final List<Object> list = (result == null ? new ArrayList<Object>() : Arrays.asList((Object[]) result));
            saveTables(sheet, anno, accessor, tableClass, list, config, work);
            
        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSpportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsIterateTables.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
                    }
    }
    
    protected void saveTables(final Sheet sheet, final XlsIterateTables iterateTablesAnno, final FieldAccessor accessor,
            final Class<?> tableClass, final List<Object> resultTableList,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        Cell after = null;
        Cell currentCell = null;
        String label = iterateTablesAnno.tableLabel();
        
        for(int i=0; i < resultTableList.size(); i++) {
            
            final Object tableObj = resultTableList.get(i);
            
            // パスの位置の変更
            work.getErrors().pushNestedPath(accessor.getName(), i);
            
            // execute PreProcess listener
            final XlsListener listenerAnno = work.getAnnoReader().getAnnotation(tableObj.getClass(), XlsListener.class);
            if(listenerAnno != null) {
                Object listenerObj = config.createBean(listenerAnno.listenerClass());
                for(Method method : listenerObj.getClass().getMethods()) {
                    final XlsPreSave preProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPreSave.class);
                    if(preProcessAnno != null) {
                        Utils.invokeNeedProcessMethod(listenerObj, method, tableObj, sheet, config, work.getErrors());
                    }
                }
            }
            
            // execute PreProcess method
            for(Method method : tableObj.getClass().getMethods()) {
                final XlsPreSave preProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPreSave.class);
                if(preProcessAnno != null) {
                    Utils.invokeNeedProcessMethod(tableObj, method, tableObj, sheet, config, work.getErrors());                    
                }
            }
            
            currentCell = Utils.getCell(sheet, label, after, false, !iterateTablesAnno.optional(), config);
            if(currentCell == null) {
                //TODO: 見出しが足りない場合の追加処理を記述する
                
                // パスの位置の変更
                work.getErrors().popNestedPath();
                break;
            }
            
            // process single label.
            saveSingleLabelledCell(sheet, tableObj, currentCell, config, work);
            
            // process horizontal table.
            saveMultipleHorizontalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, work);
            
            // TODO process vertical table
//            saveMultipleVerticalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, setting);
            
            after = currentCell;
            
            // set PostProcess listener
            if(listenerAnno != null) {
                Object listenerObj = config.createBean(listenerAnno.listenerClass());
                for(Method method : listenerObj.getClass().getMethods()) {
                    final XlsPostSave postProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPostSave.class);
                    if(postProcessAnno != null) {
                        work.addNeedPostProcess(new NeedProcess(tableObj, listenerObj, method));
                    }
                }
            }
            
            // set PreProcess method
            for(Method method : tableObj.getClass().getMethods()) {
                final XlsPostSave postProcessAnno = work.getAnnoReader().getAnnotation(method, XlsPostSave.class);
                if(postProcessAnno != null) {
                    work.addNeedPostProcess(new NeedProcess(tableObj, tableObj, method));
                }
            }
            
            // パスの位置の変更
            work.getErrors().popNestedPath();
        }
        
        
    }
    
    protected void saveSingleLabelledCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final LabelledCellProcessor labelledCellProcessor = 
                (LabelledCellProcessor) config.getFieldProcessorRegistry().getSavingProcessor(XlsLabelledCell.class);
        
        final List<FieldAccessor> properties = FieldAdapterUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledCell.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());
        
        for(FieldAccessor property : properties) {
            
            final XlsLabelledCell anno = property.getAnnotation(XlsLabelledCell.class).get();
            
            Cell titleCell = null;
            try {
                titleCell = Utils.getCell(sheet, anno.label(), headerCell, config);
            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }
            
            final XlsLabelledCell labelledCell = new XlsLabelledCellForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex(),
                    Utils.formatCellAddress(titleCell.getRowIndex(), titleCell.getColumnIndex()));
            
            labelledCellProcessor.saveProcess(sheet, tableObj, labelledCell, property, config, work);
        }
        
    }
    
    protected void saveMultipleHorizontalTableCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final XlsIterateTables iterateTables, final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final int headerColumn = headerCell.getColumnIndex();
        int headerRow = headerCell.getRowIndex();
        
        if (iterateTables.bottom() > 0) {
            // if positive value set to bottom(), row index of table header move
            headerRow += iterateTables.bottom();
        }
        
        final HorizontalRecordsProcessor processor = (HorizontalRecordsProcessor) config.getFieldProcessorRegistry().getSavingProcessor(XlsHorizontalRecords.class);
        
        final List<FieldAccessor> properties = FieldAdapterUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsHorizontalRecords.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());
        
        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        for(FieldAccessor property : properties) {
            
            final XlsHorizontalRecords anno = property.getAnnotation(XlsHorizontalRecords.class).get();
            
            // 処理対象と同じテーブルラベルのとき、マッピングを実行する。
            if(iterateTables.tableLabel().equals(anno.tableLabel())) {
                final XlsHorizontalRecords recordsAnno = new XlsHorizontalRecordsForIterateTables(anno, headerColumn, headerRow);
                accessorProxies.add(new FieldAccessorProxy(recordsAnno, processor, property));
                
            }
            
        }
        
        // 順番を並び替えて保存処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy adaptorProxy : accessorProxies) {
            adaptorProxy.saveProcess(sheet, tableObj, config, work);
        }
        
    }
    
//    protected void saveMultipleVerticalTableCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
//            final XlsIterateTables iterateTables, final XlsMapperConfig config, final SavingProcessSetting setting) throws XlsMapperException {
//        
//        int headerColumn = headerCell.getColumnIndex();
//        final int headerRow = headerCell.getRowIndex();
//        
//        if (iterateTables.bottom() > 0) {
//            // if positive value set to bottom(), row index of table header move
//            headerColumn += iterateTables.bottom();
//        }
//        
//        final VerticalRecordsProcessor processor = new VerticalRecordsProcessor();
//        final List<FieldAdaptor> properties = Utils.getSavingPropertiesWithAnnotation(
//                tableObj.getClass(), setting.getAnnoReader(), XlsVerticalRecords.class);
//        
//        for(FieldAdaptor property : properties) {
//            
//            final XlsVerticalRecords anno = property.getSavingAnnotation(XlsVerticalRecords.class);
//            
//            if (iterateTables.tableLabel().equals(anno.tableLabel())) {
//                // 処理対象と同じテーブルラベルのとき、マッピングを実行する。
//                final XlsVerticalRecords recordsAnno = new XlsVerticalRecordsForIterateTables(anno, headerColumn, headerRow);
//                processor.saveProcess(sheet, tableObj, recordsAnno, property, config, setting);
//            }
//            
//        }
//        
//    }
    
}
