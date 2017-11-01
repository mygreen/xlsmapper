package com.gh.mygreen.xlsmapper.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gh.mygreen.xlsmapper.util.PropertyPath.Token;

/**
 * クラス定義からプロパティのクラスタイプを取得する。
 * <p>プロパティは式言語の形式に似た形式をとることが可能で、フィールドにもアクセスできます。</p>
 * 
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PropertyTypeNavigator {
    
    /**
     * 非公開のプロパティにアクセス可能かどうか。
     */
    private boolean allowPrivate;
    
    /**
     * クラスタイプの解析ができない場合に処理を終了するかどうか。
     */
    private boolean ignoreNotResolveType;
    
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
     * @param rootClass 取得元となるクラス
     * @param property プロパティの式。
     * @return プロパティのクラスタイプ。
     * @throws IllegalArgumentException peropety is null or empty.
     * @throws PropertyAccessException 存在しないプロパティを指定した場合など。
     * @throws IllegalStateException リストやマップにアクセスする際にGenericsタイプが設定されておらずクラスタイプが取得できない場合。
     *         ただし、オプションignoreNotResolveType = falseのとき。
     */
    public static Object get(final Class<?> rootClass, final String property) {
        return new PropertyTypeNavigator().getPropertyType(rootClass, property);
    }
    
    /**
     * デフォルトコンストラクタ
     */
    public PropertyTypeNavigator() {
        
    }
    
    /**
     * プロパティの値を取得する。
     * 
     * @param rootClass 取得元となるクラス
     * @param property プロパティの式。
     * @return プロパティのクラスタイプ。
     * @throws IllegalArgumentException peropety is null or empty.
     * @throws PropertyAccessException 存在しないプロパティを指定した場合など。
     * @throws IllegalStateException リストやマップにアクセスする際にGenericsタイプが設定されておらずクラスタイプが取得できない場合。
     *         ただし、オプションignoreNotResolveType = falseのとき。
     */
    public Class<?> getPropertyType(final Class<?> rootClass, final String property) {
        
        ArgUtils.notEmpty(property, "property");
        
        final PropertyPath path = parseProperty(property);
        final LinkedList<Object> stack = new LinkedList<>();
        Class<?> targetClass = rootClass;
        for(Token token : path.getPathAsToken()) {
            
            targetClass = accessProperty(targetClass, token, stack);
            if(targetClass == null) {
                return null;
            }
        }
        
        return targetClass;
        
    }
    
    /**
     * プロパティの式をパースして、{@link PropertyPath} オブジェクトに変換する。
     * @param property プロパティアクセス用の式
     * @return 式を解析した結果 {@link PropertyPath}
     */
    private PropertyPath parseProperty(final String property) {
        
        if(isCacheWithPath()) {
            return cacheData.computeIfAbsent(property, k -> tokenizer.parse(k));
        } else {
            return tokenizer.parse(property);
        }
        
    }
    
    private Class<?> accessProperty(final Class<?> targetClass, final Token token, final LinkedList<Object> stack) {
        
        if(token instanceof Token.Separator) {
            return accessPropertyBySeparator(targetClass, (Token.Separator)token, stack);
            
        } else if(token instanceof Token.Name) {
            return accessPropertyByName(targetClass, (Token.Name)token, stack);
            
        } else if(token instanceof Token.Key) {
            return accessPropertyByKey(targetClass, (Token.Key)token, stack);
        }
        
        throw new IllegalStateException("not support token type : " + token.getValue());
        
    }
    
    private Class<?> accessPropertyBySeparator(final Class<?> targetClass, final Token.Separator token, final LinkedList<Object> stack) {
        
        return targetClass;
        
    }
    
    private Class<?> accessPropertyByName(final Class<?> targetClass, final Token.Name token, final LinkedList<Object> stack) {
        
        // メソッドアクセス
        final String getterMethodName = "get" + Utils.capitalize(token.getValue());
        try {
            Method getterMethod = allowPrivate ? 
                    targetClass.getDeclaredMethod(getterMethodName) : targetClass.getMethod(getterMethodName);
            getterMethod.setAccessible(true);
            
            // Collectionなどの場合、メソッド情報をスタックに積んでおく
            stack.push(getterMethod);
            
            return getterMethod.getReturnType();
            
        } catch (NoSuchMethodException | SecurityException e) {
            // not found method
            
        }
        
        // boolean用メソッドアクセス
        final String booleanMethodName = "is" + Utils.capitalize(token.getValue());
        try {
            Method getterMethod = allowPrivate ? 
                    targetClass.getDeclaredMethod(booleanMethodName) : targetClass.getMethod(booleanMethodName);;
            getterMethod.setAccessible(true);
            
            return getterMethod.getReturnType();
            
        } catch (NoSuchMethodException | SecurityException e) {
            // not found method
            
        }
        
        // フィールドアクセス
        final String fieldName = token.getValue();
        try {
            Field field = allowPrivate ? 
                    targetClass.getDeclaredField(fieldName) : targetClass.getField(fieldName);
            field.setAccessible(true);
            
            // Collectionなどの場合、メソッド情報をスタックに積んでおく
            stack.push(field);
            
            return field.getType();
            
        } catch (NoSuchFieldException | SecurityException e) {
            // not found field
            
        }
        
        throw new PropertyAccessException("not found property : " + token.getValue());
        
    }
    
    private Class<?> accessPropertyByKey(final Class<?> targetClass, final Token.Key token, final LinkedList<Object> stack) {
        
        final Object parent = stack.isEmpty() ? null : stack.pollFirst();
        if(parent == null) {
            return null;
        }
        if(Collection.class.isAssignableFrom(targetClass)) {
            
            Type argType = null;
            if(parent instanceof Method) {
                // getterメソッドのからGenericsのタイプを取得する
                Type type = ((Method)parent).getGenericReturnType();
                if(!ParameterizedType.class.isAssignableFrom(type.getClass())) {
                    if(ignoreNotResolveType) {
                        return null;
                    } else {
                        throw new IllegalStateException("not resolve generics type with property : " + token.getValue());
                    }
                }
                argType = ((ParameterizedType)type).getActualTypeArguments()[0];
                
            } else if(parent instanceof Field) {
                Type type = ((Field)parent).getGenericType();
                if(!ParameterizedType.class.isAssignableFrom(type.getClass())) {
                    if(ignoreNotResolveType) {
                        return null;
                    } else {
                        throw new IllegalStateException("not resolve generics type with property : " + token.getValue());
                    }
                }
                
                argType = ((ParameterizedType)type).getActualTypeArguments()[0];
                
            } else if(parent instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) parent;
                argType = type.getActualTypeArguments()[0];
                
            } else {
                if(ignoreNotResolveType) {
                    return null;
                } else {
                    throw new IllegalStateException("not resolve generics type with property : " + token.getValue());
                }
            }
            
            if(argType == null) {
                return null;
            }
            
            if(argType instanceof Class) {
                return (Class<?>)argType;
                
            } else if(argType instanceof ParameterizedType) {
                ParameterizedType paramType = ((ParameterizedType)argType);
                stack.push(paramType);
                return (Class<?>)paramType.getRawType();
            }
            
        } else if (targetClass.isArray()) {
            
            return targetClass.getComponentType();
            
        } else if(Map.class.isAssignableFrom(targetClass)) {
            
            Type argType = null;
            if(parent instanceof Method) {
                // getterメソッドのからGenericsのタイプを取得する
                Type type = ((Method)parent).getGenericReturnType();
                if(!ParameterizedType.class.isAssignableFrom(type.getClass())) {
                    if(ignoreNotResolveType) {
                        return null;
                    } else {
                        throw new IllegalStateException("not resolve generics type with property : " + token.getValue());
                    }
                }
                argType = ((ParameterizedType)type).getActualTypeArguments()[1];
                
            } else if(parent instanceof Field) {
                Type type = ((Field)parent).getGenericType();
                if(!ParameterizedType.class.isAssignableFrom(type.getClass())) {
                    if(ignoreNotResolveType) {
                        return null;
                    } else {
                        throw new IllegalStateException("not resolve generics type with property : " + token.getValue());
                    }
                }
                argType = ((ParameterizedType)type).getActualTypeArguments()[1];
                
            } else if(parent instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) parent;
                argType = type.getActualTypeArguments()[1];
                
            } else {
                if(ignoreNotResolveType) {
                    return null;
                } else {
                    throw new IllegalStateException("not resolve generics type with property : " + token.getValue());
                }
            }
            
            if(argType == null) {
                return null;
            }
            
            if(argType instanceof Class) {
                return (Class<?>)argType;
                
            } else if(argType instanceof ParameterizedType) {
                ParameterizedType paramType = ((ParameterizedType)argType);
                stack.push(paramType);
                return (Class<?>)paramType.getRawType();
            }
            
        }
        
        throw new PropertyAccessException("not support key access : " + targetClass.getName());
        
    }
    
    /**
     * 今までのキャッシュデータをクリアする。
     */
    public void clearCache() {
        this.cacheData.clear();
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
     * クラスタイプの解析ができない場合に処理を終了するかどうか。
     * <p>ListなどでGenericsタイプが指定されていでクラスタイプが取得できないときに、nullを返すかどうか指定します。</p>
     * @return true:その時点で処理を終了する。
     */
    public boolean isIgnoreNotResolveType() {
        return ignoreNotResolveType;
    }
    
    /**
     * クラスタイプの解析ができない場合に処理を終了するかどうか。
     * <p>ListなどでGenericsタイプが指定されていでクラスタイプが取得できないときに、nullを返すかどうか指定します。</p>
     * @param ignoreNotResolveType true:その時点で処理を終了する。
     */
    public void setIgnoreNotResolveType(boolean ignoreNotResolveType) {
        this.ignoreNotResolveType = ignoreNotResolveType;
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
