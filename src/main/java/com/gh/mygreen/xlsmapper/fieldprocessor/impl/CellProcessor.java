package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.util.CellAddress;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;


/**
 * アノテーション {@link XlsCell} を処理するクラスです。
 * 
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class CellProcessor extends AbstractFieldProcessor<XlsCell> {
    
    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsCell anno, final FieldAccessor accessor,
            final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final CellAddress cellAddress = getCellPosition(accessor, anno);
        accessor.setPosition(beansObj, cellAddress);
        
        final Cell xlsCell = POIUtils.getCell(sheet, cellAddress);
        final CellConverter<?> converter = getCellConverter(accessor, config);
        
        try {
            final Object value = converter.toObject(xlsCell, accessor, config);
            accessor.setValue(beansObj, value);
            
        } catch(TypeBindException e) {
            work.addTypeBindError(e, cellAddress, accessor.getName(), null);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
    }
    
    /**
     * アノテーションから、セルのアドレスを取得する。
     * @param accessor フィールド情報
     * @param anno アノテーション
     * @return 値が設定されているセルのアドレス
     * @throws AnnotationInvalidException アドレスの設定値が不正な場合
     */
    private CellAddress getCellPosition(final FieldAccessor accessor, final XlsCell anno) throws AnnotationInvalidException {
        
        if(Utils.isNotEmpty(anno.address())) {
            try {
                return CellAddress.of(anno.address());
            } catch(IllegalArgumentException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsCell.class)
                        .var("attrName", "address")
                        .var("attrValue", anno.address())
                        .format());
            }
        
        } else {
            if(anno.row() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsCell.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.row())
                        .var("min", 0)
                        .format());
            }
            
            if(anno.column() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
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
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsCell anno, final FieldAccessor accessor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final CellAddress cellAddress = getCellPosition(accessor, anno);
        accessor.setPosition(targetObj, cellAddress);
        
        final CellConverter converter = getCellConverter(accessor, config);
        try {
            converter.toCell(accessor, accessor.getValue(targetObj), targetObj, sheet, cellAddress, config);
            
        } catch(TypeBindException e) {
            work.addTypeBindError(e, cellAddress, accessor.getName(), null);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }  
        }
        
    }

}
