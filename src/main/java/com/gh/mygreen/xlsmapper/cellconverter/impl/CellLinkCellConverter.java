package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellLink;
import com.gh.mygreen.xlsmapper.cellconverter.LinkType;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * CellLinkのConverter.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class CellLinkCellConverter extends AbstractCellConverter<CellLink> {
    
    @Override
    protected CellLink parseDefaultValue(final String defaultValue, final FieldAccessor accessor,
            final XlsMapperConfig config) throws TypeBindException {
        
        return new CellLink(defaultValue, defaultValue);
    }

    @Override
    protected CellLink parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor,
            final XlsMapperConfig config) throws TypeBindException {
        
        final Optional<XlsTrim> trimAnno = accessor.getAnnotation(XlsTrim.class);
        
        if(evaluatedCell.getHyperlink() != null) {
            // リンクが設定されているセルは、リンクの内容を値とする
            final String address = Utils.trim(evaluatedCell.getHyperlink().getAddress(), trimAnno);
            final String label = formattedValue;
            
            return new CellLink(address, label);
            
        } else if(!formattedValue.isEmpty()) {
            // リンクがないセルは、セルの文字列を値とする
            return new CellLink(null, formattedValue);
        }
        
        return null;
    }
    
    @Override
    protected boolean isEmptyCell(final String formattedValue, final Cell cell, final XlsMapperConfig config) {
        
        if(cell.getHyperlink() != null) {
            return false;
        }
        
        return super.isEmptyCell(formattedValue, cell, config);
        
    }
    
    @Override
    protected void setupCell(final Cell cell, final Optional<CellLink> cellValue, final FieldAccessor accessor,
            final XlsMapperConfig config) throws TypeBindException {
        
        // 既存のハイパーリンクを削除
        // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
        cell.removeHyperlink();
        
        if(cellValue.isPresent()) {
            if(Utils.isNotEmpty(cellValue.get().getLink())) {
                
                final CreationHelper helper = cell.getSheet().getWorkbook().getCreationHelper();
                final LinkType type = POIUtils.judgeLinkType(cellValue.get().getLink());
                final Hyperlink link = helper.createHyperlink(type.poiType());
                
                link.setAddress(cellValue.get().getLink());
                cell.setHyperlink(link);
                cell.setCellValue(cellValue.get().getLabel());
                
            } else if(Utils.isNotEmpty(cellValue.get().getLabel())) {
                // 見出しのみ設定されている場合
                cell.setCellValue(cellValue.get().getLabel());
                
            }
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }
}
