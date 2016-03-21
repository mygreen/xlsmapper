package com.gh.mygreen.xlsmapper;

import java.lang.annotation.Annotation;


/**
 * 使い方やパラメータが間違っているアノテーション付与された場合にスローする例外。
 *
 * @version 1.4
 * @author T.TSUCHIE
 *
 */
public class AnnotationInvalidException extends XlsMapperException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final Annotation anno;
    
    public AnnotationInvalidException(final String message, final Annotation anno) {
        super(message);
        this.anno = anno;
    }
    
    public AnnotationInvalidException(final String message, final Annotation anno, final Exception exception) {
        super(message, exception);
        this.anno = anno;
    }
    
    public Annotation getAnno() {
        return anno;
    }
}
