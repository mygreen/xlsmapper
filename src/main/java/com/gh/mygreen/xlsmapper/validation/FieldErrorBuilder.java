package com.gh.mygreen.xlsmapper.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.CellPosition;

/**
 * {@link FieldError}のインスタンスを組み立てるビルダ。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldErrorBuilder {
    
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
     * @param objectName オブジェクト名
     * @param field フィールド名。ネストしている場合は、親のパスを付与した形式（e.g. person.name）で指定します。
     * @param codes メッセージコード。複数指定可能で、先頭にあるものほど優先度が高い。
     */
    public FieldErrorBuilder(final String objectName, final String field, final String[] codes) {
        this.objectName = objectName;
        this.field = field;
        this.codes = codes;
    }
    
    /**
     * エラートとなったフィールドの値を設定します。
     * @param rejectedValue エラー元の値
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder rejectedValue(final Object rejectedValue) {
        this.rejectedValue = rejectedValue;
        return this;
    }
    
    /**
     * 型変換エラーかどうかを設定します。
     * @param conversionFailure trueの場合、型変換エラー
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder conversionFailure(final boolean conversionFailure) {
        this.conversionFailure = conversionFailure;
        return this;
    }
    
    /**
     * メッセージ中の変数を追加します。
     * @param variables 変数のマップ
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder variables(final Map<String, Object> variables) {
        this.variables.putAll(variables);
        return this;
    }
    
    /**
     * メッセージ中の変数を追加します。
     * @param key 変数のキー名
     * @param value 変数の値
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder variables(final String key, final Object value) {
        this.variables.put(key, value);
        return this;
    }
    
    /**
     * デフォルトメッセージを設定します。
     * <p>指定したコードに対するメッセージが見つからない場合に、適用されるメッセージ。</p>
     * @param defaultMessage デフォルトメッセージ
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder defaultMessage(final String defaultMessage) {
        this.defaultMessage = defaultMessage;
        return this;
    }
    
    /**
     * シート名を設定します。
     * @param sheetName シート名
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder sheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    
    /**
     * ラベルを設定します。
     * <p>テーブル名やカラム名が設定します。</p>
     * @param label ラベル
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder label(final String label) {
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
    public FieldErrorBuilder label(final Optional<String> label) {
        label.ifPresent(l -> label(l));
        return this;
    }
    
    /**
     * セルのアドレス情報を設定します。
     * @param address アドレス情報
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder address(final CellPosition address) {
        this.address = address;
        return this;
    }
    
    /**
     * セルのアドレス情報を設定します。
     * <p>値が存在する場合のみ設定されます。</p>
     * @param address アドレス情報
     * @return 自身のインスタンス
     */
    public FieldErrorBuilder address(final Optional<CellPosition> address) {
        address.ifPresent(a -> address(a));
        return this;
    }
    
    /**
     * {@link FieldError}のインスタンスを組み立てる。
     * @return {@link FieldError}のインスタンス
     */
    public FieldError build() {
        
        final FieldError error = new FieldError(objectName, field, conversionFailure, codes, variables);
        error.setDefaultMessage(defaultMessage);
        error.setSheetName(sheetName);
        error.setLabel(label);
        error.setRejectedValue(rejectedValue);
        error.setAddress(address);
        
        return error;
    }
    
}
