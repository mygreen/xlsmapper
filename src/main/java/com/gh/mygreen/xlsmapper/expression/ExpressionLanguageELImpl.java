package com.gh.mygreen.xlsmapper.expression;

import java.util.Formatter;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionException;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.expression.el.FormatterWrapper;
import com.gh.mygreen.xlsmapper.expression.el.LocalELContext;


/**
 * 標準のEL式を使用するための実装。
 * <p>利用する際には、ELのライブラリが必要です。
 * 
 */
public class ExpressionLanguageELImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageELImpl.class);
    
    private static final ExpressionFactory expressionFactory;
    static {
        expressionFactory = ExpressionFactory.newInstance();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notEmpty(values, "values");
        
        try {
            final LocalELContext context = new LocalELContext();
            
            for (final Entry<String, ? > entry : values.entrySet()) {
                if(isFormatter(entry.getKey(), entry.getValue())) {
                    // Formatterの場合は、ラップクラスを設定する。
                    ValueExpression exp =  expressionFactory.createValueExpression(
                            new FormatterWrapper((Formatter) entry.getValue()),
                            FormatterWrapper.class);
                    context.setVariable(entry.getKey(), exp);
                } else {
                    ValueExpression exp =  expressionFactory.createValueExpression(entry.getValue(), Object.class);
                    context.setVariable(entry.getKey(), exp);
                }
            }
            
            if(logger.isDebugEnabled()) {
                logger.debug("Evaluating EL expression: {}", expression);
            }
            
            final ValueExpression resultExp = expressionFactory.createValueExpression(context, bracket(expression), Object.class);
            return resultExp.getValue(context);
        
        } catch (final ExpressionException ex){
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with EL failed.", expression), ex);
        }
    }
    
    /**
     * {@link FormatterWrapper}で囲むべき値かどうか判定する。
     * @param key
     * @param value
     * @return
     */
    private boolean isFormatter(final String key, final Object value) {
        if(!RootResolver.FORMATTER.equals(key)) {
            return false;
        }
        
        if(value instanceof Formatter) {
            return true;
        }
        
        return false;
    }
    
    /**
     * EL式の形式の括弧'${...}'で囲む。
     * @param value
     * @return
     */
    private String bracket(final String value) {
        return "${" + value + "}";
    }
    
}
