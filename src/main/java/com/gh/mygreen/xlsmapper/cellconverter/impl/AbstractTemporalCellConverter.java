package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellStyleProxy;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * JSR-310 'Date and Time API' の{@link TemporalAccessor}のテンプレートクラス。
 * <p>基本的に、{@link TemporalAccessor}のサブクラスのビルダは、このクラスを継承して作成する。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalCellConverter<T extends TemporalAccessor & Comparable<? super T>> extends BaseCellConverter<T> {

    /**
     * 書き込み時のExcelのセルの書式
     * <p>セルに書式が設定されていないときのデフォルトの書式
     */
    private String defaultExcelPattern;

    /**
     * アノテーションで書き込み時の指定されてたセルの書式
     * <p>指定されない場合がある
     */
    private Optional<String> settingExcelPattern = Optional.empty();

    public AbstractTemporalCellConverter(final FieldAccessor field, final Configuration config) {
        super(field, config);
    }

    @Override
    protected T parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {

        if(evaluatedCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            return convertFromDate(evaluatedCell.getDateCellValue());

        } else if(!formattedValue.isEmpty()) {

            try {
                return textFormatter.parse(formattedValue);

            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }

        }

        return null;
    }

    @Override
    protected void setupCell(final Cell cell, final Optional<T> cellValue) throws TypeBindException {

        // 書式を設定する
        final CellStyleProxy cellStyle = new CellStyleProxy(cell);
        cellStyle.setDataFormat(settingExcelPattern.orElse(null), defaultExcelPattern, getConfiguration().getCellFormatter());

        if(cellValue.isPresent()) {
            boolean isStartDate1904 = POIUtils.isDateStart1904(cell.getSheet().getWorkbook());
            Date date = convertToDate(cellValue.get(), isStartDate1904);
            POIUtils.setCellValueAsDate(cell, date, isStartDate1904);

        } else {
            cell.setCellType(CellType.BLANK);
        }

    }

    /**
     * 日時型から各タイプに変換する。
     * @param date 日時型
     * @return 変換した値
     */
    protected abstract T convertFromDate(Date date);

    /**
     * 日時型に変換する。
     * @param value 変換対象の値
     * @param dateStart1904 1904年始まりのシートかどうか
     * @return 変換した値
     */
    protected abstract Date convertToDate(T value, boolean dateStart1904);

    /**
     * デフォルトのExcelの書式を設定する
     * @param defaultExcelPattern デフォルトのExcelの書式
     */
    public void setDefaultExcelPattern(String defaultExcelPattern) {
        this.defaultExcelPattern = defaultExcelPattern;
    }

    /**
     * アノテーションで指定されたExcelの書式を設定する。
     * @param settingExcelPattern アノテーションで指定されたExcelの書式。空の場合もある。
     */
    public void setSettingExcelPattern(String settingExcelPattern) {

        if(Utils.isEmpty(settingExcelPattern)) {
            this.settingExcelPattern = Optional.empty();
        } else {
            this.settingExcelPattern = Optional.of(settingExcelPattern);
        }
    }

}
