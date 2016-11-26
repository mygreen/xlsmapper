package com.gh.mygreen.xlsmapper.expression;

import java.util.Formatter;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ELException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.Utils;
import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Function;
import com.github.mygreen.expression.el.tld.Taglib;


/**
 * 標準のEL式(EL2.x)を使用するための実装。
 * <p>利用する際には、ELのライブラリが必要です。
 * 
 * @version 1.5
 * 
 */
public class ExpressionLanguageELImpl2 extends AbstractExpressionLanguageELImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageELImpl2.class);
    
    /**
     * EL2.xで評価する
     * @param expression
     * @param values
     * @return
     */
    @Override
    protected Object doEvaluate(final String expression, final Map<String, ?> values) {
        
        try {
            final com.github.mygreen.expression.el.ELProcessor elProc = new com.github.mygreen.expression.el.ELProcessor();
            
            for (final Entry<String, ? > entry : values.entrySet()) {
                if(isFormatter(entry.getKey(), entry.getValue())) {
                    elProc.setVariable(entry.getKey(), new FormatterWrapper((Formatter) entry.getValue()));
                } else {
                    elProc.setVariable(entry.getKey(), entry.getValue());
                }
            }
            
            // カスタムタグを登録する。
            for(Taglib taglib : taglibList) {
                final String prefix = Utils.trimToEmpty(taglib.getShortName());
                
                for(Function function : taglib.getFunctions()) {
                    final String className = Utils.trimToEmpty(function.getFunctionClass());
                    final String signature = Utils.trimToEmpty(function.getFunctionSignature());
                    final String name = Utils.trimToEmpty(function.getName());
                        
                    try {
                        elProc.defineFunction(prefix, name, className, signature);
                        
                    } catch(ClassNotFoundException | NoSuchMethodException ex) {
                        throw new ExpressionEvaluationException(String.format("Faild defined with EL function : [%s:%s].", className, signature), ex);
                    }
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
    
}
