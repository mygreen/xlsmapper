package org.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 処理をする際のヒントを定義するためのアノテーションです
 * 出力時において、フィールドの処理順序の結果が変わるときに、処理順番を定義するときに付与します
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsHint {
    
    /**
     * オブジェクト内でのフィールドの処理順序を定義します。
     * <ul>
     *  <li>-1以下の場合は無視します。
     *  <li>同じ値を設定した場合は、第2処理順としてフィールド名の昇順を使用します。
     *  <li>このアノテーションの付与、または属性の値が-1以下の場合、付与しているいフィールドよりも処理順序は後になります。
     * @return
     */
    int order() default -1;
}
