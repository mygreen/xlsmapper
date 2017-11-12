package com.gh.mygreen.xlsmapper.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 複数のシートを読み込む場合に、{@link SheetBindingErrors}を格納するクラス。
 * 
 * @version 2.0
 * @param <P>
 * @author T.TSUCHIE
 *
 */
public class MultipleSheetBindingErrors<P> {
    
    private final List<SheetBindingErrors<P>> list = new ArrayList<>();
    
    /**
     * マッピング結果を追加する。
     * @param bindingErrors 1シート分のマッピング結果
     */
    public void addBindingErrors(final SheetBindingErrors<P> bindingErrors) {
        list.add(bindingErrors);
    }
    
    /**
     * 全てのマッピング情報を取得する。
     * @return
     */
    public List<SheetBindingErrors<P>> getAll() {
        return list;
    }
    
    /**
     * シート番号を指定して、マッピング情報を取得する。
     * @param sheetIndex シート番号(0から始まる)
     * @return 存在しない場合は空を返す。
     */
    public Optional<SheetBindingErrors<P>> getBySheetIndex(final int sheetIndex) {
        return list.stream()
                .filter(b -> b.getSheetIndex() == sheetIndex)
                .findFirst();
    }
    
    /**
     * シート名を指定して、マッピング情報を取得する。
     * @param sheetName シート名
     * @return 存在しない場合は空を返す。
     */
    public Optional<SheetBindingErrors<P>> getBySheetName(final String sheetName) {
        return list.stream()
                .filter(b -> Utils.equals(b.getSheetName(), sheetName))
                .findFirst();
    }
    
}
