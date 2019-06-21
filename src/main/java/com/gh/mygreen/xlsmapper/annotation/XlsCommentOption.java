package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.CellCommentHandler;

/**
 * {@link XlsComment}/{@link XlsLabelledComment}/値セルのフィールド（{@code Map<String, String> comments}) の書き込み時のセルのコメントのサイズなどの制御を指定するためのアノテーションです。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsCommentOption {
    
    /**
     * コメントを表示状態にするかどうか指定します。
     * <p>既に設定されているコメントの設定よりも、アノテーションの設定が優先されます。</p>
     * @return trueのとき表示します。
     */
    boolean visible() default false;
    
    /**
     * 書込み対象のコメントが空のとき、既に設定されているコメントを削除するかどうか指定します。
     * @return trueのとき削除します。
     */
    boolean removeIfEmpty() default false;
    
    /**
     * コメントの縦サイズを指定します。
     * <p>単位は、行数で表現します。</p>
     * <p>既にコメントが設定されている場合は無視されます。</p>
     * @return 1以上のとき有効となります。
     */
    int verticalSize() default 0;
    
    /**
     * コメントの横サイズを指定します。
     * <p>単位は、列数で表現します。</p>
     * <p>既にコメントが設定されている場合は無視されます。</p>
     * @return 1以上の時有効とする。
     */
    int horizontalSize() default 0;
    
    /**
     * 書き込み時にセルのコメント設定を独自に処理したいときに指定します。
     * @return {@link CellCommentHandler}の実装クラス。
     */
    Class<? extends CellCommentHandler>[] handler() default {};
    
}
