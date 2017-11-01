package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;

/**
 * 値が指定した値以下かどうかの最大値のチェックする。
 * <p>メッセージキーは、「cellFieldError.max」。</p>
 * <p>メッセージ中で利用可能な変数は次の通り。</p>
 * <ul>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「max」：上限値となる最大値。</li>
 *   <li>「inclusive」：値を比較する際に指定した値を含むかどうか。</li>
 * </ul>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class MaxValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T max;
    
    private final boolean inclusive;
    
    /**
     * 最大値を指定するコンストラクタ
     * 
     * @param max 上限値となる最大値
     * @throws IllegalArgumentException {@literal max == null}
     */
    public MaxValidator(final T max) {
        this(max, true);
    }
    
    /**
     * 最大値を指定するコンストラクタ
     * 
     * @param max 上限値となる最大値
     * @param inclusive 値を比較する際に指定した値を含むかどうかを指定します。
     * @throws IllegalArgumentException {@literal max == null}
     */
    public MaxValidator(final T max, final boolean inclusive) {
        ArgUtils.notNull(max, "max");
        this.max = max;
        this.inclusive = inclusive;
    }
    
    @Override
    public MaxValidator<T> addGroup(final Class<?>... group) {
        return (MaxValidator<T>)super.addGroup(group);
    }
    
    @Override
    protected String getMessageKey() {
        return "cellFieldError.max";
    }
    
    @Override
    protected Map<String, Object> getMessageVariables(final CellField<T> cellField) {
        
        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("max", getMax());
        vars.put("inclusive", isInclusive());
        return vars;
    }
    
    @Override
    protected void onValidate(final CellField<T> cellField) {
        
        final int compared = cellField.getValue().compareTo(max);
        if(compared < 0) {
            return;
            
        } else if(inclusive && compared == 0) {
            return;
        }
        
        error(cellField);
    }
    
    /**
     * 上限値となる最大値を取得します。
     * @return 最大値。
     */
    public T getMax() {
        return max;
    }
    
    /**
     * 値を比較する際に指定した値を含むかどうかを取得します。
     * @return 値を比較する際に指定した値を含むかどうか
     */
    public boolean isInclusive() {
        return inclusive;
    }
    
}
