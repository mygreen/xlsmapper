package com.gh.mygreen.xlsmapper.expression;

import java.util.HashMap;
import java.util.Map;


/**
 * 式言語の共通インタフェース。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public abstract class ExpressionLanguage {
    
    /**
     * 引数で与えた式を評価する。
     * @param expression 評価対象の式。
     * @param values 式中で利用可のな変数。
     * @return 評価した式。
     * @throws ExpressionEvaluationException 式のパースや評価に失敗した場合にスローされます。
     */
    public abstract Object evaluate(String expression, Map<String, ?> values);
    
    /**
     * プロパティの値を取得する。
     * @since 0.5
     * @param property プロパティの名前
     * @param target プロパティの取得対象のオブジェクト。
     * @return プロパティの値。
     * @throws ExpressionEvaluationException 式のパースや評価に失敗した場合にスローされます。
     */
    public Object getProperty(String property, Object target) {
        
        final Map<String, Object> vars = new HashMap<>();
        vars.put("targetObj", target);
        return evaluate("targetObj." + property, vars);
        
    }
    
}
