package com.gh.mygreen.xlsmapper.cellconvert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gh.mygreen.xlsmapper.ArgUtils;
import com.gh.mygreen.xlsmapper.FactoryCallback;
import com.gh.mygreen.xlsmapper.cellconvert.converter.ArrayCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.BigDecimalCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.BigIntegerCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.BooleanCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.ByteCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.CalendarCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.CellLinkCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.CharacterCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.DateCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.DoubleCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.EnumCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.FloatCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.IntegerCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.ListCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.LongCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.SetCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.ShortCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.SqlDateCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.SqlTimeCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.SqlTimestampCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.StringCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.converter.URICellConverter;


/**
 * ExcelのCell <=> Javaオブジェクト の相互変換をするConverterを管理するクラス。
 * 独自のConverterを登録したりする場合は、このクラスを経由する。
 * 
 * @author T.TSUCHIE
 *
 */
public class CellConverterRegistry {
    
    private static Logger logger = LoggerFactory.getLogger(CellConverterRegistry.class);
    
    /** {@link CellConverter}のインスタンスを作成する */
    private FactoryCallback<Class<CellConverter>, CellConverter> cellConverterFactory;
    
    //TODO: NamedConverterにする。
    private Map<Class<?>, CellConverter<?>> converterMap;
    
    public CellConverterRegistry() {
        init();
    }
    
    protected void init() {
        
        if(cellConverterFactory == null) {
            this.cellConverterFactory = new FactoryCallback<Class<CellConverter>, CellConverter>() {
                @Override
                public CellConverter create(final Class<CellConverter> clazz) {
                    try {
                        return clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(String.format("fail create CellConverter instance of '%s'", clazz.getName()), e);
                    }
                }
            };
        }
        
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
        
    }
    
    @SuppressWarnings("rawtypes")
    public CellConverter createCellConverter(final Class<CellConverter> clazz) {
        return cellConverterFactory.create(clazz);
    }
    
    public void setCellConverterFactory(FactoryCallback<Class<CellConverter>, CellConverter> cellConverterFactory) {
        this.cellConverterFactory = cellConverterFactory;
    }
    
//    static {
//        try {
//            InputStream in = AutoTypeConverterFactory.class.getResourceAsStream(
//                    "/xlsbeans-converter.properties");
//            if(in != null){
//                Properties props = new Properties();
//                props.load(in);
//                
//                ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
//                if (clsLoader == null) {
//                    clsLoader = AutoTypeConverterFactory.class.getClassLoader();
//                }
//                
//                for(Map.Entry<Object, Object> entry : props.entrySet()){
//                    try {
//                        Class<?> typeClazz
//                            = clsLoader.loadClass((String)entry.getKey());
//                        
//                        Class<? extends AbstractTypeConverter> converterClazz = clsLoader.loadClass(
//                                (String)entry.getValue()).asSubclass(AbstractTypeConverter.class);
//                        
//                        if(converters.containsKey(typeClazz)) {
//                            converters.remove(typeClazz);
//                        }
//                        converters.put(typeClazz, converterClazz.newInstance());
//                    } catch(Exception ex){
//                        // TODO Logging or throw exception
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        } catch(Exception ex){
//            // TODO Logging or throw exception
//            ex.printStackTrace();
//        }
    
    /**
     * タイプに対する{@link CellConverter}を取得する。
     * @param clazz
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
     * @param clazz
     * @param converter
     */
    public <T> void registerConverter(final Class<T> clazz, final CellConverter<T> converter) {
        ArgUtils.notNull(clazz, "clazz");
        ArgUtils.notNull(converter, "converter");
        
        converterMap.put(clazz, converter);
    }
    
    public Map<Class<?>, CellConverter<?>> getConverterMap() {
        return converterMap;
    }
    
    public void setConverterMap(Map<Class<?>, CellConverter<?>> converterMap) {
        this.converterMap = converterMap;
    }
    
}
