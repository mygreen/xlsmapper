package com.gh.mygreen.xlsmapper;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * POIのセルの値をフォーマットするクラス。
 *
 * @author T.TSUCHIE
 *
 */
public class POICellFormatter {
    
    /** 
     * 日本語固有日付表現
     * この数値の日付データ地域に依存するがPOIで正しく取得できず、
     * 欧米フォーマットで表現されるため固定の変換処理を行う.
     */
    private static short[] jaDate = {
            14, 15, 16, 17, 18, 19, 
            20, 21, 22, 27, 28, 29, 
            30, 31, 32, 33, 34, 35, 36, 
            45, 46, 47, 
            50, 51, 52, 53, 54, 55, 56, 57, 58
    };
    
    /**
     * POIで正しく取得することが出来ない日付フォーマット
     * バージョンがあがり対応できない場合などはここに追加.
     */
    private static String[] extDateFormat = {
            "yyyy/m/d\\ h:mm\\ AM/PM",  // 0
            "m/d",                      // 1
            "mm/dd/yy",                 // 2
            "dd\\-mmm\\-yy",            // 3
            "mmmm\\-yy",                // 4
            "mmmmm",                    // 5
            "mmmmm\\-yy",               // 6
            "yyyy/m/d\\ h:mm\\ AM/PM",  // 7
    };
    
    /**
     * セルの値を文字列として取得する
     * @param cell 取得対象の
     * @return フォーマットした文字列
     * @throws IllegalArgumentException cell is null.
     */
    public String format(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        
        // データフォーマッターの補正
        short formatIndex = cell.getCellStyle().getDataFormat();
        switch(formatIndex) {
        case 55:
            formatIndex = 34;
            break;
        case 56:
            formatIndex = 35;
            break;
        case 57:
            formatIndex = 36;
            break;
        case 58:
            formatIndex = 28;
            break;
        default:
            break;
        }
        
        DataFormat dataFormat = cell.getSheet().getWorkbook().createDataFormat();
        String formatStr = null;
        try {
            formatStr = dataFormat.getFormat(formatIndex);
        } catch(Exception e) {
            formatStr = "";
        }
        
        switch(cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            return "";
            
        case Cell.CELL_TYPE_BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
            
        case Cell.CELL_TYPE_ERROR:
            return String.valueOf(cell.getCellFormula());
            
        case Cell.CELL_TYPE_FORMULA:
            return getCellValueOfFormula(cell, formatIndex, formatStr);
            
        case Cell.CELL_TYPE_NUMERIC:
            return getCellValueOfNumeric(cell, formatIndex, formatStr);
            
        case Cell.CELL_TYPE_STRING:
            return getCellValueOfString(cell);
            
        default:
            // 不明な種類のセルの場合
            return "";
        }
        
    }
    
    private String getCellValueOfFormula(Cell cell, short formatIndex, String formatStr) {
        
        // 書式のフォーマット
        String value = null;
        DecimalFormat decimalFormat = new DecimalFormat();
        switch(formatIndex) {
        case 0:
            decimalFormat.applyPattern("0.##########");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 5:
            decimalFormat.applyPattern("\\#,##0;\\-#,##0");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 6:
            //formatter.applyPattern("\u00A5#,##0;\u00A5#,##0");
            decimalFormat.applyPattern("\\#,##0;\\-#,##0");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 7:
            //formatter.applyPattern("\u00A5#,##0.00;\u00A5#,##0.00");
            decimalFormat.applyPattern("\\#,##0.00;\\-#,##0.00");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 8:
            //formatter.applyPattern("\u00A5#,##0.00;\u00A5#,##0.00");
            decimalFormat.applyPattern("\\#,##0.00;\\-#,##0.00");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 12: // fraction
            // 分数はシンプルなフォーマットに変換
            decimalFormat.applyPattern("0.#");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 13: // fraction
            // 分数はシンプルなフォーマットに変換
            decimalFormat.applyPattern("0.#");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 25:
            // US locale
            //formatter.applyPattern("($#,##0);($#,##0)");
            decimalFormat.applyPattern("$#,##0.00;($#,##0.00)");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 26:
            // US locale
            //formatter.applyPattern("($#,##0);($#,##0)");
            decimalFormat.applyPattern("$#,##0.00;($#,##0.00)");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
        case 41: //会計
            
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
            
        case 42: //会計
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
            
        case 43: //会計
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
            
        case 44: //会計
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
            
        // 2007/3/28 gp301014 文字列指定でも数値のみ入力された場合はnumericとして取得されるため
        // formatNumeric内で処理する.
        case 49: //文字列
            decimalFormat.applyPattern("0.##########");
            value = decimalFormat.format(cell.getNumericCellValue());
            break;
            
        default:
            
            // その他ユーザ定義のデータフォーマットなど
            if(formatStr.indexOf("[DBNum") >= 0) {
                
                try {
                    
                    if(formatStr.startsWith("[DBNum1]General")) {
                        formatStr = convetFormulaFormat(formatStr);
                        formatStr = formatStr.replaceAll("General", "0.#");
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                        
                    } else if(formatStr.startsWith("[DBNum1]")) {
                        // 四捨五入
                        formatStr = convetFormulaFormat(formatStr);
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                        
                    } else if(formatStr.startsWith("[DBNum2]General")) {
                        formatStr = convetFormulaFormat(formatStr);
                        formatStr = formatStr.replaceAll("General", "0.#");
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                        
                    } else if(formatStr.startsWith("[DBNum2]")) {
                        // 四捨五入
                        formatStr = convetFormulaFormat(formatStr);
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                        
                    } else if(formatStr.startsWith("[DBNum3]General")) {
                        formatStr = convetFormulaFormat(formatStr);
                        formatStr = formatStr.replaceAll("General", "0.#");
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                        
                    } else if(formatStr.startsWith("[DBNum3]")) {
                        // 四捨五入
                        formatStr = convetFormulaFormat(formatStr);
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                        
                    } else {
                        formatStr = convetFormulaFormat(formatStr);
                        decimalFormat.applyPattern(formatStr);
                        value = decimalFormat.format(cell.getNumericCellValue());
                    }
                    
                    
                } catch (Exception e) {
                    value = Double.toString(cell.getNumericCellValue());
                }
            } else {
                // POI独特なExcelフォーマットを補正する.
                formatStr = convetFormulaFormat(formatStr);
                
                // 分数対策
                if(formatStr.indexOf("/") >= 0) {
                    //System.out.println("[TableConvert] 分数は正しく表示できません");
                    formatStr = "0.#";
                }
                
                try {
                    decimalFormat.applyPattern(formatStr);
                    value = decimalFormat.format(cell.getNumericCellValue());
                    value = value.replaceAll("E", "E+");
                } catch (Exception e) {
                    value = Double.toString(cell.getNumericCellValue());
                }
            }
            break;
            
        }
        
        // 最後の整形
        value = value
            .replaceAll(" +$", "")  // 終端の半角スペースの削除
            .replaceAll("\u00A5", "\\\\");  // 「\」を変換
        
        return value;
    }
    
    /**
     * 数式用の書式を変換する。
     * @param format
     * @return
     */
    private String convetFormulaFormat(String format) {
        format = format.replaceAll(";\\@", "");
        format = format.replaceAll("\\@_", "");
        format = format.replaceAll("\\@", "");
        format = format.replaceAll("^\\(", "");
        format = format.replaceAll("0_\\)", "0");
        format = format.replaceAll("0_", "0");
        format = format.replaceAll("0\\\\", "0");
        format = format.replaceAll("\\[[竄ｬ<>=\\$\\-\\w]+\\]", " ");
        format = format.replaceAll("\\\\\\$", "\\$");
        format = format.replaceAll("\\\"", "");
        format = format.replaceAll("\\\\\\(", "\\(");
        format = format.replaceAll("\\\\-", "-");
        format = format.replaceAll("\\+00", "00");
        return format;
    }
    
    /**
     * セルのタイプが数値の場合の値を取得する。
     * @param cell
     * @return
     */
    private String getCellValueOfNumeric(final Cell cell, short formatIndex, String formatStr) {
        
        if(formatStr == null) {
            System.out.println(Utils.formatCellAddress(cell));
        }
        
        // 日付 or 数値の判別がつかないデータを出力
        if(existDateFormat(formatStr)) {
            // index、formatともに正しく取得できない日付フォーマットを先に出力する.
            return formatExistDate(formatIndex, formatStr, cell.getDateCellValue());
            
        } else if(isDateTime(formatIndex) || DateUtil.isCellDateFormatted(cell)) {
            // 日付 (or 時刻)
            return formatDate(formatIndex, formatStr, cell.getDateCellValue());
            
        } else if(formatStr.endsWith(";@") || formatStr.startsWith("[$-") || formatStr.indexOf("yyyy") > 0) {
            // Excel XP, 2003対応 フォーマット文字列が";@"で終わっている、もしくは[$-409],[$-411],[$-F800]で始まっている場合は日付文字列のユーザ定義型.
            return format2003Date(formatIndex, formatStr, cell.getDateCellValue());
            
        }else {
            return getCellValueOfFormula(cell, formatIndex, formatStr);
            
        }
    }
    
    private boolean isDateTime(short s) {
        for(int i=0; i<jaDate.length; i++) {
            if(jaDate[i] == s)
                return true;
        }
        return false;
    }
    
    private boolean existDateFormat(final String format) {
        for(int i=0; i < extDateFormat.length; i++) {
            if(extDateFormat[i].equals(format))
                return true;
        }
        return false;
    }
    
    /**
     * 日付をExcel書式にフォーマットした文字列を返す.
     * 
     * @param format 書式
     * @param date 日付 or 時間
     * @return フォーマット変更された文字列
     */
    private String formatExistDate(short s, String format, Date date) {
        
        JaCalendar cal = new JaCalendar(date);
        String str = "";
        
        // short値が取得できず、フォーマットが取得できた場合
        if(format.equals(extDateFormat[0])) {
            // yyyy/m/d\\ h:mm\\ AM/PM
            str = cal.getYYYY() + "/" + cal.getMM() + "/" + cal.getDD() + 
            " " + cal.getHourH() + ":" + cal.getMinuteMM() + " " + cal.getAMPM();
        } else if(format.equals(extDateFormat[1])) {
            str = cal.getM() + "/" + cal.getD();
        } else if(format.equals(extDateFormat[2])) {
            str = cal.getMM() + "/" + cal.getDD() + "/" + cal.getYY();
        } else if(format.equals(extDateFormat[3])) {
            str = cal.getDD() + "-" + cal.getMMM()+ "-" + cal.getYY();
        } else if(format.equals(extDateFormat[4])) {
            str = cal.getMMMM()+ "-" + cal.getYY();
        } else if(format.equals(extDateFormat[5])) {
            str = cal.getMMMMM();
        } else if(format.equals(extDateFormat[6])) {
            str = cal.getMMMMM()+ "-" + cal.getYY();
        } else if(format.equals(extDateFormat[7])) {
            str = cal.getYYYY() + "/" + cal.getM() + "/" + cal.getD() + " " + cal.getHourH() + ":" + cal.getMinuteMM() + " " + cal.getAMPM();
        } else {
            System.out.println("[TableConvert] 想定外の日付フォーマットのため変換できません.");
            str = cal.getYYYY() + "/" + cal.getMM() + "/" + cal.getDD();
        }
        return str;
        
    }
    
    /**
     * 日付をExcel書式にフォーマットした文字列を返す.
     * 
     * @param s 
     * @param format 書式
     * @param date 日付 or 時間
     * @return フォーマット変更された文字列
     */
    private String formatDate(short s, String format, Date date) {
        
        JaCalendar cal = new JaCalendar(date);
        String str = "";
        switch (s) {
        case 14:
            // YYYY/MM/DD
            str = cal.getYYYY() + "/" + cal.getM() + "/" + cal.getD(); 
            break;
        case 15:
            // D-MMM-YY
            str = cal.getD() + "-" + cal.getMMM() + "-" + cal.getYY();
            break;
        case 16:
            // D-MMM
            str = cal.getD() + "-" + cal.getMMM();
            break;
        case 17:
            // MMM-YY
            str = cal.getMMM() + "-" + cal.getYY();
            break;
        case 18:
            // h:mm AM/PM
            str = cal.getHourH() + ":" + cal.getMinuteMM() + " " + cal.getAMPM();
            break;
        case 19:
            // h:mm:ss AM/PM
            str = cal.getHourH() + ":" + cal.getMinuteMM() + ":" + cal.getSecondSS() + " " + cal.getAMPM();
            break;
        case 20:
            // hh:mm
            str = cal.getHourHH() + ":" + cal.getMinuteMM();
            break;
        case 21:
            // hh:mm:ss
            str = cal.getHourHH() + ":" + cal.getMinuteMM() + ":" + cal.getSecondSS();
            break;
        case 22:
            // yyyy/m/d hh:mm
            str = cal.getYYYY() + "/" + cal.getM() + "/" + cal.getD() + " " + cal.getHourHH() + ":" + cal.getMinuteMM(); 
            break;
        case 27:
            // GE.M.D
            str = cal.getG() + cal.getE() + "." + cal.getM() + "." + cal.getD();
            break;
        case 28:
            // GGGE年M月D日
            str = cal.getGGGE() + "年" + cal.getM() + "月" + cal.getD() + "日";
            break;
        case 29:
            // GGGE年M月D日
            str = cal.getGGGE() + "年" + cal.getM() + "月" + cal.getD() + "日";
            break;
        case 30:
            // M/D/YY
            str = cal.getM() + "/" + cal.getD() + "/" + cal.getYY();
            break;
        case 31:
            // YYYY年M月D日
            str = cal.getYYYY() + "年" + cal.getM() + "月" + cal.getD() + "日";
            break;
        case 32:
            // h時mm分
            str = cal.getHourHH() + "時" + cal.getMinuteMM() + "分";
            break;
        case 33:
            // YYYY年M月
            //str = cal.getYYYY() + "年" + cal.getM() + "月";
            // h時mm分ss秒
            str = cal.getHourHH() + "時" + cal.getMinuteMM() + "分" + cal.getSecondSS() + "秒";
            break;
        case 34:
            // YYYY年M月
            str = cal.getYYYY() + "年" + cal.getM() + "月";
            break;
        case 35:
            // M月D日
            str = cal.getM() + "月" + cal.getD() + "日";
            break;
        case 36:
            // GE.M.D
            str = cal.getG() + cal.getE() + "." + cal.getM() + "." + cal.getD();
            break;
        case 50:
            // GE.M.D
            str = cal.getG() + cal.getE() + "." + cal.getM() + "." + cal.getD();
            break;
        case 51:
            // GGGE年M月D日
            str = cal.getGGGE() + "年" + cal.getM() + "月" + cal.getD() + "日";
            break;
        case 52:
            // YYYY年M月
            str = cal.getYYYY() + "年" + cal.getM() + "月";
            break;
        case 53:
            // M月D日
            str = cal.getM() + "月" + cal.getD() + "日";
            break;
        case 54:
            // GGGE年M月D日
            str = cal.getGGG() + cal.getE() + "年" + cal.getM() + "月" + cal.getD() + "日";
            break;
        case 55:
            // YYYY年M月
            str = cal.getYYYY() + "年" + cal.getM() + "月";
            break;
        case 56:
            // M月D日
            str = cal.getM() + "月" + cal.getD() + "日";
            break;
        case 57:
            // GE.M.D
            str = cal.getG() + cal.getE() + "." + cal.getM() + "." + cal.getD();
            break;
        case 58:
            // GGGE年M月D日
            str = cal.getGGG() + cal.getE() + "年" + cal.getM() + "月" + cal.getD() + "日";
            break;
        default:
            // 解析処理追加
            str = cal.getYYYY() + "/" + cal.getMM() + "/" + cal.getDD();
            break;
        }
        return str;
        
    }
    
    
    private String format2003Date(short s, String fs, Date date) {
        JaCalendar cal = new JaCalendar(date);
        
        // 日付埋め込み
        fs = fs.replaceAll("yyyy", cal.getYYYY());
        fs = fs.replaceAll("yy", cal.getYY());
        fs = fs.replaceAll("gggee", cal.getGGG() + cal.getE());
        fs = fs.replaceAll("ggge", cal.getGGGE());
        fs = fs.replaceAll("gge", cal.getGG() + cal.getE());
        fs = fs.replaceAll("ge", cal.getG() + cal.getE());
        fs = fs.replaceAll("rr", cal.getGGG() + cal.getEE());
        fs = fs.replaceAll("r", cal.getEE());
        fs = fs.replaceAll("mmmmm", cal.getMMMMM());
        fs = fs.replaceAll("mmmm", cal.getMMMM());
        fs = fs.replaceAll("mmm", cal.getMMM());
        fs = fs.replaceAll("mm", cal.getMinuteMM());
        //fs = fs.replaceAll("mm", cal.getMM());
        if(!fs.matches(".*(January|Feburary|March|April|May|June|July|August|September|October|November|December).*")) {
            fs = fs.replaceAll("m", cal.getM());
        }
        fs = fs.replaceAll("dddd", cal.getDDDD());
        fs = fs.replaceAll("ddd", cal.getDDD());
        fs = fs.replaceAll("dd", cal.getDD());
        if(!fs.matches(".*(Sun|Mon|Tues|Wednes|Thurs|Fry|Satur)day.*")){
            fs = fs.replaceAll("d", cal.getD());
        }
        fs = fs.replaceAll("aaaa", cal.getAAAA());
        fs = fs.replaceAll("aaa", cal.getAAA());
        
        // 時刻埋め込み
        fs = fs.replaceAll("h:mm", cal.getHourH() + ":" + cal.getMinuteMM());
        fs = fs.replaceAll("h:m", cal.getHourH() + ":" + cal.getMinuteMM());
        fs = fs.replaceAll("hh", cal.getHourHH());
        
        if(fs.startsWith("[$-")) {
            fs = fs.replaceAll("h", cal.getHourH());
        } else {
            fs = fs.replaceAll("h", cal.getHourHH());
        }
        fs = fs.replaceAll("ss", cal.getSecondSS());
        //fs = fs.replaceAll("s", cal.getSecondSS());
        //fs = fs.replaceAll("[h]", "");//未対応

        //fs = fs.replaceAll("[m]", "");//未対応

        //fs = fs.replaceAll("[s]", "");//未対応

        fs = fs.replaceAll("AM/PM", cal.getAMPM());
        //fs = fs.replaceAll("A/P", cal.getAMPM()); //未対応
        
        // Excelフォーマット変換
        fs = fs.replaceAll(";\\@", "");
        fs = fs.replaceAll("\\\\", "");
        fs = fs.replaceAll("\\\\.", "\\.");
        fs = fs.replaceAll("\\\\ ", " ");
        fs = fs.replaceAll("\\\\,", "\\,");
        fs = fs.replaceAll("\\[[<>=\\$\\-\\w]+\\]", "");
        //fs = fs.replaceAll("\\[[竄ｬ<>=\\$\\-\\w]+\\]", "");
        fs = fs.replaceAll("\\\\\\$", "\\$");
        fs = fs.replaceAll("\\\"", "");
        fs = fs.replaceAll("\\\\\\(", "\\(");
        return fs;
    }
    
    /**
     * セルのタイプが文字列の場合の値を取得する。
     * @param cell
     * @return 値が設定されていない場合、空文字を返す。
     */
    private String getCellValueOfString(final Cell cell) {
        
        String value = cell.getRichStringCellValue().getString();
        if((value == null)) {
            return "";
            
        } 
        
        // 文字列の最後の半角スペースを削除する
//        return value.replaceAll(" +$", "");
        return value;
        
    }
}
