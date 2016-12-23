package com.gh.mygreen.xlsmapper.validation.fieldvalidation;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * 正規表現を指定し、入力値チェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.pattern」。</li>
 * </ul>
 * 
 * @version 1.5.1
 *
 */
public class PatternValidator extends AbstractFieldValidator<String> {
    
    private final Pattern pattern;
    
    private final String patternName;
    
    /**
     * 正規表現のパターンを指定するコンストラクタ。
     * @param pattern 正規表現のパターン。
     */
    public PatternValidator(final String pattern) {
        this(Pattern.compile(pattern));
    }
    
    /**
     * 正規表現のパターンを指定するコンストラクタ。
     * @param pattern 正規表現のパターン。
     */
    public PatternValidator(final Pattern pattern) {
        this(pattern, null);
    }
    
    /**
     * 正規表現のパターンとその名称を指定するコンストラクタ。
     * 
     * @since 1.5.1
     * @param pattern 正規表現のパターン。
     * @param patternName エラーメッセージ中で使用するパターンの名称。
     */
    public PatternValidator(final String pattern, final String patternName) {
        this(Pattern.compile(pattern), patternName);
    }
    
    /**
     * 正規表現のパターンとその名称を指定するコンストラクタ。
     * 
     * @since 1.5.1
     * @param pattern 正規表現のパターン。
     * @param patternName エラーメッセージ中で使用するパターンの名称。
     */
    public PatternValidator(final Pattern pattern, final String patternName) {
        super();
        ArgUtils.notNull(pattern, "pattern");
        this.pattern = pattern;
        this.patternName = patternName;
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
        vars.put("pattern", getPattern().pattern());
        vars.put("patternName", getPatternName());
        return vars;
    }
    
    /**
     * 
     * @return pattern を取得する
     */
    public Pattern getPattern() {
        return pattern;
    }
    
    /**
     * @since 1.5.1
     * @return パターン名を取得する。指定されていない場合はnullを返す。
     */
    public String getPatternName() {
        return patternName;
    }
}
