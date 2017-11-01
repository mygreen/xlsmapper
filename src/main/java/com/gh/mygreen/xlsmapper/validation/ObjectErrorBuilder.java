package com.gh.mygreen.xlsmapper.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link ObjectError}のインスタンスを組み立てるビルダ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ObjectErrorBuilder {
    
    private final String objectName;
    
    private final String[] codes;
    
    private Map<String, Object> variables = new HashMap<>();
    
    private String defaultMessage;
    
    private String sheetName;
    
    private String label;
    
    /**
     * ビルダのインスタンスを作成します
     * @param objectName オブジェクト名
     * @param codes メッセージコード。複数指定可能で、先頭にあるものほど優先度が高い。
     */
    public ObjectErrorBuilder(final String objectName, final String[] codes) {
        this.objectName = objectName;
        this.codes = codes;
    }
    
    /**
     * メッセージ中の変数を追加します。
     * @param variables 変数のマップ
     * @return 自身のインスタンス
     */
    public ObjectErrorBuilder variables(final Map<String, Object> variables) {
        this.variables.putAll(variables);
        return this;
    }
    
    /**
     * メッセージ中の変数を追加します。
     * @param key 変数のキー名
     * @param value 変数の値
     * @return 自身のインスタンス
     */
    public ObjectErrorBuilder variables(final String key, final Object value) {
        this.variables.put(key, value);
        return this;
    }
    
    /**
     * デフォルトメッセージを設定します。
     * <p>指定したコードに対するメッセージが見つからない場合に、適用されるメッセージ。</p>
     * @param defaultMessage デフォルトメッセージ
     * @return 自身のインスタンス
     */
    public ObjectErrorBuilder defaultMessage(final String defaultMessage) {
        this.defaultMessage = defaultMessage;
        return this;
    }
    
    /**
     * シート名を設定します。
     * @param sheetName シート名
     * @return 自身のインスタンス
     */
    public ObjectErrorBuilder sheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    
    /**
     * ラベルを設定します。
     * <p>テーブル名やカラム名が設定します。</p>
     * @param label ラベル
     * @return 自身のインスタンス
     */
    public ObjectErrorBuilder label(final String label) {
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
    public ObjectErrorBuilder label(final Optional<String> label) {
        label.ifPresent(l -> label(l));
        return this;
    }
    
    /**
     * {@link ObjectError}のインスタンスを組み立てます。
     * @return {@link ObjectError}のインスタンス
     */
    public ObjectError build() {
        
        final ObjectError error = new ObjectError(objectName, codes, variables);
        error.setDefaultMessage(defaultMessage);
        error.setSheetName(sheetName);
        error.setLabel(label);
        
        return error;
    }
    
}
