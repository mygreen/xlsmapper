package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import java.awt.Point;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

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
import com.gh.mygreen.xlsmapper.annotation.XlsHorizontalRecords;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsPostLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreLoad;
import com.gh.mygreen.xlsmapper.annotation.XlsPreSave;
import com.gh.mygreen.xlsmapper.cellconvert.CellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordHeader;
import com.gh.mygreen.xlsmapper.fieldprocessor.RecordsProcessorUtil;


/**
 * アノテーション{@link XlsHorizontalRecords}を処理するクラス。
 * 
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class HorizontalRecordsProcessor extends AbstractFieldProcessor<XlsHorizontalRecords> {
    
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
                
                if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    break;
                }
                
                headers.add(new RecordHeader(POIUtils.getCellContents(cell, config.getCellFormatter()), rangeCount - 1));
                hColumn = hColumn + rangeCount;
                rangeCount = 1;
                
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
                
                if(terminal == RecordTerminal.Border && i==0){
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
            
            result.add(record);
            
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
                Cell labelCell = Utils.getCell(sheet, anno.tableLabel(), 0, config);
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
                            XlsHorizontalRecords.class.getSimpleName(), clazz.getName()),
                            anno);
        }
        
    }
    
    private void saveRecords(final Sheet sheet, XlsHorizontalRecords anno, final FieldAdaptor adaptor, 
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
                
                if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    break;
                }
                
                headers.add(new RecordHeader(POIUtils.getCellContents(cell, config.getCellFormatter()), rangeCount - 1));
                hColumn = hColumn + rangeCount;
                rangeCount = 1;
                
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
            
            // hRowという上限がない
            for(int i=0; i < headers.size(); i++) {
                final RecordHeader headerInfo = headers.get(i);
                hColumn = hColumn + headerInfo.getHeaderRange();
                final Cell cell = POIUtils.getCell(sheet, hColumn, hRow);
//                    System.out.printf("cell=[%s], value=%s\n", Utils.formatCellAddress(cell), POIUtils.getCellContents(cell));
                // find end of the table
                if(!POIUtils.isEmptyCellContents(cell, config.getCellFormatter())){
                    emptyFlag = false;
                }
                
                if(terminal == RecordTerminal.Border && i==0){
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
                                
                            } else if(anno.overRecord().equals(OverRecordOperate.Insert)) {
                                // すでに他の列の処理に対して行を追加している場合は行の追加は行わない。
                                if(!insertRows) {
                                    // 行を下に追加する
                                    POIUtils.insertRow(sheet, valueCell.getRowIndex()+1);
                                  insertRows = true;
                                    
    //                                    //挿入した文セルの位置がずれるので調整する
    //                                    if(preIndex != valueCell.getRowIndex()) {
    //                                        System.out.printf("insert preIndex=%d, currentIndex\n", preIndex, valueCell.getRowIndex());
    //                                        valueCell = POIUtils.getCell(sheet,
    //                                                valueCell.getColumnIndex(),
    //                                                valueCell.getRowIndex()-(valueCell.getRowIndex()-preIndex));
    //                                    }
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
                if(record == null && emptyFlag == false) {
                    if(anno.remainedRecord().equals(RemainedRecordOperate.None)) {
                        // なにもしない
                        
                    } else if(anno.remainedRecord().equals(RemainedRecordOperate.Clear)) {
                        CellStyle style = POIUtils.getCell(sheet, hColumn, hRow-1).getCellStyle();
                        Cell clearCell = POIUtils.getCell(sheet, hColumn, hRow);
                        clearCell.setCellStyle(style);
                        clearCell.setCellType(Cell.CELL_TYPE_BLANK);
                        
                    } else if(anno.remainedRecord().equals(RemainedRecordOperate.Delete)) {
                        final Row row = sheet.getRow(hRow);
                        if(row != null) {
                            sheet.removeRow(row);
                        }
                    }
                }
//                    System.out.printf("hColumn=%d\n", hColumn);
                hColumn++;
            }
            
            // マップ形式のカラムを出力する
            if(record != null) {
                saveMapColumn(sheet, headers, initColumn, hRow, record, terminal, anno, config, work);
            }
            
            // パスの位置の変更
            work.getErrors().popNestedPath();
            
            hRow++;
            
            if(emptyFlag == true && (r > result.size())) {
                // セルが空で、書き込むデータがない場合。
                break;
            }
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
            XlsHorizontalRecords anno, XlsMapperConfig config, SavingWorkObject work) throws XlsMapperException {
        
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
                }
                
                begin = begin + headerInfo.getHeaderRange() + 1;
            }
        }
        
    }
    
}
