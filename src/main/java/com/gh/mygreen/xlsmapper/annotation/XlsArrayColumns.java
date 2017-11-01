package com.gh.mygreen.xlsmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gh.mygreen.xlsmapper.fieldprocessor.CellNotFoundException;

/**
 * アノテーション{@link XlsHorizontalRecords}や{@link XlsVerticalRecords}で指定されたレコード用のクラスにおいて、
 * 隣接する連続したカラムカラムを、配列や{@link java.util.List}に設定します。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XlsArrayColumns {
    
    /**
     * 見出しとなるカラム名を設定します。
     * <p>システム設定により、正規表現による指定や正規化（改行、空白、タブの削除）による比較の対象となります。</p>
     * @return
     */
    String columnName();
    
    /**
     * 連続するセルの個数を指定します。
     * @return 1以上の値を指定します。
     */
    int size();
    
    /** 
     * 配列またはリスト要素の値のクラスを指定します。
     * <p>省略した場合、定義されたたGenericsの情報から取得します。
     */
    Class<?> elementClass() default Object.class;
    
    /**
     * 値のセルが結合しているかどうか考慮するかどうか指定します。
     * <p>trueの場合は、結合されているセルを1つのセルとして考慮します。</p>
     * <p>falseの場合は、結合されていても、分割した値として考えます。
     *  その場合、書き込み時は結合が解除されます。
     *  読み込み時は、分割した値をマッピングしますが、結合されていたセルは同じ値になります。
     * </p>
     * 
     * @return trueの場合、値のセルが結合されていることを考慮する。
     */
    boolean elementMerged() default false;
    
    /**
     * 属性{@link #columnName()}で指定したカラム（セル）が見つからない場合、trueと設定すると無視して処理を続行します。
     * <p>falseを指定し、セルが見つからない場合は、例外{@link CellNotFoundException}がスローされます。</p>
     * @return trueの場合、該当するカラム（セル）が見つからないときは無視して処理を続行します。
     */
    boolean optional() default false;
    
}
