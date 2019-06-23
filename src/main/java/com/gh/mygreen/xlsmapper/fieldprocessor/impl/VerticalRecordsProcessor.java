package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.NeedProcess;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.ArrayDirection;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsIgnorable;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordFinder;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.OverOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption.RemainedOperation;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.MergedRecord;
import com.gh.mygreen.xlsmapper.fieldprocessor.NestedRecordMergedSizeException;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordFinder;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordHeader;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordMethodCache;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordMethodFacatory;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordsProcessorUtil;
import com.gh.mygreen.xlsmapper.localization.MessageBuilder;
import com.gh.mygreen.xlsmapper.util.CellFinder;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.FieldAccessorUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.FieldFormatter;
import com.gh.mygreen.xlsmapper.xml.AnnotationReadException;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * アノテーション{@link XlsVerticalRecords}を処理するクラス。
 *
 * @version 2.1
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class VerticalRecordsProcessor extends AbstractFieldProcessor<XlsVerticalRecords>{

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsVerticalRecords anno,
            final FieldAccessor accessor, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }

        final Class<?> clazz = accessor.getType();
        if(Collection.class.isAssignableFrom(clazz)) {

            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = accessor.getComponentType();
            }

            final List<?> value = loadRecords(sheet, beansObj, anno, accessor, recordClass, config, work);
            if(value != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Collection<?> collection = Utils.convertListToCollection(value, (Class<Collection>)clazz, config.getBeanFactory());
                accessor.setValue(beansObj, collection);
            }
        } else if(clazz.isArray()) {

            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = accessor.getComponentType();
            }

            final List<?> value = loadRecords(sheet, beansObj, anno, accessor, recordClass, config, work);
            if(value != null) {
                final Object array = Array.newInstance(recordClass, value.size());
                for(int i=0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }

                accessor.setValue(beansObj, array);
            }

        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsVerticalRecords.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }

    }

   private List<?> loadRecords(final Sheet sheet, final Object beansObj, final XlsVerticalRecords anno, final FieldAccessor accessor,
           final Class<?> recordClass, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        // get table starting position
        final Optional<CellPosition> initPosition = getHeaderPosition(sheet, anno, accessor, config);
        if(!initPosition.isPresent()) {
            return null;
        }

        // ラベルの設定
        if(Utils.isNotEmpty(anno.tableLabel())) {
            final Optional<Cell> tableLabelCell = CellFinder.query(sheet, anno.tableLabel(), config).findOptional();
            tableLabelCell.ifPresent(c -> {
                final String label = POIUtils.getCellContents(c, config.getCellFormatter());
                accessor.setLabel(beansObj, label);

            });
        }

        final int initColumn = initPosition.get().getColumn();
        final int initRow = initPosition.get().getRow();

        int hColumn = initColumn;
        int hRow = initRow;

        // get header columns.
        final List<RecordHeader> headers = new ArrayList<>();
        int rangeCount = 1;
        while(true){
            try {
                Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                while(POIUtils.isEmptyCellContents(cell, config.getCellFormatter()) && rangeCount < anno.range()){
                    cell = POIUtils.getCell(sheet, hColumn, hRow + rangeCount);
                    rangeCount++;
                }

                String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(Utils.isEmpty(cellValue)){
                    break;
                } /*else {
                    for(int j=hColumn; j > initColumn; j--){
                        final Cell tmpCell = POIUtils.getCell(sheet, j, hRow);
                        if(!POIUtils.isEmptyCellContents(tmpCell, config.getCellFormatter())){
                            cell = tmpCell;
                            break;
                        }
                    }
                }*/

                headers.add(new RecordHeader(cellValue, cell.getRowIndex() - initRow));
                hRow = hRow + rangeCount;
                rangeCount = 1;

                // 結合しているセルの場合は、はじめのセルだけ取得して、後は結合分スキップする。
                CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                if(mergedRange != null) {
                    hRow = hRow + (mergedRange.getLastRow() - mergedRange.getFirstRow());
                }

            } catch(ArrayIndexOutOfBoundsException ex){
                break;
            }

            if(anno.headerLimit() > 0 && headers.size() >= anno.headerLimit()){
                break;
            }
        }

        // データ行の開始位置の調整
        hColumn += anno.headerRight();
        CellPosition startPosition = CellPosition.of(initRow, hColumn);

        // 独自の開始位置を指定する場合
        final Optional<XlsRecordFinder> finderAnno = accessor.getAnnotation(XlsRecordFinder.class);
        if(finderAnno.isPresent()) {
            final RecordFinder finder = config.createBean(finderAnno.get().value());
            startPosition = finder.find(ProcessCase.Load, finderAnno.get().args(), sheet, startPosition, beansObj, config);

        }

        return loadRecords(sheet, headers, anno, startPosition, 0, accessor, recordClass, config, work);
   }

   private List<?> loadRecords(final Sheet sheet, final List<RecordHeader> headers,
           final XlsVerticalRecords anno,
           final CellPosition initPosition, final int parentMergedSize,
           final FieldAccessor accessor, final Class<?> recordClass,
           final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        final List<Object> result = new ArrayList<>();

        final int initColumn = initPosition.getColumn();
        final int initRow = initPosition.getRow();

        final int maxColumn = initColumn + parentMergedSize;
        int hColumn = initColumn;

        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader(), config);
        RecordsProcessorUtil.checkMapColumns(sheet, recordClass, headers, work.getAnnoReader(), config);
        RecordsProcessorUtil.checkArrayColumns(sheet, recordClass, headers, work.getAnnoReader(), config);

        RecordTerminal terminal = anno.terminal();
        if(terminal == null){
            terminal = RecordTerminal.Empty;
        }

        // 各種レコードのコールバック用メソッドを抽出する
        final RecordMethodCache methodCache = new RecordMethodFacatory(work.getAnnoReader(), config)
                .create(recordClass, ProcessCase.Load);

        // レコードの見出しに対するカラム情報のキャッシュ
        final Map<String, List<FieldAccessor>> propertiesCache = new HashMap<>();

        // カラムに対するConverterのキャッシュ
        final Map<String, CellConverter<?>> converterCache = new HashMap<>();

        final int startHeaderIndex = getStartHeaderIndexForLoading(headers, recordClass, work.getAnnoReader(), config);

        // get records
        while(hColumn < POIUtils.getColumns(sheet)){

            if(parentMergedSize > 0 && hColumn >= maxColumn) {
                // ネストしている処理のとき、最大の処理レコード数をチェックする。
                break;
            }

            boolean emptyFlag = true;
            // recordは、マッピング先のオブジェクトのインスタンス。
            final Object record = config.createBean(recordClass);

            // パスの位置の変更
            work.getErrors().pushNestedPath(accessor.getName(), result.size());

            // execute PreProcess listener
            methodCache.getListenerClasses().forEach(listenerClass -> {
                listenerClass.getPreLoadMethods().forEach(method -> {
                    Utils.invokeNeedProcessMethod(listenerClass.getObject(), method, record, sheet, config, work.getErrors(), ProcessCase.Load);
                });
            });

            // execute PreProcess method
            methodCache.getPreLoadMethods().forEach(method -> {
                Utils.invokeNeedProcessMethod(record, method, record, sheet, config, work.getErrors(), ProcessCase.Load);
            });

            final List<MergedRecord> mergedRecords = new ArrayList<>();

            loadMapColumns(sheet, headers, mergedRecords, CellPosition.of(initRow, hColumn), recordClass, record, config, work);

            loadArrayColumns(sheet, headers, mergedRecords, CellPosition.of(initRow, hColumn), recordClass, record, config, work);

            for(int i=0; i < headers.size() && hColumn < POIUtils.getColumns(sheet); i++){
                final RecordHeader headerInfo = headers.get(i);
                int hRow = initRow + headerInfo.getInterval();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);

                // find end of the table
                if(!POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    emptyFlag = false;
                }

                if(terminal==RecordTerminal.Border && i == startHeaderIndex){
                    if(!POIUtils.getBorderTop(cell).equals(BorderStyle.NONE)){
                        emptyFlag = false;
                    } else {
                        emptyFlag = true;
                        break;
                    }
                }

                if(!anno.terminateLabel().equals("")){
                    if(Utils.matches(POIUtils.getCellContents(cell, config.getCellFormatter()), anno.terminateLabel(), config)){
                        emptyFlag = true;
                        break;
                    }
                }

                // mapping from Excel columns to Object properties.
                final List<FieldAccessor> propeties = propertiesCache.computeIfAbsent(headerInfo.getLabel(), key -> {
                    return FieldAccessorUtils.getColumnPropertiesByName(
                            record.getClass(), work.getAnnoReader(), config, key)
                            .stream()
                            .filter(p -> p.isReadable())
                            .collect(Collectors.toList());
                });

                for(FieldAccessor property : propeties) {
                    Cell valueCell = cell;
                    final XlsColumn column = property.getAnnotationNullable(XlsColumn.class);

                    if(column.headerMerged() > 0){
                        hRow = hRow + column.headerMerged();
                        valueCell = POIUtils.getCell(sheet, hColumn, hRow);
                    }

                    // for merged cell
                    if(POIUtils.isEmptyCellContents(valueCell, config.getCellFormatter())){
                        CellStyle valueCellFormat = valueCell.getCellStyle();
                        if(column.merged() && POIUtils.getBorderRight(valueCell).equals(BorderStyle.NONE)){
                            for(int k=hColumn; k > initColumn; k--){
                                final Cell tmpCell = POIUtils.getCell(sheet, k, hRow);
                                final CellStyle tmpCellFormat = tmpCell.getCellStyle();

                                if(!POIUtils.getBorderLeft(tmpCell).equals(BorderStyle.NONE)){
                                    break;
                                }

                                if(!POIUtils.isEmptyCellContents(tmpCell, config.getCellFormatter())){
                                    valueCell = tmpCell;
                                    break;
                                }
                            }
                        }
                    }

                    if(column.headerMerged() > 0){
                        hRow = hRow - column.headerMerged();
                    }

                    CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, valueCell.getRowIndex(), valueCell.getColumnIndex());
                    if(mergedRange != null) {
                        int mergedSize =  mergedRange.getLastColumn() - mergedRange.getFirstColumn() + 1;
                        mergedRecords.add(new MergedRecord(headerInfo, mergedRange, mergedSize));
                    } else {
                        mergedRecords.add(new MergedRecord(headerInfo, CellRangeAddress.valueOf(POIUtils.formatCellAddress(valueCell)), 1));
                    }

                    if(!Utils.isLoadCase(column.cases())) {
                        continue;
                    }

                    // set for value
                    property.setPosition(record, CellPosition.of(valueCell));
                    property.setLabel(record, headerInfo.getLabel());

                    final Cell tempCommentCell = valueCell;
                    property.getCommentSetter().ifPresent(setter -> 
                            config.getCommentOperator().loadCellComment(setter, tempCommentCell, record, property, config));
                    
                    final CellConverter<?> converter = converterCache.computeIfAbsent(property.getName(), key -> getCellConverter(property, config));
                    if(converter instanceof FieldFormatter) {
                        work.getErrors().registerFieldFormatter(property.getName(), property.getType(), (FieldFormatter<?>)converter, true);
                    }

                    try {
                        final Object value = converter.toObject(valueCell);
                        property.setValue(record, value);
                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, valueCell, property.getName(), headerInfo.getLabel());
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                }
            }

            // execute nested record
            final int skipSize = loadNestedRecords(sheet, headers, mergedRecords, anno, CellPosition.of(initRow, hColumn), record, config, work);
            if(parentMergedSize > 0 && skipSize > 0 && (hColumn + skipSize) > maxColumn) {
                // check over merged cell.
                String message = String.format("Over merged size. In sheet '%s' with columnIndex=%d, over the columnIndex=%s.",
                        sheet.getSheetName(), hColumn + skipSize, maxColumn);
                throw new NestedRecordMergedSizeException(sheet.getSheetName(), skipSize, message);
            }


            if(emptyFlag){
                // パスの位置の変更
                work.getErrors().popNestedPath();
                break;
            }

            if(isAvailabledRecord(methodCache.getIgnoreableMethod(), record)) {
                // 有効なレコードのみ、処理を行う
                result.add(record);

                // set PostProcess listener
                methodCache.getListenerClasses().forEach(listenerClass -> {
                    listenerClass.getPostLoadMethods().forEach(method -> {
                        work.addNeedPostProcess(new NeedProcess(record, listenerClass.getObject(), method));
                    });
                });

                // set PostProcess method
                methodCache.getPostLoadMethods().forEach(method -> {
                    work.addNeedPostProcess(new NeedProcess(record, record, method));
                });

            }

            // パスの位置の変更
            work.getErrors().popNestedPath();

            if(skipSize > 0) {
                hColumn += skipSize;
            } else {
                hColumn++;
            }

        }

        return result;
    }

    /**
     * 表の開始位置（見出し）の位置情報を取得する。
     *
     * @param sheet
     * @param anno
     * @param accessor
     * @param config
     * @return 表の開始位置。指定したラベルが見つからない場合、設定によりnullを返す。
     * @throws AnnotationInvalidException アノテーションの値が不正で、表の開始位置が位置が見つからない場合。
     * @throws CellNotFoundException 指定したラベルが見つからない場合。
     */
    private Optional<CellPosition> getHeaderPosition(final Sheet sheet, final XlsVerticalRecords anno,
            final FieldAccessor accessor, final Configuration config) throws AnnotationInvalidException, CellNotFoundException {

        if(Utils.isNotEmpty(anno.headerAddress())) {
            try {
                return Optional.of(CellPosition.of(anno.headerAddress()));

            } catch(IllegalArgumentException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsVerticalRecords.class)
                        .var("attrName", "headerAddress")
                        .var("attrValue", anno.headerAddress())
                        .format());

            }

        } else if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                final Cell labelCell = CellFinder.query(sheet, anno.tableLabel(), config).findWhenNotFoundException();

                if(anno.tableLabelAbove()) {
                    // 表の見出しが上にある場合、左側に補正する
                    int initColumn = labelCell.getColumnIndex() + anno.right()-1;
                    int initRow = labelCell.getRowIndex() + anno.bottom();
                    return Optional.of(CellPosition.of(initRow, initColumn));

                } else {

                    int initColumn = labelCell.getColumnIndex() + anno.right();
                    int initRow = labelCell.getRowIndex();
                    return Optional.of(CellPosition.of(initRow, initColumn));

                }

            } catch(CellNotFoundException ex) {
                if(anno.optional()) {
                    return Optional.empty();
                } else {
                    throw ex;
                }
            }
        } else {
            if(anno.headerRow() < 0) {
                throw  new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsVerticalRecords.class)
                        .var("attrName", "headerRow")
                        .var("attrValue", anno.headerRow())
                        .var("min", 0)
                        .format());
            }

            if(anno.headerColumn() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsVerticalRecords.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.headerColumn())
                        .var("min", 0)
                        .format());

            }
            return Optional.of(CellPosition.of(anno.headerRow(), anno.headerColumn()));
        }
    }

    /**
     * 表の見出しから、レコードのJavaクラスの定義にあるカラムの定義で初めて見つかるリストのインデックスを取得する。
     * <p>カラムの定義とは、アノテーション「@XlsColumn」が付与されたもの。
     * @param headers 表の見出し情報。
     * @param recordClass アノテーション「@XlsColumn」が定義されたフィールドを持つレコード用のクラス。
     * @param annoReader {@link AnnotationReader}
     * @param config システム設定
     * @return 引数「headers」の該当する要素のインデックス番号。不明な場合は0を返す。
     */
    private int getStartHeaderIndexForLoading(final List<RecordHeader> headers, Class<?> recordClass,
            final AnnotationReader annoReader,  final Configuration config) {

        // レコードクラスが不明の場合、0を返す。
        if((recordClass == null || recordClass.equals(Object.class))) {
            return 0;
        }

        for(int i=0; i < headers.size(); i++) {
            RecordHeader headerInfo = headers.get(i);
            final List<FieldAccessor> propeties = FieldAccessorUtils.getColumnPropertiesByName(
                    recordClass, annoReader, config, headerInfo.getLabel())
                    .stream()
                    .filter(p -> p.isReadable())
                    .collect(Collectors.toList());
            if(!propeties.isEmpty()) {
                return i;
            }
        }

        return 0;

    }

    private void loadMapColumns(final Sheet sheet, final List<RecordHeader> headers, final List<MergedRecord> mergedRecords,
            final CellPosition beginPosition, final Class<?> recordClass, final Object record, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                recordClass, work.getAnnoReader(), XlsMapColumns.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());

        for(FieldAccessor property : properties) {
            final XlsMapColumns mapAnno = property.getAnnotationNullable(XlsMapColumns.class);

            if(!Utils.isLoadCase(mapAnno.cases())) {
                continue;
            }

            Class<?> valueClass = mapAnno.valueClass();
            if(valueClass == Object.class) {
                valueClass = property.getComponentType();
            }

            // get converter (map key class)
            final CellConverter<?> converter = getCellConverter(valueClass, property, config);
            if(converter instanceof FieldFormatter) {
                work.getErrors().registerFieldFormatter(property.getName(), valueClass, (FieldFormatter<?>)converter, true);
            }

            boolean foundPreviousColumn = false;
            final Map<String, Object> map = new LinkedHashMap<>();
            for(RecordHeader headerInfo : headers){
                int hRow = beginPosition.getRow() + headerInfo.getInterval();
                if(Utils.matches(headerInfo.getLabel(), mapAnno.previousColumnName(), config)){
                    foundPreviousColumn = true;
                    hRow++;
                    continue;
                }

                if(Utils.isNotEmpty(mapAnno.nextColumnName()) && Utils.matches(headerInfo.getLabel(), mapAnno.nextColumnName(), config)) {
                    break;
                }

                if(foundPreviousColumn){
                    final Cell cell = POIUtils.getCell(sheet, beginPosition.getColumn(), hRow);
                    property.setMapPosition(record, CellPosition.of(cell), headerInfo.getLabel());
                    property.setMapLabel(record, headerInfo.getLabel(), headerInfo.getLabel());

                    property.getMapCommentSetter().ifPresent(setter -> 
                    config.getCommentOperator().loadMapCellComment(setter, cell, record, headerInfo.getLabel(), property, config));
                    
                    CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                    if(mergedRange != null) {
                        int mergedSize =  mergedRange.getLastColumn() - mergedRange.getFirstColumn() + 1;
                        mergedRecords.add(new MergedRecord(headerInfo, mergedRange, mergedSize));
                    } else {
                        mergedRecords.add(new MergedRecord(headerInfo, CellRangeAddress.valueOf(POIUtils.formatCellAddress(cell)), 1));
                    }

                    try {
                        final Object value = converter.toObject(cell);
                        map.put(headerInfo.getLabel(), value);
                    } catch(TypeBindException e) {
                        e.setBindClass(valueClass);  // マップの項目のタイプに変更
                        work.addTypeBindError(e, cell, String.format("%s[%s]", property.getName(), headerInfo.getLabel()), headerInfo.getLabel());
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                }
            }

            if(foundPreviousColumn) {
                property.setValue(record, map);
            }
        }
    }

    private void loadArrayColumns(final Sheet sheet, final List<RecordHeader> headers, final List<MergedRecord> mergedRecords,
            final CellPosition beginPosition, final Class<?> recordClass, final Object record, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        for(RecordHeader headerInfo : headers) {
            int hRow = beginPosition.getRow() + headerInfo.getInterval();

            // アノテーション「@XlsArrayColumns」の属性「columnName」と一致するプロパティを取得する。
            final List<FieldAccessor> arrayProperties = FieldAccessorUtils.getArrayColumnsPropertiesByName(
                    recordClass, work.getAnnoReader(), config, headerInfo.getLabel());

            if(arrayProperties.isEmpty()) {
                continue;
            }

            for(FieldAccessor property : arrayProperties) {

                final XlsArrayColumns arrayAnno = property.getAnnotationNullable(XlsArrayColumns.class);

                if(!Utils.isLoadCase(arrayAnno.cases())) {
                    continue;
                }
                Class<?> elementClass = arrayAnno.elementClass();
                if(elementClass == Object.class) {
                    elementClass = property.getComponentType();
                }

                // get converter (component class)
                final CellConverter<?> converter = getCellConverter(elementClass, property, config);
                if(converter instanceof FieldFormatter) {
                    work.getErrors().registerFieldFormatter(property.getName(), elementClass, (FieldFormatter<?>)converter, true);
                }

                final CellPosition initPosition = CellPosition.of(hRow, beginPosition.getColumn());

                ArrayCellsHandler arrayHandler = new ArrayCellsHandler(property, record, elementClass, sheet, config);
                arrayHandler.setLabel(headerInfo.getLabel());

                final List<Object> result = arrayHandler.handleOnLoading(arrayAnno, initPosition, converter, work, ArrayDirection.Vertical);

                if(result != null) {
                    // インデックスが付いていないラベルの設定
                    property.setLabel(record, headerInfo.getLabel());
                }

                final Class<?> propertyType = property.getType();
                if(Collection.class.isAssignableFrom(propertyType)) {
                    if(result != null) {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        Collection<?> collection = Utils.convertListToCollection(result, (Class<Collection>)propertyType, config.getBeanFactory());
                        property.setValue(record, collection);
                    }

                } else if(propertyType.isArray()) {

                    if(result != null) {
                        final Object array = Array.newInstance(elementClass, result.size());
                        for(int i=0; i < result.size(); i++) {
                            Array.set(array, i, result.get(i));
                        }
                        property.setValue(record, array);
                    }

                } else {
                    throw new AnnotationInvalidException(arrayAnno, MessageBuilder.create("anno.notSupportType")
                            .var("property", property.getNameWithClass())
                            .varWithAnno("anno", XlsArrayColumns.class)
                            .varWithClass("actualType", propertyType)
                            .var("expectedType", "Collection(List/Set) or Array")
                            .format());
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private int loadNestedRecords(final Sheet sheet, final List<RecordHeader> headers, final List<MergedRecord> mergedRecords,
            final XlsVerticalRecords anno,
            final CellPosition beginPosition,
            final Object record,
            final Configuration config, final LoadingWorkObject work) throws XlsMapperException {

        // 読み飛ばす、レコード数。
        // 基本的に結合している個数による。
        int skipSize = 0;

        final List<FieldAccessor> nestedProperties = FieldAccessorUtils.getPropertiesWithAnnotation(
                record.getClass(), work.getAnnoReader(), XlsNestedRecords.class)
                .stream()
                .filter(p -> p.isReadable())
                .collect(Collectors.toList());
        for(FieldAccessor property : nestedProperties) {

            final XlsNestedRecords nestedAnno = property.getAnnotationNullable(XlsNestedRecords.class);

            if(!Utils.isLoadCase(nestedAnno.cases())) {
                continue;
            }

            final Class<?> clazz = property.getType();
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many

                int mergedSize = RecordsProcessorUtil.checkNestedMergedSizeRecords(sheet, mergedRecords);
                if(skipSize < mergedSize) {
                    skipSize = mergedSize;
                }

                Class<?> recordClass = nestedAnno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getComponentType();
                }

                List<?> value = loadRecords(sheet, headers, anno, beginPosition, mergedSize, property, recordClass, config, work);
                if(value != null) {
                    Collection<?> collection = Utils.convertListToCollection(value, (Class<Collection>)clazz, config.getBeanFactory());
                    property.setValue(record, collection);
                }

            } else if(clazz.isArray()) {
                // mapping by one-to-many

                int mergedSize = RecordsProcessorUtil.checkNestedMergedSizeRecords(sheet, mergedRecords);
                if(skipSize < mergedSize) {
                    skipSize = mergedSize;
                }

                Class<?> recordClass = anno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getComponentType();
                }

                List<?> value = loadRecords(sheet, headers, anno, beginPosition, mergedSize, property, recordClass, config, work);
                if(value != null) {
                    final Object array = Array.newInstance(recordClass, value.size());
                    for(int i=0; i < value.size(); i++) {
                        Array.set(array, i, value.get(i));
                    }

                    property.setValue(record, array);
                }

            } else {
                // mapping by one-to-tone

                int mergedSize = 1;
                if(skipSize < mergedSize) {
                    skipSize = mergedSize;
                }

                Class<?> recordClass = anno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getType();
                }

                List<?> value = loadRecords(sheet, headers, anno, beginPosition, mergedSize, property, recordClass, config, work);
                if(value != null && !value.isEmpty()) {
                    property.setValue(record, value.get(0));
                }

            }
        }

        return skipSize;
    }

    /**
     * レコードが有効かどうか判定する
     * <p>アノテーション{@link XlsIgnorable}のメソッドで判定を行う。
     * @param ignoreMethod レコードの判定を無視するかどうかの判定に使用するメソッド
     * @param record 判定対象のレコードのインスタンス。
     * @return trueの場合は有効。
     */
    private boolean isAvailabledRecord(final Optional<Method> ignoreMethod, final Object record)
            throws AnnotationReadException, AnnotationInvalidException {

        if(!ignoreMethod.isPresent()) {
            // 判定用のメソッドが存在しない場合
            return true;
        }

        try {
            boolean ignored = (boolean)ignoreMethod.get().invoke(record);
            return !ignored;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("fail execute method of ignoreable record", e);
        }

    }

    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsVerticalRecords anno,
            final FieldAccessor accessor, final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }

        final Class<?> clazz = accessor.getType();
        final Object result = accessor.getValue(beansObj);
        if(Collection.class.isAssignableFrom(clazz)) {

            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = accessor.getComponentType();
            }

            final Collection<Object> value = (result == null ? new ArrayList<Object>() : (Collection<Object>) result);
            final List<Object> list = Utils.convertCollectionToList(value);
            saveRecords(sheet, beansObj, anno, accessor, recordClass, list, config, work);

        } else if(clazz.isArray()) {

            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = accessor.getComponentType();
            }

            final List<Object> list = Utils.asList(result, recordClass);
            saveRecords(sheet, beansObj, anno, accessor, recordClass, list, config, work);

        } else {
            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.notSupportType")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsVerticalRecords.class)
                    .varWithClass("actualType", clazz)
                    .var("expectedType", "Collection(List/Set) or Array")
                    .format());
        }

    }

    private void saveRecords(final Sheet sheet, final Object beansObj, final XlsVerticalRecords anno, final FieldAccessor accessor,
            final Class<?> recordClass, final List<Object> result,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        RecordsProcessorUtil.checkSavingNestedRecordClass(recordClass, accessor, work.getAnnoReader());

        // get table starting position
        final Optional<CellPosition> initPosition = getHeaderPosition(sheet, anno, accessor, config);
        if(!initPosition.isPresent()) {
            return;
        }

        // ラベルの設定
        if(Utils.isNotEmpty(anno.tableLabel())) {
            final Optional<Cell> tableLabelCell = CellFinder.query(sheet, anno.tableLabel(), config).findOptional();
            tableLabelCell.ifPresent(c -> {
                final String label = POIUtils.getCellContents(c, config.getCellFormatter());
                accessor.setLabel(beansObj, label);

            });
        }

        final int initColumn = initPosition.get().getColumn();
        final int initRow = initPosition.get().getRow();

        int hColumn = initColumn;
        int hRow = initRow;

        // get header columns.
        final List<RecordHeader> headers = new ArrayList<>();
        int rangeCount = 1;
        while(true) {
            try {
                Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                while(POIUtils.isEmptyCellContents(cell, config.getCellFormatter()) && rangeCount < anno.range()) {
                    cell = POIUtils.getCell(sheet, hColumn, hRow + rangeCount);
                    rangeCount++;
                }

                final String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(Utils.isEmpty(cellValue)) {
                    break;
                }

                headers.add(new RecordHeader(cellValue, cell.getRowIndex() - initRow));
                hRow = hRow + rangeCount;
                rangeCount = 1;

                // 結合しているセルの場合は、はじめのセルだけ取得して、後は結合分スキップする。
                CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                if(mergedRange != null) {
                    hRow = hRow + (mergedRange.getLastRow() - mergedRange.getFirstRow());
                }

            } catch(ArrayIndexOutOfBoundsException ex) {
                break;
            }

            if(anno.headerLimit() > 0 && headers.size() >= anno.headerLimit()){
                break;
            }
        }

        // レコードの操作のアノテーション
        final XlsRecordOption recordOptionAnno = getRecordOptionAnnotation(accessor);

        // データ行の開始位置の調整
        hColumn += anno.headerRight();
        CellPosition startPosition = CellPosition.of(initRow, hColumn);

        // 独自の開始位置を指定する場合
        final Optional<XlsRecordFinder> finderAnno = accessor.getAnnotation(XlsRecordFinder.class);
        if(finderAnno.isPresent()) {
            final RecordFinder finder = config.createBean(finderAnno.get().value());
            startPosition = finder.find(ProcessCase.Save, finderAnno.get().args(), sheet, startPosition, beansObj, config);

        }

        // 書き込んだセルの範囲などの情報
        final RecordOperation recordOperation = new RecordOperation(recordOptionAnno);
        recordOperation.setupCellPositoin(startPosition);

        // XlsColumn(merged=true)の結合したセルの情報
        final List<CellRangeAddress> mergedRanges = new ArrayList<CellRangeAddress>();

        saveRecords(sheet, headers,
                anno,
                startPosition, new AtomicInteger(0),
                accessor, recordClass, result,
                config, work,
                mergedRanges, recordOperation);

        // 書き込むデータがない場合は、1行目の終端を操作範囲とする。
        if(result.isEmpty()) {
            recordOperation.setupCellPositoin(hRow-1, startPosition.getColumn());
        }

        if(config.isCorrectCellDataValidationOnSave()) {
            correctDataValidation(sheet, recordOperation);
        }

        if(config.isCorrectNameRangeOnSave()) {
            correctNameRange(sheet, recordOperation);
        }
    }

    /**
     * アノテーション{@link XlsRecordOption}を取得する。
     * ただし、付与されていない場合は、属性にデフォルト値が指定されているものを取得する。
     * @param accessor フィールド情報
     * @return アノテーションのインスタンス
     */
    private XlsRecordOption getRecordOptionAnnotation(final FieldAccessor accessor) {

        return accessor.getAnnotation(XlsRecordOption.class)
                .orElseGet(() -> new XlsRecordOption() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return XlsRecordOption.class;
                    }

                    @Override
                    public RemainedOperation remainedOperation() {
                        return RemainedOperation.None;
                    }

                    @Override
                    public OverOperation overOperation() {
                        return OverOperation.Break;
                    }
                });


    }

    private void saveRecords(final Sheet sheet, final List<RecordHeader> headers,
            final XlsVerticalRecords anno,
            final CellPosition initPosition, final AtomicInteger nestedRecordSize,
            final FieldAccessor accessor, final Class<?> recordClass, final List<Object> result,
            final Configuration config, final SavingWorkObject work,
            final List<CellRangeAddress> mergedRanges, final RecordOperation recordOperation) throws XlsMapperException {

        final int initColumn = initPosition.getColumn();
        final int initRow = initPosition.getRow();

        int hColumn = initColumn;

        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader(), config);
        RecordsProcessorUtil.checkMapColumns(sheet, recordClass, headers, work.getAnnoReader(), config);
        RecordsProcessorUtil.checkArrayColumns(sheet, recordClass, headers, work.getAnnoReader(), config);

        /*
         * 書き込む時には終了位置の判定は、Borderで固定する必要がある。
         * ・Emptyの場合だと、テンプレート用のシートなので必ずデータ用のセルが、空なので書き込まれなくなる。
         * ・Emptyの場合、Borderに補正して書き込む。
         */
        RecordTerminal terminal = anno.terminal();
        if(terminal == RecordTerminal.Empty) {
            terminal = RecordTerminal.Border;
        } else if(terminal == null){
            terminal = RecordTerminal.Border;
        }

        // 各種レコードのコールバック用メソッドを抽出する
        final RecordMethodCache methodCache = new RecordMethodFacatory(work.getAnnoReader(), config)
                .create(recordClass, ProcessCase.Save);

        // レコードの見出しに対するカラム情報のキャッシュ
        final Map<String, List<FieldAccessor>> propertiesCache = new HashMap<>();

        // カラムに対するConverterのキャッシュ
        final Map<String, CellConverter<?>> converterCache = new HashMap<>();

        final int startHeaderIndex = getStartHeaderIndexForSaving(headers, recordClass, work.getAnnoReader(), config);

        // get records
        for(int r=0; r < POIUtils.getColumns(sheet); r++) {

            boolean emptyFlag = true;

            // 書き込むレコードのオブジェクトを取得。データが0件の場合、nullとなる。
            final Object record;
            if(r < result.size()) {
                record = result.get(r);
            } else {
                record = null;
            }

            // パスの位置の変更
            work.getErrors().pushNestedPath(accessor.getName(), r);

            if(record != null) {

                // execute PreProcess listner
                methodCache.getListenerClasses().forEach(listenerClass -> {
                    listenerClass.getPreSaveMethods().forEach(method -> {
                        Utils.invokeNeedProcessMethod(listenerClass.getObject(), method, record, sheet, config, work.getErrors(), ProcessCase.Save);
                    });
                });

                // execute PreProcess method
                methodCache.getPreSaveMethods().forEach(method -> {
                    Utils.invokeNeedProcessMethod(record, method, record, sheet, config, work.getErrors(), ProcessCase.Save);
                });

            }

//            // レコードの各列処理で既に行を追加したかどうかのフラグ。
//            boolean insertRows = false;

//            // レコードの各列処理で既に行を削除したかどうかのフラグ。
//            boolean deleteRows = false;


            // 書き込んだセルの座標
            // ネストしたときに、結合するための情報として使用する。
            List<CellPosition> valueCellPositions = new ArrayList<>();

            // hRowという上限がない
            for(int i=0; i < headers.size(); i++) {
                final RecordHeader headerInfo = headers.get(i);
                int hRow = initRow + headerInfo.getInterval();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);

                // find end of the table
                if(!POIUtils.getCellContents(cell, config.getCellFormatter()).equals("")){
                    emptyFlag = false;
                }

                if(terminal == RecordTerminal.Border && i == startHeaderIndex){
                    final CellStyle format = cell.getCellStyle();
                    if(!POIUtils.getBorderTop(cell).equals(BorderStyle.NONE)){
                        emptyFlag = false;
                    } else {
                        emptyFlag = true;
//                            break;
                    }
                }

                if(!anno.terminateLabel().equals("")){
                    if(Utils.matches(POIUtils.getCellContents(cell, config.getCellFormatter()), anno.terminateLabel(), config)){
                        emptyFlag = true;
//                            break;
                    }
                }

                // mapping from Excel columns to Object properties.
                if(record != null) {
                    final List<FieldAccessor> propeties = propertiesCache.computeIfAbsent(headerInfo.getLabel(), key -> {
                        return FieldAccessorUtils.getColumnPropertiesByName(
                                record.getClass(), work.getAnnoReader(), config, key)
                                .stream()
                                .filter(p -> p.isWritable())
                                .collect(Collectors.toList());
                    });

                    for(FieldAccessor property : propeties) {
                        Cell valueCell = cell;
                        final XlsColumn column = property.getAnnotationNullable(XlsColumn.class);

                        //TODO: マージを考慮する必要はないかも
                        if(column.headerMerged() > 0) {
                            hRow = hRow + column.headerMerged();
                            valueCell = POIUtils.getCell(sheet, hColumn, hRow);
                        }

                        // for merged cell
                        if(POIUtils.isEmptyCellContents(valueCell, config.getCellFormatter())) {
                            if(column.merged() && POIUtils.getBorderRight(valueCell).equals(BorderStyle.NONE)){
                                for(int k=hColumn-1; k > hColumn; k--){
                                    Cell tmpCell = POIUtils.getCell(sheet, k, hRow);
                                    final CellStyle tmpCellFormat = tmpCell.getCellStyle();
                                    if(!POIUtils.getBorderLeft(tmpCell).equals(BorderStyle.NONE)){
                                        break;
                                    }
                                    if(!POIUtils.isEmptyCellContents(tmpCell, config.getCellFormatter())){
                                        valueCell = tmpCell;
                                        break;
                                    }
                                }
                            }
                        }

                        if(column.headerMerged() > 0){
                            hRow = hRow - column.headerMerged();
                        }

                        // 書き込む行が足りない場合の操作
                        if(emptyFlag) {
                            if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Break)) {
                                break;

                            } else if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Copy)) {
                                // 1つ左のセルの書式をコピーする。
                                final CellStyle style = POIUtils.getCell(sheet, valueCell.getColumnIndex()-1, valueCell.getRowIndex()).getCellStyle();
                                valueCell.setCellStyle(style);
                                valueCell.setCellType(CellType.BLANK);

                                // セル幅の調整
                                sheet.setColumnWidth(valueCell.getColumnIndex(), sheet.getColumnWidth(valueCell.getColumnIndex()-1));

                                recordOperation.incrementCopyRecord();

                            } else if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Insert)) {
                                // POIは列の追加をサポートしていないので非対応。
                                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.notSupportValue")
                                        .var("property", accessor.getNameWithClass())
                                        .varWithAnno("anno", XlsRecordOption.class)
                                        .var("attrName", "overCase")
                                        .varWithEnum("attrValue", OverOperation.Insert)
                                        .format());
                            }

                        }

                        valueCellPositions.add(CellPosition.of(valueCell));

                        recordOperation.setupCellPositoin(valueCell);

                        if(!Utils.isSaveCase(column.cases())) {
                            continue;
                        }
                        // set for cell value
                        property.setPosition(record, CellPosition.of(valueCell));
                        property.setLabel(record, headerInfo.getLabel());
                        
                        final Cell tempCommentCell = valueCell;
                        property.getCommentGetter().ifPresent(getter -> config.getCommentOperator().saveCellComment(
                                getter, tempCommentCell, record, accessor, config));

                        final CellConverter converter = converterCache.computeIfAbsent(property.getName(), key -> getCellConverter(property, config));
                        if(converter instanceof FieldFormatter) {
                            work.getErrors().registerFieldFormatter(property.getName(), property.getType(), (FieldFormatter<?>)converter, true);
                        }

                        try {
                            converter.toCell(property.getValue(record), record, sheet, CellPosition.of(valueCell));
                        } catch(TypeBindException e) {
                            work.addTypeBindError(e, valueCell, property.getName(), headerInfo.getLabel());
                            if(!config.isContinueTypeBindFailure()) {
                                throw e;
                            }
                        }

                        // セルをマージする
                        if(column.merged() && (r > 0) && config.isMergeCellOnSave()) {
                            processSavingMergedCell(valueCell, sheet, mergedRanges, config);
                        }
                    }
                }

                /*
                 * 残りの行の操作
                 *  行の追加やコピー処理をしていないときのみ実行する
                 */
                if(record == null && emptyFlag == false && recordOperation.isNotExecuteOverRecordOperation()) {
                    if(recordOperation.getAnnotation().remainedOperation().equals(RemainedOperation.None)) {
                        // なにもしない

                    } else if(recordOperation.getAnnotation().remainedOperation().equals(RemainedOperation.Clear)) {
                        Cell clearCell = POIUtils.getCell(sheet, hColumn, hRow);
                        clearCell.setCellType(CellType.BLANK);

                    } else if(recordOperation.getAnnotation().remainedOperation().equals(RemainedOperation.Delete)) {
                        // POIは列の削除をサポートしていないので非対応。
                        throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.notSupportValue")
                                .var("property", accessor.getNameWithClass())
                                .varWithAnno("anno", XlsRecordOption.class)
                                .var("attrName", "remainedOperation")
                                .varWithEnum("attrValue", RemainedOperation.Delete)
                                .format());
                    }
                }

            }

            // マップ形式のカラムを出力する
            if(record != null) {
                saveMapColumns(sheet, headers, valueCellPositions, CellPosition.of(initRow, hColumn), recordClass, record, terminal, anno, config, work, recordOperation);

                saveArrayColumns(sheet, headers, valueCellPositions, CellPosition.of(initRow, hColumn), recordClass, record, terminal, anno, config, work, recordOperation);
            }

            // execute nested record.
            int skipSize = 0;
            if(record != null) {
                skipSize = saveNestedRecords(sheet, headers, valueCellPositions, anno, CellPosition.of(initRow, hColumn), record,
                        config, work, mergedRanges, recordOperation);
                nestedRecordSize.addAndGet(skipSize);
            }

            if(record != null) {

                // set PostProcess listener
                methodCache.getListenerClasses().forEach(listenerClass -> {
                    listenerClass.getPostSaveMethods().forEach(method -> {
                        work.addNeedPostProcess(new NeedProcess(record, listenerClass.getObject(), method));
                    });
                });

                // set PostProcess method
                methodCache.getPostSaveMethods().forEach(method -> {
                    work.addNeedPostProcess(new NeedProcess(record, record, method));
                });

            }

            // パスの位置の変更
            work.getErrors().popNestedPath();

            if(skipSize > 0) {
                hColumn += skipSize;
            } else {
                hColumn++;
            }

            if(emptyFlag == true && (r > result.size())) {
                // セルが空で、書き込むデータがない場合。
                break;
            }
        }

    }

    /**
     * 表の見出しから、レコードのJavaクラスの定義にあるカラムの定義で初めて見つかるリストのインデックスを取得する。
     * ・カラムの定義とは、アノテーション「@XlsColumn」が付与されたもの。
     * @param headers 表の見出し情報。
     * @param recordClass アノテーション「@XlsColumn」が定義されたフィールドを持つレコード用のクラス。
     * @param annoReader AnnotationReader
     * @param config システム設定
     * @return 引数「headers」の該当する要素のインデックス番号。不明な場合は、0を返す。
     */
    private int getStartHeaderIndexForSaving(final List<RecordHeader> headers, Class<?> recordClass,
            final AnnotationReader annoReader, final Configuration config) {

        // レコードクラスが不明の場合、0を返す。
        if((recordClass == null || recordClass.equals(Object.class))) {
            return 0;
        }

        for(int i=0; i < headers.size(); i++) {
            RecordHeader headerInfo = headers.get(i);
            final List<FieldAccessor> propeties = FieldAccessorUtils.getColumnPropertiesByName(
                    recordClass,annoReader, config,  headerInfo.getLabel())
                    .stream()
                    .filter(p -> p.isWritable())
                    .collect(Collectors.toList());
            if(!propeties.isEmpty()) {
                return i;
            }
        }

        return 0;

    }

    /**
     * 上部のセルと同じ値の場合マージする
     * @param currentCell
     * @param sheet
     * @param mergedRanges
     * @return
     */
    private boolean processSavingMergedCell(final Cell currentCell, final Sheet sheet,
            final List<CellRangeAddress> mergedRanges, final Configuration config) {

        final int row = currentCell.getRowIndex();
        final int column = currentCell.getColumnIndex();

        if(column <= 0) {
            return false;
        }

        // 上のセルと比較する
        final String value = POIUtils.getCellContents(currentCell, config.getCellFormatter());
        String upperValue = POIUtils.getCellContents(POIUtils.getCell(sheet, column-1, row), config.getCellFormatter());

        // 結合されている場合、結合の先頭セルを取得する
        int startColumn = column - 1;
        CellRangeAddress currentMergedRange = null;
        for(CellRangeAddress range : mergedRanges) {
            // 列が範囲外の場合
            if((range.getFirstColumn() > startColumn) || (startColumn > range.getLastColumn())) {
                continue;
            }

            // 行が範囲外の場合
            if((range.getFirstRow() > row) || (row > range.getLastRow())) {
                continue;
            }

            upperValue = POIUtils.getCellContents(POIUtils.getCell(sheet, range.getFirstColumn(), row), config.getCellFormatter());
            currentMergedRange = range;
            break;
        }

        if(!value.equals(upperValue)) {
            // 値が異なる場合は結合しない
            return false;
        }

        // 既に結合済みの場合は一端解除する
        if(currentMergedRange != null) {
            startColumn = currentMergedRange.getFirstColumn();
            POIUtils.removeMergedRange(sheet, currentMergedRange);
        }

        final CellRangeAddress newRange = POIUtils.mergeCells(sheet, startColumn, row, column, row);
        mergedRanges.add(newRange);
        return true;

    }

    private void saveMapColumns(final Sheet sheet, final List<RecordHeader> headers, final List<CellPosition> valueCellPositions,
            final CellPosition beginPosition, final Class<?> recordClass, final Object record, final RecordTerminal terminal,
            final XlsVerticalRecords anno, final Configuration config, final SavingWorkObject work,
            final RecordOperation recordOperation) throws XlsMapperException {

        final List<FieldAccessor> properties = FieldAccessorUtils.getPropertiesWithAnnotation(
                recordClass, work.getAnnoReader(), XlsMapColumns.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());
        for(FieldAccessor property : properties) {

            final XlsMapColumns mapAnno = property.getAnnotationNullable(XlsMapColumns.class);

            Class<?> valueClass = mapAnno.valueClass();
            if(valueClass == Object.class) {
                valueClass = property.getComponentType();
            }

            // get converter (map key class)
            final CellConverter converter = getCellConverter(valueClass, property, config);
            if(converter instanceof FieldFormatter) {
                work.getErrors().registerFieldFormatter(property.getName(), valueClass, (FieldFormatter<?>)converter, true);
            }

            boolean foundPreviousColumn = false;
            for(RecordHeader headerInfo : headers) {
                int hRow = beginPosition.getRow() + headerInfo.getInterval();
                if(Utils.matches(headerInfo.getLabel(), mapAnno.previousColumnName(), config)){
                    foundPreviousColumn = true;
                    hRow++;
                    continue;
                }

                if(Utils.isNotEmpty(mapAnno.nextColumnName()) && Utils.matches(headerInfo.getLabel(), mapAnno.nextColumnName(), config)) {
                    break;
                }

                if(foundPreviousColumn) {
                    final Cell cell = POIUtils.getCell(sheet, beginPosition.getColumn(), hRow);

                    // 空セルか判断する
                    boolean emptyFlag = true;
                    if(terminal == RecordTerminal.Border) {
                        if(!POIUtils.getBorderTop(cell).equals(BorderStyle.NONE)) {
                            emptyFlag = false;
                        } else {
                            emptyFlag = true;
                        }
                    }

                    if(!anno.terminateLabel().equals("")) {
                        if(Utils.matches(POIUtils.getCellContents(cell, config.getCellFormatter()), anno.terminateLabel(), config)) {
                            emptyFlag = true;
                        }
                    }

                    // 空セルの場合
                    if(emptyFlag) {
                        if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Break)) {
                            break;

                        } else if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Copy)) {
                            final CellStyle style = POIUtils.getCell(sheet, cell.getColumnIndex()-1, cell.getRowIndex()).getCellStyle();
                            cell.setCellStyle(style);
                            cell.setCellType(CellType.BLANK);

                        } else if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Insert)) {
                            // POIは列の追加をサポートしていないので非対応。
                            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.notSupportValue")
                                    .var("property", property.getNameWithClass())
                                    .varWithAnno("anno", XlsRecordOption.class)
                                    .var("attrName", "overCase")
                                    .varWithEnum("attrValue", OverOperation.Insert)
                                    .format());

                        }
                    }

                    valueCellPositions.add(CellPosition.of(cell));

                    recordOperation.setupCellPositoin(cell);

                    if(!Utils.isSaveCase(mapAnno.cases())) {
                        continue;
                    }

                    // セルの値を出力する
                    property.setMapPosition(record, CellPosition.of(cell), headerInfo.getLabel());
                    property.setMapLabel(record, headerInfo.getLabel(), headerInfo.getLabel());
                    
                    property.getMapCommentGetter().ifPresent(getter -> config.getCommentOperator().saveMapCellComment(
                            getter, cell, record, headerInfo.getLabel(), property, config));

                    try {
                        Object value = property.getValueOfMap(headerInfo.getLabel(), record);
                        converter.toCell(value, record, sheet, CellPosition.of(cell));
                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, cell, String.format("%s[%s]", property.getName(), headerInfo.getLabel()), headerInfo.getLabel());
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                }

            }
        }

    }

    private void saveArrayColumns(final Sheet sheet, final List<RecordHeader> headers, final List<CellPosition> valueCellPositions,
            final CellPosition beginPosition, Class<?> recordClass, Object record, RecordTerminal terminal,
            XlsVerticalRecords anno, Configuration config, SavingWorkObject work,
            RecordOperation recordOperation) throws XlsMapperException {


        for(RecordHeader headerInfo : headers) {
            int hRow = beginPosition.getRow() + headerInfo.getInterval();

            // アノテーション「@XlsArrayColumns」の属性「columnName」と一致するプロパティを取得する。
            final List<FieldAccessor> arrayProperties = FieldAccessorUtils.getArrayColumnsPropertiesByName(
                    recordClass, work.getAnnoReader(), config, headerInfo.getLabel());

            if(arrayProperties.isEmpty()) {
                continue;
            }

            for(FieldAccessor property : arrayProperties) {

                final XlsArrayColumns arrayAnno = property.getAnnotationNullable(XlsArrayColumns.class);

                Class<?> elementClass = arrayAnno.elementClass();
                if(elementClass == Object.class) {
                    elementClass = property.getComponentType();
                }
                final CellPosition initPosition = CellPosition.of(hRow, beginPosition.getColumn());

                // 書き込む領域について、上のセルをコピーなどする。
                int iRow = initPosition.getRow();
                for(int i=0; i < arrayAnno.size(); i++) {
                    final Cell cell = POIUtils.getCell(sheet, initPosition.getColumn(), iRow);

                    // 空セルか判断する - 値のセルかどうか
                    boolean emptyFlag = true;

                    if(terminal == RecordTerminal.Border) {
                        if(!POIUtils.getBorderTop(cell).equals(BorderStyle.NONE)) {
                            emptyFlag = false;
                        } else {
                            emptyFlag = true;
                        }
                    }

                    if(!anno.terminateLabel().equals("")) {
                        if(Utils.matches(POIUtils.getCellContents(cell, config.getCellFormatter()), anno.terminateLabel(), config)) {
                            emptyFlag = true;
                        }
                    }

                    // 空セルの場合
                    if(emptyFlag) {
                        if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Break)) {
                            break;

                        } else if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Copy)) {
                            final Cell fromCell = POIUtils.getCell(sheet, cell.getColumnIndex()-1, cell.getRowIndex());
                            copyCellStyle(fromCell, cell);

                        } else if(recordOperation.getAnnotation().overOperation().equals(OverOperation.Insert)) {
                            // POIは列の追加をサポートしていないので非対応。
                            throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.notSupportValue")
                                    .var("property", property.getNameWithClass())
                                    .varWithAnno("anno", XlsRecordOption.class)
                                    .var("attrName", "overCase")
                                    .varWithEnum("attrValue", OverOperation.Insert)
                                    .format());


                        }
                    }

                    // 結合情報を考慮して、インデックス（列番号）を次のセルに進める。
                    if(arrayAnno.elementMerged()) {
                        final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                        if(mergedRegion != null) {
                            iRow += POIUtils.getRowSize(mergedRegion);
                        } else {
                            iRow++;
                        }

                    } else {
                        iRow++;
                    }

                    recordOperation.setupCellPositoin(cell);
                }

                if(!Utils.isSaveCase(arrayAnno.cases())) {
                    continue;
                }

                // get converter (component class)
                final CellConverter<?> converter = getCellConverter(elementClass, property, config);
                if(converter instanceof FieldFormatter) {
                    work.getErrors().registerFieldFormatter(property.getName(), elementClass, (FieldFormatter<?>)converter, true);
                }

                ArrayCellsHandler arrayHandler = new ArrayCellsHandler(property, record, elementClass, sheet, config);
                arrayHandler.setLabel(headerInfo.getLabel());

                final Class<?> propertyType = property.getType();
                final Object result = property.getValue(record);

                if(result != null) {
                    // インデックスが付いていないラベルの設定
                    property.setLabel(record, headerInfo.getLabel());
                }

                if(Collection.class.isAssignableFrom(propertyType)) {

                    final Collection<Object> value = (result == null ? new ArrayList<Object>() : (Collection<Object>) result);
                    final List<Object> list = Utils.convertCollectionToList(value);
                    arrayHandler.handleOnSaving(list, arrayAnno, initPosition, converter, work, ArrayDirection.Vertical);

                } else if(propertyType.isArray()) {

                    final List<Object> list = Utils.asList(result, elementClass);
                    arrayHandler.handleOnSaving(list, arrayAnno, initPosition, converter, work, ArrayDirection.Vertical);
                }

            }
        }

    }

    /**
     * セルの書式をコピーする。
     * <p>コピー先のセルの種類は、空セルとする。</p>
     * <p>結合情報も列方向の結合をコピーする。</p>
     *
     * @since 2.0
     * @param fromCell コピー元
     * @param toCell コピー先
     */
    private void copyCellStyle(final Cell fromCell, final Cell toCell) {

        final CellStyle style = fromCell.getCellStyle();
        toCell.setCellStyle(style);
        toCell.setCellType(CellType.BLANK);

        // 縦方向に結合されている場合、結合情報のコピーする。（XlsArrayColumns用）
        final Sheet sheet = fromCell.getSheet();
        final CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, fromCell.getRowIndex(), fromCell.getColumnIndex());
        final int mergedSize = POIUtils.getRowSize(mergedRegion);

        if(mergedSize >= 2) {
            CellRangeAddress newMergedRegion = POIUtils.getMergedRegion(sheet, toCell.getRowIndex(), toCell.getColumnIndex());
            if(newMergedRegion != null) {
                // 既に結合している場合 - 通常はありえない。
                return;
            }

            newMergedRegion = POIUtils.mergeCells(sheet,
                    toCell.getColumnIndex(), mergedRegion.getFirstRow(), toCell.getColumnIndex(), mergedRegion.getLastRow());

            // 結合先のセルの書式も設定する
            for(int i=1; i < mergedSize; i++) {
                Cell mergedFromCell = POIUtils.getCell(sheet, fromCell.getColumnIndex(), toCell.getRowIndex()+i);

                Cell mergedToCell = POIUtils.getCell(sheet, toCell.getColumnIndex(), toCell.getRowIndex()+i);
                mergedToCell.setCellStyle(mergedFromCell.getCellStyle());
                mergedToCell.setCellType(CellType.BLANK);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private int saveNestedRecords(final Sheet sheet, final List<RecordHeader> headers, final List<CellPosition> valueCellPositions,
            final XlsVerticalRecords anno,
            final CellPosition beginPositoin,
            final Object record,
            final Configuration config, final SavingWorkObject work,
            final List<CellRangeAddress> mergedRanges, final RecordOperation recordOperation) throws XlsMapperException {

        int skipSize = 0;

        final List<FieldAccessor> nestedProperties = FieldAccessorUtils.getPropertiesWithAnnotation(
                record.getClass(), work.getAnnoReader(), XlsNestedRecords.class)
                .stream()
                .filter(p -> p.isWritable())
                .collect(Collectors.toList());
        for(FieldAccessor property : nestedProperties) {

            final XlsNestedRecords nestedAnno = property.getAnnotationNullable(XlsNestedRecords.class);

            if(!Utils.isSaveCase(nestedAnno.cases())) {
                continue;
            }

            final Class<?> clazz = property.getType();
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many

                Class<?> recordClass = nestedAnno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getComponentType();
                }

                Collection<Object> value = (Collection<Object>) property.getValue(record);
                if(value == null) {
                    // dummy empty record
                    value = (Collection<Object>) Arrays.asList(config.createBean(recordClass));
                }

                final List<Object> list = Utils.convertCollectionToList(value);
                final AtomicInteger nestedRecordSize = new AtomicInteger(0);
                saveRecords(sheet, headers, anno, beginPositoin, nestedRecordSize, property, recordClass, list,
                        config, work, mergedRanges, recordOperation);

                if(skipSize < list.size()) {
                    if(nestedRecordSize.get() > 0) {
                        skipSize = nestedRecordSize.get() - skipSize;
                    } else {
                        skipSize = list.size();
                    }
                }

                processSavingNestedMergedRecord(sheet, skipSize, valueCellPositions);

            } else if(clazz.isArray()) {

                // mapping by one-to-many

                Class<?> recordClass = nestedAnno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getComponentType();
                }

                Object[] value = (Object[])property.getValue(record);
                if(value == null) {
                    // dummy empty record
                    value = new Object[]{config.createBean(recordClass)};
                }

                final List<Object> list = Arrays.asList(value);
                final AtomicInteger nestedRecordSize = new AtomicInteger(0);
                saveRecords(sheet, headers, anno, beginPositoin, nestedRecordSize, property, recordClass, list,
                        config, work, mergedRanges, recordOperation);

                if(nestedRecordSize.get() > 0) {
                    skipSize = nestedRecordSize.get() - skipSize;
                } else {
                    skipSize = list.size();
                }

                processSavingNestedMergedRecord(sheet, skipSize, valueCellPositions);

            } else {

                // mapping by one-to-many
                Class<?> recordClass = anno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getType();
                }

                Object value = property.getValue(record);
                if(value == null) {
                    // dummy empty record
                    value = config.createBean(recordClass);
                }

                List<Object> list = Arrays.asList(value);
                final AtomicInteger nestedRecordSize = new AtomicInteger(0);
                saveRecords(sheet, headers, anno, beginPositoin, nestedRecordSize, property, recordClass, list,
                        config, work, mergedRanges, recordOperation);

                if(nestedRecordSize.get() > 0) {
                    skipSize = nestedRecordSize.get() - skipSize;
                } else {
                    skipSize = list.size();
                }

            }
        }

        return skipSize;
    }

    /**
     * ネストしたレコードの親のセルを結合する
     * @param sheet シート
     * @param mergedSize 結合するセルのサイズ
     * @param valueCellPositions 結合する開始位置のセルのアドレス
     */
    private void processSavingNestedMergedRecord(final Sheet sheet, final int mergedSize,
            final List<CellPosition> valueCellPositions) {

        if(mergedSize <= 1) {
            return;
        }

        // ネストした場合、上のセルのスタイルをコピーして、結合する
        for(CellPosition position : valueCellPositions) {
            Cell valueCell = POIUtils.getCell(sheet, position);
            if(valueCell == null) {
                continue;
            }

            final CellStyle style = valueCell.getCellStyle();

            // 結合するセルに対して、上のセルのスタイルをコピーする。
            // 列を挿入するときなどに必要になるため、スタイルを設定する。
            for(int i=1; i < mergedSize; i++) {
                Cell mergedCell = POIUtils.getCell(sheet, position.getColumn() + i, position.getRow());
                mergedCell.setCellStyle(style);
                mergedCell.setCellType(CellType.BLANK);
            }

            final CellRangeAddress range = new CellRangeAddress(position.getRow(), position.getRow(),
                    position.getColumn(), position.getColumn() + mergedSize -1);

            // 既に結合済みのセルがある場合、外す。
            for(int colIdx=range.getFirstColumn(); colIdx <= range.getLastColumn(); colIdx++) {
                CellRangeAddress r = POIUtils.getMergedRegion(sheet, position.getRow(), colIdx);
                if(r != null) {
                    POIUtils.removeMergedRange(sheet, r);
                }
            }

            sheet.addMergedRegion(range);
        }

    }

    /**
     * セルの入力規則の範囲を修正する。
     * @param sheet
     * @param recordOperation
     */
    private void correctDataValidation(final Sheet sheet, final RecordOperation recordOperation) {

        if(recordOperation.isNotExecuteRecordOperation()) {
            return;
        }

        //TODO: セルの結合も考慮する

        // 操作をしていないセルの範囲の取得
        final CellRangeAddress notOperateRange = new CellRangeAddress(
                recordOperation.getTopLeftPoisitoin().y,
                recordOperation.getBottomRightPosition().y,
                recordOperation.getTopLeftPoisitoin().x,
                recordOperation.getBottomRightPosition().x - recordOperation.getCountInsertRecord()
                );

        final List<? extends DataValidation> list = sheet.getDataValidations();
        for(DataValidation validation : list) {

            final CellRangeAddressList region = validation.getRegions().copy();
            boolean changedRange = false;
            for(CellRangeAddress range : region.getCellRangeAddresses()) {

                if(notOperateRange.isInRange(range.getFirstRow(), range.getFirstColumn())) {
                    // 自身のセルの範囲の場合は、行の範囲を広げる
                    range.setLastColumn(recordOperation.getBottomRightPosition().x);
                    changedRange = true;

                } else if(notOperateRange.getLastColumn() < range.getFirstColumn()) {
                    /*
                     * VerticalRecordsの場合は、挿入・削除はないので、自身以外の範囲は修正しない。
                     */
                }

            }

            // 修正した規則を、再度シートに追加する
            if(changedRange) {
                boolean updated = POIUtils.updateDataValidationRegion(sheet, validation.getRegions(), region);
                assert updated == true;
            }
        }

    }

    /**
     * 名前の定義の範囲を修正する。
     * @param sheet
     * @param recordOperation
     */
    private void correctNameRange(final Sheet sheet, final RecordOperation recordOperation) {

        if(recordOperation.isNotExecuteRecordOperation()) {
            return;
        }

        final Workbook workbook = sheet.getWorkbook();
        final int numName = workbook.getNumberOfNames();
        if(numName == 0) {
            return;
        }

        // 操作をしていないセルの範囲の取得
        final CellRangeAddress notOperateRange = new CellRangeAddress(
                recordOperation.getTopLeftPoisitoin().y,
                recordOperation.getBottomRightPosition().y,
                recordOperation.getTopLeftPoisitoin().x,
                recordOperation.getBottomRightPosition().x - recordOperation.getCountInsertRecord()
                );

        for(int i=0; i < numName; i++) {
            final Name name = workbook.getNameAt(i);

            if(name.isDeleted() || name.isFunctionName()) {
                // 削除されている場合、関数の場合はスキップ
                continue;
            }

            if(!sheet.getSheetName().equals(name.getSheetName())) {
                // 自身のシートでない名前は、修正しない。
                continue;
            }

            AreaReference areaRef = new AreaReference(name.getRefersToFormula(), POIUtils.getVersion(sheet));
            CellReference firstCellRef = areaRef.getFirstCell();
            CellReference lastCellRef = areaRef.getLastCell();

            if(notOperateRange.isInRange(firstCellRef.getRow(), firstCellRef.getCol())) {
                // 自身のセルの範囲の場合は、行の範囲を広げる。

                lastCellRef= new CellReference(
                        lastCellRef.getSheetName(),
                        lastCellRef.getRow(), recordOperation.getBottomRightPosition().x,
                        lastCellRef.isRowAbsolute(), lastCellRef.isColAbsolute());
                areaRef = new AreaReference(firstCellRef, lastCellRef, sheet.getWorkbook().getSpreadsheetVersion());

                // 修正した範囲を再設定する
                name.setRefersToFormula(areaRef.formatAsString());

            } else if(notOperateRange.getLastColumn() < firstCellRef.getCol()) {
                /*
                 * 名前の定義の場合、自身のセルノ範囲より右方にあるセルの範囲の場合、
                 * 自動的に修正されるため、修正は必要なし。
                 */

            }

        }

    }

}
