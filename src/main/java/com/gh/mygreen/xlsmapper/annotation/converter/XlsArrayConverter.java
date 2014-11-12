package com.gh.mygreen.xlsmapper.annotation.converter;



/**
 * Collection(List, Set)または配列のタイプに対してCell<=>Java Objectに相互変換する定義を行うアノテーション。
 * 
 * @author T.TSUCHIE
 *
 */
public @interface XlsArrayConverter {
    
    /**
     * 区切り文字
     * @return
     */
    String separator() default ",";
    
    /**
     * 空またはnullの項目は無視するか指定します。
     * @return
     */
    boolean ignoreEmptyItem() default false;
    
    /** 
     * 配列やリストの要素のクラス型を指定します。
     * <p>プリミティブ型とそのラッパークラスのみ指定できます。
     * <p>省略した場合、Genericsから自動的に判断してます。
     */
    Class<?> itemClass() default Object.class;
    
}
