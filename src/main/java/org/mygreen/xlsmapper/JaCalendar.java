package org.mygreen.xlsmapper;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 和暦対応カレンダークラス.
 *
 * @author T.TSUCHIE
 *
 */
public class JaCalendar {
    
    /**
     * 平成 H
     */
    static public final String HEISEI = "H";

    /**
     * 昭和 S
     */
    static public final String SHOWA = "S";

    /**
     * 大正 T
     */
    static public final String TAISHO = "T";

    /**
     * 明治 M
     */
    static public final String MEIJI = "M";

    /**
     * 未サポート U
     */
    static public final String UNSUPPORTED = "U";

    /**
     * 明治の開始日 01/09/08
     */
    static final int[] FM = { 1, 9, 8 };

    /**
     * 明治の最終日 45/07/29
     */
    static final int[] LM = { 45, 7, 29 };

    /**
     * 大正の開始日 01/07/30
     */
    static final int[] FT = { 1, 7, 30 };

    /**
     * 大正の最終日 15/12/24
     */
    static final int[] LT = { 15, 12, 24 };

    /**
     * 昭和の開始日 01/12/25
     */
    static final int[] FS = { 1, 12, 25 };

    /**
     * 昭和の最終日 64/01/07
     */
    static final int[] LS = { 64, 1, 7 };

    /**
     * 平成の開始日 01/01/08
     */
    static final int[] FH = { 1, 1, 8 };

    /**
     * 西暦での旧暦の最終日 1872/12/31  GregorianCalendarの仕様で，日本でいう月数に１を引いた値を月に入れる必要がある。
     */
    static public final GregorianCalendar LAST_DAY_OF_LUNAR = new GregorianCalendar(1872, 11, 31);

    /**
     * 西暦での慶応の最終日 1868/09/07
     */
    static public final GregorianCalendar LAST_DAY_OF_KEIO = new GregorianCalendar(1868, 8, 7);
    
    /**
     * 西暦での明治の開始日 1868/09/08
     */
    static public final GregorianCalendar FIRST_DAY_OF_MEIJI = new GregorianCalendar(1868, 8, 8);

    /**
     * 西暦での明治の最終日 1912/07/29
     */
    static public final GregorianCalendar LAST_DAY_OF_MEIJI = new GregorianCalendar(1912, 6, 29);

    /**
     * 西暦での大正の開始日 1912/07/30
     */
    static public final GregorianCalendar FIRST_DAY_OF_TAISHO = new GregorianCalendar(1912, 6, 30);

    /**
     * 西暦での大正の最終日 1926/12/24
     */
    static public final GregorianCalendar LAST_DAY_OF_TAISHO = new GregorianCalendar(1926, 11, 24);

    /**
     * 西暦での昭和の開始日 1926/12/25
     */
    static public final GregorianCalendar FIRST_DAY_OF_SHOWA = new GregorianCalendar(1926, 11, 25);

    /**
     * 西暦での昭和の最終日 1989/01/07
     */
    static public final GregorianCalendar LAST_DAY_OF_SHOWA = new GregorianCalendar(1989, 0, 7);

    /**
     * 西暦での平成の開始日 1989/01/08
     */
    static public final GregorianCalendar FIRST_DAY_OF_HEISEI = new GregorianCalendar(1989, 0, 8);

    /**
     * 明治の西暦との差分 1967
     */
    static public final int YEAR_MEIJI = 1867;

    /**
     * 大正の西暦との差分 1911
     */
    static public final int YEAR_TAISHO = 1911;

    /**
     * 昭和の西暦との差分 1925
     */
    static public final int YEAR_SHOWA = 1925;

    /**
     * 平成の西暦との差分 1988
     */
    static public final int YEAR_HEISEI = 1988;

    /**
     * 内部状態 GregorianCalendar
     */
    private GregorianCalendar cal;

    /**
    * 現時刻の和暦を生成
    */
    public JaCalendar() {
        cal = new GregorianCalendar();
    }
    
    /**
     * Dateから生成
     * 
     * @param date　
     */
    public JaCalendar(Date date) {
        cal = new GregorianCalendar();
        cal.setGregorianChange(date);
        cal.setTime(date);
    }
    

    /**
     * 西暦インスタンスから生成
     *
     * @param gcal
     */
    public JaCalendar(GregorianCalendar gcal) {
        cal = gcal;
    }

    /**
     * 年、月、日から生成 GregorianCalendarは，１月が0で表されるため，引数の月数から１を引いて生成
     *
     * @param year 年
     * @param month 月
     * @param day 日
     */
    public JaCalendar(int year, int month, int day) {
        cal = new GregorianCalendar(year, month - 1, day);
    }

    /**
     * 元号、年、月、日から生成
     *
     * @param era 元号
     * @param year 年
     * @param month 月
     * @param day 日
     */
    public JaCalendar(String era, int year, int month, int day) {
        if (JaCalendar.check(era, year, month, day)) {
            if (era.equals(HEISEI)) {
                cal = new GregorianCalendar(year + YEAR_HEISEI, month - 1, day);
            }

            if (era.equals(SHOWA)) {
                cal = new GregorianCalendar(year + YEAR_SHOWA, month - 1, day);
            }

            if (era.equals(TAISHO)) {
                cal = new GregorianCalendar(year + YEAR_TAISHO, month - 1, day);
            }

            if (era.equals(MEIJI)) {
                cal = new GregorianCalendar(year + YEAR_MEIJI, month - 1, day);
            }
        } else {
            cal = null;
        }
    }

    /**
     * GYYMMDD形式のStringから生成
     *
     * @param dateString 日付文字列
     */
    public JaCalendar(String dateString) {
        final int strLen = dateString.length();

        if (strLen == 7) {
            String era = dateString.substring(0, 1);
            final int year = Integer.parseInt(dateString.substring(1, 3));
            final int month = Integer.parseInt(dateString.substring(3, 5));
            final int day = Integer.parseInt(dateString.substring(5, 7));

            if (check(era, year, month, day)) {
                if (era.equals(HEISEI)) {
                    cal = new GregorianCalendar(year + YEAR_HEISEI, month - 1, day);
                }

                if (era.equals(SHOWA)) {
                    cal = new GregorianCalendar(year + YEAR_SHOWA, month - 1, day);
                }

                if (era.equals(TAISHO)) {
                    cal = new GregorianCalendar(year + YEAR_TAISHO, month - 1, day);
                }

                if (era.equals(MEIJI)) {
                    cal = new GregorianCalendar(year + YEAR_MEIJI, month - 1, day);
                }
            } else {
                cal = null;
            }
        } else if (strLen == 9) {
            String era = dateString.substring(0, 1);
            final int year = Integer.parseInt(dateString.substring(1, 3));
            final int month = Integer.parseInt(dateString.substring(4, 6));
            final int day = Integer.parseInt(dateString.substring(7, 9));

            if (check(era, year, month, day)) {
                if (era.equals(HEISEI)) {
                    cal = new GregorianCalendar(year + YEAR_HEISEI, month - 1, day);
                }

                if (era.equals(SHOWA)) {
                    cal = new GregorianCalendar(year + YEAR_SHOWA, month - 1, day);
                }

                if (era.equals(TAISHO)) {
                    cal = new GregorianCalendar(year + YEAR_TAISHO, month - 1, day);
                }

                if (era.equals(MEIJI)) {
                    cal = new GregorianCalendar(year + YEAR_MEIJI, month - 1, day);
                }
            }
        } else if (strLen == 8) {
            final int year = Integer.parseInt(dateString.substring(0, 4));
            final int month = Integer.parseInt(dateString.substring(4, 6));
            final int day = Integer.parseInt(dateString.substring(6, 8));

            cal = new GregorianCalendar(year, month - 1, day);
        } else if (strLen == 10) {
            final int year = Integer.parseInt(dateString.substring(0, 4));
            final int month = Integer.parseInt(dateString.substring(5, 7));
            final int day = Integer.parseInt(dateString.substring(8, 10));

            cal = new GregorianCalendar(year, month - 1, day);
        } else {
            cal = null;
        }
    }

    /**
     * 和暦チェック
     *
     * @param era 元号
     * @param year 年
     * @param month 月
     * @param day 日
     * @return boolean
     */
    public static boolean check(String era, int year, int month, int day) {
        char[] cr = era.toCharArray();
        final char c = cr[0];

        if ((c != 'M') && (c != 'T') && (c != 'S') && (c != 'H')) {
            return false;
        }

        int[] cd = new int[3];
        cd[0] = year;
        cd[1] = month;
        cd[2] = day;

        if (year < 0) {
            return false;
        }

        if (era.equals(MEIJI)) {
            if ((JaCalendar.compareTo(cd, FM) == -1) || (JaCalendar.compareTo(cd, LM) == 1)) {
                return false;
            }
        } else if (era.equals(TAISHO)) {
            if ((JaCalendar.compareTo(cd, FT) == -1) || (JaCalendar.compareTo(cd, LT) == 1)) {
                return false;
            }
        } else if (era.equals(SHOWA)) {
            if ((JaCalendar.compareTo(cd, FS) == -1) || (JaCalendar.compareTo(cd, LS) == 1)) {
                return false;
            }
        } else if (era.equals(HEISEI)) {
            if (JaCalendar.compareTo(cd, FH) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 元号を取得
     *
     * @return String
     */
    public String getEra() {
        if (cal == null) {
            return "";
        }

        if (cal.after(LAST_DAY_OF_SHOWA)) {
            return HEISEI;
        } else if (cal.after(LAST_DAY_OF_TAISHO)) {
            return SHOWA;
        } else if (cal.after(LAST_DAY_OF_MEIJI)) {
            return TAISHO;
        } else if (cal.after(LAST_DAY_OF_KEIO)) {
            return MEIJI;
        } else if (cal.after(LAST_DAY_OF_LUNAR)) {
            return UNSUPPORTED;
        } else {
            return UNSUPPORTED;
        }
    }

    /**
     * 和暦の年を取得
     *
     * @return int
     */
    public int getYear() {
        if (cal == null) {
            return 0;
        }

        if (cal.after(LAST_DAY_OF_SHOWA)) {
            return cal.get(GregorianCalendar.YEAR) - YEAR_HEISEI;
        } else if (cal.after(LAST_DAY_OF_TAISHO)) {
            return cal.get(GregorianCalendar.YEAR) - YEAR_SHOWA;
        } else if (cal.after(LAST_DAY_OF_MEIJI)) {
            return cal.get(GregorianCalendar.YEAR) - YEAR_TAISHO;
        } else if (cal.after(LAST_DAY_OF_KEIO)) {
            return cal.get(GregorianCalendar.YEAR) - YEAR_MEIJI;
        } else if (cal.after(LAST_DAY_OF_LUNAR)) {
            //return cal.get(GregorianCalendar.YEAR) - YEAR_MEIJI;
            return 0;
        } else {
            return 0;
        }
    }

    /**
     * 元号，年月日をセット
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @param era
     */
    public void set(String era, int year, int month, int day) {
        if (era.equals(HEISEI)) {
            cal.set(year + YEAR_HEISEI, month, day);
        }

        if (era.equals(SHOWA)) {
            cal.set(year + YEAR_SHOWA, month, day);
        }

        if (era.equals(TAISHO)) {
            cal.set(year + YEAR_TAISHO, month, day);
        }

        if (era.equals(MEIJI)) {
            cal.set(year + YEAR_MEIJI, month, day);
        }
    }

    /**
     * EYY/MM/DDのStringに変換。
     *
     * @return String
     */
    public String toString() {
        String delimiter = "/";

        return getEra() + Integer.toString(getYear()) + delimiter + Integer.toString(getMonth()) + delimiter +
        Integer.toString(getDay());
    }

    /**
     * 西暦文字列YYYY/MM/DDのStringに変換。
     *
     * @return String
     */
    public String toEnString() {
        String delimiter = "/";

        return Integer.toString(cal.get(Calendar.YEAR)) + delimiter + Integer.toString(getMonth()) + delimiter +
        Integer.toString(getDay());
    }

    /**
     * 元号YY年MM年DD日のStringに変換
     *
     * @return String
     */
    public String toJaString() {
        String KG = "";

        if (getEra().equals(MEIJI)) {
            KG = "明治";
        } else if (getEra().equals(TAISHO)) {
            KG = "大正";
        } else if (getEra().equals(SHOWA)) {
            KG = "昭和";
        } else if (getEra().equals(HEISEI)) {
            KG = "平成";
        }

        final int year = getYear();
        String jyear = "";

        if (year == 1) {
            jyear = "元";
        } else {
            jyear = Integer.toString(year);
        }

        return KG + jyear + "年" + Integer.toString(cal.get(GregorianCalendar.MONTH) + 1) + "月" +
        Integer.toString(cal.get(GregorianCalendar.DAY_OF_MONTH)) + "日";
    }
    
    /**
     * 西暦の年を返します。　00～99が返します。
     * 
     * @return yy形式の西暦
     */
    public String getYY(){
        String yy = "";
        try {
            yy = Integer.toString(cal.get(GregorianCalendar.YEAR));
        } catch (Exception e) {
            return yy;
        }
        if(yy.length() >= 4) {
            return yy.substring(2);
        } else {
            return yy;
        }
    }
    
    /**
     * 西暦の年1900～9999を返します。
     * 
     * @return yyyy形式の西暦
     */
    public String getYYYY(){
        String yy = "";
        try {
            yy = Integer.toString(cal.get(GregorianCalendar.YEAR));
        } catch (Exception e) {
            return yy;
        }
        return yy;
    }
    
    /**
     * 元号のアルファベット1文字が表示されます。　M,T,S,Hが返します。
     * 
     * @return　M,T,S,H <br/>
     * 認識できない元号の場合はUを返す.
     */
    public String getG(){
        return this.getEra();
    }
    
    /**
     * 元号の1文字を返します。　明,大,昭,平
     * 
     * @return 明,大,昭,平<br/>
     * 認識できない元号の場合は""を返す.
     */
    public String getGG(){
        String gg = "";

        if (getEra().equals(MEIJI)) {
            gg = "明";
        } else if (getEra().equals(TAISHO)) {
            gg = "大";
        } else if (getEra().equals(SHOWA)) {
            gg = "昭";
        } else if (getEra().equals(HEISEI)) {
            gg = "平";
        }
        return gg;
    }
    
    /**
     * 元号を返します。　明治,大正,昭和,平成
     * 
     * @return 明治,大正,昭和,平成<br/>
     * 認識できない元号の場合は""を返す.
     */
    public String getGGG(){
        String gg = "";

        if (getEra().equals(MEIJI)) {
            gg = "明治";
        } else if (getEra().equals(TAISHO)) {
            gg = "大正";
        } else if (getEra().equals(SHOWA)) {
            gg = "昭和";
        } else if (getEra().equals(HEISEI)) {
            gg = "平成";
        }
        return gg;
    }
    
    /**
     * 和暦年を返します。
     * 
     * @return　7
     */
    public String getE(){
        return Integer.toString(getYear());
    }
    
    /**
     * 0埋め2桁和暦年を返します。
     * 
     * @return 07
     */
    public String getEE(){
        DecimalFormat format = new DecimalFormat("00");
        return format.format(getYear());
    }
    
    /**
     * 元号+年を返します。
     * 
     * @return 昭和52
     */
    public String getGGGE(){
        return getGGG() + getE();
    }
    
    /**
     * 月数を返しす。　1～12を返します。
     * 
     * @return 1～12
     */
    public String getM(){
        return Integer.toString(getMonth());
    }
    
    /**
     * 01～12を返します。　1～9は01～09と0付で表示されます。
     * 
     * @return 01～12
     */
    public String getMM(){
        DecimalFormat format = new DecimalFormat("00");
        return format.format(getMonth());
    }
    
    /**
     * Jan～Dec　月名を省略形で返します。
     * 
     * @return Jan～Dec
     */
    public String getMMM(){
        
        String mmm = "";
        switch (getMonth()) {
        case 1:
            mmm = "Jan";
            break;
        case 2:
            mmm = "Feb";
            break;
        case 3:
            mmm = "Mar";
            break;
        case 4:
            mmm = "Apr";
            break;
        case 5:
            mmm = "May";
            break;
        case 6:
            mmm = "Jun";
            break;
        case 7:
            mmm = "Jul";
            break;
        case 8:
            mmm = "Aug";
            break;
        case 9:
            mmm = "Sep";
            break;
        case 10:
            mmm = "Oct";
            break;
        case 11:
            mmm = "Nov";
            break;
        case 12:
            mmm = "Dec";
            break;

        default:
            break;
        }
        return mmm;
    }
    
    /**
     * January ～ December
     * 
     * @return January ～ December
     */
    public String getMMMM(){
        String mmmm = "";
        switch (getMonth()) {
        case 1:
            mmmm = "January";
            break;
        case 2:
            mmmm = "February";
            break;
        case 3:
            mmmm = "March";
            break;
        case 4:
            mmmm = "April";
            break;
        case 5:
            mmmm = "May";
            break;
        case 6:
            mmmm = "June";
            break;
        case 7:
            mmmm = "July";
            break;
        case 8:
            mmmm = "August";
            break;
        case 9:
            mmmm = "September";
            break;
        case 10:
            mmmm = "October";
            break;
        case 11:
            mmmm = "November";
            break;
        case 12:
            mmmm = "December";
            break;

        default:
            break;
        }
        return mmmm;
        
    }
    
    /**
     * J～D　頭文字を返します。
     * 
     * @return J～D
     */
    public String getMMMMM(){
        String mmmm = "";
        switch (getMonth()) {
        case 1:
            mmmm = "J";
            break;
        case 2:
            mmmm = "F";
            break;
        case 3:
            mmmm = "M";
            break;
        case 4:
            mmmm = "A";
            break;
        case 5:
            mmmm = "M";
            break;
        case 6:
            mmmm = "J";
            break;
        case 7:
            mmmm = "J";
            break;
        case 8:
            mmmm = "A";
            break;
        case 9:
            mmmm = "S";
            break;
        case 10:
            mmmm = "O";
            break;
        case 11:
            mmmm = "N";
            break;
        case 12:
            mmmm = "D";
            break;

        default:
            break;
        }
        return mmmm;
    }
    
    /**
     * 日数を返します。　1～31を返します。
     * 
     * @return 1～31
     */
    public String getD(){
        return Integer.toString(getDay());
    }
    
    /**
     * 01～31を返します。
     * 
     * @return 01～31
     */
    public String getDD(){
        DecimalFormat format = new DecimalFormat("00");
        return format.format(getDay());
    }
    
    /**
     * 曜日を省略形で返します。　Sun～Sat
     * 
     * @return Sun～Sat
     */
    public String getDDD(){
        String ddd = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
            ddd = "Sun";
            break;
        case Calendar.MONDAY:
            ddd = "Mon";
            break;
        case Calendar.TUESDAY:
            ddd = "Tue";
            break;
        case Calendar.WEDNESDAY:
            ddd = "Wed";
            break;
        case Calendar.THURSDAY:
            ddd = "Thu";
            break;
        case Calendar.FRIDAY:
            ddd = "Fri";
            break;
        case Calendar.SATURDAY:
            ddd = "Sat";
            break;

        default:
            break;
        }
        return ddd;
    }
    
    /**
     * Sunday～Saturday
     * 
     * @return Sunday～Saturday
     */
    public String getDDDD(){
        String dddd = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
            dddd = "Sunday";
            break;
        case Calendar.MONDAY:
            dddd = "Monday";
            break;
        case Calendar.TUESDAY:
            dddd = "Tuesday";
            break;
        case Calendar.WEDNESDAY:
            dddd = "Wednesday";
            break;
        case Calendar.THURSDAY:
            dddd = "Thusday";
            break;
        case Calendar.FRIDAY:
            dddd = "Friday";
            break;
        case Calendar.SATURDAY:
            dddd = "Saturday";
            break;

        default:
            break;
        }
        return dddd;
    }
    
    /**
     * 日～土
     * 
     * @return 日～土
     */
    public String getAAA(){
        String aaa = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
            aaa = "日";
            break;
        case Calendar.MONDAY:
            aaa = "月";
            break;
        case Calendar.TUESDAY:
            aaa = "火";
            break;
        case Calendar.WEDNESDAY:
            aaa = "水";
            break;
        case Calendar.THURSDAY:
            aaa = "木";
            break;
        case Calendar.FRIDAY:
            aaa = "金";
            break;
        case Calendar.SATURDAY:
            aaa = "土";
            break;

        default:
            break;
        }
        return aaa;
    }
    
    /**
     * 日曜日～土曜日
     * 
     * @return 日曜日～土曜日
     */
    public String getAAAA(){
        String aaaa = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
            aaaa = "日曜日";
            break;
        case Calendar.MONDAY:
            aaaa = "月曜日";
            break;
        case Calendar.TUESDAY:
            aaaa = "火曜日";
            break;
        case Calendar.WEDNESDAY:
            aaaa = "水曜日";
            break;
        case Calendar.THURSDAY:
            aaaa = "木曜日";
            break;
        case Calendar.FRIDAY:
            aaaa = "金曜日";
            break;
        case Calendar.SATURDAY:
            aaaa = "土曜日";
            break;

        default:
            break;
        }
        return aaaa;
    }
    
    //-- time系関数
    /**
     * 12時間表記の時間を返す.
     * 
     * @return 時間
     */
    public String getHourH() {
        return Integer.toString(cal.get(Calendar.HOUR));
    }
    
    
    /**
     * 24時間表記の時間を返す.
     * 
     * @return 時間
     */
    public String getHourHH() {
        return Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
    }
    
    /**
     * 分を返す.
     * 
     * @return 分
     */
    public String getMinuteMM() {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(cal.get(Calendar.MINUTE));
    }
    
    /**
     * 秒を返す.
     * 
     * @return 秒
     */
    public String getSecondSS() {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(cal.get(Calendar.SECOND));
    }
    
    /**
     * AM/PMを返す.
     * 
     * @return AM, PM
     */
    public String getAMPM() {
        String ampm = "";
        switch (cal.get(Calendar.AM_PM)) {
        case Calendar.AM:
            ampm = "AM";
            break;
        case Calendar.PM:
            ampm = "PM";
            break;
        default:
            break;
        }
        return ampm;
    }
    
    /**
     * 月の取得
     *
     * @return int
     */
    public int getMonth() {
        return cal.get(GregorianCalendar.MONTH) + 1;
    }

    /**
     * 日の取得
     *
     * @return int
     */
    public int getDay() {
        return cal.get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * GregorianCalendarのgetに転送する
     *
     * @param field java.util.GregorianCalendarのフィールド
     *
     * @return int
     */
    public int get(int field) {
        if (cal != null) {
            return cal.get(field);
        } else {
            return 0;
        }
    }

    /**
     * 有効かどうかのチェック
     *
     * @return boolean
     */
    public boolean isValid() {
        if (cal != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 西暦の年月日をセット
     *
     * @param year 年
     * @param month 月
     * @param date
     */
    public void set(int year, int month, int date) {
        cal.set(year, month - 1, date);
    }

    /**
     * インスタンスの大小比較
     *
     * @return int 第２が第１よりも大：１，同じ：０，小：－１
     * @param lDate
     * @param gDate
     */
    public static int compareTo(int[] lDate, int[] gDate) {
        final int year = lDate[0];
        final int month = lDate[1];
        final int day = lDate[2];

        final int gYear = gDate[0];
        final int gMonth = gDate[1];
        final int gDay = gDate[2];

        if (year < gYear) {
            return -1;
        } else if (year == gYear) {
            if (month < gMonth) {
                return -1;
            } else if (month == gMonth) {
                if (day < gDay) {
                    return -1;
                } else if (day == gDay) {
                    return 0;
                }
            }
        }

        return 1;
    }
    
    /**
     * このカレンダーがうるう年か判定する.
     * 
     * @return 
     */
    public boolean isLeapYear() {
        return cal.isLeapYear(get(GregorianCalendar.YEAR));
    }
    

    private static void debug(JaCalendar jc) {
        System.out.print(jc.toEnString() + " " + jc.toJaString());
    }
    
    /**
     * テスト
     * @param args なし
     */
    public static void main(String[] args) {
        JaCalendar jc = new JaCalendar();
        debugPrint(jc);

        jc = new JaCalendar("00010203");
        debugPrint(jc);

        jc = new JaCalendar("19890203");
        debugPrint(jc);
        
        
        jc = new JaCalendar("19890107");
        debugPrint(jc);
        
        
        jc = new JaCalendar("S", 64, 1, 7);
        debugPrint(jc);
        
        
        jc = new JaCalendar("H", 1, 1, 8);
        debugPrint(jc);
        
        
        jc = new JaCalendar(1989, 1, 8);
        debugPrint(jc);
        
        
        jc = new JaCalendar("S", 20, 8, 15);
        debugPrint(jc);
        
        
        jc = new JaCalendar("T111209");
        debugPrint(jc);
        
        
        jc = new JaCalendar(1868, 9, 8);
        //jc.set(1868, 9, 8);
        debugPrint(jc);
        //---
        
    }
    
    private static void debugPrint(JaCalendar testCal) {
        System.out.print("--");
        debug(testCal);
        System.out.println("--");
        System.out.println(testCal.getYYYY()); //2007
        System.out.println(testCal.getYY()); //07
        System.out.println(testCal.getG()); //H
        System.out.println(testCal.getGG()); //平
        System.out.println(testCal.getGGG()); //平成
        System.out.println(testCal.getGGGE()); //平成19
        System.out.println(testCal.getE()); //19
        System.out.println(testCal.getEE());//19
        System.out.println(testCal.getM()); //2
        System.out.println(testCal.getMM());  //02
        System.out.println(testCal.getMMM());//Feb
        System.out.println(testCal.getMMMM());//February
        System.out.println(testCal.getMMMMM());// F
        System.out.println(testCal.getD()); //14
        System.out.println(testCal.getDD()); //14
        System.out.println(testCal.getDDD()); //Wed
        System.out.println(testCal.getDDDD()); //Wednesday
        System.out.println(testCal.getAAA()); //水
        System.out.println(testCal.getAAAA()); //水曜日
        System.out.println(testCal.getHourH()); //1
        System.out.println(testCal.getHourHH()); //13
        System.out.println(testCal.getMinuteMM()); //
        System.out.println(testCal.getSecondSS()); //
        System.out.println(testCal.getAMPM()); //
    }
}
