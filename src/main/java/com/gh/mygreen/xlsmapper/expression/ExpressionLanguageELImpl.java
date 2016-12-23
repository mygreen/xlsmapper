package com.gh.mygreen.xlsmapper.expression;

import java.util.Map;

import com.github.mygreen.expression.el.tld.Taglib;


/**
 * 標準のEL式を使用するための実装。
 * <p>利用する際には、ELのライブラリが必要です。</p>
 * <p>EL2.x系とEL3.xの両方に対応しており、EL3.xが利用可能な場合は自動的にに切り替わります。
 *  <br>もし、直接指定したい場合は、{@link ExpressionLangaugeEL2Impl}か{@link ExpressionLangaugeEL3Impl}の実装クラスを直接指定してください。
 * </p>
 * 
 * 
 * @version 1.5
 * 
 */
public class ExpressionLanguageELImpl extends AbstractExpressionLanguageEL {
    
    /** EL3.xが使用可能かどうか */
    private static boolean AVAILABLED_EL3;
    {
        try {
            Class.forName("javax.el.ELProcessor");
            AVAILABLED_EL3 = true;
        } catch (ClassNotFoundException e) {
            AVAILABLED_EL3 = false;
        }
    }
    
    /**
     * EL式の実装クラス。
     */
    private AbstractExpressionLanguageEL impl;
    
    public ExpressionLanguageELImpl() {
        if(AVAILABLED_EL3) {
            this.impl = new ExpressionLangaugeEL3Impl();
        } else {
            this.impl = new ExpressionLangaugeEL2Impl();
            
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        return impl.evaluate(expression, values);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void register(final Taglib taglib) {
        impl.register(taglib);
    }
    
}
