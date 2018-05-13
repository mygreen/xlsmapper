package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellStyleProxy;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 数値型のConverterの抽象クラス。
 * <p>数値型のConverterは、基本的にこのクラスを継承して作成する。</p>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellConverter<T extends Number> extends BaseCellConverter<T> {

    /**
     * 書き込み時のExcelのセルの書式
     */
    private Optional<String> excelPattern = Optional.empty();

    /**
     * 数値を処理する際のコンテキスト
     */
    private MathContext mathContext;

    public AbstractNumberCellConverter(final FieldAccessor field, final Configuration config) {
        super(field, config);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {

        if(evaluatedCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            try {
                return convertTypeValue(new BigDecimal(evaluatedCell.getNumericCellValue(), mathContext));

            } catch(ArithmeticException e) {

                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }

        } else if(!formattedValue.isEmpty()) {
            try {
                return textFormatter.parse(formattedValue);

            } catch(TextParseException e) {
                throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
            }
        }

        // プリミティブ型の場合、値がnullの時は初期値を設定する
        if(field.getType().isPrimitive()) {
            return (T)Utils.getPrimitiveDefaultValue(field.getType());

        } else if(field.isComponentType() && field.getComponentType().isPrimitive()) {
            return (T)Utils.getPrimitiveDefaultValue(field.getComponentType());
        }

        return null;

    }

    /**
     * その型における型に変換する
     * BigDecimalから変換する際には、exactXXX()メソッドを呼ぶ。
     *
     * @param value 変換対象のBigDecimal
     * @return 変換した値
     * @throws ArithmeticException 変換する数値型に合わない場合
     */
    protected abstract T convertTypeValue(final BigDecimal value) throws ArithmeticException;

    @Override
    protected void setupCell(final Cell cell, final Optional<T> cellValue) throws TypeBindException {

        // 書式を設定する
        final CellStyleProxy cellStyle = new CellStyleProxy(cell);

        excelPattern.ifPresent(pattern -> {
            cellStyle.setDataFormat(pattern, getConfiguration().getCellFormatter());
        });

        if(cellValue.isPresent()) {
            cell.setCellValue(cellValue.get().doubleValue());

        } else {
            cell.setCellType(CellType.BLANK);
        }

    }

    /**
     * Excelの書式を設定する
     * @param excelPattern excelの書式
     */
    public void setExcelPattern(String excelPattern) {
        this.excelPattern = Optional.of(excelPattern);
    }

    /**
     * 数値を処理する際のコンテキストを設定する
     * @param mathContext
     */
    public void setMathContext(MathContext mathContext) {
        this.mathContext = mathContext;
    }

}
