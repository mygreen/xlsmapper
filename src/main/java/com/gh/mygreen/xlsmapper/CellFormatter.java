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
    public default  void init(boolean cached) {

    }

    /**
     * セルの値を文字列として取得する
     * @param cell 取得対象のセル
     * @return フォーマットした文字列
     */
    public String format(Cell cell);

    /**
     * ロケールを指定してセルの値を文字列として取得する。
     * @since 0.4
     * @param cell 取得対象のセル
     * @param locale ロケr－宇
     * @return フォーマットした文字列
     */
    public String format(Cell cell, Locale locale);

    /**
     * セルの書式を取得する。
     * @since 2.0
     * @param cell
     * @return 書式を持たない場合は、空文字を返す。
     */
    public String getPattern(Cell cell);

    /**
     * セルの書式を取得する。
     * @since 2.0
     * @param cell
     * @return 書式を持たない場合は、空文字を返す。
     */
    public String getPattern(Cell cell, Locale locale);
}
