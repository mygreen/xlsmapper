package com.gh.mygreen.xlsmapper.processor.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.converter.CellConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.CellAddress;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;


/**
 * アノテーション {@link XlsCell} を処理する。
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class CellProcessor extends AbstractFieldProcessor<XlsCell> {
    
    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsCell anno, final FieldAdapter adapter,
            final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final CellAddress cellAddress = getCellPosition(adapter, anno);
        
        Utils.setPosition(cellAddress, beansObj, adapter.getName());
        
        final Cell xlsCell = POIUtils.getCell(sheet, cellAddress);
        final CellConverter<?> converter = getCellConverter(adapter, config);
        
        try {
            final Object value = converter.toObject(xlsCell, adapter, config);
            adapter.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, cellAddress, adapter.getName(), null);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
    }
    
    /**
     * アノテーションから、セルのアドレスを取得する。
     * @param adaptor
     * @param anno
     * @return
     * @throws AnnotationInvalidException
     */
    private CellAddress getCellPosition(final FieldAdapter adaptor, final XlsCell anno) throws AnnotationInvalidException {
        
        if(Utils.isNotEmpty(anno.address())) {
            CellAddress address = Utils.parseCellAddress(anno.address());
            if(address == null) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", adaptor.getNameWithClass())
                        .varWithAnno("anno", XlsCell.class)
                        .var("attrName", "address")
                        .var("attrValue", anno.address())
                        .format());
            }
            return address;
        
        } else {
            if(anno.row() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", adaptor.getNameWithClass())
                        .varWithAnno("anno", XlsCell.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.row())
                        .var("min", 0)
                        .format());
            }
            
            if(anno.column() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", adaptor.getNameWithClass())
                        .varWithAnno("anno", XlsCell.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.column())
                        .var("min", 0)
                        .format());
                
            }
            
            return CellAddress.of(anno.row(), anno.column());
        }
        
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsCell anno, final FieldAdapter adapter,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final CellAddress cellAddress = getCellPosition(adapter, anno);
        Utils.setPosition(cellAddress, targetObj, adapter.getName());
        
        final CellConverter converter = getCellConverter(adapter, config);
        try {
            converter.toCell(adapter, adapter.getValue(targetObj), targetObj, sheet, cellAddress, config);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, cellAddress, adapter.getName(), null);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }  
        }
        
    }

}
