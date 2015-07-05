package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import com.gh.mygreen.xlsmapper.ArgUtils;

/**
 * 値が指定した値以上かどうかの最小値のチェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.min」。
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class MinValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T min;
    
    /** エラーメッセージ中のための値のフォーマッタ */
    private FieldFormatter<T> formatter;
    
    /**
     * 最小値を指定するコンストラクタ
     * @param min 下限値となる最小値。
     */
    public MinValidator(final T min) {
        this(min, new DefaultFieldFormatter<T>(null));
    }
    
    /**
     * 値のフォーマットするための書式を指定するコンストラクタ
     * @param min 下限値となる最小値
     * @param pattern メッセージ中に表示するための値をフォーマットする際の書式。
     */
    public MinValidator(final T min, final String pattern) {
        this(min, new DefaultFieldFormatter<T>(pattern));
    }
    
    /**
     * 値のフォーマッタ指定するコンストラクタ
     * 
     * @since 1.0
     * @param min 下限値となる最小値。
     * @param formatter エラーメッセージ中のための値のフォーマッタ
     * @throws IllegalArgumentException formatter is null.
     */
    public MinValidator(final T min, final FieldFormatter<T> formatter) {
        super();
        
        ArgUtils.notNull(min, "min");
        ArgUtils.notNull(formatter, "formatter");
        
        this.min = min;
        this.formatter = formatter;
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
        vars.put("formattedValidatedValue", formatter.format(value));
        vars.put("min", getMin());
        vars.put("formattedMin", formatter.format(getMin()));
        return vars;
    }
    
    /**
     * Validatorの下限値の最小値を取得する。
     * @return 最小値の値。
     */
    public T getMin() {
        return min;
    }
    
}
