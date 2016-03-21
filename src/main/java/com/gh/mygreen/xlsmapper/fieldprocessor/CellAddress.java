package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.awt.Point;
import java.io.Serializable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

import com.gh.mygreen.xlsmapper.ArgUtils;

/**
 * 単一のセルのアドレスを表現するクラス。
 * <p>java.awt.Pointだと、行、列の表現がわかりづらくmutableなので、、その代わりに使用する。
 * <p>{@link CellReference}との違いは、セルのアドレスの絶対一致を表現するためのもの。
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
    public CellAddress(int row, int column) {
        super();
        ArgUtils.notMin(row, 0, "row");
        ArgUtils.notMin(column, 0, "column");
        this.row = row;
        this.column = column;
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param cell セルのインスタンス。
     */
    public CellAddress(final Cell cell) {
        this(cell.getRowIndex(), cell.getColumnIndex());
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param address 'A1'の形式のセルのアドレス
     * @throws IllegalArgumentException {@literal address == null || address.length() == 0}
     */
    public CellAddress(final String address) {
        this(new CellReference(address));
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param reference セルの参照形式。
     */
    public CellAddress(final CellReference reference) {
        this(reference.getRow(), reference.getCol());
    }
    
    /**
     * CellAddressのインスタンスを作成する。
     * @param point セルの座標
     */
    public CellAddress(final Point point) {
        this(point.y, point.x);
    }
    
    @Override
    public int compareTo(final CellAddress other) {
        
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
    public boolean equals(Object obj) {
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
     * セルのアドレスを取得する。
     * @return 'A1'の形式で、セルノアドレスを文字列として表現する。
     */
    public String formatAsString() {
        return CellReference.convertNumToColString(this.column) + (this.row + 1);
    }
    
    @Override
    public String toString() {
        return formatAsString();
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

    
    
}
