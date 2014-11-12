package com.gh.mygreen.xlsmapper.cellconvert;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * Cellの値を変換するインタフェース。
 *
 * @author T.TSUCHIE
 * @param <T> 変換対象のJavaのオブジェクト
 */
public interface CellConverter<T> {
    
    /**
     * Excel Cell => Javaオブジェクトに変換する。
     * @param cell
     * @param adaptor
     * @param config
     * @return 変換したJavaオブジェクト
     * @throws XlsMapperException
     */
    T toObject(Cell cell, FieldAdaptor adaptor, XlsMapperConfig config) throws XlsMapperException;
    
    
    /**
     * Javaオブジェクト => Excel Cellに変換する。
     * @param adaptor
     * @param targetObj
     * @param sheet
     * @param column
     * @param row
     * @param config
     * @param operate
     * @return
     * @throws XlsMapperException 
     */
    Cell toCell(FieldAdaptor adaptor, Object targetObj, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
    
    /**
     * Javaオブジェクト => Excel Cellに変換する。
     * @param adaptor
     * @param key マップのキー
     * @param targetObj
     * @param sheet
     * @param column
     * @param row
     * @param config
     * @param operate
     * @return
     * @throws XlsMapperException 
     */
    Cell toCellWithMap(FieldAdaptor adaptor, String key, Object targetObj, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
    
}
