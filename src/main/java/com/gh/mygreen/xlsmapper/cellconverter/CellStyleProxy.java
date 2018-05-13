package com.gh.mygreen.xlsmapper.cellconverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import com.gh.mygreen.xlsmapper.CellFormatter;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

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

    /**
     * セルを指定してインスタンスを作成する。
     * @param cell 管理対象のセル
     * @throws IllegalArgumentException {@literal cell is null.}
     */
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
     * @return コンストラクタで渡したセルを取得します。
     */
    public Cell getCell() {
        return cell;
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
        cell.getCellStyle().setShrinkToFit(false);
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
        cell.getCellStyle().setWrapText(false);
        cell.getCellStyle().setShrinkToFit(true);
    }

    /**
     * インデントを設定する
     * @param indent インデントの値
     */
    public void setIndent(final short indent) {

        if(cell.getCellStyle().getIndention() == indent) {
            // 既にインデントが同じ値
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
            // 既に横位置が同じ値
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
            // 既に縦位置が同じ値
            return;
        }

        cloneStyle();
        cell.getCellStyle().setVerticalAlignment(align);
    }

    /**
     * 書式を設定する
     * @param pattern 書式
     */
    public void setDataFormat(final String pattern, final CellFormatter cellFormatter) {

        String currentPattern = POIUtils.getCellFormatPattern(cell, cellFormatter);
        if(currentPattern.equalsIgnoreCase(pattern)) {
            // 既に書式が同じ場合
            return;
        }

        cloneStyle();
        cell.getCellStyle().setDataFormat(POIUtils.getDataFormatIndex(cell.getSheet(), pattern));

    }

    /**
     * 書式を設定する。
     * <p>設定使用とする書式がない場合は、セルの書式を優先する。
     *  <br>ただし、セルの書式も内場合は、デフォルトの書式を設定する。
     * </p>
     *
     * @param settingPattern 設定しようとする書式(空の場合がある)
     * @param defaultPattern デフォルトの書式
     */
    public void setDataFormat(final String settingPattern, final String defaultPattern,
            final CellFormatter cellFormatter) {

        String currentPattern = POIUtils.getCellFormatPattern(cell, cellFormatter);

        if(Utils.isNotEmpty(settingPattern)) {
            // アノテーションで書式が指定されている場合、更新する
            setDataFormat(settingPattern, cellFormatter);

        } else if(currentPattern.isEmpty() || currentPattern.equalsIgnoreCase("general")) {
            // セルの書式が設定されていない場合、デフォルトの値で更新する
            setDataFormat(defaultPattern, cellFormatter);

        }

    }

}
