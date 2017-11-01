package com.gh.mygreen.xlsmapper.expression;

import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * 式言語「JEXL」の実装。
 * <p>利用する際には、JEXL2.1のライブラリが必要です。
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageJEXLImpl.class);
    
    private final JexlEngine jexlEngine;
    
    private final ObjectCache<String, Expression> expressionCache = new ObjectCache<>();
    
    public ExpressionLanguageJEXLImpl() {
        this(new JexlEngine());
        this.jexlEngine.setLenient(true);
        this.jexlEngine.setSilent(true);
    }
    
    /**
     * {@link JexlEngine}を指定するコンストラクタ。
     * @param jexlEngine JEXLの処理エンジン。
     */
    public ExpressionLanguageJEXLImpl(final JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notNull(values, "values");
        
        if(logger.isDebugEnabled()) {
            logger.debug("Evaluating JEXL expression: {}", expression);
        }
        
        try {
            Expression expr = expressionCache.get(expression);
            if (expr == null) {
                expr = jexlEngine.createExpression(expression);
                expressionCache.put(expression, expr);
            }
            
            return expr.evaluate(new MapContext((Map<String, Object>) values));
            
        } catch(Exception ex) {
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with JEXL failed.", expression), ex);
        }
    }
    
    /**
     * {@link JexlEngine}を取得する。
     * @return
     */
    public JexlEngine getJexlEngine() {
        return jexlEngine;
    }
    
}
