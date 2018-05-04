=========================================================
SpringFrameworkとの連携
=========================================================

DI(Depenency Injection) 機能のフレームワーク `Spring Framework <https://projects.spring.io/spring-framework/>`_ と連携することができます。

Spring Framework のコンテナで管理可能、DI可能な部分は、次の箇所になります。

これらの機能・箇所は、 ``com.gh.mygreen.xlsmapper.BeanFactory`` によるインスタンスを新しく作成する箇所であり、その実装を ``com.gh.mygreen.xlsmapper.SpringBeanFactory`` に切り替え得ることで、DIを実現します。


.. list-table:: Spring Frameworkとの連携可能な箇所
   :widths: 40 60
   :header-rows: 1
   
   * - 機能・箇所
     - 説明
     
   * - シートやレコードのインスタンス
     - ``TextFormatter`` の実装クラスがSpringBeanとして管理可能です。

   * - :doc:`独自のクラスタイプの対応方法 <annotation_converter_custom>`
     - ``CellConverterFactory`` の実装クラスがSpringBeanとして管理可能です。

   * - :doc:`独自の表・セルのマッピング方法 <fieldprocessor>`
     - ``FieldProcessor`` の実装クラスがSpringBeanとして管理可能です。

   * - :ref:`独自のリスナーの実装機能 <annotationXlsListener>`
     - リスナクラスがSpringBeanとして管理可能です。
     
   * - :ref:`独自のRecordFinderの実装機能 <annotationXlsRecordFinder>`
     - ``RecordFinder`` の実装クラスがSpringBeanとして管理可能です。
     


----------------------------------------------------------------
ライブラリの追加
----------------------------------------------------------------

Spring Frameworkを利用する際には、ライブリを追加します。
Mavenを利用している場合は、pom.xmlに以下を追加します。

Spring Frameworkのバージョンは、3.0以上を指定してください。

.. sourcecode:: xml
    :linenos:
    
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.3.2.RELEASE</version>
    </dependency>


----------------------------------------------------------------
XMLによるコンテナの設定
----------------------------------------------------------------

XMLによる設定方法を説明します。

コンテナの定義の基本は次のようになります。

* アノテーションによるDIの有効化を行います。
* コンポーネントスキャン対象のパッケージの指定を行います。
* ``com.gh.mygreen.xlsmapper.SpringBeanFactory`` をSpringBeanとして登録します。

.. sourcecode:: xml
    :linenos:
    :caption: コンテナへの登録(XML形式)

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
        
        <!-- アノテーションによるDIの有効化の定義 -->
        <context:annotation-config />
        
        <!-- コンポーネントスキャン対象のパッケージの指定 -->
        <context:component-scan base-package="sample.spring" />
        
        <!-- SpringBeanFactoryの登録 -->
        <bean id="springBeanFactory" class="com.gh.mygreen.xlsmapper.SpringBeanFactory" />
        
    </beans>


----------------------------------------------------------------
JavaConfigによるコンテナの設定
----------------------------------------------------------------

Spring Framework3.0から追加された、JavaソースによるSpringBean定義の方法を説明します。

JavaConfigによる設定を使用する場合は、Spring Frameworkのバージョンをできるだけ最新のものを使用してください。
特に、機能が豊富なバージョン4.0以上の使用を推奨します。


.. sourcecode:: java
    :linenos:
    :caption: JavaConfigの設定
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Description;
    
    import com.gh.mygreen.xlsmapper.SpringBeanFactory;
    
    // Javaによるコンテナの定義
    @Configuration
    @ComponentScan(basePackages="sample.spring")
    public class XlsMapperConfig {
        
        @Bean
        @Description("Springのコンテナを経由するCSV用のBeanFactoryの定義")
        public SpringBeanFactory springBeanFactory() {
            return new SpringBeanFactory();
        }
        
    }


----------------------------------------------------------------
SpringBeanとしての定義
----------------------------------------------------------------

ステレオタイプのアノテーション ``@Component/@Service/@Reposition/@Controller`` をサポートしているため、これらを使いSpringBeanを定義します。


シートクラスや、レコードクラスをSpringコンテナに登録する場合は、スコープは *prototype* にします。
ライフサイクル・コールバック用のアノテーションを付与したメソッド内でインジェクションしたクラスなどを呼び出したりします。

.. sourcecode:: java
    :linenos:

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

作成したSpringBeanFactoryをConfigurationに設定して、シートを読み込みます。

.. sourcecode:: java
    :linenos:
    
    // 自作したSpringBeanFactory
    @Autorired
    SpringBeanFactory springBeanFacetory;
    
    public void doLoad() {
        // FacetoryBeanの実装を独自のものに変更する。
        Configuration config = new Configuration();
        config.setBeanFactory(springBeanFactory);
        
        XlsMapper mapper = new XlsMapper();
        mapper.setConig(config);
        
        SampleSheet sheet = mapper.load(...);
    }




