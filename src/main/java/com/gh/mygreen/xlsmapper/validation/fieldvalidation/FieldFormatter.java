package com.gh.mygreen.xlsmapper.validation.fieldvalidation;


/**
 * {@link FieldValidator}中で利用する値のフォーマッタのインタフェース。
 * <p>入力値検証した結果のメッセージ中に表示するために利用する。
 * 
 * @since 1.0
 * @author T.TSUCHIE
 * @param <T> フォーマット対象のフィールドのタイプ。
 *
 */
@FunctionalInterface
public interface FieldFormatter<T> {
    
    /**
     * 値をフォーマットする。
     * @param value フォーマット対象の値。nullが渡ってくることもある。
     * @return フォーマットした文字列。
     */
    String format(T value);
    
}
