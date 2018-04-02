package com.gh.mygreen.xlsmapper.util;

import java.awt.Point;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;

/**
 * 単一のセルのアドレスを表現するクラス。
 * <p>{@link java.awt.Point}だと、行、列の表現がわかりづらくmutableなので、、その代わりに使用する。
 * <p>{@link CellReference}との違いは、セルのアドレスの絶対一致を表現するためのもの。
 * <p>POIに同じ用途のクラス{@link CellAddress}が存在するが、
 *    こちらは{@link Serializable}や{@link Cloneable}が実装されておらず、使い勝手が悪い。</p>
 *
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
public class CellPosition implements Serializable, Comparable<CellPosition>, Cloneable {

    /** serialVersionUID */
    private static final long serialVersionUID = 8579701754512731611L;

    /**
     * シート上の先頭の位置を表現するための定数。
     */
    public static final CellPosition A1 = new CellPosition(0, 0);

    private final int row;

    private final int column;

    private final String toStringText;

    /**
     * CellAddressのインスタンスを作成する。
     *
     * @param row 行番号 (0から始まる)
     * @param column 列番号 (0から始まる)
     * @throws IllegalArgumentException {@literal row < 0 || column < 0}
     */
    private CellPosition(int row, int column) {
        ArgUtils.notMin(row, 0, "row");
        ArgUtils.notMin(column, 0, "column");
        this.row = row;
        this.column = column;
        this.toStringText = CellReference.convertNumToColString(column) + (row + 1);
    }

    /**
     * CellAddressのインスタンスを作成する。
     *
     * @param row 行番号 (0から始まる)
     * @param column 列番号 (0から始まる)
     * @return {@link CellPosition}のインスタンス
     * @throws IllegalArgumentException {@literal row < 0 || column < 0}
     */
    public static CellPosition of(int row, int column) {
        ArgUtils.notMin(row, 0, "row");
        ArgUtils.notMin(column, 0, "column");

        return new CellPosition(row, column);
    }

    /**
     * CellAddressのインスタンスを作成する。
     * @param cell セルのインスタンス。
     * @return {@link CellPosition}のインスタンス
     * @throws IllegalArgumentException {@literal cell == null.}
     */
    public static CellPosition of(final Cell cell) {
        ArgUtils.notNull(cell, "cell");

        return of(cell.getRowIndex(), cell.getColumnIndex());
    }

    /**
     * CellAddressのインスタンスを作成する。
     * @param reference セルの参照形式。
     * @return {@link CellPosition}のインスタンス
     * @throws IllegalArgumentException {@literal reference == null.}
     */
    public static CellPosition of(final CellReference reference) {
        ArgUtils.notNull(reference, "reference");

        return of(reference.getRow(), reference.getCol());
    }

    /**
     * CellAddressのインスタンスを作成する。
     * @param address セルのアドレス
     * @return {@link CellPosition}のインスタンス
     * @throws IllegalArgumentException {@literal address == null.}
     */
    public static CellPosition of(final CellAddress address) {
        ArgUtils.notNull(address, "address");

        return of(address.getRow(), address.getColumn());
    }

    /**
     * CellAddressのインスタンスを作成する。
     * @param address 'A1'の形式のセルのアドレス
     * @return {@link CellPosition}のインスタンス
     * @throws IllegalArgumentException {@literal address == null || address.length() == 0 || アドレスの書式として不正}
     */
    public static CellPosition of(final String address) {
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
     * @return {@link CellPosition}のインスタンス
     * @throws IllegalArgumentException {@literal point == null.}
     */
    public static CellPosition of(final Point point) {
        ArgUtils.notNull(point, "point");
        return of(point.y, point.x);
    }

    @Override
    public int compareTo(final CellPosition other) {
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
        if(!(obj instanceof CellPosition)) {
            return false;
        }
        CellPosition other = (CellPosition) obj;
        if(column != other.column) {
            return false;
        }
        if(row != other.row) {
            return false;
        }
        return true;
    }

    @Override
    public CellPosition clone() {
        return new CellPosition(row, column);
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
     * POIの{@link CellAddress}の形式に変換します。
     * @return {@link CellAddress}のインスタンス。
     */
    public CellAddress toCellAddress() {
        return new CellAddress(row, column);
    }

    /**
     * セルのアドレスを取得する。
     * @return 'A1'の形式で、セルノアドレスを文字列として表現する。
     */
    public String formatAsString() {
        return toStringText;
    }

    /**
     * 行番号に指定した値を加算する。
     * @param value 加算する値
     * @return 加算したインスタンス
     */
    public CellPosition addRow(int value) {
        return new CellPosition(row + value, column);
    }

    /**
     * 列番号に指定した値を加算する。
     * @param value 加算する値
     * @return 加算したインスタンス
     */
    public CellPosition addColumn(int value) {
        return new CellPosition(row, column + value);
    }

    /**
     * {@link #formatAsString()}の値を返します。
     */
    @Override
    public String toString() {
        return formatAsString();
    }

}
