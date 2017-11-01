package com.gh.mygreen.xlsmapper.textformatter;


/**
 * 文字列をオブジェクトを相互変換すためのインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 * @param <T> 変換対象のクラスタイプ
 */
public interface TextFormatter<T> {
    
    /**
     * 文字列をパースし、オブジェクトに変換する。
     * @param text パース対象の文字列
     * @return 変換後のオブジェクト
     * @throws TextParseException パースする際に発生した例外
     */
    T parse(String text) throws TextParseException;
    
    /**
     * オブジェクトをフォーマットし、文字列に変換する。
     * @param value フォーマット対象のオブジェクト
     * @return 変換後の文字列
     */
    String format(T value);
    
}
