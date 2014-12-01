package com.gh.mygreen.xlsmapper.expression.el;

import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;


/**
 * EL式を簡単に利用するためのインタフェース。
 * 
 * @author T.TSUCHIE
 *
 */
public class ELProcessor {
    
    private ELManager elManager = new ELManager();
    
    private ExpressionFactory factory = ELManager.getExpressionFactory();
    
    public ELManager getELManager() {
        return elManager;
    }
    
    /**
     * 戻り値のクラスタイプを指定して式を評価する
     * @param expression
     * @param expectedType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C> C eval(final String expression, final Class<C> expectedType) {
        
        final ValueExpression exp = factory.createValueExpression(
                elManager.getELContext(),
                bracket(expression),
                expectedType);
        
        return (C) exp.getValue(elManager.getELContext());
        
    }
    
    /**
     * 式を評価する
     * @param expression
     * @return
     */
    public Object eval(final String expression) {
        return eval(expression, Object.class);
    }
    
    /**
     * 
     * @param expression
     * @param value
     */
    public void setValue(final String expression, final Object value) {
        final ValueExpression exp = factory.createValueExpression(
                elManager.getELContext(),
                bracket(expression),
                Object.class);
        exp.setValue(elManager.getELContext(), value);
    }
    
    /**
     * 変数を設定する
     * @param variable
     * @param value
     */
    public void setVariable(final String variable, final Object value) {
        final ValueExpression exp = factory.createValueExpression(value, Object.class);
        elManager.setVariable(variable, exp);
    }
    
    /**
     * マップを元に変数を設定する
     * @param variables
     */
    public void setVariables(final Map<String, Object> variables) {
        for(Map.Entry<String, Object> entry : variables.entrySet()) {
            setVariable(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * EL式を括弧で囲む
     * @param expression
     * @return
     */
    private String bracket(String expression) {
        return "${" + expression + '}';
    }
    
}
