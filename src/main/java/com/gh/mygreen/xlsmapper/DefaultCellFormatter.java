package com.gh.mygreen.xlsmapper;

import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;

import com.github.mygreen.cellformatter.POICellFormatter;


/**
 * 標準のセルフォーマッター。
 * 書式をフォーマットするライブラリ、<a href="https://github.com/mygreen/excel-cellformatter" target="_blank">excel-cellformatter</a>を利用する。
 *
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class DefaultCellFormatter implements CellFormatter {
    
    private POICellFormatter poiCellFormatter = new POICellFormatter();
    
    @Override
    public String format(final Cell cell) {
        return format(cell, Locale.getDefault());
    }
    
    @Override
    public String format(final Cell cell, final Locale locale) {
        return poiCellFormatter.formatAsString(cell, locale);
    }
    
    /**
     * POICellFormatterを取得する
     * @return
     */
    public POICellFormatter getPoiCellFormatter() {
        return poiCellFormatter;
    }
    
    /**
     * POICellFormatterを設定する。
     * @param poiCellFormatter
     */
    public void setPoiCellFormatter(POICellFormatter poiCellFormatter) {
        this.poiCellFormatter = poiCellFormatter;
    }
    
}
