package org.mygreen.xlsmapper.xml;

import org.mygreen.xlsmapper.XlsMapperException;

public class XmlLoadException extends XlsMapperException {

    private static final long serialVersionUID = 4762483379595821097L;
    
    public XmlLoadException() {
        super();
    }
    
    public XmlLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public XmlLoadException(final String message) {
        super(message);
    }
    
    public XmlLoadException(final Throwable cause) {
        super(cause);
    }

}
