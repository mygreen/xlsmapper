package com.gh.mygreen.xlsmapper.util;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;

/**
 * 指定したラベルを持つセルを検索するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellFinder {
    
    
    /**
     * シート情報
     */
    private final Sheet sheet;
    
    /**
     * システム設定
     */
    private final Configuration config;
    
    /**
     * 検索対象のラベル
     */
    private final String label;
    
    /**
     * 起点となる行番号
     * ・指定しない場合は、-1を指定する
     */
    private int startRow = -1;
    
    /**
     * 起点となる列番号
     * ・指定しない場合は、-1を指定する
     */
    private int startColumn = -1;
    
    /**
     * 起点となる位置を除外するかどうか
     */
    private boolean excludeStartPoisition = false;
    
    /**
     * 検索する際の条件を組み立てる
     * @param sheet 検索対象のシート
     * @param label 検索するセルのラベル
     * @param config システム設定。
     *        設定値 {@link Configuration#isNormalizeLabelText()}、{@link Configuration#isRegexLabelText()}の値によって、
     *        検索する際にラベルを正規化、または正規表現により一致するかで判定を行う。
     */
    public static CellFinder query(final Sheet sheet, final String label, final Configuration config) {
        return new CellFinder(sheet, label, config);
    }
    
    private CellFinder(final Sheet sheet, final String label, final Configuration config) {
        
        ArgUtils.notNull(sheet, "sheet");
        ArgUtils.notEmpty(label, "label");
        ArgUtils.notNull(config, "config");
        
        this.sheet = sheet;
        this.label = label;
        this.config = config;
    }
    
    /**
     * 起点なる位置を指定する。
     * @param column 列番号(0から始まる)
     * @param row 行番号(0から始まる)
     * @return 自身のインスタンス。メソッドチェーンとして続ける。
     * @throws IllegalArgumentException {@literal column < 0 or row < 0}
     */
    public CellFinder startPosition(final int column, final int row) {
        ArgUtils.notMin(column, 0, "column");
        ArgUtils.notMin(row, 0, "row");
        
        this.startColumn = column;
        this.startRow = row;
        
        return this;
    }
    
    /**
     * 起点なる位置を指定する。
     * @param cell 起点とうなるセル
     * @return 自身のインスタンス。メソッドチェーンとして続ける。
     * @throws NullPointerException {@literal cell == null.}
     */
    public CellFinder startPosition(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        
        return startPosition(cell.getColumnIndex(), cell.getRowIndex());
    }
    
    /**
     * 起点なる位置を指定する。
     * @param address セルのアドレス
     * @return 自身のインスタンス。メソッドチェーンとして続ける。
     * @throws NullPointerException {@literal address == null.}
     */
    public CellFinder startPosition(final CellPosition address) {
        ArgUtils.notNull(address, "address");
        
        return startPosition(address.getColumn(), address.getRow());
    }
    
    /**
     * 起点となる列を指定する。
     * ただし、行は先頭から検索する。
     * @param column 列番号(0から始まる）
     * @return 自身のインスタンス。メソッドチェーンとして続ける。
     * @throws IllegalArgumentException {@literal column < 0}
     */
    public CellFinder startColumn(final int column) {
        ArgUtils.notMin(column, 0, "column");
        
        this.startColumn = column;
        this.startRow = -1;
        
        return this;
    }
    
    /**
     * 起点となる行を指定する。
     * ただし、列は先頭から検索する。
     * @param row 行番号(0から始まる）
     * @return 自身のインスタンス。メソッドチェーンとして続ける。
     * @throws IllegalArgumentException {@literal row < 0}
     */
    public CellFinder startRow(final int row) {
        ArgUtils.notMin(row, 0, "row");
        
        this.startColumn = -1;
        this.startRow = row;
        
        return this;
    }
    
    /**
     * 起点となる位置を除外するかどうか。
     * @param excludeStartPosition trueの場合、起点となる位置を除外する
     * @return 自身のインスタンス。メソッドチェーンとして続ける。
     */
    public CellFinder excludeStartPosition(final boolean excludeStartPosition) {
        this.excludeStartPoisition = excludeStartPosition;
        return this;
    }
    
    /**
     * 一致する条件のセルを探す。
     * @return 見つからない場合は、空を返す。
     */
    public Optional<Cell> findOptional() {
        
        return Optional.ofNullable(findCell());
        
    }
    
    /**
     * 一致する条件のセルを探す。
     * ただし、指定したセルが見つからない場合は、例外{@link CellNotFoundException}をスローする。
     * @return 
     * @throws CellNotFoundException 指定したセルが見つからない場合
     */
    public Cell findWhenNotFoundException() {
        
        final Cell cell = findCell();
        if(cell == null) {
            throw new CellNotFoundException(sheet.getSheetName(), label);
        }
        
        return cell;
        
    }
    
    /**
     * 一致する条件のセルを探す。
     * @param optional セルが見つからない場合に、nullを返すかどうか。
     * @return 一致したセルを返す。
     * @throws CellNotFoundException 引数「optional=false」のときに、一致するセルが見つからない場合にスローする。
     */
    public Cell find(final boolean optional) {
        if(optional) {
            return findOptional().orElse(null);
        } else {
            return findWhenNotFoundException();
        }
    }
    
    /**
     * 条件に一致するセルを探す
     * @return 見つからない場合は、nullを返す。
     */
    private Cell findCell() {
        
        final int rowStart = startRow < 0 ? 0 : startRow;
        final int columnStart = startColumn < 0 ? 0 : startColumn;
        
        final int maxRow = POIUtils.getRows(sheet);
        for(int i=rowStart; i < maxRow; i++) {
            final Row row = sheet.getRow(i);
            if(row == null) {
                continue;
            }
            
            final int maxCol = row.getLastCellNum();;
            for(int j=columnStart; j < maxCol; j++) {
                
                if(excludeStartPoisition && includeInStartPosition(j, i)) {
                    // 開始位置を除外する場合
                    continue;
                }
                
                final Cell cell = row.getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                final String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
                if(Utils.matches(cellValue, label, config)) {
                    return cell;
                }
            }
        }
        
        return null;
        
    }
    
    /**
     * 現在の位置が検索対象の開始位置を含むかどうか判定します。
     * @param currentColumn 現在の列番号
     * @param currentRow 現在の行番号
     * @return trueの場合含みます。
     */
    private boolean includeInStartPosition(final int currentColumn, final int currentRow) {
        
        if(startColumn >=0 && startRow >= 0
                && currentColumn == startColumn && currentRow == startRow) {
            // 行と列の両方の指定がある場合
            return true;
            
        } else if(startColumn >= 0 && startRow < 0 && currentColumn == startColumn) {
            // 列の指定のみ
            return true;
            
        } else if(startColumn < 0 && startRow >= 0 && currentRow == startRow) {
            // 行の指定のみ
            return true;
            
        }
        
        return false;
        
    }
    
}
