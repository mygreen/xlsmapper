package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;

import com.gh.mygreen.xlsmapper.ArgUtils;

/**
 * 値が指定した値以下かどうかの最大値のチェックする。
 * <p>メッセージキーは、「cellFieldError.max」。
 * <p>メッセージ中で利用可能な変数は次の通り。
 *   <ul>
 *    <li>「validatedValue」：検証対象の値のオブジェクト。
 *    <li>「formattedValidatedValue」：{@link FieldFormatter}により、文字列にフォーマットした検証対象の値。
 *    <li>「max」：上限値となる最大値。
 *    <li>「formattedMax」：{@link FieldFormatter}により、文字列にフォーマットした上限値となる最大値。
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class MaxValidator<T extends Comparable<T>> extends AbstractFieldValidator<T> {
    
    private final T max;
    
    /** エラーメッセージ中のための値のフォーマッタ */
    private FieldFormatter<T> formatter;
    
    /**
     * 最大値を指定するコンストラクタ
     * <p>値のフォーマットは、{@link DefaultFieldFormatter} を利用します。
     * 
     * @param max 上限値となる最大値
     */
    public MaxValidator(final T max) {
        this(max, new DefaultFieldFormatter<T>(null));
    }
    
    /**
     * 値のフォーマットするための書式を指定するコンストラクタ。
     * <p>値のフォーマットは、{@link DefaultFieldFormatter} を利用します。
     * 
     * @param max 上限値となる最大値
     * @param pattern メッセージ中に表示するための値をフォーマットする際の書式。
     */
    public MaxValidator(final T max, final String pattern) {
        this(max, new DefaultFieldFormatter<T>(pattern));
    }
    
    /**
     * 値のフォーマッタ指定するコンストラクタ
     * 
     * @since 1.0
     * @param max 上限値となる最大値
     * @param formatter エラーメッセージ中のための値のフォーマッタ
     * @throws IllegalArgumentException max or formatter is null.
     */
    public MaxValidator(final T max, final FieldFormatter<T> formatter) {
        super();
        
        ArgUtils.notNull(max, "max");
        ArgUtils.notNull(formatter, "formatter");
        
        this.max = max;
        this.formatter = formatter;
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
        vars.put("formattedValidatedValue", formatter.format(value));
        vars.put("max", getMax());
        vars.put("formattedMax", formatter.format(getMax()));
        return vars;
    }
    
    /**
     * Validatorの上限値となる最大値を取得する。
     * @return 最大値。
     */
    public T getMax() {
        return max;
    }

}
