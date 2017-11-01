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

import com.gh.mygreen.xlsmapper.cellconverter.impl.ArrayCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.BigDecimalCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.BigIntegerCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.BooleanCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.ByteCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.CalendarCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.CellLinkCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.CharacterCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.DateCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.DoubleCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.EnumCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.FloatCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.IntegerCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.ListCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LocalDateCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LocalDateTimeCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LocalTimeCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.LongCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SetCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.ShortCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SqlDateCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SqlTimeCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.SqlTimestampCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.StringCellConverterFactory;
import com.gh.mygreen.xlsmapper.cellconverter.impl.URICellConverterFactory;
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
     * {@link CellConverterFactory}のクラスのキャッシュ情報
     * ・key = 変換対象のJavaのクラスタイプ
     * ・value = Converterクラスのインスタンス。
     */
    private Map<Class<?>, CellConverterFactory<?>> converterFactoryMap;
    
    public CellConverterRegistry() {
        init();
    }
    
    /**
     * 初期化を行います。
     * <p>システム標準の{@link CellConverter}を登録などを行います。
     */
    protected void init() {
        
        if(converterFactoryMap == null) {
            this.converterFactoryMap = new ConcurrentHashMap<>();
        } else {
            converterFactoryMap.clear();
        }
        
        registerConverter(Boolean.class, new BooleanCellConverterFactory());
        registerConverter(boolean.class, new BooleanCellConverterFactory());
        
        registerConverter(Character.class, new CharacterCellConverterFactory());
        registerConverter(char.class, new CharacterCellConverterFactory());
        
        registerConverter(String.class, new StringCellConverterFactory());
        
        registerConverter(Short.class, new ShortCellConverterFactory());
        registerConverter(short.class, new ShortCellConverterFactory());
        
        registerConverter(Byte.class, new ByteCellConverterFactory());
        registerConverter(byte.class, new ByteCellConverterFactory());
        
        registerConverter(Integer.class, new IntegerCellConverterFactory());
        registerConverter(int.class, new IntegerCellConverterFactory());
        
        registerConverter(Long.class, new LongCellConverterFactory());
        registerConverter(long.class, new LongCellConverterFactory());
        
        registerConverter(Float.class, new FloatCellConverterFactory());
        registerConverter(float.class, new FloatCellConverterFactory());
        
        registerConverter(Double.class, new DoubleCellConverterFactory());
        registerConverter(double.class, new DoubleCellConverterFactory());
        
        registerConverter(BigDecimal.class, new BigDecimalCellConverterFactory());
        registerConverter(BigInteger.class, new BigIntegerCellConverterFactory());
        
        registerConverter(Date.class, new DateCellConverterFactory());
        registerConverter(java.sql.Date.class, new SqlDateCellConverterFactory());
        registerConverter(Timestamp.class, new SqlTimestampCellConverterFactory());
        registerConverter(Time.class, new SqlTimeCellConverterFactory());
        
        registerConverter(Calendar.class, new CalendarCellConverterFactory());
        
        registerConverter(Enum.class, new EnumCellConverterFactory());
        
        registerConverter(List.class, new ListCellConverterFactory());
        registerConverter(Set.class, new SetCellConverterFactory());
        registerConverter(Object[].class, new ArrayCellConverterFactory());
        
        registerConverter(URI.class, new URICellConverterFactory());
        registerConverter(CellLink.class, new CellLinkCellConverterFactory());
        
        registerConverter(LocalDateTime.class, new LocalDateTimeCellConverterFactory());
        registerConverter(LocalDate.class, new LocalDateCellConverterFactory());
        registerConverter(LocalTime.class, new LocalTimeCellConverterFactory());
        
    }
    
    /**
     * タイプに対する{@link CellConverterFactory}を取得する。
     * @param clazz 取得対象の{@link CellConverterFactory}のクラス。
     * @return 見つからない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <T> CellConverterFactory<T> getConverterFactory(final Class<T> clazz) {
        ArgUtils.notNull(clazz, "clazz");
        
        CellConverterFactory<T> converterFactory = (CellConverterFactory<T>) converterFactoryMap.get(clazz);
        if(converterFactory == null) {
            // 特別に判定が必要なクラス
            if(Enum.class.isAssignableFrom(clazz)) {
                converterFactory = (CellConverterFactory<T>) converterFactoryMap.get(Enum.class);
                
            } else if(List.class.isAssignableFrom(clazz)) {
                converterFactory = (CellConverterFactory<T>) converterFactoryMap.get(List.class);
                
            } else if(clazz.isArray()) {
                converterFactory = (CellConverterFactory<T>) converterFactoryMap.get(Object[].class);
                
            }
        }
        
        return converterFactory;
    }
    
    /**
     * タイプに対する{@link CellConverter}を登録する。
     * @param clazz 変換対象のJavaのクラスタイプ。
     * @param converterFactory 変換する{@link CellConverterFactory}のインスタンス。
     */
    public <T> void registerConverter(final Class<T> clazz, final CellConverterFactory<T> converterFactory) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(converterFactory, "converterFactory");
        
        converterFactoryMap.put(clazz, converterFactory);
    }
    
}
