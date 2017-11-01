package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import java.awt.Point;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.annotation.XlsRecordOption;
import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;


/**
 * シートのレコードの操作情報。
 * レコードの書き込み後、セルの入力規則やシートの名前の範囲を修正するために利用する。
 * 
 * @version 2.0
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class RecordOperation {
    
    /** レコード操作のアノテーション */
    private final XlsRecordOption annotation;
    
    /** レコードのコピー回数 */
    private int countCopyRecord;
    
    /** レコードの挿入回数 */
    private int countInsertRecord;
    
    /** レコードの削除件数 */
    private int countDeleteRecord;
    
    /** 左上のセルの位置 */
    private Point topLeftPoisitoin;
    
    /** 右下のセルの位置 */
    private Point bottomRightPosition;
    
    public RecordOperation(final XlsRecordOption annotation) {
        this.annotation = annotation;
        this.countCopyRecord = 0;
        this.countInsertRecord = 0;
        this.countDeleteRecord = 0;
        
    }
    
    /**
     * レコードの操作用のアノテーションを取得する。
     * 
     * @since 2.0
     * @return 付与されていない場合は、属性がデフォルト値が設定される。
     */
    public XlsRecordOption getAnnotation() {
        return annotation;
    }
    
    /**
     * レコードのコピー回数を1つ増やす
     */
    public void incrementCopyRecord() {
        this.countCopyRecord++;
    }
    
    /**
     * レコードの挿入回数を1つ増やす
     */
    public void incrementInsertRecord() {
        this.countInsertRecord++;
    }
    
    /**
     * レコードの削除回数を1つ増やす
     */
    public void incrementDeleteRecord() {
        this.countDeleteRecord++;
    }
    
    /**
     * レコードの操作を行ったかどうか。
     * コピー処理、挿入処理、削除処理が該当する。
     * @return
     */
    public boolean isExecuteRecordOperation() {
        return isExecuteOverRecordOperation() || isDeleteRecord();
    }
    
    /**
     * レコードの操作を行っていないかどうか。
     * コピー処理、挿入処理、削除処理が該当する。
     * @return
     */
    public boolean isNotExecuteRecordOperation() {
        return !isExecuteRecordOperation();
    }

    
    /**
     * レコードが足りない時の操作を行ったかどうか。
     * コピー処理、挿入処理が該当する。
     * @return
     */
    public boolean isExecuteOverRecordOperation() {
        return isCopyRecord() || isInsertRecord();
    }
    
    /**
     * レコードが足りない時の操作を行っていないかどうか。
     * @return
     */
    public boolean isNotExecuteOverRecordOperation() {
        return !isExecuteOverRecordOperation();
    }
    
    /**
     * レコードの挿入回数が1以上かどうか。
     */
    public boolean isCopyRecord() {
        return countCopyRecord > 0;
    }
    
    /**
     * レコードの挿入回数が1以上かどうか。
     */
    public boolean isInsertRecord() {
        return countInsertRecord > 0;
    }
    
    /**
     * レコードの削除回数が1以上かどうか。
     */
    public boolean isDeleteRecord() {
        return countDeleteRecord > 0;
    }
    
    public int getCountInsertRecord() {
        return countInsertRecord;
    }
    
    public int getCountDeleteRecord() {
        return countDeleteRecord;
    }
    
    /**
     * セルの位置を元に左上、右下の端の位置を記憶する。
     * @param cell セル情報
     * @throws NullPointerException {@literal cell == null.}
     */
    public void setupCellPositoin(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        setupCellPositoin(cell.getRowIndex(), cell.getColumnIndex());
    }
    
    /**
     * アドレス情報を元に左上、右下の端の位置を記憶する。
     * @param address アドレス情報
     * @throws NullPointerException {@literal address == null.}
     */
    public void setupCellPositoin(final CellPosition address) {
        ArgUtils.notNull(address, "address");
        setupCellPositoin(address.getRow(), address.getColumn());
    }
    
    /**
     * セルの位置を元に左上、右下の端の位置を記憶する。
     * @param rowIndex 行番号（0から始まる）
     * @param columnIndex 列番号（0から始まる）
     */
    public void setupCellPositoin(final int rowIndex, final int columnIndex) {
        
        if(topLeftPoisitoin == null) {
            this.topLeftPoisitoin = new Point(columnIndex, rowIndex);
        }
        
        if(bottomRightPosition == null) {
            this.bottomRightPosition = new Point(columnIndex, rowIndex);
        }
        
        // 左上のセルの位置の設定
        if(topLeftPoisitoin.x > columnIndex) {
            this.topLeftPoisitoin.x = columnIndex;
        }
        
        if(topLeftPoisitoin.y > rowIndex) {
            this.topLeftPoisitoin.y = rowIndex;
        }
        
        // 右下のセルの位置の設定
        if(bottomRightPosition.x < columnIndex) {
            this.bottomRightPosition.x = columnIndex;
        }
        
        if(bottomRightPosition.y < rowIndex) {
            this.bottomRightPosition.y = rowIndex;
        }
    }
    
    /**
     * 左上端の座標を取得する。
     * @return 左上端の座標
     */
    public Point getTopLeftPoisitoin() {
        return topLeftPoisitoin;
    }
    
    /**
     * 右下端の座標を取得する。
     * @return 右下端の座標
     */
    public Point getBottomRightPosition() {
        return bottomRightPosition;
    }
    
}
