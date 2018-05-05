package com.gh.mygreen.xlsmapper.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gh.mygreen.xlsmapper.util.PropertyPath.Token;


/**
 * プロパティにアクセスするためのクラス。
 * <p>プロパティは式言語の形式に似た形式をとることが可能で、フィールドにもアクセスできます。</p>
 *
 * <h3 class="description">基本的な使い方</h3>
 *
 * <pre class="highlight"><code class="java">
 * // Beanのインスタンスの生成
 * Person taregetObj = ...;
 *
 * // 名前を指定してプロパティにアクセスする場合
 * String name = PropertyValueNavigator.getProperty(targetObj, "name");
 *
 * // ネストしたプロパティにアクセスする場合、ピリオド（.)で繋げます。
 * String code = PropertyValueNavigator.getProperty(targetObj, "county.code");
 *
 * // リストまたは配列にアクセスする場合、括弧[インデックス番号]でインデックスを指定します。
 * String title = PropertyValueNavigator.getProperty(targetObj, "books[0].title");
 *
 * // マップにアクセスする場合、括弧[キー]でキーを指定します。
 * String motherName = PropertyValueNavigator.getProperty(targetObj, "family[mother].name");
 * </code></pre>
 *
 * <h3 class="description">オプションの指定</h3>
 * <p>オプションを指定する場合は、PropertyNavigatorのインスタンスを生成し、設定します。
 *
 * <pre class="highlight"><code class="java">
 * PropertyValueNavigator navigator = new PropertyValueNavigator();
 *
 * // 非公開のプロパティにアクセス可能かどうか
 * navigator.setAllowPrivate(true);
 *
 * // 値がnullの場合に例外をスローしないでその時点で処理を終了するかどうか。
 * navigator.setIgnoreNull(true);
 *
 * // 配列/Collection/Mapにアクセスする際に指定したキーの要素が存在しない場合に処理を終了するかどうか。
 * navigator.setIgnoreNotFoundKey(true);
 *
 * // プロパティの解析結果をキャッシュするかどうか。途中の解析結果をキャッシュして、処理を高速化します。
 * navigator.setCacheWithPath(true);
 * </code></pre>
 *
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class PropertyValueNavigator {

    /**
     * 非公開のプロパティにアクセス可能かどうか。
     */
    private boolean allowPrivate;

    /**
     * 値がnullの場合に例外をスローしないでその時点で処理を終了するかどうか。
     */
    private boolean ignoreNull;

    /**
     * 配列/Collection/Mapにアクセスする際に指定したキーの要素が存在しない場合に処理を終了するかどうか。
     */
    private boolean ignoreNotFoundKey;

    /**
     * プロパティの解析結果をキャッシュするかどうか。
     */
    private boolean cacheWithPath;

    /**
     * プロパティの式を解析して、トークンに分解するクラス。
     */
    private final PropertyPathTokenizer tokenizer = new PropertyPathTokenizer();

    /**
     * プロパティの解析結果をキャッシュデータ
     */
    private final Map<String, PropertyPath> cacheData = new ConcurrentHashMap<>();

    /**
     * プロパティの値を取得する。
     * <p>オプションはデフォルト値で処理する。</p>
     * @param obj 取得元となるオブジェクト。
     * @param property プロパティ名。
     * @return
     * @throws IllegalArgumentException peropety is null or empty.
     * @throws PropertyAccessException 存在しないプロパティを指定した場合など。
     * @throws NullPointerException アクセスしようとしたプロパティがnullの場合。
     * @throws IndexOutOfBoundsException リスト、配列にアクセスする際に存在しないインデックスにアクセスした場合。
     * @throws IllegalStateException マップにアクセスする際に存在しないキーにアクセスした場合。
     */
    public static Object get(final Object obj, final String property) {
        return new PropertyValueNavigator().getProperty(obj, property);
    }

    /**
     * デフォルトコンストラクタ
     */
    public PropertyValueNavigator() {

    }

    /**
     * プロパティの値を取得する。
     *
     * @param obj 取得元となるオブジェクト。
     * @param property プロパティの式。
     * @return プロパティの値。
     * @throws IllegalArgumentException peropety is null or empty.
     * @throws PropertyAccessException 存在しないプロパティを指定した場合など。
     * @throws NullPointerException アクセスしようとしたプロパティがnullの場合。
     *         ただし、オプション ignoreNull = falseのとき。
     * @throws IndexOutOfBoundsException リスト、配列にアクセスする際に存在しないインデックスにアクセスした場合。
     *         ただし、オプションignoreNotFoundKey = falseのとき。
     * @throws IllegalStateException マップにアクセスする際に存在しないキーにアクセスした場合。
     *         ただし、オプションignoreNotFoundKey = falseのとき。
     */
    public Object getProperty(final Object obj, final String property) {

        ArgUtils.notEmpty(property, "property");

        final PropertyPath path = parseProperty(property);
        Object targetObj = obj;
        for(Token token : path.getPathAsToken()) {

            targetObj = accessProperty(targetObj, token);
            if(targetObj == null && ignoreNull) {
                return null;
            }

        }
        return targetObj;

    }

    private PropertyPath parseProperty(final String property) {

        if(isCacheWithPath()) {
            return cacheData.computeIfAbsent(property, k -> tokenizer.parse(property));
        } else {
            return tokenizer.parse(property);
        }

    }

    /**
     * 今までのキャッシュデータをクリアする。
     */
    public void clearCache() {
        this.cacheData.clear();
    }

    private Object accessProperty(final Object targetObj, final Token token) {

        if(token instanceof Token.Separator) {
            return accessPropertyBySeparator(targetObj, (Token.Separator)token);

        } else if(token instanceof Token.Name) {
            return accessPropertyByName(targetObj, (Token.Name)token);

        } else if(token instanceof Token.Key) {
            return accessPropertyByKey(targetObj, (Token.Key)token);
        }

        throw new IllegalStateException("not support token type : " + token.getValue());

    }

    private Object accessPropertyBySeparator(final Object targetObj, final Token.Separator token) {

        return targetObj;

    }

    private Object accessPropertyByName(final Object targetObj, final Token.Name token) {

        if(targetObj == null) {
            if(ignoreNull) {
                return null;
            } else {
                throw new NullPointerException("access object is null. property name = " + token.getValue());
            }

        }

        final Class<?> targetClass = targetObj.getClass();

        // メソッドアクセス
        final String getterMethodName = "get" + Utils.capitalize(token.getValue());
        try {
            Method getterMethod = allowPrivate ?
                    targetClass.getDeclaredMethod(getterMethodName) : targetClass.getMethod(getterMethodName);
            getterMethod.setAccessible(true);

            return getterMethod.invoke(targetObj);

        } catch (NoSuchMethodException | SecurityException e) {
            // not found method

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // 値の取得に失敗
            throw new PropertyAccessException("fail access method property : " + token.getValue(), e);
        }

        // boolean用メソッドアクセス
        final String booleanMethodName = "is" + Utils.capitalize(token.getValue());
        try {
            Method getterMethod = allowPrivate ?
                    targetClass.getDeclaredMethod(booleanMethodName) : targetClass.getMethod(booleanMethodName);;
            getterMethod.setAccessible(true);

            return getterMethod.invoke(targetObj);

        } catch (NoSuchMethodException | SecurityException e) {
            // not found method

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // 値の取得に失敗
            throw new PropertyAccessException("fail access boolean method property : " + token.getValue(), e);
        }

        // フィールドアクセス
        final String fieldName = token.getValue();
        try {
            Field field = allowPrivate ?
                    targetClass.getDeclaredField(fieldName) : targetClass.getField(fieldName);
            field.setAccessible(true);

            return field.get(targetObj);

        } catch (NoSuchFieldException | SecurityException e) {
            // not found field

        } catch (IllegalArgumentException | IllegalAccessException e) {
            // 値の取得に失敗
            throw new PropertyAccessException("fail access field property : " + token.getValue(), e);
        }

        throw new PropertyAccessException("not found property : " + token.getValue());

    }

    private Object accessPropertyByKey(final Object targetObj, final Token.Key token) {

        if(targetObj == null) {
            if(ignoreNull) {
                return null;
            } else {
                throw new NullPointerException("access object is null. property key = " + token.getValue());
            }

        }

        final Class<?> targetClass = targetObj.getClass();

        if(Collection.class.isAssignableFrom(targetClass)) {
            final int index;
            try {
                index = Integer.parseInt(token.getKey().trim());
            } catch (NumberFormatException e) {
                throw new PropertyAccessException("wrong key value :" + token.getValue());
            }

            final List<?> list = Utils.convertCollectionToList((Collection<?>) targetObj);
            if((index < 0 || index >= list.size()) && ignoreNotFoundKey) {
                return null;
            }

            return list.get(index);

        } else if (targetClass.isArray()) {
            final int index;
            try {
                index = Integer.parseInt(token.getKey().trim());
            } catch (NumberFormatException e) {
                throw new PropertyAccessException("wrong key value :" + token.getValue());
            }


            final List<?> list = Utils.asList(targetObj, targetClass.getComponentType());
            if((index < 0 || index >= list.size()) && ignoreNotFoundKey) {
                return null;
            }

            return list.get(index);

        } else if(Map.class.isAssignableFrom(targetClass)) {

            String strKey = token.getKey();
            Map<?, ?> map = (Map<?, ?>) targetObj;
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                if(entry.getKey().toString().equals(strKey)) {
                    return entry.getValue();
                }
            }

            // 存在するキーがない場合
            if(ignoreNotFoundKey) {
                return null;
            }

            throw new IllegalStateException("not found map key : " + strKey);

        }

        throw new PropertyAccessException("not support key access : " + targetClass.getName());

    }

    /**
     * 実行時オプション - 非公開のプロパティにアクセス可能かどうか。
     * @return true: アクセスを許可する。false: デフォルト値。
     */
    public boolean isAllowPrivate() {
        return allowPrivate;
    }

    /**
     * 実行時オプション - 非公開のプロパティにアクセス可能かどうか。
     * @param allowPrivate true: アクセスを許可する。
     */
    public void setAllowPrivate(boolean allowPrivate) {
        this.allowPrivate = allowPrivate;
    }

    /**
     * 実行時オプション - 値がnullの場合に例外をスローしないでその時点で処理を終了するかどうか。
     * @return true: その時点で処理を終了する。false: デフォルト値。
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * 実行時オプション - 値がnullの場合に例外をスローしないでその時点で処理を終了するかどうか。
     * @param ignoreNull true: その時点で処理を終了する。
     */
    public void setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    /**
     * 実行時オプション - 配列/Collection/Mapにアクセスする際に指定したキーの要素が存在しない場合に処理を終了するかどうか。
     * @return true:その時点で処理を終了する。false: デフォルト値。
     */
    public boolean isIgnoreNotFoundKey() {
        return ignoreNotFoundKey;
    }

    /**
     * 実行時オプション - 配列/Collection/Mapにアクセスする際に指定したキーの要素が存在しない場合に処理を終了するかどうか。
     * @param ignoreNotFoundKey true:その時点で処理を終了する。
     */
    public void setIgnoreNotFoundKey(boolean ignoreNotFoundKey) {
        this.ignoreNotFoundKey = ignoreNotFoundKey;
    }

    /**
     * プロパティの解析結果をキャッシュするかどうか。
     * @return true: キャッシュする。false: デフォルト値。
     */
    public boolean isCacheWithPath() {
        return cacheWithPath;
    }

    /**
     * プロパティの解析結果をキャッシュするかどうか。
     * @param cacheWithPath true: キャッシュする。
     */
    public void setCacheWithPath(boolean cacheWithPath) {
        this.cacheWithPath = cacheWithPath;
    }

}
