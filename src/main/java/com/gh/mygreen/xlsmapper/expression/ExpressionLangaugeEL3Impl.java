package com.gh.mygreen.xlsmapper.expression;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.BeanNameResolver;
import javax.el.ELException;
import javax.el.ELProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Function;
import com.github.mygreen.expression.el.tld.Taglib;

/**
 * EL式(EL 3.x)を使用するための実装。
 *
 * @since 1.6
 * @author T.TSUCHIE
 *
 */
public class ExpressionLangaugeEL3Impl extends AbstractExpressionLanguageEL {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLangaugeEL3Impl.class);
    
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notNull(values, "values");
        
        try {
            final ELProcessor elProc = new ELProcessor();
            
            final Map<String, Object> beans = new HashMap<String, Object>();
            for (final Entry<String, ? > entry : values.entrySet()) {
                if(isFormatter(entry.getKey(), entry.getValue())) {
                    // Formatterの場合は、ラップクラスを設定する。
                    beans.put(entry.getKey(), new FormatterWrapper((Formatter) entry.getValue()));
                } else {
                    beans.put(entry.getKey(), entry.getValue());
                }
            }
            
            elProc.getELManager().addBeanNameResolver(new LocalBeanNameResolver(beans));
            
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
    
    /**
     * EL3.0用の式中の変数のResolver。
     * ・存在しない場合はnullを返す。
     *
     */
    private class LocalBeanNameResolver extends BeanNameResolver {
        
        private final Map<String, Object> map;
        
        public LocalBeanNameResolver(final Map<String, ?> map) {
            this.map = new HashMap<>(map);
        }
        
        @Override
        public boolean isNameResolved(final String beanName){
            // 存在しない場合はnullを返すように、必ずtrueを設定する。
            return true;
        }
        
        @Override
        public Object getBean(final String beanName){
            return map.get( beanName );
        }
        
        @Override
        public void setBeanValue(String beanName, Object value){
            map.put(beanName, value );
        }

    }
    
}
