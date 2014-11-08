package org.mygreen.xlsmapper.expression.el;

import java.lang.reflect.Method;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;


/**
 * ローカルでEL式を利用するための{@link ELContext}。
 *
 * @author T.TSUCHIE
 *
 */
public class LocalELContext extends ELContext {
    
    private static final CompositeELResolver DEFAULT_RESOLVER = new CompositeELResolver() {
        {
            add(new RootELResolver(true));
            add(new ArrayELResolver(false));
            add(new ListELResolver(false));
            add(new MapELResolver(false));
            add(new ResourceBundleELResolver());
            add(new BeanELResolver(false));
        }
    };
    
    /**
     * FunctionMapperの実装
     */
    private final MapBasedFunctionMapper functionMapper;
    
    /**
     * VariableMapperの実装
     */
    private final VariableMapper variableMapper;
    
    /**
     * ELResolverの実装
     */
    private final ELResolver resolver;
    
    public LocalELContext() {
        this.functionMapper = new MapBasedFunctionMapper();
        this.variableMapper = new MapBasedVariableMapper();
        this.resolver = DEFAULT_RESOLVER;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ELResolver getELResolver() {
        return resolver;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MapBasedFunctionMapper getFunctionMapper() {
        return functionMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }
    
    /**
     * EL式中で利用可能な変数を登録する
     * @param name
     * @param expression
     * @return
     */
    public ValueExpression setVariable(final String name, final ValueExpression expression) {
        return variableMapper.setVariable(name, expression);
    }
    
    /**
     * EL式中で利用可能なEL関数を登録する
     * @param prefix 接頭語
     * @param localName 名称
     * @param method 関数の実態
     */
    public void setFunction(final String prefix, final String localName, final Method method) {
        functionMapper.setFunction(prefix, localName, method);
    }
}
