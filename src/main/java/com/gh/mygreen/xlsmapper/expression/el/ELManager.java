package com.gh.mygreen.xlsmapper.expression.el;

import java.lang.reflect.Method;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;


/**
 * EL式を評価するための{@link ExpressionFactory}と{@link ELContext}のインスタンスを管理するクラス。
 *
 * @author T.TSUCHIE
 *
 */
public class ELManager {
    
    private LocalELContext elContext;
    
    /**
     * {@link ExpressionFactory}のインスタンスを取得する。
     * @return
     */
    public static ExpressionFactory getExpressionFactory() {
        return ExpressionFactory.newInstance();
    }
    
    /**
     * {@link ELContext}のインスタンスを取得する。{@link LocalELContext}を返す。
     * @return
     */
    public LocalELContext getELContext() {
        if(elContext == null) {
            this.elContext = new LocalELContext();
        }
        
        return elContext;
    }
    
    /**
     * EL関数を登録する。
     * @param prefix
     * @param function
     * @param method
     */
    public void mapFunction(String prefix, String function, Method method) {
        getELContext().mapFunction(prefix, function, method);
    }
    
    /**
     * EL式中の変数を登録する。
     * @param variable
     * @param expression
     */
    public void setVariable(final String variable, final ValueExpression expression) {
        getELContext().setVariable(variable, expression);
    }
}
