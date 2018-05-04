package com.gh.mygreen.xlsmapper.validation.fieldvalidation.impl;

import java.util.Map;
import java.util.regex.Pattern;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.AbstractFieldValidator;
import com.gh.mygreen.xlsmapper.validation.fieldvalidation.CellField;


/**
 * 正規表現を指定し、入力値チェックする。
 * <ul>
 *  <li>メッセージキーは、「cellFieldError.pattern」。</li>
 *   <li>「validatedValue」：検証対象の値のオブジェクト。</li>
 *   <li>「pattern」：正規表現の式。</li>
 *   <li>「description」：正規表現の名称。指定されていない場合は、null。</li>
 * </ul>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class PatternValidator extends AbstractFieldValidator<String> {

    private final Pattern pattern;

    private final String description;

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
     * @param description エラーメッセージ中で使用するパターンの名称。
     */
    public PatternValidator(final String pattern, final String description) {
        this(Pattern.compile(pattern), description);
    }

    /**
     * 正規表現のパターンとその名称を指定するコンストラクタ。
     *
     * @since 1.5.1
     * @param pattern 正規表現のパターン。
     * @param description エラーメッセージ中で使用するパターンの名称。
     */
    public PatternValidator(final Pattern pattern, final String description) {
        super();
        ArgUtils.notNull(pattern, "pattern");
        this.pattern = pattern;
        this.description = description;
    }

    @Override
    public PatternValidator addGroup(final Class<?>... group) {
        return (PatternValidator)super.addGroup(group);
    }

    @Override
    public String getMessageKey() {
        return "cellFieldError.pattern";
    }

    @Override
    protected Map<String, Object> getMessageVariables(final CellField<String> cellField) {
        final Map<String, Object> vars = super.getMessageVariables(cellField);
        vars.put("pattern", getPattern().pattern());
        vars.put("description", getDescription());
        return vars;
    }

    @Override
    protected void onValidate(final CellField<String> cellField) {

        if(pattern.matcher(cellField.getValue()).matches()) {
            return;
        }

        error(cellField);
    }

    /**
     * 設定されている正規表現を取得する。
     * @return pattern を取得する
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * 正規表現に対する名称を取得する。
     * @since 1.5.1
     * @return パターン名を取得する。指定されていない場合はnullを返す。
     */
    public String getDescription() {
        return description;
    }
}
