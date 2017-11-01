package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;

/**
 * 値が指定した値以上かどうかの最小値のチェックする。
 * <p>メッセージキーは、「cellFieldError.min」。</p>
 * <p>メッセージ中で利用可能な変数は次の通り。</p>
 * <ul>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「min」：下限値となる最小値。</li>
 *   <li>「inclusive」：値を比較する際に指定した値を含むかどうか。</li>
 * </ul>
 * 
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class MinValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T min;
    
    private final boolean inclusive;
    
    /**
     * 最小値を指定するコンストラクタ
     * 
     * @param min 上限値となる最小値
     * @throws IllegalArgumentException {@literal min == null}
     */
    public MinValidator(final T min) {
        this(min, true);
    }
    
    /**
     * 最小値を指定するコンストラクタ
     * 
     * @param min 上限値となる最小値
     * @param inclusive 値を比較する際に指定した値を含むかどうかを指定します。
     * @throws IllegalArgumentException {@literal min == null}
     */
    public MinValidator(final T min, final boolean inclusive) {
        ArgUtils.notNull(min, "min");
        this.min = min;
        this.inclusive = inclusive;
    }
    
    @Override
    public MinValidator<T> addGroup(final Class<?>... group) {
        return (MinValidator<T>)super.addGroup(group);
    }
    
    @Override
    protected String getMessageKey() {
        return "cellFieldError.min";
    }
    
    @Override
    protected Map<String, Object> getMessageVariables(final CellField<T> cellField) {
        
        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("min", getMin());
        vars.put("inclusive", isInclusive());
        return vars;
    }
    
    @Override
    protected void onValidate(final CellField<T> cellField) {
        
        final int compared = cellField.getValue().compareTo(min);
        if(compared > 0) {
            return;
            
        } else if(inclusive && compared == 0) {
            return;
        }
        
        error(cellField);
    }
    
    /**
     * 上限値となる最小値を取得します。
     * @return 最小値。
     */
    public T getMin() {
        return min;
    }
    
    /**
     * 値を比較する際に指定した値を含むかどうかを取得します。
     * @return 値を比較する際に指定した値を含むかどうか
     */
    public boolean isInclusive() {
        return inclusive;
    }
    
}
