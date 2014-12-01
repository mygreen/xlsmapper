package com.gh.mygreen.xlsmapper;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * シンプルなセルフォーマッター
 *
 * 参考URL
 * <ul>
 *   <li><a href="http://www.ne.jp/asahi/hishidama/home/tech/apache/poi/cell.html"></a></li>
 *   <li><a href="http://shin-kawara.seesaa.net/article/159663314.html">POIでセルの値をとるのは大変　日付編</a></li>
 *
 * @version 0.2.3
 * @author T.TSUCHIE
 *
 */
public class POICellFormatter {
    
    private static Logger logger = LoggerFactory.getLogger(POICellFormatter.class);
    
    /**
     * セルの値を文字列として取得する
     * @param cell 取得対象の
     * @return フォーマットした文字列
     * @throws IllegalArgumentException cell is null.
     */
    public String format(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        
        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                // 結合しているセルの場合、左上のセル以外に値が設定されている場合がある。
                return getMergedCellValue(cell);
                
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
                
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().toString();
                
            case Cell.CELL_TYPE_NUMERIC:
                return getNumericCellValue(cell);
                
            case Cell.CELL_TYPE_FORMULA:
                return getFormulaCellValue(cell);
                
            case Cell.CELL_TYPE_ERROR:
                return "";
                
            default:
                return "";
        }
    }
    
    /**
     * 式が設定されているセルの値を評価する。
     * @param cell
     * @return
     */
    private String getFormulaCellValue(final Cell cell) {
        
        final int cellType = cell.getCellType();
        if(cellType != Cell.CELL_TYPE_FORMULA) {
            throw new IllegalArgumentException(String.format("cell type should be FORMULA, but %d.", cellType));
        }
        
        final Workbook workbook = cell.getSheet().getWorkbook();
        final CreationHelper helper = workbook.getCreationHelper();
        final FormulaEvaluator evaluator = helper.createFormulaEvaluator();
        
        // 再帰的に処理する
        final Cell evalCell = evaluator.evaluateInCell(cell);
        return format(evalCell);
        
    }
    
    /**
     * 結合されているセルの値の取得。
     * <p>通常は左上のセルに値が設定されているが、結合されているときは左上以外のセルの値を取得する。
     * <p>左上以外のセルに値が設定されている場合は、CellTypeがCELL_TYPE_BLANKになるため注意が必要。
     * @param cell
     * @return
     */
    private String getMergedCellValue(final Cell cell) {
        
        final int rowIndex = cell.getRowIndex();
        final int columnIndex = cell.getColumnIndex();
        
        final Sheet sheet = cell.getSheet();
        final int size = sheet.getNumMergedRegions();
        
        for(int i=0; i < size; i++) {
            final CellRangeAddress range = sheet.getMergedRegion(i);
            if(range.isInRange(rowIndex, columnIndex)) {
                final Cell firstCell = POIUtils.getCell(sheet, range.getFirstColumn(), range.getFirstRow());
                return format(firstCell);
            }
        }
        
        return "";
    }
    
    /**
     * 数値型のセルの値を取得する。
     * <p>書式付きの数字か日付のどちらかの場合がある。
     * @param cell
     * @return
     */
    private String getNumericCellValue(final Cell cell) {
        
        final int cellType = cell.getCellType();
        if(cellType != Cell.CELL_TYPE_NUMERIC) {
            throw new IllegalArgumentException(String.format("cell type should be FORMULA, but %d.", cellType));
        }
        
        // セルの書式の取得。
        // 補正したりする
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
        
        final DataFormat dataFormat = cell.getSheet().getWorkbook().createDataFormat();
        String formatStr = null;
        try {
            formatStr = dataFormat.getFormat(formatIndex);
        } catch(Exception e) {
            formatStr = "";
        }
        
        if(isSpecialDateFormat(formatStr)) {
            // POIで処理できない日付の形式
            return formatSpecialDate(cell.getDateCellValue(), formatStr);
            
        } else if(isJaDate(formatIndex) || DateUtil.isCellDateFormatted(cell)) {
            // 日本語固有の日付フォーマットの場合
            return formatJaDate(cell.getDateCellValue(), formatIndex);
            
        } else if(formatStr.endsWith(";@") || formatStr.startsWith("[$-") || formatStr.indexOf("yyyy") > 0) {
            /* Excel XP, 2003対応 フォーマット文字列が";@"で終わっている、
             * もしくは[$-409],[$-411],[$-F800]で始まっている場合は、日付文字列のユーザ定義型。 
             */
            return format2003Date(cell.getDateCellValue(), formatStr);
            
        } else {
            // 数値として処理する
            return formatNumeric(cell.getNumericCellValue(), formatIndex, formatStr);
        }
        
    }
    
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
     * 日本語固有の日付のインデックスの場合
     * @param formatIndex
     * @return
     */
    private boolean isJaDate(short formatIndex) {
        for(int i=0; i < jaDate.length; i++) {
            if(jaDate[i] == formatIndex) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 日本語固有の日付をフォーマットする
     * @param date
     * @param formatIndex
     * @return
     */
    private String formatJaDate(final Date date, final short formatIndex) {
        
        final JaCalendar cal = new JaCalendar(date);
        final String str;
        
        switch (formatIndex) {
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
    
    /**
     * POIで正しく取得することが出来ない日付フォーマット
     * バージョンがあがり対応できない場合などはここに追加.
     */
    private static String[] specailDateFormat = {
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
     * POIで正しく取得することができない日付フォーマットか判定する。
     * @param formatStr
     * @return
     */
    private boolean isSpecialDateFormat(final String formatStr) {
        for(String str : specailDateFormat) {
            if(str.equals(formatStr)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * POIで処理できない特別な形式の日時型のフォーマット
     * @param date
     * @param format
     * @return
     */
    private String formatSpecialDate(final Date date, final String format) {
        final JaCalendar cal = new JaCalendar(date);
        
        final String str;
        
        // short値が取得できず、フォーマットが取得できた場合
        if(format.equals(specailDateFormat[0])) {
            // yyyy/m/d\\ h:mm\\ AM/PM
            str = cal.getYYYY() + "/" + cal.getMM() + "/" + cal.getDD() + 
                    " " + cal.getHourH() + ":" + cal.getMinuteMM() + " " + cal.getAMPM();
        } else if(format.equals(specailDateFormat[1])) {
            str = cal.getM() + "/" + cal.getD();
        } else if(format.equals(specailDateFormat[2])) {
            str = cal.getMM() + "/" + cal.getDD() + "/" + cal.getYY();
        } else if(format.equals(specailDateFormat[3])) {
            str = cal.getDD() + "-" + cal.getMMM()+ "-" + cal.getYY();
        } else if(format.equals(specailDateFormat[4])) {
            str = cal.getMMMM()+ "-" + cal.getYY();
        } else if(format.equals(specailDateFormat[5])) {
            str = cal.getMMMMM();
        } else if(format.equals(specailDateFormat[6])) {
            str = cal.getMMMMM()+ "-" + cal.getYY();
        } else if(format.equals(specailDateFormat[7])) {
            str = cal.getYYYY() + "/" + cal.getM() + "/" + cal.getD() + " " + cal.getHourH() + ":" + cal.getMinuteMM() + " " + cal.getAMPM();
        } else {
            if(logger.isDebugEnabled()) {
                logger.debug("想定外の日付フォーマットのため変換できません。{}", format);
            }
            str = cal.getYYYY() + "/" + cal.getMM() + "/" + cal.getDD();
        }
        
        return str;
    }
    
    /**
     * Excel2003/XP形式の場合の日付のフォーマット
     * @param fs
     * @param date
     * @return
     */
    private String format2003Date(final Date date, final String formatStr) {
        
        final JaCalendar cal = new JaCalendar(date);
        
        // 書式を変更する
        String fs = formatStr;
        
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
     * 書式付きの数値をフォーマットする
     * 
     * @param numeric
     * @param formatIndex
     * @param formatStr
     * @return
     */
    private String formatNumeric(final double numeric, final short formatIndex, final String formatStr) {
        
        String value = "";
        final DecimalFormat decimalFormat = new DecimalFormat();
        switch(formatIndex) {
        case 0:
            decimalFormat.applyPattern("0.##########");
            value = decimalFormat.format(numeric);
            break;
        case 5:
            decimalFormat.applyPattern("\\#,##0;\\-#,##0");
            value = decimalFormat.format(numeric);
            break;
        case 6:
            //formatter.applyPattern("\u00A5#,##0;\u00A5#,##0");
            decimalFormat.applyPattern("\\#,##0;\\-#,##0");
            value = decimalFormat.format(numeric);
            break;
        case 7:
            //formatter.applyPattern("\u00A5#,##0.00;\u00A5#,##0.00");
            decimalFormat.applyPattern("\\#,##0.00;\\-#,##0.00");
            value = decimalFormat.format(numeric);
            break;
        case 8:
            //formatter.applyPattern("\u00A5#,##0.00;\u00A5#,##0.00");
            decimalFormat.applyPattern("\\#,##0.00;\\-#,##0.00");
            value = decimalFormat.format(numeric);
            break;
        case 12: // fraction
            // 分数はシンプルなフォーマットに変換
            decimalFormat.applyPattern("0.#");
            value = decimalFormat.format(numeric);
            break;
        case 13: // fraction
            // 分数はシンプルなフォーマットに変換
            decimalFormat.applyPattern("0.#");
            value = decimalFormat.format(numeric);
            break;
        case 25:
            // US locale
            //formatter.applyPattern("($#,##0);($#,##0)");
            decimalFormat.applyPattern("$#,##0.00;($#,##0.00)");
            value = decimalFormat.format(numeric);
            break;
        case 26:
            // US locale
            //formatter.applyPattern("($#,##0);($#,##0)");
            decimalFormat.applyPattern("$#,##0.00;($#,##0.00)");
            value = decimalFormat.format(numeric);
            break;
        case 41: //会計
            
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(numeric);
            break;
            
        case 42: //会計
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(numeric);
            break;
            
        case 43: //会計
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(numeric);
            break;
            
        case 44: //会計
            decimalFormat.applyPattern("\\\t\t#,##0;\\-#,##0");
            value = decimalFormat.format(numeric);
            break;
            
        // 2007/3/28 gp301014 文字列指定でも数値のみ入力された場合はnumericとして取得されるため
        // formatNumeric内で処理する.
        case 49: //文字列
            decimalFormat.applyPattern("0.##########");
            value = decimalFormat.format(numeric);
            break;
            
        default:
            
            // その他ユーザ定義のデータフォーマットなど
            if(formatStr.indexOf("[DBNum") >= 0) {
                
                try {
                    
                    if(formatStr.startsWith("[DBNum1]General")) {
                        String fs = convetNumericFormat(formatStr);
                        fs = fs.replaceAll("General", "0.#");
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                        
                    } else if(formatStr.startsWith("[DBNum1]")) {
                        // 四捨五入
                        String fs = convetNumericFormat(formatStr);
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                        
                    } else if(formatStr.startsWith("[DBNum2]General")) {
                        String fs = convetNumericFormat(formatStr);
                        fs = formatStr.replaceAll("General", "0.#");
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                        
                    } else if(formatStr.startsWith("[DBNum2]")) {
                        // 四捨五入
                        String fs = convetNumericFormat(formatStr);
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                        
                    } else if(formatStr.startsWith("[DBNum3]General")) {
                        String fs = convetNumericFormat(formatStr);
                        fs = formatStr.replaceAll("General", "0.#");
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                        
                    } else if(formatStr.startsWith("[DBNum3]")) {
                        // 四捨五入
                        String fs = convetNumericFormat(formatStr);
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                        
                    } else {
                        String fs = convetNumericFormat(formatStr);
                        decimalFormat.applyPattern(fs);
                        value = decimalFormat.format(numeric);
                    }
                    
                    
                } catch (Exception e) {
                    value = Double.toString(numeric);
                }
            } else {
                // POI独特なExcelフォーマットを補正する.
                String fs = convetNumericFormat(formatStr);
                
                // 分数対策
                if(fs.indexOf("/") >= 0) {
                    //System.out.println("[TableConvert] 分数は正しく表示できません");
                    fs = "0.#";
                }
                
                try {
                    decimalFormat.applyPattern(fs);
                    value = decimalFormat.format(numeric);
                    value = value.replaceAll("E", "E+");
                } catch (Exception e) {
                    value = Double.toString(numeric);
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
     * @param formatStr
     * @return
     */
    private String convetNumericFormat(final String formatStr) {
        
        String fs = formatStr;
        fs = fs.replaceAll(";\\@", "");
        fs = fs.replaceAll("\\@_", "");
        fs = fs.replaceAll("\\@", "");
        fs = fs.replaceAll("^\\(", "");
        fs = fs.replaceAll("0_\\)", "0");
        fs = fs.replaceAll("0_", "0");
        fs = fs.replaceAll("0\\\\", "0");
        fs = fs.replaceAll("\\[[竄ｬ<>=\\$\\-\\w]+\\]", " ");
        fs = fs.replaceAll("\\\\\\$", "\\$");
        fs = fs.replaceAll("\\\"", "");
        fs = fs.replaceAll("\\\\\\(", "\\(");
        fs = fs.replaceAll("\\\\-", "-");
        fs = fs.replaceAll("\\+00", "00");
        
        return fs;
    }
    
}
