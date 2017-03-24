package com.gh.mygreen.xlsmapper.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;

import com.gh.mygreen.xlsmapper.BeanFactory;
import com.gh.mygreen.xlsmapper.XlsMapperConfig;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultItemConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ItemConverter;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * ユーティリティクラス。
 * 
 * @version 1.5
 * @author T.TSUCHIE
 * @author Naoki Takezoe
 * @author Mitsuyoshi Hasegawa
 *
 */
public class Utils {
    
    @SuppressWarnings("rawtypes")
    private static final ItemConverter ITEM_CONVERTER = new DefaultItemConverter();
    
    /**
     * コレクションの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col 処理対象のコレクション。
     * @param separator 区切り文字。
     * @param ignoreEmptyItem 空、nullの要素を無視するかどうか。
     * @param trim トリムをするかどうか。
     * @param itemConverter 要素を変換するクラス。
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String join(final Collection<?> col, final String separator,
            final boolean ignoreEmptyItem, final boolean trim, final ItemConverter itemConverter) {
        
        final List<Object> list = new ArrayList<Object>();
        for(Object item : col) {
            if(item == null) {
                continue;
            }
            
            Object value = item;
            
            if(item instanceof String) {
                String str = (String) item;
                if(ignoreEmptyItem && isEmpty(str)) {
                    continue;
                    
                } else if(trim) {
                    value = str.trim();
                }
                
            } else if(item instanceof Character && isEmpty(item.toString())) {
                String str = item.toString();
                if(ignoreEmptyItem && isEmpty(str)) {
                    continue;
                    
                } else if(trim) {
                    value = str.trim().charAt(0);
                }
                
            } else if(char.class.isAssignableFrom(item.getClass())) {
                String str = item.toString();
                if(ignoreEmptyItem && isEmpty(str)) {
                    continue;
                    
                } else if(trim) {
                    value = str.trim().charAt(0);
                }
            }
            
            list.add(value);
            
        }
        
        return join(list, separator, itemConverter);
        
    }
    
    /**
     * 配列の要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param arrays
     * @param separator
     * @return
     */
    public static String join(final Object[] arrays, final String separator) {
        
        return join(arrays, separator, ITEM_CONVERTER);
        
    }
    
    /**
     * 配列の要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param arrays
     * @param separator
     * @param itemConverter
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String join(final Object[] arrays, final String separator, final ItemConverter itemConverter) {
        
        if(arrays == null) {
            return "";
        }
        
        final int len = arrays.length;
        if(len == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < len; i++) {
            final Object item = arrays[i];
            sb.append(itemConverter.convertToString(item));
            
            if(separator != null && (i < len-1)) {
                sb.append(separator);
            }
        }
        
        return sb.toString();
        
    }
    
    /**
     * Collectionの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col
     * @param separator
     * @return
     */
    public static String join(final Collection<?> col, final String separator) {
        return join(col, separator, ITEM_CONVERTER);
    }
    
    /**
     * Collectionの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col
     * @param separator
     * @param itemConverter
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String join(final Collection<?> col, final String separator, final ItemConverter itemConverter) {
        
        if(col == null) {
            return "";
        }
        
        final int size = col.size();
        if(size == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for(Iterator<?> itr = col.iterator(); itr.hasNext();) {
            final Object item = itr.next();
            String text = itemConverter.convertToString(item);
            sb.append(text);
            
            if(separator != null && itr.hasNext()) {
                sb.append(separator);
            }
        }
        
        return sb.toString();
        
    }
    
    /**
     * 先頭の文字を大文字にする。
     * <pre>
     * Utils.capitalize(null)  = null
     * Utils.capitalize("")    = ""
     * Utils.capitalize("cat") = "Cat"
     * Utils.capitalize("cAt") = "CAt"
     * </pre>
     * @param str
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String capitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }
        
        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toUpperCase())
            .append(str.substring(1))
            .toString();
    }
    
    /**
     * 先頭の文字を小文字にする。
     * @param str 変換対象の文字
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String uncapitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }
        
        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toLowerCase())
            .append(str.substring(1))
            .toString();
    }
    
    /**
     * システム設定に従いラベルを比較する。
     * <p>正規表現や正規化を行い指定する。
     * 
     * @since 1.1
     * @param text1 セルのラベル
     * @param text2 アノテーションに指定されているラベル。
     *              {@literal /<ラベル>/}と指定する場合、正規表現による比較を行う。
     * @param config システム設定
     * @return true:ラベルが一致する。
     */
    public static boolean matches(final String text1, final String text2, final XlsMapperConfig config){
        if(config.isRegexLabelText() && text2.startsWith("/") && text2.endsWith("/")){
            return normalize(text1, config).matches(text2.substring(1, text2.length() - 1));
        } else {
            return normalize(text1, config).equals(normalize(text2, config));
//            return normalize(text1, config).equals(text2);
        }
    }
    
    /**
     * システム設定に従いラベルを正規化する。
     * @since 1.1
     * @param text セルのラベル
     * @param config システム設定
     * @return true:ラベルが一致する。
     */
    private static String normalize(final String text, final XlsMapperConfig config){
        if(text != null && config.isNormalizeLabelText()){
            return text.trim().replaceAll("[\n\r]", "").replaceAll("[\t 　]+", " ");
        }
        return text;
    }
    
    /**
     * 文字列が空文字か判定する。
     * <p>文字数が1でかつ、{@literal \u0000}のときは、trueを判定する。</p>
     * @param str 判定対象の文字
     * @return trueの場合、空文字を判定する。
     */
    public static boolean isEmpty(final String str) {
        if(str == null || str.isEmpty()) {
            return true;
        }
        
        if(str.length() == 1) {
            return str.charAt(0) == '\u0000';
        }
        
        return false;
    }
    
    /**
     * 文字列が空文字でないか判定する。
     * @param str 判定対象の文字
     * @return
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }
    
    /**
     * コレクションが空か判定する。
     * @param collection
     * @return nullまたはサイズが0のときにtrueを返す。
     */
    public static boolean isEmpty(final Collection<?> collection) {
        if(collection == null || collection.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isNotEmpty(final Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 配列がが空か判定する。 
     * @param arrays
     * @return nullまたは、配列のサイズが0のときにtrueを返す。
     */
    public static boolean isEmpty(final Object[] arrays) {
        if(arrays == null || arrays.length == 0) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 配列が空でないか判定する
     * @param arrays
     * @return
     */
    public static boolean isNotEmpty(final Object[] arrays) {
        return !isEmpty(arrays);
    }
    
    /**
     * オブジェクトの比較を行う。
     * <p>値がnullの場合を考慮する。
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean equals(final Object obj1, final Object obj2) {
        
        if(obj1 == null && obj2 == null) {
            return true;
        }
        
        if(obj1 == null) {
            return false;
        }
        
        if(obj2 == null) {
            return false;
        }
        
        return obj1.equals(obj2);
        
    }
    
    public static boolean notEquals(final Object obj1, final Object obj2) {
        return !equals(obj1, obj2);
    }
    
    /**
     * オブジェクトを文字列に変換する。
     * <p>nullの場合、文字列として "null"を返す。
     * <p>単純に、{@link Object#toString()}を呼び出す。
     * @param value
     * @return
     */
    public static String convertToString(final Object value) {
        
        if(value == null) {
            return "null";
        }
        
        return value.toString();
        
    }
    
    /**
     * アノテーションの属性trimに従い、文字列をトリムする。
     * @param value トリム対象の文字
     * @param trimAnno トリムのアノテーション
     * @return トリミングした結果。
     */
    public static String trim(final String value, final Optional<XlsTrim> trimAnno) {
        
        if(!trimAnno.isPresent() || value == null) {
            return value;
        }
        
        return value.trim();
        
    }
    
    /**
     * PostProcessなどのメソッドを実行する。
     * <p>メソッドの引数が既知のものであれば、インスタンスを設定する。
     * 
     * @param processObj 実行対象の処理が埋め込まれているオブジェクト。
     * @param method 実行対象のメソッド情報
     * @param beanObj 処理対象のBeanオブジェクト。
     * @param sheet シート情報
     * @param config 共通設定
     * @param errors エラー情報
     * @throws XlsMapperException 
     */
    public static void invokeNeedProcessMethod(final Object processObj, final Method method, final Object beanObj, 
            final Sheet sheet, final XlsMapperConfig config, final SheetBindingErrors errors) throws XlsMapperException {
        
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Object[] paramValues =  new Object[paramTypes.length];
        
        for(int i=0; i < paramTypes.length; i++) {
            if(Sheet.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = sheet;
                
            } else if(XlsMapperConfig.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = config;
                
            } else if(SheetBindingErrors.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = errors;
                
            } else if(paramTypes[i].isAssignableFrom(beanObj.getClass())) {
                paramValues[i] = beanObj;
                
            } else if(paramTypes[i].equals(Object.class)) {
                paramValues[i] = beanObj;
                
            } else {
                paramValues[i] = null;
            }
        }
        
        try {
            method.setAccessible(true);
            method.invoke(processObj, paramValues);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable t = e.getCause() == null ? e : e.getCause();
            throw new XlsMapperException(
                    String.format("fail execute method '%s#%s'.", processObj.getClass().getName(), method.getName()),
                    t);
        }
    }
    
    /**
     * 文字列形式のロケールをオブジェクトに変換する。
     * <p>アンダーバーで区切った'ja_JP'を分解して、Localeに渡す。
     * @param str
     * @return 引数が空の時はデフォルトロケールを返す。
     */
    public static Locale getLocale(final String str) {
        
        if(isEmpty(str)) {
            return Locale.getDefault();
        }
        
        if(!str.contains("_")) {
            return new Locale(str);
        }
        
        final String[] split = str.split("_");
        if(split.length == 2) {
            return new Locale(split[0], split[1]);
            
        } else {
            return new Locale(split[0], split[1], split[2]);
        }
        
    }
    
    /**
     * エスケープ文字を除去した文字列を取得する。
     * @param str
     * @param escapeChar
     * @return
     */
    public static String removeEscapeChar(final String str, final char escapeChar) {
        
        if(str == null || str.isEmpty()) {
            return str;
        }
        
        final String escapeStr = String.valueOf(escapeChar);
        StringBuilder sb = new StringBuilder();
        
        LinkedList<String> stack = new LinkedList<String>();
        
        final int length = str.length();
        for(int i=0; i < length; i++) {
            final char c = str.charAt(i);
            
            if(StackUtils.equalsTopElement(stack, escapeStr)) {
                // スタックの一番上がエスケープ文字の場合
                StackUtils.popup(stack);
                sb.append(c);
                
            } else if(c == escapeChar) {
                // スタックに積む
                stack.push(String.valueOf(c));
                
            } else {
                sb.append(c);
            }
            
        }
        
        if(!stack.isEmpty()) {
            sb.append(StackUtils.popupAndConcat(stack));
        }
        
        return sb.toString();
        
    }
    
    /**
     * Listのインスタンスを他のCollectionのインスタンスに変換する。
     * <p>ただし、変換先のクラスタイプがインタフェースの場合は変換しない。
     * <p>変換元のクラスと変換先のクラスが同じ場合は、変換しない。
     * 
     * @since 1.0
     * @param list 変換元のListのインスタンス
     * @param toClass 変換先のCollectionのクラス
     * @param beanFactory てインスタンスを生成するファクトリクラス。
     * @return 変換したコレクションのインスタンス
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection convertListToCollection(final List list, final Class<Collection> toClass,
            final BeanFactory<Class<?>, Object> beanFactory) {
        
        if(list.getClass().equals(toClass)) {
            return list;
        }
        
        if(toClass.isInterface()) {
            if(List.class.isAssignableFrom(toClass)) {
                // 変換先がListの実態の場合はそのまま。
                return list;
                
            } else if(Set.class.isAssignableFrom(toClass)) {
                
                Collection value = (Collection) beanFactory.create(LinkedHashSet.class);
                value.addAll(list);
                return value;
                
            } else if(Queue.class.isAssignableFrom(toClass)) {
                
                Collection value = (Collection) beanFactory.create(LinkedList.class);
                value.addAll(list);
                return value;
                
            } else if(Collection.class.isAssignableFrom(toClass)) {
                Collection value = (Collection) beanFactory.create(ArrayList.class);
                value.addAll(list);
                return value;
                
            } else {
                throw new IllegalArgumentException("not support class type:" + toClass.getName());
            }
            
        }
        
        Collection value = (Collection) beanFactory.create(toClass);
        value.addAll(list);
        
        return value;
        
    }
    
    /**
     * CollectionのインスタンスをListに変換する。
     * 
     * @since 1.0
     * @param collection 変換元のCollectionのインスタンス。
     * @return 変換したListのインスタンス。
     */
    public static <T> List<T> convertCollectionToList(final Collection<T> collection) {
        
        if(List.class.isAssignableFrom(collection.getClass())) {
            return (List<T>)collection;
        }
        
        return new ArrayList<>(collection);
        
    }
    
    
    
}
