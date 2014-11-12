package com.gh.mygreen.xlsmapper.expression.el;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;


/**
 * 関数のマッパーを作成する。
 * <p>EL関数を「f:escape(...)」などのように登録しておくことで利用可能とする。
 *
 * @author T.TSUCHIE
 *
 */
public class MapBasedFunctionMapper extends FunctionMapper {
    
    private static final String FUNCTION_NAME_SEPARATOR = ":";
    
    private Map<String, Method> functionMap = Collections.emptyMap();
    
    @Override
    public Method resolveFunction(final String prefix, final String localName) {
        return functionMap.get(createKey(prefix, localName));
    }
    
    public void setFunction(final String prefix, final String localName, final Method method) {
        if(functionMap.isEmpty()) {
            functionMap = new HashMap<String, Method>();
        }
        functionMap.put(FUNCTION_NAME_SEPARATOR, method);
    }
    
    private String createKey(final String prefix, final String localName) {
        return prefix + FUNCTION_NAME_SEPARATOR + localName;
    }
}
