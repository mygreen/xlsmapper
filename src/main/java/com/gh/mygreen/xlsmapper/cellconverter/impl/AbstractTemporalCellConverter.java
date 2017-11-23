package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellStyleProxy;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.POIUtils;


/**
 * JSR-310 'Date and Time API' の{@link TemporalAccessor}のテンプレートクラス。
 * <p>基本的に、{@link TemporalAccessor}のサブクラスのビルダは、このクラスを継承して作成する。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalCellConverter<T extends TemporalAccessor & Comparable<? super T>> extends AbstractCellConverter<T> {

    /**
     * 書き込み時のExcelのセルの書式
     */
    private String excelPattern;

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
                throw newTypeBindExceptionWithParse(e, evaluatedCell, formattedValue);
            }

        }

        return null;
    }

    @Override
    protected void setupCell(final Cell cell, final Optional<T> cellValue) throws TypeBindException {

        // 書式を設定する
        final CellStyleProxy cellStyle = new CellStyleProxy(cell);
        cellStyle.setDataFormat(excelPattern);

        if(cellValue.isPresent()) {
            boolean isStartDate1904 = POIUtils.isDateStart1904(cell.getSheet().getWorkbook());
            Date date = convertToDate(cellValue.get(), isStartDate1904);
            cell.setCellValue(date);

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
     * Excelの書式を設定する
     * @param excelPattern Excelの書式
     */
    public void setExcelPattern(String excelPattern) {
        this.excelPattern = excelPattern;
    }


}
