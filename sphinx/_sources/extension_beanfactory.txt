--------------------------------------------------------
BeanFactoryの拡張
--------------------------------------------------------

XlsMapperに読み込み時など、Beanのインスタンスを生成します。

あまりないかもしれませんが、BeanをSpringFrameworkのコンテナから取得する場合など、生成処理を切り替えることで可能となります。

* Beanの生成処理を切り替えるには、インタフェース ``com.gh.mygreen.xlsmapper.FactoryCallback`` を実装したものを、XlsMapperConfigに渡します。
* 標準では、``com.gh.mygreen.xlsmapper.DefaultBeanFactory`` が設定されています。
* Springから生成するシートやレコードのBeanクラスは、スコープはprototypeにします。


SpringコンテナからBeanを生成するSpringBeanFactoryを作成します。
Springコンテナ管理外のクラスにも、インジェクションできるようにもします。

.. sourcecode:: java
    
    // SpringのコンテナからBeanを作成するFactoryCallsbackの実装
    // Sprijgコンテナに登録しておく。
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



作成したSpringBeanFactoryをSpringコンテナに登録します。

.. sourcecode:: xml

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
            http://www.springframework.org/schema/aop
            http://www.springframework.org/schema/aop/spring-aop-3.2.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context-3.2.xsd
        ">
        
        <!-- AutowiredのScan有効定義 -->
        <context:annotation-config/>
        <context:component-scan base-package="com.gh.mygreen.xlsmapper.spring" />
        
        <!-- 作成したSpringBeanFactoryの登録 -->
        <bean id="springBeanFactory" class="com.gh.mygreen.xlsmapper.spring.SpringBeanFactory" />
        

    </beans>


Springコンテナに登録するBeanのScopeはprototypeにします。
ライフサイクル・コールバック用のアノテーションを付与したメソッド内でインジェクションしたクラスなどを呼び出したりします。

.. sourcecode:: java

    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @Component
    @XlsSheet(name="Spring管理のBean")
    public class SampleSheet {
        
        /** SpringBeanをインジェクションする */
        @Autowired
        private SampleService sampleService;
        
        @XlsHorizontalRecords(tableLabel="一覧")
        private List<SampleRecord> records;
        
        /** 読み込み後に処理を実行する */
        @XlsPostLoad
        public void onLoad() {
            
            sampleService.doService();
            
        }

作成したSpringBeanFactoryをXlsMapperConfigに設定して、シートを読み込みます。

.. sourcecode:: java
    
    // 自作したSpringBeanFactory
    @Autorired
    SpringBeanFactory springBeanFacetory;
    
    public void doLoad() {
        // FacetoryBeanの実装を独自のものに変更する。
        XlsMapperConfig config = new XlsMapperConfig();
        config.setBeanFactory(springBeanFactory);
        
        XlsMapper mapper = new XlsMapper();
        mapper.setConig(config);
        
        SampleSheet sheet = mapper.load(...);
    }




