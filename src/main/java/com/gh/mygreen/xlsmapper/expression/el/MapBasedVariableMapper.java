package com.gh.mygreen.xlsmapper.expression.el;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;


/**
 * 変数のマッパーを作成する
 *
 * @author T.TSUCHIE
 *
 */
public class MapBasedVariableMapper extends VariableMapper {
    
    private Map<String, ValueExpression> varMap = Collections.emptyMap();
    
    @Override
    public ValueExpression resolveVariable(final String variable) {
        return varMap.get(variable);
    }
    
    @Override
    public ValueExpression setVariable(final String variable, final ValueExpression expression) {
        if(varMap.isEmpty()) {
            varMap = new HashMap<String, ValueExpression>();
        }
        
        return varMap.put(variable, expression);
    }
}
