package com.gh.mygreen.xlsmapper.util;

import java.awt.Point;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

/**
 * 単一のセルのアドレスを表現するクラス。
 * <p>java.awt.Pointだと、行、列の表現がわかりづらくmutableなので、、その代わりに使用する。
 * <p>{@link CellReference}との違いは、セルのアドレスの絶対一致を表現するためのもの。
 * <p>POIに同じ用途のクラス{@link org.apache.poi.ss.util.CellAddress}が存在するが、
 *    こちらは{@link Serializable}や{@link Cloneable}が実装されておらず、使い勝手が悪い。</p>
 * 
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
public class CellAddress implements Serializable, Comparable<CellAddress>, Cloneable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 8579701754512731611L;

    /**
     * シート上の先頭の位置を表現するための定数。
     */
    public static final CellAddress A1 = new CellAddress(0, 0);
    
    private final int row;
    
    private final int column;
    
    /**
     * CellAddressのインスタンスを作成する。
     *
     * @param row 行番号 (0から始まる)
     * @param column 列番号 (0から始まる)
     * @throws IllegalArgumentException {@literal row < 0 || column < 0}
     */
    private CellAddress(int row, int column) {
        ArgUtils.notMin(row, 0, "row");
        ArgUtils.notMin(column, 0, "column");
        this.row = row;
        this.column = column;
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     *
     * @param row 行番号 (0から始まる)
     * @param column 列番号 (0から始まる)
     * @return {@link CellAddress}のインスタンス
     * @throws IllegalArgumentException {@literal row < 0 || column < 0}
     */
    public static CellAddress of(int row, int column) {
        ArgUtils.notMin(row, 0, "row");
        ArgUtils.notMin(column, 0, "column");
        
        return new CellAddress(row, column);
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param cell セルのインスタンス。
     * @return {@link CellAddress}のインスタンス
     * @throws NullPointerException {@link cell == null.}
     */
    public static CellAddress of(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        
        return of(cell.getRowIndex(), cell.getColumnIndex());
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param reference セルの参照形式。
     * @return {@link CellAddress}のインスタンス
     * @throws NullPointerException {@link reference == null.}
     */
    public static CellAddress of(final CellReference reference) {
        ArgUtils.notNull(reference, "reference");
        
        return of(reference.getRow(), reference.getCol());
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param address セルのアドレス
     * @return {@link CellAddress}のインスタンス
     * @throws NullPointerException {@link address == null.}
     */
    public static CellAddress of(final org.apache.poi.ss.util.CellAddress address) {
        ArgUtils.notNull(address, "address");
        
        return of(address.getRow(), address.getColumn());
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param address 'A1'の形式のセルのアドレス
     * @return {@link CellAddress}のインスタンス
     * @throws IllegalArgumentException {@literal address == null || address.length() == 0 || アドレスの書式として不正}
     */
    public static CellAddress of(final String address) {
        ArgUtils.notEmpty(address, "address");
        
        if(!matchedCellAddress(address)) {
            throw new IllegalArgumentException(address + " is wrong cell address pattern.");
        }
        
        return of(new CellReference(address));
    }
    
    private static final Pattern PATTERN_CELL_ADREESS = Pattern.compile("^([a-zA-Z]+)([0-9]+)$");
    
    private static boolean matchedCellAddress(final String address) {
        final Matcher matcher = PATTERN_CELL_ADREESS.matcher(address);
        return matcher.matches();
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param point セルの座標
     * @return {@link CellAddress}のインスタンス
     * @throws NullPointerException {@link point == null.}
     */
    public static CellAddress of(final Point point) {
        ArgUtils.notNull(point, "point");
        return of(point.y, point.x);
    }
    
    @Override
    public int compareTo(final CellAddress other) {
        ArgUtils.notNull(other, "other");
        
        int r = this.row - other.row;
        if (r != 0) {
            return r;
        }
        
        int c = this.column - other.column;
        if (c != 0) {
            return c;
        }
        
        return 0;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof CellAddress)) {
            return false;
        }
        CellAddress other = (CellAddress) obj;
        if(column != other.column) {
            return false;
        }
        if(row != other.row) {
            return false;
        }
        return true;
    }
    
    @Override
    public CellAddress clone() {
        return new CellAddress(row, column);
    }
    
    /**
     * 行番号を取得する。
     * @return 行番号 (0から始まる)
     */
    public int getRow() {
        return row;
    }
    
    /**
     * 列番号を取得する。
     * @return 列番号 (0から始まる)
     */
    public int getColumn() {
        return column;
    }
    
    /**
     * {@link Point}の形式に変換します。
     * @return {@link Point}のインスタンス。
     *          x座標が列番号（column index）、y座標が行番号(row index)なります。
     */
    public Point toPoint() {
        return new Point(column, row);
    }
    
    /**
     * POIの{@link org.apache.poi.ss.util.CellAddress}の形式に変換します。
     * @return {@link org.apache.poi.ss.util.CellAddress}のインスタンス。
     */
    public org.apache.poi.ss.util.CellAddress toPoiCellAddress() {
        return new org.apache.poi.ss.util.CellAddress(row, column);
    }
    
    /**
     * セルのアドレスを取得する。
     * @return 'A1'の形式で、セルノアドレスを文字列として表現する。
     */
    public String formatAsString() {
        return CellReference.convertNumToColString(this.column) + (this.row + 1);
    }
    
    /**
     * {@link #formatAsString()}の値を返します。
     */
    @Override
    public String toString() {
        return formatAsString();
    }
    
}
