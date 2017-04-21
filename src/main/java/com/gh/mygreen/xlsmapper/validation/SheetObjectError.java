package com.gh.mygreen.xlsmapper.validation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * シートのエラー情報を保持するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SheetObjectError implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 4850835995741701297L;
    
    /**
     * オブジェクト名
     */
    private final String objectName;
    
    /** 
     * メッセージコード
     * <p>複数指定可能で、先頭にあるものほど優先度が高い。</p>
     */
    private final String[] codes;
    
    /**
     * メッセージの引数。
     */
    private Map<String, Object> variables;
    
    /**
     * デフォルトメッセージ。
     * <p>指定したコードに対するメッセージが見つからない場合に、適用されるメッセージ。</p>
     */
    private String defaultMessage;
    
    /**
     * シート名
     */
    private String sheetName;
    
    /**
     * ラベル
     * <p>テーブル名やカラム名が設定されます。</p>
     */
    private String label;
    
    public SheetObjectError(final String objectName, final String[] codes, final Map<String, Object> variables) {
        this.objectName = objectName;
        this.codes = codes;
        this.variables = variables;
    }
    
    /**
     * オブジェクト名の取得。
     * @return Beanクラス名のパッケージ名を除いた値。
     */
    public String getObjectName() {
        return objectName;
    }
    
    /**
     * メッセージコードの候補を取得する。
     * @return メッセージコードの候補。
     */
    public String[] getCodes() {
        return codes;
    }
    
    /**
     * メッセージ変数を取得する。
     * @return メッセージをフォーマットする際に、その中で利用可能な変数。
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    /**
     * デフォルトメッセージを設定する。
     * <p>メッセージコードで指定したメッセージリソースが見つからない場合に適用されるメッセージ。</p>
     * @param defaultMessage デフォルトメッセージ
     */
    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
    
    /**
     * デフォルトメッセージを取得する。
     * <p>メッセージコードで指定したメッセージリソースが見つからない場合に適用されるメッセージ。</p>
     * @return 設定されていない場合は空を返します。
     */
    public Optional<String> getDefaultMessage() {
        return Optional.ofNullable(defaultMessage);
    }
    
    /**
     * シート名を取得します
     * @return 設定されていない場合は、空を返します。
     */
    public Optional<String> getSheetName() {
        return Optional.ofNullable(sheetName);
    }
    
    /**
     * シート名を設定します。
     * @param sheetName シート名
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    /**
     * ラベルを取得します。
     * <p>テーブル名やカラム名が設定されます。</p>
     * @return 設定されていない場合は空を返します。
     */
    public Optional<String> getLabelAsOptional() {
        return Optional.ofNullable(label);
    }
    
    /**
     * ラベルを取得します。
     * <p>テーブル名やカラム名が設定されます。</p>
     * @return 設定されていない場合はnullを返します。
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * ラベルを設定します。
     * <p>テーブル名やカラム名が設定されます。</p>
     * @param label ラベル
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * {@link SheetObjectError}のインスタンスのビルダクラス
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static final class Builder {
        
        private final SheetBindingErrors<?> errors;
        
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
        public Builder(final SheetBindingErrors<?> errors, final String objectName, final String[] codes) {
            this.errors = errors;
            this.objectName = objectName;
            this.codes = codes;
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
         * {@link SheetObjectError}のインスタンスを組み立て、{@link SheetBindingErrors}にエラーとして追加します。
         * @return {@link SheetBindingErrors}のインスタンス
         */
        public SheetBindingErrors<?> buildAndAddError() {
            
            final SheetObjectError error = new SheetObjectError(objectName, codes, variables);
            error.setDefaultMessage(defaultMessage);
            error.setSheetName(sheetName);
            error.setLabel(label);
            
            errors.addError(error);
            
            return errors;
        }
        
    }
    
}
