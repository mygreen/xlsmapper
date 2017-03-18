package com.gh.mygreen.xlsmapper.cellconverter;

import com.gh.mygreen.xlsmapper.XlsMapperException;


/**
 * Conversionで致命的なエラーが発生したときにスローされる例外。
 * @author T.TSUCHIE
 *
 */
public class ConversionException extends XlsMapperException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final Class<?> targetType;
    
    public ConversionException(final String message, final Class<?> targetType) {
        super(message);
        this.targetType = targetType;
    }
    
    public ConversionException(final String message, final Throwable ex, final Class<?> targetType) {
        super(message, ex);
        this.targetType = targetType;
    }
    
    public Class<?> getTargetType() {
        return targetType;
    }
    
}
