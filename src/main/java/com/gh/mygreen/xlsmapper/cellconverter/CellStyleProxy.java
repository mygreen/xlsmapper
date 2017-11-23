package com.gh.mygreen.xlsmapper.cellconverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * セルのスタイルを管理するクラス。
 * <p>既存のものを異なる設定をするならば、新しくする。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellStyleProxy {

    private final Cell cell;

    /**
     * 既にスタイルを新しくしたかどうか。
     */
    private boolean updated;

    public CellStyleProxy(final Cell cell) {
        ArgUtils.notNull(cell, "cell");

        this.cell = cell;
        this.updated = false;
    }

    private void cloneStyle() {
        if(updated) {
            // 既に更新済みの場合
            return;
        }

        CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        cell.setCellStyle(style);

        // 更新フラグをtrueにする
        this.updated = true;
    }

    /**
     * 折り返し設定を有効にする
     */
    public void setWrapText() {
        if(cell.getCellStyle().getWrapText()) {
            // 既に有効な場合
            return;
        }

        cloneStyle();
        cell.getCellStyle().setWrapText(true);
    }

    /**
     * 縮小して表示を有効にする
     */
    public void setShrinkToFit() {
        if(cell.getCellStyle().getShrinkToFit()) {
            // 既に有効な場合
            return;
        }

        cloneStyle();
        cell.getCellStyle().setShrinkToFit(true);
    }

    /**
     * インデントを設定する
     * @param indent インデントの値
     */
    public void setIndent(final short indent) {

        if(cell.getCellStyle().getIndention() == indent) {
            return;
        }

        cloneStyle();
        cell.getCellStyle().setIndention(indent);

    }

    /**
     * 横位置を設定する
     * @param align 横位置
     */
    public void setHorizontalAlignment(final HorizontalAlignment align) {

        if(cell.getCellStyle().getAlignmentEnum().equals(align)) {
            return;
        }

        cloneStyle();
        cell.getCellStyle().setAlignment(align);
    }

    /**
     * 縦位置を設定する
     * @param align 縦位置
     */
    public void setVerticalAlignment(final VerticalAlignment align) {

        if(cell.getCellStyle().getVerticalAlignmentEnum().equals(align)) {
            return;
        }

        cloneStyle();
        cell.getCellStyle().setVerticalAlignment(align);
    }

}
