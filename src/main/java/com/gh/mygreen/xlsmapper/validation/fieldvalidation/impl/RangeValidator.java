package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;


/**
 * 値が指定した範囲内かどうかチェックする。
 * <p>メッセージキーは、「cellFieldError.range」。</p>
 * <p>メッセージ中で利用可能な変数は次の通り。</p>
 * <ul>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「min」：下限値となる最小値。</li>
 *   <li>「max」：上限値となる最大値。</li>
 *   <li>「inclusive」：値を比較する際に指定した値を含むかどうか。</li>
 * </ul>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class RangeValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T min;
    
    private final T max;
    
    private final boolean inclusive;
    
    /**
     * コンストラクタ
     * 
     * @since 1.0
     * @param min 下限値となる最小値
     * @param max 上限値となる最大値
     * @throws IllegalArgumentException {@literal min == null or max == null}
     * @throws IllegalArgumentException {@literal min > max}
     */
    public RangeValidator(final T min, final T max) {
        this(min, max, true);
    }
    
    /**
     * コンストラクタ
     * 
     * @since 1.0
     * @param min 下限値となる最小値
     * @param max 上限値となる最大値
     * @param inclusive 値を比較する際に指定した値を含むかどうかを指定します。
     * @throws IllegalArgumentException {@literal min == null or max == null}
     * @throws IllegalArgumentException {@literal min > max}
     */
    public RangeValidator(final T min, final T max, final boolean inclusive) {
        ArgUtils.notNull(min, "min");
        ArgUtils.notNull(max, "max");
        ArgUtils.notMax(min, max, "min");
        
        this.min = min;
        this.max = max;
        this.inclusive = inclusive;
    }
    
    @Override
    public RangeValidator<T> addGroup(final Class<?>... group) {
        return (RangeValidator<T>)super.addGroup(group);
    }
    
    @Override
    public String getMessageKey() {
        return "cellFieldError.range";
    }
    
    @Override
    protected Map<String, Object> getMessageVariables(final CellField<T> cellField) {
        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("min", getMin());
        vars.put("max", getMax());
        vars.put("inclusive", isInclusive());
        return vars;
    }
    
    @Override
    protected void onValidate(final CellField<T> cellField) {
        final T value = cellField.getValue();
        if(validate(value)) {
            return;
        }
        
        error(cellField);
    }
    
    private boolean validate(final T value) {
        final int comparedMin = value.compareTo(min);
        final int comparedMax = value.compareTo(max);
        
        if(comparedMin > 0 && comparedMax < 0) {
            return true;
        }
        
        if(inclusive && (comparedMin == 0 || comparedMax == 0)) {
            return true;
        }
        
        return false;
        
    }
    
    /**
     * Validatorの下限値となる最小値を取得する。
     * @return 最小値。
     */
    public T getMin() {
        return min;
    }
    
    /**
     * Validatorの上限値となる最大値を取得する。
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
