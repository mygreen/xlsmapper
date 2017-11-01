package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;
import java.util.Optional;

/**
 * {@link SheetBindingErrors}経由で呼び出される{@link ObjectError}のビルダクラス。
 * <p>{@link #buildAndAddError()}でチェインでより使い安くする。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class InternalObjectErrorBuilder extends ObjectErrorBuilder {
    
    private final SheetBindingErrors<?> errors;
    
    /**
     * ビルダのインスタンスを作成します。
     * @param errors エラー情報
     * @param objectName オブジェクト名
     * @param codes メッセージコード。複数指定可能で、先頭にあるものほど優先度が高い。
     */
    public InternalObjectErrorBuilder(final SheetBindingErrors<?> errors, final String objectName, final String[] codes) {
        super(objectName, codes);
        this.errors = errors;
        
    }
    
    @Override
    public InternalObjectErrorBuilder variables(final Map<String, Object> variables) {
        super.variables(variables);
        return this;
    }
    
    @Override
    public InternalObjectErrorBuilder variables(final String key, final Object value) {
        super.variables(key, value);
        return this;
    }
    
    @Override
    public InternalObjectErrorBuilder defaultMessage(final String defaultMessage) {
        super.defaultMessage(defaultMessage);
        return this;
    }
    
    @Override
    public InternalObjectErrorBuilder sheetName(final String sheetName) {
        super.sheetName(sheetName);
        return this;
    }
    
    @Override
    public InternalObjectErrorBuilder label(final String label) {
        super.label(label);
        return this;
    }
    
    @Override
    public InternalObjectErrorBuilder label(final Optional<String> label) {
        super.label(label);
        return this;
    }
    
    /**
     * {@link ObjectError}のインスタンスを組み立て、{@link SheetBindingErrors}にエラーとして追加します。
     * @return {@link SheetBindingErrors}のインスタンス
     */
    public SheetBindingErrors<?> buildAndAddError() {
        
        final ObjectError error = build();
        
        errors.addError(error);
        
        return errors;
    }
    
}
