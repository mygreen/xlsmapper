package org.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import org.mygreen.xlsmapper.ArgUtils;


/**
 * 値が指定した範囲内かどうかチェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.range」。
 *  
 * @author T.TSUCHIE
 *
 */
public class RangeValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T min;
    
    private final T max;
    
    /** エラー用のためのフォーマットパターン */
    private String pattern;
    
    public RangeValidator(final T min, final T max) {
        super();
        ArgUtils.notNull(min, "min");
        ArgUtils.notNull(max, "max");
        ArgUtils.notMax(min, max, "min");
        
        this.min = min;
        this.max = max;
    }
    
    public RangeValidator(final T min, final T max, final String pattern) {
        this(min, max);
        this.pattern = pattern;
    }
    
    @Override
    public String getDefaultMessageKey() {
        return "cellFieldError.range";
    }
    
    @Override
    protected boolean validate(final T value) {
        if(isNullValue(value)) {
            return true;
        }
        
        if(value.compareTo(getMin()) >= 0 && value.compareTo(getMax()) <= 0) {
            return true;
        }
        return false;
    }
    
    @Override
    protected LinkedHashMap<String, Object> getMessageVars(final T value) {
        final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
        vars.put("validatedValue", value);
        vars.put("formattedValidatedValue", formatValue(value, getPattern()));
        vars.put("min", getMin());
        vars.put("formattedMin", formatValue(getMin(), getPattern()));
        vars.put("max", getMax());
        vars.put("formattedMax", formatValue(getMax(), getPattern()));
        return vars;
    }
    
    public T getMin() {
        return min;
    }
    
    public T getMax() {
        return max;
    }
    
    public String getPattern() {
        return pattern;
    }
}
