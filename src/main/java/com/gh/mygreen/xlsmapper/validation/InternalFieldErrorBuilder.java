package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link SheetBindingErrors}経由で呼び出される{@link FieldError}のビルダクラス。
 * <p>{@link #buildAndAddError()}でチェインでより使い安くする。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class InternalFieldErrorBuilder extends FieldErrorBuilder {
    
    private final SheetBindingErrors<?> errors;
    
    /**
     * ビルダのインスタンスを作成します
     * @param errors エラー情報
     * @param objectName オブジェクト名
     * @param field フィールド名。ネストしている場合は、親のパスを付与した形式（e.g. person.name）で指定します。
     * @param codes メッセージコード。複数指定可能で、先頭にあるものほど優先度が高い。
     */
    public InternalFieldErrorBuilder(final SheetBindingErrors<?> errors, final String objectName, final String field, final String[] codes) {
        super(objectName, field, codes);
        this.errors = errors;
    }
    
    @Override
    public InternalFieldErrorBuilder rejectedValue(final Object rejectedValue) {
        super.rejectedValue(rejectedValue);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder conversionFailure(final boolean conversionFailure) {
        super.conversionFailure(conversionFailure);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder variables(final Map<String, Object> variables) {
        super.variables(variables);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder variables(final String key, final Object value) {
        super.variables(key, value);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder defaultMessage(final String defaultMessage) {
        super.defaultMessage(defaultMessage);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder sheetName(final String sheetName) {
        super.sheetName(sheetName);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder label(final String label) {
        super.label(label);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder label(final Optional<String> label) {
        super.label(label);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder address(final CellPosition address) {
        super.address(address);
        return this;
    }
    
    @Override
    public InternalFieldErrorBuilder address(final Optional<CellPosition> address) {
        super.address(address);
        return this;
    }
    
    /**
     * {@link FieldError}のインスタンスを組み立て、{@link SheetBindingErrors}にエラーとして追加します。
     * @return {@link SheetBindingErrors}のインスタンス
     */
    public SheetBindingErrors<?> buildAndAddError() {
        
        final FieldError error = super.build();
        
        errors.addError(error);
        
        return errors;
    }
}
