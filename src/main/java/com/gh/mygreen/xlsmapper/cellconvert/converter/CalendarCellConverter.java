package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDateConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsFormula;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * {@link Calendar}型の変換用クラス。
 *
 * @since 1.5
 * @author T.TSUCHIE
 *
 */
public class CalendarCellConverter extends AbstractCellConverter<Calendar> {
    
    private DateCellConverter dateConverter;
    
    public CalendarCellConverter() {
        this.dateConverter = new DateCellConverter();
    }
    
    @Override
    public Calendar toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
        
        final Date date = dateConverter.toObject(cell, adaptor, config);
        Calendar cal = null;
        if(date != null) {
            cal = Calendar.getInstance();
            cal.setTime(date);
        }
        
        return cal;
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Calendar targetValue, final Object targetBean,
            final Sheet sheet, final int column, final int row,
            final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getSavingAnnotation(XlsConverter.class);
        final XlsDateConverter anno = dateConverter.getSavingAnnotation(adaptor);
        final XlsFormula formulaAnno = adaptor.getSavingAnnotation(XlsFormula.class);
        final boolean primaryFormula = formulaAnno == null ? false : formulaAnno.primary();
        
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.wrapText());
            POIUtils.shrinkToFit(cell, converterAnno.shrinkToFit());
        }
        
        Calendar value = targetValue;
        
        // デフォルト値から値を設定する
        if(value == null && Utils.hasDefaultValue(converterAnno)) {
            final String defaultValue = converterAnno.defaultValue();
            final DateFormat formatter;
            
            if(Utils.isNotEmpty(anno.javaPattern())) {
                formatter = dateConverter.createDateFormat(anno);
            } else {
                formatter = dateConverter.createDateFormat(dateConverter.getDefaultDateConverterAnnotation());
            }
            
            try {
                Date date = dateConverter.parseDate(defaultValue, formatter);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                value = cal;
            } catch (ParseException e) {
                throw newTypeBindException(e, cell, adaptor, defaultValue)
                    .addAllMessageVars(dateConverter.createTypeErrorMessageVars(anno));
            }
            
        }
        
        // セルの書式の設定
        if(Utils.isNotEmpty(anno.javaPattern())) {
            cell.getCellStyle().setDataFormat(POIUtils.getDataFormatIndex(sheet, anno.javaPattern()));
        }
        
        if(value != null && !primaryFormula) {
            cell.setCellValue(value);
            
        } else if(formulaAnno != null) {
            Utils.setupCellFormula(adaptor, formulaAnno, config, cell, targetBean);
            
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
    
}
