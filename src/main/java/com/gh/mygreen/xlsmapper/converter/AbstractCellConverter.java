package com.gh.mygreen.xlsmapper.converter;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * Converterの抽象クラス。
 * 通常は、このクラスを継承してConverterを作成する。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractCellConverter<T> implements CellConverter<T> {
    
    /**
     * 型変換失敗したときの例外{@link TypeBindException}をスローします。
     * @since 0.5
     * @param error
     * @param cell
     * @param adapter
     * @param targetValue
     * @return
     */
    public TypeBindException newTypeBindException(final Exception error, 
            final Cell cell, final FieldAdapter adapter, final Object targetValue) {
        //TODO:メッセージのコード化
        final String message = new StringBuilder()
            .append(String.format("Fail conversion field value '%s' => type '%s'.",
                    Utils.convertToString(targetValue), adapter.getType()))
            .append(String.format(" Cell '%s' map to '%s#%s'.", 
                    Utils.formatCellAddress(cell), adapter.getDeclaringClass().getName(), adapter.getName()))
            .toString();
        
        return new TypeBindException(error, message, adapter.getType(), targetValue);
        
    }
    
    /**
     * 型変換失敗したときの例外{@link TypeBindException}をスローします。
     * @param cell
     * @param adapter
     * @param targetValue
     * @return
     */
    public TypeBindException newTypeBindException(final Cell cell, final FieldAdapter adapter, final Object targetValue) {
        
        //TODO:メッセージのコード化
        final String message = new StringBuilder()
            .append(String.format("Fail conversion field value '%s' => type '%s'.",
                    Utils.convertToString(targetValue), adapter.getType()))
            .append(String.format(" Cell '%s' map to '%s#%s'.", 
                    Utils.formatCellAddress(cell), adapter.getDeclaringClass().getName(), adapter.getName()))
            .toString();
        
        return new TypeBindException(message, adapter.getType(), targetValue);
        
    }
    
}
