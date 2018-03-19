package com.gh.mygreen.xlsmapper.cellconverter.impl;

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

/**
 * 日時型のConverterの抽象クラス。
 * <p>{@link Date}を継承している<code>javax.sql.Time/Date/Timestamp</code>はこのクラスを継承して作成します。</p>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractDateCellConverter<T extends Date> extends BaseCellConverter<T> {

    /**
     * 書き込み時のExcelのセルの書式
     */
    private String excelPattern;

    public AbstractDateCellConverter(FieldAccessor field, Configuration config) {
        super(field, config);
    }

    @Override
    protected T parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {

        if(evaluatedCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            return convertTypeValue(evaluatedCell.getDateCellValue());

        } else if(!formattedValue.isEmpty()) {
            // セルを文字列としてパースする
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
        cellStyle.setDataFormat(excelPattern);

        if(cellValue.isPresent()) {
            cell.setCellValue(cellValue.get());

        } else {
            cell.setCellType(CellType.BLANK);
        }

    }

    /**
     * その型における型に変換する
     * @param date 変換対象の値
     * @return 変換後の値
     */
    protected abstract T convertTypeValue(Date date);

    /**
     * Excelの書式を設定する
     * @param excelPattern Excelの書式
     */
    public void setExcelPattern(String excelPattern) {
        this.excelPattern = excelPattern;
    }


}
