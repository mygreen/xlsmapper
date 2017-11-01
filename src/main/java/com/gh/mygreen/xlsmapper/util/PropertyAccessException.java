package com.gh.mygreen.xlsmapper.util;


/**
 * {@link PropertyValueNavigator}でプロパティのアクセスに失敗した際にスローされる。
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class PropertyAccessException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -8522981718391342777L;
    
    public PropertyAccessException(final String message) {
        super(message);
    }
    
    public PropertyAccessException(final String message, final Throwable e) {
        super(message, e);
    }

}
