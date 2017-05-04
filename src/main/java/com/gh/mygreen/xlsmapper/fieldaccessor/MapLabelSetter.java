package com.gh.mygreen.xlsmapper.fieldaccessor;

import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;

/**
 * {@link XlsMapColumns}のフィールドに対するラベル情報のsetter
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface MapLabelSetter {
    
    /**
     * フィールドのラベル情報を設定します
     * @param beanObj フィールドが定義してあるクラスのインスタンス
     * @param label ラベル情報
     * @param key マップのキー
     */
    void set(Object beanObj, String label, String key);
    
    
}
