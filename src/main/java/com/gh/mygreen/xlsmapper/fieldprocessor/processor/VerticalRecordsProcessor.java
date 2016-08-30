package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import java.awt.Point;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.NeedProcess;
import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.OverRecordOperate;
import com.gh.mygreen.xlsmapper.annotation.RecordTerminal;
import com.gh.mygreen.xlsmapper.annotation.RemainedRecordOperate;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsListener;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsNestedRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
import com.gh.mygreen.xlsmapper.cellconvert.CellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellAddress;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.MergedRecord;
import com.gh.mygreen.xlsmapper.fieldprocessor.NestMergedSizeException;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordHeader;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordsProcessorUtil;
import com.gh.mygreen.xlsmapper.xml.AnnotationReadException;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * アノテーション{@link XlsVerticalRecords}を処理するクラス。
 * 
 * @version 1.5
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class VerticalRecordsProcessor extends AbstractFieldProcessor<XlsVerticalRecords>{

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsVerticalRecords anno,
            final FieldAdaptor adaptor, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        // ラベルの設定
        if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                final Cell tableLabelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
                Utils.setLabel(POIUtils.getCellContents(tableLabelCell, config.getCellFormatter()), beansObj, adaptor.getName());
            } catch(CellNotFoundException e) {
                
            }
        }
        
        final Class<?> clazz = adaptor.getTargetClass();
        if(Collection.class.isAssignableFrom(clazz)) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getLoadingGenericClassType();
            }
            
            final List<?> value = loadRecords(sheet, anno, adaptor, recordClass, config, work);
            if(value != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Collection<?> collection = Utils.convertListToCollection(value, (Class<Collection>)clazz, config.getBeanFactory());
                adaptor.setValue(beansObj, collection);
            }
        } else if(clazz.isArray()) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getLoadingGenericClassType();
            }
            
            final List<?> value = loadRecords(sheet, anno, adaptor, recordClass, config, work);
            if(value != null) {
                final Object array = Array.newInstance(recordClass, value.size());
                for(int i=0; i < value.size(); i++) {
                    Array.set(array, i, value.get(i));
                }
                
                adaptor.setValue(beansObj, array);
            }
            
        } else {
            throw new AnnotationInvalidException(
                    String.format("With '%s', '@XlsVerticalRecords.class.getSimpleName()' should only granted Collection(List/Set) or Array. : %s", 
                            adaptor.getNameWithClass(), clazz.getName()),
                            anno);
        }
        
    }
    
   private List<?> loadRecords(final Sheet sheet, XlsVerticalRecords anno, final FieldAdaptor adaptor,
           final Class<?> recordClass, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        // get table starting position
        final CellAddress initPosition = getHeaderPosition(sheet, anno, adaptor, config);
        if(initPosition == null) {
            return null;
        }
        
        final int initColumn = initPosition.getColumn();
        final int initRow = initPosition.getRow();
        
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
        
        return loadRecords(sheet, headers, anno, new CellAddress(initRow, hColumn), 0, adaptor, recordClass, config, work);
   }
   
   private List<?> loadRecords(final Sheet sheet, final List<RecordHeader> headers,
           final XlsVerticalRecords anno,
           final CellAddress initPosition, final int parentMergedSize,
           final FieldAdaptor adaptor, final Class<?> recordClass,
           final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
       
        final List<Object> result = new ArrayList<>();
        
        final int initColumn = initPosition.getColumn(); 
        final int initRow = initPosition.getRow();
        
        final int maxColumn = initColumn + parentMergedSize;
        int hColumn = initColumn;
        
        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader(), config);
        
        RecordTerminal terminal = anno.terminal();
        if(terminal == null){
            terminal = RecordTerminal.Empty;
        }
        
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
            work.getErrors().pushNestedPath(adaptor.getName(), result.size());
            
            // execute PreProcess listener
            final XlsListener listenerAnno = work.getAnnoReader().getAnnotation(record.getClass(), XlsListener.class);
            if(listenerAnno != null) {
                Object listenerObj = config.createBean(listenerAnno.listenerClass());
                for(Method method : listenerObj.getClass().getMethods()) {
                    final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(listenerAnno.listenerClass(), method, XlsPreLoad.class);
                    if(preProcessAnno != null) {
                        Utils.invokeNeedProcessMethod(listenerObj, method, record, sheet, config, work.getErrors());
                    }
                }
            }
            
            // execute PreProcess method
            for(Method method : record.getClass().getMethods()) {
                final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPreLoad.class);
                if(preProcessAnno != null) {
                    Utils.invokeNeedProcessMethod(record, method, record, sheet, config, work.getErrors());
                }
            }
            
            final List<MergedRecord> mergedRecords = new ArrayList<>();
            
            loadMapColumns(sheet, headers, mergedRecords, new CellAddress(initRow, hColumn), record, config, work);
            
            for(int i=0; i < headers.size() && hColumn < POIUtils.getColumns(sheet); i++){
                final RecordHeader headerInfo = headers.get(i);
                int hRow = initRow + headerInfo.getInterval();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                
                // find end of the table
                if(!POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    emptyFlag = false;
                }
                
                if(terminal==RecordTerminal.Border && i == startHeaderIndex){
                    final CellStyle format = cell.getCellStyle();
                    if(format!=null && !(format.getBorderTop() == CellStyle.BORDER_NONE)){
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
                final List<FieldAdaptor> propeties = Utils.getLoadingColumnProperties(
                        record.getClass(), headerInfo.getLabel(), work.getAnnoReader(), config);
                for(FieldAdaptor property : propeties) {
                    Cell valueCell = cell;
                    final XlsColumn column = property.getLoadingAnnotation(XlsColumn.class);
                    
                    if(column.headerMerged() > 0){
                        hRow = hRow + column.headerMerged();
                        valueCell = POIUtils.getCell(sheet, hColumn, hRow);
                    }
                    
                    // for merged cell
                    if(POIUtils.isEmptyCellContents(valueCell, config.getCellFormatter())){
                        CellStyle valueCellFormat = valueCell.getCellStyle();
                        if(column.merged() && 
                                (valueCellFormat == null || valueCellFormat.getBorderRight() == CellStyle.BORDER_NONE)){
                            for(int k=hColumn; k > initColumn; k--){
                                final Cell tmpCell = POIUtils.getCell(sheet, k, hRow);
                                final CellStyle tmpCellFormat = tmpCell.getCellStyle();
                                
                                if(tmpCellFormat!=null && !(tmpCellFormat.getBorderLeft() == CellStyle.BORDER_NONE)){
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
                        mergedRecords.add(new MergedRecord(headerInfo, CellRangeAddress.valueOf(Utils.formatCellAddress(valueCell)), 1));
                    }
                    
                    // set for value
                    Utils.setPosition(valueCell.getColumnIndex(), valueCell.getRowIndex(), record, property.getName());
                    Utils.setLabel(headerInfo.getLabel(), record, property.getName());
                    final CellConverter<?> converter = getLoadingCellConverter(property, config.getConverterRegistry(), config);
                    try {
                        final Object value = converter.toObject(valueCell, property, config);
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
            final int skipSize = loadNestedRecords(sheet, headers, mergedRecords, anno, new CellAddress(initRow, hColumn), record, config, work);
            if(parentMergedSize > 0 && skipSize > 0 && (hColumn + skipSize) > maxColumn) {
                // check over merged cell.
                String message = String.format("Over merged size. In sheet '%s' with columnIndex=%d, over the columnIndex=%s.",
                        sheet.getSheetName(), hColumn + skipSize, maxColumn);
                throw new NestMergedSizeException(sheet.getSheetName(), skipSize, message);
            }
            
            
            if(emptyFlag){
                // パスの位置の変更
                work.getErrors().popNestedPath();
                break;
            }
            
            if(!anno.ignoreEmptyRecord() || !isEmptyRecord(adaptor, record, work.getAnnoReader())) {
                result.add(record);
                
            }
            
            // set PostProcess listener
            if(listenerAnno != null) {
                Object listenerObj = config.createBean(listenerAnno.listenerClass());
                for(Method method : listenerObj.getClass().getMethods()) {
                    
                    final XlsPostLoad postProcessAnno = work.getAnnoReader().getAnnotation(listenerAnno.listenerClass(), method, XlsPostLoad.class);
                    if(postProcessAnno != null) {
                        work.addNeedPostProcess(new NeedProcess(record, listenerObj, method));
                    }
                }
            }
            
            // set PostProcess method
            for(Method method : record.getClass().getMethods()) {
                final XlsPostLoad postProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPostLoad.class);
                if(postProcessAnno != null) {
                    work.addNeedPostProcess(new NeedProcess(record, record, method));
                }
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
     * @param adaptor
     * @param config
     * @return 表の開始位置。指定したラベルが見つからない場合、設定によりnullを返す。
     * @throws AnnotationInvalidException アノテーションの値が不正で、表の開始位置が位置が見つからない場合。
     * @throws CellNotFoundException 指定したラベルが見つからない場合。
     */
    private CellAddress getHeaderPosition(final Sheet sheet, final XlsVerticalRecords anno,
            final FieldAdaptor adaptor, final XlsMapperConfig config) throws AnnotationInvalidException, CellNotFoundException {
        
        if(Utils.isNotEmpty(anno.headerAddress())) {
            Point address = Utils.parseCellAddress(anno.headerAddress());
            if(address == null) {
                throw new AnnotationInvalidException(
                        String.format("With '%s', @XlsVerticalRecords#headerAddress is wrong cell address '%s'.",
                                adaptor.getNameWithClass(), anno.headerAddress(), adaptor.getNameWithClass()), anno);
            }
            return new CellAddress(address);
            
        } else if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                Cell labelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
                
                if(anno.tableLabelAbove()) {
                    // 表の見出しが上にある場合。HorizontalRecordsを同じ。
                    int initColumn = labelCell.getColumnIndex();
                    int initRow = labelCell.getRowIndex() + anno.right();
                    return new CellAddress(initRow, initColumn);
                    
                } else {
                    
                    int initColumn = labelCell.getColumnIndex() + anno.right();
                    int initRow = labelCell.getRowIndex();
                    return new CellAddress(initRow, initColumn);
                    
                }
                
            } catch(CellNotFoundException ex) {
                if(anno.optional()) {
                    return null;
                } else {
                    throw ex;
                }
            }
        } else {
            if(anno.headerColumn() < 0 || anno.headerRow() < 0) {
                throw new AnnotationInvalidException(
                        String.format("With '%s', @XlsVerticalRecords#headerColumn or headerRow should be greater than or equal zero. (headerColumn=%d, headerRow=%d)",
                                adaptor.getNameWithClass(), anno.headerColumn(), anno.headerRow()), anno);
            }
            return new CellAddress(anno.headerRow(), anno.headerColumn());
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
            final AnnotationReader annoReader,  final XlsMapperConfig config) {
        
        // レコードクラスが不明の場合、0を返す。
        if((recordClass == null || recordClass.equals(Object.class))) {
            return 0;
        }
        
        for(int i=0; i < headers.size(); i++) {
            RecordHeader headerInfo = headers.get(i);
            final List<FieldAdaptor> propeties = Utils.getLoadingColumnProperties(
                    recordClass, headerInfo.getLabel(), annoReader, config);
            if(!propeties.isEmpty()) {
                return i;
            }
        }
        
        return 0;
        
    }
    
    private void loadMapColumns(final Sheet sheet, final List<RecordHeader> headers, final List<MergedRecord> mergedRecords,
            final CellAddress beginPosition, final Object record, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final List<FieldAdaptor> properties = Utils.getLoadingMapColumnProperties(record.getClass(), work.getAnnoReader());
        
        for(FieldAdaptor property : properties) {
            final XlsMapColumns mapAnno = property.getLoadingAnnotation(XlsMapColumns.class);
            
            Class<?> itemClass = mapAnno.itemClass();
            if(itemClass == Object.class) {
                itemClass = property.getLoadingGenericClassType();
            }
            
            // get converter (map key class)
            final CellConverter<?> converter = config.getConverterRegistry().getConverter(itemClass);
            if(converter == null) {
                throw newNotFoundConverterExpcetion(itemClass);
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
                    Utils.setPositionWithMapColumn(cell.getColumnIndex(), cell.getRowIndex(), record, property.getName(), headerInfo.getLabel());
                    Utils.setLabelWithMapColumn(headerInfo.getLabel(), record, property.getName(), headerInfo.getLabel());
                    
                    CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                    if(mergedRange != null) {
                        int mergedSize =  mergedRange.getLastColumn() - mergedRange.getFirstColumn() + 1;
                        mergedRecords.add(new MergedRecord(headerInfo, mergedRange, mergedSize));
                    } else {
                        mergedRecords.add(new MergedRecord(headerInfo, CellRangeAddress.valueOf(Utils.formatCellAddress(cell)), 1));
                    }
                    
                    try {
                        final Object value = converter.toObject(cell, property, config);
                        map.put(headerInfo.getLabel(), value);
                    } catch(TypeBindException e) {
                        e.setBindClass(itemClass);  // マップの項目のタイプに変更
                        work.addTypeBindError(e, cell, String.format("%s[%s]", property.getName(), headerInfo.getLabel()), headerInfo.getLabel());     
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                }
            }
            
            property.setValue(record, map);
        }
    }
    
    @SuppressWarnings("unchecked")
    private int loadNestedRecords(final Sheet sheet, final List<RecordHeader> headers, final List<MergedRecord> mergedRecords,
            final XlsVerticalRecords anno,
            final CellAddress beginPosition, 
            final Object record,
            final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        // 読み飛ばす、レコード数。
        // 基本的に結合している個数による。
        int skipSize = 0;
        
        final List<FieldAdaptor> nestedProperties = Utils.getLoadingNestedRecordsProperties(record.getClass(), work.getAnnoReader());
        for(FieldAdaptor property : nestedProperties) {
            
            final XlsNestedRecords nestedAnno = property.getLoadingAnnotation(XlsNestedRecords.class);
            final Class<?> clazz = property.getTargetClass();
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many
                
                int mergedSize = RecordsProcessorUtil.checkNestedMergedSizeRecords(sheet, mergedRecords);
                if(skipSize < mergedSize) {
                    skipSize = mergedSize;
                }
                
                Class<?> recordClass = nestedAnno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getLoadingGenericClassType();
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
                    recordClass = property.getLoadingGenericClassType();
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
                    recordClass = property.getTargetClass();
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
     * レコードの値か空かどうか判定する。
     * <p>アノテーション<code>@XlsIsEmpty</code>のメソッドで判定を行う。
     * @param adaptor
     * @param record
     * @param annoReader
     * @return アノテーションがない場合はfalseを返す。
     * @throws AnnotationReadException 
     * @throws AnnotationInvalidException 
     */
    private boolean isEmptyRecord(final FieldAdaptor adaptor, final Object record, final AnnotationReader annoReader) throws AnnotationReadException, AnnotationInvalidException {
        
        for(Method method : record.getClass().getMethods()) {
            final XlsIsEmpty emptyAnno = annoReader.getAnnotation(record.getClass(), method, XlsIsEmpty.class);
            if(emptyAnno == null) {
                continue;
            }
            
            try {
                method.setAccessible(true);
                return (boolean) method.invoke(record);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new AnnotationInvalidException(
                        String.format("With '%s', @XlsIsEmpty should be appended method that no args and returning boolean type.",
                                adaptor.getNameWithClass()), emptyAnno);
            }
        }
        
        // メソッドが見つからない場合。
        return false;
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsVerticalRecords anno,
            final FieldAdaptor adaptor, final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        // ラベルの設定
        if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                final Cell tableLabelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
                Utils.setLabel(POIUtils.getCellContents(tableLabelCell, config.getCellFormatter()), beansObj, adaptor.getName());
            } catch(CellNotFoundException e) {
                
            }
        }
        
        final Class<?> clazz = adaptor.getTargetClass();
        final Object result = adaptor.getValue(beansObj);
        if(Collection.class.isAssignableFrom(clazz)) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getSavingGenericClassType();
            }
            
            final Collection<Object> value = (result == null ? new ArrayList<Object>() : (Collection<Object>) result);
            final List<Object> list = Utils.convertCollectionToList(value);
            saveRecords(sheet, anno, adaptor, recordClass, list, config, work);
            
        } else if(clazz.isArray()) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getSavingGenericClassType();
            }
            
            final List<Object> list = (result == null ? new ArrayList<Object>() : Arrays.asList((Object[]) result));
            saveRecords(sheet, anno, adaptor, recordClass, list, config, work);
            
        } else {
            throw new AnnotationInvalidException(
                    String.format("With '%s', '@XlsVerticalRecords' should only granted Collection(List/Set) or array. : %s", 
                            adaptor.getNameWithClass(), clazz.getName()), anno);
        }
        
    }
    
    private void saveRecords(final Sheet sheet, XlsVerticalRecords anno, final FieldAdaptor adaptor, 
            final Class<?> recordClass, final List<Object> result,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        RecordsProcessorUtil.checkSavingNestedRecordClass(recordClass, adaptor, work.getAnnoReader());
        
        // get table starting position
        final CellAddress initPosition = getHeaderPosition(sheet, anno, adaptor, config);
        if(initPosition == null) {
            return;
        }
        
        final int initColumn = initPosition.getColumn();
        final int initRow = initPosition.getRow();
        
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
        
        // XlsColumn(merged=true)の結合したセルの情報
        final List<CellRangeAddress> mergedRanges = new ArrayList<CellRangeAddress>();
        
        // 書き込んだセルの範囲などの情報
        final RecordOperation recordOperation = new RecordOperation();
        recordOperation.setupCellPositoin(initRow, hColumn+1);
        
        // データ行の開始位置の調整
        hColumn += anno.headerRight();
        
        saveRecords(sheet, headers,
                anno,
                new CellAddress(initRow, hColumn), new AtomicInteger(0),
                adaptor, recordClass, result,
                config, work,
                mergedRanges, recordOperation);
        
        // 書き込むデータがない場合は、1行目の終端を操作範囲とする。
        if(result.isEmpty()) {
            recordOperation.setupCellPositoin(hRow-1, hColumn-2);
        }
        
        if(config.isCorrectCellDataValidationOnSave()) {
            correctDataValidation(sheet, recordOperation);
        }
        
        if(config.isCorrectNameRangeOnSave()) {
            correctNameRange(sheet, recordOperation);
        }
    }
    
    private void saveRecords(final Sheet sheet, final List<RecordHeader> headers,
            final XlsVerticalRecords anno,
            final CellAddress initPosition, final AtomicInteger nestedRecordSize,
            final FieldAdaptor adaptor, final Class<?> recordClass, final List<Object> result,
            final XlsMapperConfig config, final SavingWorkObject work,
            final List<CellRangeAddress> mergedRanges, final RecordOperation recordOperation) throws XlsMapperException {

        final int initColumn = initPosition.getColumn();
        final int initRow = initPosition.getRow();
        
        int hColumn = initColumn;
        
        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader(), config);
        
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
        
        final int startHeaderIndex = getStartHeaderIndexForSaving(headers, recordClass, work.getAnnoReader(), config);
        
        // get records
        for(int r=0; r < POIUtils.getColumns(sheet); r++) {
            
            boolean emptyFlag = true;
            
            // 書き込むレコードのオブジェクトを取得。データが0件の場合、nullとなる。
            Object record = null;
            if(r < result.size()) {
                record = result.get(r);
            }
            
            // パスの位置の変更
            work.getErrors().pushNestedPath(adaptor.getName(), r);
            
            if(record != null) {
             // execute PreProcess/ listner
                final XlsListener listenerAnno = work.getAnnoReader().getAnnotation(record.getClass(), XlsListener.class);
                if(listenerAnno != null) {
                    Object listenerObj = config.createBean(listenerAnno.listenerClass());
                    for(Method method : listenerObj.getClass().getMethods()) {
                        final XlsPreSave preProcessAnno = work.getAnnoReader().getAnnotation(listenerAnno.listenerClass(), method, XlsPreSave.class);
                        if(preProcessAnno != null) {
                            Utils.invokeNeedProcessMethod(listenerObj, method, record, sheet, config, work.getErrors());
                        }
                    }
                }
                
                // execute PreProcess/PostProcess method
                for(Method method : record.getClass().getMethods()) {
                    final XlsPreSave preProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPreSave.class);
                    if(preProcessAnno != null) {
                        Utils.invokeNeedProcessMethod(record, method, record, sheet, config, work.getErrors());                    
                    }
                }
            }
            
//            // レコードの各列処理で既に行を追加したかどうかのフラグ。
//            boolean insertRows = false;
            
//            // レコードの各列処理で既に行を削除したかどうかのフラグ。
//            boolean deleteRows = false;
            
            
            // 書き込んだセルの座標
            // ネストしたときに、結合するための情報として使用する。
            List<CellAddress> valueCellPositions = new ArrayList<>();
            
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
                    if(format!=null && !(format.getBorderTop() == CellStyle.BORDER_NONE)){
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
                    final List<FieldAdaptor> propeties = Utils.getSavingColumnProperties(
                            record.getClass(), headerInfo.getLabel(), work.getAnnoReader(), config);
                    for(FieldAdaptor property : propeties) {
                        Cell valueCell = cell;
                        final XlsColumn column = property.getSavingAnnotation(XlsColumn.class);
                        
                        //TODO: マージを考慮する必要はないかも
                        if(column.headerMerged() > 0) {
                            hRow = hRow + column.headerMerged();
                            valueCell = POIUtils.getCell(sheet, hColumn, hRow);
                        }
                        
                        // for merged cell
                        if(POIUtils.isEmptyCellContents(valueCell, config.getCellFormatter())) {
                            final CellStyle valueCellFormat = valueCell.getCellStyle();
                            if(column.merged()
                                    && (valueCellFormat == null || valueCellFormat.getBorderRight() == CellStyle.BORDER_NONE)){
                                for(int k=hColumn-1; k > hColumn; k--){
                                    Cell tmpCell = POIUtils.getCell(sheet, k, hRow);
                                    final CellStyle tmpCellFormat = tmpCell.getCellStyle();
                                    if(tmpCellFormat!=null && !(tmpCellFormat.getBorderLeft() == CellStyle.BORDER_NONE)){
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
                            if(anno.overRecord().equals(OverRecordOperate.Break)) {
                                break;
                                
                            } else if(anno.overRecord().equals(OverRecordOperate.Copy)) {
                                // 1つ左のセルの書式をコピーする。
                                final CellStyle style = POIUtils.getCell(sheet, valueCell.getColumnIndex()-1, valueCell.getRowIndex()).getCellStyle();
                                valueCell.setCellStyle(style);
                                valueCell.setCellType(Cell.CELL_TYPE_BLANK);
                                
                                // セル幅の調整
                                sheet.setColumnWidth(valueCell.getColumnIndex(), sheet.getColumnWidth(valueCell.getColumnIndex()-1));
                                
                                recordOperation.incrementCopyRecord();
                                
                            } else if(anno.overRecord().equals(OverRecordOperate.Insert)) {
                                // POIは列の追加をサポートしていないので非対応。
                                throw new AnnotationInvalidException(String.format("With '%s', XlsVerticalRecoreds#overRecord not supported 'OverRecordOperate.Insert'.",
                                        adaptor.getNameWithClass()), anno);
                            }
                            
                        }
                        
                        valueCellPositions.add(new CellAddress(valueCell));
                        
                        // set for cell value
                        Utils.setPosition(valueCell.getColumnIndex(), valueCell.getRowIndex(), record, property.getName());
                        Utils.setLabel(headerInfo.getLabel(), record, property.getName());
                        final CellConverter converter = getSavingCellConverter(property, config.getConverterRegistry(), config);
                        try {
                            converter.toCell(property, property.getValue(record), record, sheet, valueCell.getColumnIndex(), valueCell.getRowIndex(), config);
                        } catch(TypeBindException e) {
                            work.addTypeBindError(e, valueCell, property.getName(), headerInfo.getLabel());
                            if(!config.isContinueTypeBindFailure()) {
                                throw e;
                            }
                        }
                        
                        recordOperation.setupCellPositoin(valueCell);
                        
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
                    if(anno.remainedRecord().equals(RemainedRecordOperate.None)) {
                        // なにもしない
                        
                    } else if(anno.remainedRecord().equals(RemainedRecordOperate.Clear)) {
                        Cell clearCell = POIUtils.getCell(sheet, hColumn, hRow);
                        clearCell.setCellType(Cell.CELL_TYPE_BLANK);
                        
                    } else if(anno.remainedRecord().equals(RemainedRecordOperate.Delete)) {
                        // POIは列の削除をサポートしていないので非対応。
                        throw new AnnotationInvalidException("XlsVerticalRecoreds#remainedRecord not supported 'RemainedRecordOperate.Delete'.", anno);
                    }
                }
                
            }
            
            // マップ形式のカラムを出力する
            if(record != null) {
                saveMapColumn(sheet, headers, valueCellPositions, new CellAddress(initRow, hColumn), record, terminal, anno, config, work, recordOperation);
            }
            
            // execute nested record.
            int skipSize = 0;
            if(record != null) {
                skipSize = saveNestedRecords(sheet, headers, valueCellPositions, anno, new CellAddress(initRow, hColumn), record,
                        config, work, mergedRanges, recordOperation);
                nestedRecordSize.addAndGet(skipSize);
            }
            
            if(record != null) {
                
                // set PostProcess listener
                final XlsListener listenerAnno = work.getAnnoReader().getAnnotation(record.getClass(), XlsListener.class);
                if(listenerAnno != null) {
                    Object listenerObj = config.createBean(listenerAnno.listenerClass());
                    for(Method method : listenerObj.getClass().getMethods()) {
                        
                        final XlsPostSave postProcessAnno = work.getAnnoReader().getAnnotation(listenerAnno.listenerClass(), method, XlsPostSave.class);
                        if(postProcessAnno != null) {
                            work.addNeedPostProcess(new NeedProcess(record, listenerObj, method));
                        }
                    }
                }
                
                // set PostProcess method
                for(Method method : record.getClass().getMethods()) {
                    
                    final XlsPostSave postProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPostSave.class);
                    if(postProcessAnno != null) {
                        work.addNeedPostProcess(new NeedProcess(record, record, method));
                    }
                }
                
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
            final AnnotationReader annoReader, final XlsMapperConfig config) {
        
        // レコードクラスが不明の場合、0を返す。
        if((recordClass == null || recordClass.equals(Object.class))) {
            return 0;
        }
        
        for(int i=0; i < headers.size(); i++) {
            RecordHeader headerInfo = headers.get(i);
            final List<FieldAdaptor> propeties = Utils.getSavingColumnProperties(
                    recordClass, headerInfo.getLabel(), annoReader, config);
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
            final List<CellRangeAddress> mergedRanges, final XlsMapperConfig config) {
        
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
    
    private void saveMapColumn(final Sheet sheet, final List<RecordHeader> headers, final List<CellAddress> valueCellPositions,
            final CellAddress beginPosition, final Object record, final RecordTerminal terminal,
            final XlsVerticalRecords anno, final XlsMapperConfig config, final SavingWorkObject work,
            final RecordOperation recordOperation) throws XlsMapperException {
        
        final List<FieldAdaptor> properties = Utils.getSavingMapColumnProperties(record.getClass(), work.getAnnoReader());
        for(FieldAdaptor property : properties) {
            
            final XlsMapColumns mapAnno = property.getSavingAnnotation(XlsMapColumns.class);
            
            Class<?> itemClass = mapAnno.itemClass();
            if(itemClass == Object.class) {
                itemClass = property.getSavingGenericClassType();
            }
            
            // get converter (map key class)
            final CellConverter converter = config.getConverterRegistry().getConverter(itemClass);
            if(converter == null) {
                throw newNotFoundConverterExpcetion(itemClass);
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
                        CellStyle format = cell.getCellStyle();
                        if(format != null && !(format.getBorderTop() == CellStyle.BORDER_NONE)) {
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
                        if(anno.overRecord().equals(OverRecordOperate.Break)) {
                            break;
                            
                        } else if(anno.overRecord().equals(OverRecordOperate.Copy)) {
                            final CellStyle style = POIUtils.getCell(sheet, cell.getColumnIndex()-1, cell.getRowIndex()).getCellStyle();
                            cell.setCellStyle(style);
                            cell.setCellType(Cell.CELL_TYPE_BLANK);
                            
                        } else if(anno.overRecord().equals(OverRecordOperate.Insert)) {
                            // POIは列の追加をサポートしていないので非対応。
                            throw new AnnotationInvalidException(String.format("With '%s', @XlsVerticalRecoreds#overRecord not supported 'OverRecordOperate.Insert'.",
                                    property.getNameWithClass()), anno);
                            
                        }
                    }
                    
                    valueCellPositions.add(new CellAddress(cell));
                    
                    // セルの値を出力する
                    Utils.setPositionWithMapColumn(cell.getColumnIndex(), cell.getRowIndex(), record, property.getName(), headerInfo.getLabel());
                    Utils.setLabelWithMapColumn(headerInfo.getLabel(), record, property.getName(), headerInfo.getLabel());
                    try {
                        Object itemValue = property.getValueOfMap(headerInfo.getLabel(), record);
                        converter.toCell(property, itemValue, record, sheet, cell.getColumnIndex(), cell.getRowIndex(), config);
                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, cell, String.format("%s[%s]", property.getName(), headerInfo.getLabel()), headerInfo.getLabel());
                        if(!config.isContinueTypeBindFailure()) {
                            throw e;
                        }
                    }
                    
                    recordOperation.setupCellPositoin(cell);
                }
                
            }
        }
        
    }
    
    @SuppressWarnings("unchecked")
    private int saveNestedRecords(final Sheet sheet, final List<RecordHeader> headers, final List<CellAddress> valueCellPositions,
            final XlsVerticalRecords anno,
            final CellAddress beginPositoin,
            final Object record,
            final XlsMapperConfig config, final SavingWorkObject work,
            final List<CellRangeAddress> mergedRanges, final RecordOperation recordOperation) throws XlsMapperException {
        
        int skipSize = 0;
        
        final List<FieldAdaptor> nestedProperties = Utils.getSavingNestedRecordsProperties(record.getClass(), work.getAnnoReader());
        for(FieldAdaptor property : nestedProperties) {
            
            final XlsNestedRecords nestedAnno = property.getSavingAnnotation(XlsNestedRecords.class);
            final Class<?> clazz = property.getTargetClass();
            if(Collection.class.isAssignableFrom(clazz)) {
                // mapping by one-to-many
                
                Class<?> recordClass = nestedAnno.recordClass();
                if(recordClass == Object.class) {
                    recordClass = property.getSavingGenericClassType();
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
                    recordClass = property.getSavingGenericClassType();
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
                    recordClass = property.getTargetClass();
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
            final List<CellAddress> valueCellPositions) {
        
        // ネストした場合、上のセルのスタイルをコピーして、結合する
        for(CellAddress position : valueCellPositions) {
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
                mergedCell.setCellType(Cell.CELL_TYPE_BLANK);
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
        
        if(!POIUtils.AVAILABLE_METHOD_SHEET_DAVA_VALIDATION) {
            return;
        }
        
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
            
            AreaReference areaRef = new AreaReference(name.getRefersToFormula());
            CellReference firstCellRef = areaRef.getFirstCell();
            CellReference lastCellRef = areaRef.getLastCell();
            
            if(notOperateRange.isInRange(firstCellRef.getRow(), firstCellRef.getCol())) {
                // 自身のセルの範囲の場合は、行の範囲を広げる。
                
                lastCellRef= new CellReference(
                        lastCellRef.getSheetName(),
                        lastCellRef.getRow(), recordOperation.getBottomRightPosition().x,
                        lastCellRef.isRowAbsolute(), lastCellRef.isColAbsolute());
                areaRef = new AreaReference(firstCellRef, lastCellRef);
                
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
