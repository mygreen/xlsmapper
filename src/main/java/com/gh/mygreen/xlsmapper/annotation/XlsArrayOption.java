package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link XlsArrayCells}や{@link XlsLabelledArrayCells}の書き込み時の配列の操作を指定するためのアノテーションです。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsArrayOption {
    
    /**
     * 書き込み時にデータの配列のサイズ数に対して属性size()の値が小さく、足りない場合の操作を指定します。
     * @return
     */
    OverOperate overCase() default OverOperate.Break;
    
    /**
     * 書き込み時にデータの配列のサイズ数に対して属性size()の値が大きく、余っている場合の操作を指定します。
     * @return
     */
    RemainedOperate remainedCase() default RemainedOperate.None;
    
    /**
     * アノテーション {@link XlsArrayCells}や{@link XlsLabelledArrayCells}で、
     * 書き込み時に、配列やリストのデータサイズ数に対して、属性size()の値が小さい場合の操作を指定します。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static enum OverOperate {
        
        /** レコードの書き込みを中断します。 */
        Break,
        
        /** エラーとして処理します。 */
        Error,
        ;
        
    }
    
    /**
     * アノテーション {@link XlsArrayCells}や{@link XlsLabelledArrayCells}で、
     * 書き込み時に、配列やリストのデータサイズ数に対して、属性size()の値が大きい場合の操作を指定します。
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static enum RemainedOperate {
        
        /** セルの値をクリアします */
        Clear,
        
        /** 何もしません */
        None,
        ;
        
    }
    
}
