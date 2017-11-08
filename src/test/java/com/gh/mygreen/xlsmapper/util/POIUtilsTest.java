package com.gh.mygreen.xlsmapper.util;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.gh.mygreen.xlsmapper.TestUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * {@link POIUtils}のテスタ
 *
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class POIUtilsTest {
    
    /**
     * テスト結果ファイルの出力ディレクトリ
     */
    private static File OUT_DIR;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        OUT_DIR = createOutDir();
    }
    
    @Test
    public void testJudgeLinkType() {
        
        assertThat(POIUtils.judgeLinkType("!A1"), is(HyperlinkType.DOCUMENT));
        assertThat(POIUtils.judgeLinkType("Sheet(a)!A1"), is(HyperlinkType.DOCUMENT));
        
        assertThat(POIUtils.judgeLinkType("sample@sample.co.jp"), is(HyperlinkType.EMAIL));
        assertThat(POIUtils.judgeLinkType("mailto:sample@sample.co.jp"), is(HyperlinkType.EMAIL));
        
        assertThat(POIUtils.judgeLinkType("http://sample.co.jp/"), is(HyperlinkType.URL));
        assertThat(POIUtils.judgeLinkType("http://sample.co.jp/?name1=1&name2=2"), is(HyperlinkType.URL));
        
        assertThat(POIUtils.judgeLinkType("sample.xls"), is(HyperlinkType.FILE));
        assertThat(POIUtils.judgeLinkType("../sample.xls"), is(HyperlinkType.FILE));
        
    }
    
    /**
     * {@link POIUtils#updateDataValidationRegion(Sheet, CellRangeAddressList, CellRangeAddressList)}
     * ・XSSF形式、縦方向
     * @since 0.5
     */
    @Test
    public void testUpdateDataValidationRegion_xssf_v() throws Exception {
        
        Workbook workbook = WorkbookFactory.create(new FileInputStream("src/test/data/utils.xlsx"));
        Sheet sheet = workbook.getSheet("入力規則");
        
        CellRangeAddressList oldRegion = new CellRangeAddressList();
        oldRegion.addCellRangeAddress(new CellRangeAddress(4, 5, 2, 2));
        
        CellRangeAddressList newRegion = new CellRangeAddressList();
        newRegion.addCellRangeAddress(new CellRangeAddress(4, 7, 2, 2));
        
        boolean updated = false;
        List<? extends DataValidation> validations = sheet.getDataValidations();
        for(DataValidation dv : validations) {
            
            CellRangeAddressList region = dv.getRegions();
            if(POIUtils.equalsRegion(region, oldRegion)) {
                updated = POIUtils.updateDataValidationRegion(sheet, region, newRegion);
                break;
            }
        }
        
        assertThat(updated, is(true));
        
        // 書き換わったかどうか確認する
        boolean found = false;
        List<? extends DataValidation> updatedvalidations = sheet.getDataValidations();
        for(DataValidation dv : updatedvalidations) {
            CellRangeAddressList region = dv.getRegions();
            if(POIUtils.equalsRegion(region, newRegion)) {
                found = true;
                break;
            }
        }
        
        assertThat(found, is(true));
        
        workbook.write(new FileOutputStream(new File(OUT_DIR, "utils_out.xlsx")));
        
    }
    
    /**
     * {@link POIUtils#updateDataValidationRegion(Sheet, CellRangeAddressList, CellRangeAddressList)}
     * ・XSSF形式、横方向
     * @since 0.5
     */
    @Test
    public void testUpdateDataValidationRegion_xssf_h() throws Exception {
        
        Workbook workbook = WorkbookFactory.create(new FileInputStream("src/test/data/utils.xlsx"));
        Sheet sheet = workbook.getSheet("入力規則");
        
        CellRangeAddressList oldRegion = new CellRangeAddressList();
        oldRegion.addCellRangeAddress(new CellRangeAddress(12, 12, 4, 5));
        
        CellRangeAddressList newRegion = new CellRangeAddressList();
        newRegion.addCellRangeAddress(new CellRangeAddress(12, 12, 4, 7));
        
        boolean updated = false;
        List<? extends DataValidation> validations = sheet.getDataValidations();
        for(DataValidation dv : validations) {
            
            CellRangeAddressList region = dv.getRegions();
            if(POIUtils.equalsRegion(region, oldRegion)) {
                updated = POIUtils.updateDataValidationRegion(sheet, region, newRegion);
                break;
            }
        }
        
        assertThat(updated, is(true));
        
        // 書き換わったかどうか確認する
        boolean found = false;
        List<? extends DataValidation> updatedvalidations = sheet.getDataValidations();
        for(DataValidation dv : updatedvalidations) {
            CellRangeAddressList region = dv.getRegions();
            if(POIUtils.equalsRegion(region, newRegion)) {
                found = true;
                break;
            }
        }
        
        assertThat(found, is(true));
        
        workbook.write(new FileOutputStream(new File(OUT_DIR, "utils_out.xlsx")));
        
    }
    
    /**
     * {@link POIUtils#updateDataValidationRegion(Sheet, CellRangeAddressList, CellRangeAddressList)}
     * ・HSSF形式
     * @since 0.5
     */
    @Test
    public void testUpdateDataValidationRegion_hssf() throws Exception {
        
        Workbook workbook = WorkbookFactory.create(new FileInputStream("src/test/data/utils.xls"));
        Sheet sheet = workbook.getSheet("入力規則");
        
        CellRangeAddressList oldRegion = new CellRangeAddressList();
        oldRegion.addCellRangeAddress(new CellRangeAddress(4, 5, 2, 2));
        
        CellRangeAddressList newRegion = new CellRangeAddressList();
        newRegion.addCellRangeAddress(new CellRangeAddress(4, 7, 2, 2));
        
        boolean updated = false;
        List<? extends DataValidation> validations = sheet.getDataValidations();
        for(DataValidation dv : validations) {
            
            CellRangeAddressList region = dv.getRegions();
            if(POIUtils.equalsRegion(region, oldRegion)) {
                updated = POIUtils.updateDataValidationRegion(sheet, region, newRegion);
                break;
            }
        }
        
        assertThat(updated, is(true));
        
        // 書き換わったかどうか確認する
        boolean found = false;
        List<? extends DataValidation> updatedvalidations = sheet.getDataValidations();
        for(DataValidation dv : updatedvalidations) {
            CellRangeAddressList region = dv.getRegions();
            if(POIUtils.equalsRegion(region, newRegion)) {
                found = true;
                break;
            }
        }
        
        assertThat(found, is(true));
        
        workbook.write(new FileOutputStream(new File(OUT_DIR, "utils_out.xls")));
        
    }
    
//    /**
//     * {@link POIUtils#removeDataValidationRegion(Sheet, CellRangeAddressList)}
//     * ・HSSF形式
//     * @since 0.5
//     */
//    @Test
//    public void testRemoeDataValidationRegion_hssf() throws Exception {
//        
//        Workbook workbook = WorkbookFactory.create(new FileInputStream("src/test/data/utils.xls"));
//        Sheet sheet = workbook.getSheet("入力規則");
//        
//        CellRangeAddressList oldRegion = new CellRangeAddressList();
//        oldRegion.addCellRangeAddress(new CellRangeAddress(4, 5, 2, 2));
//        
//        boolean removed = false;
//        List<? extends DataValidation> validations = sheet.getDataValidations();
//        for(DataValidation dv : validations) {
//            
//            CellRangeAddressList region = dv.getRegions();
//            if(POIUtils.equalsRegion(region, oldRegion)) {
//                removed = POIUtils.removeDataValidation(sheet, dv);
//                break;
//            }
//        }
//        
//        assertThat(removed, is(true));
//        
//        workbook.write(new FileOutputStream(OUT_DIR, "utils_out.xls"));
//        
//        workbook = WorkbookFactory.create(new FileInputStream(new File(OUT_DIR, "utils_out.xls")));
//        sheet = workbook.getSheet("入力規則");
//        
//        // 書き換わったかどうか確認する
//        List<? extends DataValidation> removedList = sheet.getDataValidations();
//        assertThat(removedList, is(hasSize(validations.size() -1)));
//        
//        
//    }
}
