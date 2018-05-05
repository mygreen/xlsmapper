package com.gh.mygreen.xlsmapper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.gh.mygreen.xlsmapper.util.Utils;


/**
 * SpringのコンテナからBeanを作成するための{@link BeanFactory} の実装。
 * <p>利用するには、このクラスをSpringのコンテナに登録しておく必要があります。</p>
 * <p>Springのコンテナに登録されていないクラスは、通常のクラスとしてインスタンスを作成します。
 *  <br>ただし、コンテナ管理外のクラスに対しても、アノテーション{@link Autowired}によるインジェクションが可能です。
 * </p>
 *
 * @since 2.0
 * @author T.tSUCHIE
 */
public class SpringBeanFactory implements BeanFactory<Class<?>, Object>, ApplicationContextAware, InitializingBean  {

    private AutowireCapableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    private BeanFactory<Class<?>, Object> defaultBeanFactory = new DefaultBeanFactory();

    @Override
    public Object create(final Class<?> clazz) {

        Assert.notNull(clazz, "clazz should not be null.");

        final String beanName = getBeanName(clazz);
        if(beanFactory.containsBean(beanName)) {
            // Spring管理のクラスの場合
            return beanFactory.getBean(beanName, clazz);

        } else {
            // 通常のBeanクラスの場合
            Object obj = defaultBeanFactory.create(clazz);

            // Springコンテナ管理外でもインジェクションする。
            beanFactory.autowireBean(obj);

            return obj;
        }
    }

    /**
     * クラスタイプから、候補となるSpringBean名を取得する
     * @param targetClass SpringBeanのクラスタイプ
     * @return SpringBean名
     */
    private String getBeanName(final Class<?> targetClass) {

        final Component componentAnno = targetClass.getAnnotation(Component.class);
        if(componentAnno != null && !componentAnno.value().isEmpty()) {
            return componentAnno.value();
        }

        final Service serviceAnno = targetClass.getAnnotation(Service.class);
        if(serviceAnno != null && !serviceAnno.value().isEmpty()) {
            return serviceAnno.value();
        }

        final Repository repositoryAnno = targetClass.getAnnotation(Repository.class);
        if(repositoryAnno != null && !repositoryAnno.value().isEmpty()) {
            return repositoryAnno.value();
        }

        final Controller controllerAnno = targetClass.getAnnotation(Controller.class);
        if(controllerAnno != null && !controllerAnno.value().isEmpty()) {
            return controllerAnno.value();
        }

        // ステレオタイプのアノテーションでBean名の指定がない場合は、クラス名の先頭を小文字にした名称とする。
        String simpleName = Utils.uncapitalize(targetClass.getSimpleName());

        String[] names = applicationContext.getBeanNamesForType(targetClass);
        if(names.length == 0) {
            return simpleName;
        }

        // 複数存在する場合、候補の中から一番シンプルな形式の名前で一致するものを選択する
        for(String name : names) {
            if(name.equals(simpleName)) {
                return name;
            }
        }

        // 見つからない場合は、候補の先頭のものにする。
        return names[0];
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(applicationContext != null && beanFactory == null) {
            this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
        }

    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
