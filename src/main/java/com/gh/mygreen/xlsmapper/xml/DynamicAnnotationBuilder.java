package com.gh.mygreen.xlsmapper.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.gh.mygreen.xlsmapper.xml.bind.AnnotationInfo;

import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;


/**
 * Javassitを利用して、{@link Annotation}のインスタンスを動的に作成するクラス。
 * <p>独自のClassLoaderを設定することが可能。
 * <p>このクラスはシングルトンです。
 * 
 * @version 0.5
 * @author Naoki Takezoe
 * @author T.TSUCHIE
 *
 */
public class DynamicAnnotationBuilder {
    
    /**
     * シングルトンのインスタンス。
     */
    private static DynamicAnnotationBuilder INSTANCE = new DynamicAnnotationBuilder();
    
    private ClassLoader classLoader;
    
    private OgnlContext ognlContext;
    
    private DynamicAnnotationBuilder() {
        this.ognlContext = new OgnlContext();
        this.ognlContext.setMemberAccess(new DefaultMemberAccess(true));
        
    }
    
    /**
     * インスタンスを取得する。
     * @return
     */
    public static DynamicAnnotationBuilder getInstance() {
        return INSTANCE;
    }
    
    /**
     * アノテーションが定義された{@link ClassLoader}を設定する。
     * <p>nullの場合、標準のClassLoaderが使用される。
     * @param classLoader
     */
    public static void init(final ClassLoader classLoader) {
        getInstance().classLoader = classLoader;
    }
    
    /**
     * アノテーションが定義されたClassLoaderとJavaBeansを定義したClassLoaderを設定する。
     * @param classLoader アノテーションを見つけるために使用するClassLoader
     * @param propertyLoaders JavaBeansを見つけるために使用するClassLoader
     */
    public static void init(final ClassLoader classLoader, final ClassLoader[] propertyLoaders) {
        getInstance().classLoader = classLoader;
        
        if(propertyLoaders != null && propertyLoaders.length != 0) {
            Map<Integer, ClassLoader> loaderMap = new HashMap<>();
            for(ClassLoader propertyLoader : propertyLoaders) {
                loaderMap.put(propertyLoader.hashCode(), propertyLoader);
            }
            
            getInstance().ognlContext = new OgnlContext(loaderMap);
            getInstance().ognlContext.setMemberAccess(new DefaultMemberAccess(true));
            getInstance().ognlContext.setClassResolver(new MultipleLoaderClassResolver());
            
        }
    }
    
    /**
     * 指定したアノテーションのクラス情報から、アノテーションのインスタンスを組み立てる。
     * @param annoClass アノテーションのクラス
     * @param info アノテーションの情報
     * @return アノテーションのインスタンス。
     * @throws AnnotationReadException
     */
    public Annotation buildAnnotation(final Class<?> annoClass, final AnnotationInfo info) throws AnnotationReadException {
        
        final Map<String, Object> defaultValues = new HashMap<>();
        for(Method method : annoClass.getMethods()) {
            final Object defaultValue = method.getDefaultValue();
            if(defaultValue != null) {
                defaultValues.put(method.getName(), defaultValue);
            }
        }
        
        final Map<String, Object> xmlValues = new HashMap<>();
        for(String key : info.getAttributeKeys()) {
            try {
                Object value = Ognl.getValue(info.getAttribute(key), ognlContext, new Object());
                xmlValues.put(key, value);
            } catch(OgnlException e) {
                throw new AnnotationReadException(String.format("fail annotation attribute %s with ognl.", key), e);
            }
        }
        
        ClassLoader loader = classLoader;
        if(loader == null){
            loader = Thread.currentThread().getContextClassLoader();
        }
        
        Object obj = Proxy.newProxyInstance(loader, new Class[]{annoClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        String name = method.getName();
                        if (name.equals("annotationType")) {
                            return annoClass;
                        } else if(xmlValues.containsKey(name)){
                            return xmlValues.get(name);
                        } else {
                            return defaultValues.get(name);
                        }
                    }
        });
        
        return (Annotation) obj;
    }
    
}
