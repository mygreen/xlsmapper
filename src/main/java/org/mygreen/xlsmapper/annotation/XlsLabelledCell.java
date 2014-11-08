package org.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * セルの見出しを指定し、その左右もしくは下側のセルの値をマッピングします。
 * 
 * @author Naoki Takezoe
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsLabelledCell {
    
    /**
     * セルが見つからなかった場合はエラーとなりますが、optional属性にtrueを指定しておくと、無視して処理を続行します。
     */
    boolean optional() default false;
    
    /**
     * type属性の方向に向かって指定したセル数分を検索し、最初に発見した空白以外のセルの値を取得します。
     */
    int range() default 1;
    
    /**
     * 見出しセルから見て値が設定されているセルの位置・方向を指定します。
     */
    LabelledCellType type();
    
    /**
     * 見出しとなるセルの値を指定します。
     * <p>他の属性{@link #labelColumn()}、{@link #labelRow()}、{@link #labelAddress()}でも指定可能です。
     */
    String label() default "";
    
    /**
     * 見出しとなるセルの行番号を指定します。0から始まります。
     * <p>{@link #labelColumn()}属性とセットで指定します。
     */
    int labelRow() default -1;
    
    /**
     * 見出しとなるセルの列番号を指定します。0から始まります。
     * <p>{@link #labelRow()}属性とセットで指定します。
     */
    int labelColumn() default -1;
    
    /**
     * 見出しとなるセルをアドレス形式で指定します。'A1'などのようにシートのアドレスで指定します。
     * <p>
     */
    String labelAddress() default "";
    
    /** 
     * 同じラベルのセルが複数ある場合に領域の見出しを指定します。 
     * headerLabel属性で指定されたセルからlabel属性で指定されたセルを下方向に検索し、最初に見つかったセルをラベルセルとして使用します。
     */
    String headerLabel() default "";
    
    /**
     * ラベルセルから指定したセル数分離れたセルの値をマッピングする際に指定します。
     */
    int skip() default 0;
}
