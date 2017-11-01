package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Date;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Date}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DateCellConverterFactory extends AbstractDateCellConverterFactory<Date> {
    
    @Override
    public DateCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final DateCellConverter cellConverter = new DateCellConverter(this, field, config);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected Date convertTypeValue(final Date date) {
        return date;
    }
    
    /**
     * {@inheritDoc}
     * <p>{@code yyyy-MM-dd HH:mm:ss}の値を返す。</p>
     */
    @Override
    protected String getDefaultJavaPattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }
    
    /**
     * {@inheritDoc}
     * <p>{@code yyyy-mm-dd hh:mm:ss}の値を返す。</p>
     */
    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd hh:mm:ss";
    }
    
    public class DateCellConverter extends AbstractDateCellConverter<Date> {
        
        private final DateCellConverterFactory convererFactory;
        
        private DateCellConverter(final DateCellConverterFactory convererFactory,
                final FieldAccessor field, final Configuration config) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Date convertTypeValue(final Date value) {
            return convererFactory.convertTypeValue(value);
        }
    }
    
}
