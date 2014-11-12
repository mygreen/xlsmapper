package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import com.gh.mygreen.xlsmapper.ArgUtils;

/**
 * 値が指定した値以下かどうかの最大値のチェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.max」。
 *
 */
public class MaxValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T max;
    
    /** エラー用のためのフォーマットパターン */
    private String pattern;
    
    public MaxValidator(final T max) {
        super();
        ArgUtils.notNull(max, "max");
        this.max = max;
    }
    
    public MaxValidator(final T max, final String pattern) {
        this(max);
        this.pattern = pattern;
    }
    
    @Override
    public String getDefaultMessageKey() {
        return "cellFieldError.max";
    }
    
    @Override
    protected boolean validate(final T value) {
        if(isNullValue(value)) {
            return true;
        }
        
        if(value.compareTo(getMax()) <= 0) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected LinkedHashMap<String, Object> getMessageVars(final T value) {
        final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
        vars.put("validatedValue", value);
        vars.put("formattedValidatedValue", formatValue(value, getPattern()));
        vars.put("max", getMax());
        vars.put("formattedMax", formatValue(getMax(), getPattern()));
        return vars;
    }
    
    public T getMax() {
        return max;
    }
    
    public String getPattern() {
        return pattern;
    }

}
