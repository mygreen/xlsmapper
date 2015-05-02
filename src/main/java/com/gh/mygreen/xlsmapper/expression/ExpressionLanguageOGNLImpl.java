package com.gh.mygreen.xlsmapper.expression;

import java.util.Map;
import java.util.Map.Entry;

import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * OGNLの式評価する。
 * <p>利用する際には、OGNLのライブラリが必要です。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageOGNLImpl extends ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageOGNLImpl.class);
    
    private final ObjectCache<String, Object> expressionCache = new ObjectCache<String, Object>();
    
    /**
     * 非公開のprivate/protectedなどのアクセスをするかどうか。
     */
    private boolean allowPrivateAccess;
    
    /**
     * privateなメンバーにアクセス指定可能か指定するコンストラクタ
     * @param allowPrivateAccess
     */
    public ExpressionLanguageOGNLImpl(boolean allowPrivateAccess) {
        this.allowPrivateAccess = allowPrivateAccess;
    }
    
    /**
     * コンストラクタ
     */
    public ExpressionLanguageOGNLImpl() {
        this(false);
    }
    
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notEmpty(values, "values");
        
        try {
            final OgnlContext ctx = (OgnlContext) Ognl.createDefaultContext(null);
            
            if(allowPrivateAccess) {
                ctx.setMemberAccess(new DefaultMemberAccess(true));
            }
            
            for(final Entry<String, ?> entry : values.entrySet())
                ctx.put(entry.getKey(), entry.getValue());
            
            if(logger.isDebugEnabled()) {
                logger.debug("Evaluating OGNL expression: {}", expression);
            }
            
            Object expr = expressionCache.get(expression);
            if (expr == null) {
                expr = Ognl.parseExpression(expression);
                expressionCache.put(expression, expr);
            }
            
            return Ognl.getValue(expr, ctx);
        } catch(OgnlException e) {
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with OGNL failed.", expression), e);
        }
    }
    
    /**
     * 非公開のメンバーなどにアクセスを許可するかどうか。
     * @return
     */
    public boolean isAllowPrivateAccess() {
        return allowPrivateAccess;
    }
    
}
