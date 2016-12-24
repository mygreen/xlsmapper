package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

import com.gh.mygreen.xlsmapper.cellconvert.LinkType;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellAddress;
import com.github.mygreen.cellformatter.POICell;

/**
 * Apache POIとJExcel APIの差を埋めるユーティリティクラス。
 * 
 * @version 1.6
 * @author T.TSUCHIE
 *
 */
public class POIUtils {
    
    /** 標準のセルフォーマッター */
    private static CellFormatter defaultCellFormatter = new DefaultCellFormatter();
    
    /**
     * セルの「縮小して表示する」のメソッドが利用可能かどうか。
     * POI-3.10以上の場合、trueとなる。
     * @since 0.2.3
     */
    public static final boolean AVAILABLE_METHOD_CELL_SHRINK_TO_FIT;
    static {
        boolean available = false;
        try {
            //POI-3.10以降
            final Method method = CellStyle.class.getMethod("setShrinkToFit", boolean.class);
            method.setAccessible(true);
            available = true;
        } catch (Exception e) {
            available = false;
        } 
        
        AVAILABLE_METHOD_CELL_SHRINK_TO_FIT = available;
    }
    
    /**
     * セルの入力規則の取得のメソッドが利用可能かどうか。
     * POI-3.11以上の場合、trueとなる。
     * @since 0.3
     */
    public static final boolean AVAILABLE_METHOD_SHEET_DAVA_VALIDATION;
    static {
        boolean available = false;
        try {
            // POI-3.11移行
            final Method method = Sheet.class.getMethod("getDataValidations");
            method.setAccessible(true);
            available = true;
        } catch(Exception e) {
            available = false;
        }
        
        AVAILABLE_METHOD_SHEET_DAVA_VALIDATION = available;
    }
    
    /**
     * セルのハイパーリンクの削除メソッドが利用可能かどうか。
     * POI-3.11以上の場合、trueとなる。
     * @since 0.4
     */
    public static final boolean AVAILABLE_METHOD_CELL_REMOVE_HYPERLINK;
    static {
        boolean available = false;
        try {
            //POI-3.11以降
            final Method method = Cell.class.getMethod("removeHyperlink", boolean.class);
            method.setAccessible(true);
            available = true;
        } catch(Exception e) {
            available = false;
        }
        
        AVAILABLE_METHOD_CELL_REMOVE_HYPERLINK = available;
    }
    
    /**
     * シートの結合セルノ削除メソッドが利用可能かどうか。
     * POI-3.15以上の場合、trueとなる。
     * @since 1.6
     */
    public static final boolean AVAILABLE_METHOD_SHEET_REMOVE_MERGE_REGIONS;
    static {
        boolean available = false;
        try {
            //POI-3.15以降
            final Method method = Sheet.class.getMethod("removeMergedRegions", Collection.class);
            method.setAccessible(true);
            available = true;
        } catch(Exception e) {
            available = false;
        }
        
        AVAILABLE_METHOD_SHEET_REMOVE_MERGE_REGIONS = available;
    }
    
    /**
     * シートの最大列数を取得する。
     * @see jxl.Sheet.getColumns()
     * @param sheet
     * @return
     */
    public static int getColumns(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        
        int minRowIndex = sheet.getFirstRowNum();
        int maxRowIndex = sheet.getLastRowNum();
        int maxColumnsIndex = 0;
        for(int i = minRowIndex; i <= maxRowIndex; i++) {
            final Row row = sheet.getRow(i);
            if(row == null) {
                continue;
            }
            
            final int column = row.getLastCellNum();
            if(column > maxColumnsIndex) {
                maxColumnsIndex = column;
            }
        }
        
        return maxColumnsIndex;
    }
    
    /**
     * シートの最大行数を取得する
     * 
     * @see jxl.Sheet.getRows()
     * @param sheet
     * @return
     */
    public static int getRows(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        return sheet.getLastRowNum() + 1;
    }
    
    /**
     * シートから任意のセルを取得する。
     * @since 0.5
     * @param sheet シートオブジェクト
     * @param address アドレス（Point.x=column, Point.y=row）
     * @return 
     */
    public static Cell getCell(final Sheet sheet, final Point address) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(address, "address");
        return getCell(sheet, address.x, address.y);
    }
    
    /**
     * シートから任意のセルを取得する。
     * @since 1.4
     * @param sheet シートオブジェクト
     * @param address セルのアドレス
     * @return 
     */
    public static Cell getCell(final Sheet sheet, final CellAddress address) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(address, "address");
        return getCell(sheet, address.getColumn(), address.getRow());
    }
    
    /**
     * シートから任意のセルを取得する。
     * 
     * @see jxl.Sheet.getCell(int column, int row)
     * @param sheet
     * @param column
     * @param row
     * @return
     */
    public static Cell getCell(final Sheet sheet, final int column, final int row) {
        ArgUtils.notNull(sheet, "sheet");
        
        Row rows = sheet.getRow(row);
        if(rows == null) {
            rows = sheet.createRow(row);
        }
        
        Cell cell = rows.getCell(column);
        if(cell == null) {
            cell = rows.createCell(column, Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
    /**
     * 任意の行のセルを全て取得する。
     * @see jxl.Seet.getRow(int row)
     * @param sheet
     * @param row
     * @return
     */
    public static Cell[] getRow(final Sheet sheet, final int row) {
        ArgUtils.notNull(sheet, "sheet");
        
        Row rows = sheet.getRow(row);
        if(rows == null) {
            rows = sheet.createRow(row);
        }
        int maxColumn = getColumns(sheet);
        Cell[] cells = new Cell[maxColumn];
        for(int i=0; i < maxColumn; i++) {
            Cell cell = rows.getCell(i);
            if(cell == null) {
                cell = rows.createCell(i, Cell.CELL_TYPE_BLANK);
            }
            cells[i] = cell;
        }
        
        return cells;
    }
    
    /**
     * 任意の列のセルを全て取得する。
     * @see jxl.Seet.getColumn(int col)
     * @param sheet
     * @param col
     * @return
     */
    public static Cell[] getColumn(final Sheet sheet, final int col) {
        ArgUtils.notNull(sheet, "sheet");
        
        int maxRow = getRows(sheet);
        Cell[] cells = new Cell[maxRow];
        for(int i=0; i < maxRow; i++) {
            Row rows = sheet.getRow(i);
            if(rows == null) {
                rows = sheet.createRow(i);
                
            }
            
            Cell cell = rows.getCell(col);
            if(cell == null) {
                cell = rows.createCell(col, Cell.CELL_TYPE_BLANK);
            }
            
            cells[i] = cell;
        }
        
        return cells;
    }
    
    /**
     * フォーマッターを指定してセルの値を取得する
     * 
     * @param cell
     * @param cellFormatter 
     * @return
     */
    public static String getCellContents(final Cell cell, final CellFormatter cellFormatter) {
        ArgUtils.notNull(cell, "cell");
        ArgUtils.notNull(cellFormatter, "cellFormatter");
        
        return cellFormatter.format(cell);
        
    }
    
    /**
     * 指定してセルの値が空かどうか判定する。
     * <p>ブランクセルなどの判定は優先的に行う。
     * @param cell
     * @return
     */
    public static boolean isEmptyCellContents(final Cell cell) {
        return isEmptyCellContents(cell, defaultCellFormatter);
    }
    
    /**
     * フォーマッターを指定してセルの値が空かどうか判定する。
     * <p>ブランクセルなどの判定は優先的に行う。
     * @param sheet シート
     * @param cellFormatter
     * @throws IllegalArgumentException {@literal sheet == null.}
     * @throws IllegalArgumentException {@literal cellFormatter == null.}
     * @return
     */
    public static boolean isEmptyCellContents(final Cell cell, final CellFormatter cellFormatter) {
        ArgUtils.notNull(cell, "cell");
        ArgUtils.notNull(cellFormatter, "cellFormatter");
        
        return getCellContents(cell, cellFormatter).isEmpty();
    }
    
    /**
     * 指定した書式のインデックス番号を取得する。シートに存在しない場合は、新しく作成する。
     * @param sheet シート
     * @param pattern 作成する書式のパターン
     * @return 書式のインデックス番号。
     * @throws IllegalArgumentException {@literal sheet == null.}
     * @throws IllegalArgumentException {@literal pattern == null || pattern.isEmpty().}
     */
    public static short getDataFormatIndex(final Sheet sheet, final String pattern) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(pattern, "pattern");
        
        return sheet.getWorkbook().getCreationHelper().createDataFormat().getFormat(pattern);
        
    }
    
    /**
     * セルに設定されている書式を取得する。
     * @since 1.1
     * @param cell セルのインスタンス。
     * @return 書式が設定されていない場合は、空文字を返す。
     *         cellがnullの場合も空文字を返す。
     *         標準の書式の場合も空文字を返す。
     */
    public static String getCellFormatPattern(final Cell cell) {
        if(cell == null) {
            return "";
        }
        
        POICell poiCell = new POICell(cell);
        if(poiCell.getFormatIndex() == 0) {
            return "";
        } else {
            return poiCell.getFormatPattern();
        }
        
    }
    
    /**
     * 指定した範囲のセルを結合する。
     * @param sheet
     * @param startCol
     * @param startRow
     * @param endCol
     * @param endRow
     * @return
     */
    public static CellRangeAddress mergeCells(final Sheet sheet, int startCol, int startRow, int endCol, int endRow) {
        ArgUtils.notNull(sheet, "sheet");
        
        // 結合先のセルの値を空に設定する
        for(int r=startRow; r <= endRow; r++) {
            for(int c=startCol; c <= endCol; c++) {
                
                if(r == startRow && c == startCol) {
                    continue;
                }
                
                Cell cell = getCell(sheet, c, r);
                cell.setCellType(Cell.CELL_TYPE_BLANK);
            }
        }
        
        final CellRangeAddress range = new CellRangeAddress(startRow, endRow, startCol, endCol);
        sheet.addMergedRegion(range);
        return range;
    }
    
    /**
     * 指定したセルのアドレスの結合情報を取得する。
     * @since 0.5
     * @param sheet
     * @param rowIdx
     * @param colIdx
     * @return 結合していない場合nullを返す。
     */
    public static CellRangeAddress getMergedRegion(final Sheet sheet, final int rowIdx, final int colIdx) {
        ArgUtils.notNull(sheet, "sheet");
        
        final int num = sheet.getNumMergedRegions();
        for(int i=0; i < num; i ++) {
            final CellRangeAddress range = sheet.getMergedRegion(i);
            if(range.isInRange(rowIdx, colIdx)) {
                return range;
            }
        }
        
        return null;
    }
    
    /**
     * 指定した範囲の結合を解除する。
     * @param sheet
     * @param mergedRange
     * @return 引数で指定した結合が見つからない場合。
     */
    public static boolean removeMergedRange(final Sheet sheet, final CellRangeAddress mergedRange) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(mergedRange, "mergedRange");
        
        final String mergedAddress = mergedRange.formatAsString(sheet.getSheetName(), true);
        
        final int num = sheet.getNumMergedRegions();
        for(int i=0; i < num; i ++) {
            final CellRangeAddress range = sheet.getMergedRegion(i);
            final String rangeAddress = range.formatAsString(sheet.getSheetName(), true);
            if(rangeAddress.equals(mergedAddress)) {
                sheet.removeMergedRegion(i);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 指定した行の下に行を1行追加する
     * @param sheet
     * @param rowIndex 追加する行数
     * @return 追加した行を返す。
     */
    public static Row insertRow(final Sheet sheet, final int rowIndex) {
        
        ArgUtils.notNull(sheet, "cell");
        ArgUtils.notMin(rowIndex, 0, "rowIndex");
        
        // 最終行を取得する
        int lastRow = sheet.getLastRowNum();
        if(lastRow < rowIndex) {
            // データが定義されている範囲害の場合は、行を新たに作成して返す。
            return sheet.createRow(rowIndex);
        }
        
        sheet.shiftRows(rowIndex, lastRow+1, 1);
        return sheet.createRow(rowIndex);
    }
    
    /**
     * 指定した行を削除する。
     * <p>削除した行は上に詰める。
     * @since 0.5
     * @param sheet
     * @param rowIndex 削除する行数
     * @return 削除した行
     */
    public static Row removeRow(final Sheet sheet, final int rowIndex) {
        
        ArgUtils.notNull(sheet, "cell");
        ArgUtils.notMin(rowIndex, 0, "rowIndex");
        
        final Row row = sheet.getRow(rowIndex);
        if(row == null) {
            // 削除対象の行にデータが何もない場合
            return row;
        }
        
        sheet.removeRow(row);
        
        // 上に1つ行をずらす
        int lastRow = sheet.getLastRowNum();
        if(rowIndex +1 > lastRow) {
            return row;
        }
        
        sheet.shiftRows(rowIndex+1, lastRow, -1);
        
        return row;
    }
    
    /**
     * セルの折り返し設定を有効にする
     * @param cell
     * @param forceWrapText trueの場合有効にする。falseの場合は変更しない。
     */
    public static void wrapCellText(final Cell cell, final boolean forceWrapText) {
        
        ArgUtils.notNull(cell, "cell");
        
        if(!forceWrapText) {
            return;
        }
        
        final CellStyle style = cell.getCellStyle();
        style.setWrapText(true);
        setCellStyleWithShrinkToFit(cell, style, false);
        
        cell.setCellStyle(style);
    }
    
    /**
     * セルの縮小表示設定を有効にする。
     * @param cell
     * @param forceShrinkToFit trueの場合有効にする。falseの場合は変更しない。
     */
    public static void shrinkToFit(final Cell cell, final boolean forceShrinkToFit) {
        
        ArgUtils.notNull(cell, "cell");
        
        if(!forceShrinkToFit) {
            return;
        }
        
        final CellStyle style = cell.getCellStyle();
        
        style.setWrapText(false);
        setCellStyleWithShrinkToFit(cell, style, true);
    }
    
    /**
     * セルの縮小表示設定を変更する。
     * <p>POI-3.9以前の場合は、リフレクションで強制的に変更する。
     * @param cell 変更対象のセル
     * @param style 縮小表示設定を行うStyle
     * @param shrinkToFit
     */
    public static void setCellStyleWithShrinkToFit(final Cell cell, final CellStyle style, final boolean shrinkToFit) {
        
        ArgUtils.notNull(cell, "cell");
        ArgUtils.notNull(style, "style");
        
        if(AVAILABLE_METHOD_CELL_SHRINK_TO_FIT) {
            try {
                //POI-3.10以降
                final Method method = style.getClass().getMethod("setShrinkToFit", boolean.class);
                method.setAccessible(true);
                method.invoke(style, true);
                
                cell.setCellStyle(style);
                
                return;
                
            } catch (Exception e) {}
        }
        
        if(style instanceof HSSFCellStyle) {
            // POI-3.9以前のExcel2003形式
            try {
                final Field field = style.getClass().getDeclaredField("_format");
                field.setAccessible(true);
                
                ExtendedFormatRecord record = (ExtendedFormatRecord) field.get(style);
                record.setShrinkToFit(shrinkToFit);
                
                cell.setCellStyle(style);
                return;
            } catch (Exception e ) { }
            
        } else if(style instanceof XSSFCellStyle) {
            // POI-3.9以前のExcel2007形式
            try {
                final Method aligngmentMethod = style.getClass().getDeclaredMethod("getCellAlignment");
                aligngmentMethod.setAccessible(true);
                final XSSFCellAlignment alignment = (XSSFCellAlignment) aligngmentMethod.invoke(style);
                
                final Field alignmentField = alignment.getClass().getDeclaredField("cellAlignement");
                alignmentField.setAccessible(true);
                CTCellAlignment alignment2 = (CTCellAlignment) alignmentField.get(alignment);
                
                alignment2.setShrinkToFit(shrinkToFit);
                
                cell.setCellStyle(style);
                return;
            } catch (Exception e ) { }
            
        }
        
    }
    
    /**
     * 座標をExcelのアドレス形式'A1'などに変換する
     * @param rowIndex 行インデックス
     * @param colIndex 列インデックス
     * @return
     */
    public static String formatCellAddress(final int rowIndex, final int colIndex) {
        return CellReference.convertNumToColString(colIndex) + String.valueOf(rowIndex+1);
    }
    
    /**
     * 座標をExcelのアドレス形式'A1'になどに変換する。
     * @param cellAddress セルの位置情報
     * @return
     * @throws IllegalArgumentException address == null.
     */
    public static String formatCellAddress(final Point cellAddress) {
        ArgUtils.notNull(cellAddress, "cellAddress");
        return formatCellAddress(cellAddress.y, cellAddress.x);
    }
    
    /**
     * セルのアドレス'A1'を取得する。
     * @param cell セル情報
     * @return IllegalArgumentException cell == null.
     */
    public static String formatCellAddress(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        return CellReference.convertNumToColString(cell.getColumnIndex()) + String.valueOf(cell.getRowIndex()+1);
    }
    
    /**
     * リンクのアドレスを判定する。
     * @param linkAddress リンクのアドレス（URL）
     * @return 不明な場合は{@link LinkType#UNKNOWN}を返す。
     * @throws IllegalArgumentException linkAddress が空文字の場合。
     */
    public static LinkType judgeLinkType(final String linkAddress) {
        
        ArgUtils.notEmpty(linkAddress, "linkAddress");
        
        if(linkAddress.matches(".*![\\p{Alnum}]+")) {
            // !A1のアドレスを含むかどうか
            return LinkType.DOCUMENT;
            
        } else if(linkAddress.matches(".+@.+")) {
            // @を含むかどうか
            return LinkType.EMAIL;
            
        } else if(linkAddress.matches("[\\p{Alpha}]+://.+")) {
            // プロトコル付きかどうか
            return LinkType.URL;
            
        } else if(linkAddress.matches(".+\\.[\\p{Alnum}]+")) {
            // 拡張子付きかどうか
            return LinkType.FILE;
        } else {
            return LinkType.UNKNOWN;
        }
        
    }
    
    /**
     * 入力規則の範囲を更新する。
     * @since 0.5
     * @param sheet シート
     * @param oldRegion 更新対象の範囲。
     * @param newRegion 新しい範囲。
     * @return true:更新完了。false:指定した範囲を持つ入力規則が見つからなかった場合。
     */
    public static boolean updateDataValidationRegion(final Sheet sheet,
            final CellRangeAddressList oldRegion, final CellRangeAddressList newRegion) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(oldRegion, "oldRegion");
        ArgUtils.notNull(newRegion, "newRegion");
        
        if(sheet instanceof XSSFSheet) {
            
            final List<String> oldSqref = convertSqref(oldRegion);
            
            try {
                final XSSFSheet xssfSheet = (XSSFSheet) sheet;
                Field fWorksheet = XSSFSheet.class.getDeclaredField("worksheet");
                fWorksheet.setAccessible(true);
                CTWorksheet worksheet = (CTWorksheet) fWorksheet.get(xssfSheet);
                
                CTDataValidations dataValidations = worksheet.getDataValidations();
                if(dataValidations == null) {
                    return false;
                }
                
                for(int i=0; i < dataValidations.getCount(); i++) {
                    CTDataValidation dv = dataValidations.getDataValidationArray(i);
                    
                    // 規則の範囲を比較し、同じならば範囲を書き換える。
                    @SuppressWarnings("unchecked")
                    List<String> sqref = new ArrayList<>(dv.getSqref());
                    if(equalsSqref(sqref, oldSqref)) {
                        List<String> newSqref = convertSqref(newRegion);
                        dv.setSqref(newSqref);
                        
                        // 設定し直す
                        dataValidations.setDataValidationArray(i, dv);
                        return true;
                    }
                    
                }
                
                return false;
                
            } catch(Exception e) {
                throw new RuntimeException("fail update DataValidation's Regsion.", e);
            }
            
        } else if(sheet instanceof HSSFSheet) {
            
            final HSSFSheet hssfSheet = (HSSFSheet) sheet;
            try {
                Field fWorksheet = HSSFSheet.class.getDeclaredField("_sheet");
                fWorksheet.setAccessible(true);
                InternalSheet worksheet = (InternalSheet) fWorksheet.get(hssfSheet);
                
                DataValidityTable dvt = worksheet.getOrCreateDataValidityTable();
                
                // シート内の入力規則のデータを検索して、一致するものがあれば書き換える。
                final AtomicBoolean updated = new AtomicBoolean(false);
                RecordVisitor visitor = new RecordVisitor() {
                    
                    @Override
                    public void visitRecord(final Record r) {
                        if (!(r instanceof DVRecord)) {
                            return;
                        }
                        
                        final DVRecord dvRecord = (DVRecord) r;
                        final CellRangeAddressList region = dvRecord.getCellRangeAddress();
                        if(equalsRegion(region, oldRegion)) {
                            
                            // 一旦既存の範囲を削除する。
                            while(region.countRanges() != 0) {
                                region.remove(0);
                            }
                            
                            // 新しい範囲を追加する。
                            for(CellRangeAddress newRange : newRegion.getCellRangeAddresses()) {
                                region.addCellRangeAddress(newRange);
                            }
                            
                            updated.set(true);
                            return;
                        }
                    }
                };
                
                dvt.visitContainedRecords(visitor);
                
                return updated.get();
                
            } catch(Exception e) {
                throw new RuntimeException("fail update DataValidation's Regsion.", e);
            }
        } else {
            throw new UnsupportedOperationException("not supported update dava validation's region for type " + sheet.getClass().getName());
        }
        
    }
    
    /**
     * CellRangeAddressを文字列形式のリストに変換する。
     * @since 0.5
     * @param region
     * @return
     */
    private static List<String> convertSqref(final CellRangeAddressList region) {
        
        List<String> sqref = new ArrayList<>();
        for(CellRangeAddress range : region.getCellRangeAddresses()) {
            sqref.add(range.formatAsString());
        }
        
        return sqref;
        
    }
    
    /**
     * 文字列形式のセルの範囲が同じかどうか比較する。
     * @since 0.5
     * @param sqref1
     * @param sqref2
     * @return
     */
    public static boolean equalsSqref(final List<String> sqref1, final List<String> sqref2) {
        
        if(sqref1.size() != sqref2.size()) {
            return false;
        }
        
        Collections.sort(sqref1);
        Collections.sort(sqref2);
        
        final int size = sqref1.size();
        for(int i=0; i < size; i++) {
            if(!sqref1.get(i).equals(sqref2.get(i))) {
                return false;
            }
        }
        
        return true;
        
    }
    
    /**
     * 文字列形式のセルの範囲が同じかどうか比較する。
     * @since 0.5
     * @param region1
     * @param region2
     * @return
     */
    public static boolean equalsRegion(final CellRangeAddressList region1, final CellRangeAddressList region2) {
        
        return equalsSqref(convertSqref(region1), convertSqref(region2));
        
    }
    
//    public static boolean removeDataValidation(final Sheet sheet, final DataValidation dataValidation) {
//        ArgUtils.notNull(sheet, "sheet");
//        ArgUtils.notNull(dataValidation, "dataValidation");
//        
//        if(sheet instanceof XSSFSheet) {
//            final XSSFSheet xssfSheet = (XSSFSheet) sheet;
//            final XSSFDataValidation xssfDataValidation = (XSSFDataValidation) dataValidation;
//            
//            try {
//                final Field fWorksheet = XSSFSheet.class.getDeclaredField("worksheet");
//                fWorksheet.setAccessible(true);
//                CTWorksheet worksheet = (CTWorksheet) fWorksheet.get(xssfSheet);
//                
//                // 既存の入力規則の取得
//                CTDataValidations dataValidations = worksheet.getDataValidations();
//                if(dataValidations == null) {
//                    return false;
//                }
//                
//                final Method mCtVal = XSSFDataValidation.class.getDeclaredMethod("getCtDdataValidation");
//                mCtVal.setAccessible(true);
//                
//                CTDataValidation removeVal = (CTDataValidation) mCtVal.invoke(xssfDataValidation);
//                
//                List<CTDataValidation> newList = new ArrayList<>();
//                for(int i=0; i < dataValidations.getCount(); i++) {
//                    CTDataValidation itemVal = dataValidations.getDataValidationArray(i);
//                    if(!itemVal.equals(removeVal)) {
//                        newList.add(itemVal);
//                    }
//                    
//                }
//                
//                // 削除された（サイズが変わった）場合に、入力規則を設定し直す。
//                if(newList.size() != dataValidations.getCount()) {
//                    dataValidations.setDataValidationArray(newList.toArray(new CTDataValidation[newList.size()]));
//                    return true;
//                }
//                
//                return false;
//                
//            } catch (Exception e) {
//                throw new RuntimeException("fail remove sheet validation rule.", e);
//            }
//            
//        } else if(sheet instanceof HSSFSheet) {
//            
//            final HSSFSheet hssfSheet = (HSSFSheet) sheet;
//            final HSSFDataValidation hssfDataValidation = (HSSFDataValidation) dataValidation;
//            try {
//                
//                Field fWorksheet = HSSFSheet.class.getDeclaredField("_sheet");
//                fWorksheet.setAccessible(true);
//                InternalSheet worksheet = (InternalSheet) fWorksheet.get(hssfSheet);
//                
//                DataValidityTable dvt = worksheet.getOrCreateDataValidityTable();
//                final CellRangeAddressList removeRegion = hssfDataValidation.getRegions();
//                
//                // シート内の入力規則のデータを検索して、一致するものがあれば書き換える。
//                final AtomicBoolean removed = new AtomicBoolean(false);
//                RecordVisitor visitor = new RecordVisitor() {
//                    
//                    @Override
//                    public void visitRecord(final Record r) {
//                        if (!(r instanceof DVRecord)) {
//                            return;
//                        }
//                        
//                        final DVRecord dvRecord = (DVRecord) r;
//                        final CellRangeAddressList region = dvRecord.getCellRangeAddress();
//                        if(equalsRegion(region, removeRegion)) {
//                            //TODO:
//                            
//                        }
//                        
//                    }
//                };
//                
//                dvt.visitContainedRecords(visitor);
//                
//                return removed.get();
//                
//            } catch (Exception e) {
//                throw new RuntimeException("fail remove sheet validation rule.", e);
//            }
//            
//        } else {
//            throw new UnsupportedOperationException("not supported remove dava validation's region for type " + sheet.getClass().getName());
//        }
//    }
    
    /**
     * テンプレートの入力規則の制約「リスト」を追加する。
     * <p>POI-3.7以上が必要。
     * @param sheet シート
     * @param constraints 制約とするコレクションの中身
     * @param startPosition 開始位置
     * @param endPosition 終了位置
     */
    public static void setupExplicitListConstaint(final Sheet sheet, final Collection<String> constraints,
            final Point startPosition, final Point endPosition) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(constraints, "constraints");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");
        
        setupExplicitListConstaint(sheet, constraints.toArray(new String[constraints.size()]),
                startPosition, endPosition);
    }
        
    /**
     * テンプレートの入力規則の制約「リスト」を追加する。
     * <p>POI-3.7以上が必要。
     * @param sheet シート
     * @param constraints 制約とするリストの中身
     * @param startPosition 開始位置
     * @param endPosition 終了位置
     */
    public static void setupExplicitListConstaint(final Sheet sheet, final String[] constraints,
            final Point startPosition, final Point endPosition) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(constraints, "constraints");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");
        
        final DataValidationHelper helper = sheet.getDataValidationHelper();
        final DataValidationConstraint constraint = helper.createExplicitListConstraint(constraints);
        setupConstaint(sheet, constraint, startPosition, endPosition);
        
    }
    
    /**
     * テンプレートの入力規則の制約「リスト」を式形式で追加する。
     * <p>POI-3.7以上が必要。
     * @param sheet シート
     * @param listFormula 入力規則の式('='は含まない)
     * @param startPosition 設定するセルの開始位置
     * @param endPosition 設定するセルの終了位置
     */
    public static void setupFormulaListConstaint(final Sheet sheet, final String listFormula,
            final Point startPosition, final Point endPosition) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(listFormula, "listFormula");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");
        
        final DataValidationHelper helper = sheet.getDataValidationHelper();
        final DataValidationConstraint constraint = helper.createFormulaListConstraint("=" + listFormula);
        setupConstaint(sheet, constraint, startPosition, endPosition);
    }
    
    /**
     * 指定した範囲のセルに制約を追加する。
     * <p>POI-3.7以上が必要。
     * @param sheet シート
     * @param constraint 制約
     * @param startPosition 設定するセルの開始位置
     * @param endPosition 設定するセルの終了位置
     */
    public static void setupConstaint(final Sheet sheet, final DataValidationConstraint constraint,
            final Point startPosition, final Point endPosition) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(constraint, "constraint");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");
        
        final DataValidationHelper helper = sheet.getDataValidationHelper();
        
        final CellRangeAddressList region = new CellRangeAddressList(
                startPosition.y, endPosition.y,
                startPosition.x, endPosition.x
                );
        final DataValidation dataValidation = helper.createValidation(constraint, region);
        sheet.addValidationData(dataValidation);
    }
    
    /**
     * 指定した範囲の名前を登録する。
     * <p>POI-3.7以上が必要。
     * <p>指定した名前が既に存在する場合は、新しい範囲に書き換える。
     * @param sheet シート
     * @param name 名前
     * @param startPosition 設定するセルの開始位置
     * @param endPosition 設定するセルの終了位置
     * @return
     */
    public static Name defineName(final Sheet sheet, final String name,
            final Point startPosition, final Point endPosition) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(name, "name");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");
        
        final Workbook workbook = sheet.getWorkbook();
        Name nameObj = workbook.getName(name);
        if(nameObj == null) {
            nameObj = workbook.createName();
            nameObj.setNameName(name);
        }
        
        final AreaReference areaRef = buildNameArea(sheet.getSheetName(), startPosition, endPosition);
        nameObj.setRefersToFormula(areaRef.formatAsString());
        
        return nameObj;
        
    }
    
    /**
     * 名前の範囲の形式を組み立てる。
     * <code>シート名!$A$1:$A:$5</code>
     * @param sheetName シート名
     * @param startPosition 設定するセルの開始位置
     * @param endPosition 設定するセルの終了位置
     * @return
     */
    public static AreaReference buildNameArea(final String sheetName,
            final Point startPosition, final Point endPosition) {
        
        ArgUtils.notEmpty(sheetName, "sheetName");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");
        
        final CellReference firstRefs = new CellReference(sheetName, startPosition.y, startPosition.x, true, true);
        final CellReference lastRefs = new CellReference(sheetName, endPosition.y, endPosition.x, true, true);
        
        return new AreaReference(firstRefs, lastRefs);
    }
    
    /**
     * セルに設定されているハイパーリンクを削除する。
     * <p>POIのバージョンによって、{@link Cell#removeHyperlink()}のネイティブのメソッドを呼び出す。
     * @since 0.4
     * @param cell
     * @return true: ハイパーリンクが設定されており削除できた場合。false:ハイパーリンクが設定されていない場合。
     * @throws IllegalArgumentException cell == null.
     */
    public static boolean removeHyperlink(final Cell cell) {
        
        ArgUtils.notNull(cell, "cell");
        
        final Hyperlink link = cell.getHyperlink();
        if(link == null) {
            return false;
        }
        
        if(AVAILABLE_METHOD_CELL_REMOVE_HYPERLINK) {
            cell.removeHyperlink();
            return true;
        } else {
            // 既存のハイパーリンクのURLをクリアし、再設定する。
            link.setAddress("");
            cell.setHyperlink(link);
            return true;
        }
        
    }
    
    /**
     * セルの範囲が重複（交錯）しているかどうか判定する。
     * <p>このメソッドは、POI-3.14で追加されたメソッド{@literal Sheet#intersects(...)}と後方互換性を保つためのもの。</p>
     *
     * @param my
     * @param other
     * @return trueの場合、1つでもセルの範囲が重複している。
     */
    public static boolean intersectsRegion(final CellRangeAddressBase my, final CellRangeAddressBase other) {
        return my.getFirstRow() <= other.getLastRow() &&
                my.getFirstColumn() <= other.getLastColumn() &&
                other.getFirstRow() <= my.getLastRow() &&
                other.getFirstColumn() <= my.getLastColumn();
    }
    
}
