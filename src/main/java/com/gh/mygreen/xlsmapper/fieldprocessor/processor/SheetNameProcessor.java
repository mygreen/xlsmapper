package com.gh.mygreen.xlsmapper.fieldprocessor.processor;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.LoadingWorkObject;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsSheetName;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import com.gh.mygreen.xlsmapper.fieldprocessor.LoadingFieldProcessor;


/**
 * アノテーション {@link XlsSheetName} を処理する。
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 */
public class SheetNameProcessor implements LoadingFieldProcessor<XlsSheetName> {

    @Override
    public void loadProcess(final Sheet sheet, final Object beansObj, final XlsSheetName anno, final FieldAdaptor adaptor,
            final XlsMapperConfig config, final LoadingWorkObject work) {
        
        final String sheetName = sheet.getSheetName();
        adaptor.setValue(beansObj, sheetName);
        
    }
}
