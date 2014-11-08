package org.mygreen.xlsmapper.expression;

import java.util.Map;
import java.util.Map.Entry;

import org.mygreen.xlsmapper.ArgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 * SpELを使用するための実装。
 * <p>利用するには、Spring3で追加されたライブラリ「spring-expression」が必要。
 * 
 */
public class ExpressionLanguageSpELImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageSpELImpl.class);
    
    private final ObjectCache<String, Object> expressionCache = new ObjectCache<String, Object>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notEmpty(values, "values");
        
        try {
            final EvaluationContext ctx = new StandardEvaluationContext();
            for (final Entry<String, ? > entry : values.entrySet()) {
                ctx.setVariable(entry.getKey(), entry.getValue());
            }
            
            if(logger.isDebugEnabled()) {
                logger.debug("Evaluating SpEL expression: {}", expression);
            }
            
            Expression expr = (Expression) expressionCache.get(expression);
            if (expr == null) {
                ExpressionParser parser = new SpelExpressionParser();
                expr = parser.parseExpression(expression);
                expressionCache.put(expression, expr);
            }
            
            return expr.getValue(ctx);
        
        } catch (final ExpressionException ex){
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with SpEL failed.", expression), ex);
        }
    }
    
}
