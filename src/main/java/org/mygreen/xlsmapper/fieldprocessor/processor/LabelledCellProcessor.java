package org.mygreen.xlsmapper.fieldprocessor.processor;

import java.awt.Point;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.AnnotationInvalidException;
import org.mygreen.xlsmapper.LoadingWorkObject;
import org.mygreen.xlsmapper.POIUtils;
import org.mygreen.xlsmapper.SavingWorkObject;
import org.mygreen.xlsmapper.Utils;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;
import org.mygreen.xlsmapper.annotation.XlsLabelledCell;
import org.mygreen.xlsmapper.cellconvert.CellConverter;
import org.mygreen.xlsmapper.cellconvert.TypeBindException;
import org.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * {@link XlsLabelledCell}を処理するFieldProcessor。
 *
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class LabelledCellProcessor extends AbstractFieldProcessor<XlsLabelledCell> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsLabelledCell anno,
            final FieldAdaptor adaptor, final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final FindInfo info = findCell(sheet, anno);
        Utils.setPosition(info.position.x, info.position.y, beansObj, adaptor.getName());
        Utils.setLabel(info.label, beansObj, adaptor.getName());
        
        final CellConverter<?> converter = getLoadingCellConverter(adaptor, config.getConverterRegistry());
        try {
            final Object value = converter.toObject(info.targetCell, adaptor, config);
            adaptor.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.position, adaptor.getName(), info.label);
            if(!config.isSkipTypeBindFailure()) {
                throw e;
            }
        }
    }
    
    private class FindInfo {
        Cell targetCell;
        Point position;
        String label;
    }
    
    private FindInfo findCell(final Sheet sheet, final XlsLabelledCell anno) throws XlsMapperException {
        
        Cell targetCell = null;
        
        final Point labelPosition = getLabelPosition(sheet, anno);
        if(labelPosition == null) {
            return null;
        }
        int column = labelPosition.x;
        int row = labelPosition.y;
        
        
        int range = anno.range();
        if(range < 1){
            range = 1;
        }
        
        for(int i=anno.skip(); i < range; i++){
            switch(anno.type()){
            case Left:
                targetCell = POIUtils.getCell(sheet, column - (1 * (i + 1)), row);
                break;
            case Right:
                targetCell = POIUtils.getCell(sheet, column + (1 * (i + 1)), row);
                break;
            case Bottom:
                targetCell = POIUtils.getCell(sheet, column, row + (1 * (i + 1)));
                break;
            default:
                throw new AnnotationInvalidException(
                        String.format("@XlsLabelledCell atrribute 'type' is invalid. : %s", anno.type().name()), anno);
            }
            
            if(POIUtils.getCellContents(targetCell).length()>0){
                break;
            }
        }
        
        final FindInfo info = new FindInfo();
        info.targetCell = targetCell;
        info.label = POIUtils.getCellContents(POIUtils.getCell(sheet, column, row));
        
        if(POIUtils.getCellContents(targetCell).length() > 0) {
            info.position = new Point(targetCell.getColumnIndex(), targetCell.getRowIndex());
        } else {
            info.position = new Point(column, row);
        }
        
        return info;
    }
    
    private Point getLabelPosition(final Sheet sheet, final XlsLabelledCell anno) throws XlsMapperException {
        
        if(Utils.isNotEmpty(anno.labelAddress())) {
            final Point address = Utils.parseCellAddress(anno.labelAddress());
            if(address == null) {
                throw new AnnotationInvalidException(
                        String.format("@XlsLabelledCell#labelAddress is wrong cell address '%s'.", anno.labelAddress()), anno);
            }
            return address;
            
        } else if(Utils.isNotEmpty(anno.label())) {
            try {
                if(Utils.isNotEmpty(anno.headerLabel())){
                    Cell headerCell = Utils.getCell(sheet, anno.headerLabel(), 0);
                    Cell labelCell = Utils.getCell(sheet, anno.label(), headerCell.getRowIndex() + 1);
                    int column = labelCell.getColumnIndex();
                    int row = labelCell.getRowIndex();
                    return new Point(column, row);
                    
                } else {
                    Cell labelCell = Utils.getCell(sheet, anno.label(), 0);
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
            if(anno.labelColumn() < 0 || anno.labelRow() < 0) {
                throw new AnnotationInvalidException(
                        String.format("@XlsLabelledCell#labelColumn or labelRow soulde be greather than or equal zero. (headerColulmn=%d, headerRow=%d)",
                                anno.labelColumn(), anno.labelRow()), anno);
            }
            
            return new Point(anno.labelColumn(), anno.labelRow());
        }
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object beansObj, final XlsLabelledCell anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final FindInfo info = findCell(sheet, anno);
        Utils.setPosition(info.position.x, info.position.y, beansObj, adaptor.getName());
        Utils.setLabel(info.label, beansObj, adaptor.getName());
        
        final CellConverter<?> converter = getLoadingCellConverter(adaptor, config.getConverterRegistry());
        try {
            final Cell xlsCell = converter.toCell(adaptor, beansObj, sheet, info.position.x, info.position.y, config);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, info.position, adaptor.getName(), info.label);
            if(!config.isSkipTypeBindFailure()) {
                throw e;
            }
        }
        
    }
}
