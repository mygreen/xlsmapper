package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * 正規表現を指定し、入力値チェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.pattern」。
 *
 */
public class PatternValidator extends AbstractFieldValidator<String> {
    
    private final Pattern pattern;
    
    public PatternValidator(final String pattern) {
        this(Pattern.compile(pattern));
    }
    
    public PatternValidator(final Pattern pattern) {
        super();
        ArgUtils.notNull(pattern, "pattern");
        this.pattern = pattern;
    }
    
    @Override
    public String getDefaultMessageKey() {
        return "cellFieldError.pattern";
    }
    
    @Override
    protected boolean validate(final String value) {
        if(isNullValue(value)) {
            return true;
        }
        
        if(pattern.matcher(value).matches()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected LinkedHashMap<String, Object> getMessageVars(final String value) {
        final LinkedHashMap<String, Object> vars = new LinkedHashMap<>();
        vars.put("validatedValue", value);
        vars.put("pattern", pattern.pattern());
        return vars;
    }
    
    /**
     * pattern を取得する
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }
}
