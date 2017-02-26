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
            final FieldAdapter adaptor, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final FindInfo info = findCell(adaptor, sheet, anno, config);
        if(info == null) {
            /*
             * ラベル用のセルが見つからない場合
             * optional=falseの場合は、例外がスローされここには到達しない。
             */
            return;
        }
        Utils.setPosition(info.position.x, info.position.y, beansObj, adaptor.getName());
        Utils.setLabel(info.label, beansObj, adaptor.getName());
        
        final CellConverter<?> converter = getCellConverter(adaptor, config.getConverterRegistry(), config);
        try {
            final Object value = converter.toObject(info.targetCell, adaptor, config);
            adaptor.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.position, adaptor.getName(), info.label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
    }
    
    private class FindInfo {
        Cell targetCell;
        Point position;
        String label;
    }
    
    private FindInfo findCell(final FieldAdapter adaptor, final Sheet sheet, final XlsLabelledCell anno, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Point labelPosition = getLabelPosition(adaptor, sheet, anno, config);
        if(labelPosition == null) {
            return null;
        }
        
        final int column = labelPosition.x;
        final int row = labelPosition.y;
        
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
        info.position = targetPosition;
        info.label = POIUtils.getCellContents(POIUtils.getCell(sheet, column, row), config.getCellFormatter());
        
        return info;
    }
    
    private Point getLabelPosition(final FieldAdapter adaptor, final Sheet sheet, final XlsLabelledCell anno, final XlsMapperConfig config) throws XlsMapperException {
        
        if(Utils.isNotEmpty(anno.labelAddress())) {
            final Point address = Utils.parseCellAddress(anno.labelAddress());
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
                    int column = labelCell.getColumnIndex();
                    int row = labelCell.getRowIndex();
                    return new Point(column, row);
                    
                } else {
                    Cell labelCell = Utils.getCell(sheet, anno.label(), 0, config);
                    int column = labelCell.getColumnIndex();
                    int row = labelCell.getRowIndex();
                    return new Point(column, row);
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
            
            return new Point(anno.labelColumn(), anno.labelRow());
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
        
        Utils.setPosition(info.position.x, info.position.y, targetObj, adaptor.getName());
        Utils.setLabel(info.label, targetObj, adaptor.getName());
        
        final CellConverter converter = getCellConverter(adaptor, config.getConverterRegistry(), config);
        try {
            final Cell xlsCell = converter.toCell(adaptor, adaptor.getValue(targetObj), targetObj, sheet, info.position.x, info.position.y, config);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.position, adaptor.getName(), info.label);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
        
    }
}
