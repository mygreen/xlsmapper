package com.gh.mygreen.xlsmapper.fieldprocessor.impl;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * アノテーション {@link XlsSheetName} を処理する。
 *
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class SheetNameProcessor extends AbstractFieldProcessor<XlsSheetName> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsSheetName anno, final FieldAccessor accessor,
            final Configuration config, final LoadingWorkObject work) {

        if(!Utils.isLoadCase(anno.cases())) {
            return;
        }

        final String sheetName = sheet.getSheetName();
        accessor.setValue(beansObj, sheetName);

    }

    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsSheetName anno, final FieldAccessor accessor,
            final Configuration config, final SavingWorkObject work) throws XlsMapperException {

        if(!Utils.isSaveCase(anno.cases())) {
            return;
        }

        final String sheetName = sheet.getSheetName();
        accessor.setValue(targetObj, sheetName);

    }
}
