package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.XlsMapperException;

/**
 * 
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAccessException extends XlsMapperException {

    /** serialVersionUID */
    private static final long serialVersionUID = 5714262921347758230L;
    
    private final FieldAccessor accessor;
    
    public FieldAccessException(final FieldAccessor accessor, final String message) {
        super(message);
        this.accessor = accessor;
        
    }
    
    public FieldAccessException(final FieldAccessor accessor, final String message, final Throwable e) {
        super(message, e);
        this.accessor = accessor;
        
    }
    
    public FieldAccessor getAdapter() {
        return accessor;
    }
}
