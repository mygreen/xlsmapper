package com.gh.mygreen.xlsmapper.cellconverter;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayConverter;

/**
 * {@link XlsArrayConverter}で指定する、配列やリストの要素をオブジェクトに変換するためのクラス。
 * 
 * @version 2.0
 * @since 1.1
 * @author T.TSUCHIE
 * @param <T> 変換対象のクラスタイプ
 *
 */
public interface ElementConverter<T> {
    
    /**
     * 文字列を指定したクラス型に変換する。
     * 
     * <p>文字列の値がnullまたは空文字の場合、プリミティブ型の場合ゼロなどのデフォルト値を返す。
     *  <br>オブジェクト型の場合はnullを返す。
     * </p>
     * 
     * @since 1.1
     * @param text 変換対象の文字列
     * @param targetClass 変換後のクラスタイプ
     * @return 変換した値。
     * @throws IllegalArgumentException targetClass is null.
     * @throws ConversionException 変換に失敗した場合にスローされます。
     *                             また、引数targetClassが対応していないクラス型の場合にすろーされます。
     */
    T convertToObject(String text, Class<T> targetClass) throws ConversionException;
    
    /**
     * 任意のオブジェクトを文字列に変換する。
     * 
     * @since 1.1
     * @param value 変換対象のオブジェクト。
     * @return 変換した文字列
     */
    String convertToString(T value);
}
