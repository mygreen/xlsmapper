package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * URIのConverter.
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class URICellConverter extends AbstractCellConverter<URI> {
    
    @Override
    protected URI parseDefaultValue(final String defaultValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        try {
            return new URI(defaultValue);
            
        } catch (URISyntaxException e) {
            throw newTypeBindExceptionWithDefaultValue(e, accessor, defaultValue);
        }
    }
    
    @Override
    protected URI parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        final Optional<XlsTrim> trimAnno = accessor.getAnnotation(XlsTrim.class);
        
        if(evaluatedCell.getHyperlink() != null) {
            // リンクが設定されているセルは、リンクの内容を値とする
            final String address = Utils.trim(evaluatedCell.getHyperlink().getAddress(), trimAnno);
            
            try {
                return new URI(address);
                
            } catch (URISyntaxException e) {
                throw newTypeBindExceptionWithDefaultValue(e, accessor, address);
            }
            
        } else if(!formattedValue.isEmpty()) {
            // リンクがないセルは、セルの文字列を値とする
            try {
                return new URI(formattedValue);
                
            } catch (URISyntaxException e) {
                throw newTypeBindExceptionWithDefaultValue(e, accessor, formattedValue);
            }
        }
        
        return null;
    }
    
    @Override
    protected boolean isEmptyCell(final String formattedValue, final Cell cell, final Configuration config) {
        
        if(cell.getHyperlink() != null) {
            return false;
        }
        
        return super.isEmptyCell(formattedValue, cell, config);
        
    }
    
    @Override
    protected void setupCell(final Cell cell, final Optional<URI> cellValue, final FieldAccessor accessor,
            final Configuration config) throws TypeBindException {
        
        // 既存のハイパーリンクを削除
        // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
        cell.removeHyperlink();
        
        if(cellValue.isPresent()) {
            final CreationHelper helper = cell.getSheet().getWorkbook().getCreationHelper();
            final Hyperlink link = helper.createHyperlink(HyperlinkType.URL);
            
            link.setAddress(cellValue.get().toString());
            cell.setHyperlink(link);
            cell.setCellValue(cellValue.get().toString());
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }
    
}
