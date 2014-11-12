package com.gh.mygreen.xlsmapper;


/**
 * インスタンスを作成する処理のコールバックインタフェース
 * 
 * @param <T> create()メソッドの引数のクラスタイプ。
 * @param <R> create()メソッドの戻り値のタイプ。
 * @author T.TSUCHIE
 * 
 */
public interface FactoryCallback<T, R> {
    
    /**
     * 引数TのクラスタイプのインスタンスRを返す。
     * @param type
     * @return
     */
    R create(T type);
}
