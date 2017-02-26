package com.gh.mygreen.xlsmapper.converter.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
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
 * URIのConverter.
 *
 * @version 1.5
 * @author T.TSUCHIE
 *
 */
public class URICellConverter extends AbstractCellConverter<URI> {
    
    @Override
    public URI toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(!defaultValueAnno.isPresent()) {
                return null;
            } else {
                final String defaultValue = defaultValueAnno.get().value();
                try {
                    return new URI(defaultValue);
                } catch (URISyntaxException e) {
                    throw newTypeBindException(e, cell, adapter, defaultValue);
                }
            }
            
        } else if(cell.getHyperlink() != null) {
            // リンクが設定されているセルは、リンクの内容を値とする
            final String address = Utils.trim(cell.getHyperlink().getAddress(), trimAnno);
            try {
                return new URI(address);
            } catch (URISyntaxException e) {
                throw newTypeBindException(e, cell, adapter, address);
            }
            
        } else {
            // リンクがないセルは、セルの文字列を値とする
            final String str = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), trimAnno);
            if(Utils.isEmpty(str)) {
                return null;
            }
            
            try {
                return new URI(str);
            } catch (URISyntaxException e) {
                throw newTypeBindException(cell, adapter, str);
            }
            
        }
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final URI targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        URI value = targetValue;
        
        // 既存のハイパーリンクを削除
        // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
        cell.removeHyperlink();
        
        if(value == null && defaultValueAnno.isPresent()) {
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final Hyperlink link = helper.createHyperlink(Hyperlink.LINK_URL);
            link.setAddress(defaultValueAnno.get().value());
            cell.setHyperlink(link);
            
            cell.setCellValue(defaultValueAnno.get().value());
            
        } else if(value != null && !primaryFormula) {
            
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final Hyperlink link = helper.createHyperlink(Hyperlink.LINK_URL);
            link.setAddress(value.toString());
            cell.setHyperlink(link);
            
            cell.setCellValue(value.toString());
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
