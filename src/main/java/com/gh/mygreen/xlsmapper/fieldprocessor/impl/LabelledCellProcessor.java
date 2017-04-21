package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.awt.Point;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessType;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.CellFinder;
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
            final FieldAccessor accessor, final Configuration config, final LoadingWorkObject work) throws XlsMapperException {
        
        final Optional<FindInfo> info = findCell(accessor, sheet, anno, config, ProcessType.Load);
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
        CellPosition address;
        String label;
    }
    
    private Optional<FindInfo> findCell(final FieldAccessor accessor, final Sheet sheet, final XlsLabelledCell anno,
            final Configuration config, final ProcessType processType)
            throws XlsMapperException {
        
        final Optional<CellPosition> labelPosition = getLabelPosition(accessor, sheet, anno, config);
        if(!labelPosition.isPresent()) {
            return Optional.empty();
        }
        
        final int column = labelPosition.get().getColumn();
        final int row = labelPosition.get().getRow();
        
        /*
         * 見出しか結合している場合を考慮する場合
         * ・結合サイズ分で補正する。
         * ・考慮しない場合は、mergedXXXSizeの値は0のまま。
         */
        int mergedRowSize = 0;
        int mergedColumnSize = 0;
        if(anno.labelMerged()) {
            CellRangeAddress mergedRegion = POIUtils.getMergedRegion(sheet, row, column);
            if(mergedRegion != null) {
                mergedRowSize = mergedRegion.getLastRow() - mergedRegion.getFirstRow();
                mergedColumnSize = mergedRegion.getLastColumn() - mergedRegion.getFirstColumn();
            }
        }
        
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
                targetPosition.x = column + index + mergedColumnSize;
                targetPosition.y = row;
                targetCell = POIUtils.getCell(sheet, targetPosition);
                
            } else if(anno.type() == LabelledCellType.Bottom) {
                targetPosition.x = column;
                targetPosition.y = row + index + mergedRowSize;
                targetCell = POIUtils.getCell(sheet, targetPosition);
                
            }
            
            if(POIUtils.getCellContents(targetCell, config.getCellFormatter()).length() > 0){
                break;
            }
            
            if(processType.equals(ProcessType.Save)) {
                /*
                 * 書き込み時は、属性rangeの範囲を考慮しない。
                 * テンプレートファイルの場合、値は空を設定しているため。
                 */
                break;
            }
        }
        
        final FindInfo info = new FindInfo();
        info.targetCell = targetCell;
        info.address = CellPosition.of(targetPosition);
        info.label = POIUtils.getCellContents(POIUtils.getCell(sheet, column, row), config.getCellFormatter());
        
        return Optional.of(info);
    }
    
    private Optional<CellPosition> getLabelPosition(final FieldAccessor accessor, final Sheet sheet, final XlsLabelledCell anno, final Configuration config) throws XlsMapperException {
        
        if(Utils.isNotEmpty(anno.labelAddress())) {
            try {
                return Optional.of(CellPosition.of(anno.labelAddress()));
            
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
                    Cell headerCell = CellFinder.query(sheet, anno.headerLabel(), config).findWhenNotFoundException();
                    Cell labelCell = CellFinder.query(sheet, anno.label(), config)
                            .startPosition(headerCell.getColumnIndex(), headerCell.getRowIndex() + 1)
                            .findWhenNotFoundException();
                    return Optional.of(CellPosition.of(labelCell));
                    
                } else {
                    Cell labelCell = CellFinder.query(sheet, anno.label(), config).findWhenNotFoundException();
                    return Optional.of(CellPosition.of(labelCell));
                }
            } catch(CellNotFoundException ex){
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
            
            return Optional.of(CellPosition.of(anno.labelRow(), anno.labelColumn()));
        }
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsLabelledCell anno, final FieldAccessor accessor,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {
        
        final Optional<FindInfo> info = findCell(accessor, sheet, anno, config, ProcessType.Save);
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
