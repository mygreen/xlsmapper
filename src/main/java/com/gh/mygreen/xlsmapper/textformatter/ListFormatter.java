package com.gh.mygreen.xlsmapper.textformatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gh.mygreen.xlsmapper.cellconverter.ConversionException;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultItemConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ItemConverter;
import com.gh.mygreen.xlsmapper.util.Utils;

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
    private boolean ignoreEmptyItem = false;
    
    /**
     * トリムして処理をするかどうか
     */
    private boolean trimmed = false;
    
    private ItemConverter itemConverter = new DefaultItemConverter();
    
    public ListFormatter(final Class<?> elementType) {
        this.elementType = elementType;
    }
    
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
        for(String item : split) {
            String strVal = Utils.trim(item, trimmed);
            if(ignoreEmptyItem && Utils.isEmpty(strVal)) {
                continue;
            }
            
            try {
                list.add(itemConverter.convertToObject(strVal, elementType));
                
            } catch(ConversionException e) {
                final Map<String, Object> vars = new HashMap<>();
                vars.put("separator", separator);
                vars.put("ignoreEmptyItem", ignoreEmptyItem);
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
        return Utils.join(value, separator, ignoreEmptyItem, trimmed, itemConverter);
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
     * @param ignoreEmptyItem 空の要素は無視するかどうか
     */
    public void setIgnoreEmptyItem(boolean ignoreEmptyItem) {
        this.ignoreEmptyItem = ignoreEmptyItem;
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
     * @param itemConverter 要素の変換処理方法
     */
    public void setItemConverter(ItemConverter itemConverter) {
        this.itemConverter = itemConverter;
    }
    
}
