package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.CellCommentHandler;

/**
 * 書き込み時のコメントの位置、書式などのを指定するためのアノテーションです。
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
     * コメントを表示状態にするかどうか。
     * @return trueのとき表示する。
     */
    boolean visible() default false;
    
    /**
     * 書込み対象のコメントが空のとき、すでに設定されているコメントを削除するかどうか。
     * @return trueのとき削除する。
     */
    boolean removeIfEmpty() default false;
    
    /**
     * コメントの縦サイズ。
     * 行の個数分で表現する。
     * @return 1以上のとき有効とする。
     */
    int verticalSize() default 0;
    
    /**
     * コメントの横サイズ。
     * 列の個数分で表現する。
     * @return 1以上の時有効とする。
     */
    int horizontalSize() default 0;
    
    /**
     * 書き込み時にセルのコメント設定を独自に処理したいときに指定します。
     * @return {@link CellCommentHandler}の実装クラス。
     */
    Class<? extends CellCommentHandler>[] handler() default {};
    
}
