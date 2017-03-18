package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 標準のフィールドのフォーマッタ。
 *
 * @since 1.0
 * @author T.TSUCHIE
 * @param <T> フィールドのタイプ
 *
 */
public class DefaultFieldFormatter<T> implements FieldFormatter<T> {
    
    /**
     * フォーマットする際の書式
     */
    private final String pattern;
    
    /**
     * フォーマッタのコンストラクタ
     * @param pattern フォーマットする際の書式。書式を指定しない場合はnullを指定する。
     */
    public DefaultFieldFormatter(final String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public String format(final T value) {
        
        if(value == null) {
            return null;
        }
        
        if(Utils.isEmpty(pattern)) {
            return value.toString();
        }
        
        if(value instanceof Number) {
            final NumberFormat fomatter = new DecimalFormat(pattern);
            return fomatter.format(value);
            
        } else if(value instanceof Date) {
            final DateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.format(value);
            
        } else {
            return value.toString();
        }
    }
    
    /**
     * フォーマットする際の書式
     * @return
     */
    public String getPattern() {
        return pattern;
    }
}
