package com.gh.mygreen.xlsmapper.xml;

import com.gh.mygreen.xlsmapper.XlsMapperException;

/**
 * XMLを操作する際に発生する例外。
 *
 * @author T.TSUCHIE
 *
 */
public class XmlOperateException extends XlsMapperException {

    private static final long serialVersionUID = 4762483379595821097L;
    
    public XmlOperateException() {
        super();
    }
    
    public XmlOperateException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public XmlOperateException(final String message) {
        super(message);
    }
    
    public XmlOperateException(final Throwable cause) {
        super(cause);
    }

}
