package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * URIのConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class URICellConverter extends AbstractCellConverter<URI>{
    
    @Override
    public URI toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(Utils.hasNotDefaultValue(converterAnno)) {
                return null;
            } else {
                final String defaultValue = converterAnno.defaultValue();
                try {
                    return new URI(defaultValue);
                } catch (URISyntaxException e) {
                    throw newTypeBindException(cell, adaptor, defaultValue);
                }
            }
            
        } else if(cell.getHyperlink() != null) {
            // リンクが設定されているセルは、リンクの内容を値とする
            final String address = Utils.trim(cell.getHyperlink().getAddress(), converterAnno);
            try {
                return new URI(address);
            } catch (URISyntaxException e) {
                throw newTypeBindException(cell, adaptor, address);
            }
            
        } else {
            // リンクがないセルは、セルの文字列を値とする
            final String str = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), converterAnno);
            try {
                return new URI(str);
            } catch (URISyntaxException e) {
                throw newTypeBindException(cell, adaptor, str);
            }
            
        }
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet,
            final int column, final int row, final XlsMapperConfig config) {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, null);
    }
    
    @Override
    public Cell toCellWithMap(final FieldAdaptor adaptor, final String key, final Object targetObj, final Sheet sheet,
            final int column, final int row, final XlsMapperConfig config) throws XlsMapperException {
        
        return toCell(adaptor, targetObj, sheet, column, row, config, key);
    }
    
    private Cell toCell(final FieldAdaptor adaptor, final Object targetObj, final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config, final String mapKey) {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        final URI value;
        if(mapKey == null) {
            value = (URI)adaptor.getValue(targetObj);
        } else {
            value = (URI)adaptor.getValueOfMap(mapKey, targetObj);
        }
        
        if(value != null) {
            // 既存のハイパーリンクを削除
            // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
            POIUtils.removeHyperlink(cell);
            
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final Hyperlink link = helper.createHyperlink(Hyperlink.LINK_URL);
            link.setAddress(value.toString());
            cell.setHyperlink(link);
            
            cell.setCellValue(value.toString());
            
        } else {
            // 既存のハイパーリンクを削除
            // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
            POIUtils.removeHyperlink(cell);
            
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
