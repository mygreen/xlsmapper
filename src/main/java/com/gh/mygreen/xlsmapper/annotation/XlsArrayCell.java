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
public @interface XlsArrayCell {
    
    /**
     * 連続するセルの個数を指定します。
     * @return 1以上の値を指定します。
     */
    int size();
    
    /**
     * 値のセルが結合しているかどうか考慮するかどうか指定します。
     * この値により、属性{@link #size()}の指定方法が変わります。
     * <p>trueの場合は、結合されているセルを1つのセルとしてマッピングします。</p>
     * <p>falseの場合は、結合されていても解除した状態と同じマッピング結果となります。
     *  <br>ただし、書き込む際には、結合を解除して書き込まれます。
     * </p>
     * 
     * @return trueの場合、値のセルが結合されていることを考慮する。
     */
    boolean itemMerged() default true;
    
    /**
     * 連続する隣接するセルの方向を指定します。
     * @return セルの方向
     */
    ArrayDirection direction() default ArrayDirection.Horizon;
    
    /** 
     * 配列またはリスト要素の値のクラスを指定します。
     * <p>省略した場合、定義されたたGenericsの情報から取得します。
     */
    Class<?> itemClass() default Object.class;
    
    /**
     * セルの行番号を指定します。0から始まります。
     * {@link #column()}属性とセットで指定します。
     */
    int row() default -1;
    
    /**
     * セルの列番号を指定します。0から始まります。
     * {@link #row()}属性とセットで指定します。
     */
    int column() default -1;
    
    /**
     * セルのアドレスを指定します。'A1'などのようにシートのアドレスで指定します。
     * <p>{@link #row()}、{@link #column()}属性のどちらか一方を指定します。
     * @return
     */
    String address() default "";
    
}
