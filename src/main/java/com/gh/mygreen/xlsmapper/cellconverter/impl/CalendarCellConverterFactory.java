package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.BaseCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactorySupport;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.TypeBindException;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;
import com.gh.mygreen.xlsmapper.textformatter.TextFormatter;
import com.gh.mygreen.xlsmapper.textformatter.TextParseException;

/**
 * {@link Calendar}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CalendarCellConverterFactory extends CellConverterFactorySupport<Calendar>
        implements CellConverterFactory<Calendar> {
    
    private DateCellConverterFactory dateCellConverterFactory = new DateCellConverterFactory();
    
    @Override
    public CalendarCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final DateCellConverterFactory.DateCellConverter dateCellConverter = dateCellConverterFactory.create(field, config);
        
        final CalendarCellConverter cellConverter = new CalendarCellConverter(field, config, dateCellConverter);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected void setupCustom(final BaseCellConverter<Calendar> cellConverter, final FieldAccessor field, final Configuration config) {
        
        // 何もなし
        
    }
    
    @Override
    protected TextFormatter<Calendar> createTextFormatter(final FieldAccessor field, final Configuration config) {
        
        final TextFormatter<Date> dateTextFormatter = dateCellConverterFactory.createTextFormatter(field, config);
        
        return new TextFormatter<Calendar>() {
            
            @Override
            public Calendar parse(final String text) throws TextParseException {
                
                Date date = dateTextFormatter.parse(text);
                Calendar cal= Calendar.getInstance();
                cal.setTime(date);
                
                return cal;
            }
            
            @Override
            public String format(final Calendar value) {
                
                return dateTextFormatter.format(value.getTime());
            }
        };
    }
    
    public class CalendarCellConverter extends BaseCellConverter<Calendar> {
        
        private final  DateCellConverterFactory.DateCellConverter dateCellConverter;
        
        private CalendarCellConverter(final FieldAccessor field, final Configuration config,
                final DateCellConverterFactory.DateCellConverter dateCellConverter) {
            super(field, config);
            this.dateCellConverter = dateCellConverter;
        }
        
        @Override
        protected Calendar parseCell(final Cell evaluatedCell, final String formattedValue) throws TypeBindException {
            
            Date date = dateCellConverter.parseCell(evaluatedCell, formattedValue);
            if(date != null) {
                Calendar cal= Calendar.getInstance();
                cal.setTime(date);
                
                return cal;
            }
            
            return null;
        }
        
        @Override
        protected void setupCell(final Cell cell, final Optional<Calendar> cellValue) throws TypeBindException {
            
            Optional<Date> dateCellValue = cellValue.map(c -> c.getTime());
            dateCellConverter.setupCell(cell, dateCellValue);
        }
        
    }
}
