package com.gh.mygreen.xlsmapper.util;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.CellFormatter;
import com.gh.mygreen.xlsmapper.DefaultCellFormatter;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.cellconverter.ConversionException;
import com.gh.mygreen.xlsmapper.cellconverter.LinkType;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;
import com.github.mygreen.cellformatter.POICell;

/**
 * Apache POIとJExcel APIの差を埋めるユーティリティクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class POIUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(POIUtils.class);
    
    /** 標準のセルフォーマッター */
    private static CellFormatter defaultCellFormatter = new DefaultCellFormatter();
    
    /**
     * シートの種類を判定する。
     * 
     * @since 2.0
     * @param sheet 判定対象のオブジェクト
     * @return シートの種類。不明な場合はnullを返す。
     * @throws NullPointerException {@literal sheet == null}
     */
    public static SpreadsheetVersion getVersion(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        
        if(sheet instanceof HSSFSheet) {
            return SpreadsheetVersion.EXCEL97;
            
        } else if(sheet instanceof XSSFSheet) {
            return SpreadsheetVersion.EXCEL2007;
        }
        
        return null;
    }
    
    /**
     * シートの最大列数を取得する。
     * <p>{@literal jxl.Sheet.getColumns()}</p>
     * @param sheet シートオブジェクト
     * @return 最大列数
     * @throws NullPointerException {@link sheet == null.}
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
     * <p>{@literal jxl.Sheet.getRows()}</p>
     * @param sheet シートオブジェクト
     * @return 最大行数
     * @throws NullPointerException {@link sheet == null.}
     */
    public static int getRows(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        return sheet.getLastRowNum() + 1;
    }
    
    /**
     * シートから任意アドレスのセルを取得する。
     * @since 0.5
     * @param sheet シートオブジェクト
     * @param address アドレス（Point.x=column, Point.y=row）
     * @return セル
     * @throws NullPointerException {@link sheet == null or address == null.}
     */
    public static Cell getCell(final Sheet sheet, final Point address) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(address, "address");
        return getCell(sheet, address.x, address.y);
    }
    
    /**
     * シートから任意アドレスのセルを取得する。
     * @since 1.4
     * @param sheet シートオブジェクト
     * @param address セルのアドレス
     * @return セル
     * @throws NullPointerException {@link sheet == null or address == null.}
     */
    public static Cell getCell(final Sheet sheet, final CellAddress address) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(address, "address");
        return getCell(sheet, address.getColumn(), address.getRow());
    }
    
    /**
     * シートから任意アドレスのセルを取得する。
     * 
     * <p>{@literal jxl.Sheet.getCell(int column, int row)}</p>
     * @param sheet シートオブジェクト
     * @param column 列番号（0から始まる）
     * @param row 行番号（0から始まる）
     * @return セル
     * @throws NullPointerException {@link sheet == null}
     */
    public static Cell getCell(final Sheet sheet, final int column, final int row) {
        ArgUtils.notNull(sheet, "sheet");
        
        Row rows = sheet.getRow(row);
        if(rows == null) {
            rows = sheet.createRow(row);
        }
        
        Cell cell = rows.getCell(column);
        if(cell == null) {
            cell = rows.createCell(column, CellType.BLANK);
        }
        
        return cell;
    }
    
    /**
     * 任意の行のセルを全て取得する。
     * <p> {@literal jxl.Seet.getRow(int row)}</p>
     * @param sheet シートオブジェクト
     * @param row 行番号（0から始まる）
     * @return 行レコード（カラムの集合）。
     *         ただし、シートの最大列数以下の場合、空のセルを補完する。
     * @throws NullPointerException {@link sheet == null}
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
                cell = rows.createCell(i, CellType.BLANK);
            }
            cells[i] = cell;
        }
        
        return cells;
    }
    
    /**
     * 任意の列のセルを全て取得する。
     * <p> {@literal jxl.Seet.getColumn(int col)}</p>
     * @param sheet
     * @param col 列番号（0から始まる）
     * @return 列レコード（行の集合）。
     *         ただし、シートの最大行数以下の場合、空のセルを補完する。
     * @throws NullPointerException {@link sheet == null}
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
                cell = rows.createCell(col, CellType.BLANK);
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
     * <p>ブランクセルなどの判定は優先的に行う。</p>
     * @param cell
     * @return
     */
    public static boolean isEmptyCellContents(final Cell cell) {
        return isEmptyCellContents(cell, defaultCellFormatter);
    }
    
    /**
     * フォーマッターを指定してセルの値が空かどうか判定する。
     * <p>ブランクセルなどの判定は優先的に行う。</p>
     * @param cell セル
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
     * @return 結合した範囲のアドレス情報
     * @throws NullPointerException {@literal sheet == null}
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
                cell.setCellType(CellType.BLANK);
            }
        }
        
        final CellRangeAddress range = new CellRangeAddress(startRow, endRow, startCol, endCol);
        sheet.addMergedRegion(range);
        return range;
    }
    
    /**
     * 指定したセルのアドレスの結合情報を取得する。
     * @since 0.5
     * @param sheet シート情報
     * @param rowIdx 行番号
     * @param colIdx 列番号
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
            
        } else if(linkAddress.matches("[\\p{Alpha}]+[0-9]+")) {
            // A1の通常のアドレスの形式
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
    
    /**
     * 日時の開始日が1904年かどうか。
     * 通常は、1900年始まり。
     * @param workbook ワークブック
     * @return trueの場合は、1904年始まり。falseの場合は、1900年始まり。
     */
    public static boolean isDateStart1904(final Workbook workbook) {
        
        if(workbook instanceof HSSFWorkbook) {
            try {
                Method method = HSSFWorkbook.class.getDeclaredMethod("getWorkbook");
                method.setAccessible(true);
                
                InternalWorkbook iw = (InternalWorkbook) method.invoke(workbook);
                return iw.isUsing1904DateWindowing();
                
            } catch(NoSuchMethodException | SecurityException e) {
                logger.warn("fail access method HSSFWorkbook.getWorkbook.", e);
                return false;
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.warn("fail invoke method HSSFWorkbook.getWorkbook.", e);
                return false;
            }
            
        } else if(workbook instanceof XSSFWorkbook) {
            try {
                Method method = XSSFWorkbook.class.getDeclaredMethod("isDate1904");
                method.setAccessible(true);
                
                boolean value = (boolean) method.invoke(workbook);
                return value;
                
            } catch(NoSuchMethodException | SecurityException e) {
                logger.warn("fail access method XSSFWorkbook.isDate1904.", e);
                return false;
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.warn("fail invoke method XSSFWorkbook.isDate1904.", e);
                return false;
            }
            
        } else {
            logger.warn("unknown workbook type.", workbook.getClass().getName());
        }
        
        return false;
    }

    /**
     * セルに設定する数式をアノテーションから組み立てる。
     * 
     * @since 1.5
     * @param accessor フィールド
     * @param formulaAnno 数式定義用のアノテーション。
     * @param config システム設定。
     * @param cell 設定対象のセル。
     * @param targetBean 処理対象のJavaBean.
     * @return 数式。
     * @throws XlsMapperException
     */
    public static String getFormulaValue(final FieldAccessor accessor, final XlsFormula formulaAnno,
            final XlsMapperConfig config, final Cell cell, final Object targetBean) throws XlsMapperException {
        
        if(Utils.isNotEmpty(formulaAnno.value())) {
            final Map<String, Object> vars = new HashMap<>();
            vars.put("rowIndex", cell.getRowIndex());
            vars.put("columnIndex", cell.getColumnIndex());
            vars.put("rowNumber", cell.getRowIndex()+1);
            vars.put("columnNumber", cell.getColumnIndex()+1);
            vars.put("columnAlpha", CellReference.convertNumToColString(cell.getColumnIndex()));
            vars.put("address", formatCellAddress(cell));
            vars.put("targetBean", targetBean);
            vars.put("cell", cell);
            
            try {
                return config.getFormulaFormatter().interpolate(formulaAnno.value(), vars);
            } catch(Exception e) {
                throw new AnnotationInvalidException(formulaAnno, MessageBuilder.create("anno.attr.invalidEL")
                        .var("property", accessor.getNameWithClass())
                        .var("attr", XlsFormula.class)
                        .var("attrName", "value")
                        .var("attrValue", formulaAnno.value())
                        .format(), e);
            }
            
        } else if(Utils.isNotEmpty(formulaAnno.methodName())) {
            
            // 戻り値が文字列の数式を返すメソッドを探す
            final Class<?> targetClass = targetBean.getClass();
            Method method = null;
            for(Method m : targetClass.getDeclaredMethods()) {
                if(m.getName().equals(formulaAnno.methodName())
                        && m.getReturnType().equals(String.class)) {
                    method = m;
                    break;
                }
            }
            
            if(method == null) {
                throw new AnnotationInvalidException(formulaAnno, MessageBuilder.create("anno.attr.notFoundMethod")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsFormula.class)
                        .var("attrName", "methodName")
                        .var("attrValue", formulaAnno.methodName())
                        .varWithClass("definedClass", targetClass)
                        .format());
            }
            
            // メソッドの引数の組み立て
            final Class<?>[] paramTypes = method.getParameterTypes();
            final Object[] paramValues = new Object[paramTypes.length];
            
            for(int i=0; i < paramTypes.length; i++) {
                if(Cell.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = cell;
                    
                } else if(CellAddress.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = CellAddress.of(cell);
                    
                } else if(Point.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = CellAddress.of(cell).toPoint();
                    
                } else if(org.apache.poi.ss.util.CellAddress.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = CellAddress.of(cell).toPoiCellAddress();
                    
                } else if(Sheet.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = cell.getSheet();
                    
                } else if(XlsMapperConfig.class.isAssignableFrom(paramTypes[i])) {
                    paramValues[i] = config;
                    
                } else {
                    paramValues[i] = null;
                }
            }
            
            // メソッドの実行
            try {
                method.setAccessible(true);
                return (String) method.invoke(targetBean, paramValues);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Throwable t = e.getCause() == null ? e : e.getCause();
                throw new XlsMapperException(
                        String.format("Fail execute method '%s#%s'.", targetClass.getName(), method.getName()),
                        t);
            }
            
        } else {
            throw new AnnotationInvalidException(formulaAnno, MessageBuilder.create("anno.attr.required.any")
                    .var("property", accessor.getNameWithClass())
                    .varWithAnno("anno", XlsFormula.class)
                    .varWithArrays("attrNames", "value", "methodName")
                    .format());
        }
        
    }

    /**
     * セルに数式を設定する。
     * @since 1.5
     * 
     * @param accessor フィールド
     * @param formulaAnno 数式定義用のアノテーション。
     * @param config システム設定。
     * @param cell 設定対象のセル。
     * @param targetBean 処理対象のJavaBean.
     * @throws XlsMapperException
     */
    public static void setupCellFormula(final FieldAccessor accessor, final XlsFormula formulaAnno,
            final XlsMapperConfig config, final Cell cell, final Object targetBean) throws XlsMapperException {
        
        ArgUtils.notNull(accessor, "adaptor");
        ArgUtils.notNull(formulaAnno, "formulaAnno");
        ArgUtils.notNull(config, "config");
        ArgUtils.notNull(cell, "cell");
        
        final String formula = getFormulaValue(accessor, formulaAnno, config, cell, targetBean);
        if(Utils.isEmpty(formula)) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        
        try {
            cell.setCellFormula(formula);
            cell.setCellType(CellType.FORMULA);
            
        } catch(FormulaParseException e) {
            // 数式の解析に失敗した場合
            final String message = new StringBuilder()
                    .append(String.format("Fail parse formula '%s'.", formula))
                    .append(String.format(" Cell '%s' map from '%s#%s'.", 
                            formatCellAddress(cell), accessor.getDeclaringClass().getName(), accessor.getName()))
                    .toString();
                
            throw new ConversionException(message, e, accessor.getType());
        }
    }
    
    /**
     * アノテーション{@link XlsCellOption}を元に、セルの制御の設定「折り返し設定」「縮小して表示」を設定します。
     * @param cell セル
     * @param cellOptionAnno セルの制御を設定するアノテーション。
     * @throws NullPointerException {@literal cell == null.}
     */
    public static void setupCellOption(final Cell cell, final XlsCellOption cellOptionAnno) {
        
        ArgUtils.notNull(cell, "cell");
        
        if(cellOptionAnno.shrinkToFit()) {
            cell.getCellStyle().setShrinkToFit(true);
            
        } else if(cellOptionAnno.wrapText()) {
            cell.getCellStyle().setWrapText(true);
            
        }
        
    }
    
}
