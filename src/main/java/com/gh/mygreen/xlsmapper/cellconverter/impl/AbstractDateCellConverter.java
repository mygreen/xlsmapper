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
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

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
     * <p>セルに書式が設定されていないときのデフォルトの書式
     */
    private String defaultExcelPattern;

    /**
     * アノテーションで書き込み時の指定されてたセルの書式
     * <p>指定されない場合がある
     */
    private Optional<String> settingExcelPattern = Optional.empty();

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
        cellStyle.setDataFormat(settingExcelPattern.orElse(null), defaultExcelPattern, getConfiguration().getCellFormatter());

        if(cellValue.isPresent()) {
            boolean isStartDate1904 = POIUtils.isDateStart1904(cell.getSheet().getWorkbook());
            POIUtils.setCellValueAsDate(cell, cellValue.get(), isStartDate1904);

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
