package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;

import com.gh.mygreen.xlsmapper.cellconvert.LinkType;

/**
 * Apache POIとJExcel APIの差を埋めるユーティリティクラス。
 * 
 * @author T.TSUCHIE
 *
 */
public class POIUtils {
    
    /** 標準のセルフォーマッター */
    private static POICellFormatter defaultCellFormatter = new POICellFormatter();
    
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
     * セルの値を取得する
     * 
     * @param cell
     * @return
     */
//    public static String getCellContents(final Cell cell) {
//        return getCellContents(cell, defaultCellFormatter);
//    }
    
    /**
     * フォーマッターを指定してセルの値を取得する
     * 
     * @param cell
     * @param cellFormatter 
     * @return
     */
    public static String getCellContents(final Cell cell, final POICellFormatter cellFormatter) {
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
     * @param cell
     * @param cellFormatter
     * @return
     */
    public static boolean isEmptyCellContents(final Cell cell, final POICellFormatter cellFormatter) {
        ArgUtils.notNull(cell, "cell");
        ArgUtils.notNull(cellFormatter, "cellFormatter");
        
        if(isBlankCell(cell)) {
            return true;
        }
        return getCellContents(cell, cellFormatter).isEmpty();
    }
    
    /**
     * セルの値が空かどうか。
     * @param cell
     * @return
     */
    public static boolean isBlankCell(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        
        return cell.getCellType() == Cell.CELL_TYPE_BLANK;
    }
    
    /**
     * 指定した書式のインデックス番号を取得する。シートに存在しない場合は、新しく作成する。
     * @param sheet
     * @param pattern
     * @return
     */
    public static short getDataFormatIndex(final Sheet sheet, final String pattern) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(pattern, "pattern");
        
        return sheet.getWorkbook().getCreationHelper().createDataFormat().getFormat(pattern);
        
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
     * 指定した範囲の結合を解除する。
     * @param sheet
     * @param mergedRange
     * @return 引数で指定した結合が見つからない場合。
     */
    public static boolean removeMergedRange(final Sheet sheet, final CellRangeAddress mergedRange) {
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notNull(mergedRange, "mergedRange");
        
        final int num = sheet.getNumMergedRegions();
        for(int i=0; i < num; i ++) {
            final CellRangeAddress range = sheet.getMergedRegion(i);
            if(range.equals(mergedRange)) {
                sheet.removeMergedRegion(i);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 行をN行追加する
     * @param rowIndex
     * @return
     */
    public static Row insertRow(final Sheet sheet, final int rowIndex) {
        
        // 最終行を取得する
        int lastRow = sheet.getLastRowNum();
        
        sheet.shiftRows(rowIndex, lastRow+1, 1);
        return sheet.createRow(rowIndex);
    }
    
    /**
     * セルの折り返し設定を有効にする
     * @param cell
     * @param forceWrapText trueの場合有効にする。falseの場合は変更しない。
     */
    public static void wrapCellText(final Cell cell, final boolean forceWrapText) {
        
        if(!forceWrapText) {
            return;
        }
        
        final CellStyle style = cell.getCellStyle();
        style.setWrapText(true);
        setCellStyleWithShrinkToFit(cell, style, false);
        
        cell.setCellStyle(style);
    }
    
    public static void shrinkToFit(final Cell cell, final boolean forceShrinkToFit) {
        if(!forceShrinkToFit) {
            return;
        }
        
        final CellStyle style = cell.getCellStyle();
        
        style.setWrapText(false);
        setCellStyleWithShrinkToFit(cell, style, true);
    }

    private static void setCellStyleWithShrinkToFit(final Cell cell, final CellStyle style, final boolean shrinkToFit) {
        
        try {
            //POI-3.10以降
            final Method method = style.getClass().getMethod("setShrinkToFit", boolean.class);
            method.setAccessible(true);
            method.invoke(style, true);
            
            cell.setCellStyle(style);
            
            return;
            
        } catch (Exception e) { } 
        
        if(style instanceof HSSFCellStyle) {
            // POI-3.9以前のExcel2003形式
            try {
                final Field field = style.getClass().getDeclaredField("_format");
                field.setAccessible(true);
                
                ExtendedFormatRecord record = (ExtendedFormatRecord) field.get(style);
                record.setShrinkToFit(shrinkToFit);
                
                cell.setCellStyle(style);
                return;
            } catch (Exception e ) {
//                e.printStackTrace();
            }
            
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
            } catch (Exception e ) {
//                e.printStackTrace();
            }
            
            
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
     * @param address
     * @return
     * @throws IllegalArgumentException address == null.
     */
    public static String formatCellAddress(final Point cellAddress) {
        ArgUtils.notNull(cellAddress, "cellAddress");
        return formatCellAddress(cellAddress.y, cellAddress.x);
    }
    
    /**
     * セルのアドレス'A1'を取得する。
     * @param cell
     * @return IllegalArgumentException cell == null.
     */
    public static String formatCellAddress(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        return CellReference.convertNumToColString(cell.getColumnIndex()) + String.valueOf(cell.getRowIndex()+1);
    }
    
    /**
     * リンクのアドレスを判定する。
     * @param linkAddress
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
    
}
