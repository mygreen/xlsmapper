package com.gh.mygreen.xlsmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import com.github.mygreen.cellformatter.lang.ArgUtils;


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

        ArgUtils.notNull(clazz, "clazz");

        try {
            final Class<?> declaredClass = clazz.getDeclaringClass();

            if(declaredClass != null && !Modifier.isStatic(clazz.getModifiers())) {
                // 非staticな内部クラスの場合
                Constructor<?> cons = clazz.getDeclaredConstructor(declaredClass);
                cons.setAccessible(true);
                return cons.newInstance((Object)null);
            }

            Constructor<?> cons = clazz.getDeclaredConstructor();
            cons.setAccessible(true);
            return cons.newInstance();
        } catch (ReflectiveOperationException  e) {
            throw new RuntimeException(String.format("fail create Bean instance of '%s'", clazz.getName()), e);
        }
    }

}
