package com.gh.mygreen.xlsmapper.cellconvert.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * String型を処理するためのConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class StringCellConverter extends AbstractCellConverter<String> {

    @Override
    public String toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config) {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            return Utils.getDefaultValueIfEmpty(null, converterAnno);
        }
        
        String resultValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        resultValue = Utils.trim(resultValue, converterAnno);
        resultValue = Utils.getDefaultValueIfEmpty(resultValue, converterAnno);
        
        return resultValue;
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, null);
    }
    
    @Override
    public Cell toCellWithMap(FieldAdaptor adaptor, String key, Object targetObj, Sheet sheet, int column, int row, XlsMapperConfig config)
            throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, key);
    }
    
    private Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config, final String mapKey) {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        String value;
        if(mapKey == null) {
            value = (String)adaptor.getValue(targetObj);
        } else {
            value = (String)adaptor.getValueOfMap(mapKey, targetObj);
        }
        
        value = Utils.trim(value, converterAnno);
        value = Utils.getDefaultValueIfEmpty(value, converterAnno);
        if(Utils.isNotEmpty(value)) {
            cell.setCellValue(value);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
}
