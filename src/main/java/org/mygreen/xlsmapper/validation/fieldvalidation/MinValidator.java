package org.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import org.mygreen.xlsmapper.ArgUtils;

/**
 * 値が指定した値以上かどうかの最小値のチェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.min」。
 *
 */
public class MinValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T min;
    
    /** エラー用のためのフォーマットパターン */
    private String pattern;
    
    public MinValidator(final T min) {
        super();
        ArgUtils.notNull(min, "min");
        this.min = min;
    }
    
    public MinValidator(final T min, final String pattern) {
        this(min);
        this.pattern = pattern;
    }
    
    @Override
    public String getDefaultMessageKey() {
        return "cellFieldError.min";
    }
    
    @Override
    protected boolean validate(final T value) {
        if(isNullValue(value)) {
            return true;
        }
        
        if(value.compareTo(getMin()) >= 0) {
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
        return vars;
    }
    
    public T getMin() {
        return min;
    }
    
    public String getPattern() {
        return pattern;
    }
    
}
