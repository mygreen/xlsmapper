package com.gh.mygreen.xlsmapper.processor.impl;

import java.awt.Point;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.converter.CellConverter;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.CellAddress;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.util.Utils;
import com.gh.mygreen.xlsmapper.validation.MessageBuilder;


/**
 * {@link XlsLabelledCell}を処理するFieldProcessor。
 * 
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class LabelledCellProcessor extends AbstractFieldProcessor<XlsLabelledCell> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsLabelledCell anno,
            final FieldAdapter adapter, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final FindInfo info = findCell(adapter, sheet, anno, config);
        if(info == null) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }
        Utils.setPosition(info.address, beansObj, adapter.getName());
        Utils.setLabel(info.label, beansObj, adapter.getName());
        
        final CellConverter<?> converter = getCellConverter(adapter, config);
        try {
            final Object value = converter.toObject(info.targetCell, adapter, config);
            adapter.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.address, adapter.getName(), info.label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
    }
    
    private static class FindInfo {
        Cell targetCell;
        CellAddress address;
        String label;
    }
    
    private FindInfo findCell(final FieldAdapter adapter, final Sheet sheet, final XlsLabelledCell anno, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final CellAddress labelPosition = getLabelPosition(adapter, sheet, anno, config);
        if(labelPosition == null) {
            return null;
        }
        
        final int column = labelPosition.getColumn();
        final int row = labelPosition.getRow();
        
        int range = anno.range();
        if(range < 1){
            range = 1;
        }
        
        // 値が設定されているセルを検索する。
        Point targetPosition = new Point();
        Cell targetCell = null;
        for(int i=0; i < range; i++){
            final int index = anno.skip() + i +1;
            if(anno.type() == LabelledCellType.Left) {
                targetPosition.x = column - index;
                targetPosition.y = row;
                targetCell = POIUtils.getCell(sheet, targetPosition);
                
            } else if(anno.type() == LabelledCellType.Right) {
                targetPosition.x = column + index;
                targetPosition.y = row;
                targetCell = POIUtils.getCell(sheet, targetPosition);
                
            } else if(anno.type() == LabelledCellType.Bottom) {
                targetPosition.x = column;
                targetPosition.y = row + index;
                targetCell = POIUtils.getCell(sheet, targetPosition);
                
            }
            
            if(POIUtils.getCellContents(targetCell, config.getCellFormatter()).length() > 0){
                break;
            }
        }
        
        final FindInfo info = new FindInfo();
        info.targetCell = targetCell;
        info.address = CellAddress.of(targetPosition);
        info.label = POIUtils.getCellContents(POIUtils.getCell(sheet, column, row), config.getCellFormatter());
        
        return info;
    }
    
    private CellAddress getLabelPosition(final FieldAdapter adaptor, final Sheet sheet, final XlsLabelledCell anno, final XlsMapperConfig config) throws XlsMapperException {
        
        if(Utils.isNotEmpty(anno.labelAddress())) {
            final CellAddress address = Utils.parseCellAddress(anno.labelAddress());
            if(address == null) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", adaptor.getNameWithClass())
                        .varWithAnno("anno", XlsLabelledCell.class)
                        .var("attrName", "labelAddress")
                        .var("attrValue", anno.labelAddress())
                        .format());
                
            }
            return address;
            
        } else if(Utils.isNotEmpty(anno.label())) {
            try {
                if(Utils.isNotEmpty(anno.headerLabel())){
                    Cell headerCell = Utils.getCell(sheet, anno.headerLabel(), 0, 0, config);
                    Cell labelCell = Utils.getCell(sheet, anno.label(), headerCell.getColumnIndex(), headerCell.getRowIndex() + 1, config);
                    return CellAddress.of(labelCell);
                    
                } else {
                    Cell labelCell = Utils.getCell(sheet, anno.label(), 0, config);
                    return CellAddress.of(labelCell);
                }
            } catch(XlsMapperException ex){
                if(anno.optional()){
                    return null;
                } else {
                    throw ex;
                }
            }
            
        } else {
            // column, rowのアドレスを直接指定の場合
            if(anno.labelRow() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", adaptor.getNameWithClass())
                        .varWithAnno("anno", XlsLabelledCell.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.labelRow())
                        .var("min", 0)
                        .format());
            }
            
            if(anno.labelColumn() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", adaptor.getNameWithClass())
                        .varWithAnno("anno", XlsLabelledCell.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.labelColumn())
                        .var("min", 0)
                        .format());
                
            }
            
            return CellAddress.of(anno.labelRow(), anno.labelColumn());
        }
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsLabelledCell anno, final FieldAdapter adaptor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final FindInfo info = findCell(adaptor, sheet, anno, config);
        if(info == null) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }
        
        Utils.setPosition(info.address, targetObj, adaptor.getName());
        Utils.setLabel(info.label, targetObj, adaptor.getName());
        
        final CellConverter converter = getCellConverter(adaptor, config);
        try {
            final Cell xlsCell = converter.toCell(adaptor, adaptor.getValue(targetObj), targetObj, sheet, info.address, config);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.address, adaptor.getName(), info.label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
        
    }
}
