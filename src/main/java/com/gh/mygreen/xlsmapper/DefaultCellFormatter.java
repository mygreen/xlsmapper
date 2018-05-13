package com.gh.mygreen.xlsmapper;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;

import com.github.mygreen.cellformatter.FormatterResolver;
import com.github.mygreen.cellformatter.POICell;
import com.github.mygreen.cellformatter.POICellFormatter;


/**
 * 標準のセルフォーマッター。
 * 書式をフォーマットするライブラリ、<a href="https://github.com/mygreen/excel-cellformatter" target="_blank">excel-cellformatter</a>を利用する。
 *
 * @version 2.0
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class DefaultCellFormatter implements CellFormatter {

    /**
     * 値をキャッシュするかどうか
     */
    private boolean cached;

    /**
     * 値のキャッシュ
     */
    private Map<String, String> cacheData = new ConcurrentHashMap<>();

    private POICellFormatter poiCellFormatter = new POICellFormatter();

    @Override
    public void init(boolean cached) {
        setCached(cached);
        clearCacheData();
    }

    @Override
    public String format(final Cell cell) {
        return format(cell, Locale.getDefault());
    }

    @Override
    public String format(final Cell cell, final Locale locale) {

        if(isCached()) {
            final String cachedKey = createKey(cell, locale);
            return cacheData.computeIfAbsent(cachedKey, key -> poiCellFormatter.formatAsString(cell, locale));

        } else {
            return poiCellFormatter.formatAsString(cell, locale);
        }
    }

    /**
     * キャッシュのキーを作成する。
     * @param cell
     * @param locale
     * @return
     */
    private String createKey(final Cell cell, final Locale locale) {

        if(cell == null) {
            return "empty_cell_value";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(":sheet=").append(cell.getSheet().getSheetName());
        sb.append(":address=").append(cell.getAddress().formatAsString());
        sb.append(":locale=").append(locale.toString());

        return sb.toString();
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

    /**
     * 値をキャッシュするかどうか。
     * @return trueのときキャッシュする。
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * 値をキャッシュするかどうか設定します。
     * @param cached trueのときキャッシュする。
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    /**
     * キャッシュをクリアします。
     */
    public void clearCacheData() {
        this.cacheData.clear();
    }

    @Override
    public String getPattern(final Cell cell) {
        return getPattern(cell, Locale.getDefault());
    }

    @Override
    public String getPattern(final Cell cell, final Locale locale) {

        final POICell poiCell = new POICell(cell);
        final FormatterResolver formatterResolver = poiCellFormatter.getFormatterResolver();

        final short formatIndex = poiCell.getFormatIndex();
        final String formatPattern = poiCell.getFormatPattern();

        if(formatterResolver.canResolve(formatIndex)) {
            return formatterResolver.getFormatter(formatIndex).getPattern(locale);

        } else if(formatterResolver.canResolve(formatPattern)) {
            return formatterResolver.getFormatter(formatPattern).getPattern(locale);

        } else {
            return formatPattern;

        }
    }

}
