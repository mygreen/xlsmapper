package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 連続する隣接するセルを配列またはリストにマッピングします。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@XlsFieldProcessor(value={})
public @interface XlsArrayCells {
    
    /**
     * 連続するセルの個数を指定します。
     * 
     * @return 1以上の値を指定します。
     */
    int size();
    
    /**
     * 値のセルが結合しているかどうか考慮するかどうか指定します。
     * 
     * この値により、属性{@link #size()}の指定方法が変わります。
     * <p>trueの場合は、結合されているセルを1つのセルとしてマッピングします。</p>
     * <p>falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
     *  <br>ただし、書き込む際には、結合を解除されます。
     * </p>
     * 
     * @return trueの場合、値のセルが結合されていることを考慮します。
     */
    boolean elementMerged() default true;
    
    /**
     * 連続する隣接するセルの方向を指定します。
     * 
     * @return セルの方向を指定します。
     */
    ArrayDirection direction() default ArrayDirection.Horizon;
    
    /** 
     * 配列またはリスト要素の値のクラスを指定します。
     * <p>省略した場合、定義されたGenericsタイプから取得します。</p>
     */
    Class<?> elementClass() default Object.class;
    
    /**
     * セルの行番号を指定します。
     * {@link #column()}属性とセットで指定します。
     * 
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     */
    int row() default -1;
    
    /**
     * セルの列番号を指定します。
     * {@link #row()}属性とセットで指定します。
     * 
     * @return 値は0から始まります。-1以下の負の値は無視されます。
     * 
     */
    int column() default -1;
    
    /**
     * セルのアドレスを指定します。
     * <p>{@link #row()}、{@link #column()}属性のどちらか一方を指定します。</p>
     * 
     * @return 'A1'の形式で指定します。空文字は無視されます。
     */
    String address() default "";
    
}
