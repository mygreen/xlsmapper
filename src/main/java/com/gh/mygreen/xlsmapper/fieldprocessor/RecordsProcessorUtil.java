package com.gh.mygreen.xlsmapper.fieldprocessor;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsColumn;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.HorizontalRecordsProcessor;
import com.gh.mygreen.xlsmapper.fieldprocessor.processor.VerticalRecordsProcessor;
import com.gh.mygreen.xlsmapper.xml.AnnotationReader;


/**
 * Provides generic utility methods for {@link HorizontalRecordsProcessor} and {@link VerticalRecordsProcessor}.
 * 
 * @author Naoki Takezoe
 */
public class RecordsProcessorUtil {
    
    /**
     * アノテーションXlsColumnの属性columnNameで指定した値が、ヘッダーセルに存在するかチェックする。
     * @param sheet
     * @param recordClass
     * @param headers
     * @param reader
     * @param config
     * @throws Exception
     */
    public static void checkColumns(final Sheet sheet, final Class<?> recordClass,
            final List<RecordHeader> headers, final AnnotationReader reader, final XlsMapperConfig config)
                    throws XlsMapperException {
        
        for(FieldAdaptor property : Utils.getLoadingColumnProperties(recordClass, null, reader, config)) {
            final XlsColumn column = property.getLoadingAnnotation(XlsColumn.class);
            
            if(!column.optional()){
                String columnName = column.columnName();
                boolean find = false;
                for(RecordHeader info: headers){
                    if(Utils.matches(info.getHeaderLabel(), columnName, config)){
                        find = true;
                        break;
                    }
                }
                if(!find){
                    throw new CellNotFoundException(sheet.getSheetName(), columnName);
                }
            }
        }
        
    }
}
