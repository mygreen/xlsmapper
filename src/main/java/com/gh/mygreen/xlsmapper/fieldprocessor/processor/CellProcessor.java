package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import java.awt.Point;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.AnnotationInvalidException;
import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsCell;
import com.gh.mygreen.xlsmapper.cellconvert.CellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * アノテーション {@link XlsCell} を処理する。
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class CellProcessor extends AbstractFieldProcessor<XlsCell> {
    
    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsCell anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
        
        final Point cellPosition = getCellPosition(anno);
        
        Utils.setPosition(cellPosition.x, cellPosition.y, beansObj, adaptor.getName());
        
        final Cell xlsCell = POIUtils.getCell(sheet, cellPosition.x, cellPosition.y);
        final CellConverter<?> converter = getLoadingCellConverter(adaptor, config.getConverterRegistry(), config);
        
        try {
            final Object value = converter.toObject(xlsCell, adaptor, config);
            adaptor.setValue(beansObj, value);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, cellPosition, adaptor.getName(), null);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }
        }
    }
    
    /**
     * アノテーションから、セルのアドレスを取得する。
     * @param anno
     * @return
     * @throws AnnotationInvalidException
     */
    private Point getCellPosition(final XlsCell anno) throws AnnotationInvalidException {
        
        Point point = null;
        if(Utils.isNotEmpty(anno.address())) {
            point = Utils.parseCellAddress(anno.address());
            if(point == null) {
                throw new AnnotationInvalidException("@XlsCell attribute 'address' cannot be valid address.", anno);
            }
            return point;
        
        } else {
            if(anno.row() < 0 || anno.column() < 0) {
                throw new AnnotationInvalidException(
                        String.format("@XlsCell#column or row sould be greater than or equal zero. (column=%d, row=%d)",
                                anno.column(), anno.row()), anno);
            }
            return new Point(anno.column(), anno.row());
        }
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsCell anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final Point cellPosition = getCellPosition(anno);
        Utils.setPosition(cellPosition.x, cellPosition.y, targetObj, adaptor.getName());
        
        final CellConverter converter = getSavingCellConverter(adaptor, config.getConverterRegistry(), config);
        try {
            converter.toCell(adaptor, adaptor.getValue(targetObj), sheet, cellPosition.x, cellPosition.y, config);
        } catch(TypeBindException e) {
            work.addTypeBindError(e, cellPosition, adaptor.getName(), null);
            if(!config.isContinueTypeBindFailure()) {
                throw e;
            }  
        }
        
    }
}
