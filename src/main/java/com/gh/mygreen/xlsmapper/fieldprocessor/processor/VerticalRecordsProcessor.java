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
import org.apache.poi.ss.usermodel.DataValidationHelper;
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
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.annotation.XlsVerticalRecords;
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
 * アノテーション{@link XlsVerticalRecords}を処理するクラス。
 *
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class VerticalRecordsProcessor extends AbstractFieldProcessor<XlsVerticalRecords>{

    @Override
    public void loadProcess(final Sheet sheet, final Object obj, final XlsVerticalRecords anno,
            final FieldAdaptor adaptor, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
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
                            XlsVerticalRecords.class.getSimpleName(), clazz.getName()),
                            anno);
        }
        
    }
    
   private List<?> loadRecords(final Sheet sheet, XlsVerticalRecords anno, final FieldAdaptor adaptor,
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
        
        while(true){
            try {
                Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                while(POIUtils.isEmptyCellContents(cell, config.getCellFormatter()) && rangeCount < anno.range()){
                    cell = POIUtils.getCell(sheet, hColumn, hRow + rangeCount);
                    rangeCount++;
                }
                
                if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    break;
                } else {
                    for(int j=hColumn; j > initColumn; j--){
                        final Cell tmpCell = POIUtils.getCell(sheet, j, hRow);
                        if(!POIUtils.isEmptyCellContents(tmpCell, config.getCellFormatter())){
                            cell = tmpCell;
                            break;
                        }
                    }
                }
                
                headers.add(new RecordHeader(POIUtils.getCellContents(cell, config.getCellFormatter()), rangeCount-1));
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
        
        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader());
        
        RecordTerminal terminal = anno.terminal();
        if(terminal == null){
            terminal = RecordTerminal.Empty;
        }
        
        // get records
        hColumn++;
        System.out.printf("sheetColumns=%d\n", POIUtils.getColumns(sheet));
        while(hColumn < POIUtils.getColumns(sheet)){
            
            hRow = initRow;
            boolean emptyFlag = true;
            // recordは、マッピング先のオブジェクトのインスタンス。
            final Object record = config.createBean(recordClass);
            
            // パスの位置の変更
            work.getErrors().pushNestedPath(adaptor.getName(), result.size());
            
            // set PostProcess method
            for(Method method : record.getClass().getMethods()) {
                final XlsPreLoad preProcessAnno = work.getAnnoReader().getAnnotation(record.getClass(), method, XlsPreLoad.class);
                if(preProcessAnno != null) {
                    Utils.invokeNeedProcessMethod(method, record, sheet, config, work.getErrors());
                }
            }
            
            loadMapColumns(sheet, headers, hRow, hColumn, record, config, work);
            
            for(int i=0; i < headers.size() && hColumn < POIUtils.getColumns(sheet); i++){
                final RecordHeader headerInfo = headers.get(i);
                hRow = hRow + headerInfo.getHeaderRange();
                Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                
                // find end of the table
                if(!POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    emptyFlag = false;
                }
                
                if(terminal==RecordTerminal.Border && i==0){
                    final CellStyle format = cell.getCellStyle();
                    if(format!=null && !(format.getBorderTop() == CellStyle.BORDER_NONE)){
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
                final List<FieldAdaptor> propeties = Utils.getLoadingColumnProperties(
                        record.getClass(), headerInfo.getHeaderLabel(), work.getAnnoReader());
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
                
                hRow++;
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
            
            hColumn++;
            
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
    private Point getHeaderPosition(final Sheet sheet, final XlsVerticalRecords anno,
            final XlsMapperConfig config) throws XlsMapperException {
        
        if(Utils.isNotEmpty(anno.tableAddress())) {
            Point address = Utils.parseCellAddress(anno.headerAddress());
            if(address == null) {
                throw new AnnotationInvalidException(
                        String.format("@XlsVerticalRecords#headerAddress is wrong cell address '%s'.", anno.headerAddress()), anno);
            }
            return address;
            
        } else if(Utils.isNotEmpty(anno.tableLabel())) {
            try {
                Cell labelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
                int initColumn = labelCell.getColumnIndex() + 1;
                int initRow = labelCell.getRowIndex();
                
                return new Point(initColumn, initRow);
                
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
                        String.format("@XlsVerticalRecords#headerColumn or headerRow soulde be greather than or equal zero. (headerColulmn=%d, headerRow=%d)",
                                anno.headerColumn(), anno.headerRow()), anno);
            }
            return new Point(anno.headerColumn(), anno.headerRow());
        }
    }
    
    private void loadMapColumns(Sheet sheet, List<RecordHeader> headerInfos, 
           int begin, int column, Object record, XlsMapperConfig config, LoadingWorkObject work) throws XlsMapperException {
        
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
            for(RecordHeader headerInfo : headerInfos){
                if(headerInfo.getHeaderLabel().equals(mapAnno.previousColumnName())){
                    flag = true;
                    begin++;
                    continue;
                }
                
                if(flag){
                    final Cell cell = POIUtils.getCell(sheet, column, begin + headerInfo.getHeaderRange());
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
    public void saveProcess(final Sheet sheet, final Object obj, final XlsVerticalRecords anno,
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
                recordClass = adaptor.getLoadingGenericClassType();
            }
            
            final List<Object> list = (result == null ? new ArrayList<Object>() : (List<Object>) result);
            saveRecords(sheet, anno, adaptor, recordClass, list, config, work);
            
        } else if(clazz.isArray()) {
            
            Class<?> recordClass = anno.recordClass();
            if(recordClass == Object.class) {
                recordClass = adaptor.getLoadingGenericClassType();
            }
            
            final List<Object> list = (result == null ? new ArrayList<Object>() : Arrays.asList(result));
            saveRecords(sheet, anno, adaptor, recordClass, list, config, work);
            
        } else {
            throw new AnnotationInvalidException(
                    String.format("Annotation '@%s' should only granted List or Array. : %s", 
                            XlsVerticalRecords.class.getSimpleName(), clazz.getName()),
                            anno);
        }
        
    }
    
    private void saveRecords(final Sheet sheet, XlsVerticalRecords anno, final FieldAdaptor adaptor, 
            final Class<?> recordClass, final List<Object> result,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
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
                    cell = POIUtils.getCell(sheet, hColumn, hRow + rangeCount);
                    rangeCount++;
                }
                
                if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
                    break;
                }
                
                headers.add(new RecordHeader(POIUtils.getCellContents(cell, config.getCellFormatter()), rangeCount - 1));
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
        
        // Check for columns
        RecordsProcessorUtil.checkColumns(sheet, recordClass, headers, work.getAnnoReader());
        
        RecordTerminal terminal = anno.terminal();
        if(terminal == null){
            terminal = RecordTerminal.Empty;
        }
        
        // 結合したセルの情報
        final List<CellRangeAddress> mergedRanges = new ArrayList<CellRangeAddress>();
        
        // 書き込んだセルの範囲などの情報
        final RecordOperation recordOperation = new RecordOperation();
        
        // get records
        hColumn++;
        for(int r=0; r < POIUtils.getColumns(sheet); r++) {
            
            hRow = initRow;
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
            
//            // レコードの各列処理で既に行を追加したかどうかのフラグ。
//            boolean insertRows = false;
            
            // hRowという上限がない
            for(int i=0; i < headers.size(); i++) {
                final RecordHeader headerInfo = headers.get(i);
                hRow = hRow + headerInfo.getHeaderRange();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
                // find end of the table
                if(!POIUtils.getCellContents(cell, config.getCellFormatter()).equals("")){
                    emptyFlag = false;
                }
                
                if(terminal == RecordTerminal.Border && i==0){
                    final CellStyle format = cell.getCellStyle();
                    if(format!=null && !(format.getBorderTop() == CellStyle.BORDER_NONE)){
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
                                
                                recordOperation.incrementCopyRecord();
                                
                            } else if(anno.overRecord().equals(OverRecordOperate.Insert)) {
                                // POIは列の追加をサポートしていないので非対応。
                                throw new AnnotationInvalidException("XlsVerticalRecoreds#overRecord not supported 'OverRecordOperate.Insert'.", anno);
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
                        Cell clearCell = POIUtils.getCell(sheet, hColumn, hRow);
                        clearCell.setCellType(Cell.CELL_TYPE_BLANK);
                        
                    } else if(anno.remainedRecord().equals(RemainedRecordOperate.Delete)) {
                        // POIは列の削除をサポートしていないので非対応。
                        throw new AnnotationInvalidException("XlsVerticalRecoreds#remainedRecord not supported 'RemainedRecordOperate.Delete'.", anno);
                    }
                }
                hRow++;
            }
            
            // マップ形式のカラムを出力する
            if(record != null) {
                saveMapColumn(sheet, headers, initRow, hColumn, record, terminal, anno, config, work);
            }
            
            // パスの位置の変更
            work.getErrors().popNestedPath();
            
            hColumn++;
            
            if(emptyFlag == true && (r > result.size())) {
                // セルが空で、書き込むデータがない場合。
                break;
            }
        }
        
        if(config.isCorrectCellDataValidationOnSave()) {
            correctDataValidation(sheet, recordOperation);
        }
        
        if(config.isCorrectNameRangeOnSave()) {
            correctNameRange(sheet, recordOperation);
        }
        
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
    
    private void saveMapColumn(Sheet sheet, List<RecordHeader> headerInfos, 
            int begin, int column, Object record, RecordTerminal terminal,
            XlsVerticalRecords anno, XlsMapperConfig config, SavingWorkObject work) throws XlsMapperException {
        
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
                    final Cell cell = POIUtils.getCell(sheet, column, begin + headerInfo.getHeaderRange());
                    
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
                        if(POIUtils.getCellContents(cell, config.getCellFormatter()).equals(anno.terminateLabel())) {
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
                            throw new AnnotationInvalidException("XlsVerticalRecoreds#overRecord not supported 'OverRecordOperate.Insert'.", anno);
                            
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
                recordOperation.getBottomRightPosition().y,
                recordOperation.getTopLeftPoisitoin().x,
                recordOperation.getBottomRightPosition().x - recordOperation.getCountInsertRecord()
                );
        
        final DataValidationHelper helper = sheet.getDataValidationHelper();
        final List<? extends DataValidation> list = sheet.getDataValidations();
        for(DataValidation validation : list) {
            
            final CellRangeAddressList region = validation.getRegions();
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
                sheet.addValidationData(helper.createValidation(validation.getValidationConstraint(), region));
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
