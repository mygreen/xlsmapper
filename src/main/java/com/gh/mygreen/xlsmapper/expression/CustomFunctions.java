package com.gh.mygreen.xlsmapper.expression;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.poi.ss.util.CellReference;

/**
 * EL式中で利用可能なEL関数。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CustomFunctions {
    
    /**
     * 列番号を英字名に変換します。
     * @param column 列番号(1から始まる)
     * @return 列の英字名
     */
    public static String colToAlpha(final int column) {
        return CellReference.convertNumToColString(column-1);
    }
    
    /**
     * 文字列がnullの場合に空文字に変換する。
     * <pre class="highlight"><code class="java">
     *     CustomFunctions.defaultString(null) = ""
     *     CustomFunctions.defaultString("") = ""
     *     CustomFunctions.defaultString("abc") = "abc"
     * </code></pre>
     * 
     * @param text 判定対象の文字列
     * @return 非nullの場合は、引数の値をそのまま返す。
     */
    public static String defaultString(final String text) {
        if(text == null) {
            return "";
        }
        
        return text;
    }
    
    /**
     * int型の配列の値を結合する。
     * @param array 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    public static String join(final int[] array, final String delimiter) {
        
        if(array == null || array.length == 0) {
            return "";
        }
        
        String value = Arrays.stream(array)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(defaultString(delimiter)));
        
        return value;
    }
    
    /**
     * 配列の値を結合する。
     * @param array 結合対象の配列
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象の配列がnulの場合、空文字を返す。
     */
    public static String join(final Object[] array, final String delimiter) {
        
        if(array == null || array.length == 0) {
            return "";
        }
        
        String value = Arrays.stream(array)
                .map(v -> v.toString())
                .collect(Collectors.joining(defaultString(delimiter)));
        
        return value;
    }
    
    /**
     * コレクションの値を結合する。
     * @param collection 結合対象のコレクション
     * @param delimiter 区切り文字
     * @return 結合した文字列を返す。結合の対象のコレクションがnulの場合、空文字を返す。
     */
    public static String join(final Collection<?> collection, final String delimiter) {
        
        if(collection == null || collection.isEmpty()) {
            return "";
        }
        
        String value = collection.stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(defaultString(delimiter)));
        
        return value;
    }
    
}
