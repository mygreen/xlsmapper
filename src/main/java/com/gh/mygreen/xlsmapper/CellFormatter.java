package com.gh.mygreen.xlsmapper;

import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;


/**
 * セルフォーマッターのインタフェース。
 * <p>標準の実装として{@link DefaultCellFormatter}がある。
 * 
 * @version 2.0
 * @since 0.1
 * @author T.TSUCHIE
 *
 */
public interface CellFormatter {
    
    /**
     * キャッシュの情報の初期化と設定を行います。
     * @param cached trueのとき、キャッシュを有効にします。
     */
    default public void init(boolean cached) {
        
    }
    
    /**
     * セルの値を文字列として取得する
     * @param cell 取得対象の
     * @return フォーマットした文字列
     */
    public String format(final Cell cell);
    
    /**
     * ロケールを指定してセルの値を文字列として取得する。
     * @since 0.4
     * @param cell
     * @param locale
     * @return
     */
    public String format(final Cell cell, final Locale locale);
    
}
