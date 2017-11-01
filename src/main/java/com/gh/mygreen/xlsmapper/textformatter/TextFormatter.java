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
    
    T parse(String text) throws TextParseException;
    
    String format(T value);
    
}
