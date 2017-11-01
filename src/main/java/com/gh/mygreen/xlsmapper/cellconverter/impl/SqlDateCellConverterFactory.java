package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.util.Date;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link java.sql.Date}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class SqlDateCellConverterFactory extends AbstractDateCellConverterFactory<java.sql.Date> {

    @Override
    public SqlDateCellConverter create(final FieldAccessor field, final Configuration config) {
        
        final SqlDateCellConverter cellConverter = new SqlDateCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
        
    }
    
    @Override
    protected java.sql.Date convertTypeValue(final Date date) {
        return new java.sql.Date(date.getTime());
    }
    
    /**
     * {@inheritDoc}
     * <p>{@code yyyy-MM-dd}の値を返す。</p>
     */
    @Override
    protected String getDefaultJavaPattern() {
        return "yyyy-MM-dd";
    }
    
    /**
     * {@inheritDoc}
     * <p>{@code yyyy-mm-dd}の値を返す。</p>
     */
    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd";
    }
    
    public class SqlDateCellConverter extends AbstractDateCellConverter<java.sql.Date> {
        
        private final SqlDateCellConverterFactory convererFactory;
        
        private SqlDateCellConverter(final FieldAccessor field, final Configuration config,
                final SqlDateCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected java.sql.Date convertTypeValue(final Date value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
