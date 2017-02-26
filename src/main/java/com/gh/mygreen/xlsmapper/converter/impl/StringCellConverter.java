package com.gh.mygreen.xlsmapper.converter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCellOption;
import com.gh.mygreen.xlsmapper.annotation.XlsDefaultValue;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * String型を処理するためのConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class StringCellConverter extends AbstractCellConverter<String> {

    @Override
    public String toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config) {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        String resultValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        resultValue = Utils.trim(resultValue, adapter.getAnnotation(XlsTrim.class));
        
        if(resultValue.isEmpty()) {
            if(defaultValueAnno.isPresent()) {
                resultValue = defaultValueAnno.get().value();
                
            } else if(trimAnno.isPresent()) {
                // trimが有効な場合、空文字を設定する。
                resultValue = "";
                
            } else {
                resultValue = null;
            }
        }
        
        return resultValue;
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final String targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula =formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        String value = targetValue;
        
        value = Utils.trim(value, trimAnno);
        value = Utils.getDefaultValueIfEmpty(value, defaultValueAnno);
        
        if(Utils.isNotEmpty(value) && !primaryFormula) {
            cell.setCellValue(value);
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
}
