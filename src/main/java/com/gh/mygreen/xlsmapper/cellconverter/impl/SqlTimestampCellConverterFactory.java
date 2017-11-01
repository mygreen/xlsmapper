package com.gh.mygreen.xlsmapper.cellconverter.impl;

import java.sql.Timestamp;
import java.util.Date;

import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.cellconverter.CellConverter;
import com.gh.mygreen.xlsmapper.fieldaccessor.FieldAccessor;

/**
 * {@link Timestamp}を処理する{@link CellConverter}を作成するためのファクトリクラス。
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SqlTimestampCellConverterFactory extends AbstractDateCellConverterFactory<Timestamp> {

    @Override
    public SqlTimestampCellConverter create(FieldAccessor field, Configuration config) {
        
        final SqlTimestampCellConverter cellConverter = new SqlTimestampCellConverter(field, config, this);
        setupCellConverter(cellConverter, field, config);
        
        return cellConverter;
    }
    
    @Override
    protected Timestamp convertTypeValue(Date date) {
        return new Timestamp(date.getTime());
    }
    
    /**
     * {@inheritDoc}
     * <p>{@code yyyy-MM-dd HH:mm:ss.SSS}の値を返す。</p>
     */
    @Override
    protected String getDefaultJavaPattern() {
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }
    
    /**
     * {@inheritDoc}
     * <p>{@code yyyy-mm-dd hh:mm:ss.SSS}の値を返す。</p>
     */
    @Override
    protected String getDefaultExcelPattern() {
        return "yyyy-mm-dd hh:mm:ss.SSS";
    }
    
    public class SqlTimestampCellConverter extends AbstractDateCellConverter<Timestamp> {
        
        private final SqlTimestampCellConverterFactory convererFactory;
        
        private SqlTimestampCellConverter(final FieldAccessor field, final Configuration config,
                final SqlTimestampCellConverterFactory convererFactory) {
            super(field, config);
            this.convererFactory = convererFactory;
        }
        
        @Override
        protected Timestamp convertTypeValue(final Date value) {
            return convererFactory.convertTypeValue(value);
        }
        
    }
    
}
