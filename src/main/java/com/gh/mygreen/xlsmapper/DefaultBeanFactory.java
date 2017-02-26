package com.gh.mygreen.xlsmapper;

import java.lang.reflect.Constructor;


/**
 * Beanのインスタンスを生成する標準のクラス。
 *
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
public class DefaultBeanFactory implements BeanFactory<Class<?>, Object> {
    
    @Override
    public Object create(final Class<?> clazz) {
        try {
            Constructor<?> cons = clazz.getDeclaredConstructor();
            cons.setAccessible(true);
            return cons.newInstance();
        } catch (ReflectiveOperationException  e) {
            throw new RuntimeException(String.format("fail create Bean instance of '%s'", clazz.getName()), e);
        }
    }
}
