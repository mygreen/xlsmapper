package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gh.mygreen.xlsmapper.validation.CellFieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * テスト時のユーティリティクラス。
 * <p>staticインポートして利用する。
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class TestUtils {
    
    /**
     * セルのアドレスを指定してエラーを取得する。
     * @param errors
     * @param address
     * @return 見つからない場合はnullを返す。
     */
    public static CellFieldError cellFieldError(final SheetBindingErrors errors, final String address) {
        for(CellFieldError error : errors.getCellFieldErrors()) {
            if(error.getFormattedCellAddress().equalsIgnoreCase(address)) {
                return error;
            }
        }
        
        return null;
    }
    
    /**
     * セルのアドレス形式にフォーマットする。
     * @param cellAddress
     * @return
     */
    public static String cellAddress(final Point cellAddress) {
        return POIUtils.formatCellAddress(cellAddress);
    }
    
    /**
     * 任意の時間でフォーマットする。
     * @param pattern
     * @param date
     * @return
     */
    public static String format(final String pattern, final Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }
    
    /**
     * 文字列をタイムスタンプに変換する。(yyyy-MM-dd HH:mm:ss.SSS)の形式
     * @param value
     * @return
     */
    public static  Timestamp timestamp(String value) {
        return Timestamp.valueOf(value);
    }
    
    /**
     * Timestampを{@link java.util.Date}に変換する。
     * @param timestamp
     * @return
     */
    public static  Date utilDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
    
    /**
     * Timestampを{@link java.sql.Date}に変換する。
     * @param timestamp
     * @return
     */
    public static  java.sql.Date sqlDate(Timestamp timestamp) {
        return new java.sql.Date(timestamp.getTime());
    }
    
    /**
     * Timestampを{@link java.sql.Time}に変換する。
     * @param timestamp
     * @return
     */
    public static  java.sql.Time sqlTime(Timestamp timestamp) {
        return new java.sql.Time(timestamp.getTime());
    }
    
    /**
     * 文字列の比較をする。
     * 引数の値がnullでも問題ない。
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsStr(final String str1, final String str2) {
        
        if(str1 == null && str2 == null) {
            return true;
        } else if(str1 == null || str2 == null) {
            return false;
        } else {
            return str1.equals(str2);
        }
        
    }
}
