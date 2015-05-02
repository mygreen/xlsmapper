package com.gh.mygreen.xlsmapper.expression;

import java.util.Formatter;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ELException;
import javax.el.ELProcessor;

import org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.expression.el.FormatterWrapper;
import com.gh.mygreen.xlsmapper.expression.el.RootELResolver;


/**
 * 標準のEL式を使用するための実装。
 * <p>利用する際には、ELのライブラリが必要です。
 * 
 */
public class ExpressionLanguageELImpl extends ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageELImpl.class);
    
    /** EL3.xが使用可能かどうか */
    boolean availabledEl3;
    {
        try {
            Class.forName("javax.el.ELProcessor");
            this.availabledEl3 = true;
        } catch (ClassNotFoundException e) {
            this.availabledEl3 = false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notEmpty(values, "values");
        
        if(availabledEl3) {
            return evaluateWithEL3(expression, values);
        } else {
            return evaluateWithEL2(expression, values);
        }
        
    }
    
    /**
     * EL3.xで評価する
     * @param expression
     * @param values
     * @return
     */
    Object evaluateWithEL3(final String expression, final Map<String, ?> values) {
        
        try {
            final ELProcessor elProc = new ELProcessor();
            elProc.getELManager().addELResolver(new RootELResolver(true));
            
            for (final Entry<String, ? > entry : values.entrySet()) {
                if(isFormatter(entry.getKey(), entry.getValue())) {
                    // Formatterの場合は、ラップクラスを設定する。
                    elProc.defineBean(entry.getKey(), new FormatterWrapper((Formatter) entry.getValue()));
                } else {
                    elProc.defineBean(entry.getKey(), entry.getValue());
                }
            }
            
            if(logger.isDebugEnabled()) {
                logger.debug("Evaluating EL expression: {}", expression);
            }
            
            return elProc.eval(expression);
        
        } catch (final ELException ex){
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with EL failed.", expression), ex);
        }
    }
    
    /**
     * EL2.xで評価する
     * @param expression
     * @param values
     * @return
     */
    Object evaluateWithEL2(final String expression, final Map<String, ?> values) {
        
        try {
            final com.gh.mygreen.xlsmapper.expression.el.ELProcessor elProc = new com.gh.mygreen.xlsmapper.expression.el.ELProcessor();
            
            for (final Entry<String, ? > entry : values.entrySet()) {
                if(isFormatter(entry.getKey(), entry.getValue())) {
                    elProc.setVariable(entry.getKey(), new FormatterWrapper((Formatter) entry.getValue()));
                } else {
                    elProc.setVariable(entry.getKey(), entry.getValue());
                }
            }
            
            if(logger.isDebugEnabled()) {
                logger.debug("Evaluating EL expression: {}", expression);
            }
            
            return elProc.eval(expression);        
        } catch (final ELException ex){
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
    
}
