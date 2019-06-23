package com.gh.mygreen.xlsmapper.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.gh.mygreen.xlsmapper.Configuration;
import com.gh.mygreen.xlsmapper.XlsMapperException;
import com.gh.mygreen.xlsmapper.annotation.XlsTrim;
import com.gh.mygreen.xlsmapper.cellconverter.DefaultElementConverter;
import com.gh.mygreen.xlsmapper.cellconverter.ElementConverter;
import com.gh.mygreen.xlsmapper.fieldprocessor.ProcessCase;
import com.gh.mygreen.xlsmapper.validation.SheetBindingErrors;


/**
 * ユーティリティクラス。
 *
 * @version 2.0
 * @author T.TSUCHIE
 * @author Naoki Takezoe
 * @author Mitsuyoshi Hasegawa
 *
 */
public class Utils {

    @SuppressWarnings("rawtypes")
    private static final ElementConverter ELEMENT_CONVERTER = new DefaultElementConverter();

    /**
     * コレクションの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col 処理対象のコレクション。
     * @param separator 区切り文字。
     * @param ignoreEmptyElement 空、nullの要素を無視するかどうか。
     * @param trim トリムをするかどうか。
     * @param elementConverter 要素を変換するクラス。
     * @return 結合した文字列
     */
    @SuppressWarnings("rawtypes")
    public static String join(final Collection<?> col, final String separator,
            final boolean ignoreEmptyElement, final boolean trim, final ElementConverter elementConverter) {

        final List<Object> list = new ArrayList<Object>();
        for(Object element : col) {
            if(element == null) {
                continue;
            }

            Object value = element;

            if(element instanceof String) {
                String str = (String) element;
                if(ignoreEmptyElement && isEmpty(str)) {
                    continue;

                } else if(trim) {
                    value = str.trim();
                }

            } else if(element instanceof Character && isEmpty(element.toString())) {
                String str = element.toString();
                if(ignoreEmptyElement && isEmpty(str)) {
                    continue;

                } else if(trim) {
                    value = str.trim().charAt(0);
                }

            } else if(char.class.isAssignableFrom(element.getClass())) {
                String str = element.toString();
                if(ignoreEmptyElement && isEmpty(str)) {
                    continue;

                } else if(trim) {
                    value = str.trim().charAt(0);
                }
            }

            list.add(value);

        }

        return join(list, separator, elementConverter);

    }

    /**
     * 配列の要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param arrays 結合対象の配列
     * @param separator 区切り文字
     * @return 結合した文字列
     */
    public static String join(final Object[] arrays, final String separator) {

        return join(arrays, separator, ELEMENT_CONVERTER);

    }

    /**
     * 配列の要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param arrays 結合対象の配列
     * @param separator 区切り文字
     * @param elementConverter 要素を変換するクラス。
     * @return 結合した文字列
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String join(final Object[] arrays, final String separator, final ElementConverter elementConverter) {

        if(arrays == null) {
            return "";
        }

        final int len = arrays.length;
        if(len == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(int i=0; i < len; i++) {
            final Object element = arrays[i];
            sb.append(elementConverter.convertToString(element));

            if(separator != null && (i < len-1)) {
                sb.append(separator);
            }
        }

        return sb.toString();

    }

    /**
     * Collectionの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col 結合対象のコレクション
     * @param separator 区切り文字
     * @return 結合した文字列
     */
    public static String join(final Collection<?> col, final String separator) {
        return join(col, separator, ELEMENT_CONVERTER);
    }

    /**
     * Collectionの要素を指定した区切り文字で繋げて1つの文字列とする。
     * @param col 結合対象のコレクション
     * @param separator 区切り文字
     * @param elementConverter 要素を変換するクラス。
     * @return 結合した文字列
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String join(final Collection<?> col, final String separator, final ElementConverter elementConverter) {

        if(col == null) {
            return "";
        }

        final int size = col.size();
        if(size == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(Iterator<?> itr = col.iterator(); itr.hasNext();) {
            final Object element = itr.next();
            String text = elementConverter.convertToString(element);
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
     * @return true:ラベルが一致する。比較対象のラベルがnullの場合は、falseを返す。
     */
    public static boolean matches(final String text1, final String text2, final Configuration config){
        
        if(text1 == null || text2 == null) {
            return false;
        }
        
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
    public static String normalize(final String text, final Configuration config){
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
     * 文字列をトリムする。
     * @param value トリム対象の文字
     * @param trimmed トリムするかどうか。
     * @return トリミングした結果。
     */
    public static String trim(final String value, final boolean trimmed) {
        if(!trimmed || value == null) {
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
     * @param processCase 処理ケース
     * @throws XlsMapperException
     */
    public static void invokeNeedProcessMethod(final Object processObj, final Method method, final Object beanObj,
            final Sheet sheet, final Configuration config, final SheetBindingErrors<?> errors, final ProcessCase processCase)
                    throws XlsMapperException {

        final Class<?>[] paramTypes = method.getParameterTypes();
        final Object[] paramValues =  new Object[paramTypes.length];

        for(int i=0; i < paramTypes.length; i++) {
            if(Sheet.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = sheet;

            } else if(Configuration.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = config;

            } else if(SheetBindingErrors.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = errors;

            } else if(paramTypes[i].isAssignableFrom(beanObj.getClass())) {
                paramValues[i] = beanObj;

            } else if(ProcessCase.class.equals(paramTypes[i])) {
                paramValues[i] = processCase;

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

    /**
     * リストに要素のインデックスを指定して追加します。
     * <p>リストのサイズが足りない場合は、サイズを自動的に変更します。</p>
     * @since 2.0
     * @param list リスト
     * @param element 追加する要素。値はnullでもよい。
     * @param index 追加する要素のインデックス番号(0以上)
     * @throws IllegalArgumentException {@literal list == null.}
     * @throws IllegalArgumentException {@literal index < 0.}
     */
    public static <P> void addListWithIndex(final List<P> list, final P element, final int index) {
        ArgUtils.notNull(list, "list");
        ArgUtils.notMin(index, 0, "index");

        final int listSize = list.size();
        if(listSize < index) {
            // 足りない場合は、要素を追加する。
            final int lackSize = index - listSize;
            for(int i=0; i < lackSize; i++) {
                list.add(null);
            }
            list.add(element);

        } else if(listSize == index) {
            // 最後の要素に追加する
            list.add(element);

        } else {
            // リストのサイズが足りている場合
            list.set(index, element);
        }

    }

    /**
     * プリミティブ型のデフォルト値を取得します。
     * @param type 変換対象のクラスタイプ。
     * @return 対応していない型の場合は、nullを返します。
     * @throws IllegalArgumentException {@literal type is null.}
     */
    public static Object getPrimitiveDefaultValue(final Class<?> type) {
        ArgUtils.notNull(type, "type");

        if(!type.isPrimitive()) {
            return null;
        }

        if(type.equals(boolean.class)) {
            return false;

        } else if(type.equals(char.class)) {
            return '\u0000';

        } else if(type.equals(byte.class)) {
            return (byte)0;

        } else if(type.equals(short.class)) {
            return (short)0;

        } else if(type.equals(int.class)) {
            return 0;

        } else if(type.equals(long.class)) {
            return 0l;

        } else if(type.equals(float.class)) {
            return 0.0f;

        } else if(type.equals(double.class)) {
            return 0.0d;

        }

        return null;
    }

    /**
     * 配列を{@link List}に変換します。
     * プリミティブ型の配列をを考慮して処理します。
     * @param object 変換対象の配列
     * @param componentType 配列の要素のタイプ
     * @return 配列がnullの場合は、空のリストに変換します。
     * @throws IllegalArgumentException {@literal arrayが配列でない場合。componentTypeがサポートしていないプリミティブ型の場合。}
     */
    public static List<Object> asList(final Object object, final Class<?> componentType) {
        ArgUtils.notNull(componentType, "componentType");

        if(object == null) {
            return new ArrayList<>();
        }

        if(!object.getClass().isArray()) {
            throw new IllegalArgumentException(String.format("args0 is not arrays : %s.", object.getClass().getName()));
        }

        if(!componentType.isPrimitive()) {
            return Arrays.asList((Object[])object);
        }

        if(componentType.equals(boolean.class)) {
            boolean[] array = (boolean[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(boolean v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(char.class)) {
            char[] array = (char[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(char v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(byte.class)) {
            byte[] array = (byte[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(byte v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(short.class)) {
            short[] array = (short[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(short v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(int.class)) {
            int[] array = (int[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(int v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(long.class)) {
            long[] array = (long[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(long v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(float.class)) {
            float[] array = (float[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(float v : array) {
                list.add(v);
            }
            return list;

        } else if(componentType.equals(double.class)) {
            double[] array = (double[])object;
            List<Object> list = new ArrayList<>(array.length);
            for(double v : array) {
                list.add(v);
            }
            return list;

        }

        throw new IllegalArgumentException(String.format("not support primitive type : %s.", componentType.getName()));

    }

    /**
     * コレクションを配列に変換する。
     * @param collection 変換対象のコレクション。
     * @return 変換した配列。
     * @throws IllegalArgumentException {@literal collection is null.}
     */
    public static int[] toArray(final Collection<Integer> collection) {
        ArgUtils.notNull(collection, "collection");

        final int size = collection.size();
        final int[] array = new int[size];

        int i=0;
        for(Integer value : collection) {
            array[i] = value;
            i++;
        }

        return array;
    }

    /**
     * 配列のサイズを取得します。
     * プリミティブ型の配列をを考慮して処理します。
     * @param object 取得対象の配列
     * @param componentType 配列の要素のタイプ
     * @return 配列がnullの場合は、0を返します。
     * @throws IllegalArgumentException {@literal arrayが配列でない場合。componentTypeがサポートしていないプリミティブ型の場合。}
     */
    public static int getArraySize(final Object object, final Class<?> componentType) {

        ArgUtils.notNull(componentType, "componentType");

        if(object == null) {
            return 0;
        }

        if(!object.getClass().isArray()) {
            throw new IllegalArgumentException(String.format("args0 is not arrays : %s.", object.getClass().getName()));
        }

        if(!componentType.isPrimitive()) {
            return ((Object[])object).length;
        }

        if(componentType.equals(boolean.class)) {
            return ((boolean[]) object).length;

        } else if(componentType.equals(char.class)) {
            return ((char[]) object).length;

        } else if(componentType.equals(byte.class)) {
            return ((byte[]) object).length;

        } else if(componentType.equals(short.class)) {
            return ((short[]) object).length;

        } else if(componentType.equals(int.class)) {
            return ((int[]) object).length;

        } else if(componentType.equals(long.class)) {
            return ((long[]) object).length;

        } else if(componentType.equals(float.class)) {
            return ((byte[]) object).length;

        } else if(componentType.equals(double.class)) {
            return ((byte[]) object).length;

        }

        throw new IllegalArgumentException(String.format("not support primitive type : %s.", componentType.getName()));
    }

    /**
     * 文字列配列の結合
     * @param array1
     * @param array2
     * @return 結合した配列。引数のどちらからnullの場合は、cloneした配列を返します。
     */
    public static String[] concat(final String[] array1, final String[] array2) {

        if(array1 == null || array1.length == 0) {
            return clone(array2);

        } else if(array2 == null || array2.length == 0) {
            return clone(array1);
        }

        final String[] joinedArray = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;

    }

    /**
     * 文字列の配列をクローンします。
     * @param array クローン対象の配列
     * @return クローンした配列。引数がnullの場合は、nullを返します。
     */
    public static String[] clone(final String[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 読み込み処理のケースか判定する。
     * <p>ケースが指定されていないときは、該当すると判定する。</p>
     * @since 2.0
     * @param cases 判定対象のケース
     * @return trueのとき、読み込み対象と判定する。
     */
    public static boolean isLoadCase(final ProcessCase[] cases) {

        if(cases == null || cases.length == 0) {
            return true;
        }

        for(ProcessCase pc : cases) {
            if(pc == ProcessCase.Load) {
                return true;
            }
        }

        return false;

    }

    /**
     * 書き込み処理のケースか判定する。
     * <p>ケースが指定されていないときは、該当すると判定する。</p>
     * @since 2.0
     * @param cases 判定対象のケース
     * @return trueのとき、書き込み対象と判定する。
     */
    public static boolean isSaveCase(final ProcessCase[] cases) {

        if(cases == null || cases.length == 0) {
            return true;
        }

        for(ProcessCase pc : cases) {
            if(pc == ProcessCase.Save) {
                return true;
            }
        }

        return false;

    }

    /**
     * 現在の処理ケースが該当するか判定する。
     * <p>ケースが指定されていないときは、該当すると判定する。</p>
     * @param currentCase 現在の処理ケース
     * @param cases 判定対象のケース
     * @return trueのとき判定対象。
     * @throws IllegalArgumentException {@code currentCase is null.}
     */
    public static boolean isProcessCase(final ProcessCase currentCase, ProcessCase[] cases) {

        ArgUtils.notNull(currentCase, "currentCase");

        if(currentCase == ProcessCase.Load) {
            return isLoadCase(cases);
        } else if(currentCase == ProcessCase.Save) {
            return isSaveCase(cases);
        } else {
            throw new IllegalArgumentException("currentCase is not support:" + currentCase);
        }

    }


}
