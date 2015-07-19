package com.gh.mygreen.xlsmapper.cellconvert.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.POIUtils;
import com.gh.mygreen.xlsmapper.Utils;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.converter.XlsEnumConverter;
import com.gh.mygreen.xlsmapper.cellconvert.AbstractCellConverter;
import com.gh.mygreen.xlsmapper.cellconvert.ConversionException;
import com.gh.mygreen.xlsmapper.fieldprocessor.FieldAdaptor;


/**
 * 列挙型のConverter。
 * <p>一度読み込んだ列挙型の情報はキャッシュする。列挙型はstaticであるため、動的に変更できないため。
 * 
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class EnumCellConverter extends AbstractCellConverter<Enum> {
    
    /**
     * 列挙科型のクラスとその値とのマップ。(キャッシュデータ)
     * <p>key=列挙型のクラスタイプ、value=列挙型の各項目の文字列形式とオブジェクト形式の値のマップ。
     */
    private Map<Class<?>, Map<String, Enum>> cacheData;
    
    public EnumCellConverter() {
        this.cacheData =  new ConcurrentHashMap<Class<?>, Map<String,Enum>>();
    }
    
    @SuppressWarnings({"unchecked"})
    @Override
    public Enum<?> toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config) throws XlsMapperException {
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsEnumConverter anno = getLoadingAnnotation(adaptor);
        
        String cellValue = POIUtils.getCellContents(cell, config.getCellFormatter());
        cellValue = Utils.trim(cellValue, converterAnno);
        if(Utils.isEmpty(cellValue) && Utils.hasNotDefaultValue(converterAnno)) {
            return null;
        }
        cellValue = Utils.getDefaultValueIfEmpty(cellValue, converterAnno);
        
        final Class<Enum> taretClass = (Class<Enum>) adaptor.getTargetClass();
        Enum<?> resultValue = convertToObject(cellValue, taretClass, anno);
        if(resultValue == null && Utils.isNotEmpty(cellValue)) {
            // 値があり変換できない場合
            throw newTypeBindException(cell, adaptor, cellValue)
                .addAllMessageVars(createTypeErrorMessageVars(taretClass, anno));
        }
        
        return resultValue;
    }
    
    /**
     * 型変換エラー時のメッセージ変数の作成
     * @throws ConversionException 
     */
    private Map<String, Object> createTypeErrorMessageVars(final Class<Enum> taretClass, final XlsEnumConverter anno) throws ConversionException {
        
        final Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("candidateValues", Utils.join(getLoadingAvailableValue(taretClass, anno), ", "));
        vars.put("ignoreCase", anno.ignoreCase());
        
        return vars;
    }
    
    private XlsEnumConverter getDefaultEnumConverterAnnotation() {
        return new XlsEnumConverter() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return XlsEnumConverter.class;
            }
            
            @Override
            public boolean ignoreCase() {
                return false;
            }
            
            @Override
            public String valueMethodName() {
                return "";
            }
            
        };
    }
    
    private XlsEnumConverter getLoadingAnnotation(final FieldAdaptor adaptor) {
        XlsEnumConverter anno = adaptor.getLoadingAnnotation(XlsEnumConverter.class);
        if(anno == null) {
            anno = getDefaultEnumConverterAnnotation();
        }
        return anno;
    }
    
    private XlsEnumConverter getSavingAnnotation(final FieldAdaptor adaptor) {
        XlsEnumConverter anno = adaptor.getSavingAnnotation(XlsEnumConverter.class);
        if(anno == null) {
            anno = getDefaultEnumConverterAnnotation();
        }
        return anno;
    }
    
    /**
     * 読み込み時の入力の候補となる値を取得する
     * @param clazz 列挙型の値
     * @param anno
     * @return
     * @throws ConversionException 
     */
    private Collection<String> getLoadingAvailableValue(final Class<Enum> clazz, final XlsEnumConverter anno) throws ConversionException {
        
        final Set<String> values = new LinkedHashSet<String>();
        
        final Map<String, Enum> map = getEnumValueMapFromCache(clazz);
        for(Map.Entry<String, Enum> entry : map.entrySet()) {
            if(anno.valueMethodName().isEmpty()) {
                values.add(entry.getKey());
            } else {
                try {
                    final Method method = clazz.getMethod(anno.valueMethodName(), new Class[]{});
                    method.setAccessible(true);
                    final String value = method.invoke(entry.getValue(), new Object[]{}).toString();
                    values.add(value);
                } catch(Exception e) {
                    throw new ConversionException(
                            String.format("Not found Enum method '%s#%s()'.", clazz.getName(), anno.valueMethodName()),
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
    
    private Enum<?> convertToObject(final String value, final Class<Enum> clazz, final XlsEnumConverter anno) throws ConversionException {
        
        final Map<String, Enum> map = getEnumValueMapFromCache(clazz);
        for(Map.Entry<String, Enum> entry : map.entrySet()) {
            
            final String key;
            if(anno.valueMethodName().isEmpty()) {
                key = entry.getKey();
            } else {
                try {
                    final Method method = clazz.getMethod(anno.valueMethodName(), new Class[]{});
                    method.setAccessible(true);
                    key = method.invoke(entry.getValue(), new Object[]{}).toString();
                } catch(Exception e) {
                    throw new ConversionException(
                            String.format("Not found Enum method '%s#%s()'.", clazz.getName(), anno.valueMethodName()),
                            e, clazz);
                }
            }
            
            if(anno.ignoreCase() && value.equalsIgnoreCase(key)) {
                return entry.getValue();
                
            } else if(value.equals(key)) {
                return entry.getValue();
            }
        }
        
        return null;
        
    }
    
    private String convertToString(final Enum<?> value, final Class<Enum> clazz, final XlsEnumConverter anno) throws ConversionException {
        
        final Map<String, Enum> map = getEnumValueMapFromCache(clazz);
        for(Map.Entry<String, Enum> entry : map.entrySet()) {
            
            if(entry.getValue() != value) {
                continue;
            }
            
            if(anno.valueMethodName().isEmpty()) {
                return value.name();
            } else {
                try {
                    final Method method = clazz.getMethod(anno.valueMethodName(), new Class[]{});
                    method.setAccessible(true);
                    return method.invoke(entry.getValue(), new Object[]{}).toString();
                } catch(Exception e) {
                    throw new ConversionException(
                            String.format("Not found Enum method '%s#%s()'.", clazz.getName(), anno.valueMethodName()),
                            e, clazz);
                }
            }
        }
        
        return null;
        
    }
    
    @Override
    public Cell toCell(final FieldAdaptor adaptor, final Enum targetValue, final Sheet sheet,
            final int column, final int row, final XlsMapperConfig config) throws XlsMapperException {
        
        final XlsConverter converterAnno = adaptor.getLoadingAnnotation(XlsConverter.class);
        final XlsEnumConverter anno = getSavingAnnotation(adaptor);
        
        final Class<Enum> taretClass = (Class<Enum>) adaptor.getTargetClass();
        final Cell cell = POIUtils.getCell(sheet, column, row);
        
        // セルの書式設定
        if(converterAnno != null) {
            POIUtils.wrapCellText(cell, converterAnno.forceWrapText());
            POIUtils.shrinkToFit(cell, converterAnno.forceShrinkToFit());
        }
        
        Enum value = targetValue;
        
        // デフォルト値から値を設定する
        if(value == null && Utils.hasDefaultValue(converterAnno)) {
            value = convertToObject(Utils.getDefaultValue(converterAnno), (Class<Enum>) adaptor.getTargetClass(), anno);
            
            // 初期値が設定されているが、変換できないような時はエラーとする
            if(value == null) {
                throw newTypeBindException(cell, adaptor, Utils.getDefaultValue(converterAnno))
                        .addAllMessageVars(createTypeErrorMessageVars(taretClass, anno));
            }
        }
        
        if(value != null) {
            final String cellValue = convertToString(value, (Class<Enum>) adaptor.getTargetClass(), anno);
            cell.setCellValue(cellValue);
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }
        
        return cell;
    }
    
}
