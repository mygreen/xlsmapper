package org.mygreen.xlsmapper;

import org.apache.poi.ss.usermodel.Workbook;
import org.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * コールバック用のインタフェース。
 * <p>シートの読み込み時、書き込み時に呼ばれるコールバック処理を定義するために使用する。
 * 
 * @param <P> Javaのオブジェクトタイプ
 * @author T.TSUCHIE
 *
 */
public interface SingleCallback<P> {
    
    void call(Workbook workbook, P beansObj, XlsMapperConfig config, SheetBindingErrors errors);
    
}
