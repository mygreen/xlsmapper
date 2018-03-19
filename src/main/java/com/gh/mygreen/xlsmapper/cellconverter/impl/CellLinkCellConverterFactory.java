package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Optional;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.CellLink;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link CellLink}型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellLinkCellConverterFactory extends CellConverterFactorySupport<CellLink>
        implements CellConverterFactory<CellLink>{
    
    @Override
    public CellLinkCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final CellLinkCellConverter cellConverter = new CellLinkCellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<CellLink> cellConverter, final FieldAccessor field,
            Configuration config) {
        // 何もしない
    }
    
    @Override
    protected TextFormatter<CellLink> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        return new TextFormatter<CellLink>() {
            
            @Override
            public CellLink parse(String text) throws TextParseException {
                return new CellLink(text, text);
            }
            
            @Override
            public String format(CellLink value) {
                return String.format("[%s](%s)", value.getLabel(), value.getLink());
            }
        };
    }
    
    public class CellLinkCellConverter extends BaseCellConverter<CellLink> {
        
        private CellLinkCellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
            
        }
        
        @Override
        protected CellLink parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            
            final Optional<XlsTrim> trimAnno = getField().getAnnotation(XlsTrim.class);
            
            final Hyperlink hyperlink = POIUtils.getHyperlink(evaluatedCell);
            if(hyperlink != null) {
                // リンクが設定されているセルは、リンクの内容を値とする
                final String address = Utils.trim(hyperlink.getAddress(), trimAnno);
                final String label = Utils.trim(formattedValue, trimAnno);
                
                return new CellLink(address, label);
                
            } else if(!formattedValue.isEmpty()) {
                // リンクがないセルは、セルの文字列を値とする
                return new CellLink(null, formattedValue);
            }
            
            return null;
        }
        
        @Override
        protected boolean isEmptyCell(final String formattedValue, final Cell cell) {
            if(POIUtils.getHyperlink(cell) != null) {
                return false;
            }
            
            return super.isEmptyCell(formattedValue, cell);
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<CellLink> cellValue) throws TypeBindException {
            
            // 既存のハイパーリンクを削除
            // 削除しないと、Excelの見た目上はリンクは変わっているが、データ上は2重にリンクが設定されている。
            cell.removeHyperlink();
            
            if(cellValue.isPresent()) {
                final CreationHelper helper = cell.getSheet().getWorkbook().getCreationHelper();
                final HyperlinkType type = POIUtils.judgeLinkType(cellValue.get().getLink());
                final Hyperlink link = helper.createHyperlink(type);
                link.setAddress(cellValue.get().getLink());
                cell.setHyperlink(link);
                
                cell.setCellValue(cellValue.get().getLabel());
                
            } else {
                cell.setCellType(CellType.BLANK);
            }
            
        }
        
    }
    
}
