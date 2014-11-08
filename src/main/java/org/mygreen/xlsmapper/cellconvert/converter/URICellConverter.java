package org.mygreen.xlsmapper.cellconvert.converter;

import java.net.URI;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.XlsMapperException;
import org.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class URICellConverter extends AbstractCellConverter<URI>{

    @Override
    public URI toObject(Cell cell, FieldAdaptor adaptor, XlsMapperConfig config)
            throws XlsMapperException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Cell toCell(FieldAdaptor adaptor, Object targetObj, Sheet sheet, int column, int row, XlsMapperConfig config) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Cell toCellWithMap(FieldAdaptor adaptor, String key, Object targetObj, Sheet sheet, int column, int row, XlsMapperConfig config)
            throws XlsMapperException {
        // TODO Auto-generated method stub
        return null;
    }
}
