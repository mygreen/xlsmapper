package com.gh.mygreen.xlsmapper.xml;

import com.gh.mygreen.xlsmapper.XlsMapperException;


/**
 * アノテーションをXMLから読み込み込む際に失敗した場合にスローする例外。
 *
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class AnnotationReadException extends XlsMapperException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    public AnnotationReadException(final Throwable e) {
        super(e);
    }
    
    public AnnotationReadException(final String message, final Throwable e) {
        super(message, e);
    }
}
