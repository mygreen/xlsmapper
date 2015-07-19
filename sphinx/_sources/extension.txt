======================================
拡張方法
======================================

--------------------------------------------------------
FieldProcessorの拡張
--------------------------------------------------------

Excelのシートを独自の基準で走査して、Javaクラスにマッピングする、 FieldProcessor を実装することができます。

* 読み込み時用のインタフェース ``com.gh.mygreen.xlsmapper.fieldprocessor.LoadingFieldProcessor`` と、書き込み時のインタフェース ``com.gh.mygreen.xlsmapper.fieldprocessor.SavingFieldProcessor`` が分かれており、必要なものを実装します。

    * 読み込みと書き込み処理を同時に実装する際には、抽象クラス「com.gh.mygreen.xlsmapper.fieldprocessor.AbstractFieldProcessor」を継承すると便利です。
    * 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.fieldprocessor.processor`` 以下に格納されているクラスを参照してください。。


.. sourcecode:: java
    
    public class CellProcessor extends AbstractFieldProcessor<SampleAnno> {
    
        // シートの読み込み時の処理
        @Override
        public void loadProcess(final Sheet sheet, final Object beansObj, final SampleAnno anno, final FieldAdaptor adaptor,
                final XlsMapperConfig config, final LoadingWorkObject work) throws XlsMapperException {
            
            //TODO: 実装する
        }
        
        // シートの書き込み時の処理
        @Override
        public void saveProcess(final Sheet sheet, final Object targetObj, final SampleAnno anno, final FieldAdaptor adaptor,
                final XlsMapperConfig config, final SavingWorkObject work) throws XlsMapperException {
            
            //TODO: 実装する
            
        }
    }


* 作成した FieldProcessorは、 ``FieldProcessorRegistry#registerProcessor(...)`` にて登録します。

    * システム標準の ``FieldProcessorRegistry`` は、``XlsMapperConfg#getFieldProcessorRegistry()`` から取得できます。


.. sourcecode:: java
    
    // 独自のFieldProcessorの登録
    XlsMapperConfig config = new XlsMapperConfig();
    config.getFieldProcessorRegistry().registerProcessor(SampleAnno.class, new SampleFieldProcessor());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfig(config);
    



--------------------------------------------------------
CellConverterの拡張
--------------------------------------------------------

Excelのセルの値をJavaの任意のクラスにマッピングするには、CellConveterを実装します。

* インタフェース ``com.gh.mygreen.xlsmapper.cellconvert.CellConverter`` を実装します。

    * 実際には、ユーティリティメソッドがそろっている、``AbstractCellConverter`` を継承して実装します。
    * 読み込み時、書き込み時のそれぞれのメソッドを実装します。
    * 実装のサンプルは、パッケージ ``com.gh.mygreen.xlsmapper.cellconvert.converter`` 以下に格納されているクラスを参照してください。

.. sourcecode:: java
    
    public class LocaleDateConverter extends AbstractCellConverter<LocaleDate> {
        
        // シート読み込み時のExcel Cell => Javaオブジェクトに変換する。
        @Override
        public LocaleDate toObject(final Cell cell, final FieldAdaptor adaptor, final XlsMapperConfig config)
            throws XlsMapperException {
            //TODO: 実装する
        }
        
        //シート書き込み時のJavaオブジェクト => Excel Cellに変換する。
        Cell toCell(FieldAdaptor adaptor, LocaleDate targetValue, Sheet sheet, int column, int row, XlsMapperConfig config) throws XlsMapperException;
            //TODO: 実装する
        }


* よく利用するConverterの場合は、 ``CellConverterRegstry`` に登録します。

    * システム標準の ``CellConverterRegstry`` は、``XlsMapperConfg#getConverterRegistry()`` から取得できます。

.. sourcecode:: java
    
    // 独自のCellConveterの登録
    XlsMapperConfig config = new XlsMapperConfig();
    config.getConverterRegistry().registerProcessor(LocaleDate.class, new LocaleDateConverter());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfig(config);
    

* 1つのセル専用のConverterの場合は、アノテーションを用いて ``@XlsConverter(converterClass=SampleConverter.class)`` のように指定します。

    * Converterのクラスのインスタンスは、 ``XlsMapperConfig#createBean(...)`` 経由で作成されます。
    * インスタンスを生成する際に、Spring経由などで作成したい場合は、下記の「BeanFactoryの拡張」を参照してください。

.. sourcecode:: java
    
    public class SampleRecord {
         
         // フィールド独自のConveterの設定
         @XlsColumn(columnName="日時")
         @XlsConverter(converterClass=LocaleDateConvereter.class)
         private LocaleDate localeDate;
    }
    


--------------------------------------------------------
POIのセルフォーマッターの拡張
--------------------------------------------------------

POIによるExcelの値を文字列として取得するには、一般的には難しいです。
POIは、数値と日時型はファイル内部では同じdouble型で保持されているため、書式で判断する必要がります。

書式はカスタマイズ可能であり、様々なものが利用できるため、セルのタイプを単純には判定できません。
そこで、XlsMapperでは、セルの値を取得するために、外部のライブラリ `excel-cellformatter <http://mygreen.github.io/excel-cellformatter/>`_ を利用しています。

セルの値を取得する処理を独自の実装に切り替えることができます。

* セルの実装を切り替えるには、インタフェース ``com.gh.mygreen.xlsmapper.CellFormatter`` を実装したものを、XlsMapperConfigに渡します。
* 標準では、``com.gh.mygreen.xlsmapper.DefaultCellFormatter`` が設定されています。

.. sourcecode:: java
    
    XlsMapperConfig config = new XlsMapperConfig();
    
    // 独自の処理系に変更する。
    config.setCellFormatter(new CustomCellFormatter());
    
    XlsMapper mapper = new XlsMapper();
    mapper.setConfig(config);

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

