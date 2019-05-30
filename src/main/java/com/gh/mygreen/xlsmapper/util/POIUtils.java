package com.gh.mygreen.xlsmapper.util;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.CellFormatter;
import com.gh.mygreen.xlsmapper.DefaultCellFormatter;
import com.github.mygreen.cellformatter.lang.ExcelDateUtils;

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
     * @throws IllegalArgumentException {@literal sheet == null}
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
     * @throws IllegalArgumentException {@literal sheet == null.}
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
     * @throws IllegalArgumentException {@literal sheet == null.}
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
     * @throws IllegalArgumentException {@literal sheet == null or address == null.}
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
     * @throws IllegalArgumentException {@literal sheet == null or address == null.}
     */
    public static Cell getCell(final Sheet sheet, final CellPosition address) {
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
     * @throws IllegalArgumentException {@literal sheet == null}
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
     * @throws IllegalArgumentException {@literal sheet == null}
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
     * @throws IllegalArgumentException {@literal sheet == null}
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
     * @return フォーマットした文字列
     * @throws IllegalArgumentException {@literal cell or cellFormatter is null.}
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
     * @param cellFormatter セルのフォーマッタ
     * @throws IllegalArgumentException {@literal  sheet == null or cellFormatter == null.}
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
     * @param cellFormatter セルのフォーマッタ
     * @return 書式が設定されていない場合は、空文字を返す。
     *         cellがnullの場合も空文字を返す。
     *         標準の書式の場合も空文字を返す。
     */
    public static String getCellFormatPattern(final Cell cell, final CellFormatter cellFormatter) {
        if(cell == null) {
            return "";
        }

        String pattern = cellFormatter.getPattern(cell);
        if(pattern.equalsIgnoreCase("general")) {
            return "";
        }

        return pattern;

    }

    /**
     * 指定した範囲のセルを結合する。
     * @param sheet
     * @param startCol
     * @param startRow
     * @param endCol
     * @param endRow
     * @return 結合した範囲のアドレス情報
     * @throws IllegalArgumentException {@literal sheet == null}
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
     * 領域の列サイズ（横セル数）を計算します。
     *
     * @since 2.0
     * @param region 領域
     * @return 列サイズ（横セル数）。引数がnullの時は、0を返します。
     */
    public static int getColumnSize(final CellRangeAddress region) {
        if(region == null) {
            return 0;
        }
        return region.getLastColumn() - region.getFirstColumn() + 1;
    }

    /**
     * 領域の行サイズ（行セル数）を計算します。
     *
     * @since 2.0
     * @param region 領域
     * @return 行サイズ（行セル数）。引数がnullの時は、0を返します。
     */
    public static int getRowSize(final CellRangeAddress region) {
        if(region == null) {
            return 0;
        }
        return region.getLastRow() - region.getFirstRow() + 1;
    }

    /**
     * 指定した行の下に行を1行追加する
     * @param sheet
     * @param rowIndex 追加する行数
     * @return 追加した行を返す。
     */
    public static Row insertRow(final Sheet sheet, final int rowIndex) {

        ArgUtils.notNull(sheet, "sheet");
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
     * @return 不明な場合は{@link HyperlinkType#NONE}を返す。
     * @throws IllegalArgumentException linkAddress が空文字の場合。
     */
    public static HyperlinkType judgeLinkType(final String linkAddress) {

        ArgUtils.notEmpty(linkAddress, "linkAddress");

        if(linkAddress.matches(".*![\\p{Alnum}]+")) {
            // !A1のアドレスを含むかどうか
            return HyperlinkType.DOCUMENT;

        } else if(linkAddress.matches("[\\p{Alpha}]+[0-9]+")) {
            // A1の通常のアドレスの形式
            return HyperlinkType.DOCUMENT;

        } else if(linkAddress.matches(".+@.+")) {
            // @を含むかどうか
            return HyperlinkType.EMAIL;

        } else if(linkAddress.matches("[\\p{Alpha}]+://.+")) {
            // プロトコル付きかどうか
            return HyperlinkType.URL;

        } else if(linkAddress.matches(".+\\.[\\p{Alnum}]+")) {
            // 拡張子付きかどうか
            return HyperlinkType.FILE;

        } else {
            return HyperlinkType.NONE;
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

        final AreaReference areaRef = buildNameArea(sheet.getSheetName(), startPosition, endPosition,
                sheet.getWorkbook().getSpreadsheetVersion());
        nameObj.setRefersToFormula(areaRef.formatAsString());

        return nameObj;

    }

    /**
     * 名前の範囲の形式を組み立てる。
     * <code>シート名!$A$1:$A:$5</code>
     * @param sheetName シート名
     * @param startPosition 設定するセルの開始位置
     * @param endPosition 設定するセルの終了位置
     * @param sheetVersion シートの形式
     * @return
     */
    public static AreaReference buildNameArea(final String sheetName,
            final Point startPosition, final Point endPosition, SpreadsheetVersion sheetVersion) {

        ArgUtils.notEmpty(sheetName, "sheetName");
        ArgUtils.notNull(startPosition, "startPosition");
        ArgUtils.notNull(endPosition, "endPosition");

        final CellReference firstRefs = new CellReference(sheetName, startPosition.y, startPosition.x, true, true);
        final CellReference lastRefs = new CellReference(sheetName, endPosition.y, endPosition.x, true, true);

        return new AreaReference(firstRefs, lastRefs, sheetVersion);
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
     * 結合を考慮してセルの罫線（上部）を取得する。
     *
     * @param cell セル
     * @return {@literal BorderStyle}
     * @throws IllegalArgumentException {@literal cell is null.}
     */
    public static BorderStyle getBorderTop(final Cell cell) {

        ArgUtils.notNull(cell, "cell");

        final Sheet sheet = cell.getSheet();
        CellRangeAddress mergedRegion = getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());

        final Cell target;
        if(mergedRegion == null) {
            // 結合されていない場合
            target = cell;

        } else {
            if(mergedRegion.getFirstRow() == cell.getRowIndex()) {
                // 引数のCellが上部のセルの場合
                target = cell;
            } else {
                target = getCell(sheet, cell.getColumnIndex(), mergedRegion.getFirstRow());
            }

        }

        final CellStyle style = target.getCellStyle();
        if(style == null) {
            return BorderStyle.NONE;
        } else {
            return style.getBorderTopEnum();
        }

    }

    /**
     * 結合を考慮してセルの罫線（下部）を取得する。
     *
     * @param cell セル
     * @return {@literal BorderStyle}
     * @throws IllegalArgumentException {@literal cell is null.}
     */
    public static BorderStyle getBorderBottom(final Cell cell) {

        ArgUtils.notNull(cell, "cell");

        final Sheet sheet = cell.getSheet();
        CellRangeAddress mergedRegion = getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());

        final Cell target;
        if(mergedRegion == null) {
            // 結合されていない場合
            target = cell;

        } else {
            if(mergedRegion.getLastRow() == cell.getRowIndex()) {
                // 引数のCellが下部のセルの場合
                target = cell;
            } else {
                target = getCell(sheet, cell.getColumnIndex(), mergedRegion.getLastRow());
            }

        }

        final CellStyle style = target.getCellStyle();
        if(style == null) {
            return BorderStyle.NONE;
        } else {
            return style.getBorderBottomEnum();
        }

    }

    /**
     * 結合を考慮してセルの罫線（左部）を取得する。
     *
     * @param cell セル
     * @return {@literal BorderStyle}
     * @throws IllegalArgumentException {@literal cell is null.}
     */
    public static BorderStyle getBorderRight(final Cell cell) {

        ArgUtils.notNull(cell, "cell");

        final Sheet sheet = cell.getSheet();
        CellRangeAddress mergedRegion = getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());

        final Cell target;
        if(mergedRegion == null) {
            // 結合されていない場合
            target = cell;

        } else {
            if(mergedRegion.getLastColumn() == cell.getColumnIndex()) {
                // 引数のCellが右部のセルの場合
                target = cell;
            } else {
                target = getCell(sheet, mergedRegion.getLastColumn(), cell.getRowIndex());
            }

        }

        final CellStyle style = target.getCellStyle();
        if(style == null) {
            return BorderStyle.NONE;
        } else {
            return style.getBorderRightEnum();
        }

    }

    /**
     * 結合を考慮してセルの罫線（右部）を取得する。
     *
     * @param cell セル
     * @return {@literal BorderStyle}
     * @throws IllegalArgumentException {@literal cell is null.}
     */
    public static BorderStyle getBorderLeft(final Cell cell) {

        ArgUtils.notNull(cell, "cell");

        final Sheet sheet = cell.getSheet();
        CellRangeAddress mergedRegion = getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());

        final Cell target;
        if(mergedRegion == null) {
            // 結合されていない場合
            target = cell;

        } else {
            if(mergedRegion.getFirstColumn() == cell.getColumnIndex()) {
                // 引数のCellが左部のセルの場合
                target = cell;
            } else {
                target = getCell(sheet, mergedRegion.getFirstColumn(), cell.getRowIndex());
            }

        }

        final CellStyle style = target.getCellStyle();
        if(style == null) {
            return BorderStyle.NONE;
        } else {
            return style.getBorderLeftEnum();
        }

    }

    /**
     * ハイパーリンクを取得する。
     * <p>結合されているセルの場合にも対応。
     * @param cell
     * @return 見つからない場合は、nullを返す。
     * @throws IllegalArgumentException {@literal cell is null.}
     */
    public static Hyperlink getHyperlink(final Cell cell) {

        ArgUtils.notNull(cell, "cell");

        Hyperlink link = cell.getHyperlink();
        if(link != null) {
            return link;
        }

        final Sheet sheet = cell.getSheet();
        CellRangeAddress mergedRange = getMergedRegion(sheet, cell.getRowIndex(), cell.getColumnIndex());
        if(mergedRange == null) {
            return null;
        }

        for(Hyperlink item : sheet.getHyperlinkList()) {
            if(item.getFirstRow() == mergedRange.getFirstRow()
                    && item.getFirstColumn() == mergedRange.getFirstColumn()) {
                return item;
            }

        }

        return null;

    }

    /**
     * {@literal 1900-01-01 00:00:00.000}の時間（単位はミリ秒）。
     * <p>Excelは設定により、1900年始まりか1904年始まりか指定できるため、その基準値として利用する。
     */
    public static final long MILLISECONDS_19000101_END = ExcelDateUtils.parseDate("1900-01-01 23:59:54.999").getTime();

    /**
     * セルに日時を設定する。
     * <p>1900年1月0日となる経過時間指定の場合は、POIのバグにより設定できあいため、数値として設定する。</p>
     *
     * @param cell 設定するセル
     * @param date セルに設定する日時
     * @param dateStart1904 1904年始まりの設定のシートかどうか
     */
    public static void setCellValueAsDate(Cell cell, Date date, boolean dateStart1904) {

        ArgUtils.notNull(cell, "cell");
        ArgUtils.notNull(date, "date");

        if(dateStart1904) {
            // 1904年始まりの場合は、そのまま設定する
            cell.setCellValue(date);

        } else {

            long timemills = date.getTime();
            if(timemills <= MILLISECONDS_19000101_END) {
                // 1900年1月0日の場合は、数値に変換してから設定する
                // タイムゾーンを除去する
                Date strip = new Date(date.getTime() + TimeZone.getDefault().getRawOffset());
                double num = ExcelDateUtils.convertExcelNumber(strip, dateStart1904);
                cell.setCellValue(num);

            } else {
                cell.setCellValue(date);
            }

        }

    }

}
