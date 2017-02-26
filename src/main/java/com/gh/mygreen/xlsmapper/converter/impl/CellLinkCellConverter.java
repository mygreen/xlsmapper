package com.gh.mygreen.xlsmapper.converter.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.CellLink;
import com.gh.mygreen.xlsmapper.converter.LinkType;
import com.gh.mygreen.xlsmapper.processor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * CellLinkのConverter.
 * 
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
public class CellLinkCellConverter extends AbstractCellConverter<CellLink> {
    
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
            final String label = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), converterAnno);
            
            return new CellLink(address, label);
            
        } else {
            // リンクがないセルは、セルの文字列を値とする
            final String label = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), converterAnno);
            if(Utils.isEmpty(label)) {
                return null;
            }
            return new CellLink(null, label);
            
        }
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final CellLink targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row, final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsFormula formulaAnno = adaptor.getSavingAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno == null ? false : formulaAnno.primary();
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            cell.getCellStyle().setWrapText(converterAnno.wrapText());
            cell.getCellStyle().setShrinkToFit(converterAnno.shrinkToFit());
        }
        
        final CellLink value = targetValue;
        
        // 既存のハイパーリンクを削除
        // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
        cell.removeHyperlink();
        
        if(value != null && Utils.isNotEmpty(value.getLink()) && !primaryFormula) {
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final LinkType type = POIUtils.judgeLinkType(value.getLink());
            final Hyperlink link = helper.createHyperlink(type.poiType());
            
            link.setAddress(value.getLink());
            cell.setHyperlink(link);
            cell.setCellValue(value.getLabel());
            
        } else if(value != null && Utils.isNotEmpty(value.getLabel()) && !primaryFormula) {
            // 見出しのみ設定されている場合
            cell.setCellValue(value.getLabel());
            
        } else if(formulaAnno != null) {
            Utils.setupCellFormula(adaptor, formulaAnno, config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
