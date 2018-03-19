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
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * {@link URI}型を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class URICellConverterFactory extends CellConverterFactorySupport<URI>
        implements CellConverterFactory<URI>{
    
    @Override
    public URICellConverter create(final FieldAccessor field, final Configuration config) {
        
        final URICellConverter cellConverter = new URICellConverter(field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<URI> cellConverter, final FieldAccessor field,
            Configuration config) {
        // 何もしない
    }
    
    @Override
    protected TextFormatter<URI> createTextFormatter(final FieldAccessor field, final Configuration config) {
        return new TextFormatter<URI>() {

            @Override
            public URI parse(String text) throws TextParseException {
                try {
                    return new URI(text);
                } catch (URISyntaxException e) {
                    throw new TextParseException(text, URI.class);
                }
            }
            
            @Override
            public String format(final URI value) {
                return value.toString();
            }
        };
    }
    
    public class URICellConverter extends BaseCellConverter<URI> {
        
        private URICellConverter(final FieldAccessor field, final Configuration config) {
            super(field, config);
        }
        
        @Override
        protected URI parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            
            final Hyperlink hyperlink = POIUtils.getHyperlink(evaluatedCell);
            if(hyperlink != null) {
                // リンクが設定されているセルは、リンクの内容を値とする
                Optional<XlsTrim> trimAnno = getField().getAnnotation(XlsTrim.class);
                final String address = Utils.trim(hyperlink.getAddress(), trimAnno);
                
                try {
                    return new URI(address);
                } catch (URISyntaxException e) {
                    throw newTypeBindExceptionOnParse(e, evaluatedCell, address);
                }
                
            } else if(!formattedValue.isEmpty()) {
                // リンクがないセルは、セルの文字列を値とする
                try {
                    return this.textFormatter.parse(formattedValue);
                    
                } catch(TextParseException e) {
                    throw newTypeBindExceptionOnParse(e, evaluatedCell, formattedValue);
                }
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
        protected void setupCell(final Cell cell, final Optional<URI> cellValue) throws TypeBindException {
            
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
    
}
