package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * 値が指定した範囲内かどうかチェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.range」。
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class RangeValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T min;
    
    private final T max;
    
    /** エラーメッセージ中のための値のフォーマッタ */
    private FieldFormatter<T> formatter;
    
    /**
     * 値の範囲となる最小値と最大値を指定するコンストラクタ
     * @param min 下限値となる最小値
     * @param max 上限値となる最大値
     */
    public RangeValidator(final T min, final T max) {
        this(min, max, new DefaultFieldFormatter<T>(null));
    }
    
    /**
     * 値のフォーマットするための書式を指定するコンストラクタ
     * @param min 下限値となる最小値
     * @param max 上限値となる最大値
     * @param pattern メッセージ中に表示するための値をフォーマットする際の書式。
     */
    public RangeValidator(final T min, final T max, final String pattern) {
        this(min, max, new DefaultFieldFormatter<T>(pattern));
    }
    
    /**
     * 値のフォーマッタ指定するコンストラクタ
     * 
     * @since 1.0
     * @param min 下限値となる最小値
     * @param max 上限値となる最大値
     * @param formatter エラーメッセージ中のための値のフォーマッタ
     * @throws IllegalArgumentException formatter is null.
     */
    public RangeValidator(final T min, final T max, final FieldFormatter<T> formatter) {
        super();
        
        ArgUtils.notNull(min, "min");
        ArgUtils.notNull(max, "max");
        ArgUtils.notMax(min, max, "min");
        ArgUtils.notNull(formatter, "formatter");
        
        this.min = min;
        this.max = max;
        this.formatter = formatter;
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
        vars.put("formattedValidatedValue", formatter.format(value));
        vars.put("min", getMin());
        vars.put("formattedMin", formatter.format(getMin()));
        vars.put("max", getMax());
        vars.put("formattedMax", formatter.format(getMax()));
        return vars;
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
    
}
