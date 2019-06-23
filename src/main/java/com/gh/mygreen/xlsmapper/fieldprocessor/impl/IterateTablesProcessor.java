package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.NeedProcess;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecordsForIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsIterateTables;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCells;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledArrayCellsForIterateTable;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCellForIterateTable;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledComment;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCommentForIterateTable;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecordsForIterateTables;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxy;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessorProxyComparator;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordMethodCache;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordMethodFacatory;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.CellFinder;
import com.gh.mygreen.xlsmapper.util.FieldAccessorUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * アノテーション{@link XlsIterateTables}を処理する。
 *
 * @version 2.1
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class IterateTablesProcessor extends AbstractFieldProcessor<XlsIterateTables> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsIterateTables anno,
            final FieldAccessor accessor, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }

        final Class<?> clazz = accessor.getType();

        if(Collection.class.isAssignableFrom(clazz)) {

            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }

            List<?> value = loadTables(sheet, beansObj, anno, accessor, tableClass, config, work);
            if(value != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Collection<?> collection = Utils.convertListToCollection(value, (Class<Collection>)clazz, config.getBeanFactory());
                accessor.setValue(beansObj, collection);
            }

        } else if(clazz.isArray()) {

            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }

            final List<?> value = loadTables(sheet, beansObj, anno, accessor, tableClass, config, work);
            if(value != null) {

                final Object array = Array.newInstance(tableClass, value.size());
                for(int i=0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }

                accessor.setValue(beansObj, array);
            }

        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsIterateTables.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }

    }

    private List<?> loadTables(final Sheet sheet, final Object beansObj, final XlsIterateTables iterateTablesAnno, final FieldAccessor accessor,
            final Class<?> tableClass, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        // アノテーションの整合性のチェック
        checkRecordAnnotation(tableClass, work.getAnnoReader());

        final List<Object> resultTableList = new ArrayList<>();

        Cell after = null;
        Cell currentCell = null;

        // 各種レコードのコールバック用メソッドを抽出する
        final RecordMethodCache methodCache = new RecordMethodFacatory(work.getAnnoReader(), config)
                .create(tableClass, ProcessCase.Load);

        final String label = iterateTablesAnno.tableLabel();

        currentCell = CellFinder.query(sheet, label, config).find(iterateTablesAnno.optional());

        while(currentCell != null) {
            // 1 table object instance
            final Object tableObj = config.createBean(tableClass);

            // ラベルの設定
            accessor.setArrayLabel(beansObj, POIUtils.getCellContents(currentCell, config.getCellFormatter()), resultTableList.size());

            // パスの位置の変更
            work.getErrors().pushNestedPath(accessor.getName(), resultTableList.size());


            // execute PreProcess listener
            methodCache.getListenerClasses().forEach(listenerClass -> {
                listenerClass.getPreLoadMethods().forEach(method -> {
                    Utils.invokeNeedProcessMethod(listenerClass.getObject(), method, tableObj, sheet, config, work.getErrors(), ProcessCase.Load);
                });
            });

            // execute PreProcess method
            methodCache.getPreLoadMethods().forEach(method -> {
                Utils.invokeNeedProcessMethod(tableObj, method, tableObj, sheet, config, work.getErrors(), ProcessCase.Load);
            });

            // process sinslbe labelled comment
            loadSingleLabelledComment(sheet, tableObj, currentCell, config, work);
            
            // process single label.
            loadSingleLabelledCell(sheet, tableObj, currentCell, config, work);

            // process array labels.
            loadSingleLabelledArrayCell(sheet, tableObj, currentCell, config, work);

            // process horizontal table.
            loadMultipleHorizontalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, work);

            // process vertial table
            loadMultipleVerticalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, work);

            resultTableList.add(tableObj);
            after = currentCell;
            currentCell = CellFinder.query(sheet, label, config)
                    .startPosition(after)
                    .excludeStartPosition(true)
                    .findOptional()
                    .orElse(null);

            // set PostProcess listener
            methodCache.getListenerClasses().forEach(listenerClass -> {
                listenerClass.getPostLoadMethods().forEach(method -> {
                    work.addNeedPostProcess(new NeedProcess(tableObj, listenerClass.getObject(), method));
                });
            });

            // set PostProcess method
            methodCache.getPostLoadMethods().forEach(method -> {
                work.addNeedPostProcess(new NeedProcess(tableObj, tableObj, method));
            });

            // パスの位置の変更
            work.getErrors().popNestedPath();
        }

        return resultTableList;

    }

    /**
     * レコード用のアノテーションの整合性のチェックを行う。
     * <p>{@link XlsHorizontalRecords}と{@link XlsVerticalRecords}は、どちらか一方のみ指定可能。</p>
     * @param tableClass テーブル用のクラス情報
     * @param annoReader アノテーションの提供クラス
     */
    private void checkRecordAnnotation(final Class<?> tableClass, final AnnotationReader annoReader) {

        final int horizontalSize = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableClass, annoReader, XlsHorizontalRecords.class)
                .size();

        final int verticalSize = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableClass, annoReader, XlsVerticalRecords.class)
                .size();

        if(horizontalSize > 0 && verticalSize > 0) {
            throw new AnnotationInvalidException(MessageBuilder.create("anno.XlsIterateTables.horizontalAndVertical")
                    .varWithClass("tableClass", tableClass)
                    .format());
        }

    }
    
    /**
     * XlsLabelledCommentによる処理する。
     * @param sheet
     * @param tableObj
     * @param headerCell
     * @param config
     * @throws XlsMapperException
     */
    private void loadSingleLabelledComment(final Sheet sheet, final Object tableObj,
            final Cell headerCell, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        final LabelledCommentProcessor labelledCommentProcessor =
                (LabelledCommentProcessor) config.getFieldProcessorRegistry().getProcessor(XlsLabelledComment.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledComment.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {
            final XlsLabelledComment anno = property.getAnnotationNullable(XlsLabelledComment.class);

            Cell titleCell = null;
            try {
                titleCell = CellFinder.query(sheet, anno.label(), config)
                        .startPosition(headerCell)
                        .excludeStartPosition(true)
                        .findWhenNotFoundException();

            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }

            final XlsLabelledComment labelledCell = new XlsLabelledCommentForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex());

            labelledCommentProcessor.loadProcess(sheet, tableObj, labelledCell, property, config, work);
        }
    }

    /**
     * XlsLabelledCellによる処理する。
     * @param sheet
     * @param tableObj
     * @param headerCell
     * @param config
     * @throws XlsMapperException
     */
    private void loadSingleLabelledCell(final Sheet sheet, final Object tableObj,
            final Cell headerCell, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        final LabelledCellProcessor labelledCellProcessor =
                (LabelledCellProcessor) config.getFieldProcessorRegistry().getProcessor(XlsLabelledCell.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledCell.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {
            final XlsLabelledCell anno = property.getAnnotationNullable(XlsLabelledCell.class);

            Cell titleCell = null;
            try {
                titleCell = CellFinder.query(sheet, anno.label(), config)
                        .startPosition(headerCell)
                        .excludeStartPosition(true)
                        .findWhenNotFoundException();

            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }

            final XlsLabelledCell labelledCell = new XlsLabelledCellForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex());

            labelledCellProcessor.loadProcess(sheet, tableObj, labelledCell, property, config, work);
        }
    }

    /**
     * XlsLabelledArrayCellを処理する。
     * @param sheet
     * @param tableObj
     * @param headerCell
     * @param config
     * @throws XlsMapperException
     */
    private void loadSingleLabelledArrayCell(final Sheet sheet, final Object tableObj,
            final Cell headerCell, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        final LabelledArrayCellsProcessor labelledArrayCellProcessor =
                (LabelledArrayCellsProcessor) config.getFieldProcessorRegistry().getProcessor(XlsLabelledArrayCells.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledArrayCells.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {
            final XlsLabelledArrayCells anno = property.getAnnotationNullable(XlsLabelledArrayCells.class);

            Cell titleCell = null;
            try {
                titleCell = CellFinder.query(sheet, anno.label(), config)
                        .startPosition(headerCell)
                        .excludeStartPosition(true)
                        .findWhenNotFoundException();

            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }

            final XlsLabelledArrayCells wrapAnno = new XlsLabelledArrayCellsForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex());

            labelledArrayCellProcessor.loadProcess(sheet, tableObj, wrapAnno, property, config, work);
        }
    }

    private void loadMultipleHorizontalTableCell(final Sheet sheet, final Object tableObj,
            final Cell headerCell, final XlsIterateTables iterateTablesAnno,
            final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        int headerColumn = headerCell.getColumnIndex();
        int headerRow = headerCell.getRowIndex();

        if (iterateTablesAnno.bottom() > 0) {
            // if positive value set to bottom(), row index of table header move
            headerRow += iterateTablesAnno.bottom();
        }

        final HorizontalRecordsProcessor processor =
                (HorizontalRecordsProcessor) config.getFieldProcessorRegistry().getProcessor(XlsHorizontalRecords.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsHorizontalRecords.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());

        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        for(FieldAccessor property : properties) {
            final XlsHorizontalRecords anno = property.getAnnotationNullable(XlsHorizontalRecords.class);

            if(iterateTablesAnno.tableLabel().equals(anno.tableLabel())) {

                final XlsHorizontalRecords recordsAnno = new XlsHorizontalRecordsForIterateTables(anno, headerColumn, headerRow);
                accessorProxies.add(new FieldAccessorProxy(recordsAnno, processor, property));
            }
        }

        // 順番を並び替えて読み込み処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy adaptorProxy : accessorProxies) {
            adaptorProxy.loadProcess(sheet, tableObj, config, work);
        }

    }

    private void loadMultipleVerticalTableCell(final Sheet sheet, final Object tableObj,
            final Cell headerCell, final XlsIterateTables iterateTablesAnno,
            final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        int headerColumn = headerCell.getColumnIndex();
        int headerRow = headerCell.getRowIndex();

        if (iterateTablesAnno.bottom() > 0) {
            // if positive value set to bottom(), row index of table header move
            headerRow += iterateTablesAnno.bottom();
        }

        final VerticalRecordsProcessor processor =
                (VerticalRecordsProcessor) config.getFieldProcessorRegistry().getProcessor(XlsVerticalRecords.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsVerticalRecords.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());

        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        for(FieldAccessor property : properties) {
            final XlsVerticalRecords anno = property.getAnnotationNullable(XlsVerticalRecords.class);

            if(iterateTablesAnno.tableLabel().equals(anno.tableLabel())) {

                final XlsVerticalRecords recordsAnno = new XlsVerticalRecordsForIterateTables(anno, headerColumn, headerRow);
                accessorProxies.add(new FieldAccessorProxy(recordsAnno, processor, property));
            }
        }

        // 順番を並び替えて読み込み処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy adaptorProxy : accessorProxies) {
            adaptorProxy.loadProcess(sheet, tableObj, config, work);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsIterateTables anno, final FieldAccessor accessor,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }

        final Object result = accessor.getValue(beansObj);
        final Class<?> clazz = accessor.getType();

        if(Collection.class.isAssignableFrom(clazz)) {

            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }

            final Collection<Object> value = (result == null ? new ArrayList<Object>() : (Collection<Object>) result);
            final List<Object> list = Utils.convertCollectionToList(value);
            saveTables(sheet, anno, accessor, tableClass, list, config, work);

        } else if(clazz.isArray()) {

            Class<?> tableClass = anno.tableClass();
            if(tableClass == Object.class) {
                tableClass = accessor.getComponentType();
            }

            final List<Object> list = Utils.asList(result, tableClass);
            saveTables(sheet, anno, accessor, tableClass, list, config, work);

        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsIterateTables.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }
    }

    private void saveTables(final Sheet sheet, final XlsIterateTables iterateTablesAnno, final FieldAccessor accessor,
            final Class<?> tableClass, final List<Object> resultTableList,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        // アノテーションの整合性のチェック
        checkRecordAnnotation(tableClass, work.getAnnoReader());

        Cell after = null;
        Cell currentCell = null;
        String label = iterateTablesAnno.tableLabel();

        // 各種レコードのコールバック用メソッドを抽出する
        final RecordMethodCache methodCache = new RecordMethodFacatory(work.getAnnoReader(), config)
                .create(tableClass, ProcessCase.Save);

        for(int i=0; i < resultTableList.size(); i++) {

            final Object tableObj = resultTableList.get(i);

            // パスの位置の変更
            work.getErrors().pushNestedPath(accessor.getName(), i);

            // execute PreProcess listener
            methodCache.getListenerClasses().forEach(listenerClass -> {
                listenerClass.getPreSaveMethods().forEach(method -> {
                    Utils.invokeNeedProcessMethod(listenerClass.getObject(), method, tableObj, sheet, config, work.getErrors(), ProcessCase.Save);
                });
            });

            // execute PreProcess method
            methodCache.getPreSaveMethods().forEach(method -> {
                Utils.invokeNeedProcessMethod(tableObj, method, tableObj, sheet, config, work.getErrors(), ProcessCase.Save);
            });

            if(after == null) {
                currentCell = CellFinder.query(sheet, label, config).find(iterateTablesAnno.optional());
            } else {
                currentCell = CellFinder.query(sheet, label, config)
                        .startPosition(after)
                        .excludeStartPosition(true)
                        .find(iterateTablesAnno.optional());
            }
            if(currentCell == null) {
                //TODO: 見出しが足りない場合の追加処理を記述する

                // パスの位置の変更
                work.getErrors().popNestedPath();
                break;
            }
            
            // process single label comment
            saveSingleLabelledComment(sheet, tableObj, currentCell, config, work);

            // process single label.
            saveSingleLabelledCell(sheet, tableObj, currentCell, config, work);

            // process array labels.
            saveSingleLabelledArrayCell(sheet, tableObj, currentCell, config, work);

            // process horizontal table.
            saveMultipleHorizontalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, work);

            // process vertical table
            saveMultipleVerticalTableCell(sheet, tableObj, currentCell, iterateTablesAnno, config, work);

            after = currentCell;

            // set PostProcess listener
            methodCache.getListenerClasses().forEach(listenerClass -> {
                listenerClass.getPostSaveMethods().forEach(method -> {
                    work.addNeedPostProcess(new NeedProcess(tableObj, listenerClass.getObject(), method));
                });
            });

            // set PostProcess method
            methodCache.getPostSaveMethods().forEach(method -> {
                work.addNeedPostProcess(new NeedProcess(tableObj, tableObj, method));
            });

            // パスの位置の変更
            work.getErrors().popNestedPath();
        }


    }
    
    private void saveSingleLabelledComment(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        final LabelledCommentProcessor labelledCommentProcessor =
                (LabelledCommentProcessor) config.getFieldProcessorRegistry().getProcessor(XlsLabelledComment.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledComment.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {

            final XlsLabelledComment anno = property.getAnnotationNullable(XlsLabelledComment.class);

            Cell titleCell = null;
            try {
                titleCell = CellFinder.query(sheet, anno.label(), config)
                        .startPosition(headerCell)
                        .excludeStartPosition(true)
                        .findWhenNotFoundException();

            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }

            final XlsLabelledComment labelledCell = new XlsLabelledCommentForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex());

            labelledCommentProcessor.saveProcess(sheet, tableObj, labelledCell, property, config, work);
        }

    }

    private void saveSingleLabelledCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        final LabelledCellProcessor labelledCellProcessor =
                (LabelledCellProcessor) config.getFieldProcessorRegistry().getProcessor(XlsLabelledCell.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledCell.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {

            final XlsLabelledCell anno = property.getAnnotationNullable(XlsLabelledCell.class);

            Cell titleCell = null;
            try {
                titleCell = CellFinder.query(sheet, anno.label(), config)
                        .startPosition(headerCell)
                        .excludeStartPosition(true)
                        .findWhenNotFoundException();

            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }

            final XlsLabelledCell labelledCell = new XlsLabelledCellForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex());

            labelledCellProcessor.saveProcess(sheet, tableObj, labelledCell, property, config, work);
        }

    }

    private void saveSingleLabelledArrayCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        final LabelledArrayCellsProcessor labelledArrayCellProcessor =
                (LabelledArrayCellsProcessor) config.getFieldProcessorRegistry().getProcessor(XlsLabelledArrayCells.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsLabelledArrayCells.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {

            final XlsLabelledArrayCells anno = property.getAnnotationNullable(XlsLabelledArrayCells.class);

            Cell titleCell = null;
            try {
                titleCell = CellFinder.query(sheet, anno.label(), config)
                        .startPosition(headerCell)
                        .excludeStartPosition(true)
                        .findWhenNotFoundException();

            } catch (CellNotFoundException e) {
                if (anno.optional()) {
                    continue;
                } else {
                    throw e;
                }
            }

            final XlsLabelledArrayCells labelledCell = new XlsLabelledArrayCellsForIterateTable(
                    anno, titleCell.getRowIndex(), titleCell.getColumnIndex());

            labelledArrayCellProcessor.saveProcess(sheet, tableObj, labelledCell, property, config, work);
        }

    }

    private void saveMultipleHorizontalTableCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final XlsIterateTables iterateTables, final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        final int headerColumn = headerCell.getColumnIndex();
        int headerRow = headerCell.getRowIndex();

        if (iterateTables.bottom() > 0) {
            // if positive value set to bottom(), row index of table header move
            headerRow += iterateTables.bottom();
        }

        final HorizontalRecordsProcessor processor =
                (HorizontalRecordsProcessor) config.getFieldProcessorRegistry().getProcessor(XlsHorizontalRecords.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsHorizontalRecords.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());

        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        for(FieldAccessor property : properties) {

            final XlsHorizontalRecords anno = property.getAnnotationNullable(XlsHorizontalRecords.class);

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

    private void saveMultipleVerticalTableCell(final Sheet sheet, final Object tableObj, final Cell headerCell,
            final XlsIterateTables iterateTablesAnno, final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        int headerColumn = headerCell.getColumnIndex();
        int headerRow = headerCell.getRowIndex();

        if (iterateTablesAnno.bottom() > 0) {
            // if positive value set to bottom(), row index of table header move
            headerRow += iterateTablesAnno.bottom();
        }

        final VerticalRecordsProcessor processor =
                (VerticalRecordsProcessor) config.getFieldProcessorRegistry().getProcessor(XlsVerticalRecords.class);

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                tableObj.getClass(), work.getAnnoReader(), XlsVerticalRecords.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());

        final List<FieldAccessorProxy> accessorProxies = new ArrayList<>();
        for(FieldAccessor property : properties) {

            final XlsVerticalRecords anno = property.getAnnotationNullable(XlsVerticalRecords.class);

            // 処理対象と同じテーブルラベルのとき、マッピングを実行する。
            if(iterateTablesAnno.tableLabel().equals(anno.tableLabel())) {
                final XlsVerticalRecords recordsAnno = new XlsVerticalRecordsForIterateTables(anno, headerColumn, headerRow);
                accessorProxies.add(new FieldAccessorProxy(recordsAnno, processor, property));

            }

        }

        // 順番を並び替えて保存処理を実行する
        Collections.sort(accessorProxies, new FieldAccessorProxyComparator());
        for(FieldAccessorProxy adaptorProxy : accessorProxies) {
            adaptorProxy.saveProcess(sheet, tableObj, config, work);
        }

    }

}
