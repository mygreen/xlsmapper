package com.gh.mygreen.xlsmapper.cellconvert.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * String型を処理するためのConverter.
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class StringCellConverter extends AbstractCellConverter<String> {

    @Override
    public String toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config) {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        String resultValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        resultValue = Utils.trim(resultValue, converterAnno);
        
        if(resultValue.isEmpty()) {
            if(Utils.hasDefaultValue(converterAnno)) {
                resultValue = converterAnno.defaultValue();
                
            } else if(Utils.getTrimValue(converterAnno)) {
                // trimが有効な場合、空文字を設定する。
                resultValue = "";
                
            } else {
                resultValue = null;
            }
        }
        
        return resultValue;
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final String targetValue, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        String value = targetValue;
        
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
