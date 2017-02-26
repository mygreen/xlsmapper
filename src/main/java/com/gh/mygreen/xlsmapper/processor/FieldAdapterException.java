package com.gh.mygreen.xlsmapper.processor;

import com.gh.mygreen.xlsmapper.XlsMapperException;

/**
 * 
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAdapterException extends XlsMapperException {

    /** serialVersionUID */
    private static final long serialVersionUID = 5714262921347758230L;
    
    private final FieldAdapter adapter;
    
    public FieldAdapterException(final FieldAdapter adapter, final String message) {
        super(message);
        this.adapter = adapter;
        
    }
    
    public FieldAdapterException(final FieldAdapter adapter, final String message, final Throwable e) {
        super(message, e);
        this.adapter = adapter;
        
    }
    
    public FieldAdapter getAdapter() {
        return adapter;
    }
}
