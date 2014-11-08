package org.mygreen.xlsmapper.expression;

import java.util.Map;


/**
 * 式言語の共通インタフェース。
 * 
 * @author T.TSUCHIE
 *
 */
public interface ExpressionLanguage {
    
    /**
     * 引数で与えた式を評価する。
     * @param expression 評価対象の式。
     * @param values 式中で利用可のな変数。
     * @return 評価した式。
     */
    Object evaluate(String expression, Map<String, ?> values);
    
}
