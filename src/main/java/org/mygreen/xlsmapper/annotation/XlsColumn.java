package org.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で指定されたクラスのプロパティをカラム名にマッピングします。
 * 
 * @author Naoki Takezoe
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsColumn {
    
    /**
     * 見出しとなるカラム名を設定します。
     * @return
     */
    String columnName();
    
    /**
     * 同じ値がグループごとに結合されている場合は、merged=trueに設定します。
     * trueにした場合、前の列の値が引き継がれて設定されます。
     * @return
     */
    boolean merged() default false;
    
    /**
     * 見出し行が結合され、1つの見出しに対して複数の列が存在する場合はheaderMergedプロパティを使用します。
     * headerMergedの値には列見出しから何セル分離れているかを指定します。 
     * @return
     */
    int headerMerged() default 0;
    
    /**
     * 該当するカラム（セル）が見つからない場合、trueとすると、無視して処理を続行します。
     * @return
     */
    boolean optional() default false;
}
