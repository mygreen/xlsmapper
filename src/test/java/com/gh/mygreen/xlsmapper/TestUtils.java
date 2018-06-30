package com.gh.mygreen.xlsmapper;

import java.awt.Point;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.util.CellReference;

import com.gh.mygreen.xlsmapper.util.ArgUtils;
import com.gh.mygreen.xlsmapper.util.CellPosition;
import com.gh.mygreen.xlsmapper.util.POIUtils;
import com.gh.mygreen.xlsmapper.validation.FieldError;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * テスト時のユーティリティクラス。
 * <p>staticインポートして利用する。
 * @version 1.0
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
    public static FieldError cellFieldError(final SheetBindingErrors<?> errors, final String address) {
        for(FieldError error : errors.getFieldErrors()) {
            Optional<CellPosition> position = error.getAddressAsOptional();
            if(position.isPresent() && position.get().formatAsString().equalsIgnoreCase(address)) {
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
     * セルのアドレス形式にフォーマットする。
     * @param cellAddress
     * @return
     */
    public static String cellAddress(final CellPosition cellAddress) {
        return cellAddress.formatAsString();
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
    public static Timestamp toTimestamp(String value) {
        return Timestamp.valueOf(value);
    }

    /**
     * Timestampを{@link java.util.Date}に変換する。
     * @param timestamp
     * @return
     */
    public static Date toUtilDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }

    /**
     * Timestampを{@link Calendar}に変換する
     * @since 1.0
     * @param timestamp
     * @return
     */
    public static Calendar toCalendar(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        return cal;
    }

    /**
     * Timestampを{@link java.sql.Date}に変換する。
     * @param timestamp
     * @return
     */
    public static  java.sql.Date toSqlDate(Timestamp timestamp) {
        return new java.sql.Date(timestamp.getTime());
    }

    /**
     * Timestampを{@link java.sql.Time}に変換する。
     * @param timestamp
     * @return
     */
    public static  java.sql.Time toSqlTime(Timestamp timestamp) {
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

    /**
     * ユニコードに変換する
     * @param c 変換対象文字
     * @return
     */
    public static String toUnicode(char c) {
        return toUnicode(String.valueOf(c));
    }

    /**
     * ユニコードに変換する
     * @param original
     * @return
     */
    public static String toUnicode(String original) {
        if(original == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            sb.append(String.format("\\u%04X", Character.codePointAt(original, i)));
        }
        String unicode = sb.toString();
        return unicode;
    }

    /**
     * リストを作成する。
     * @param items
     * @return
     */
    public static <T> List<T> toList(T... items) {
        return Arrays.asList(items);

    }

    /**
     * 配列を作成する。
     * @param items
     * @return
     */
    public static <T> T[] toArray(T... items) {
        return items;

    }

    /**
     * 集合を作成する。
     * @param items
     * @return
     */
    public static <T> Set<T> toSet(T... items) {
        return new LinkedHashSet<T>(toList(items));

    }

    /**
     * 値をトリムする。
     * 引数がnullの場合は、空文字を返す。
     * @param value
     */
    public static String trim(String value) {

        if(value == null) {
            return "";
        } else {
            return value.trim();
        }
    }

    /**
     * 文字列の形式のセルのアドレスを、Point形式に変換する。
     * @param address
     * @return
     */
    public static Point toPointAddress(final String address) {
        ArgUtils.notEmpty(address, "address");

        CellReference ref = new CellReference(address);
        return new Point(ref.getCol(), ref.getRow());
    }

    /**
     * 基準日を基準にして、daysを加算した日時を取得する。
     * @param base
     * @param days
     */
    public static Date getDateByDay(Date base, int days) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(base);

        cal.add(Calendar.DAY_OF_MONTH, days);

        return cal.getTime();

    }

    /**
     * テスト用の結果出力ディレクトリを作成します。
     * <p>target/test_out</p> を作成します。
     *
     * @return
     */
    public static File createOutDir() {

        final File dir = new File("target/test_out");

        if(!dir.exists()) {
            dir.mkdirs();
        }

        return dir;

    }
}
