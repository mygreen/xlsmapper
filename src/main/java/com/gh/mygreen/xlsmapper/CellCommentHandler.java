package com.gh.mygreen.xlsmapper;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.annotation.XlsCommentOption;

/**
 * セルのコメントを操作する
 * <p>シートを書き込む時に、コメントの位置やサイズを任意で指定したい時に独自の実装を指定します。</p>
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public interface CellCommentHandler {
    
    /**
     * シート読み込み時にセルのコメントを取得する。
     * @param cell コメント取得対象のセル
     * @param commentOption コメントのオプション
     * @return セルのコメント。コメントがない場合は、空を返す。
     */
    Optional<String> handleLoad(Cell cell, Optional<XlsCommentOption> commentOption);
    
    /**
     * シート書き込み時にセルのコメントを設定する。
     * @param cell 書込み対象のセル
     * @param text コメント。コメントがない場合は、空が設定されます。
     * @param commentOption コメントのオプション
     */
    void handleSave(Cell cell, Optional<String> text, Optional<XlsCommentOption> commentOption);
    
}
