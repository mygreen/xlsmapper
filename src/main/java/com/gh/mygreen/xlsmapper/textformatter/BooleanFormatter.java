package com.gh.mygreen.xlsmapper.textformatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.github.mygreen.cellformatter.lang.ArgUtils;

/**
 * Booleanのフォーマッタ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BooleanFormatter implements TextFormatter<Boolean> {
    
    private static final String[] DEFAULT_READ_TRUE_VALUES = new String[] {"true", "1", "yes", "on", "y", "t"};
    private static final String[] DEFAULT_READ_FALSE_VALUES = new String[] {"false", "0", "no", "off", "f", "n"};
    
    private static final String DEFAULT_WRITE_TRUE_VALUE = "true";
    private static final String DEFAULT_WRITE_FALSE_VALUE = "false";
    
    private final Set<String> loadTrueValues;
    
    private final Set<String> loadFalseValues;
    
    private final String saveTrueValue;
    
    private final String saveFalseValue;
    
    private boolean ignoreCase;
    
    private boolean failToFalse;
    
    public BooleanFormatter() {
        this(DEFAULT_READ_TRUE_VALUES, DEFAULT_READ_FALSE_VALUES,
                DEFAULT_WRITE_TRUE_VALUE, DEFAULT_WRITE_FALSE_VALUE,
                true, false);
    }
    
    public BooleanFormatter(final String[] loadTrueValues, final String[] loadFalseValues,
            final String saveTrueValue, final String saveFalseValue,
            final boolean ignoreCase, boolean failToFalse) {
        
        ArgUtils.notNull(loadTrueValues, "loadTrueValues");
        ArgUtils.notNull(loadFalseValues, "loadFalseValues");
        
        this.loadTrueValues = toSet(loadTrueValues);
        this.loadFalseValues = toSet(loadFalseValues);
        this.saveTrueValue = saveTrueValue;
        this.saveFalseValue = saveFalseValue;
        this.ignoreCase = ignoreCase;
        this.failToFalse = failToFalse;
    }
    
    private static Set<String> toSet(final String[] values) {
        
        Set<String> set = new LinkedHashSet<>();
        Collections.addAll(set, values);
        return Collections.unmodifiableSet(set);
        
    }
    
    @Override
    public Boolean parse(final String text) {
        
        if(contains(loadTrueValues, text, ignoreCase) ) {
            return Boolean.TRUE;
            
        } else if(contains(loadFalseValues, text, ignoreCase) ) {
            return Boolean.FALSE;
            
        } else {
            if(failToFalse) {
                return Boolean.FALSE;
            } else {
                final Map<String, Object> vars = new HashMap<>();
                vars.put("trueValues", loadTrueValues);
                vars.put("falseValues", loadFalseValues);
                
                vars.put("ignoreCase", ignoreCase);
                vars.put("failToFalse", failToFalse);
                
                throw new TextParseException(text, Boolean.class, vars);
            }
        }
    }
    
    private static boolean contains(final Set<String> set, final String value, final boolean ignoreCase) {
        
        if(ignoreCase) {
            for(String element : set) {
                if(element.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            
            return false;
        } else {
            return set.contains(value);
        }
        
    }
    
    @Override
    public String format(final Boolean object) {
        
        return object ? saveTrueValue : saveFalseValue;
        
    }
    
    public boolean isIgnoreCase() {
        return ignoreCase;
    }
    
    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    public boolean isFailToFalse() {
        return failToFalse;
    }
    
    public void setFailToFalse(boolean failToFalse) {
        this.failToFalse = failToFalse;
    }
    
    public Set<String> getLoadTrueValues() {
        return loadTrueValues;
    }
    
    public Set<String> getLoadFalseValues() {
        return loadFalseValues;
    }
    
    public String getSaveTrueValue() {
        return saveTrueValue;
    }
    
    public String getSaveFalseValue() {
        return saveFalseValue;
    }
}
