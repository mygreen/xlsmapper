package com.gh.mygreen.xlsmapper;


/**
 * インスタンスを作成する処理のインタフェース
 * 
 * @param <T> create()メソッドの引数のクラスタイプ。
 * @param <R> create()メソッドの戻り値のタイプ。
 * @author T.TSUCHIE
 * 
 */
public interface BeanFactory<T, R> {
    
    /**
     * 引数TのクラスタイプのインスタンスRを返す。
     * @param type クラスタイプ
     * @return 生成したインスタンス。
     */
    R create(T type);
}
