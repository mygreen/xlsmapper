package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.SavingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * アノテーション {@link XlsSheetName} を処理する。
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class SheetNameProcessor extends AbstractFieldProcessor<XlsSheetName> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsSheetName anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final LoadingWorkObject work) {
        
        final String sheetName = sheet.getSheetName();
        adaptor.setValue(beansObj, sheetName);
        
    }
    
    @Override
    public void saveProcess(final Sheet sheet, final Object targetObj, final XlsSheetName anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
        
        final String sheetName = sheet.getSheetName();
        adaptor.setValue(targetObj, sheetName);
        
    }
}
