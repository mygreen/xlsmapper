package com.gh.mygreen.xlsmapper.expression;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.github.mygreen.expression.el.FormatterWrapper;
import com.github.mygreen.expression.el.tld.Taglib;
import com.github.mygreen.expression.el.tld.TldLoader;


/**
 * ExpressionLanguageELImpl*で使用する共通メソッドを定義する抽象クラス。
 * 
 * @version 1.5
 * 
 */
public abstract class AbstractExpressionLanguageELImpl {
    
    /** TLDファイルの定義内容 */
    protected List<Taglib> taglibList = new ArrayList<>();
    
    /**
     * {@inheritDoc}
     */
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notEmpty(values, "values");
        
        return doEvaluate(expression, values);
        
    }
    
    protected abstract Object doEvaluate(final String expression, final Map<String, ?> values);
    
    /**
     * {@link FormatterWrapper}で囲むべき値かどうか判定する。
     * @param key
     * @param value
     * @return
     */
    protected boolean isFormatter(final String key, final Object value) {
        if(!RootResolver.FORMATTER.equals(key)) {
            return false;
        }
        
        if(value instanceof Formatter) {
            return true;
        }
        
        return false;
    }
    
    /**
     * カスタムタグの定義ファイル「TLD（Tag Library Defenitioin）」を登録する。
     * <p>{@link TldLoader}クラスで読み込む。
     * 
     * @since 1.5
     * @param taglib カスタムタグの定義内容。
     */
    public void register(final Taglib taglib) {
        this.taglibList.add(taglib);
    }
    
}
