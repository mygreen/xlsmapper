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
    
    private transient final Annotation targetAnnotation;
    
    public AnnotationInvalidException(final String message) {
        super(message);
        this.targetAnnotation = null;
    }
    
    public AnnotationInvalidException(final Annotation targetAnnotation, final String message) {
        super(message);
        this.targetAnnotation = targetAnnotation;
    }
    
    public AnnotationInvalidException(final Annotation targetAnnotation, final String message, final Exception exception) {
        super(message, exception);
        this.targetAnnotation = targetAnnotation;
    }
    
    /**
     * エラーの元となったアノテーションを取得する。
     * @return 必要なアノテーションが付与されていない時など、nullを返すときもあります。
     */
    public Annotation getTargetAnnotation() {
        return targetAnnotation;
    }
}
