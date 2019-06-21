package com.gh.mygreen.xlsmapper;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.annotation.XlsArrayColumns;
import com.gh.mygreen.xlsmapper.annotation.XlsCommentOption;
import com.gh.mygreen.xlsmapper.annotation.XlsMapColumns;
import com.gh.mygreen.xlsmapper.fieldaccessor.ArrayCommentGetter;
import com.gh.mygreen.xlsmapper.fieldaccessor.ArrayCommentSetter;
import com.gh.mygreen.xlsmapper.fieldaccessor.CommentGetter;
import com.gh.mygreen.xlsmapper.fieldaccessor.CommentSetter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldaccessor.MapCommentGetter;
import com.gh.mygreen.xlsmapper.fieldaccessor.MapCommentSetter;
import com.gh.mygreen.xlsmapper.util.ArgUtils;

/**
 * セルのコメントを操作する。
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class CellCommentOperator {
    
    /**
     * セルのコメントを実際に処理する実装。
     */
    private CellCommentHandler commentHandler = new DefaultCellCommentHandler();
    
    /**
     * セルのコメントを抽出し、フィールドに設定する。
     * @param commentSetter 抽出したコメントの設定先
     * @param cell 抽出するセル
     * @param beanObj コメントを設定するBeanオブジェクト
     * @param field 処理対象のフィールド
     * @param config システム設定
     * @throws IllegalArgumentException cell == null.
     */
    public void loadCellComment(final CommentSetter commentSetter, final Cell cell, final Object beanObj, 
            final FieldAccessor field, final Configuration config) {
        
        ArgUtils.notNull(cell, "cell");
        
        Optional<XlsCommentOption> commentOption = field.getAnnotation(XlsCommentOption.class);
        Optional<String> commentText = resolveCommentHandler(commentOption, config).handleLoad(cell, commentOption);
        
        // コメントが設定されているときのみ設定する
        commentText.ifPresent(comment -> commentSetter.set(beanObj, comment));
        
    }
    
    /**
     * セルにコメントを書き込む
     * @param commentGetter 書込み対象のコメントの取得先
     * @param cell コメントを書き込むセル
     * @param beanObj コメントを設定するBeanオブジェクト
     * @param field 処理対象のフィールド
     * @param config システム設定
     */
    public void saveCellComment(final CommentGetter commentGetter, final Cell cell, final Object beanObj,
            final FieldAccessor field, final Configuration config) {
        
        ArgUtils.notNull(cell, "cell");

        Optional<XlsCommentOption> commentOption = field.getAnnotation(XlsCommentOption.class);
        Optional<String> commentText = commentGetter.get(beanObj);
        
        resolveCommentHandler(commentOption, config).handleSave(cell, commentText, commentOption);
        
    }
    
    /**
     * セルのコメントを抽出し、{@link XlsMapColumns} フィールドに設定する。
     * @param commentSetter 抽出したコメントの設定先
     * @param cell 抽出するセル
     * @param beanObj コメントを設定するBeanオブジェクト
     * @param key マップのキー
     * @param field 処理対象のフィールド
     * @param config システム設定
     * @throws IllegalArgumentException cell == null.
     */
    public void loadMapCellComment(final MapCommentSetter commentSetter, final Cell cell, final Object beanObj, 
            final String key, final FieldAccessor field, final Configuration config) {
        
        ArgUtils.notNull(cell, "cell");
        
        Optional<XlsCommentOption> commentOption = field.getAnnotation(XlsCommentOption.class);
        Optional<String> commentText = resolveCommentHandler(commentOption, config).handleLoad(cell, commentOption);
        
        // コメントが設定されているときのみ設定する
        commentText.ifPresent(comment -> commentSetter.set(beanObj, comment, key));
        
    }
    
    /**
     * {@link XlsMapColumns} フィールドのセルにコメントを書き込む
     * @param commentGetter 書込み対象のコメントの取得先
     * @param cell コメントを書き込むセル
     * @param beanObj コメントを設定するBeanオブジェクト
     * @param key マップのキー
     * @param field 処理対象のフィールド
     * @param config システム設定
     */
    public void saveMapCellComment(final MapCommentGetter commentGetter, final Cell cell, final Object beanObj,
            final String key, final FieldAccessor field, final Configuration config) {
        
        ArgUtils.notNull(cell, "cell");

        Optional<XlsCommentOption> commentOption = field.getAnnotation(XlsCommentOption.class);
        Optional<String> commentText = commentGetter.get(beanObj, key);
        
        resolveCommentHandler(commentOption, config).handleSave(cell, commentText, commentOption);
        
    }
    
    /**
     * セルのコメントを抽出し、{@link XlsArrayColumns} フィールドに設定する。
     * @param commentSetter 抽出したコメントの設定先
     * @param cell 抽出するセル
     * @param beanObj コメントを設定するBeanオブジェクト
     * @param index 配列のインデックス
     * @param field 処理対象のフィールド
     * @param config システム設定
     * @throws IllegalArgumentException cell == null.
     */
    public void loadArrayCellComment(final ArrayCommentSetter commentSetter, final Cell cell, final Object beanObj, 
            final int index, final FieldAccessor field, final Configuration config) {
        
        ArgUtils.notNull(cell, "cell");
        
        Optional<XlsCommentOption> commentOption = field.getAnnotation(XlsCommentOption.class);
        Optional<String> commentText = resolveCommentHandler(commentOption, config).handleLoad(cell, commentOption);
        
        // コメントが設定されているときのみ設定する
        commentText.ifPresent(comment -> commentSetter.set(beanObj, comment, index));
        
    }
    
    /**
     * {@link XlsArrayColumns} フィールドのセルにコメントを書き込む
     * @param commentGetter 書込み対象のコメントの取得先
     * @param cell コメントを書き込むセル
     * @param beanObj コメントを設定するBeanオブジェクト
     * @param index 配列のインデックス
     * @param field 処理対象のフィールド
     * @param config システム設定
     */
    public void saveArrayCellComment(final ArrayCommentGetter commentGetter, final Cell cell, final Object beanObj,
            final int index, final FieldAccessor field, final Configuration config) {
        
        ArgUtils.notNull(cell, "cell");

        Optional<XlsCommentOption> commentOption = field.getAnnotation(XlsCommentOption.class);
        Optional<String> commentText = commentGetter.get(beanObj, index);
        
        resolveCommentHandler(commentOption, config).handleSave(cell, commentText, commentOption);
        
    }
    
    /**
     * {@link CellCommentHandler}の実装を取得する。
     * @param commentOption セルコメントのオプション
     * @param config システム設定
     * @return {@link CellCommentHandler}の実装
     */
    private CellCommentHandler resolveCommentHandler(Optional<XlsCommentOption> commentOption, Configuration config) {
        
        if(!commentOption.isPresent()) {
            return commentHandler;
        }
        
        Class<? extends CellCommentHandler>[] handlerClass = commentOption.get().handler();
        if(handlerClass.length == 0) {
            return commentHandler;
        }
        
        return (CellCommentHandler) config.getBeanFactory().create(handlerClass[0]);
        
    }
    
    /**
     * セルのコメントを実際に処理する実装を取得する。
     * 
     * @return セルのコメントを実際に処理する実装。
     */
    public CellCommentHandler getCommentHandler() {
        return commentHandler;
    }
    
    /**
     * セルのコメントを実際に処理する実装を設定する。
     * 
     * @param commentHandler セルのコメントを実際に処理する実装。
     */
    public void setCommentHandler(CellCommentHandler commentHandler) {
        this.commentHandler = commentHandler;
    }
}
