package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.awt.Point;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
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
            final FieldAccessor accessor, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final Optional<FindInfo> info = findCell(accessor, sheet, anno, config);
        if(!info.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }
        
        accessor.setPosition(beansObj, info.get().address);
        accessor.setLabel(beansObj, info.get().label);
        
        final CellConverter<?> converter = getCellConverter(accessor, config);
        try {
            final Object value = converter.toObject(info.get().targetCell, accessor, config);
            accessor.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.get().address, accessor.getName(), info.get().label);
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
    
    private Optional<FindInfo> findCell(final FieldAccessor accessor, final Sheet sheet, final XlsLabelledCell anno, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Optional<CellAddress> labelPosition = getLabelPosition(accessor, sheet, anno, config);
        if(!labelPosition.isPresent()) {
            return Optional.empty();
        }
        
        final int column = labelPosition.get().getColumn();
        final int row = labelPosition.get().getRow();
        
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
        
        return Optional.of(info);
    }
    
    private Optional<CellAddress> getLabelPosition(final FieldAccessor accessor, final Sheet sheet, final XlsLabelledCell anno, final XlsMapperConfig config) throws XlsMapperException {
        
        if(Utils.isNotEmpty(anno.labelAddress())) {
            try {
                return Optional.of(CellAddress.of(anno.labelAddress()));
            
            } catch(IllegalArgumentException e) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.invalidAddress")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsLabelledCell.class)
                        .var("attrName", "labelAddress")
                        .var("attrValue", anno.labelAddress())
                        .format());
                
            }
            
        } else if(Utils.isNotEmpty(anno.label())) {
            try {
                if(Utils.isNotEmpty(anno.headerLabel())){
                    Cell headerCell = Utils.getCell(sheet, anno.headerLabel(), 0, 0, config);
                    Cell labelCell = Utils.getCell(sheet, anno.label(), headerCell.getColumnIndex(), headerCell.getRowIndex() + 1, config);
                    return Optional.of(CellAddress.of(labelCell));
                    
                } else {
                    Cell labelCell = Utils.getCell(sheet, anno.label(), 0, config);
                    return Optional.of(CellAddress.of(labelCell));
                }
            } catch(XlsMapperException ex){
                if(anno.optional()){
                    return Optional.empty();
                } else {
                    throw ex;
                }
            }
            
        } else {
            // column, rowのアドレスを直接指定の場合
            if(anno.labelRow() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsLabelledCell.class)
                        .var("attrName", "row")
                        .var("attrValue", anno.labelRow())
                        .var("min", 0)
                        .format());
            }
            
            if(anno.labelColumn() < 0) {
                throw new AnnotationInvalidException(anno, MessageBuilder.create("anno.attr.min")
                        .var("property", accessor.getNameWithClass())
                        .varWithAnno("anno", XlsLabelledCell.class)
                        .var("attrName", "column")
                        .var("attrValue", anno.labelColumn())
                        .var("min", 0)
                        .format());
                
            }
            
            return Optional.of(CellAddress.of(anno.labelRow(), anno.labelColumn()));
        }
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsLabelledCell anno, final FieldAccessor accessor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final Optional<FindInfo> info = findCell(accessor, sheet, anno, config);
        if(!info.isPresent()) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }
        
        accessor.setPosition(targetObj, info.get().address);
        accessor.setLabel(targetObj, info.get().label);
        
        final CellConverter converter = getCellConverter(accessor, config);
        try {
            final Cell xlsCell = converter.toCell(accessor, accessor.getValue(targetObj), targetObj, sheet, info.get().address, config);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.get().address, accessor.getName(), info.get().label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
        
    }
}
