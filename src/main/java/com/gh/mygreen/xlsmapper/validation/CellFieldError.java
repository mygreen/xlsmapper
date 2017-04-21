package com.gh.mygreen.xlsmapper.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * オブジェクトのフィールドであるセルのエラー情報を保持するクラスです。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CellFieldError extends SheetObjectError {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * フィールド名
     */
    private final String field;
    
    /**
     * 型変換に失敗したときのエラーかどうか
     */
    private final boolean conversionFailure;
    
    /**
     * エラーとなる値
     */
    private Object rejectedValue;
    
    /**
     * セルのアドレス情報
     */
    private CellPosition address;
    
    public CellFieldError(final String objectName, final String field, final boolean conversionFailure,
            final String[] codes, final Map<String, Object> variables) {
        super(objectName, codes, variables);
        
        this.field = field;
        this.conversionFailure = conversionFailure;
    }
    
    /**
     * 型変換に失敗したかどうか。
     * <p>型変換に失敗した場合、検証対象のBeanやフィールドに値が設定されないないため、
     *    後から値を検証する際に検証をスキップする判定に利用する。
     * </p>
     * @return trueの場合、型変換にしっぱいしたエラー。
     */
    public boolean isConversionFailure() {
        return conversionFailure;
    }    
    
    /**
     * フィールド名を取得する。
     * <p>ネストしている場合は、親のパスを付与した形式（e.g. person.name）となります。</p>
     * @return Beanにされたフィールドの名称を返す。
     */
    public String getField() {
        return field;
    }
    
    /**
     * エラートとなったフィールドの値を取得する。
     * <p>ただし、型変換エラーの場合、変換前の値となります。</p>
     * @return フィールドの値。
     */
    public Object getRejectedValue() {
       return rejectedValue;
    }
    
    /**
     * エラートとなったフィールドの値を設定する。
     * <p>ただし、型変換エラーの場合、変換前の値となります。</p>
     * @param rejectedValue フィールドの値。
     */
    public void setRejectedValue(Object rejectedValue) {
       this.rejectedValue = rejectedValue;
    }
    
    /**
     * セルのアドレス情報を取得します。
     * @return 設定されていない場合は、空を返します。
     */
    public Optional<CellPosition> getAddressAsOptional() {
        return Optional.ofNullable(address);
    }
    
    /**
     * セルのアドレス情報を取得します。
     * @return 設定されていない場合は、nullを返します。
     */
    public CellPosition getAddress() {
        return address;
    }
    
    /**
     * セルのアドレス情報を設定します。
     * @param address アドレス情報
     */
    public void setAddress(CellPosition address) {
        this.address = address;
    }
    
    /**
     * {@link CellFieldError}のインスタンスのビルダクラス。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static final class Builder {
        
        private final SheetBindingErrors<?> errors;
        
        private final String objectName;
        
        private final String field;
        
        private final String[] codes;
        
        private Object rejectedValue;
        
        private boolean conversionFailure;
        
        private Map<String, Object> variables = new HashMap<>();
        
        private String defaultMessage;
        
        private String sheetName;
        
        private String label;
        
        private CellPosition address;
        
        /**
         * ビルダのインスタンスを作成します
         * @param errors エラー情報
         * @param objectName オブジェクト名
         * @param field フィールド名。ネストしている場合は、親のパスを付与した形式（e.g. person.name）で指定します。
         * @param codes メッセージコード。複数指定可能で、先頭にあるものほど優先度が高い。
         */
        public Builder(final SheetBindingErrors<?> errors, final String objectName, final String field, final String[] codes) {
            this.errors = errors;
            this.objectName = objectName;
            this.field = field;
            this.codes = codes;
        }
        
        /**
         * エラートとなったフィールドの値を設定します。
         * @param rejectedValue エラー元の値
         * @return 自身のインスタンス
         */
        public Builder rejectedValue(final Object rejectedValue) {
            this.rejectedValue = rejectedValue;
            return this;
        }
        
        /**
         * 型変換エラーかどうかを設定します。
         * @param conversionFailure trueの場合、型変換エラー
         * @return 自身のインスタンス
         */
        public Builder conversionFailure(final boolean conversionFailure) {
            this.conversionFailure = conversionFailure;
            return this;
        }
        
        /**
         * メッセージ中の変数を追加します。
         * @param variables 変数のマップ
         * @return 自身のインスタンス
         */
        public Builder variables(final Map<String, Object> variables) {
            this.variables.putAll(variables);
            return this;
        }
        
        /**
         * メッセージ中の変数を追加します。
         * @param key 変数のキー名
         * @param value 変数の値
         * @return 自身のインスタンス
         */
        public Builder variables(final String key, final Object value) {
            this.variables.put(key, value);
            return this;
        }
        
        /**
         * デフォルトメッセージを設定します。
         * <p>指定したコードに対するメッセージが見つからない場合に、適用されるメッセージ。</p>
         * @param defaultMessage デフォルトメッセージ
         * @return 自身のインスタンス
         */
        public Builder defaultMessage(final String defaultMessage) {
            this.defaultMessage = defaultMessage;
            return this;
        }
        
        /**
         * シート名を設定します。
         * @param sheetName シート名
         * @return 自身のインスタンス
         */
        public Builder sheetName(final String sheetName) {
            this.sheetName = sheetName;
            return this;
        }
        
        /**
         * ラベルを設定します。
         * <p>テーブル名やカラム名が設定します。</p>
         * @param label ラベル
         * @return 自身のインスタンス
         */
        public Builder label(final String label) {
            this.label = label;
            return this;
        }
        
        /**
         * ラベルを設定します。
         * <p>テーブル名やカラム名が設定します。</p>
         * <p>値が存在する場合のみ設定されます。</p>
         * @param label ラベル
         * @return 自身のインスタンス
         */
        public Builder label(final Optional<String> label) {
            label.ifPresent(l -> label(l));
            return this;
        }
        
        /**
         * セルのアドレス情報を設定します。
         * @param address アドレス情報
         * @return 自身のインスタンス
         */
        public Builder address(final CellPosition address) {
            this.address = address;
            return this;
        }
        
        /**
         * セルのアドレス情報を設定します。
         * <p>値が存在する場合のみ設定されます。</p>
         * @param address アドレス情報
         * @return 自身のインスタンス
         */
        public Builder address(final Optional<CellPosition> address) {
            address.ifPresent(a -> address(a));
            return this;
        }
        
        /**
         * {@link CellFieldError}のインスタンスを組み立て、{@link SheetBindingErrors}にエラーとして追加します。
         * @return {@link CellFieldError}のインスタンス
         */
        public SheetBindingErrors<?> buildAndAddError() {
            
            final CellFieldError error = new CellFieldError(objectName, field, conversionFailure, codes, variables);
            error.setDefaultMessage(defaultMessage);
            error.setSheetName(sheetName);
            error.setLabel(label);
            error.setRejectedValue(rejectedValue);
            error.setAddress(address);
            
            errors.addError(error);
            
            return errors;
        }
        
    }
    
}
