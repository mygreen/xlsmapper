package com.gh.mygreen.xlsmapper.expression;



/**
 * 式言語の評価に失敗した場合にスローする例外。
 * @author T.TSUCHIE
 *
 */
public class ExpressionEvaluationException extends RuntimeException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    public ExpressionEvaluationException(final String message, final Throwable cause) {
        super(message, cause);
        
    }
}
