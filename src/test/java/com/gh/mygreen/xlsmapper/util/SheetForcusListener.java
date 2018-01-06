package com.gh.mygreen.xlsmapper.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.gh.mygreen.xlsmapper.annotation.XlsPostSave;

/**
 * 保存時にシートにフォーカスをするリスナー
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SheetForcusListener {

    @XlsPostSave
    public void onSave(final Sheet sheet) {

        Workbook workbook = sheet.getWorkbook();

        int index = workbook.getSheetIndex(sheet.getSheetName());
        workbook.setActiveSheet(index);
        workbook.setFirstVisibleTab(index);

    }

}
