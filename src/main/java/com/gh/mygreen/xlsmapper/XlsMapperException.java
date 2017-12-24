package com.gh.mygreen.xlsmapper;


/**
 * XlsMapperの基の本例外クラス。
 *
 * @author T.TSUCHIE
 *
 */
public class XlsMapperException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -5739922099089739224L;

    public XlsMapperException() {
    }

    public XlsMapperException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public XlsMapperException(final String message) {
        super(message);
    }

    public XlsMapperException(final Throwable cause) {
        super(cause);
    }
}
