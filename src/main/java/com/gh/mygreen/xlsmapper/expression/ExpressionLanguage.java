package com.gh.mygreen.xlsmapper.expression;

import java.util.Map;


/**
 * 式言語の共通インタフェース。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public interface ExpressionLanguage {
    
    /**
     * 引数で与えた式を評価する。
     * @param expression 評価対象の式。
     * @param values 式中で利用可な変数。
     * @return 評価した式。
     * @throws IllegalArgumentException expression or values is null. expression is empty.
     * @throws ExpressionEvaluationException 式のパースや評価に失敗した場合にスローされます。
     */
    Object evaluate(String expression, Map<String, ?> values);
    
}
