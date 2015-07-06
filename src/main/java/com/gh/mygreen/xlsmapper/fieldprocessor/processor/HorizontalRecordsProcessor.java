package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import java.awt.Point;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.CellCommentStore;
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
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsIsEmpty;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.cellconvert.CellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordHeader;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordsProcessorUtil;
import com.gh.mygreen.xlsmapper.xml.AnnotationReadException;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * アノテーション{@link XlsHorizontalRecords}を処理するクラス。
 * 
 * @version 0.5
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class HorizontalRecordsProcessor extends AbstractFieldProcessor<XlsHorizontalRecords> {
    
    private static Logger logger = LoggerFactory.getLogger(HorizontalRecordsProcessor.class);
    
    @Override
    public void loadProcess(final Sheet sheet, final Object obj, final XlsHorizontalRecords anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        // ラベルの設定
        if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                final Cell tableLabelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
                Utils.setLabel(POIUtils.getCellContents(tableLabelCell, config.getCellFormatter()), obj, adaptor.getName());
            } catch(CellNotFoundException e) {
                
            }
        }
        
        final Class<?> clazz = adaptor.getTargetClass();
        if(List.class.isAssignableFrom(clazz)) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getLoadingGenericClassType();
            }
            
            final List<?> value = loadRecords(sheet, anno, adaptor, recordClass, config, work);
            if(value != null) {
                adaptor.setValue(obj, value);
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
                
                adaptor.setValue(obj, array);
            }
            
        } else {
            throw new AnnotationInvalidException(
                    String.format("Annotation '@%s' should only granted List or Array. : %s", 
                            XlsHorizontalRecords.class.getSimpleName(), clazz.getName()),
                            anno);
        }
        
    }
    
    private List<?> loadRecords(final Sheet sheet, XlsHorizontalRecords anno, final FieldAdaptor adaptor, 
            final Class<?> recordClass, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final List<Object> result = new ArrayList<>();
        final List<RecordHeader> headers = new ArrayList<>();
        
        // get header
        final Point initPosition = getHeaderPosition(sheet, anno, config);
        if(initPosition == null) {
            return null;
        }
        
        int initColumn = initPosition.x;
        int initRow = initPosition.y;
        
        int hColumn = initColumn;
        int hRow = initRow;
        int rangeCount = 1;
        
        while(true) {
            try {
                Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                
                while(POIUtils.isEmptyCellContents(cell, config.getCellFormatter()) && rangeCount < anno.range()) {
                    cell = POIUtils.getCell(sheet, hColumn + rangeCount, hRow);
                    rangeCount++;
                }
                
                final String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(Utils.isEmpty(cellValue)){
                    break;
                }
                
                headers.add(new RecordHeader(cellValue, rangeCount - 1));
                hColumn = hColumn + rangeCount;
                rangeCount = 1;
                
                // 結合しているセルの場合は、はじめのセルだけ取得して、後は結合分スキップする。
                CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                if(mergedRange != null) {
                    hColumn = hColumn + (mergedRange.getLastColumn() - mergedRange.getFirstColumn());
                }
                
            } catch(ArrayIndexOutOfBoundsException ex) {
                break;
            }
            
            if(anno.headerLimit() > 0 && headers.size() >= anno.headerLimit()){
                break;
            }
        }
        
        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader());
        
        RecordTerminal terminal = anno.terminal();
        if(terminal == null){
            terminal = RecordTerminal.Empty;
        }
        
        final int startHeaderIndex = getStartHeaderIndex(headers, recordClass, work);
        
        // get records
        hRow++;
        while(hRow < POIUtils.getRows(sheet)){
            hColumn = initColumn;
            boolean emptyFlag = true;
            // recordは、マッピング先のオブジェクトのインスタンス。
            final Object record = config.createBean(recordClass);
            
            // パスの位置の変更
            work.getErrors().pushNestedPath(adaptor.getName(), result.size());
            
            // set PreProcess method
            for(Method method : record.getClass().getMethods()) {
                final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPreLoad.class);
                if(preProcessAnno != null) {
                    Utils.invokeNeedProcessMethod(method, record, sheet, config, work.getErrors());
                }
            }
            
            loadMapColumns(sheet, headers, hColumn, hRow, record, config, work);
            
            for(int i=0; i < headers.size() && hRow < POIUtils.getRows(sheet); i++){
                final RecordHeader headerInfo = headers.get(i);
                hColumn = hColumn + headerInfo.getHeaderRange();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                
                // find end of the table
                if(!POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    emptyFlag = false;
                }
                
                if(terminal == RecordTerminal.Border && i==startHeaderIndex){
                    final CellStyle format = cell.getCellStyle();
                    if(format != null && !(format.getBorderLeft() == CellStyle.BORDER_NONE)){
                        emptyFlag = false;
                    } else {
                        emptyFlag = true;
                        break;
                    }
                }
                
                if(!anno.terminateLabel().equals("")){
                    if(POIUtils.getCellContents(cell, config.getCellFormatter()).equals(anno.terminateLabel())){
                        emptyFlag = true;
                        break;
                    }
                }
                
                // mapping from Excel columns to Object properties.
                final List<FieldAdaptor> propeties = Utils.getLoadingColumnProperties(record.getClass(), headerInfo.getHeaderLabel(), work.getAnnoReader());
                for(FieldAdaptor property : propeties) {
                    Cell valueCell = cell;
                    final XlsColumn column = property.getLoadingAnnotation(XlsColumn.class);
                    if(column.headerMerged() > 0) {
                        hColumn = hColumn + column.headerMerged();
                        valueCell = POIUtils.getCell(sheet, hColumn, hRow);
                    }
                    
                    // for merged cell
                    if(POIUtils.isEmptyCellContents(valueCell, config.getCellFormatter())) {
                        final CellStyle valueCellFormat = valueCell.getCellStyle();
                        if(column.merged()
                                && (valueCellFormat == null || valueCellFormat.getBorderTop() == CellStyle.BORDER_NONE)) {
                            for(int k=hRow-1; k > initRow; k--){
                                Cell tmpCell = POIUtils.getCell(sheet, hColumn, k);
                                final CellStyle tmpCellFormat = tmpCell.getCellStyle();
                                if(tmpCellFormat!=null && !(tmpCellFormat.getBorderBottom() == CellStyle.BORDER_NONE)){
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
                        hColumn = hColumn - column.headerMerged();
                    }
                    
                    // set for value
                    Utils.setPosition(valueCell.getColumnIndex(), valueCell.getRowIndex(), record, property.getName());
                    Utils.setLabel(headerInfo.getHeaderLabel(), record, property.getName());
                    final CellConverter<?> converter = getLoadingCellConverter(property, config.getConverterRegistry());
                    try {
                        final Object value = converter.toObject(valueCell, property, config);
                        property.setValue(record, value);
                    } catch(TypeBindException e) {
                        work.addTypeBindError(e, valueCell, property.getName(), headerInfo.getHeaderLabel());
                        if(!config.isSkipTypeBindFailure()) {
                            throw e;
                        }
                    }
                }
                
                hColumn++;
                
            }
            
            if(emptyFlag){
                // パスの位置の変更
                work.getErrors().popNestedPath();
                break;
            }
            
            if(!anno.skipEmptyRecord() || !isEmptyRecord(record, work.getAnnoReader())) {
                result.add(record);
                
            }
            
            // set PostProcess method
            for(Method method : record.getClass().getMethods()) {
                final XlsPostLoad postProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPostLoad.class);
                if(postProcessAnno != null) {
                    work.addNeedPostProcess(new NeedProcess(record, method));
                }
            }
            
            // パスの位置の変更
            work.getErrors().popNestedPath();
            
            hRow++;
        }
        
        return result;
    }
    
    /**
     * 表の開始位置（見出し）の位置情報を取得する。
     * 
     * @param sheet
     * @param anno
     * @param config
     * @return Point.y = row, Point.x = column
     * @throws XlsMapperException 
     */
    private Point getHeaderPosition(final Sheet sheet, final XlsHorizontalRecords anno,
            final XlsMapperConfig config) throws XlsMapperException {
        
        //TODO: AnnotationInvalidExceptionのメッセージは、フィールド、メソッド名も出力する。
        
        if(Utils.isNotEmpty(anno.headerAddress())) {
            final Point address = Utils.parseCellAddress(anno.headerAddress());
            if(address == null) {
                throw new AnnotationInvalidException(
                        String.format("@XlsHorizontalRecors#headerAddress is wrong cell address '%s'.", anno.headerAddress()), anno);
            }
            
            return address;
            
        } else if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                Cell labelCell = Utils.getCell(sheet, anno.tableLabel(), 0, 0, config);
                int initColumn = labelCell.getColumnIndex();
                int initRow = labelCell.getRowIndex() + anno.bottom();
                
                return new Point(initColumn, initRow);
                
            } catch(CellNotFoundException ex) {
                if(anno.optional()) {
                    return null;
                } else {
                    throw ex;
                }
            }
            
        } else {
            // column, rowのアドレスを直接指定の場合
            if(anno.headerColumn() < 0 || anno.headerRow() < 0) {
                throw new AnnotationInvalidException(
                        String.format("@XlsHorizontalRecors#headerColumn or headerRow soulde be greather than or equal zero. (headerColulmn=%d, headerRow=%d)",
                                anno.headerColumn(), anno.headerRow()), anno);
            }
            
            return new Point(anno.headerColumn(), anno.headerRow());
        }
        
    }
    
    /**
     * 表の見出しから、レコードのJavaクラスの定義にあるカラムの定義で初めて見つかるリストのインデックスを取得する。
     * ・カラムの定義とは、アノテーション「@XlsColumn」が付与されたもの。
     * @param headers
     * @param recordClass
     * @param work
     * @return
     */
    private int getStartHeaderIndex(List<RecordHeader> headers, Class<?> recordClass, LoadingWorkObject work) {
        
        // レコードクラスが不明の場合、0を返す。
        if((recordClass == null || recordClass.equals(Object.class))) {
            return 0;
        }
        
        for(int i=0; i < headers.size(); i++) {
            RecordHeader headerInfo = headers.get(i);
            final List<FieldAdaptor> propeties = Utils.getLoadingColumnProperties(recordClass, headerInfo.getHeaderLabel(), work.getAnnoReader());
            if(!propeties.isEmpty()) {
                return i;
            }
        }
        
        return 0;
        
    }
    
    private void loadMapColumns(Sheet sheet, List<RecordHeader> headerInfos, 
            int begin, int row, Object record, XlsMapperConfig config, LoadingWorkObject work) throws XlsMapperException {
        
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
            
            boolean flag = false;
            final Map<String, Object> map = new LinkedHashMap<>();
            for(RecordHeader headerInfo : headerInfos) {
                if(headerInfo.getHeaderLabel().equals(mapAnno.previousColumnName())){
                    flag = true;
                    begin++;
                    continue;
                }
                
                if(flag){
                    final Cell cell = POIUtils.getCell(sheet, begin + headerInfo.getHeaderRange(), row);
                    Utils.setPositionWithMapColumn(cell.getColumnIndex(), cell.getRowIndex(), record, property.getName(), headerInfo.getHeaderLabel());
                    Utils.setLabelWithMapColumn(headerInfo.getHeaderLabel(), record, property.getName(), headerInfo.getHeaderLabel());
                    
                    try {
                        final Object value = converter.toObject(cell, property, config);
                        map.put(headerInfo.getHeaderLabel(), value);
                    } catch(TypeBindException e) {
                        e.setBindClass(itemClass);  // マップの項目のタイプに変更
                        work.addTypeBindError(e, cell, String.format("%s[%s]", property.getName(), headerInfo.getHeaderLabel()), headerInfo.getHeaderLabel());
                        if(!config.isSkipTypeBindFailure()) {
                            throw e;
                        } 
                    }
                }
                begin = begin + headerInfo.getHeaderRange() + 1;
            }
            
            property.setValue(record, map);
        }
    }
    
    /**
     * レコードの値か空かどうか判定する。
     * <p>アノテーション<code>@XlsIsEmpty</code>のメソッドで判定を行う。
     * @param record
     * @param annoReader
     * @return アノテーションがない場合はfalseを返す。
     * @throws AnnotationReadException 
     * @throws AnnotationInvalidException 
     */
    private boolean isEmptyRecord(final Object record, final AnnotationReader annoReader) throws AnnotationReadException, AnnotationInvalidException {
        
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
                        String.format("@XlsIsEmpty should be appended method that no args and returning boolean type."),
                        emptyAnno);
            }
        }
        
        // メソッドが見つからない場合。
        return false;
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object obj, final XlsHorizontalRecords anno,
            final FieldAdaptor adaptor, final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        // ラベルの設定
        if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                final Cell tableLabelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
                Utils.setLabel(POIUtils.getCellContents(tableLabelCell, config.getCellFormatter()), obj, adaptor.getName());
            } catch(CellNotFoundException e) {
                
            }
        }
        
        final Class<?> clazz = adaptor.getTargetClass();
        final Object result = adaptor.getValue(obj);
        if(List.class.isAssignableFrom(clazz)) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getSavingGenericClassType();
            }
            
            final List<Object> list = (result == null ? new ArrayList<Object>() : (List<Object>) result);
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
                    String.format("Annotation '@%s' should only granted List or Array. : %s", 
                            XlsHorizontalRecords.class.getSimpleName(), clazz.getName()),
                            anno);
        }
        
    }
    
    /**
     * 表の見出しから、レコードのJavaクラスの定義にあるカラムの定義で初めて見つかるリストのインデックスを取得する。
     * ・カラムの定義とは、アノテーション「@XlsColumn」が付与されたもの。
     * @param headers
     * @param recordClass
     * @param work
     * @return
     */
    private int getStartHeaderIndex(List<RecordHeader> headers, final List<Object> result, Class<?> recordClass, SavingWorkObject work) {
        
        // レコードクラスが不明の場合、実際のリストオブジェクトの要素から取得する
        if((recordClass == null || recordClass.equals(Object.class)) && !result.isEmpty()) {
            recordClass = result.get(0).getClass();
           
        }
        
        for(int i=0; i < headers.size(); i++) {
            RecordHeader headerInfo = headers.get(i);
            final List<FieldAdaptor> propeties = Utils.getSavingColumnProperties(recordClass, headerInfo.getHeaderLabel(), work.getAnnoReader());
            if(!propeties.isEmpty()) {
                return i;
            }
        }
        
        return 0;
        
    }
    
    private void saveRecords(final Sheet sheet, final XlsHorizontalRecords anno, final FieldAdaptor adaptor, 
            final Class<?> recordClass, final List<Object> result, final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final List<RecordHeader> headers = new ArrayList<>();
        
        // get header
        final Point initPosition = getHeaderPosition(sheet, anno, config);
        if(initPosition == null) {
            return;
        }
        
        int initColumn = initPosition.x;
        int initRow = initPosition.y;
        
        // 見出しセルの取得する
        int hColumn = initColumn;
        int hRow = initRow;
        int rangeCount = 1;
        
        while(true) {
            try {
                Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                while(POIUtils.isEmptyCellContents(cell, config.getCellFormatter()) && rangeCount < anno.range()) {
                    cell = POIUtils.getCell(sheet, hColumn + rangeCount, hRow);
                    rangeCount++;
                }
                
                String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(Utils.isEmpty(cellValue)){
                    break;
                }
                
                headers.add(new RecordHeader(cellValue, rangeCount - 1));
                hColumn = hColumn + rangeCount;
                rangeCount = 1;
                
                // 結合しているセルの場合は、はじめのセルだけ取得して、後は結合分スキップする。
                CellRangeAddress mergedRange = POIUtils.getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
                if(mergedRange != null) {
                    hColumn = hColumn + (mergedRange.getLastColumn() - mergedRange.getFirstColumn());
                }
                
            } catch(ArrayIndexOutOfBoundsException ex) {
                break;
            }
            
            if(anno.headerLimit() > 0 && headers.size() >= anno.headerLimit()){
                break;
            }
        }
        
        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader());
        
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
        
        // 結合したセルの情報
        final List<CellRangeAddress> mergedRanges = new ArrayList<CellRangeAddress>();
        
        // 書き込んだセルの範囲などの情報
        final RecordOperation recordOperation = new RecordOperation();
        recordOperation.setupCellPositoin(hRow+1, initColumn);
        
        // コメントの補完
        final List<CellCommentStore> commentStoreList;
        if(config.isCorrectCellCommentOnSave()
                && (anno.overRecord().equals(OverRecordOperate.Insert) || anno.remainedRecord().equals(RemainedRecordOperate.Delete))) {
            commentStoreList = loadCommentAndRemove(sheet);
        } else {
            commentStoreList = new ArrayList<>();
        }
        
        final int startHeaderIndex = getStartHeaderIndex(headers, result, recordClass, work);
        
        // get records
        hRow++;
        for(int r=0; r < POIUtils.getRows(sheet); r++) {
            
            hColumn = initColumn;
            boolean emptyFlag = true;
            
            // 書き込むレコードのオブジェクトを取得。データが0件の場合、nullとなる。
            Object record = null;
            if(r < result.size()) {
                record = result.get(r);
            }
            
            // パスの位置の変更
            work.getErrors().pushNestedPath(adaptor.getName(), r);
            
            if(record != null) {
                
                // set PreProcess method
                for(Method method : record.getClass().getMethods()) {
                    final XlsPreSave preProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPreSave.class);
                    if(preProcessAnno != null) {
                        Utils.invokeNeedProcessMethod(method, record, sheet, config, work.getErrors());                    
                    }
                }
            }
            
            // レコードの各列処理で既に行を追加したかどうかのフラグ。
            boolean insertRows = false;
            
            // レコードの各列処理で既に行を削除したかどうかのフラグ。
            boolean deleteRows = false;
            
            // hRowという上限がない
            for(int i=0; i < headers.size(); i++) {
                final RecordHeader headerInfo = headers.get(i);
                hColumn = hColumn + headerInfo.getHeaderRange();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                
                // find end of the table
                if(!POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    emptyFlag = false;
                }
                
                if(terminal == RecordTerminal.Border && i==startHeaderIndex){
                    final CellStyle format = cell.getCellStyle();
                    if(format != null && !(format.getBorderLeft() == CellStyle.BORDER_NONE)){
                        emptyFlag = false;
                    } else {
                        emptyFlag = true;
//                            break;
                    }
                }
                
                if(!anno.terminateLabel().equals("")){
                    if(POIUtils.getCellContents(cell, config.getCellFormatter()).equals(anno.terminateLabel())){
                        emptyFlag = true;
//                            break;
                    }
                }
                
                // mapping from Excel columns to Object properties.
                if(record != null) {
                    final List<FieldAdaptor> propeties = Utils.getSavingColumnProperties(record.getClass(), headerInfo.getHeaderLabel(), work.getAnnoReader());
                    for(FieldAdaptor property : propeties) {
                        Cell valueCell = cell;
                        final XlsColumn column = property.getSavingAnnotation(XlsColumn.class);
                        
                        //TODO: マージを考慮する必要はないかも
                        if(column.headerMerged() > 0) {
                            hColumn = hColumn + column.headerMerged();
                            valueCell = POIUtils.getCell(sheet, hColumn, hRow);
                        }
                        
                        // for merged cell
                        if(POIUtils.isEmptyCellContents(valueCell, config.getCellFormatter())) {
                            final CellStyle valueCellFormat = valueCell.getCellStyle();
                            if(column.merged()
                                    && (valueCellFormat == null || valueCellFormat.getBorderTop() == CellStyle.BORDER_NONE)) {
                                for(int k=hRow-1; k > initRow; k--){
                                    Cell tmpCell = POIUtils.getCell(sheet, hColumn, k);
                                    final CellStyle tmpCellFormat = tmpCell.getCellStyle();
                                    if(tmpCellFormat != null && !(tmpCellFormat.getBorderBottom() == CellStyle.BORDER_NONE)){
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
                            hColumn = hColumn - column.headerMerged();
                        }
                        
                        // 書き込む行が足りない場合の操作
                        if(emptyFlag) {
                            if(anno.overRecord().equals(OverRecordOperate.Break)) {
                                break;
                                
                            } else if(anno.overRecord().equals(OverRecordOperate.Copy)) {
                                // 1つ上のセルの書式をコピーする。
                                final CellStyle style = POIUtils.getCell(sheet, valueCell.getColumnIndex(), valueCell.getRowIndex()-1).getCellStyle();
                                valueCell.setCellStyle(style);
                                valueCell.setCellType(Cell.CELL_TYPE_BLANK);
                                
                                recordOperation.incrementCopyRecord();
                                
                            } else if(anno.overRecord().equals(OverRecordOperate.Insert)) {
                                // すでに他の列の処理に対して行を追加している場合は行の追加は行わない。
                                if(!insertRows) {
                                    // 行を下に追加する
                                    POIUtils.insertRow(sheet, valueCell.getRowIndex()+1);
                                    insertRows = true;
                                    recordOperation.incrementInsertRecord();
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("insert row : sheet name=[{}], row index=[{}]", sheet.getSheetName(), valueCell.getRowIndex()+1);
                                    }
                                }
                                
                                // １つ上のセルの書式をコピーする
                                final CellStyle style = POIUtils.getCell(sheet, valueCell.getColumnIndex(), valueCell.getRowIndex()-1).getCellStyle();
                                valueCell.setCellStyle(style);
                                valueCell.setCellType(Cell.CELL_TYPE_BLANK);
                            }
                            
                        }
                        
                        // set for cell value
                        Utils.setPosition(valueCell.getColumnIndex(), valueCell.getRowIndex(), record, property.getName());
                        Utils.setLabel(headerInfo.getHeaderLabel(), record, property.getName());
                        final CellConverter<?> converter = getSavingCellConverter(property, config.getConverterRegistry());
                        try {
                            converter.toCell(property, record, sheet, valueCell.getColumnIndex(), valueCell.getRowIndex(), config);
                        } catch(TypeBindException e) {
                            work.addTypeBindError(e, valueCell, property.getName(), headerInfo.getHeaderLabel());
                            if(!config.isSkipTypeBindFailure()) {
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
                        final Cell clearCell = POIUtils.getCell(sheet, hColumn, hRow);
                        clearCell.setCellType(Cell.CELL_TYPE_BLANK);
                        
                    } else if(anno.remainedRecord().equals(RemainedRecordOperate.Delete)) {
                        if(initRow == hRow -1) {
                            // 1行目は残しておき、値をクリアする
                            final Cell clearCell = POIUtils.getCell(sheet, hColumn, hRow);
                            clearCell.setCellType(Cell.CELL_TYPE_BLANK);
                            
                        } else if(!deleteRows) {
                            // すでに他の列の処理に対して行を追加している場合は行の追加は行わない。
                            final Row row = POIUtils.removeRow(sheet, hRow);
                            deleteRows = true;
                            if(row != null) {
                                if(logger.isDebugEnabled()) {
                                    logger.debug("delete row : sheet name=[{}], row index=[{}]", sheet.getSheetName(), hRow);
                                }
                                recordOperation.incrementDeleteRecord();
                            }
                        }
                    }
                }
                
                hColumn++;
            }
            
            // マップ形式のカラムを出力する
            if(record != null) {
                saveMapColumn(sheet, headers, initColumn, hRow, record, terminal, anno, config, work, recordOperation);
            }
            
            if(record != null) {
                // set PostProcess method
                for(Method method : record.getClass().getMethods()) {
                    final XlsPostSave postProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPostSave.class);
                    if(postProcessAnno != null) {
                        work.addNeedPostProcess(new NeedProcess(record, method));
                    }
                }
            }
            
            // パスの位置の変更
            work.getErrors().popNestedPath();
            
            /*
             * 行が削除されていない場合は、次の行に進む。
             * ・行が削除されていると、現在の行数は変わらない。
             */
            if(!deleteRows) {
                hRow++;
            }
            
            if(emptyFlag == true && (r > result.size())) {
                // セルが空で、書き込むデータがない場合。
                break;
            }
        }
        
        // 書き込むデータがない場合は、1行目の終端を操作範囲とする。
        if(result.isEmpty()) {
            recordOperation.setupCellPositoin(hRow-2, hColumn-1);
        }
        
        if(config.isCorrectCellDataValidationOnSave()) {
            correctDataValidation(sheet, recordOperation);
        }
        
        if(config.isCorrectNameRangeOnSave()) {
            correctNameRange(sheet, recordOperation);
        }
        
        if(config.isCorrectCellCommentOnSave()) {
            correctComment(sheet, recordOperation, commentStoreList);
        }
        
    }
    
    /**
     * 上部のセルと同じ値の場合マージする
     * @param currentCell
     * @param sheet
     * @param mergedRanges
     * @param config
     * @return
     */
    private boolean processSavingMergedCell(final Cell currentCell, final Sheet sheet,
            final List<CellRangeAddress> mergedRanges, final XlsMapperConfig config) {
        
        final int row = currentCell.getRowIndex();
        final int column = currentCell.getColumnIndex();
        
        if(row <= 0) {
            return false;
        }
        
        // 上のセルと比較する
        final String value = POIUtils.getCellContents(currentCell, config.getCellFormatter());
        String upperValue = POIUtils.getCellContents(POIUtils.getCell(sheet, column, row-1), config.getCellFormatter());
        
        // 結合されている場合、結合の先頭セルを取得する
        int startRow = row - 1;
        CellRangeAddress currentMergedRange = null;
        for(CellRangeAddress range : mergedRanges) {
            // 列が範囲外の場合
            if((range.getFirstColumn() > column) || (column > range.getLastColumn())) {
                continue;
            }
            
            // 行が範囲外の場合
            if((range.getFirstRow() > startRow) || (startRow > range.getLastRow())) {
                continue;
            }
            
            upperValue = POIUtils.getCellContents(POIUtils.getCell(sheet, column, range.getFirstRow()), config.getCellFormatter());
            currentMergedRange = range;
            break;
        }
        
        if(!value.equals(upperValue)) {
            // 値が異なる場合は結合しない
            return false;
        }
        
        // 既に結合済みの場合は一端解除する
        if(currentMergedRange != null) {
            startRow = currentMergedRange.getFirstRow();
            mergedRanges.remove(currentMergedRange);
            POIUtils.removeMergedRange(sheet, currentMergedRange);
        }
        
        final CellRangeAddress newRange = POIUtils.mergeCells(sheet, column, startRow, column, row);
        mergedRanges.add(newRange);
        return true;
        
    }
    
    private void saveMapColumn(Sheet sheet, List<RecordHeader> headerInfos, 
            int begin, int row, Object record, RecordTerminal terminal,
            XlsHorizontalRecords anno, XlsMapperConfig config, SavingWorkObject work,
            RecordOperation recordOperation) throws XlsMapperException {
        
        final List<FieldAdaptor> properties = Utils.getSavingMapColumnProperties(record.getClass(), work.getAnnoReader());
        for(FieldAdaptor property : properties) {
            
            final XlsMapColumns mapAnno = property.getSavingAnnotation(XlsMapColumns.class);
            
            Class<?> itemClass = mapAnno.itemClass();
            if(itemClass == Object.class) {
                itemClass = property.getSavingGenericClassType();
            }
            
            // get converter (map key class)
            final CellConverter<?> converter = config.getConverterRegistry().getConverter(itemClass);
            if(converter == null) {
                throw newNotFoundConverterExpcetion(itemClass);
            }
            
            boolean flag = false;
            for(RecordHeader headerInfo : headerInfos) {
                if(headerInfo.getHeaderLabel().equals(mapAnno.previousColumnName())){
                    flag = true;
                    begin++;
                    continue;
                }
                
                if(flag) {
                    final Cell cell = POIUtils.getCell(sheet, begin + headerInfo.getHeaderRange(), row);
                    
                    // 空セルか判断する
                    boolean emptyFlag = true;
                    if(terminal == RecordTerminal.Border) {
                        CellStyle format = cell.getCellStyle();
                        if(format != null && !(format.getBorderLeft() == CellStyle.BORDER_NONE)) {
                            emptyFlag = false;
                        } else {
                            emptyFlag = true;
                        }
                    }
                    
                    if(!anno.terminateLabel().equals("")) {
                        if(POIUtils.getCellContents(cell, config.getCellFormatter()).equals(anno.terminateLabel())) {
                            emptyFlag = true;
                        }
                    }
                    
                    // 空セルの場合
                    if(emptyFlag) {
                        if(anno.overRecord().equals(OverRecordOperate.Break)) {
                            break;
                            
                        } else if(anno.overRecord().equals(OverRecordOperate.Copy)) {
                            final CellStyle style = POIUtils.getCell(sheet, cell.getColumnIndex(), cell.getRowIndex()-1).getCellStyle();
                            cell.setCellStyle(style);
                            cell.setCellType(Cell.CELL_TYPE_BLANK);
                            
                        } else if(anno.overRecord().equals(OverRecordOperate.Insert)) {
                            // 既に追加ずみなので、セルの書式のコピーのみ行う
                            final CellStyle style = POIUtils.getCell(sheet, cell.getColumnIndex(), cell.getRowIndex()-1).getCellStyle();
                            cell.setCellStyle(style);
                            cell.setCellType(Cell.CELL_TYPE_BLANK);
                            
                            
                        }
                    }
                    
                    // セルの値を出力する
                    Utils.setPositionWithMapColumn(cell.getColumnIndex(), cell.getRowIndex(), record, property.getName(), headerInfo.getHeaderLabel());
                    Utils.setLabelWithMapColumn(headerInfo.getHeaderLabel(), record, property.getName(), headerInfo.getHeaderLabel());
                    try {
                        converter.toCellWithMap(property, headerInfo.getHeaderLabel(), record, sheet, cell.getColumnIndex(), cell.getRowIndex(), config);
                        
                    } catch(TypeBindException e) {
                        
                        work.addTypeBindError(e, cell, String.format("%s[%s]", property.getName(), headerInfo.getHeaderLabel()), headerInfo.getHeaderLabel());
                        if(!config.isSkipTypeBindFailure()) {
                            throw e;
                        }
                    }
                    
                    recordOperation.setupCellPositoin(cell);
                }
                
                begin = begin + headerInfo.getHeaderRange() + 1;
            }
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
                recordOperation.getBottomRightPosition().y - recordOperation.getCountInsertRecord(),
                recordOperation.getTopLeftPoisitoin().x,
                recordOperation.getBottomRightPosition().x
                );
        
        final List<? extends DataValidation> list = sheet.getDataValidations();
        for(DataValidation validation : list) {
            
            final CellRangeAddressList region = validation.getRegions().copy();
            boolean changedRange = false;
            for(CellRangeAddress range : region.getCellRangeAddresses()) {
                
                if(notOperateRange.isInRange(range.getFirstRow(), range.getFirstColumn())) {
                    // 自身のセルの範囲の場合は、行の範囲を広げる
                    range.setLastRow(recordOperation.getBottomRightPosition().y);
                    changedRange = true;
                    
                } else if(notOperateRange.getLastRow() < range.getFirstRow()) {
                    // 自身のセルの範囲より下方にあるセルの範囲の場合、行の挿入や削除に影響を受けているので修正する。
                    if(recordOperation.isInsertRecord()) {
                        range.setFirstRow(range.getFirstRow() + recordOperation.getCountInsertRecord());
                        range.setLastRow(range.getLastRow() + recordOperation.getCountInsertRecord());
                        
                    } else if(recordOperation.isDeleteRecord()) {
                        range.setFirstRow(range.getFirstRow() - recordOperation.getCountDeleteRecord());
                        range.setLastRow(range.getLastRow() - recordOperation.getCountDeleteRecord());
                        
                    }
                    changedRange = true;
                }
                
            }
            
            // 修正した規則を、更新する。
            if(changedRange) {
                boolean updated = POIUtils.updateDataValidationRegion(sheet, validation.getRegions(), region);
                assert updated;
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
                recordOperation.getBottomRightPosition().y - recordOperation.getCountInsertRecord(),
                recordOperation.getTopLeftPoisitoin().x,
                recordOperation.getBottomRightPosition().x
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
                        recordOperation.getBottomRightPosition().y, lastCellRef.getCol(),
                        lastCellRef.isRowAbsolute(), lastCellRef.isColAbsolute());
                areaRef = new AreaReference(firstCellRef, lastCellRef);
                
                // 修正した範囲を再設定する
                name.setRefersToFormula(areaRef.formatAsString());
                
            } else if(notOperateRange.getLastRow() < firstCellRef.getRow()) {
                /*
                 * 名前の定義の場合、自身のセルの範囲より下方にあるセルの範囲の場合、
                 * 自動的に修正されるため、修正は必要なし。
                 */
                
            }
            
        }
        
    }
    
    /**
     * セルのコメントを全て取得する。その際に、セルからコメントを削除する。
     * @param sheet
     * @return
     */
    private List<CellCommentStore> loadCommentAndRemove(final Sheet sheet) {
        
        final List<CellCommentStore> list = new ArrayList<>();
        
        final int maxRow = POIUtils.getRows(sheet);
        for(int rowIndex=0; rowIndex < maxRow; rowIndex++) {
            final Row row = sheet.getRow(rowIndex);
            if(row == null) {
                continue;
            }
            
            final short maxCol = row.getLastCellNum();
            for(short colIndex=0; colIndex < maxCol; colIndex++) {
                
                final Cell cell = row.getCell(colIndex);
                if(cell == null) {
                    continue;
                }
                
                CellCommentStore commentStore = CellCommentStore.getAndRemove(cell);
                if(commentStore != null) {
                    list.add(commentStore);
                }
            }
            
        }
        
        return list;
    }
    
    /**
     * セルのコメントを再設定する。
     * @param sheet
     * @param recordOperation
     * @param commentStoreList
     */
    private void correctComment(final Sheet sheet, final RecordOperation recordOperation,
            final List<CellCommentStore> commentStoreList) {
        
        if(commentStoreList.isEmpty()) {
            return;
        }
        
        if(!recordOperation.isInsertRecord() && !recordOperation.isDeleteRecord()) {
            return;
        }
        
        // 操作をしていないセルの範囲の取得
        final CellRangeAddress notOperateRange = new CellRangeAddress(
                recordOperation.getTopLeftPoisitoin().y,
                recordOperation.getBottomRightPosition().y - recordOperation.getCountInsertRecord(),
                recordOperation.getTopLeftPoisitoin().x,
                recordOperation.getBottomRightPosition().x
                );
        
        for(CellCommentStore commentStore : commentStoreList) {
            
            if(notOperateRange.getLastRow() >= commentStore.getRow()) {
                // 行の追加・削除をしていない範囲の場合
                commentStore.set(sheet);
                
            } else {
                // 自身のセルの範囲より下方にあるセルの範囲の場合、行の挿入や削除に影響を受けているので修正する。
                if(recordOperation.isInsertRecord()) {
                    commentStore.addRow(recordOperation.getCountInsertRecord());
                    commentStore.set(sheet);
                    
                } else if(recordOperation.isDeleteRecord()) {
                    commentStore.addRow(-recordOperation.getCountDeleteRecord());
                    commentStore.set(sheet);
                    
                }
                
            }
            
        }
        
    }
}
