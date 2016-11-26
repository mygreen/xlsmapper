package com.gh.mygreen.xlsmapper.expression;

import java.util.Map;

import com.github.mygreen.expression.el.tld.Taglib;
import com.github.mygreen.expression.el.tld.TldLoader;


/**
 * 標準のEL式を使用するための実装。
 * <p>利用する際には、ELのライブラリが必要です。
 * 
 * @version 1.5
 * 
 */
public class ExpressionLanguageELImpl implements ExpressionLanguage {
    
    /** EL3.xが使用可能かどうか */
    private static boolean availabledEl3;
    static {
        try {
            Class.forName("javax.el.ELProcessor");
            availabledEl3 = true;
        } catch (ClassNotFoundException e) {
            availabledEl3 = false;
        }
    }
    
    /** 実装クラス */
    private AbstractExpressionLanguageELImpl impl;
    
    
    public ExpressionLanguageELImpl() {
        if (availabledEl3) {
            this.impl = new ExpressionLanguageELImpl3();
        } else {
            this.impl = new ExpressionLanguageELImpl2();
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
     * カスタムタグの定義ファイル「TLD（Tag Library Defenitioin）」を登録する。
     * <p>{@link TldLoader}クラスで読み込む。
     * 
     * @since 1.5
     * @param taglib カスタムタグの定義内容。
     */
    public void register(final Taglib taglib) {
        this.impl.register(taglib);
    }
    
}
