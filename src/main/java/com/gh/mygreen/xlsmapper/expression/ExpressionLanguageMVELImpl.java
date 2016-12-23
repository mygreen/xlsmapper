package com.gh.mygreen.xlsmapper.expression;

import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * 式言語「MVEL」の実装。
 * <p>利用する際には、MVEL2.xのライブラリが必要です。</p>
 * 
 * @version 1.6
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageMVELImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageMVELImpl.class);
    
    private final ObjectCache<String, Object> expressionCache = new ObjectCache<String, Object>();
    
    private ParserConfiguration parserConfiguration = new ParserConfiguration();
    
    @Override
    public Object evaluate(final String expression, final Map<String, ?> values) {
        
        ArgUtils.notEmpty(expression, "expression");
        ArgUtils.notNull(values, "values");
        
        try {
            if(logger.isDebugEnabled()) {
                logger.debug("Evaluating MVEL expression: {}", expression);
            }
            
            Object expr = expressionCache.get(expression);
            if (expr == null) {
                expr = MVEL.compileExpression(expression, new ParserContext(parserConfiguration));
                expressionCache.put(expression, expr);
            }
            
            return MVEL.executeExpression(expr, values);
        } catch(Exception ex) {
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with MVEL failed.", expression), ex);
        }
        
    }
    
    /**
     * MVELの式を評価する際の設定情報を取得する。
     * @since 1.6
     * @return
     */
    public ParserConfiguration getParserConfiguration() {
        return parserConfiguration;
    }
    
    /**
     * MVELの式を評価する際の設定情報を設定する。
     * @since 1.6
     * @param parserConfiguration
     */
    public void setParserConfiguration(ParserConfiguration parserConfiguration) {
        this.parserConfiguration = parserConfiguration;
    }
    
    
}
