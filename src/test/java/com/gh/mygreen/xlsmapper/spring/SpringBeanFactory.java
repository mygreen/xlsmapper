package com.gh.mygreen.xlsmapper.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.gh.mygreen.xlsmapper.FactoryCallback;
import com.gh.mygreen.xlsmapper.Utils;


/**
 * SpringのコンテナからBeanを作成するFactoryCallsbackの実装
 *
 */
public class SpringBeanFactory implements FactoryCallback<Class<?>,
        Object>, ApplicationContextAware, InitializingBean  {
    
    private AutowireCapableBeanFactory beanFactory;
    
    private ApplicationContext applicationContext;
    
    @Override
    public Object create(final Class<?> clazz) {
        
        Assert.notNull(clazz, "bean clazz should not be null.");
        
        String beanName = Utils.uncapitalize(clazz.getSimpleName());
        if(beanFactory.containsBean(beanName)) {
            // Spring管理のクラスの場合
            return beanFactory.getBean(beanName, clazz);
            
        } else {
            // 通常のBeanクラスの場合
            Object obj;
            try {
                obj = clazz.newInstance();
            } catch (ReflectiveOperationException  e) {
                throw new RuntimeException(String.format("fail create Bean instance of '%s'", clazz.getName()), e);
            }
            
            // Springコンテナ管理外でもインジェクションする。
            beanFactory.autowireBean(obj);
            
            return obj;
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if(applicationContext != null && beanFactory == null) {
            this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
        }
        
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        
    }

}
