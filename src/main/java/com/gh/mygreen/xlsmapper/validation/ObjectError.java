package com.gh.mygreen.xlsmapper.validation;

import java.util.Map;


/**
 * オブジェクトのエラー情報を保持するクラス。
 *
 * @author T.TSUCHIE
 *
 */
public class ObjectError {
    
    /**
     * オブジェクト名
     */
    private final String objectName;
    
    /** 
     * メッセージコード
     * <p>複数指定可能で、先頭にあるものほど優先度が高い。
     */
    private String[] codes;
    
    /**
     * メッセージの引数。マップ形式のメッセージの場合に使用する。
     */
    private Map<String, ?> vars;
    
    /**
     * メッセージの引数。インデックス形式のメッセージに使用する。
     */
    private Object[] args;
    
    /** オブジェクトのラベル。設定されてない場合もある。 */
    private String label;
    
    /** デフォルトメッセージ */
    private String defaultMessage;
    
    public ObjectError(final String objectName) {
        this.objectName = objectName;
    }
    
    public ObjectError(final String objectName, final String[] codes, final Object[] args) {
        this.objectName = objectName;
        this.codes = codes;
        this.args = args;
    }
    
    public ObjectError(final String objectName, final String[] codes, final Map<String, ?> vars) {
        this.objectName = objectName;
        this.codes = codes;
        this.vars = vars;
    }
    
    public String getObjectName() {
        return objectName;
    }
    
    public String[] getCodes() {
        return codes;
    }
    
    public Map<String, ?> getVars() {
        return vars;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    public ObjectError setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
        return this;
    }
    
}
