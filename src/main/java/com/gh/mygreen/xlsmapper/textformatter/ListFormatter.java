package com.gh.mygreen.xlsmapper.textformatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gh.mygreen.xlsmapper.cellconverter.ConversionException;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultElementConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ElementConverter;
import com.gh.mygreen.xlsmapper.util.Utils;

/**
 * リストの形式に変換するフォーマッタ。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ListFormatter implements TextFormatter<List> {
    
    /**
     * 要素のクラスタイプ
     */
    private final Class<?> elementType;
    
    /**
     * 要素の区切り
     */
    private String separator = ",";
    
    /**
     * フォーマットする際に空の要素は無視するかどうか
     */
    private boolean ignoreEmptyElement = false;
    
    /**
     * トリムして処理をするかどうか
     */
    private boolean trimmed = false;
    
    /**
     * 要素の変換クラス
     */
    private ElementConverter elementConverter = new DefaultElementConverter();
    
    public ListFormatter(final Class<?> elementType) {
        this.elementType = elementType;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List parse(final String text) throws TextParseException {
        
        if(Utils.isEmpty(text)) {
            return Collections.emptyList();
        }
        
        final String[] split = text.split(separator);
        if(split.length == 0) {
            return Collections.emptyList();
        }
        
        final List list = new ArrayList<>();
        for(String element : split) {
            String strVal = Utils.trim(element, trimmed);
            if(ignoreEmptyElement && Utils.isEmpty(strVal)) {
                continue;
            }
            
            try {
                list.add(elementConverter.convertToObject(strVal, elementType));
                
            } catch(ConversionException e) {
                final Map<String, Object> vars = new HashMap<>();
                vars.put("separator", separator);
                vars.put("ignoreEmptyElement", ignoreEmptyElement);
                vars.put("trimmed", trimmed);
                vars.put("elementClass", elementType.getName());
                
                throw new TextParseException(text, List.class, e, vars);
            }
        }
        
        return list;
    }
    
    @Override
    public String format(final List value) {
        if(value == null) {
            return "";
        }
        return Utils.join(value, separator, ignoreEmptyElement, trimmed, elementConverter);
    }
    
    /**
     * 要素の区切り文字を設定します。
     * @param separator 区切り文字
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    /**
     * フォーマットする際に空の要素は無視するかどうか設定します。
     * @param ignoreEmptyElement 空の要素は無視するかどうか
     */
    public void setIgnoreEmptyElement(boolean ignoreEmptyElement) {
        this.ignoreEmptyElement = ignoreEmptyElement;
    }
    
    /**
     * トリムして処理をするかどうか設定します。
     * @param trimmed トリムして処理をするかどうか
     */
    public void setTrimmed(boolean trimmed) {
        this.trimmed = trimmed;
    }
    
    /**
     * 要素の変換処理方法を設定します。
     * @param elementConverter 要素の変換処理方法
     */
    public void setElementConverter(ElementConverter elementConverter) {
        this.elementConverter = elementConverter;
    }
    
}
