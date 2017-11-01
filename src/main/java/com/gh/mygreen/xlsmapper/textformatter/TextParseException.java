package com.gh.mygreen.xlsmapper.textformatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 文字列をパースしてオブジェクトに変換する際に失敗したときにスローされる例外。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TextParseException extends RuntimeException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * パース元の文字列
     */
    private final String fromText;
    
    /**
     * パース後のクラスタイプ
     */
    private final Class<?> toType;
    
    /**
     * エラー時の変数
     */
    private final Map<String, Object> errorVariables = new HashMap<>();
    
    /**
     * 文字列をオブジェクトへパース時に発生した例外を指定するコンストラクタ。
     * @param fromText パース対象の文字列
     * @param toType パース先のクラスタイプ
     * @param error パースする際に発生した冷害
     */
    public TextParseException(final String fromText, final Class<?> toType, final Throwable error) {
        this(fromText, toType, error, Collections.emptyMap());
    }
    
    /**
     * 文字列をオブジェクトへパース時に発生した例外を指定し、さらにメッセージ変数を指定するコンストラクタ。
     * @param fromText パース対象の文字列
     * @param toType パース先のクラスタイプ
     * @param error パースする際に発生した例外
     * @param errorVariables 設定値などの情報。
     */
    public TextParseException(final String fromText, final Class<?> toType, final Throwable error, 
            final Map<String, Object> errorVariables) {
        super(String.format("Fail parse text '%s' to type '%s'.", fromText, toType.getName()), error);
        this.fromText = fromText;
        this.toType = toType;
        this.errorVariables.putAll(errorVariables);
    }
    
    /**
     * 
     * @param fromText パース対象の文字列
     * @param toType パース先のクラスタイプ
     */
    public TextParseException(final String fromText, final Class<?> toType) {
        this(fromText, toType, Collections.emptyMap());
    }
    
    /**
     * 
     * @param fromText パース対象の文字列
     * @param toType パース先のクラスタイプ
     * @param errorVariables 設定値などの情報。
     */
    public TextParseException(final String fromText, final Class<?> toType, final Map<String, Object> errorVariables) {
        super(String.format("Fail parse text '%s' to type '%s'.", fromText, toType.getName()));
        this.fromText = fromText;
        this.toType = toType;
        this.errorVariables.putAll(errorVariables);
    }
    
    /**
     * パースに失敗した文字列を取得する
     * @return パースに失敗した文字列
     */
    public String getFromText() {
        return fromText;
    }
    
    /**
     * パース後のクラスタイプを取得する。
     * @return パース後のクラスタイプ。
     */
    public Class<?> toType() {
        return toType;
    }
    
    /**
     * パースに失敗したときの設定値などを取得する。
     * @return 設定値など。
     */
    public Map<String, Object> getErrorVariables() {
        return errorVariables;
    }
    
}
