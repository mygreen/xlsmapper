package com.gh.mygreen.xlsmapper.converter.impl;

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
import com.gh.mygreen.xlsmapper.converter.CellLink;
import com.gh.mygreen.xlsmapper.converter.LinkType;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.ConversionUtils;
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
    public CellLink toObject(final Cell cell, final FieldAdapter adapter, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        if(POIUtils.isEmptyCellContents(cell, config.getCellFormatter())) {
            
            if(!defaultValueAnno.isPresent()) {
                return null;
            } else {
                final String defaultValue = defaultValueAnno.get().value();
                return new CellLink(defaultValue, defaultValue);
            }
            
        } else if(cell.getHyperlink() != null) {
            // リンクが設定されているセルは、リンクの内容を値とする
            final String address = Utils.trim(cell.getHyperlink().getAddress(), trimAnno);
            final String label = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), trimAnno);
            
            return new CellLink(address, label);
            
        } else {
            // リンクがないセルは、セルの文字列を値とする
            final String label = Utils.trim(POIUtils.getCellContents(cell, config.getCellFormatter()), trimAnno);
            if(Utils.isEmpty(label)) {
                return null;
            }
            return new CellLink(null, label);
            
        }
    }
    
    @Override
    public Cell toCell(final FieldAdapter adapter, final CellLink targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row, final XlsMapperConfig config) throws XlsMapperException {
        
        final Optional<XlsDefaultValue> defaultValueAnno = adapter.getAnnotation(XlsDefaultValue.class);
        final Optional<XlsTrim> trimAnno = adapter.getAnnotation(XlsTrim.class);
        
        final Optional<XlsFormula> formulaAnno = adapter.getAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno.map(a -> a.primary()).orElse(false);
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        ConversionUtils.setupCellOption(cell, adapter.getAnnotation(XlsCellOption.class));
        
        final CellLink value = targetValue;
        
        // 既存のハイパーリンクを削除
        // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
        cell.removeHyperlink();
        
        if(value == null && defaultValueAnno.isPresent()) {
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final LinkType type = POIUtils.judgeLinkType(defaultValueAnno.get().value());
            final Hyperlink link = helper.createHyperlink(type.poiType());
            
            link.setAddress(defaultValueAnno.get().value());
            cell.setHyperlink(link);
            cell.setCellValue(defaultValueAnno.get().value());
            
        } else if(value != null && Utils.isNotEmpty(value.getLink()) && !primaryFormula) {
            final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            final LinkType type = POIUtils.judgeLinkType(value.getLink());
            final Hyperlink link = helper.createHyperlink(type.poiType());
            
            link.setAddress(value.getLink());
            cell.setHyperlink(link);
            cell.setCellValue(value.getLabel());
            
        } else if(value != null && Utils.isNotEmpty(value.getLabel()) && !primaryFormula) {
            // 見出しのみ設定されている場合
            cell.setCellValue(value.getLabel());
            
        } else if(formulaAnno.isPresent()) {
            Utils.setupCellFormula(adapter, formulaAnno.get(), config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
}
