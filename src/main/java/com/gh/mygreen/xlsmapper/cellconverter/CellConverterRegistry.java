package com.gh.mygreen.xlsmapper.cellconverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.gh.mygreen.xlsmapper.cellconverter.impl.ArrayCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.BigDecimalCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.BigIntegerCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.BooleanCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.ByteCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.CalendarCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.CellLinkCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.CharacterCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.DateCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.DoubleCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.EnumCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.FloatCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.IntegerCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.ListCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LocalDateCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LocalDateTimeCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LocalTimeCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LongCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SetCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.ShortCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SqlDateCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SqlTimeCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SqlTimestampCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.StringCellConverter;
import com.gh.mygreen.xlsmapper.cellconverter.impl.URICellConverter;
import com.gh.mygreen.xlsmapper.util.ArgUtils;


/**
 * ExcelのCell {@literal <=>} Javaオブジェクト の相互変換をする{@link CellConverter}を管理するクラス。
 * 独自の{@link CellConverter}を登録したりする場合は、このクラスを経由する。
 * 
 * @version 1.0
 * @author T.TSUCHIE
 *
 */
public class CellConverterRegistry {
    
    /**
     * Conveterのクラスのキャッシュ情報
     * ・key = 変換対象のJavaのクラスタイプ
     * ・value = Converterクラスのインスタンス。
     */
    private Map<Class<?>, CellConverter<?>> converterMap;
    
    public CellConverterRegistry() {
        init();
    }
    
    /**
     * 初期化を行います。
     * <p>システム標準の{@link CellConverter}を登録などを行います。
     */
    protected void init() {
        
        if(converterMap == null) {
            this.converterMap = new ConcurrentHashMap<>();
        } else {
            converterMap.clear();
        }
        
        registerConverter(Boolean.class, new BooleanCellConverter());
        registerConverter(boolean.class, new BooleanCellConverter());
        
        registerConverter(Character.class, new CharacterCellConverter());
        registerConverter(char.class, new CharacterCellConverter());
        
        registerConverter(String.class, new StringCellConverter());
        
        registerConverter(Short.class, new ShortCellConverter());
        registerConverter(short.class, new ShortCellConverter());
        
        registerConverter(Byte.class, new ByteCellConverter());
        registerConverter(byte.class, new ByteCellConverter());
        
        registerConverter(Integer.class, new IntegerCellConverter());
        registerConverter(int.class, new IntegerCellConverter());
        
        registerConverter(Long.class, new LongCellConverter());
        registerConverter(long.class, new LongCellConverter());
        
        registerConverter(Float.class, new FloatCellConverter());
        registerConverter(float.class, new FloatCellConverter());
        
        registerConverter(Double.class, new DoubleCellConverter());
        registerConverter(double.class, new DoubleCellConverter());
        
        registerConverter(BigDecimal.class, new BigDecimalCellConverter());
        registerConverter(BigInteger.class, new BigIntegerCellConverter());
        
        registerConverter(Date.class, new DateCellConverter());
        registerConverter(java.sql.Date.class, new SqlDateCellConverter());
        registerConverter(Timestamp.class, new SqlTimestampCellConverter());
        registerConverter(Time.class, new SqlTimeCellConverter());
        
        registerConverter(Calendar.class, new CalendarCellConverter());
        
        registerConverter(Enum.class, new EnumCellConverter());
        
        registerConverter(List.class, new ListCellConverter());
        registerConverter(Set.class, new SetCellConverter());
        registerConverter(Object[].class, new ArrayCellConverter());
        
        registerConverter(URI.class, new URICellConverter());
        registerConverter(CellLink.class, new CellLinkCellConverter());
        
        registerConverter(LocalDateTime.class, new LocalDateTimeCellConverter());
        registerConverter(LocalDate.class, new LocalDateCellConverter());
        registerConverter(LocalTime.class, new LocalTimeCellConverter());
        
    }
    
    /**
     * タイプに対する{@link CellConverter}を取得する。
     * @param clazz 取得対象の{@link CellConverter}のクラス。
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <T> CellConverter<T> getConverter(final Class<T> clazz) {
        ArgUtils.notNull(clazz, "clazz");
        
        CellConverter<T> converter = (CellConverter<T>) converterMap.get(clazz);
        if(converter == null) {
            // 特別に判定が必要なクラス
            if(Enum.class.isAssignableFrom(clazz)) {
                converter = (CellConverter<T>) converterMap.get(Enum.class);
                
            } else if(List.class.isAssignableFrom(clazz)) {
                converter = (CellConverter<T>) converterMap.get(List.class);
                
            } else if(clazz.isArray()) {
                converter = (CellConverter<T>) converterMap.get(Object[].class);
                
            }
        }
        
        return converter;
    }
    
    /**
     * タイプに対する{@link CellConverter}を登録する。
     * @param clazz 変換対象のJavaのクラスタイプ。
     * @param converter 変換するConverterのインスタンス。
     */
    public <T> void registerConverter(final Class<T> clazz, final CellConverter<T> converter) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(converter, "converter");
        
        converterMap.put(clazz, converter);
    }
    
}
