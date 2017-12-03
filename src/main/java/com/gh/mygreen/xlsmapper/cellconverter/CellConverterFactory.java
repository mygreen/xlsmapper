package com.gh.mygreen.xlsmapper.cellconverter;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link CellConverter}のインスタンスを作成するためのインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public interface CellConverterFactory<T> {
    
    /**
     * フィールドに対するセル変換クラスを作成する。
     * @param accessor フィールド情報
     * @param config システム設定
     * @return セルの変換クラス。
     */
    CellConverter<T> create(FieldAccessor accessor, Configuration config);
    
}
