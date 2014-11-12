package org.mygreen.xlsmapper.cellconvert.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.POIUtils;
import org.mygreen.xlsmapper.Utils;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;
import org.mygreen.xlsmapper.annotation.converter.XlsConverter;
import org.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import org.mygreen.xlsmapper.cellconvert.CellLink;
import org.mygreen.xlsmapper.cellconvert.LinkType;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * CellLinkのConverter.
 *
 * @author T.TSUCHIE
 *
 */
public class CellLinkCellConverter extends AbstractCellConverter<CellLink>{
    
    @Override
    public CellLink toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(Utils.hasNotDefaultValue(converterAnno)) {
                return null;
            } else {
                final String defaultValue = converterAnno.defaultValue();
                return new CellLink(defaultValue, defaultValue);
            }
            
        } else if(cell.getHyperlink() != null) {
            // リンクが設定されているセルは、リンクの内容を値とする
            final String address = Utils.trim(cell.getHyperlink().getAddress(), converterAnno);
            final String label = Utils.trim(cell.getHyperlink().getLabel(), converterAnno);
            
            return new CellLink(address, label);
            
        } else {
            // リンクがないセルは、セルの文字列を値とする
            final String label = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), converterAnno);
            return new CellLink(null, label);
            
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
        
        final CellLink value;
        if(mapKey == null) {
            value = (CellLink)adaptor.getValue(targetObj);
        } else {
            value = (CellLink)adaptor.getValueOfMap(mapKey, targetObj);
        }
        
        if(value != null && Utils.isNotEmpty(value.getLink())) {
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final LinkType type = POIUtils.judgeLinkType(value.getLink());
            final Hyperlink link = helper.createHyperlink(type.poiType());
            
            link.setAddress(value.getLink());
            cell.setHyperlink(link);
            cell.setCellValue(value.getLabel());
            
        } else if(value != null && Utils.isNotEmpty(value.getLabel())) {
            // 見出しのみ設定されている場合
            cell.setCellValue(value.getLabel());
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
