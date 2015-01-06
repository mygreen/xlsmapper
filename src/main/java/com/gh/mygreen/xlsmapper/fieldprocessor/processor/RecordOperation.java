package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import java.awt.Point;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.ArgUtils;


/**
 * シートのレコードの操作情報
 * レコードの書き込み後、セルの入力規則やシートの名前の範囲を修正するために利用する。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class RecordOperation {
    
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
    
    public RecordOperation() {
        this.countCopyRecord = 0;
        this.countInsertRecord = 0;
        this.countDeleteRecord = 0;
        
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
     * @param cell
     */
    public void setupCellPositoin(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        setupCellPositoin(cell.getRowIndex(), cell.getColumnIndex());
    }
    
    /**
     * セルの位置を元に左上、右下の端の位置を記憶する。
     * @param rowIndex
     * @param columnIndex
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
    
    public Point getTopLeftPoisitoin() {
        return topLeftPoisitoin;
    }
    
    public Point getBottomRightPosition() {
        return bottomRightPosition;
    }
    
}
