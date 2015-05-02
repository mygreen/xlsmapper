package com.gh.mygreen.xlsmapper.expression.el;

import java.lang.reflect.Method;
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
    
    private Map<String, Method> functionMap = new HashMap<String, Method>();
    
    @Override
    public Method resolveFunction(final String prefix, final String localName) {
        return functionMap.get(createKey(prefix, localName));
    }
    
    /**
     * EL関数を登録する。
     * @param prefix
     * @param localName
     * @param method
     */
    public void mapFunction(final String prefix, final String localName, final Method method) {
        functionMap.put(createKey(prefix, localName), method);
    }
    
    private String createKey(final String prefix, final String localName) {
        return prefix + FUNCTION_NAME_SEPARATOR + localName;
    }
}
