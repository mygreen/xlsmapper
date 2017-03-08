package com.gh.mygreen.xlsmapper.converter.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.annotation.XlsEnumConverter;
import com.gh.mygreen.xlsmapper.converter.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.converter.ConversionException;
import com.gh.mygreen.xlsmapper.converter.TypeBindException;
import com.gh.mygreen.xlsmapper.processor.FieldAdapter;
import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * 列挙型のConverter。
 * <p>一度読み込んだ列挙型の情報はキャッシュする。列挙型はstaticであるため、動的に変更できないため。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class EnumCellConverter extends AbstractCellConverter<Enum> {
    
    //TODO: キャッシュは、FieldAdapterのgetNameWithClassをキーとする。
    
    /**
     * 列挙科型のクラスとその値とのマップ。(キャッシュデータ)
     * <p>key=列挙型のクラスタイプ、value=列挙型の各項目の文字列形式とオブジェクト形式の値のマップ。
     */
    private Map<Class<?>, Map<String, Enum>> cacheData;
    
    public EnumCellConverter() {
        this.cacheData =  new ConcurrentHashMap<Class<?>, Map<String,Enum>>();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Enum<?> parseDefaultValue(final String defaultValue, final FieldAdapter adapter, final XlsMapperConfig config) 
            throws TypeBindException {
        
        final Optional<XlsEnumConverter> convertAnno = adapter.getAnnotation(XlsEnumConverter.class);
        
        final Class<Enum> taretClass = (Class<Enum>) adapter.getType();
        try {
            Enum<?> value = convertToObject(defaultValue, taretClass, convertAnno);
            return value;
            
        } catch(ConversionException e) {
            throw newTypeBindExceptionWithDefaultValue(e, adapter, defaultValue)
                .addAllMessageVars(createTypeErrorMessageVars(taretClass, convertAnno));
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Enum parseCell(final Cell evaluatedCell, final String formattedValue, final FieldAdapter adapter,
            final XlsMapperConfig config) throws TypeBindException {
        
        if(!formattedValue.isEmpty()) {
            
            final Optional<XlsEnumConverter> convertAnno = adapter.getAnnotation(XlsEnumConverter.class);
            
            final Class<Enum> taretClass = (Class<Enum>) adapter.getType();
            try {
                Enum<?> value = convertToObject(formattedValue, taretClass, convertAnno);
                return value;
                
            } catch(ConversionException e) {
                throw newTypeBindExceptionWithParse(e, evaluatedCell, adapter, formattedValue)
                    .addAllMessageVars(createTypeErrorMessageVars(taretClass, convertAnno));
            }
            
        }
        
        return null;
        
    }
    
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     * @throws ConversionException 
     */
    private Map<String, Object> createTypeErrorMessageVars(final Class<Enum> taretClass, final Optional<XlsEnumConverter> convertAnno) 
            throws ConversionException {
        
        final String valueMethodName = convertAnno.map(a -> a.valueMethodName()).orElse("");
        final boolean ignoreCase = convertAnno.map(a -> a.ignoreCase()).orElse(false);
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("candidateValues", Utils.join(getLoadingAvailableValue(taretClass, valueMethodName), ", "));
        vars.put("ignoreCase", ignoreCase);
        
        return vars;
    }
    
    /**
     * 読み込み時の入力の候補となる値を取得する
     * @param clazz 列挙型の値
     * @param valueMethodName
     * @return
     * @throws ConversionException 
     */
    private Collection<String> getLoadingAvailableValue(final Class<Enum> clazz, final String valueMethodName) throws ConversionException {
        
        final Set<String> values = new LinkedHashSet<String>();
        
        final Map<String, Enum> map = getEnumValueMapFromCache(clazz);
        for(Map.Entry<String, Enum> entry : map.entrySet()) {
            if(valueMethodName.isEmpty()) {
                values.add(entry.getKey());
            } else {
                try {
                    final Method method = clazz.getMethod(valueMethodName, new Class[]{});
                    method.setAccessible(true);
                    final String value = method.invoke(entry.getValue(), new Object[]{}).toString();
                    values.add(value);
                } catch(Exception e) {
                    throw new ConversionException(
                            String.format("Not found Enum method '%s#%s()'.", clazz.getName(), valueMethodName),
                            e, clazz);
                }
            }
        }
        
        return values;
        
    }
    
    /**
     * 列挙型のキーと値のマップをキャッシュから取得する。
     * <p>key=列挙型のname()の値。value=列挙型の各項目のオブジェクト。
     * <p>キャッシュ上に存在しなければ、新しく情報を作成しキャッシュに追加する。
     * @param clazz
     * @return
     */
    private synchronized Map<String, Enum> getEnumValueMapFromCache(final Class<Enum> clazz) {
        if(cacheData.containsKey(clazz)) {
            return cacheData.get(clazz);
        }
        
        final Map<String, Enum> map = createEnumValueMap(clazz);
        cacheData.put(clazz, map);
        return map;
    }
    
    /**
     * 列挙型のマップを作成する。
     * 
     * @param clazz 列挙型のクラス。
     * @return key=項目名（{@link Enum#name()}メソッドの値）、value=列挙型の項目オブジェクト。
     */
    @SuppressWarnings({"unchecked"})
    private Map<String, Enum> createEnumValueMap(final Class<Enum> clazz) {
        
        final Map<String, Enum> map = new LinkedHashMap<>();
        final EnumSet set = EnumSet.allOf(clazz);
        final Iterator<Enum> it = set.iterator();
        while(it.hasNext()) {
            final Enum e = it.next();
            map.put(e.name(), e);
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    private Enum<?> convertToObject(final String value, final Class<Enum> clazz, final Optional<XlsEnumConverter> convertAnno)
            throws ConversionException {
        
        final String valueMethodName = convertAnno.map(a -> a.valueMethodName()).orElse("");
        final boolean ignoreCase = convertAnno.map(a -> a.ignoreCase()).orElse(false);
        
        final Map<String, Enum> map = getEnumValueMapFromCache(clazz);
        for(Map.Entry<String, Enum> entry : map.entrySet()) {
            
            final String key;
            if(valueMethodName.isEmpty()) {
                key = entry.getKey();
            } else {
                try {
                    final Method method = clazz.getMethod(valueMethodName, new Class[]{});
                    method.setAccessible(true);
                    key = method.invoke(entry.getValue(), new Object[]{}).toString();
                } catch(Exception e) {
                    throw new ConversionException(
                            String.format("Not found Enum method '%s#%s()'.", clazz.getName(), valueMethodName),
                            e, clazz);
                }
            }
            
            if(ignoreCase && value.equalsIgnoreCase(key)) {
                return entry.getValue();
                
            } else if(value.equals(key)) {
                return entry.getValue();
            }
        }
        
        throw new ConversionException(String.format("fail parse '%s' => %s.", value, clazz.getName()), clazz);
        
    }
    
    private String convertToString(final Enum<?> value, final Class<Enum> clazz, final Optional<XlsEnumConverter> convertAnno)
            throws ConversionException {
        
        final String valueMethodName = convertAnno.map(a -> a.valueMethodName()).orElse("");
        
        final Map<String, Enum> map = getEnumValueMapFromCache(clazz);
        for(Map.Entry<String, Enum> entry : map.entrySet()) {
            
            if(entry.getValue() != value) {
                continue;
            }
            
            if(valueMethodName.isEmpty()) {
                return value.name();
            } else {
                try {
                    final Method method = clazz.getMethod(valueMethodName, new Class[]{});
                    method.setAccessible(true);
                    return method.invoke(entry.getValue(), new Object[]{}).toString();
                } catch(Exception e) {
                    throw new ConversionException(
                            String.format("Not found Enum method '%s#%s()'.", clazz.getName(), valueMethodName),
                            e, clazz);
                }
            }
        }
        
        throw new ConversionException(String.format("fail format '%s'.", value.name()), clazz);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void setupCell(final Cell cell, final Optional<Enum> cellValue, final FieldAdapter adapter, final XlsMapperConfig config)
            throws TypeBindException {
        
        final Optional<XlsEnumConverter> convertAnno = adapter.getAnnotation(XlsEnumConverter.class);
        
        final Class<Enum> taretClass = (Class<Enum>) adapter.getType();
        
        if(cellValue.isPresent()) {
            final String value = convertToString(cellValue.get(), taretClass, convertAnno);
            cell.setCellValue(value);
            
        } else {
            cell.setCellType(CellType.BLANK);
        }
        
    }
    
}
