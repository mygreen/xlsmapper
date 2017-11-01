package com.gh.mygreen.xlsmapper.validation;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * シートのエラー情報を保持するクラス。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ObjectError implements Serializable {

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
    
    /**
     * コンストラクタ
     * 
     * @param objectName オブジェクト名
     * @param codes メッセージコード
     * @param variables メッセージの引数
     */
    public ObjectError(final String objectName, final String[] codes, final Map<String, Object> variables) {
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
    
}
