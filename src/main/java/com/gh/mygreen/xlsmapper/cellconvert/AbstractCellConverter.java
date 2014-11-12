package com.gh.mygreen.xlsmapper.cellconvert;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;




/**
 * Converterの抽象クラス。
 * 通常は、このクラスを継承してConverterを作成する。
 * 
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractCellConverter<T> implements CellConverter<T> {
    
    /**
     * 型変換失敗したときの例外{@link TypeBindException}をスローします。
     * @param cell
     * @param adaptor
     * @param targetValue
     * @return
     */
    public TypeBindException newTypeBindException(final Cell cell, final FieldAdaptor adaptor, final Object targetValue) {
        
        final String message = new StringBuilder()
            .append(String.format("Fail conversion field value '%s' => type '%s'.",
                    Utils.convertToString(targetValue), adaptor.getTargetClass()))
            .append(String.format(" Cell '%s' map to '%s#%s'.", 
                    Utils.formatCellAddress(cell), adaptor.getDeclaringClass().getName(), adaptor.getName()))
            .toString();
        
        return new TypeBindException(message, adaptor.getTargetClass(), targetValue);
        
    }
    
}
