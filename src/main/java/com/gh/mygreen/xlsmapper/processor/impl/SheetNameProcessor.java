package com.gh.mygreen.xlsmapper.processor.impl;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.processor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;


/**
 * アノテーション {@link XlsSheetName} を処理する。
 * 
 * @version 2.0
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class SheetNameProcessor extends AbstractFieldProcessor<XlsSheetName> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsSheetName anno, final FieldAdapter adapter,
            final XlsMapperConfig config, final LoadingWorkObject work) {
        
        final String sheetName = sheet.getSheetName();
        adapter.setValue(beansObj, sheetName);
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsSheetName anno, final FieldAdapter adapter,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final String sheetName = sheet.getSheetName();
        adapter.setValue(targetObj, sheetName);
        
    }
}
