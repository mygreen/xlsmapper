package com.gh.mygreen.xlsmapper.expression.el;

import java.util.Formatter;
import java.util.Locale;

/**
 * EL式中で{@code java.util.Formatter#format}を使用すると、オーバーロードしているメソッドと区別がつかなくなるため、
 * ラップしオーバーロードのメソッドをなくすため利用します。
 *
 */
public class FormatterWrapper {
    
    private final Formatter formatter;
    
    public FormatterWrapper(final Formatter formatter) {
        this.formatter = formatter;
    }
    
    public FormatterWrapper(final Locale locale) {
        this.formatter = new Formatter(locale);
    }
    
    public String format(final String format, final Object... args) {
        return formatter.format(format, args).toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FormatterWrapper");
        sb.append("{}");
        return sb.toString();
    }
}
