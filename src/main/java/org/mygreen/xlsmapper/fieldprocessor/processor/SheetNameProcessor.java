package org.mygreen.xlsmapper.fieldprocessor.processor;

import org.apache.poi.ss.usermodel.Sheet;
import org.mygreen.xlsmapper.LoadingWorkObject;
import org.mygreen.xlsmapper.XlsMapperConfig;
import org.mygreen.xlsmapper.annotation.XlsSheetName;
import org.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;
import org.mygreen.xlsmapper.fieldprocessor.LoadingFieldProcessor;


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
